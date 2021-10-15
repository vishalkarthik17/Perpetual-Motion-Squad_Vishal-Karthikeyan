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

public class MainActivity extends AppCompatActivity {

    Button toRegBtn,LoginBtn;
    EditText email,pw;
    ProgressBar pb;
    FirebaseDatabase database;
    DatabaseReference myRef;
    FirebaseAuth fAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toRegBtn=findViewById(R.id.LoginToRegisterBtn);
        email=findViewById(R.id.RegisterEmail);
        pw=findViewById(R.id.RegisterPassword);
        pb=findViewById(R.id.progressBarLogin);
        LoginBtn=findViewById(R.id.LoginBtn);

        database= FirebaseDatabase.getInstance();
        myRef=database.getReference();
        fAuth= FirebaseAuth.getInstance();

        toRegBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),Registration.class));
            }
        });

        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String em=email.getText().toString().trim();
                String pass=pw.getText().toString().trim();
                if(TextUtils.isEmpty(em)){
                    email.setError("Email is Required");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    pw.setError("Password is Required");
                    return;
                }
                if(pass.length()<6){
                    pw.setError("Password Must be >= 6 Characters");
                    return;
                }
                pb.setVisibility(View.VISIBLE);

                fAuth.signInWithEmailAndPassword(em,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"Login Sucessfull",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(),MainMenu.class));
                        }
                        else{
                            Toast.makeText(MainActivity.this,"Error "+task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            pb.setVisibility(View.INVISIBLE);
                        }
                    }
                });
            }
        });

    }
}