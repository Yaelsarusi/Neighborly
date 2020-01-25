package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private String id;
    private String presentedName;
    private String address;
    private String description;
    private List<ItemModel> itemsList;
    // firebase does not handle uri, we hold a string instead
    private String imageUriString;

    public UserModel() {
        this.itemsList = new ArrayList<>();
    }

    public UserModel(String id, String name, String address, String imageUriString, String description) {
        this.id = id;
        this.presentedName = name;
        this.address = address;
        this.itemsList = new ArrayList<>();
        this.imageUriString = imageUriString;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPresentedName() {
        return presentedName;
    }

    public void setPresentedName(String presentedName) {
        this.presentedName = presentedName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ItemModel> getItemsList() {
        return itemsList;
    }

    public void setItemsList(List<ItemModel> itemsList) {
        this.itemsList = itemsList;
    }

    public void addToItemsList(ItemModel item){
        this.itemsList.add(item);
    }

    public String getImageUriString() {
        return imageUriString;
    }

    public void setImageUriString(String imageUriString) {
        this.imageUriString = imageUriString;
    }
}
