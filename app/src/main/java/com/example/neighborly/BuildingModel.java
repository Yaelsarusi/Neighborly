package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

class BuildingModel {

    private String address;

    private List<UserModelFacade> usersList;
    private List<ItemModel> itemsList;

    public BuildingModel() {
        usersList = new ArrayList<>();
        itemsList = new ArrayList<>();
    }

    public BuildingModel(String address, UserModelFacade user) {
        this.address = address;
        this.usersList = new ArrayList<>();
        this.itemsList = new ArrayList<>();
        this.usersList.add(user);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<UserModelFacade> getUsersList() {
        return usersList;
    }

    public void setUsersList(List<UserModelFacade> usersList) {
        this.usersList = usersList;
    }

    public List<ItemModel> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<ItemModel> itemsList) {
        this.itemsList = itemsList;
    }

    public void addItemToList(ItemModel item) {
        if (itemsList == null){
            itemsList = new ArrayList<>();
        }
        itemsList.add(item);
    }

    public void addUserToList(UserModelFacade user) {
        if (usersList == null){
            usersList = new ArrayList<>();
        }
        for (UserModelFacade oldUser: usersList){
            if (oldUser.getId().equals(user.getId())){
                return;
            }
        }
        usersList.add(user);
    }
}

