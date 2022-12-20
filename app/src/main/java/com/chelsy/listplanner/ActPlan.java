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
import android.widget.ImageView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class ActPlan extends AppCompatActivity {
    FloatingActionButton addPlan;
    FirebaseFirestore db;
    RecyclerView recyclerPlan;
    ArrayList<AlterPlans> plansArr;
    PlanAdapter adapter;
    FirebaseAuth mAuth;
    ImageView profileImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        recyclerPlan = findViewById(R.id.recPlan);
        recyclerPlan.setLayoutManager(new LinearLayoutManager(this));
        recyclerPlan.setHasFixedSize(true);

        plansArr = new ArrayList<AlterPlans>();
        adapter = new PlanAdapter(ActPlan.this, plansArr);
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

        profileImg = (ImageView) findViewById(R.id.imgprofile);
        profileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActProfile.class);
                startActivity(intent);
            }
        });
    }

    private void PopData() {
        String uId = mAuth.getCurrentUser().getUid();
        db.collection("Plans")
                .whereEqualTo("uId", uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                AlterPlans alterPlans = new AlterPlans(document.getString("title"),
                                        document.getString("date"),
                                        document.getString("time"),
                                        document.getId());
                                plansArr.add(alterPlans);
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }
}