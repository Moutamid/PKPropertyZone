package com.techroof.pkpropertyzone.Authentication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fxn.stash.Stash;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techroof.pkpropertyzone.Chat.ChatActivity;
import com.techroof.pkpropertyzone.Chat.Messages;
import com.techroof.pkpropertyzone.PropertyActivity;
import com.techroof.pkpropertyzone.R;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    EditText Email, Password;
    Button LoginBtn;
    TextView SignUpTv;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    private String cityId, areaName, context, otherUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        cityId = getIntent().getStringExtra("cityId");
        areaName = getIntent().getStringExtra("areaName");
        context = getIntent().getStringExtra("context");
        otherUserId = getIntent().getStringExtra("other_user_id");
        //   getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Email = findViewById(R.id.login_email);
        Password = findViewById(R.id.login_password);
        SignUpTv = findViewById(R.id.tv_not_have_account);
        LoginBtn = findViewById(R.id.loginBtn);
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);

        // userType = getIntent().getStringExtra("userType");

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = Email.getText().toString();
                String password = Password.getText().toString();

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

                //User Authentication

                firebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    done();

                                /*    if (userType.equals("seller")){

                                        Intent sellerHome=new Intent(LoginActivity.this, SellPropertyActivity.class);
                                        startActivity(sellerHome);
                                        finish();

                                    }else{

                                        Intent buyerHome=new Intent(LoginActivity.this, BuyPropertyActivity.class);
                                        startActivity(buyerHome);
                                        finish();

                                    }

                                 */


                                } else {
                                    Toast.makeText(LoginActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.GONE);
                                }
                            }
                        });
            }
        });

        SignUpTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                intent.putExtra("cityId", cityId);
                intent.putExtra("areaName", areaName);
                intent.putExtra("context", context);
                intent.putExtra("other_user_id", otherUserId);
                startActivity(intent);
            }
        });
    }

    private void done() {

        progressBar.setVisibility(View.GONE);
        Toast.makeText(LoginActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();

        if (context.equals("addProperty")) {

            Intent intent = new Intent(LoginActivity.this, PropertyActivity.class);
            intent.putExtra("cityId", cityId);
            intent.putExtra("areaName", areaName);
            startActivity(intent);

        } else if (context.equals("chat")) {

            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
            intent.putExtra("other_user_id", otherUserId);
            startActivity(intent);

        }
    }
}