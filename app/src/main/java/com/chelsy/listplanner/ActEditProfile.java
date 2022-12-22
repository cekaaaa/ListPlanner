package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ActEditProfile extends AppCompatActivity {

    Button btnsave, btncancel;
    EditText inputname, inputemail;
    String name, email;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_edit_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        btnsave = (Button) findViewById(R.id.save);
        btncancel = (Button) findViewById(R.id.cancel);

        inputname = (EditText) findViewById(R.id.username);
        inputemail = (EditText) findViewById(R.id.email);
        getCurrentProfile();
        btnsave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile();
            }
        });
        btncancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActEditProfile.class);
                startActivity(intent);
            }
        });
    }

    private void editProfile() {
        String uId = mAuth.getCurrentUser().getUid();
        name = inputname.getText().toString().trim();
        email = inputemail.getText().toString().trim();
        if (name.isEmpty()){
            inputname.setError("Name is required");
            inputname.requestFocus();
            return;
        }
        Users users = new Users(name, email);
        db.collection("Users").document(uId)
                .set(users)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getBaseContext(), "Your profile has been updated", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getBaseContext(), ActProfile.class);
                        startActivity(intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ActEditProfile.this, "", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void getCurrentProfile(){
        String uId = mAuth.getCurrentUser().getUid();
        db.collection("Users").document(uId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            inputname.setText(documentSnapshot.getString("username"));
                            inputemail.setText(documentSnapshot.getString("email"));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}