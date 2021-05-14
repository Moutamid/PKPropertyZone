package com.techroof.pkpropertyzone;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.loader.content.AsyncTaskLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techroof.pkpropertyzone.Adapter.ViewPagerAdapter;
import com.techroof.pkpropertyzone.Authentication.LoginActivity;
import com.techroof.pkpropertyzone.BuyProperty.BuyPropertyActivity;
import com.techroof.pkpropertyzone.Chat.ChatActivity;
import com.techroof.pkpropertyzone.Chat.Conv;
import com.techroof.pkpropertyzone.Chat.ConversationActivity;
import com.techroof.pkpropertyzone.Chat.Messages;
import com.techroof.pkpropertyzone.Fragments.ApartmentsFragment;
import com.techroof.pkpropertyzone.Fragments.ConstructionFragment;
import com.techroof.pkpropertyzone.Fragments.ShopsFragment;
import com.techroof.pkpropertyzone.Model.Property;
import com.techroof.pkpropertyzone.SellProperty.AddPropertyActivity;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PropertyActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "PropertyActivity";

    //________________________________________________________________________

    private FirebaseAuth mAuth;

    private String currentUserId, otherUserId;


    private static final String PACKAGE_NAME = "dev.moutamid.pkpropertyzone";
    private SharedPreferences sharedPreferences;

    private String getStoredString(String name) {

        sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, "Error");

    }

    private void storeString(String name, String object) {

        sharedPreferences = getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(name, object).apply();
    }

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FloatingActionButton fab;
    private TextView navUserName, noPropText;
    private CircleImageView navUserImg;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mToggle;
    private CardView mainCV;
    private String cityId, areaName;
    private ImageView navImg, chatBtn;
    //    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText searchInput;
//    private  FilterClass mclass = new FilterClass() {
//        @Override
//        public void searchItem(CharSequence text) {
//
//        }
//    };


    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            storeString("allowed", "notallowed");

            handler.postDelayed(runnable, 500);

        }
    };

//    private DatabaseReference databaseReference;
//    private ChildEventListener childEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_property);
        storeString("allowed", "notallowed");

        //------------EXTRA
        mAuth = FirebaseAuth.getInstance();
        final DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference();

        chatBtn = findViewById(R.id.chat_btn);

        if (mAuth.getCurrentUser() != null) {

            databaseReference1.child("messages").child(mAuth.getCurrentUser().getUid())
                    .child("newMcg").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists())
                        return;

                    boolean newMessage = dataSnapshot.getValue(Boolean.class);

                    if (newMessage) {
                        chatBtn.setImageResource(R.drawable.ic_baseline_chat_unread_24);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    Toast.makeText(PropertyActivity.this, databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        handler.postDelayed(runnable, 500);

        cityId = getIntent().getStringExtra("cityId");
        areaName = getIntent().getStringExtra("areaName");
        searchInput = findViewById(R.id.search_input_property);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        fab = findViewById(R.id.fab_property);

        noPropText = findViewById(R.id.no_prop_text);
        navImg = findViewById(R.id.nav_img);

        navigationView = findViewById(R.id.buyer_nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        mainCV = findViewById(R.id.cv);

//        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        navImg.setVisibility(View.VISIBLE);
        chatBtn.setVisibility(View.VISIBLE);

        setViewPager(viewPager);

        tabLayout.setupWithViewPager(viewPager);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mAuth.getCurrentUser() != null)
                    databaseReference1.child("messages").child(mAuth.getCurrentUser().getUid()).child("newMcg").setValue(false);
                chatBtn.setImageResource(R.drawable.ic_baseline_chat_24);

                Intent chat = new Intent(PropertyActivity.this, ConversationActivity.class);
                startActivity(chat);

            }
        });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG, "onTextChanged: ");
                storeString("allowed", "allowed");
                storeString("query", charSequence.toString().toLowerCase());//.trim()

            }

            @Override
            public void afterTextChanged(Editable editable) {

                storeString("allowed", "allowed");
            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    Intent intent = new Intent(PropertyActivity.this, AddPropertyActivity.class);
                    intent.putExtra("cityId", cityId);
                    intent.putExtra("areaName", areaName);
                    intent.putExtra("context", "addProperty");
                    intent.putExtra("propertyType", String.valueOf(viewPager.getCurrentItem()));
                    startActivity(intent);
                }
            }
        });

        View navHeader = navigationView.getHeaderView(0);
        navUserImg = navHeader.findViewById(R.id.nav_user_img);
        navUserName = navHeader.findViewById(R.id.nav_user_name);


        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            navUserName.setText("N/A");

        } else {

            db.collection("Users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if (documentSnapshot.exists()) {
                                String name = documentSnapshot.getString("name");
                                String image = documentSnapshot.getString("image");

                                //    Glide.with(PropertyActivity.this).load(image).into(navUserImg);
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
        }


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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeString("query", "");
        handler.removeCallbacks(runnable);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.home:
                break;


            case R.id.your_properties:

                Intent properties = new Intent(PropertyActivity.this, SellerViewPropertyActivity.class);
                startActivity(properties);

                break;

            case R.id.logout:

                mAuth.signOut();
                Intent home = new Intent(PropertyActivity.this, HomeActivity.class);
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


    private void setViewPager(ViewPager viewPager) {

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPagerAdapter.addFragment(new ShopsFragment(), "Plots");
        viewPagerAdapter.addFragment(new ApartmentsFragment(), "Apartments");
        viewPagerAdapter.addFragment(new ConstructionFragment(), "Construction");
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(viewPagerAdapter);
    }

}