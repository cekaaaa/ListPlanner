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
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

public class ActProfile extends AppCompatActivity {

    Button logout, editprofile;
    TextView txtname, txtemail, credit;
    FirebaseAuth mAuth;
    String username, email;
    FirebaseFirestore db;
    ShapeableImageView profPic;
    String imgUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_profile);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        profPic = findViewById(R.id.imageView);
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
        credit = (TextView) findViewById(R.id.txtabout);
        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActCredit.class);
                startActivity(intent);
            }
        });
        editprofile = (Button) findViewById(R.id.btneditprofile);
        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActEditProfile.class);
                startActivity(intent);
            }
        });
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
                        imgUrl = documentSnapshot.getString("profileLocation");
                        txtname.setText(username);
                        txtemail.setText(email);
                        Picasso.get().load(imgUrl).into(profPic);
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