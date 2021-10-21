package com.example.sosapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class Restarter extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle b=intent.getExtras();
        if(b!=null && b.get("whatService").toString().equals("LocationTrack")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent ii=new Intent(context, TrackingService.class);
                ii.setAction("STARTSERVICE");
                context.startForegroundService(ii);
            } else {
                Intent ii=new Intent(context, TrackingService.class);
                ii.setAction("STARTSERVICE");
                context.startService(ii);
            }
        }
        if(b!=null && b.get("whatService").toString().equals("Trigger")){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Intent ii=new Intent(context, TriggerService.class);
                ii.setAction("STARTSERVICE");
                context.startForegroundService(ii);
            } else {
                Intent ii=new Intent(context, TriggerService.class);
                ii.setAction("STARTSERVICE");
                context.startService(ii);
            }
        }
    }
}
