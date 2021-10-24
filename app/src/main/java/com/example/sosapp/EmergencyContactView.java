package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EmergencyContactView extends AppCompatActivity {
    DatabaseReference reff;
    FirebaseAuth fAuth;
    ListView EClist;
    ArrayAdapter<String> adapter;
    ArrayList<String> listName=new ArrayList<String>();
    Button bck,addec,removeec;
    Switch policeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_view);

        EClist=findViewById(R.id.EmergencyContactViewList);
        reff= FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,listName);
        EClist.setAdapter(adapter);
        bck=findViewById(R.id.BackToViewEC);
        addec=findViewById(R.id.AddECButton);
        removeec=findViewById(R.id.RemoveEC);
        policeSwitch=findViewById(R.id.switch1);

        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("EmergencyContacts").getChildren()){
                    String uid=i.getKey();
                    if(!uid.equals("AAA")){
                        String toDisp=snapshot.child("Users").child(uid).child("Name").getValue().toString();
                        listName.add(toDisp);
                        adapter.notifyDataSetChanged();
                        //Toast.makeText(EmergencyContactView.this,toDisp , Toast.LENGTH_SHORT).show();
                    }

                }
                String s=snapshot.child("Users").child(fAuth.getUid()).child("police_alert").getValue().toString();
                if(s.equals("false"))
                    policeSwitch.setChecked(false);
                else
                    policeSwitch.setChecked(true);



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        policeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(policeSwitch.isChecked()){
                    reff.child("Users").child(fAuth.getUid()).child("police_alert").setValue("true");
                }
                else
                    reff.child("Users").child(fAuth.getUid()).child("police_alert").setValue("false");
            }
        });

        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmergencyContactView.this,MainMenu.class));
            }
        });
        addec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit=new Intent(EmergencyContactView.this,EmergencyContactAdd.class);
                edit.putExtra("Edit","ADD");
                startActivity(edit);
            }
        });
        removeec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent edit=new Intent(EmergencyContactView.this,EmergencyContactAdd.class);
                edit.putExtra("Edit","REMOVE");
                startActivity(edit);
            }
        });

    }

}