package com.techroof.pkpropertyzone;

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
import com.techroof.pkpropertyzone.Adapter.AddpropertyAdapter;
import com.techroof.pkpropertyzone.Authentication.LoginActivity;
import com.techroof.pkpropertyzone.Chat.ConversationActivity;
import com.techroof.pkpropertyzone.Model.AddPropertyModel;
import com.techroof.pkpropertyzone.Model.ConstructionPropertyModel;
import com.techroof.pkpropertyzone.SellProperty.AddPropertyActivity;
import com.techroof.pkpropertyzone.SellProperty.SellPropertyActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class SellerViewPropertyActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    ArrayList<AddPropertyModel> modelList = new ArrayList<>();
    ArrayList<ConstructionPropertyModel> constructionPropertyModelList = new ArrayList<>();
    AddpropertyAdapter adapter;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore firestore;
    private FirebaseUser currentUser;
    ProgressDialog progressDialog;
    TextView noPropertyAdded;
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller_view_property);

        backBtn=findViewById(R.id.back_btn);

        backBtn.setVisibility(View.VISIBLE);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               onBackPressed();
            }
        });

        firebaseAuth = FirebaseAuth.getInstance();

        firestore = FirebaseFirestore.getInstance();

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        if (currentUser == null) {

            Intent login = new Intent(SellerViewPropertyActivity.this, LoginActivity.class);
            login.putExtra("userType","seller");
            startActivity(login);
            finish();

        } else {

            recyclerView = findViewById(R.id.add_property_recyclerView);
            layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(layoutManager);

            progressDialog = new ProgressDialog(this);

            noPropertyAdded = findViewById(R.id.tv_no_property_added);

            showSellPropertyData();
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
                        adapter = new AddpropertyAdapter(SellerViewPropertyActivity.this, modelList);
                        recyclerView.setAdapter(adapter);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        noPropertyAdded.setVisibility(View.VISIBLE);
                        Toast.makeText(SellerViewPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}