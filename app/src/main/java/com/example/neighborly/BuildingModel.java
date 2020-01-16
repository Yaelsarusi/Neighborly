package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

class BuildingModel {

    private String address;
    private List<UserModelFacade> userList;

    public BuildingModel() {
    }

    public BuildingModel(String address, UserModelFacade user) {
        this.address = address;
        this.userList = new ArrayList<>();
        this.userList.add(user);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<UserModelFacade> getUserList() {
        return userList;
    }

    public void addUser(UserModelFacade user) {
        userList.add(user);
    }
}
