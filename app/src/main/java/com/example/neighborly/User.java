package com.example.neighborly;
import java.util.ArrayList;
import java.util.List;

public class User {
    private String id;
    private String userPresentedName;
    private String address;
    private String description;
    private List<Item> userItems;

    public User(){
        userItems = new ArrayList<>();
    }

    public User(String id, String name){
        this.id = id;
        this.userPresentedName = name;
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
