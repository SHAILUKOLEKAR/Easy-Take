package com.shailu.eteasytake.model;

public class Users {

    private String Name, Password, Phone, Image, Owner_Name, Hotel_Name, MO_NO_for_Pay, Village_Name, NO_OF_TABLEs,UPI;

    public Users() {

    }

    public Users(String name, String password, String phone, String image, String owner_Name, String hotel_Name, String MO_NO_for_Pay, String village_Name, String NO_OF_TABLEs, String UPI) {
        Name = name;
        Password = password;
        Phone = phone;
        Image = image;
        Owner_Name = owner_Name;
        Hotel_Name = hotel_Name;
        this.MO_NO_for_Pay = MO_NO_for_Pay;
        Village_Name = village_Name;
        this.NO_OF_TABLEs = NO_OF_TABLEs;
        this.UPI = UPI;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPassword() {
        return Password;
    }

    public void setPassword(String password) {
        Password = password;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getOwner_Name() {
        return Owner_Name;
    }

    public void setOwner_Name(String owner_Name) {
        Owner_Name = owner_Name;
    }

    public String getHotel_Name() {
        return Hotel_Name;
    }

    public void setHotel_Name(String hotel_Name) {
        Hotel_Name = hotel_Name;
    }

    public String getMO_NO_for_Pay() {
        return MO_NO_for_Pay;
    }

    public void setMO_NO_for_Pay(String MO_NO_for_Pay) {
        this.MO_NO_for_Pay = MO_NO_for_Pay;
    }

    public String getVillage_Name() {
        return Village_Name;
    }

    public void setVillage_Name(String village_Name) {
        Village_Name = village_Name;
    }

    public String getNO_OF_TABLEs() {
        return NO_OF_TABLEs;
    }

    public void setNO_OF_TABLEs(String NO_OF_TABLEs) {
        this.NO_OF_TABLEs = NO_OF_TABLEs;
    }

    public String getUPI() {
        return UPI;
    }

    public void setUPI(String UPI)
    {
        this.UPI = UPI;
    }
}