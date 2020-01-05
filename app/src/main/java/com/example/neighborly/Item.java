package com.example.neighborly;

import com.google.android.gms.auth.api.Auth;

public class Item {
    private String ownerId;
    private String name;
    private String description;
    private String imagePathInStore;

    // This constructor is needed for Firebase
    public Item(){}

    public Item(String ownerId, String name){
        this.ownerId = ownerId;
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagePathInStore() {
        return imagePathInStore;
    }

    public void setImagePathInStore(String imagePathInStore) {
        this.imagePathInStore = imagePathInStore;
    }
}
