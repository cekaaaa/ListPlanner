package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class ActEditProfile extends AppCompatActivity {

    Button btnsave, btncancel;
    EditText inputname, inputemail;
    TextView changeimg;
    String name, email;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String currImageUrl;
    private static final int PICK_IMG = 1;
    ShapeableImageView profPic;
    StorageReference mStore;
    ProgressBar progressBar;

    private Uri imUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_edit_profile);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mStore = FirebaseStorage.getInstance().getReference("User Picture");
        btnsave = (Button) findViewById(R.id.save);
        btncancel = (Button) findViewById(R.id.cancel);
        profPic = (ShapeableImageView) findViewById(R.id.imageView);
        inputname = (EditText) findViewById(R.id.username);
        inputemail = (EditText) findViewById(R.id.email);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
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
                Intent intent = new Intent(getBaseContext(), ActProfile.class);
                startActivity(intent);
            }
        });

        changeimg = (TextView) findViewById(R.id.changeimg);
        changeimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openStorage();
            }
        });

    }

    private void openStorage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMG && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            imUri = data.getData();

            Picasso.get().load(imUri).into(profPic);
        }

    }

    private String getFileExt(Uri uri) {
        ContentResolver cr = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cr.getType(uri));
    }

    private void editProfile() {
        String uId = mAuth.getCurrentUser().getUid();
        name = inputname.getText().toString().trim();
        email = inputemail.getText().toString().trim();
        if (name.isEmpty()) {
            inputname.setError("Name is required");
            inputname.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        if (imUri != null) {
            StorageReference fileR = mStore.child(System.currentTimeMillis() + "." + getFileExt(imUri));
            fileR.putFile(imUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    String imUrl = uri.toString();
                                    ProfileUser profileUser = new ProfileUser(name, email, imUrl);
                                    db.collection("Users")
                                            .document(uId)
                                            .set(profileUser)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        progressBar.setVisibility(View.GONE);
                                                        Toast.makeText(getBaseContext(), "Profile has been updated", Toast.LENGTH_LONG).show();
                                                        Intent intent = new Intent(getBaseContext(), ActProfile.class);
                                                        startActivity(intent);
                                                        finish();

                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    progressBar.setVisibility(View.GONE);
                                                    Toast.makeText(getBaseContext(), "Error has been occured " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(getBaseContext(), "There has been an error when uploading a document: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                    });
        } else {
            ProfileUser users = new ProfileUser(name, email, currImageUrl);
            db.collection("Users").document(uId)
                    .set(users)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(), "Your profile has been updated", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getBaseContext(), ActProfile.class);
                            startActivity(intent);
                            finish();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(ActEditProfile.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    });
        }

    }

    public void getCurrentProfile() {
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
                            currImageUrl = documentSnapshot.getString("profileLocation");
                            Picasso.get().load(currImageUrl).into(profPic);
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