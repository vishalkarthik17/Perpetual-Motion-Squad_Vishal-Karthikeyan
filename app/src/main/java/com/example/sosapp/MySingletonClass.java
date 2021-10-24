package com.example.sosapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.sosapp.EmergencyContactAdd.PERMISSIONS_REQUEST_READ_CONTACTS;

public class MySingletonClass extends AppCompatActivity {
    private static MySingletonClass instance;
    DatabaseReference reff= FirebaseDatabase.getInstance().getReference();
    FirebaseAuth fAuth=FirebaseAuth.getInstance();


    public HashMap<String,String>PhonexUID_DB=new HashMap<>();
    //PhoneNumber Registered --> Uid Mapping


    public static MySingletonClass getInstance() {
        if (instance == null)
            instance = new MySingletonClass();
        return instance;
    }

    private MySingletonClass() {

    }

    public void setValuePhoneUIDHashMap() {

        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.child("Phone_UID").getChildren()){
                    String ss=i.getKey().toString();
                    String sss=i.getValue().toString();
                    if(ss!=null && sss!=null && !ss.equals("AAA"))
                    PhonexUID_DB.put(ss,sss);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
