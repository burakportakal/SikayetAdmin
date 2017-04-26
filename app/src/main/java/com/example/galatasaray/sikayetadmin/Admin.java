package com.example.galatasaray.sikayetadmin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import static com.example.galatasaray.sikayetadmin.MainActivity.firebaseDatabase;
import static com.example.galatasaray.sikayetadmin.MainActivity.mAuth;

public class Admin extends AppCompatActivity {
    Button btnApart;
    Button btnSikayet;
    ListView listView;
    Boolean flag=true;
    FirebaseUser user=mAuth.getCurrentUser();
    ArrayList<Bildirim> bildirimler=new ArrayList<Bildirim>();
    ArrayList<DatabaseReference> list1 = new ArrayList<>();
    DatabaseReference reference;
    ValueEventListener listener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        final String[] values = getIntent().getStringArrayExtra("apartlar");
        btnApart =(Button)findViewById(R.id.btnApart);
        btnSikayet =(Button)findViewById(R.id.btnSikayet);
        listView =(ListView)findViewById(R.id.listApart);
        btnApart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent  intent = new Intent(Admin.this,ApartEkle.class);
                startActivity(intent);
            }
        });

        btnSikayet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(Admin.this,SikayetGoruntule.class);
                startActivity(intent);
            }
        });
        final ArrayList<String> list = new ArrayList<String>();
        final ArrayList<String> listTarih = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Apart silme olayı
            }
        });

        if(values!=null && values.length>0) {
                 reference= firebaseDatabase.getInstance().getReference("Bildirimler/"+user.getUid());

                reference.addValueEventListener(listener = new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Bildirim bildirim = new Bildirim(snapshot.child("Name").getValue().toString(), snapshot.child("DaireNo").getValue().toString()
                                    , Boolean.valueOf(snapshot.child("Okundu").getValue().toString()));
                            if (!bildirim.Okundu) {
                                bildirimler.add(bildirim);
                                DatabaseReference tempReference = firebaseDatabase.getInstance().getReference("Bildirimler/" + user.getUid() + "/" + snapshot.getKey());
                                list1.add(tempReference);
                            }
                        }
                        if(bildirimler.size()>0)
                        addNotification(list1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

               /* reference.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Bildirim bildirim=new Bildirim(dataSnapshot.child("Name").getValue().toString(),dataSnapshot.child("DaireNo").getValue().toString()
                                ,Boolean.valueOf(dataSnapshot.child("Okundu").getValue().toString()));
                        if(!bildirim.Okundu){
                        bildirimler.add(bildirim);
                        ArrayList<DatabaseReference> list1=new ArrayList<>();
                        DatabaseReference reference= firebaseDatabase.getInstance().getReference("Bildirimler/"+user.getUid()+"/"+dataSnapshot.getKey());
                        list1.add(reference);
                        addNotification(list1);
                        }
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });*/


        }

        }


    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
    public void onBackPressed() {
        mAuth.signOut();
        finish();
    }
    private void addNotification(ArrayList<DatabaseReference> keyList) {
        reference.removeEventListener(listener);
        NotificationCompat.Builder builder =
                (NotificationCompat.Builder) new NotificationCompat.Builder(Admin.this)
                        .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                        .setContentTitle("Şikayetler")
                        .setContentText(bildirimler.size()+" yeni şikayetiniz var");

        Intent notificationIntent = new Intent(this, SikayetGoruntule.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);
        builder.setAutoCancel(true);
        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
        bildirimler.clear();
        for(int i=0;i<keyList.size();i++) {
            DatabaseReference ref = ((DatabaseReference) keyList.get(i));
            ref.child("Okundu").setValue("True");
        }
        reference.addValueEventListener(listener);


    }
    private void createPopup(final String sikayedId, final String userId, String isim)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        DatabaseReference mRef =firebaseDatabase.getReference("Sikayet/"+userId+"/Sikayetler/"+sikayedId);
                        mRef.child("Yapildi").setValue("True");

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(Admin.this);
        builder.setMessage("Yapildı olarak işaretle?").setPositiveButton("Evet", dialogClickListener)
                .setNegativeButton("Hayır", dialogClickListener).show();
    }
}
