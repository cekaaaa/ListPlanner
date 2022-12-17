package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class Login extends AppCompatActivity {

    EditText inputemail, inputpass;
    Button btnlogin;
    String email, pass;
    FirebaseAuth mAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        btnlogin = (Button) findViewById(R.id.btnlogin2);

        inputemail = (EditText) findViewById(R.id.txtemail);
        inputpass = (EditText) findViewById(R.id.txtpass);
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
            }
        });
    }

    private void doLogin() {
        email = inputemail.getText().toString().trim();
        pass = inputpass.getText().toString().trim();

        if (email.isEmpty()) {
            inputemail.setError("Email is required");
            inputemail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            inputemail.setError("Enter a valid email");
            inputemail.requestFocus();
            return;
        }

        if (pass.isEmpty()) {
            inputpass.setError("Email is required");
            inputpass.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getBaseContext(), ActPlan.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(getBaseContext(), "Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

}