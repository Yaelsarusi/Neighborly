package com.example.neighborly;

public class ItemModel {
    private String name;
    private String ownerId;
    private String description;
    // firebase does not handle uri, we hold a string instead
    private String imageUriString;

    public ItemModel() {
    }

    public ItemModel(String imageUriString, String name, String ownerId, String description) {
        this.imageUriString = imageUriString;
        this.name = cleanItemName(name);
        this.ownerId = ownerId;
        this.description = description;
    }

    public String getImageUriString() {
        return this.imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }

    public String getName() {
        return name.toLowerCase();
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return ownerId;
    }

    public void setOwner(String owner) {
        this.ownerId = owner;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public static String cleanItemName(String name) {
        return name.toLowerCase().replaceAll("\\s+","");
    }
}
