package com.example.galatasaray.sikayetadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.galatasaray.sikayetadmin.MainActivity.firebaseDatabase;
import static com.example.galatasaray.sikayetadmin.MainActivity.mAuth;

public class ApartEkle extends AppCompatActivity {
    EditText apartAdi;
    Button btnEkle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apart_ekle);
        apartAdi = (EditText)findViewById(R.id.txtApartAdi);
        btnEkle=(Button)findViewById(R.id.btnApartEkle);
        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String apart=apartAdi.getText().toString();
                FirebaseUser user =mAuth.getCurrentUser();
                String uId=user.getUid();
                Date date=new Date();
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                DatabaseReference mRef= firebaseDatabase.getReference("Admin/"+uId);
                mRef.push().setValue(apart);
                ApartBildirim mApart=new ApartBildirim(apart,uId);
                 mRef= firebaseDatabase.getReference("ApartListesi");
                 mRef.push().setValue(mApart);
            }
        });

    }
    public void onBackPressed() {
        finish();
    }
}
