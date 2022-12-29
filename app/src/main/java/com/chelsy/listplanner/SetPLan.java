package com.chelsy.listplanner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SetPLan extends AppCompatActivity {
    EditText nameEdit, dateEdit, timeEdit, descEdit;
    Button save, cancel;
    Calendar calendar;
    FirebaseAuth mAuth;
    FirebaseFirestore db;
    String uIdPlan;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // hide action bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_set_plan);

        //progress bar
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        //database firebase
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        nameEdit = (EditText) findViewById(R.id.titlePlan);
        dateEdit = (EditText) findViewById(R.id.date);
        timeEdit = (EditText) findViewById(R.id.time);
        descEdit = (EditText) findViewById(R.id.desc);

        save = (Button) findViewById(R.id.save);
        cancel = (Button) findViewById(R.id.cancel);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addPlan();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), ActPlan.class);
                startActivity(intent);
                finish();
            }
        });
        dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dateDialog(dateEdit);
            }
        });

        timeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeDialog(timeEdit);
            }
        });

        Intent intent = getIntent();
        uIdPlan = intent.getStringExtra("uId");

        if (uIdPlan != null) {
            editPlan(uIdPlan);
        }
    }

    //date dialog
    private void dateDialog(EditText dateEdit) {
        DatePickerDialog datePickerDialog;
        DatePickerDialog.OnDateSetListener dataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                DateFormat SDF = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                String strDate = SDF.format(calendar.getTime());
                dateEdit.setText(strDate);
            }
        };
        datePickerDialog = new DatePickerDialog(this, dataSetListener,
                calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.show();
    }

    // time dialog
    private void timeDialog(EditText timeEdit) {
        TimePickerDialog timePickerDialog;
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                DateFormat SDF = new SimpleDateFormat("HH:mm", Locale.US);
                String strTime = SDF.format(calendar.getTime());
                timeEdit.setText(strTime);
            }
        };
        timePickerDialog = new TimePickerDialog(this, timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false);
        timePickerDialog.show();
    }

    // add plan
    public void addPlan() {
        String inputTitle, inputDate, inputTime, inputDesc, uId;
        inputTitle = nameEdit.getText().toString().trim();
        inputDate = dateEdit.getText().toString().trim();
        inputTime = timeEdit.getText().toString().trim();
        inputDesc = descEdit.getText().toString().trim();
        uId = mAuth.getCurrentUser().getUid();

        if (inputTitle.isEmpty()) {
            nameEdit.setError("Name of plan is required");
            nameEdit.requestFocus();
            return;
        }
        if (inputDate.isEmpty()) {
            dateEdit.setError("Set date is required");
            dateEdit.requestFocus();
            return;
        }
        if (inputTime.isEmpty()) {
            timeEdit.setError("Set time is required");
            timeEdit.requestFocus();
            return;
        }
        if (inputDesc.isEmpty()) {
            descEdit.setError("Description of plan is required");
            descEdit.requestFocus();
            return;
        }
        if (inputDesc.length() < 30) {
            descEdit.setError("Decription must be minimum 30 characters");
            descEdit.requestFocus();
            return;
        }
        if (inputTitle.length() < 8) {
            nameEdit.setError("Name of plan must be minimum 8 characters");
            nameEdit.requestFocus();
            return;
        }
        progressBar.setVisibility(View.VISIBLE);

        Plans dataPlan = new Plans(inputTitle, inputDate, inputTime, inputDesc, uId);
        // edit plan
        if (uIdPlan != null) {
            db.collection("Plans").document(uIdPlan)
                    .set(dataPlan)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getBaseContext(), "Plan has been updated", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getBaseContext(), ActPlan.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(), "Error " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
            // add plan
        } else {
            db.collection("Plans")
                    .add(dataPlan)
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(), "Your plan has been added", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(getBaseContext(), ActPlan.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getBaseContext(), "An error has been occured " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    // get data for edit plan
    private void editPlan(String uIdPlan) {
        db.collection("Plans").document(uIdPlan)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            nameEdit.setText(documentSnapshot.getString("title"));
                            dateEdit.setText(documentSnapshot.getString("date"));
                            timeEdit.setText(documentSnapshot.getString("time"));
                            descEdit.setText(documentSnapshot.getString("desc"));
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "Error " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}