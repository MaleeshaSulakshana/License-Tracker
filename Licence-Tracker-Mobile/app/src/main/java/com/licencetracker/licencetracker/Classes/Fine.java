package com.licencetracker.licencetracker.Classes;

public class Fine {

    int id;
    String date_time, amount, location;

    public Fine(int id, String date_time, String amount, String location) {
        this.id = id;
        this.date_time = date_time;
        this.amount = amount;
        this.location = location;
    }

    public int getId() {
        return id;
    }

    public String getDate_time() {
        return date_time;
    }

    public String getAmount() {
        return amount;
    }

    public String getLocation() {
        return location;
    }

}
