package com.shailu.eteasytake.model;

import android.widget.LinearLayout;

public class Admin_History_Model {
    private String Time_of_Order;
    private String Total;
    private String Name;
    private String table_no;
    private String Phone;
    private String pay_type;


    public Admin_History_Model() {
    }

    public Admin_History_Model(String time_of_Order, String total, String name, String table_no, String phone, String pay_type) {
        Time_of_Order = time_of_Order;
        Total = total;
        Name = name;
        this.table_no = table_no;
        Phone = phone;
        this.pay_type = pay_type;
    }

    public String getTime_of_Order() {
        return Time_of_Order;
    }

    public void setTime_of_Order(String time_of_Order) {
        Time_of_Order = time_of_Order;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getTable_no() {
        return table_no;
    }

    public void setTable_no(String table_no) {
        this.table_no = table_no;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getPay_type() {
        return pay_type;
    }

    public void setPay_type(String pay_type) {
        this.pay_type = pay_type;
    }
}
