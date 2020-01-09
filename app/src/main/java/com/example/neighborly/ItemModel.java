package com.example.neighborly;

public class ItemModel {

    // firebase does not handle uri, we hold a string instead
    private String imageUriString;
    private String name;
    private String owner;
    private String description;

    public ItemModel() {
    }

    public ItemModel(String imageUriString, String name, String owner, String description) {
        this.imageUriString = imageUriString;
        this.name = name;
        this.owner = owner;
        this.description = description;
    }

    public String getImageUriString() {
        return this.imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
