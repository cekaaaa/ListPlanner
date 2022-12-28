package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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

    // Menu inflater
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
        setContentView(R.layout.activity_act_profile);

        // firebase
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

    // get user for profile picture, username, email by uid user
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

    // logout
    private void checkLogout() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null) {
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}