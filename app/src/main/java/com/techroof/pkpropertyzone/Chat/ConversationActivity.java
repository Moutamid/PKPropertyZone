package com.techroof.pkpropertyzone.Chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.techroof.pkpropertyzone.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ConversationActivity extends AppCompatActivity {
    private static final String TAG = "ConversationActivity";

    private FirebaseAuth mAuth;
    private DatabaseReference chatsRef, msgRef, mref;
    private FirebaseFirestore usersRef;
    private ProgressDialog pd;
    private RecyclerView convRv;
    private ImageView backBtn;
    private TextView noChatsText;
    private LinearLayoutManager layoutManager;
    private String currentUserId, otherUserId;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);

        mAuth = FirebaseAuth.getInstance();
        currentUserId = mAuth.getCurrentUser().getUid();

        database = FirebaseDatabase.getInstance("https://pkpropertyzone-3ea93-default-rtdb.firebaseio.com/");

        chatsRef = database.getReference().child("chats").child(currentUserId);

        msgRef = database.getReference().child("messages").child(currentUserId);
        usersRef = FirebaseFirestore.getInstance();
        mref = database.getReference();

        backBtn = findViewById(R.id.back_btn);
        convRv = findViewById(R.id.conv_list_rv);
        noChatsText = findViewById(R.id.no_chats_text);

        pd = new ProgressDialog(this);
        pd.setMessage("Loading");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);

//        convRv.setHasFixedSize(true);
        convRv.setLayoutManager(layoutManager);

    }

    @Override
    protected void onStart() {
        super.onStart();

        mref.child("chats").child(mAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.exists()) {

                            pd.dismiss();
                            noChatsText.setVisibility(View.GONE);

                            Query conversationQuery = chatsRef.orderByChild("timestamp");

                            final FirebaseRecyclerAdapter<Conv, ConvViewHolder> firebaseConvAdapter =
                                    new FirebaseRecyclerAdapter<Conv, ConvViewHolder>(
                                            Conv.class,
                                            R.layout.chats_conv_layout,
                                            ConvViewHolder.class,
                                            conversationQuery
                                    ) {
                                        @Override
                                        protected void populateViewHolder(final ConvViewHolder convViewHolder,
                                                                          final Conv conv, final int position) {

                                            pd.dismiss();

                                            final String list_user_id = getRef(position).getKey();

                                            Query lastMessageQuery = msgRef.child(list_user_id).limitToLast(1);

                                            lastMessageQuery.addChildEventListener(new ChildEventListener() {
                                                @Override
                                                public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                                                    String data = dataSnapshot.child("message").getValue().toString();
                                                    convViewHolder.setMessage(position, data, conv.isSeen());
                                                    pd.dismiss();
                                                    noChatsText.setVisibility(View.GONE);

                                                }

                                                @Override
                                                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                                                    pd.dismiss();
                                                    noChatsText.setVisibility(View.GONE);

                                                }

                                                @Override
                                                public void onChildRemoved(DataSnapshot dataSnapshot) {
                                                    notifyDataSetChanged();
                                                    pd.dismiss();
                                                    noChatsText.setVisibility(View.GONE);

                                                }

                                                @Override
                                                public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                                                    pd.dismiss();
                                                    noChatsText.setVisibility(View.GONE);

                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {
                                                    pd.dismiss();
                                                    noChatsText.setVisibility(View.GONE);

                                                }
                                            });


                                            usersRef.collection("Users")
                                                    .document(list_user_id)
                                                    .get()
                                                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                                        @Override
                                                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                                                            final String userName = documentSnapshot.get("name").toString();
                                                            //String userThumb = dataSnapshot.child("image").getValue().toString();

                                                            pd.dismiss();
                                                            noChatsText.setVisibility(View.GONE);

                                                            convViewHolder.setName(userName);
                                                            //convViewHolder.setUserImage(userThumb, getApplicationContext());

                                                            convViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                                                @Override
                                                                public void onClick(View view) {


                                                                    Intent chatIntent = new Intent(getApplicationContext(), ChatActivity.class);
                                                                    chatIntent.putExtra("other_user_id", list_user_id);
                                                                    chatIntent.putExtra("user_name", userName);
                                                                    startActivity(chatIntent);

                                                                }
                                                            });
                                                            convViewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                                                                @Override
                                                                public boolean onLongClick(View view) {

                                                                    CharSequence options[] = new CharSequence[]{
                                                                            "YES", "NO"
                                                                    };
                                                                    final AlertDialog.Builder builder = new AlertDialog.Builder(ConversationActivity.this);
                                                                    builder.setTitle("Delete Conversation?");

                                                                    builder.setItems(options, new DialogInterface.OnClickListener() {
                                                                        @Override
                                                                        public void onClick(DialogInterface dialog, int which) {

                                                                            if (which == 0) {

                                                                                mref.child("chats").child(mAuth.getCurrentUser().getUid())
                                                                                        .child(list_user_id).removeValue()
                                                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {

                                                                                                    mref.child("messages").child(mAuth.getCurrentUser().getUid())
                                                                                                            .child(list_user_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {


                                                                                                                notifyDataSetChanged();
                                                                                                                convViewHolder.mView.setVisibility(View.GONE);
                                                                                                                Toast.makeText(ConversationActivity.this, "Conversation removed", Toast.LENGTH_SHORT).show();

                                                                                                            } else {
                                                                                                                Toast.makeText(ConversationActivity.this, "Error", Toast.LENGTH_SHORT).show();

                                                                                                            }
                                                                                                        }
                                                                                                    });

                                                                                                } else {
                                                                                                    Toast.makeText(ConversationActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });

                                                                            } else {

                                                                            }

                                                                        }
                                                                    });
                                                                    builder.show();
                                                                    return false;


                                                                }
                                                            });


                                                        }
                                                    });

                                        }
                                    };

                            convRv.setAdapter(firebaseConvAdapter);


                        } else {
                            pd.dismiss();
                            noChatsText.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        pd.dismiss();
                        noChatsText.setVisibility(View.VISIBLE);
                    }
                });

    }

    public static class ConvViewHolder extends RecyclerView.ViewHolder {
        //        Context context;
        View mView;

        public ConvViewHolder(View itemView) {//, Context context) {
            super(itemView);

            this.mView = itemView;
//            this.context = context;

        }

        public void setMessage(int i, String message, boolean isSeen) {

            TextView userStatusView = (TextView) mView.findViewById(R.id.user_single_status);
            userStatusView.setText(message);

            if (!isSeen) {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.BOLD);
            } else {
                userStatusView.setTypeface(userStatusView.getTypeface(), Typeface.NORMAL);
            }

        }

        public void setName(String name) {

            TextView userNameView = (TextView) mView.findViewById(R.id.user_single_name);
            userNameView.setText(name);

        }

        public void setUserImage(String thumb_image, Context ctx) {

            CircleImageView userImageView = (CircleImageView) mView.findViewById(R.id.user_single_image);
            Glide.with(ctx).load(thumb_image).placeholder(R.drawable.user_icon).into(userImageView);

        }

        public void setUserOnline(String online_status) {

            ImageView userOnlineView = (ImageView) mView.findViewById(R.id.user_single_online_icon);

            if (online_status.equals("true")) {

                userOnlineView.setVisibility(View.VISIBLE);

            } else {

                userOnlineView.setVisibility(View.INVISIBLE);

            }

        }

    }


}