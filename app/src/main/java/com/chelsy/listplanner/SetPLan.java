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
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setContentView(R.layout.activity_set_plan);

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
        String paramUId = intent.getStringExtra("uId");
        Toast.makeText(this, "uId" + paramUId, Toast.LENGTH_SHORT).show();
    }

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

    private void timeDialog(EditText timeEdit){
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

    public void addPlan(){
        String inputTitle, inputDate, inputTime, inputDesc, uId;
        inputTitle = nameEdit.getText().toString().trim();
        inputDate = dateEdit.getText().toString().trim();
        inputTime = timeEdit.getText().toString().trim();
        inputDesc = descEdit.getText().toString().trim();
        uId = mAuth.getCurrentUser().getUid();

        if(inputTitle.isEmpty()){
            nameEdit.setError("Name of plan is required");
            nameEdit.requestFocus();
            return;
        }
        if(inputDate.isEmpty()){
            dateEdit.setError("Set date is required");
            dateEdit.requestFocus();
            return;
        }
        if (inputTime.isEmpty()){
            timeEdit.setError("Set time is required");
            timeEdit.requestFocus();
            return;
        }
        if (inputDesc.isEmpty()){
            descEdit.setError("Description of plan is required");
            descEdit.requestFocus();
            return;
        }
        if (inputDesc.length() < 50){
            descEdit.setError("Decription must be minimum 50 characters");
            descEdit.requestFocus();
            return;
        }
        if (inputTitle.length() < 10){
            nameEdit.setError("Name of plan must be minimum 10 characters");
            nameEdit.requestFocus();
            return;
        }

        Plans dataPlan = new Plans(inputTitle, inputDate, inputTime, inputDesc, uId);
        db.collection("Plans")
                .add(dataPlan)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Toast.makeText(getBaseContext(), "Your plan has been added", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getBaseContext(), ActPlan.class);
                        startActivity(intent);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getBaseContext(), "An error has been occured " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}