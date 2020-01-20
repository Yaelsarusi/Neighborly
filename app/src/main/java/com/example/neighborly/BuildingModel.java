package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

class BuildingModel {

    private String address;
    private ArrayList<UserModelFacade> userList;
    private ArrayList<ItemModel> itemList;

    public BuildingModel() {
    }

    public BuildingModel(String address, UserModelFacade user) {
        this.address = address;
        this.itemList = new ArrayList<>();
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

    public List<ItemModel> getItemsList() {
        return itemList;
    }

    public void addItemToList(ItemModel item) {
        itemList.add(item);
    }
}
