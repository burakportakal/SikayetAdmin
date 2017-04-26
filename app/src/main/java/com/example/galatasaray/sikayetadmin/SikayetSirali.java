package com.example.galatasaray.sikayetadmin;

import android.support.annotation.NonNull;
import android.widget.TextView;

import java.util.Date;

/**
 * Created by Galatasaray on 8.04.2017.
 */

public class SikayetSirali implements Comparable<SikayetSirali> {
    public String UserId;
    public String Isim;
    public String Tarih;
    public String Sikayet;
    public String Daire;
    public String Apart;
    public String Telefon;
    public String SikayedId;
    public Boolean Yapildi;
    public SikayetSirali(){

    }
    public SikayetSirali(String userId,String isim,String tarih,String sikayet,String daire,String apart,String telefon,String sikayedId,Boolean yapildi){
        UserId=userId;
        Isim=isim;
        Tarih=tarih;
        Sikayet=sikayet;
        Daire=daire;
        Apart=apart;
        Telefon=telefon;
        SikayedId=sikayedId;
        Yapildi=yapildi;
    }

    @Override
    public int hashCode()
    {
        return Sikayet.hashCode();
    }


    @Override
    public boolean equals(Object o)
    {
        return this.Sikayet.equals(o);
    }

    @Override
    public int compareTo(@NonNull SikayetSirali o) {
        Date date=new Date(this.Tarih);
        Date date2=new Date(o.Tarih);
        if(date.compareTo(date2)==-1)
            return 1;
        else if(date.compareTo(date2)==1)
            return -1;
        else
            return 0;
    }
    @Override
    public String toString(){
        return "Sikayet eden: "+this.Isim+"\nApart: "+this.Apart+"\nDaire No:"+this.Daire+"\nTelefon: "+this.Telefon+"\nTarih: "+this.Tarih+"\nSikayeti: "+this.Sikayet;
    }
}
