package com.techroof.pkpropertyzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.techroof.pkpropertyzone.BuyProperty.ViewPropertyActivity;
import com.techroof.pkpropertyzone.Model.Property;
import com.techroof.pkpropertyzone.R;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;

import de.hdodenhof.circleimageview.CircleImageView;

public class BuyPropertyAdapter  extends RecyclerView.Adapter<BuyPropertyAdapter.BuyerPropertyViewHolder> {

    private Context context;
    private ArrayList<Property> propertyArrayList;
    private ArrayList<Property> propertyAL;

    private FirebaseFirestore db;

    public BuyPropertyAdapter(Context context, ArrayList<Property> propertyArrayList) {
        this.context = context;
        this.propertyArrayList = propertyArrayList;
        this.propertyAL=propertyArrayList;
        db = FirebaseFirestore.getInstance();

    }

    @NonNull
    @Override
    public BuyerPropertyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.property_layout, parent, false);

        BuyerPropertyViewHolder buyerPropertyViewHolder = new BuyerPropertyViewHolder(view);
        return buyerPropertyViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final BuyerPropertyViewHolder holder, final int position) {

        //Glide.with().load(dishesArrayList.get(position).getDishImage()).into(holder.image);
        Picasso.get().load(propertyArrayList.get(position).getImageUrl())
                .into(holder.image, new Callback() {
            @Override
            public void onSuccess() {
//                holder.imgPb.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(context, "Error while loading the image", Toast.LENGTH_SHORT).show();
            }
        });

        holder.name.setText(propertyArrayList.get(position).getPropertyName());
        holder.price.setText("Rs. " + propertyArrayList.get(position).getPropertyPrice());
        holder.desc.setText(propertyArrayList.get(position).getPropertyDescription());
        holder.location.setText(propertyArrayList.get(position).getLocation());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

             Intent viewProperty = new Intent(context, ViewPropertyActivity.class);
                 viewProperty.putExtra("property_id",
                         propertyArrayList.get(position).getPropertyId());

                 viewProperty.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(viewProperty);
            }
        });
    }

    @Override
    public int getItemCount() {
        return propertyArrayList.size();
    }

    public static class BuyerPropertyViewHolder extends RecyclerView.ViewHolder {

        private TextView name, price, desc,location;
        private ImageView image;
//        private ProgressBar imgPb;

        public BuyerPropertyViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.property_name);
            price = itemView.findViewById(R.id.property_price);
            desc = itemView.findViewById(R.id.property_desc);
//            imgPb = itemView.findViewById(R.id.img_pb);
            image=itemView.findViewById(R.id.property_img);
            location=itemView.findViewById(R.id.address_text);
        }
    }

    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String Key = charSequence.toString();
                if(Key.isEmpty()){

                    propertyArrayList = propertyAL;

                }
                else {
                    ArrayList<Property> filtered = new ArrayList<>();
                    for (Property property : propertyAL) {
                        if(property.getLocation().toLowerCase().contains(Key.toLowerCase())){
                            filtered.add(property);
                        }
                    }
                    propertyArrayList = filtered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = propertyArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                propertyArrayList = (ArrayList<Property>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }
}
