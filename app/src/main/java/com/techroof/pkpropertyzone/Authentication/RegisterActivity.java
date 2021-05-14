package com.techroof.pkpropertyzone.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techroof.pkpropertyzone.BuyProperty.BuyPropertyActivity;
import com.techroof.pkpropertyzone.R;
import com.techroof.pkpropertyzone.SellProperty.SellPropertyActivity;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    EditText Name, Email, Password, Phone;
    Button RegisterBtn;
    TextView LoginTv;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;

    FirebaseFirestore firestore;
    String UserID,cityId,areaName,context,otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        cityId = getIntent().getStringExtra("cityId");
        areaName = getIntent().getStringExtra("areaName");
        context = getIntent().getStringExtra("context");
        otherUserId = getIntent().getStringExtra("other_user_id");

        firestore = FirebaseFirestore.getInstance();

      //  getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Name = findViewById(R.id.name);
        Email = findViewById(R.id.email);
        Password = findViewById(R.id.password);
        Phone = findViewById(R.id.phone);
        RegisterBtn = findViewById(R.id.registerBtn);
        LoginTv = findViewById(R.id.tv_already_register);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        //userType=getIntent().getStringExtra("userType");

            RegisterBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String email = Email.getText().toString();
                    final String password = Password.getText().toString();
                    final String name = Name.getText().toString();
                    final String phone = Phone.getText().toString();

                    if (TextUtils.isEmpty(email)) {
                        Email.setError("Please Enter Email");
                        return;
                    }
                    if (TextUtils.isEmpty(password)) {
                        Password.setError("Please Enter Password");
                        return;
                    }
                    if (password.length() < 6) {
                        Password.setError("Password must be equal to and greater than 6 characters");
                        return;
                    }
                    progressBar.setVisibility(View.VISIBLE);

                    // Register the user in firestore
                    firebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        UserID = firebaseAuth.getCurrentUser().getUid();
                                        final Map<String, Object> seller = new HashMap<>();
                                        seller.put("userId",UserID);
                                        seller.put("name", name);
                                        seller.put("email", email);
                                        seller.put("password", password);
                                        seller.put("phone", phone);
                                       // seller.put("userType", userType);

                                        firestore.collection("Users").document(UserID).set(seller)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(RegisterActivity.this, "Account has been created", Toast.LENGTH_SHORT).show();

                                                    /*    if (userType.equals("seller")){

                                                            Intent sellerHome=new Intent(RegisterActivity.this, SellPropertyActivity.class);
                                                            startActivity(sellerHome);
                                                            finish();


                                                        }else{

                                                            Intent buyerHome=new Intent(RegisterActivity.this, BuyPropertyActivity.class);
                                                            startActivity(buyerHome);
                                                            finish();

                                                        }

                                                     */

                                                        Intent intent = new Intent(RegisterActivity.this,LoginActivity.class);
                                                        intent.putExtra("cityId",cityId);
                                                        intent.putExtra("areaName",areaName);
                                                        intent.putExtra("context",context);
                                                        intent.putExtra("other_user_id",otherUserId);

                                                        startActivity(intent);

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                progressBar.setVisibility(View.GONE);
                                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                }
            });

            LoginTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent reg=new Intent(RegisterActivity.this, LoginActivity.class);
                   reg.putExtra("areaName",areaName);
                    reg.putExtra("cityId",cityId);
                    reg.putExtra("context",context);
                    reg.putExtra("other_user_id",otherUserId);
                    startActivity(reg);

                }
            });

    }
}