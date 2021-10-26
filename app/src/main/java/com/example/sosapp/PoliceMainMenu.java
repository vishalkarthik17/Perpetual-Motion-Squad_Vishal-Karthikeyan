package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PoliceMainMenu extends AppCompatActivity {
    DatabaseReference reff;
    FirebaseAuth fAuth;
    ListView troubleList;
    ArrayList<String> name;
    ArrayAdapter<String> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_police_main_menu);

        reff= FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();
        troubleList=findViewById(R.id.troubleList);
        name=new ArrayList<>();
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line,name);
        troubleList.setAdapter(adapter);

        MySingletonClass.getInstance().setValuesPhoneUIDHashMap();
        MySingletonClass.getInstance().setValuesEmergencyContactList();
        MySingletonClass.getInstance().setValuesNameUIDHashMap();
        MySingletonClass.getInstance().setPoliceStationCoordinates();

        refresh();
        troubleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item=adapter.getItem(position);
                Intent ii=new Intent(PoliceMainMenu.this,PoliceMapsActivity.class);
                ii.putExtra("who",item);
                startActivity(ii);
            }
        });
    }

    private void refresh() {

        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.clear();
                String mystation=snapshot.child("PoliceStations").child(fAuth.getUid()).child("Code").getValue().toString();
                for(DataSnapshot i : snapshot.child("Trouble").getChildren()){
                    String id=i.getKey();
                    if(!id.equals("AAA")){
                        String station=i.getValue().toString();
                        //Toast.makeText(PoliceMainMenu.this, station+" "+mystation, Toast.LENGTH_SHORT).show();
                        if(snapshot.child("Users").child(id).child("police_alert").getValue().equals("true")
                                && station.trim().equals(mystation.trim()) ){
                            name.add(snapshot.child("Users").child(id).child("Name").getValue().toString());
                            adapter.notifyDataSetChanged();
                        }
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}