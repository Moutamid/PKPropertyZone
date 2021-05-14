package com.techroof.pkpropertyzone.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.techroof.pkpropertyzone.AreasActivity;
import com.techroof.pkpropertyzone.Model.Cities;
import com.techroof.pkpropertyzone.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.MyViewHolder> implements Filterable {
    Context context;
    ArrayList<Cities> cities;
    ArrayList<Cities> citiesArrayList;

    public CitiesAdapter(Context context, ArrayList<Cities> cities) {
        this.context = context;
        this.cities = cities;
        this.citiesArrayList = cities;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cities_model_layout,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        holder.cityName.setText(citiesArrayList.get(position).getCityName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AreasActivity.class);
                intent.putExtra("cityId",citiesArrayList.get(position).getCityId());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return citiesArrayList.size();
    }

    private Filter getFilter1()
    {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String key = charSequence.toString();
                if(key.isEmpty())
                {
                    citiesArrayList = cities;
                }
                else
                {
                    ArrayList<Cities> filtered = new ArrayList<>();
                    for(Cities cities : cities)
                    {
                        if(cities.getCityName().toLowerCase().contains(key.toLowerCase()))
                        {
                            filtered.add(cities);
                        }
                    }
                    citiesArrayList = filtered;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = citiesArrayList;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                citiesArrayList = (ArrayList<Cities>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public Filter getFilter() {
        return getFilter1();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView cityName;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            cityName = itemView.findViewById(R.id.cityName);
        }
    }
}
