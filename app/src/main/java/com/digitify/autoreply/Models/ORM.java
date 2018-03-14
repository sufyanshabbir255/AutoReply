package com.digitify.autoreply.Models;

import com.orm.SugarRecord;

public class ORM extends SugarRecord {
    private String number;
    private String token;
    public ORM(){
    }

    public ORM(String number,String token){
        this.number = number;
        this.token=token;

    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}