package com.shailu.eteasytake.model;

public class CategeryTwo {

    private String Total;
    private String user_id;
    private String user_name;
    private String pay_mode;
    private String pay_status;
    private String payer_mo;
    private String user_table;

    public CategeryTwo()
    {
    }

    public CategeryTwo(String total, String user_id, String user_name, String pay_mode, String pay_status, String payer_mo, String user_table) {
        Total = total;
        this.user_id = user_id;
        this.user_name = user_name;
        this.pay_mode = pay_mode;
        this.pay_status = pay_status;
        this.payer_mo = payer_mo;
        this.user_table = user_table;
    }

    public String getTotal() {
        return Total;
    }

    public void setTotal(String total) {
        Total = total;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getPay_mode() {
        return pay_mode;
    }

    public void setPay_mode(String pay_mode) {
        this.pay_mode = pay_mode;
    }

    public String getPay_status() {
        return pay_status;
    }

    public void setPay_status(String pay_status) {
        this.pay_status = pay_status;
    }

    public String getPayer_mo() {
        return payer_mo;
    }

    public void setPayer_mo(String payer_mo) {
        this.payer_mo = payer_mo;
    }

    public String getUser_table() {
        return user_table;
    }

    public void setUser_table(String user_table) {
        this.user_table = user_table;
    }
}
