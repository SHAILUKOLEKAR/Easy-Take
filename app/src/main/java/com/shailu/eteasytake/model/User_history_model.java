package com.shailu.eteasytake.model;

public class User_history_model {
    private String Hotel_name;
    private String Time_of_Order;
    private String Total;

    public User_history_model() {
    }

    public User_history_model(String hotel_name, String time_of_Order, String total) {
        Hotel_name = hotel_name;
        Time_of_Order = time_of_Order;
        Total = total;
    }

    public String getHotel_name() {
        return Hotel_name;
    }

    public void setHotel_name(String hotel_name) {
        Hotel_name = hotel_name;
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
}
