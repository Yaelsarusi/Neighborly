package com.example.neighborly;

import java.io.Serializable;

public class ItemModel implements Serializable {

    private String name;
    private String ownerId;
    private String description;
    // firebase does not handle uri, we represent the image as a string instead
    private String imageUriString;

    public ItemModel() { }

    public ItemModel(String imageUriString, String name, String ownerId, String description) {
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.imageUriString = imageUriString;
    }

    public ItemModel(String name, String ownerId) {
        this.name = cleanItemName(name);
        this.ownerId = ownerId;
        this.description = "";
        this.imageUriString = "";
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUriString() {
        return imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }

    public static String cleanItemName(String name) {
        return name.toLowerCase().replaceAll("\\s+","");
    }

    public String getPresentedName() {
        return name.substring(0,1).toUpperCase() + name.substring(1).toLowerCase();
    }


}
