package com.techroof.pkpropertyzone.SellProperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.pkpropertyzone.Authentication.LoginActivity;
import com.techroof.pkpropertyzone.BuyProperty.BuyPropertyActivity;
import com.techroof.pkpropertyzone.Chat.ConversationActivity;
import com.techroof.pkpropertyzone.HomeActivity;
import com.techroof.pkpropertyzone.R;
import com.techroof.pkpropertyzone.Adapter.AddpropertyAdapter;
import com.techroof.pkpropertyzone.Model.AddPropertyModel;
import com.techroof.pkpropertyzone.Model.ConstructionPropertyModel;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellPropertyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener  {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<AddPropertyModel> modelList = new ArrayList<>();
    ArrayList<ConstructionPropertyModel> constructionPropertyModelList = new ArrayList<>();
    AddpropertyAdapter adapter;
    FloatingActionButton floatingActionButton;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    ProgressDialog progressDialog;
    ImageView chatBtn,navBtn;
    TextView noPropertyAdded;
    String UserId;
    private TextView navUserName;
    private CircleImageView navUserImg;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private CardView mainCV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell_property);

        chatBtn=findViewById(R.id.chat_btn);
        navBtn=findViewById(R.id.nav_img);

        navigationView=findViewById(R.id.seller_nav_view);
        drawerLayout=findViewById(R.id.drawer_layout);
        mainCV=findViewById(R.id.cv);

        navBtn.setVisibility(View.VISIBLE);
        chatBtn.setVisibility(View.VISIBLE);

        floatingActionButton = findViewById(R.id.fab);

        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {

            Intent login = new Intent(SellPropertyActivity.this, LoginActivity.class);
            login.putExtra("userType","seller");
            startActivity(login);
            finish();

        } else {

            recyclerView = findViewById(R.id.add_property_recyclerView);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            progressDialog = new ProgressDialog(this);

            noPropertyAdded = findViewById(R.id.tv_no_property_added);

            UserId = firebaseAuth.getCurrentUser().getUid();
            
            showSellPropertyData();

            floatingActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(SellPropertyActivity.this, AddPropertyActivity.class);
                    intent.putExtra("userId", UserId);
                    startActivity(intent);
                }
            });


            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent chat=new Intent(SellPropertyActivity.this, ConversationActivity.class);
                    startActivity(chat);

                }
            });

            navBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });


            View navHeader = navigationView.getHeaderView(0);
            navUserImg = navHeader.findViewById(R.id.nav_user_img);
            navUserName = navHeader.findViewById(R.id.nav_user_name);

            mToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close) {
                private float scaleFactor = 6f;

                @Override
                public void onDrawerSlide(View drawerView, float slideOffset) {
                    super.onDrawerSlide(drawerView, slideOffset);

                    float slideX = drawerView.getWidth() * slideOffset;
                    mainCV.setTranslationX(slideX);
                    mainCV.setScaleX(1 - (slideOffset / scaleFactor));
                    mainCV.setScaleY(1 - (slideOffset / scaleFactor));
                }
            };
            drawerLayout.setDrawerElevation(0f);
            drawerLayout.setScrimColor(Color.TRANSPARENT);
            drawerLayout.addDrawerListener(mToggle);

            setNavigationViewListener();

        }
    }

    private void showSellPropertyData() {
      //  progressDialog.setTitle("Loading Data");
        progressDialog.setMessage("Retrieving....");
        progressDialog.setCancelable(false);
        progressDialog.show();

        firestore.collection("Properties")
                .whereEqualTo("seller",firebaseAuth.getCurrentUser().getUid())
                .get()

                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        modelList.clear();
                        progressDialog.dismiss();
                        //Show data
                        for (DocumentSnapshot doc : task.getResult()) {

                            if (!task.getResult().isEmpty()) {

                                noPropertyAdded.setVisibility(View.GONE);
                                AddPropertyModel addPropertyModel = doc.toObject(AddPropertyModel.class);
                                modelList.add(addPropertyModel);
                            }
                        }
                        adapter = new AddpropertyAdapter(SellPropertyActivity.this, modelList);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        noPropertyAdded.setVisibility(View.VISIBLE);
                        Toast.makeText(SellPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.home:
                break;

            case R.id.logout:

                firebaseAuth.signOut();
                Intent home = new Intent(SellPropertyActivity.this, HomeActivity.class);
                startActivity(home);
                break;

        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    private void setNavigationViewListener() {
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

