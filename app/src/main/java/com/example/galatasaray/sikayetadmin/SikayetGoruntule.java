package com.example.galatasaray.sikayetadmin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.provider.ContactsContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.text.Html;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.example.galatasaray.sikayetadmin.MainActivity.apartlar;
import static com.example.galatasaray.sikayetadmin.MainActivity.firebaseDatabase;
import static com.example.galatasaray.sikayetadmin.MainActivity.mAuth;

public class SikayetGoruntule extends AppCompatActivity {
    int dataSize=0 ;
    ArrayList<SikayetData> sikayetSayisi = new ArrayList<>();
    ArrayList<Sikayetler> sikayetlers = new ArrayList<Sikayetler>();
    final ArrayList<SikayetSirali> sirali = new ArrayList<SikayetSirali>();
    ListView listSikayet;
    int DataSize=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sikayet_goruntule);
        listSikayet=(ListView)findViewById(R.id.listSikayet);
        FirebaseUser user = mAuth.getCurrentUser();
        sikayetlers.clear();
        getDataFromDatabase(user);

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

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if(sirali.get(position).Yapildi) {
                 view = super.getView(position, convertView, parent);
                 view.setBackgroundColor(Color.CYAN);
            }
            else
            {
                 view = super.getView(position, convertView, parent);
                 view.setBackgroundColor(Color.RED);
                 view.setDrawingCacheBackgroundColor(Color.YELLOW);
            }
            return view;
        }

    }
    private void getDataFromDatabase(FirebaseUser user)
    {
        if(user != null) {
            String uId = user.getUid();
            final DatabaseReference userSikayetler = firebaseDatabase.getReference("Sikayet");
            userSikayetler.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    sikayetlers.clear();

                    String[] data = new String[(int) dataSnapshot.getChildrenCount()];
                    int counter=0;
                    for (Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator(); i.hasNext(); ) {

                        DataSnapshot snap=i.next();
                        for(int j =0 ;j<apartlar.ApartAdi.length;j++)
                        {
                            //Sadece kendi apartlarının kullanıcılarının şikayetlerini getir
                           String a= snap.child("Apart Adi").getValue().toString();
                            if(apartlar.ApartAdi[j].equals(a))
                            {
                                //Kullanıcı bilgilerini al
                                Sikayetler sikayet=new Sikayetler();
                                sikayet.UserId=snap.getKey();
                                sikayet.Isim=snap.child("Name").getValue().toString();
                                sikayet.DaireNo=snap.child("Daire").getValue().toString();
                                sikayet.ApartAdi=snap.child("Apart Adi").getValue().toString();
                                sikayet.Telefon=snap.child("Telefon").getValue().toString();
                                SikayetData[] sikayetleri = new SikayetData[(int)snap.child("Sikayetler").getChildrenCount()] ;
                                int counter2=0;
                                //Kullanıcının şikayetlerini şikayet objesi altında sakla
                                for(Iterator<DataSnapshot> dataSikayet=snap.child("Sikayetler").getChildren().iterator();dataSikayet.hasNext();)
                                {
                                    DataSnapshot snapshot=dataSikayet.next();
                                    SikayetData tempData=new SikayetData(snapshot.child("Sikayet").getValue().toString(),snapshot.child("Tarih").getValue().toString()
                                    ,snapshot.getKey().toString(),Boolean.valueOf(snapshot.child("Yapildi").getValue().toString()));
                                    sikayetleri[counter2]=tempData;
                                    sikayetSayisi.add(tempData);
                                    counter2++;
                                }
                                sikayet.Sikayetleri=sikayetleri;
                                sikayetlers.add(sikayet);
                            }
                        }
                        /*data[counter]=snap.getValue().toString();
                        counter++;*/
                    }
                    //Şikayetleri listele
                    listele();
                    //adminPage();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });

        }
    }
    /*
    Şikayetleri tarihe göre sıralı olarak listeler.
     */
    private void listele()
    {
        SikayetSirali sikayet;
        final ArrayList<String> list = new ArrayList<String>();
        sirali.clear();
        for (int i = sikayetlers.size()-1; i >=0; i--) {
            for(int j=sikayetlers.get(i).Sikayetleri.length-1;j>=0;j--) {
                 sikayet= new SikayetSirali(sikayetlers.get(i).UserId,sikayetlers.get(i).Isim,sikayetlers.get(i).Sikayetleri[j].Tarih,sikayetlers.get(i).Sikayetleri[j].Sikayet,
                        sikayetlers.get(i).DaireNo,sikayetlers.get(i).ApartAdi,sikayetlers.get(i).Telefon,sikayetlers.get(i).Sikayetleri[j].SikayetId,sikayetlers.get(i).Sikayetleri[j].Yapildi);
                 sirali.add(sikayet);
                //list.add("Sikayet eden:"+sikayetlers.get(i).Isim+"\nŞikayeti: "+sikayetlers.get(i).Sikayetleri[j].Sikayet+"\nTarih: "+sikayetlers.get(i).Sikayetleri[j].Tarih);
            }
        }
        Collections.sort(sirali);
        list.clear();
        for(SikayetSirali sikayetTemp : sirali)
        {
            list.add(sikayetTemp.toString());
        }
        final SikayetGoruntule.StableArrayAdapter adapter = new SikayetGoruntule.StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);

        listSikayet.setAdapter(adapter);
        //Şikayetin üzerine tıklandığında yapıldı olarak işaretlensin mi? diye sorar.
        listSikayet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String sikayet1 = sirali.get((int)id).Sikayet;
                String sikayetId="";
                String UserId="";
                String Isim="";
               for (SikayetSirali value : sirali){
                    if(value.Sikayet.equals(sikayet1)) {
                        sikayetId = value.SikayedId;
                        UserId=value.UserId;
                        Isim=value.Isim;
                    }
                }
                createPopup(sikayetId,UserId,Isim);
            }
        });

    }
    /*
    Yapıldı olarak işaretlensin mi popup ini açar
     */
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

        AlertDialog.Builder builder = new AlertDialog.Builder(SikayetGoruntule.this);
        builder.setMessage("Yapildı olarak işaretle?").setPositiveButton("Evet", dialogClickListener)
                .setNegativeButton("Hayır", dialogClickListener).show();
    }

}
