package com.techroof.pkpropertyzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.TextView;

import com.techroof.pkpropertyzone.Model.Areas;
import com.techroof.pkpropertyzone.R;
import com.techroof.pkpropertyzone.PropertyActivity;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AreasAdapter extends RecyclerView.Adapter<AreasAdapter.MyViewHolder> {
    Context context;
    ArrayList <Areas> areas;
    ArrayList <Areas> areasArrayList;

    public AreasAdapter(Context context, ArrayList<Areas> areas) {
        this.context = context;
        this.areas = areas;
        this.areasArrayList = areas;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.areas_model_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        holder.areaName.setText(areasArrayList.get(position).getAreaName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PropertyActivity.class);
                intent.putExtra("cityId",areasArrayList.get(position).getCityId());
                intent.putExtra("cityName",areasArrayList.get(position).getCityName());
                intent.putExtra("areaName",areasArrayList.get(position).getAreaName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return areasArrayList.size();
    }

    public Filter getFilter(){
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String key = charSequence.toString();
                if(key.isEmpty())
                {
                    areasArrayList = areas;
                }
                else
                {
                    ArrayList<Areas> filtered = new ArrayList<>();
                    for(Areas areas : areas)
                    {
                        if(areas.getAreaName().toLowerCase().contains(key.toLowerCase()))
                        {
                            filtered.add(areas);
                        }
                    }
                    areasArrayList = filtered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = areasArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                areasArrayList = (ArrayList<Areas>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView areaName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            areaName = itemView.findViewById(R.id.areaName);
        }
    }
}
