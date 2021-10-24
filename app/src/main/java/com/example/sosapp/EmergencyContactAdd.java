package com.example.sosapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.IInterface;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EmergencyContactAdd extends AppCompatActivity {
    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    //MyCustomAdapter dataAdapter ;
    ArrayAdapter dataAdapter;
    ListView listView;
    DatabaseReference reff;
    FirebaseAuth fAuth;
    ArrayList<String> DatabasePN;
    ArrayList<String>Names;
    HashMap<String,String>Name_PhoneNum;
    boolean isAdd=false;
    TextView ecEditTitle;
    Button bck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency_contact_add);
        listView = (ListView) findViewById(R.id.ECList1);
        ecEditTitle=findViewById(R.id.ECEditTitle);
        reff= FirebaseDatabase.getInstance().getReference();
        fAuth=FirebaseAuth.getInstance();


        Names=new ArrayList<>();
        dataAdapter=new ArrayAdapter(EmergencyContactAdd.this,android.R.layout.simple_list_item_multiple_choice,Names);
        listView.setAdapter(dataAdapter);
        dataAdapter.notifyDataSetChanged();
        bck=findViewById(R.id.BackToViewEC);

        DatabasePN=new ArrayList<>(MySingletonClass.getInstance().PhonexUID_DB.keySet());
        Name_PhoneNum=new HashMap<>();

        Bundle b=getIntent().getExtras();
        if(b.getString("Edit")!=null && b.getString("Edit").equals("ADD"))
        {
            ecEditTitle.setText("Select to Add Emergency Contact");
            isAdd=true;
            requestContactPermission();
        }
        else if (b.getString("Edit")!=null && b.getString("Edit").equals("REMOVE")){
            ecEditTitle.setText("Select to Remove Emergency Contact");
            isAdd=false;
            Toast.makeText(this, "remove", Toast.LENGTH_SHORT).show();
            removeOption();
        }

        bck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EmergencyContactAdd.this,EmergencyContactView.class));
            }
        });

    }

    private void removeOption() {
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot i : snapshot.child("Users").child(fAuth.getUid()).child("EmergencyContacts").getChildren()){
                    String uid=i.getKey();
                    if(!uid.equals("AAA")){
                        String toDisp=snapshot.child("Users").child(uid).child("Name").getValue().toString();
                        Names.add(toDisp);
                        dataAdapter.notifyDataSetChanged();
                        //Toast.makeText(EmergencyContactView.this,toDisp , Toast.LENGTH_SHORT).show();
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @SuppressLint("Range")
    private void getContacts(){
        ContentResolver contentResolver = getContentResolver();
        String contactId = null;
        String displayName = null;
        String phoneNumber = null;
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") int hasPhoneNumber = Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)));
                if (hasPhoneNumber > 0) {

                    ContactsInfo contactsInfo = new ContactsInfo();
                    contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                    displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                    contactsInfo.setContactId(contactId);
                    contactsInfo.setDisplayName(displayName);

                    Cursor phoneCursor = getContentResolver().query(
                            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null,
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{contactId},
                            null);

                    if (phoneCursor.moveToNext()) {
                        phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        phoneNumber = phoneNumber.replaceAll("\\s", "");
                        if(phoneNumber.charAt(0)=='+')
                            phoneNumber=phoneNumber.substring(3);
                        contactsInfo.setPhoneNumber(phoneNumber);
                    }

                    phoneCursor.close();

                    //contactsInfoList.add(toDisp);
                    if(DatabasePN.contains(phoneNumber) && !MySingletonClass.getInstance().currentEmergencyContacts.contains(phoneNumber)){
                        Names.add(displayName);
                        Name_PhoneNum.put(displayName,phoneNumber);
                        dataAdapter.notifyDataSetChanged();
                    }

                }
            }
        }
        cursor.close();


    }

    public void requestContactPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        android.Manifest.permission.READ_CONTACTS)) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Read contacts access needed");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setMessage("Please enable access to contacts.");
                    builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @TargetApi(Build.VERSION_CODES.M)
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            requestPermissions(
                                    new String[]
                                            {android.Manifest.permission.READ_CONTACTS}
                                    , PERMISSIONS_REQUEST_READ_CONTACTS);
                        }
                    });
                    builder.show();
                } else {
                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.READ_CONTACTS},
                            PERMISSIONS_REQUEST_READ_CONTACTS);
                }
            } else {
                getContacts();
            }
        } else {
            getContacts();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CONTACTS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContacts();
                } else {
                    Toast.makeText(this, "You have disabled a contacts permission", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.checkbox1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {

        if(isAdd==true){
            ArrayList<String> selPhno=new ArrayList<>();
            ArrayList<String> Uid=new ArrayList<>();
            int id=item.getItemId();
            if(id==R.id.item_done1){
                for(int i=0;i<listView.getCount();i++){
                    if(listView.isItemChecked(i)){
                        String itemSelected=listView.getItemAtPosition(i).toString();
                        selPhno.add(Name_PhoneNum.get(itemSelected));
                    }
                }

            }
            for(int i=0;i<selPhno.size();i++){
                Uid.add(MySingletonClass.getInstance().PhonexUID_DB.get(selPhno.get(i)));
                reff.child("Users").child(fAuth.getUid()).child("EmergencyContacts").child(Uid.get(i)).setValue("true");
                reff.child("Users").child(Uid.get(i)).child("YourResp").child(fAuth.getUid()).setValue("false");
            }
            Toast.makeText(this, "Selecteddd", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this,EmergencyContactView.class));
        }
        else{
            ArrayList<String>selNames=new ArrayList<>();
            ArrayList<String>uid=new ArrayList<>();
            String ss="";
            int id=item.getItemId();
            if(id==R.id.item_done1){
                for(int i=0;i<listView.getCount();i++){
                    if(listView.isItemChecked(i)){
                        String itemSelected=listView.getItemAtPosition(i).toString();
                        selNames.add(itemSelected);
                        uid.add(MySingletonClass.getInstance().NamexUID_DB.get(itemSelected));
                        ss+=itemSelected+" ";
                    }
                }
            }
            for(int i=0;i<uid.size();i++){
                reff.child("Users").child(fAuth.getUid()).child("EmergencyContacts").child(uid.get(i)).removeValue();
                reff.child("Users").child(uid.get(i)).child("YourResp").child(fAuth.getUid()).removeValue();
            }
            Toast.makeText(this,ss,Toast.LENGTH_SHORT).show();
            MySingletonClass.getInstance().setValuesEmergencyContactList();
            startActivity(new Intent(this,EmergencyContactView.class));
        }



       return super.onOptionsItemSelected(item);

    }
}