package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private String id;
    private String userPresentedName;
    private String address;
    private String description;
    private List<ItemModel> userItemModels;

    public UserModel() {
        userItemModels = new ArrayList<>();
    }

    public UserModel(String id, String name, String address) {
        this.id = id;
        this.userPresentedName = name;
        this.address = address;
    }

    public String getUserPresentedName() {
        return userPresentedName;
    }

    public String getAddress() {
        return address;
    }

    public String getDescription() {
        return description;
    }

    public List<ItemModel> getUserItemModels() {
        return userItemModels;
    }

    public void setUserPresentedName(String userPresentedName) {
        this.userPresentedName = userPresentedName;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addItemToList(ItemModel item) {
        userItemModels.add(item);
    }

    public String getId() {
        return id;
    }
}
