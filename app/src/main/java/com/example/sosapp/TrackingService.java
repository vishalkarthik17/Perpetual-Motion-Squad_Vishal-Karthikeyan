package com.example.sosapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class TrackingService extends Service implements LocationListener {
    private LocationManager manager;
    DatabaseReference reff,reff2;
    FirebaseAuth fAuth;
    boolean close=false;

    @Override
    public void onCreate() {
        super.onCreate();
        fAuth=FirebaseAuth.getInstance();
        reff= FirebaseDatabase.getInstance().getReference().child("Users").child(fAuth.getUid());
        reff2=FirebaseDatabase.getInstance().getReference();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startMyOwnForeground();
        else
            startForeground(1, new Notification());
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

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return START_NOT_STICKY;
        }
        if(intent.getAction().equals("STARTSERVICE")){
            manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, this);
            close=false;
        }
        else if(intent.getAction().equals("STOPSERVICE")){
            close=true;
            reff2.child("Trouble").child(fAuth.getUid()).removeValue();
            stopForeground(true);
            stopSelf();
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(close==false){
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("restartservice");
            broadcastIntent.setClass(this, Restarter.class);
            this.sendBroadcast(broadcastIntent);
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        if(!close){
            String station="none";
            station = calculatePoliceStation(location.getLatitude(),location.getLongitude());
            if(!station.equals("none"))
            reff2.child("Trouble").child(fAuth.getUid()).setValue(station);
           // Toast.makeText(TrackingService.this, "Loc", Toast.LENGTH_SHORT).show();
            reff.child("location").setValue(location);
        }

    }
    String calculatePoliceStation(Double i,Double j){
        Double minDist=Double.MAX_VALUE;
        String nearestPoliceStation="none";
        HashMap <String, ArrayList<Double>> ps=MySingletonClass.getInstance().PoliceStation;
        for( String s : ps.keySet()){
            ArrayList<Double> c=ps.get(s);
            Double dis=Math.sqrt(  ( (i-c.get(0))*(i-c.get(0)) )+((j-c.get(1))*(j-c.get(1)))  );
            if(minDist>dis){
                minDist=dis;
                nearestPoliceStation=s;
            }
        }
        return nearestPoliceStation;
    }
}
