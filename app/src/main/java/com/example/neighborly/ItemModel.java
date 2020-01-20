package com.example.neighborly;

public class ItemModel {
    private String name;
    private String ownerId;
    private String description;
    // firebase does not handle uri, we hold a string instead
    private String imageUriString;

    public ItemModel() { }

    public ItemModel(String imageUriString, String name, String ownerId, String description) {
        this.name = cleanItemName(name);
        this.ownerId = ownerId;
        this.description = description;
        this.imageUriString = imageUriString;
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
}
