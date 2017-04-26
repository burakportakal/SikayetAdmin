package com.example.galatasaray.sikayetadmin;

import android.support.annotation.NonNull;

import java.util.Date;

/**
 * Created by Galatasaray on 8.04.2017.
 */

public class Sikayetler{
    public String UserId;
    public String Isim;
    public String DaireNo;
    public String ApartAdi;
    public String Telefon;
    public SikayetData[] Sikayetleri;
    public Sikayetler(){}
    public Sikayetler(String userId,String isim,String daireNo,String apartAdi,String telefon,SikayetData[] sikayetleri,String dateTime){
        UserId=userId;
        Isim=isim;
        DaireNo=daireNo;
        ApartAdi=apartAdi;
        Telefon=telefon;

        Sikayetleri=sikayetleri;
    }


}
