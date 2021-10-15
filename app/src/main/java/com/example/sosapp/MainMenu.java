package com.example.sosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    Button setVoiceTriggerBtn, AddEmergencyContactBtn,yourRespBtn,LiveTracingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setVoiceTriggerBtn=findViewById(R.id.VoiceTriggerBtn);
        AddEmergencyContactBtn=findViewById(R.id.AddECBtn);
        yourRespBtn=findViewById(R.id.yourResp);
        LiveTracingBtn=findViewById(R.id.liveTrackBtn);

        setVoiceTriggerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SetVoiceTrigger.class));
            }
        });
        AddEmergencyContactBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),AddEmergencyContact.class));
            }
        });
        yourRespBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(),MainMenu.class));
            }
        });
        LiveTracingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getApplicationContext(),MainMenu.class));
            }
        });

    }
}