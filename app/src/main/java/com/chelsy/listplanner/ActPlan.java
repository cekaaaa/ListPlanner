package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class ActPlan extends AppCompatActivity {
    FloatingActionButton addPlan;
    FirebaseFirestore db;
    RecyclerView recyclerPlan;
    CollectionReference planRef;
    PlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        recyclerPlan = findViewById(R.id.recPlan);
        planRef = db.collection("Plans");

        addPlan = (FloatingActionButton) findViewById(R.id.addPlan);
        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SetPLan.class);
                startActivity(intent);
            }
        });
    initData();
    }
    public void initData(){
        Query query = planRef;
        FirestoreRecyclerOptions<Plans> options = new FirestoreRecyclerOptions.Builder<Plans>()
                .setQuery(query, Plans.class)
                .build();

        adapter = new PlanAdapter(options);

        recyclerPlan.setHasFixedSize(true);
        recyclerPlan.setLayoutManager(new LinearLayoutManager(getBaseContext()));
        recyclerPlan.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }
}