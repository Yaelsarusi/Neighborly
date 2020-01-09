package com.example.neighborly;

import android.graphics.Bitmap;

public class ItemModel {

    private Bitmap image;
    private String name;
    private String owner;
    private String description;

    public ItemModel()
    {
    }

    public ItemModel(Bitmap image, String name, String owner, String description) {
        this.image = image;
        this.name = name;
        this.owner = owner;
        this.description = description;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
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
