package com.techroof.pkpropertyzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.techroof.pkpropertyzone.BuyProperty.ViewPropertyActivity;
import com.techroof.pkpropertyzone.Model.AddPropertyModel;
import com.techroof.pkpropertyzone.Model.Property;
import com.techroof.pkpropertyzone.R;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ShopsAdapter extends RecyclerView.Adapter<ShopsAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "ShopsAdapter";
    Context context;
    ArrayList<Property> mData = new ArrayList<>();
    ArrayList<Property> mDataList = new ArrayList<>();

    public ShopsAdapter(Context context, ArrayList<Property> mData) {
        this.context = context;
        this.mData = new ArrayList<>(mData);
        this.mDataList = new ArrayList<>(mData);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.property_layout, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.propertyName.setText(mData.get(position).getPropertyName());
        holder.propertyDescription.setText(mData.get(position).getPropertyDescription());
        holder.fullAddress.setText(mData.get(position).getFullAddress());
        holder.propertyPrice.setText(mData.get(position).getPropertyPrice());

        Picasso.get()
                .load(mData.get(position).getImageUrl())
                .placeholder(R.drawable.image1)
                .into(holder.propertyImage, new Callback() {
                    @Override
                    public void onSuccess() {
//                          holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "Error while loading the image", Toast.LENGTH_SHORT).show();
                    }
                });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPropertyActivity.class);
                intent.putExtra("property_id", mData.get(position).getPropertyId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public Filter getFilter() {
        Log.d(TAG, "getFilter: ");
        return shopsFilter;
    }

    private Filter shopsFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            ArrayList<Property> filteredList = new ArrayList<>();
            Log.d(TAG, "performFiltering: ");
            if (constraint == null
                    || constraint.length() == 0
                    || constraint.toString().trim().equals("")
                    || constraint.toString() == null)
            {
                Log.d(TAG, "performFiltering: if (constraint == null || constraint.length() == 0 || constraint.toString().trim().equals(\"\")) {");
                filteredList.addAll(mDataList);
            } else {
                Log.d(TAG, "performFiltering: } else {");
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (Property item : mDataList) {
                    if (item.getFullAddress() != null)
                    if (item.getFullAddress().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (mData == null) {
                Log.d(TAG, "publishResults: if (mData == null) {");
                return;
            }
            if (results.values == null) {
                Log.d(TAG, "publishResults: if ( results.values == null){");
                return;
            }
            mData.clear();
//            mData = new ArrayList<>();
//            mData.clear();
//            mData = (ArrayList<Property>) results.values;
            mData.addAll((ArrayList<Property>) results.values);
            notifyDataSetChanged();
            Log.d(TAG, "publishResults: done");
        }
    };

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private ImageView propertyImage;
        private TextView propertyName, propertyDescription, fullAddress, propertyPrice;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            propertyImage = itemView.findViewById(R.id.property_img);
            propertyName = itemView.findViewById(R.id.property_name);
            propertyDescription = itemView.findViewById(R.id.property_desc);
            fullAddress = itemView.findViewById(R.id.address_text);
            propertyPrice = itemView.findViewById(R.id.property_price);
//            progressBar = itemView.findViewById(R.id.img_pb);
        }
    }
//    public Filter getFilterShop()
//    {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence charSequence) {
//                String key = charSequence.toString();
//                if(key.isEmpty())
//                {
//                    mData = mDataList;
//                }
//                else
//                {
//                    ArrayList<Property> filtered = new ArrayList<>();
//                    for(Property property : mDataList)
//                    {
//                        if(property.getPropertyName().toLowerCase().contains(key.toLowerCase()))
//                        {
//                            filtered.add(property);
//                        }
//                    }
//                    mData = filtered;
//                }
//                FilterResults filterResults = new FilterResults();
//                filterResults.values = mData;
//                return filterResults;
//            }
//
//            @Override
//            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
//                mData = (ArrayList<Property>)filterResults.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
}
