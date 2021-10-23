package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainMenu extends AppCompatActivity {

    Button setVoiceTriggerBtn, AddEmergencyContactBtn,yourRespBtn,LiveTracingBtn;
    Button trackon,trackoff;
    DatabaseReference reff;
    FirebaseAuth fAuth;
    TextView TrackText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO,Manifest.permission.FOREGROUND_SERVICE},101);

        setVoiceTriggerBtn=findViewById(R.id.VoiceTriggerBtn);
        AddEmergencyContactBtn=findViewById(R.id.AddECBtn);
        yourRespBtn=findViewById(R.id.yourResp);
        LiveTracingBtn=findViewById(R.id.liveTrackBtn);
        trackon=findViewById(R.id.trackON);
        trackoff=findViewById(R.id.trackOFF);
        TrackText=findViewById(R.id.TrackText);
        reff= FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();


        //Track Text visibility condition.
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String ct=snapshot.child("Users").child(fAuth.getUid()).child("CanTrackMe").getValue().toString();
                if(ct.equals("true"))
                    TrackText.setVisibility(View.VISIBLE);
                else
                    TrackText.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //4 Menu Option Buttons OnClick
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
                startActivity(new Intent(getApplicationContext(),YourRespView.class));
            }
        });
        LiveTracingBtn.setOnClickListener(new View.OnClickListener() {
            boolean trouble=false;
            String who="";
            @Override
            public void onClick(View v) {

                reff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("YourResp").getChildren()){
                            String val= (String) i.getValue();
                            if(val.equals("true")){
                                boolean trouble=true;
                                Intent liveScreen=new Intent(getApplicationContext(),MapsActivity.class);
                                liveScreen.putExtra("uid",i.getKey().toString());
                                startActivity(liveScreen);
                            }
                        }
                        if(trouble==false){
                            Toast.makeText(MainMenu.this, "No one in Trouble", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


            }
        });


        //these two on click listeners call startTracking and stopTracking Respectively
        trackon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startTracking();
            }
        });
        trackoff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                stopTracking();
            }
        });

        startTriggerService();

    }
    //Trigger Service: Starter and Stopper
    //Trigger Service Listens For Mobile Shake and initiates Voice input
    public void startTriggerService() {

        Intent serviceIntent = new Intent(this, TriggerService.class);
        serviceIntent.setAction("STARTSERVICE");
        if(!isMyServiceRunning(TriggerService.class)){
            startService(serviceIntent);
        }
        else
            Toast.makeText(this, "already running", Toast.LENGTH_SHORT).show();
    }
    public void stopTriggerService() {
        if(isMyServiceRunning(TriggerService.class)){
            Intent serviceIntent = new Intent(this, TriggerService.class);
            serviceIntent.setAction("STOPSERVICE");
            startService(serviceIntent);
        }

    }


    //startTracking and stopTracking :
    // updates the CanTrackMe field in database and calls respective Tracking Services
    public void startTracking(){
        reff.child("Users").child(fAuth.getUid()).child("CanTrackMe").setValue("true");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("EmergencyContacts").getChildren()){
                    String ec=i.getKey();
                    if(!ec.equals("AAA"))
                    reff.child("Users").child(ec).child("YourResp").child(fAuth.getUid()).setValue("true");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        startTrackingService();
    }
    public void stopTracking(){
        reff.child("Users").child(fAuth.getUid()).child("CanTrackMe").setValue("false");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("EmergencyContacts").getChildren()){
                    String ec=i.getKey();
                    if(!ec.equals("AAA"))
                    reff.child("Users").child(ec).child("YourResp").child(fAuth.getUid()).setValue("false");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        stopTrackingService();
    }


    //Tracking Service: Starter and Stopper
    //Tracking Service Starts/Stops Tracking the live location of the user in distress
    public void startTrackingService() {

        Intent serviceIntent = new Intent(this, TrackingService.class);
        serviceIntent.setAction("STARTSERVICE");
        if(!isMyServiceRunning(TrackingService.class)){
            startService(serviceIntent);
        }

        else
            Toast.makeText(this, "already running", Toast.LENGTH_SHORT).show();
    }
    public void stopTrackingService() {
        if(isMyServiceRunning(TrackingService.class)){
            Intent serviceIntent = new Intent(this, TrackingService.class);
            serviceIntent.setAction("STOPSERVICE");
            startService(serviceIntent);
        }

    }

    //Function to check whether a service is running or not
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}