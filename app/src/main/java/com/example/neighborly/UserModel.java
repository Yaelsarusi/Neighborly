package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private String id;
    private String userPresentedName;
    private String address;
    private String description;
    private List<Item> userItems;

    public UserModel() {
        userItems = new ArrayList<>();
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

    public List<Item> getUserItems() {
        return userItems;
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

    public String getId() {
        return id;
    }
}