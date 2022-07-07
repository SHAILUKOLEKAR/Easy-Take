package com.shailu.eteasytake.model;

public class User_Cart_Model {
    private String Item_Name;
    private String Price, Total, Quantity;
    // private String item_image_cart;


    public User_Cart_Model() {
    }

    public User_Cart_Model(String item_Name, String price, String total, String quantity) {
        Item_Name = item_Name;
        Price = price;
        Total = total;
        Quantity = quantity;
    }

    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}


