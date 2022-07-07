package com.shailu.eteasytake.model;

public class Admin_Kitchen_Model {

    private String Item_Name,Quantity,Table_No,Time_of_Order;

    public Admin_Kitchen_Model() {
    }

    public Admin_Kitchen_Model(String item_Name, String quantity, String table_No, String time_of_Order) {
        Item_Name = item_Name;
        Quantity = quantity;
        Table_No = table_No;
        Time_of_Order = time_of_Order;
    }


    public String getItem_Name() {
        return Item_Name;
    }

    public void setItem_Name(String item_Name) {
        Item_Name = item_Name;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getTable_No() {
        return Table_No;
    }

    public void setTable_No(String table_No) {
        Table_No = table_No;
    }

    public String getTime_of_Order() {
        return Time_of_Order;
    }

    public void setTime_of_Order(String time_of_Order) {
        Time_of_Order = time_of_Order;
    }
}
