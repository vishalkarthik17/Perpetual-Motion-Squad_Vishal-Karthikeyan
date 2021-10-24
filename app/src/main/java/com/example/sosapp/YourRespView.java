package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class YourRespView extends AppCompatActivity {
    DatabaseReference reff;
    FirebaseAuth fAuth;
    ListView respList;
    ArrayAdapter<String>adapter;
    ArrayList<String> listName=new ArrayList<String>();
    Button bck,edit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_resp_view);
        respList=findViewById(R.id.EmergencyContactViewList);
        reff= FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,listName);
        respList.setAdapter(adapter);
        bck=findViewById(R.id.BackToViewEC);
        edit=findViewById(R.id.AddECButton);
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("YourResp").getChildren()){
                    String uid=i.getKey();
                    if(!uid.equals("AAA")){
                        String toDisp=snapshot.child("Users").child(uid).child("Name").getValue().toString();
                        listName.add(toDisp);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(YourRespView.this,toDisp , Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YourRespView.this,MainMenu.class));
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YourRespView.this,YourRespEdit.class));
            }
        });


    }
}