package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

class BuildingModel {

    private String address;
    private List<String> userId;

    public BuildingModel() {

    }

    public BuildingModel(String address, String userId) {
        this.address = address;
        this.userId = new ArrayList<>();
        this.userId.add(userId);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getUserId() {
        return userId;
    }

    public void addUserId(String uid) {
        userId.add(uid);
    }
}
