package com.example.sosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class EmergencyContactView extends AppCompatActivity {
    Button AECButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_view);
        AECButton=findViewById(R.id.AddEmergencyContactButton);
        AECButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EmergencyContactView.this, EmergencyContactAdd.class));

            }
        });
    }
}