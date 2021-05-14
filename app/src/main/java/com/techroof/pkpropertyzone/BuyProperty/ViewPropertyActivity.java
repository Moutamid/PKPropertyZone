package com.techroof.pkpropertyzone.BuyProperty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;
import com.techroof.pkpropertyzone.Chat.ChatActivity;
import com.techroof.pkpropertyzone.R;

public class ViewPropertyActivity extends AppCompatActivity {

    private String propertyId, propVideo, sellerPhone, sellerId;
    private Button chatBtn, viewNumberBtn;
    private ImageView propertyImg, backBtn, navImg, chatImg;
    private TextView propertyName, propertyDesc, propertyAddress, propertyPrice;
    private FirebaseFirestore db;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_property);

        propertyId = getIntent().getStringExtra("property_id");

        chatBtn = findViewById(R.id.chat_btn);
        viewNumberBtn = findViewById(R.id.view_number_btn);
        propertyImg = findViewById(R.id.property_details_img);
        propertyName = findViewById(R.id.property_name);
        propertyDesc = findViewById(R.id.property_desc);
        propertyAddress = findViewById(R.id.address_text);
        propertyPrice = findViewById(R.id.property_price);
        backBtn = findViewById(R.id.back_btn);
        navImg = findViewById(R.id.nav_img);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading...");
        pd.show();

        db = FirebaseFirestore.getInstance();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent chat = new Intent(ViewPropertyActivity.this, ChatActivity.class);
                chat.putExtra("other_user_id", sellerId);
                startActivity(chat);

            }
        });


        db.collection("Properties")
                .document(propertyId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {
                            pd.dismiss();

                            try {
                                Picasso.get().load(documentSnapshot.get("imageUrl").toString())
                                        .into(propertyImg);

                            } catch (Exception e) {

                            }

                            propertyName.setText(documentSnapshot.get("propertyName").toString());
                            propertyDesc.setText(documentSnapshot.get("propertyDescription").toString());
                            propertyAddress.setText(documentSnapshot.get("fullAddress").toString());
                            propertyPrice.setText(documentSnapshot.get("propertyPrice").toString());
                            sellerPhone = documentSnapshot.get("sellerPhone").toString();
                            sellerId = documentSnapshot.get("seller").toString();

                            try {
                                propVideo = documentSnapshot.get("videoUrl").toString();

                            } catch (Exception e) {
                            }


                            if (propVideo != null) {


//                                MediaController mediaController = new
//                                        MediaController(ViewPropertyActivity.this);

                            } else {
//                                Toast.makeText(ViewPropertyActivity.this, "No Video", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            pd.dismiss();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(ViewPropertyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                pd.dismiss();

            }
        });

        viewNumberBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(ViewPropertyActivity.this, sellerPhone, Toast.LENGTH_LONG).show();

            }
        });

    }
}