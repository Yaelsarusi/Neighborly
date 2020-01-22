package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

class BuildingModel {

    private String address;

    private List<UserModelFacade> usersList;
    private List<ItemModel> itemsList;
    private List<RequestModel> requestList;

    public BuildingModel() {
        usersList = new ArrayList<>();
        itemsList = new ArrayList<>();
        requestList = new ArrayList<>();
    }

    public BuildingModel(String address, UserModelFacade user) {
        this.address = address;
        this.usersList = new ArrayList<>();
        this.itemsList = new ArrayList<>();
        this.requestList = new ArrayList<>();
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
        itemsList.add(item);
    }

    public void addUserToList(UserModelFacade user) {
        for (UserModelFacade oldUser: usersList){
            if (oldUser != null && oldUser.getId().equals(user.getId())){
                return;
            }
        }
        usersList.add(user);
    }

    public void addRequestToList(RequestModel requestModel) {
        requestList.add(requestModel);
    }

    public List<RequestModel> getRequestList() {
        return requestList;
    }

    public void setRequestList(List<RequestModel> requestList) {
        this.requestList = requestList;
    }

    public UserModelFacade getUserById(String id){
        for (UserModelFacade user: usersList){
            if (user!= null && user.getId().equals(id)){
                return user;
            }
        }
        return null;
    }
}

