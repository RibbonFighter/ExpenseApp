package com.example.touringapp.Model;

public class Trip {
    private String name;
    private String destination;
    private String date;
    private String risk;
    private String desc;

    public Trip(){

    }

    public Trip(String name, String destination, String date, String risk, String desc) {
        this.name = name;
        this.destination = destination;
        this.date = date;
        this.risk = risk;
        this.desc = desc;
    }

    public Trip(String name, String destination, String date, String risk, String description, String post_key) {
        this.name = name;
        this.destination = destination;
        this.date = date;
        this.risk = risk;
        this.desc = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRisk() {
        return risk;
    }

    public void setRisk(String risk) {
        this.risk = risk;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}




