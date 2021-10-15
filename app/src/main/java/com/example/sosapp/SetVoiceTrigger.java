package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class SetVoiceTrigger extends AppCompatActivity {
    Button SpeakBtn;
    TextView CurrentTriggerText;
    FirebaseAuth fAuth;
    DatabaseReference reff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_voice_trigger);

        fAuth=FirebaseAuth.getInstance();
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child(fAuth.getUid());

        SpeakBtn=findViewById(R.id.ChangeTriggerBtn);
        CurrentTriggerText=findViewById(R.id.CurrentTriggerTextView);
        SetTriggerTextFromDatabase();
        SpeakBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speak();
            }
        });
    }
    public void SetTriggerTextFromDatabase(){
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String curText=snapshot.child("Trigger").getValue().toString();
                CurrentTriggerText.setText(curText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void speak(){
        Intent intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en-US");
        startActivityForResult(intent,10);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==10 && resultCode==RESULT_OK){
            String newTrigger=data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0);
            reff.child("Trigger").setValue(newTrigger);
            SetTriggerTextFromDatabase();

        }
    }
}