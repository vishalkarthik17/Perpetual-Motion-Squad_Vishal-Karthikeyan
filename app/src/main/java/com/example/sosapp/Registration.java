package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Registration extends AppCompatActivity {

    EditText fname,Remail,Rpw,phno;
    Button regBtn;
    ProgressBar Rpb;
    FirebaseAuth fAuth;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        fname=findViewById(R.id.RegisterFullName);
        Remail=findViewById(R.id.RegisterEmail);
        Rpw=findViewById(R.id.RegisterPassword);
        regBtn=findViewById(R.id.LoginBtn);
        Rpb=findViewById(R.id.progressBarReg);
        phno=findViewById(R.id.PhoneText);

        fAuth=FirebaseAuth.getInstance();
        reff= FirebaseDatabase.getInstance().getReference();


        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em=Remail.getText().toString().trim();
                String pass=Rpw.getText().toString().trim();
                String namee=fname.getText().toString().trim();
                String phNum=phno.getText().toString().trim();
                if(TextUtils.isEmpty(em)){
                    Remail.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    Rpw.setError("Password is Required");
                    return;
                }
                if(pass.length()<6){
                    Rpw.setError("Password Must be >= 6 Characters");
                    return;
                }
                if(phNum.length()<10){
                    phno.setError("Invalid Phone Number");
                    return;
                }
                Rpb.setVisibility(View.VISIBLE);

                fAuth.createUserWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            reff.child("Users").child(fAuth.getUid()).child("Name").setValue(namee);
                            reff.child("Users").child(fAuth.getUid()).child("Email").setValue(em);
                            reff.child("Users").child(fAuth.getUid()).child("Trigger").setValue("help me");
                            reff.child("Users").child(fAuth.getUid()).child("Phone_Number").setValue(phNum);
                            reff.child("Phone_UID").child(phNum).setValue(fAuth.getUid());
                            reff.child("Name_UID").child(namee).setValue(fAuth.getUid());
                            reff.child("Users").child(fAuth.getUid()).child("CanTrackMe").setValue("false");
                            reff.child("Users").child(fAuth.getUid()).child("EmergencyContacts").child("AAA").setValue("AAA");
                            reff.child("Users").child(fAuth.getUid()).child("YourResp").child("AAA").setValue("AAA");
                            reff.child("Users").child(fAuth.getUid()).child("location").child("AAA").setValue("AAA");
                            Toast.makeText(Registration.this,"Registered",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }
                        else{
                            Toast.makeText(Registration.this,"Error "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            Rpb.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });


    }
}