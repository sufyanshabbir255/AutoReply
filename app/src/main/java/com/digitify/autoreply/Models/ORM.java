package com.digitify.autoreply.Models;

import com.orm.SugarRecord;

public class ORM extends SugarRecord {
    public String number;
    public String token;
    public ORM(){
    }

    public ORM(String number,String token){
        this.number = number;
        this.token=token;

    }
}