package com.techroof.pkpropertyzone.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.techroof.pkpropertyzone.R;
import com.techroof.pkpropertyzone.Model.AddPropertyModel;
import com.techroof.pkpropertyzone.SellerViewPropertyActivity;

import java.nio.DoubleBuffer;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AddpropertyAdapter extends RecyclerView.Adapter<AddpropertyAdapter.MyViewHolder> {
    Context context;
    ArrayList<AddPropertyModel> mData;
    private ProgressDialog pd;
    private FirebaseFirestore db;

    public AddpropertyAdapter(Context context, ArrayList<AddPropertyModel> mData) {
        this.context = context;
        this.mData = mData;

        db=FirebaseFirestore.getInstance();

        pd=new ProgressDialog(context);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Please wait...");
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.propertyName.setText(mData.get(position).getPropertyName());
        holder.propertyDescripton.setText(mData.get(position).getPropertyDescription());
        holder.propertyPrice.setText(mData.get(position).getPropertyPrice());

        Picasso.get().load(mData.get(position).getImageUrl())
                .into(holder.propertyImage, new Callback() {
                    @Override
                    public void onSuccess() {
//                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "Error while loading the image", Toast.LENGTH_SHORT).show();
                    }
                });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                CharSequence options[] = new CharSequence[]{
                        "YES", "NO"
                };
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Delete Property?");

                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        if (which == 0) {

                            pd.show();


                            db.collection("Properties")
                                    .document(mData.get(position).getPropertyId())
                                    .delete()
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            if (task.isSuccessful()){

                                                Intent property = new Intent(context, SellerViewPropertyActivity.class);
                                                context.startActivity(property);
                                                ((Activity)context).finish();
                                                pd.dismiss();

                                            }else{
                                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                                            }


                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    pd.dismiss();
                                }
                            });

                            Toast.makeText(context, "Deleted", Toast.LENGTH_SHORT).show();


                        } else {

                            pd.dismiss();
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                builder.show();

                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        private ImageView propertyImage;
        private TextView propertyName;
        private TextView propertyDescripton;
        private TextView propertyPrice;
//        private ProgressBar progressBar;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImage = itemView.findViewById(R.id.property_img);
            propertyName = itemView.findViewById(R.id.property_name);
            propertyDescripton = itemView.findViewById(R.id.property_desc);
            propertyPrice = itemView.findViewById(R.id.property_price);
//            progressBar=itemView.findViewById(R.id.img_pb);
        }
    }
}
