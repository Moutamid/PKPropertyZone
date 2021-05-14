package com.techroof.pkpropertyzone.BuyProperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.pkpropertyzone.Adapter.BuyPropertyAdapter;
import com.techroof.pkpropertyzone.Authentication.LoginActivity;
import com.techroof.pkpropertyzone.Chat.ChatActivity;
import com.techroof.pkpropertyzone.Chat.ConversationActivity;
import com.techroof.pkpropertyzone.HomeActivity;
import com.techroof.pkpropertyzone.Model.Property;
import com.techroof.pkpropertyzone.R;

import java.util.ArrayList;
import java.util.Properties;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyPropertyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private BuyPropertyAdapter buyerPropertyAdapter;
    private RecyclerView propRv;
    private ArrayList<Property> propertiesArrayList;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressDialog pd;
    private TextView navUserName,noPropText;
    private CircleImageView navUserImg;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private CardView mainCV;
    private EditText searchET;
    private String[] cityandsectors;
    private String sector,userType;
    private ImageView navImg,chatBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_property);

        mAuth=FirebaseAuth.getInstance();
        db=FirebaseFirestore.getInstance();
        currentUser=mAuth.getCurrentUser();

        propRv=findViewById(R.id.buyer_prop_rv);
        noPropText=findViewById(R.id.no_prop_text);
        propertiesArrayList=new ArrayList<>();
        navImg=findViewById(R.id.nav_img);
        chatBtn=findViewById(R.id.chat_btn);
        navigationView=findViewById(R.id.buyer_nav_view);
        drawerLayout=findViewById(R.id.drawer_layout);
        mainCV=findViewById(R.id.cv);

        userType=getIntent().getStringExtra("userType");

        navImg.setVisibility(View.VISIBLE);
        chatBtn.setVisibility(View.VISIBLE);

        layoutManager= new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        propRv.setHasFixedSize(true);
        propRv.setLayoutManager(layoutManager);

        searchET = findViewById(R.id.property_sector_et);
      //  cityandsectors = getResources().getStringArray(R.array.citiesandsectors);

        //final ArrayAdapter sectoradapter = new ArrayAdapter(this, R.layout.spinner_item, cityandsectors);
        //sectoradapter.setDropDownViewResource(R.layout.spinner_item);
        //citiesandsectorsSpinner.setAdapter(sectoradapter);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent chat=new Intent(BuyPropertyActivity.this, ConversationActivity.class);
                startActivity(chat);

            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                buyerPropertyAdapter.getFilter().filter(charSequence);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        pd = new ProgressDialog(this);
        pd.setMessage("Loading...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        if (currentUser==null){

            Intent login=new Intent(BuyPropertyActivity.this, LoginActivity.class);
            login.putExtra("userType","buyer");

            startActivity(login);
            pd.dismiss();
            finish();

        }else{

            db.collection("Properties")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {

                            if (task.isSuccessful()){

                                for (QueryDocumentSnapshot queryDocumentSnapshot:task.getResult()){

                                    if (!task.getResult().isEmpty()){

                                        pd.dismiss();
                                        noPropText.setVisibility(View.GONE);

                                        Property property=queryDocumentSnapshot.toObject(Property.class);
                                        propertiesArrayList.add(property);

                                    }else{
                                        pd.dismiss();
                                    }
                                }
                                buyerPropertyAdapter=new BuyPropertyAdapter(
                                        BuyPropertyActivity.this,propertiesArrayList);
                                propRv.setAdapter(buyerPropertyAdapter);

                            }else{
                                pd.dismiss();
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    pd.dismiss();
                    Toast.makeText(BuyPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                String email = documentSnapshot.getString("email");
                                String phone = documentSnapshot.getString("phone");
                                //String address = documentSnapshot.getString("address");
                               // String image = documentSnapshot.getString("image");

                             //   Glide.with(BuyerHomeActivity.this).load(image).into(navUserImg);
                                navUserName.setText(name);

                            } else {
                                Toast.makeText(getApplicationContext(), "error", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

            View navHeader = navigationView.getHeaderView(0);
            navUserImg = navHeader.findViewById(R.id.nav_user_img);
            navUserName = navHeader.findViewById(R.id.nav_user_name);

            navImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    drawerLayout.openDrawer(GravityCompat.START);
                }
            });


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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.home:
                break;

            case R.id.logout:

                mAuth.signOut();
                Intent home = new Intent(BuyPropertyActivity.this, HomeActivity.class);
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