package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

public class YourRespEdit extends AppCompatActivity {
    DatabaseReference reff;
    FirebaseAuth fAuth;
    ListView respList;
    ArrayAdapter<String> adapter;
    ArrayList<String> listName=new ArrayList<String>();
    Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_resp_edit);
        respList=findViewById(R.id.RespList1);
        reff= FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();
        adapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_multiple_choice,listName);
        respList.setAdapter(adapter);
        back=findViewById(R.id.backToYourResp);
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("YourResp").getChildren()){
                    String uid=i.getKey();
                    if(!uid.equals("AAA")){
                        String toDisp=snapshot.child("Users").child(uid).child("Name").getValue().toString();
                        listName.add(toDisp);
                        adapter.notifyDataSetChanged();
                        Toast.makeText(YourRespEdit.this,toDisp , Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(YourRespEdit.this,YourRespView.class));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkbox,menu);
        return true;
        //return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id=item.getItemId();
        if(id==R.id.item_done){
            String itemSelected="Selected : ";
            for(int i=0;i<respList.getCount();i++){
                if(respList.isItemChecked(i)){
                    itemSelected+=respList.getItemAtPosition(i)+" \n ";
                }
            }
            Toast.makeText(this, itemSelected, Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);

    }
}