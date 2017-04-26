package com.example.galatasaray.sikayetadmin;

import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by Galatasaray on 8.04.2017.
 */

public class SikayetData implements Comparable<SikayetData> {
    public String SikayetId;
    public String Sikayet;
    public  String Tarih;
    public Boolean Yapildi;

    public SikayetData(){

    }
    public SikayetData(String sikayet,String tarih,String sikayetId,Boolean yapildi){
        this.Sikayet=sikayet;
        this.Tarih=tarih;
        this.SikayetId=sikayetId;
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
    public int compareTo(@NonNull SikayetData o) {
        Date date=new Date(this.Tarih);
        Date date2=new Date(o.Tarih);
        if(date.compareTo(date2)==-1)
            return 1;
        else if(date.compareTo(date2)==1)
            return -1;
        else
            return 0;
    }



}
