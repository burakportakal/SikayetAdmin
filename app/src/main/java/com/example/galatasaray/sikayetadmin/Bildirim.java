package com.example.galatasaray.sikayetadmin;

/**
 * Created by Galatasaray on 9.04.2017.
 */

public class Bildirim {

    public String Name;
    public String DaireNo;
    public Boolean Okundu;
    public Bildirim(){}
    public Bildirim(String name,String daireNo,Boolean okundu){
        Name=name;
        DaireNo=daireNo;
        Okundu=okundu;
    }
}
