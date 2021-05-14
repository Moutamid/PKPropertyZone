package com.techroof.pkpropertyzone.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.techroof.pkpropertyzone.Adapter.ConstructionAdapter;
import com.techroof.pkpropertyzone.Adapter.ShopsAdapter;
//import com.techroof.pkpropertyzone.FilterClass;
import com.techroof.pkpropertyzone.Model.Property;
import com.techroof.pkpropertyzone.R;

import java.util.ArrayList;

public class ConstructionFragment extends Fragment {// implements FilterClass {
    private static final String TAG = "ConstructionFragment";

    private static final String PACKAGE_NAME = "dev.moutamid.pkpropertyzone";
    private SharedPreferences sharedPreferences;

    private String getStoredString(String name) {
        Log.d(TAG, "getStoredString: ");
        if (getActivity() == null){
            return "";
        }
        sharedPreferences = getActivity().getSharedPreferences(PACKAGE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(name, "");

    }

    private class CheckQuery extends AsyncTask<Void, Void, Void> {
        String commitId = "";

//        boolean commit = false;
//        boolean commitFilter = false;

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d(TAG, "doInBackground: ");
            String query = getStoredString("query");

//            if (query.equals("Error")) {
////                commit = false;
////                commitFilter = false;
//                commitId = "Error";
//                return null;
//            }
//
//            if (query.equals("")) {
//                modelProperty.clear();
//                modelProperty = new ArrayList<>(modelPropertyListFull);
//                commitId = "null";
////                commit = true;
////                commitFilter = true;
//                return null;
//            }

            commitId = query;

//            filterShopsRecyclerView(query);
            Log.d(TAG, "doInBackground: value:" + query);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            if (!commitId.equals("error"))
            if (getStoredString("allowed").equals("allowed"))
                constructionAdapter.getFilter().filter(commitId);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new CheckQuery().execute();
                    Log.d(TAG, "onPostExecute run: ");
                }
            }, 400);

        }
    }

    View view;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<Property> modelProperty = new ArrayList<>();
    private ConstructionAdapter constructionAdapter;
    private FirebaseFirestore firestore;
    private String cityId, areaName;
    private ProgressDialog pd;

    public ConstructionFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_construction, container, false);
        pd = new ProgressDialog(getContext());
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage("Loading");
        pd.show();

        cityId = getActivity().getIntent().getStringExtra("cityId");
        areaName = getActivity().getIntent().getStringExtra("areaName");

        recyclerView = view.findViewById(R.id.construction_recyclerView);
        layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(layoutManager);

        firestore = FirebaseFirestore.getInstance();
        showConstructionData();
        return view;
    }

    private void showConstructionData() {
        firestore.collection("Properties")
                .whereEqualTo("cityId", cityId)
                .whereEqualTo("areaName", areaName)
                .whereEqualTo("propertyType", "Construction")
                .orderBy("timestamp", Query.Direction.DESCENDING)

                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        modelProperty.clear();
                        for (DocumentSnapshot doc : task.getResult()) {
                            Property property = doc.toObject(Property.class);
                            modelProperty.add(property);
                        }
                        constructionAdapter = new ConstructionAdapter(getContext(), modelProperty);
                        recyclerView.setAdapter(constructionAdapter);
                        new CheckQuery().execute();
                        pd.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), "Error" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });
    }

//    @Override
//    public void searchItem(CharSequence text) {
////        constructionAdapter.getFilter().filter(text);
//    }
}