package com.example.touringapp.Model;

public class Data {
    private int amount;
    private String type;
    private String date;
    private String description;
    private String id;

    public Data(){

    }

    public Data(int amount, String type, String description, String date, String id) {
        this.amount = amount;
        this.type = type;
        this.description = description;
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
