package com.example.panorama;

import org.json.JSONArray;
import org.json.JSONObject;

public class TransactionsItem {
    float amount;
    String date;
    JSONObject location;
    JSONArray category;
    String name;
    String merchantName;

    public TransactionsItem(float amount, String date, JSONObject location, JSONArray category, String name, String merchantName) {
        this.amount = amount;
        this.date = date;
        this.location = location;
        this.category = category;
        this.name = name;
        this.merchantName = merchantName;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public JSONObject getLocation() {
        return location;
    }

    public void setLocation(JSONObject location) {
        this.location = location;
    }

    public JSONArray getCategory() {
        return category;
    }

    public void setCategory(JSONArray category) {
        this.category = category;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMerchantName() {
        return merchantName;
    }

    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
    }
}

