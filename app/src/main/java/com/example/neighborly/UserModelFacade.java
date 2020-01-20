package com.example.neighborly;

import java.util.ArrayList;

public class UserModelFacade {
    private String id;
    private String userPresentedName;
    private String description;
    // firebase does not handle uri, we hold a string instead
    private String imageUriString;
    private ArrayList<ItemModel> userItemModels;


    public UserModelFacade() {}

    public UserModelFacade(UserModel user) {
        this.id = user.getId();
        this.userPresentedName = user.getUserPresentedName();
        this.description = user.getDescription();
        this.imageUriString = user.getImageUriString();
        this.userItemModels = new ArrayList<>();
        this.userItemModels.addAll(user.getUserItemModels());
    }

    public String getId() {
        return id;
    }

    public String getUserPresentedName() {
        return userPresentedName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUriString() {
        return this.imageUriString;
    }

    public ArrayList<ItemModel> getUserItemModels() {
        return userItemModels;
    }

}
