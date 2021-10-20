package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

        trackon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reff.child("Users").child(fAuth.getUid()).child("CanTrackMe").setValue("true");
                reff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("EmergencyContacts").getChildren()){
                            String ec=i.getKey();
                            reff.child("Users").child(ec).child("YourResp").child(fAuth.getUid()).setValue("true");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                startService(new Intent(MainMenu.this,TrackingService.class));
            }
        });

        trackoff.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                reff.child("Users").child(fAuth.getUid()).child("CanTrackMe").setValue("false");
                reff.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("EmergencyContacts").getChildren()){
                            String ec=i.getKey();
                            reff.child("Users").child(ec).child("YourResp").child(fAuth.getUid()).setValue("false");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                stopService(new Intent(MainMenu.this,TrackingService.class));
            }
        });

    }
}