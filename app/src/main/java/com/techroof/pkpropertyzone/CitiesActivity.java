package com.techroof.pkpropertyzone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.pkpropertyzone.Adapter.CitiesAdapter;
import com.techroof.pkpropertyzone.Model.Cities;

import java.util.ArrayList;

public class CitiesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ArrayList<Cities> modelCities = new ArrayList<>();
    private ImageView backBtn;
    private FirebaseFirestore firestore;
    private CitiesAdapter citiesAdapter;
    private EditText searchInput;
    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cities);

        pd = new ProgressDialog(this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading...");
        pd.show();

        recyclerView = findViewById(R.id.cities_recycler_view);
        backBtn = findViewById(R.id.back_btn);
        firestore = FirebaseFirestore.getInstance();
        searchInput = findViewById(R.id.search_input);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager
                (2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        backBtn.setVisibility(View.VISIBLE);

        backBtn.setColorFilter(ContextCompat.getColor(this, R.color.textColor), PorterDuff.Mode.MULTIPLY);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CitiesActivity.super.onBackPressed();
            }
        });

        showCitiesData();
    }

    private void showCitiesData() {

        firestore.collection("Places").orderBy("cityName", Query.Direction.ASCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelCities.clear();

                        for (DocumentSnapshot doc : task.getResult()) {
                            Cities cities = doc.toObject(Cities.class);
                            modelCities.add(cities);
                        }
                        citiesAdapter = new CitiesAdapter(CitiesActivity.this, modelCities);
                        recyclerView.setAdapter(citiesAdapter);

                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CitiesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (citiesAdapter != null)
                    citiesAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
}