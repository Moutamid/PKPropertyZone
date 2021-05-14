package com.techroof.pkpropertyzone.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.techroof.pkpropertyzone.Chat.Messages;
import com.techroof.pkpropertyzone.R;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder>{

    private List<Messages> mMessageList;
    private DatabaseReference mUserDatabase,mref;
    private FirebaseAuth mAuth;
    private String otherUserId,currentUserImg,otherUserImg;
    Context context;
    private FirebaseDatabase database;


    public MessagesAdapter(List<Messages> mMessageList, Context context, String otherUserId) {
        this.mMessageList = mMessageList;
        this.context=context;
        this.otherUserId=otherUserId;

        database= FirebaseDatabase.getInstance("https://pkpropertyzone-3ea93-default-rtdb.firebaseio.com/");



//        ChatActivity chatActivity=new ChatActivity();
    //    otherUserId=chatActivity.getIntent().getStringExtra("other_user_id");
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        mAuth= FirebaseAuth.getInstance();
        mref=database.getReference();

        mref.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //otherUserImg=dataSnapshot.child(otherUserId).child("image").getValue().toString();
//                currentUserImg=dataSnapshot.child(mAuth.getCurrentUser().getUid()).child("image").getValue().toString();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        String userId=null;
        // if (userId.equals(mAuth.getCurrentUser().getUid())) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.messages_layout, parent, false);
        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView sender,receiver,sendMsgTime,receiveMsgTime;
        public CircleImageView senderImg,receiverImg;
        public ConstraintLayout senderCl,receiverCl;
        public ImageView senderImgMsg,receiverImgMsg;
        public CardView senderImgCv,receiveImgCv;

        public MessageViewHolder(View view) {
            super(view);

            sender= (TextView) view.findViewById(R.id.sender_txt);
            receiver=(TextView) view.findViewById(R.id.receiver_txt);
            senderCl=view.findViewById(R.id.sender_txt_cl);
            receiverCl=view.findViewById(R.id.receiver_txt_cl);
            senderImg=(CircleImageView)view.findViewById(R.id.chat_sender_user_image);
            receiverImg=(CircleImageView)view.findViewById(R.id.chat_receiver_user_image);

            sendMsgTime=view.findViewById(R.id.sender_time_text);
            receiveMsgTime=view.findViewById(R.id.receiver_time_text);

            senderImgCv=view.findViewById(R.id.sender_img_msg_cv);
            receiveImgCv=view.findViewById(R.id.receiver_img_msg_cv);
            senderImgMsg=view.findViewById(R.id.sender_img_msg);
            receiverImgMsg=view.findViewById(R.id.receiver_img_msg);

        }
    }
    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        String senderId=mAuth.getCurrentUser().getUid();
        final Messages messages=mMessageList.get(i);

        String fromUserId=messages.getFrom();
        String fromMessageType=messages.getType();

        mUserDatabase= database.getReference().child("users").child(fromUserId);

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    // Toast.makeText(context, "img", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        if (fromMessageType.equals("text")){

            viewHolder.receiver.setVisibility(View.INVISIBLE);
            viewHolder.receiverImg.setVisibility(View.INVISIBLE);

            viewHolder.receiverCl.setVisibility(View.INVISIBLE);
            viewHolder.senderImgCv.setVisibility(View.GONE);
            viewHolder.receiveImgCv.setVisibility(View.GONE);


            if (fromUserId.equals(senderId)){

                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");

                // Create a calendar object that will convert the date and time value in milliseconds to date.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(messages.getTime());

                String msgTime=formatter.format(calendar.getTime());

                viewHolder.sender.setGravity(Gravity.LEFT);
                viewHolder.sender.setText(messages.getMessage());
                viewHolder.sendMsgTime.setText(msgTime);
                //Glide.with(context).load(currentUserImg).into(viewHolder.senderImg);

                viewHolder.sender.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        CharSequence options[] = new CharSequence[]{
                                "YES", "NO"
                        };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0){

                                   mref.child("messages").child(mAuth.getCurrentUser().getUid())
                                           .child(otherUserId)
                                           .child(messages.getMessageId()).child("message")
                                           .setValue("Message deleted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                       @Override
                                       public void onComplete(@NonNull Task<Void> task) {

                                           if (task.isSuccessful()){

                                               mref.child("messages").child(otherUserId)
                                                       .child(mAuth.getCurrentUser().getUid())
                                                       .child(messages.getMessageId()).child("message")
                                                       .setValue("Message deleted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                   @Override
                                                   public void onComplete(@NonNull Task<Void> task) {

                                                       if (task.isSuccessful()){
                                                           Toast.makeText(context, "Message Deleted", Toast.LENGTH_SHORT).show();
                                                       }else{
                                                           Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                                       }
                                                   }
                                               });

                                           }else{
                                               Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                           }
                                       }
                                   }).addOnFailureListener(new OnFailureListener() {
                                       @Override
                                       public void onFailure(@NonNull Exception e) {
                                           Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                       }
                                   });

                                }else{

                                }

                            }
                        });
                        builder.show();
                        return false;


                    }
                });

            }
            else{

                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");

                // Create a calendar object that will convert the date and time value in milliseconds to date.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(messages.getTime());

                String msgTime=formatter.format(calendar.getTime());

                viewHolder.sender.setVisibility(View.INVISIBLE);
                viewHolder.senderCl.setVisibility(View.INVISIBLE);

                viewHolder.senderImg.setVisibility(View.INVISIBLE);

                viewHolder.receiverImg.setVisibility(View.VISIBLE);
                viewHolder.receiver.setVisibility(View.VISIBLE);
                viewHolder.receiverCl.setVisibility(View.VISIBLE);

                //viewHolder.receiver.setTextColor(Color.WHITE);
                viewHolder.receiver.setGravity(Gravity.LEFT);
                viewHolder.receiver.setText(messages.getMessage());
                viewHolder.receiveMsgTime.setText(msgTime);

//                Glide.with(context).load(otherUserImg).into(viewHolder.receiverImg);


            }
        }
        else if (fromMessageType.equals("image")){

            viewHolder.receiver.setVisibility(View.INVISIBLE);
            viewHolder.receiverImg.setVisibility(View.INVISIBLE);

            viewHolder.receiverCl.setVisibility(View.INVISIBLE);
            viewHolder.receiveImgCv.setVisibility(View.INVISIBLE);


            if (fromUserId.equals(senderId)){


                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");

                // Create a calendar object that will convert the date and time value in milliseconds to date.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(messages.getTime());

                String msgTime=formatter.format(calendar.getTime());

                viewHolder.sender.setGravity(Gravity.LEFT);
                viewHolder.sender.setText(messages.getMessage());
                viewHolder.sendMsgTime.setText(msgTime);

                viewHolder.senderCl.setVisibility(View.GONE);
                viewHolder.senderImgCv.setVisibility(View.VISIBLE);
                viewHolder.senderImgMsg.setVisibility(View.VISIBLE);

               // Glide.with(context).load(currentUserImg).into(viewHolder.senderImg);
                Glide.with(context).load(messages.getMessage()).into(viewHolder.senderImgMsg);


                viewHolder.sender.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        CharSequence options[] = new CharSequence[]{
                                "YES", "NO"
                        };
                        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("Delete Message?");

                        builder.setItems(options, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                if (which==0){

                                    mref.child("messages").child(mAuth.getCurrentUser().getUid())
                                            .child(otherUserId)
                                            .child(messages.getMessageId()).child("message")
                                            .setValue("Message deleted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                mref.child("messages").child(otherUserId)
                                                        .child(mAuth.getCurrentUser().getUid())
                                                        .child(messages.getMessageId()).child("message")
                                                        .setValue("Message deleted").addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        if (task.isSuccessful()){
                                                            Toast.makeText(context, "Message Deleted", Toast.LENGTH_SHORT).show();
                                                        }else{
                                                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                                        }
                                                    }
                                                });

                                            }else{
                                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                                }else{

                                }

                            }
                        });
                        builder.show();
                        return false;


                    }
                });

            }
            else{

                SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");

                // Create a calendar object that will convert the date and time value in milliseconds to date.
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(messages.getTime());

                String msgTime=formatter.format(calendar.getTime());

                viewHolder.sender.setVisibility(View.GONE);
                viewHolder.senderCl.setVisibility(View.GONE);
                viewHolder.senderImgCv.setVisibility(View.GONE);

                viewHolder.senderImg.setVisibility(View.INVISIBLE);
                viewHolder.senderImgMsg.setVisibility(View.GONE);


                viewHolder.receiverImg.setVisibility(View.VISIBLE);
                viewHolder.receiver.setVisibility(View.VISIBLE);
                viewHolder.receiverCl.setVisibility(View.GONE);
                viewHolder.receiverImgMsg.setVisibility(View.VISIBLE);


                //viewHolder.receiver.setTextColor(Color.WHITE);
                viewHolder.receiver.setGravity(Gravity.LEFT);
                viewHolder.receiver.setText(messages.getMessage());
                viewHolder.receiveMsgTime.setText(msgTime);

          //      Glide.with(context).load(otherUserImg).into(viewHolder.receiverImg);

                Glide.with(context).load(messages.getMessage()).into(viewHolder.receiverImgMsg);


            }
        }
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

