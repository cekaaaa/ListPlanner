package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActProfile extends AppCompatActivity {

    Button logout;
    TextView txtname, txtemail;
    FirebaseAuth mAuth;
    String username, email;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        logout = (Button) findViewById(R.id.btnlogout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                checkLogout();
            }
        });

        txtname = (TextView) findViewById(R.id.txtprofilename);
        txtemail = (TextView) findViewById(R.id.txtprofileemail);
        getUser();
    }

    public void getUser() {
        String uId = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(uId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        username = documentSnapshot.getString("username");
                        email = documentSnapshot.getString("email");
                        txtname.setText(username);
                        txtemail.setText(email);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Set your username first", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkLogout() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}