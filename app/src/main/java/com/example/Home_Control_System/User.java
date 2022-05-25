package com.example.Home_Control_System;

public class User {
    //private int id;
    private String mobilenumber;
    private String password;
    public void setMobileNumber(String MobileNumber){this.mobilenumber = MobileNumber;}
    public void setPassword(String Password) {
        this.password = Password;
    }
    public String getMobilenumber(){return mobilenumber;}
    public String getPassword() {
        return password;
    }

}