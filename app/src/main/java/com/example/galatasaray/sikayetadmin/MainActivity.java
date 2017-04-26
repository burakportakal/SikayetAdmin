package com.example.galatasaray.sikayetadmin;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "EmailPassword";
    public static final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    public static final FirebaseAuth mAuth =FirebaseAuth.getInstance();;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private NotificationManager notificationManager;
    public static Apart apartlar=new Apart();
    ArrayList<Bildirim> bildirimler=new ArrayList<Bildirim>();

    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    if(progress==null)
                         progress = ProgressDialog.show(MainActivity.this, "Giriş",
                            "Giriş yapılıyor", true);
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                updateUI(user);

                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
    }
    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
    public void login(View view)
    {
        signIn("ageafo@gmail.com","metallica2");
    }
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        progress = ProgressDialog.show(this, "Giriş",
                "Giriş yapılıyor", true);
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(MainActivity.this, "Auth failed",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [START_EXCLUDE]
                        /*if (!task.isSuccessful()) {
                            mStatusTextView.setText(R.string.auth_failed);
                        }*/

                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }
    //Admin sayfasına girişi sağlıyor
    //@param user kullanıcı girişi yapan user
    //Admin değilse ne olcağı bilinmiyor düzelt
    private void getDataFromDatabase(FirebaseUser user)
    {
        if(user != null) {
            String uId = user.getUid();
            DatabaseReference userInfo = firebaseDatabase.getReference("Admin/"+uId);
                userInfo.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                       String[] data = new String[(int) dataSnapshot.getChildrenCount()];
                       int counter=0;
                        for (Iterator<DataSnapshot> i = dataSnapshot.getChildren().iterator(); i.hasNext(); ) {

                            DataSnapshot snap=i.next();
                            data[counter]=snap.getValue().toString();
                            counter++;
                        }
                        apartlar.ApartAdi = data;
                        adminPage();
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        }
    }
    /*
    //Giriş ekranını günceller butonları yok eder ya da geri getirir.
     */
    private void updateUI(FirebaseUser user) {

        if (user != null)
        {
            findViewById(R.id.btnLogin).setVisibility(View.GONE);
            getDataFromDatabase(user);
        }
        else
        {
            findViewById(R.id.btnLogin).setVisibility(View.VISIBLE);
        }
    }
    /*
    //Admin sayfasını açar varsa progress ekranını sonlandırır.
     */
    private void adminPage()
    {
        Intent intent = new Intent(this,Admin.class);
        intent.putExtra("apartlar",apartlar.ApartAdi);
        if(progress!=null)
            progress.dismiss();
        startActivity(intent);


    }

}
