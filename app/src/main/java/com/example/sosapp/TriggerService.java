package com.example.sosapp;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class TriggerService extends Service implements SensorEventListener {
    public int counter=0;
    private SpeechRecognizer speechRecognizer;
    private Intent intentRecognizer;
    boolean close=false;

    SensorManager sensorManager;
    Sensor accelerometerSensor;
    boolean isSensorAvailable=false,isFirstTime=true;

    float curx,cury,curz,lastx,lasty,lastz,xdiff,ydiff,zdiff;
    float shakeThreshhold=15.0f;
    private Vibrator vibrator;

    DatabaseReference reff;
    FirebaseAuth fAuth;
    String TriggerPassword;

    MediaPlayer player;
    @Override
    public void onCreate() {
        super.onCreate();
        reff=FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                TriggerPassword=snapshot.child("Users").child(fAuth.getUid()).child("Trigger").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        intentRecognizer=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intentRecognizer.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) { }
            @Override
            public void onBeginningOfSpeech() {            }
            @Override
            public void onRmsChanged(float rmsdB) {
            }
            @Override
            public void onBufferReceived(byte[] buffer) {}
            @Override
            public void onEndOfSpeech() { }
            @Override
            public void onError(int error) {

            }
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches=results.getStringArrayList(speechRecognizer.RESULTS_RECOGNITION);
                if(matches.contains(TriggerPassword)){
                    PasswordMatch();
                    /*
                    Toast.makeText(TriggerService.this, "Correct password", Toast.LENGTH_SHORT).show();
                    player=MediaPlayer.create(TriggerService.this, Settings.System.DEFAULT_ALARM_ALERT_URI);
                    player.start();*/
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {

            }
            @Override
            public void onEvent(int eventType, Bundle params) {

            }
        });


        vibrator= (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        sensorManager=(SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if(sensorManager.getDefaultSensor((Sensor.TYPE_ACCELEROMETER))!=null){
            accelerometerSensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            isSensorAvailable=true;
        }
        else Toast.makeText(this, "Accelerometer Not Available", Toast.LENGTH_SHORT).show();


        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
    }
    void PasswordMatch() {
        startTracking();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent!=null && intent.getAction()!=null){
            if(intent.getAction().equals("STARTSERVICE")){
                if(isSensorAvailable){
                    sensorManager.registerListener(this,accelerometerSensor,SensorManager.SENSOR_DELAY_NORMAL);
                }
                close=false;
            }
            else if(intent.getAction().equals("STOPSERVICE")){
                if(isSensorAvailable){
                    sensorManager.unregisterListener(this);
                }
                //player.stop();
                speechRecognizer.stopListening();
                close=true;
                stopForeground(true);
                stopSelf();

            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speechRecognizer.stopListening();
        if(close==false){
            Intent broadcastIntent = new Intent();
            broadcastIntent.putExtra("whatService","Trigger");
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        super.onDestroy();
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSensorChanged(SensorEvent event) {
        curx=event.values[0];
        cury=event.values[1];
        curz=event.values[2];


        if(!isFirstTime){
            xdiff=Math.abs(lastx-curx);
            ydiff=Math.abs(lasty-cury);
            zdiff=Math.abs(lastz-curz);
            if( ( xdiff>shakeThreshhold  && ydiff>shakeThreshhold) ||
                    ( ydiff>shakeThreshhold  && zdiff>shakeThreshhold) ||
                    ( xdiff>shakeThreshhold  && zdiff>shakeThreshhold) )
            {
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
                    vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
                else
                    vibrator.vibrate(500);
                speechRecognizer.startListening(intentRecognizer);
            }
        }
        lastx=curx;
        lasty=cury;
        lastz=curz;
        isFirstTime=false;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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
