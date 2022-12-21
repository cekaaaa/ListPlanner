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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;


public class Register extends AppCompatActivity {

    EditText inputUserEdit, inputEmailEdit, inputPassEdit, inputRepassEdit;
    String inputUser, inputEmail, inputPass, inputRepass;
    Button btnregis;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    Map<String, Object> user = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        if(getSupportActionBar() != null){
            getSupportActionBar().hide();
        }

        inputUserEdit = (EditText) findViewById(R.id.txtusername);
        inputEmailEdit = (EditText) findViewById(R.id.txtemail);
        inputPassEdit = (EditText) findViewById(R.id.txtpass);
        inputRepassEdit = (EditText) findViewById(R.id.txtrepass);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnregis = (Button) findViewById(R.id.btnregis);
        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doRegister();
            }
        });
    }

    // Validation
    private void doRegister() {
        inputUser = inputUserEdit.getText().toString().trim();
        inputEmail = inputEmailEdit.getText().toString().trim();
        inputPass = inputPassEdit.getText().toString().trim();
        inputRepass = inputRepassEdit.getText().toString().trim();

        if(inputUser.isEmpty()){
            inputUserEdit.setError("Username is required");
            inputUserEdit.requestFocus();
            return;
        }
        if(inputEmail.isEmpty()){
            inputEmailEdit.setError("Email is required");
            inputEmailEdit.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()){
            inputEmailEdit.setError("Invalid email address");
            inputEmailEdit.requestFocus();
            return;
        }
        if(inputPass.isEmpty()){
            inputPassEdit.setError("Password is required");
            inputPassEdit.requestFocus();
            return;
        }
        if(inputPass.length() < 8){
            inputPassEdit.setError("Password should be minimum 8 character");
            inputPassEdit.requestFocus();
            return;
        }
        if(inputRepass.isEmpty()){
            inputRepassEdit.setError("Retype password is required");
            inputRepassEdit.requestFocus();
            return;
        }
        if(!inputRepass.equals(inputPass)){
            inputRepassEdit.setError("Retype password must be same as password");
            inputRepassEdit.requestFocus();
            return;
        }

        // register account
        mAuth.createUserWithEmailAndPassword(inputEmail, inputPass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            String uId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            user.put("email", inputEmail);
                            user.put("username", inputUser);
                            db.collection("Users")
                                    .document(uId)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(getBaseContext(), "Your account has been registered", Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(getBaseContext(), Login.class);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getBaseContext(), "Failed to register you account!", Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getBaseContext(), "Failed to register you account!", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}