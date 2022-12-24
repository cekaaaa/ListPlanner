package com.chelsy.listplanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageView;

public class ActCredit extends AppCompatActivity {

    ImageView linkedin, instagram, github, ukrida;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_act_credit);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        linkedin = (ImageView) findViewById(R.id.linkedin);
        linkedin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((android.content.Intent.ACTION_VIEW),
                        Uri.parse("https://www.linkedin.com/in/chelsy-kwan/"));
                startActivity(intent);
            }
        });
        instagram = (ImageView) findViewById(R.id.instagram);
        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((android.content.Intent.ACTION_VIEW),
                        Uri.parse("https://www.instagram.com/lie_chelsy/"));
                startActivity(intent);
            }
        });
        github = (ImageView) findViewById(R.id.github);
        github.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((android.content.Intent.ACTION_VIEW),
                        Uri.parse("https://github.com/cekaaaa"));
                startActivity(intent);
            }
        });
        ukrida = (ImageView) findViewById(R.id.ukrida);
        ukrida.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent((android.content.Intent.ACTION_VIEW),
                        Uri.parse("http://www.ukrida.ac.id"));
                startActivity(intent);
            }
        });


    }
}