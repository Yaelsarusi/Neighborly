package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

class BuildingModel {

    private String address;
    private List<String> userIdList;

    public BuildingModel() {

    }

    public BuildingModel(String address, String userId) {
        this.address = address;
        this.userIdList = new ArrayList<>();
        this.userIdList.add(userId);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<String> getUserIdList() {
        return userIdList;
    }

    public void addUserId(String uid) {
        userIdList.add(uid);
    }
}
