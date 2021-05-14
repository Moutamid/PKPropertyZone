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

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.techroof.pkpropertyzone.BuyProperty.ViewPropertyActivity;
import com.techroof.pkpropertyzone.Model.Property;
import com.techroof.pkpropertyzone.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ConstructionAdapter extends RecyclerView.Adapter<ConstructionAdapter.MyViewHolder> implements Filterable {
    private static final String TAG = "ConstructionAdapter";

    Context context;
    ArrayList<Property> mData;
    ArrayList<Property> mDataList;

    public ConstructionAdapter(Context context, ArrayList<Property> mData) {
        this.context = context;
        this.mData = new ArrayList<>(mData);
        this.mDataList = new ArrayList<>(mData);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.property_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {
        holder.addressImage.setVisibility(View.GONE);
        holder.fullAddress.setVisibility(View.GONE);
        holder.propertyPrice.setVisibility(View.GONE);
        holder.constructionName.setText(mData.get(position).getPropertyName());
        holder.constructionDescription.setText(mData.get(position).getPropertyDescription());
        Picasso.get()
        .load("https://firebasestorage.googleapis.com/v0/b/pkpropertyzone-3ea93.appspot.com/o/images%2Fnicolas-j-leclercq-WJg2bynUWOk-unsplash.jpg?alt=media&token=42253c16-c4d1-497a-a98b-a83ecbbe410f")
                .placeholder(R.drawable.prop_img)
                .into(holder.constructionImage, new Callback() {
                    @Override
                    public void onSuccess() {
//                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(context, "Error while loading image", Toast.LENGTH_SHORT).show();
                    }
                });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ViewPropertyActivity.class);
                intent.putExtra("property_id",mData.get(position).getPropertyId());
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
        return constructionFilter;
    }

    private Filter constructionFilter = new Filter() {
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
                    if (item.getPropertyDescription() != null)
                        if (item.getPropertyDescription().toLowerCase().contains(filterPattern)) {
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
        private ImageView constructionImage,addressImage;
        private TextView  constructionName, constructionDescription,fullAddress,propertyPrice;
//        private ProgressBar progressBar;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            constructionImage = itemView.findViewById(R.id.property_img);
            constructionName = itemView.findViewById(R.id.property_name);
            constructionDescription = itemView.findViewById(R.id.property_desc);
//            progressBar = itemView.findViewById(R.id.img_pb);
            addressImage = itemView.findViewById(R.id.address_img);
            fullAddress = itemView.findViewById(R.id.address_text);
            propertyPrice = itemView.findViewById(R.id.property_price);
        }
    }
//    public Filter getFilterConstruct()
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
