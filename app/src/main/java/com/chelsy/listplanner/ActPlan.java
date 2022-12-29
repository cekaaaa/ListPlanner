package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ActPlan extends AppCompatActivity {
    TextView txtprofile;
    String username;
    FloatingActionButton addPlan;
    FirebaseFirestore db;
    RecyclerView recyclerPlan;
    ArrayList<AlterPlans> plansArr;
    PlanAdapter adapter;
    FirebaseAuth mAuth;
    ShapeableImageView profPic;
    String imgUrl;

    // menu inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.nav_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // item menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.home){
            Intent intent = new Intent(getBaseContext(), ActPlan.class);
            startActivity(intent);
        }
        if (id == R.id.profile){
            Intent intent = new Intent(getBaseContext(), ActProfile.class);
            startActivity(intent);
        }
        if (id == R.id.about){
            Intent intent = new Intent(getBaseContext(), ActCredit.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        //recycler plan
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

        profPic = findViewById(R.id.imgprofile);
        profPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActProfile.class);
                startActivity(intent);
            }
        });

        txtprofile = (TextView) findViewById(R.id.profile);
        getUser();
    }

    // Get user for profile picture and textview
    public void getUser() {
        String uId = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(uId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        username = documentSnapshot.getString("username");
                        imgUrl = documentSnapshot.getString("profileLocation");
                        txtprofile.setText("Hello, " + username);
                        Picasso.get().load(imgUrl).into(profPic);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Set your username first", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // populate data plan
    public void PopData() {
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