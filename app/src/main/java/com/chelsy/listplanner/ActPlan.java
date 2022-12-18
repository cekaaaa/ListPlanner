package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ActPlan extends AppCompatActivity {
    FloatingActionButton addPlan;
    FirebaseFirestore db;
    RecyclerView recyclerPlan;
    ArrayList<Plans> plansArr;
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
        recyclerPlan.setLayoutManager(new LinearLayoutManager(this));
        recyclerPlan.setHasFixedSize(true);

        plansArr = new ArrayList<Plans>();
        adapter = new PlanAdapter(getBaseContext(), plansArr);
        recyclerPlan.setAdapter(adapter);
        PopData();

        addPlan = (FloatingActionButton) findViewById(R.id.addPlan);
        addPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), SetPLan.class);
                startActivity(intent);
            }
        });
    }

    private void PopData() {
        db.collection("Plans")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for(QueryDocumentSnapshot document: task.getResult()) {
                                Plans plans = new Plans(document.getString("title"),
                                        document.getString("date"),
                                        document.getString("time"));
                                plansArr.add(plans);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}