package com.techroof.pkpropertyzone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.techroof.pkpropertyzone.BuyProperty.BuyPropertyActivity;
import com.techroof.pkpropertyzone.SellProperty.SellPropertyActivity;

public class HomeActivity extends AppCompatActivity {

    Button sellproperty;
    private ConstraintLayout buyProperty,getStartedBtn;
    FirebaseAuth firebaseAuth;
    String UserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth = FirebaseAuth.getInstance();

        buyProperty = findViewById(R.id.buy_property);
        sellproperty = findViewById(R.id.sell_property);
        getStartedBtn=findViewById(R.id.get_started_btn);

        getStartedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buyintent = new Intent(HomeActivity.this, CitiesActivity.class);
                buyintent.putExtra("userType","buyer");
                startActivity(buyintent);
            }
        });

        buyProperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent buyintent = new Intent(HomeActivity.this, BuyPropertyActivity.class);
                buyintent.putExtra("userType","buyer");
                startActivity(buyintent);
            }
        });

        sellproperty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sellintent = new Intent(HomeActivity.this, SellPropertyActivity.class);
                sellintent.putExtra("userType","seller");
                startActivity(sellintent);
            }
        });
    }
}