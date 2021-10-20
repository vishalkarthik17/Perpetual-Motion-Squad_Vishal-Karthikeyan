package com.example.sosapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainMenu extends AppCompatActivity {

    Button setVoiceTriggerBtn, AddEmergencyContactBtn,yourRespBtn,LiveTracingBtn;
    Button trackon,trackoff;
    DatabaseReference reff;
    FirebaseAuth fAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        setVoiceTriggerBtn=findViewById(R.id.VoiceTriggerBtn);
        AddEmergencyContactBtn=findViewById(R.id.AddECBtn);
        yourRespBtn=findViewById(R.id.yourResp);
        LiveTracingBtn=findViewById(R.id.liveTrackBtn);
        trackon=findViewById(R.id.trackON);
        trackoff=findViewById(R.id.trackOFF);
        reff= FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();


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
                //startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            }
        });

        trackon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reff.child("Users").child(fAuth.getUid()).child("CanTrackMe").setValue("true");
                startService(new Intent(MainMenu.this,TrackingService.class));
            }
        });

        trackoff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reff.child("Users").child(fAuth.getUid()).child("CanTrackMe").setValue("false");
                stopService(new Intent(MainMenu.this,TrackingService.class));
            }
        });

    }
}