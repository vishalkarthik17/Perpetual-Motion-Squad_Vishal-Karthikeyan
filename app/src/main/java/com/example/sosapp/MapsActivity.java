package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference reff;
    private LocationManager manager;
    boolean first=true;
    ProgressBar pbMap;
    TextView LocText,titleText;
    FirebaseAuth fAuth;
    Button backBtn;

    private final int MIN_TIME = 1000;
    private final int MIN_DIST = 1;
    int canTrack=0;
    String who;
    // private ActivityMapsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        who="";
        Intent fromMain=getIntent();
        Bundle b=fromMain.getExtras();
        if(b!=null){
            who=b.get("uid").toString();
        }

        pbMap=findViewById(R.id.progressBarMap1);
        LocText=findViewById(R.id.LocationText);
        fAuth=FirebaseAuth.getInstance();
        backBtn=findViewById(R.id.BackBtnMap1);
        titleText=findViewById(R.id.MapTitleText1);

        reff = FirebaseDatabase.getInstance().getReference();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map1);
        mapFragment.getMapAsync(this);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainMenu.class));
            }
        });

        reff.child("Users").child(who).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double lat=Double.parseDouble(snapshot.child("location").child("latitude").getValue().toString());
                double longi=Double.parseDouble(snapshot.child("location").child("longitude").getValue().toString());
                String str= String.valueOf(lat)+" / "+String.valueOf(longi)+" ";
                //Toast.makeText(MapsActivity.this, str, Toast.LENGTH_SHORT).show();
                LatLng curpos=new LatLng(lat,longi);
                if(lat!=0 && longi!=0){
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                    Date date = new Date();
                    String abc=(formatter.format(date))+" ";
                    Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(lat, longi, 1);
                        abc=abc+addresses.get(0).getSubLocality()+", "+addresses.get(0).getLocality();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mMap.addMarker(new MarkerOptions().position(curpos).title(abc));
                    LocText.setText("Location :"+abc.substring(19, abc.length()));
                    if(first==true){
                        pbMap.setVisibility(View.INVISIBLE);
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curpos, 15));
                        first=false;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        reff.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                titleText.setText(snapshot.child("Users").child(who).child("Name").getValue().toString()+" is in Trouble!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reff.child("Users").child(who).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child("CanTrackMe").getValue().toString().equals("true")){
                    double lat=Double.parseDouble(snapshot.child("location").child("latitude").getValue().toString());
                    double longi=Double.parseDouble(snapshot.child("location").child("longitude").getValue().toString());
                    String str= String.valueOf(lat)+" / "+String.valueOf(longi)+" ";
                   // Toast.makeText(MapsActivity.this, str, Toast.LENGTH_SHORT).show();
                    LatLng curpos=new LatLng(lat,longi);
                    if(lat!=0 && longi!=0){
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        Date date = new Date();
                        String abc=(formatter.format(date))+" ";
                        Geocoder geocoder = new Geocoder(MapsActivity.this, Locale.getDefault());
                        try {
                            List<Address> addresses = geocoder.getFromLocation(lat, longi, 1);
                            abc=abc+addresses.get(0).getSubLocality()+", "+addresses.get(0).getLocality();

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mMap.addMarker(new MarkerOptions().position(curpos).title(abc));
                        LocText.setText("Location :"+abc.substring(19, abc.length()));
                        if(first==true){
                            pbMap.setVisibility(View.INVISIBLE);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curpos, 15));
                            first=false;
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