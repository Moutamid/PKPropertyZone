package com.techroof.pkpropertyzone.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.techroof.pkpropertyzone.Adapter.MessagesAdapter;
import com.techroof.pkpropertyzone.Authentication.LoginActivity;
import com.techroof.pkpropertyzone.R;
import com.techroof.pkpropertyzone.SellProperty.AddPropertyActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private ImageView userImg, sendBtn, backBtn;
    private EditText msgEt;
    private TextView userName;
    private RecyclerView msgsRv;
    private String message, currentUserId, otherUserName;
    private String otherUserId;
    private DatabaseReference mref;
    private FirebaseAuth mAuth;
    private MessagesAdapter mAdapter;
    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayout;
    private static final int GALLERY_PICK_CODE = 1;
    private Uri imageUri;
    private StorageReference stRef;
    private ProgressDialog pd;
    private String currentDate, currentTime, imageMsgKey, msgImgUrl;
    private FirebaseDatabase database;
    private FirebaseFirestore db;

    private boolean isOnline = false;

    private void checkOnlineStatus() {

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                isOnline = snapshot.getValue(Boolean.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ChatActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        checkOnlineStatus();

        backBtn = findViewById(R.id.back_btn);
        userImg = findViewById(R.id.chat_bar_user_image);
        userName = findViewById(R.id.chat_bar_user_name);
        //lastSeen=findViewById(R.id.chat_bar_last_seen);
        msgEt = findViewById(R.id.chat_message_et);
        //  addBtn=findViewById(R.id.chat_add_btn);
        sendBtn = findViewById(R.id.chat_send_btn);
        msgsRv = findViewById(R.id.messages_list_rv);

        db = FirebaseFirestore.getInstance();

        database = FirebaseDatabase.getInstance("https://pkpropertyzone-3ea93-default-rtdb.firebaseio.com/");

        mref = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser userId = mAuth.getCurrentUser();
        otherUserId = getIntent().getStringExtra("other_user_id");

        if (userId == null) {
            Intent intent = new Intent(ChatActivity.this, LoginActivity.class);
            intent.putExtra("context", "chat");
            intent.putExtra("other_user_id", otherUserId);

            startActivity(intent);

        } else {

            currentUserId = mAuth.getCurrentUser().getUid();

            pd = new ProgressDialog(this);
            pd.setCanceledOnTouchOutside(false);
            pd.setMessage("Please wait...");

            stRef = FirebaseStorage.getInstance().getReference();

            backBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });

            otherUserId = getIntent().getStringExtra("other_user_id");

            mAdapter = new MessagesAdapter(messagesList, ChatActivity.this, otherUserId);
            msgsRv = findViewById(R.id.messages_list_rv);

            mLinearLayout = new LinearLayoutManager(this);
            msgsRv.setHasFixedSize(true);
            msgsRv.setLayoutManager(mLinearLayout);

            msgsRv.setAdapter(mAdapter);

            loadMessages();

            db.collection("Users")
                    .document(otherUserId)
                    .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    otherUserName = documentSnapshot.get("name").toString();

                    //   Glide.with(getApplicationContext()).load(image).into(userImg);
                    userName.setText(otherUserName);

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            mref.child("chats").child(currentUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (!dataSnapshot.hasChild(otherUserId)) {

                        Map chatAddMap = new HashMap();
                        chatAddMap.put("seen", false);
                        chatAddMap.put("timestamp", ServerValue.TIMESTAMP);
                        Map chatUserMap = new HashMap();
                        chatUserMap.put("chats/" + currentUserId + "/" + otherUserId, chatAddMap);
                        chatUserMap.put("chats/" + otherUserId + "/" + currentUserId, chatAddMap);
                        mref.updateChildren(chatUserMap, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {

                                    Log.d("CHAT_LOG", databaseError.getMessage().toString());

                                }
                            }
                        });
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            sendBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isOnline)
                        sendMessage();
                    else {
                        Toast.makeText(ChatActivity.this, "You are offline!", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }
    }


    private void loadMessages() {
        mref.child("messages").child(currentUserId).child(otherUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Messages message = dataSnapshot.getValue(Messages.class);
                messagesList.add(message);
                msgsRv.scrollToPosition(messagesList.size() - 1);
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    private void sendMessage() {

        String message = msgEt.getText().toString();

        if (msgEt.getText().toString().equals("image")) {

            if (!TextUtils.isEmpty(message)) {
                String current_user_ref = "messages/" + currentUserId + "/" + otherUserId;
                String chat_user_ref = "messages/" + otherUserId + "/" + currentUserId;
                DatabaseReference user_message_push = mref.child("messages")
                        .child(currentUserId).child(otherUserId).push();

                mref.child("messages").child(otherUserId).child("newMcg").setValue(true);

                String push_id = user_message_push.getKey();
                Map messageMap = new HashMap();
                messageMap.put("message", msgImgUrl);
                messageMap.put("seen", false);
                messageMap.put("type", "image");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", currentUserId);
                messageMap.put("messageId", push_id);

                Map messageUserMap = new HashMap();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                msgEt.setText("");
                mref.updateChildren(messageUserMap);
            }

        } else {

            if (!TextUtils.isEmpty(message)) {
                String current_user_ref = "messages/" + currentUserId + "/" + otherUserId;
                String chat_user_ref = "messages/" + otherUserId + "/" + currentUserId;
                DatabaseReference user_message_push = mref.child("messages")
                        .child(currentUserId).child(otherUserId).push();

                mref.child("messages").child(otherUserId).child("newMcg").setValue(true);

                String push_id = user_message_push.getKey();
                Map messageMap = new HashMap();
                messageMap.put("message", message);
                messageMap.put("seen", false);
                messageMap.put("type", "text");
                messageMap.put("time", ServerValue.TIMESTAMP);
                messageMap.put("from", currentUserId);
                messageMap.put("messageId", push_id);
                Map messageUserMap = new HashMap();
                messageUserMap.put(current_user_ref + "/" + push_id, messageMap);
                messageUserMap.put(chat_user_ref + "/" + push_id, messageMap);
                msgEt.setText("");
                mref.updateChildren(messageUserMap);
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_PICK_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            //addictImg.setImageURI(imageUri);
            msgEt.setText("image");
            uploadImg();
        }
    }

    public void uploadImg() {

        pd.show();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat date = new SimpleDateFormat("MM dd,yyyy");
        currentDate = date.format(calendar.getTime());

        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        currentTime = time.format(calendar.getTime());

        imageMsgKey = currentDate + "_" + currentTime;

        final StorageReference filePath = stRef.child("Message Images")
                .child(imageUri.getLastPathSegment() + "_" + imageMsgKey);

        final UploadTask uploadTask = filePath.putFile(imageUri);

        uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                if (task.isSuccessful()) {

                    pd.dismiss();
                    Toast.makeText(ChatActivity.this, "Uploaded Successfully", Toast.LENGTH_LONG).show();
                    Task<Uri> uriTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (task.isSuccessful()) {

                                pd.dismiss();
                                msgImgUrl = filePath.getDownloadUrl().toString();
                                msgEt.setEnabled(false);
                                return filePath.getDownloadUrl();


                            } else {
                                pd.dismiss();
                                throw task.getException();
                            }
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            pd.dismiss();
                            msgImgUrl = task.getResult().toString();
                            msgEt.setEnabled(false);
                        }
                    });
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                double progress = (100.0 * taskSnapshot.getBytesTransferred())
                        / taskSnapshot.getTotalByteCount();
                pd.setMessage((int) progress + "% Uploaded");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(ChatActivity.this, e.toString(), Toast.LENGTH_LONG).show();
            }
        });


    }


}