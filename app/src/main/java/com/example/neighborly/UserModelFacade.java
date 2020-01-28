package com.example.neighborly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModelFacade implements Serializable {
    private String id;
    private String presentedName;
    private String description;
    // firebase does not handle uri, we hold a string instead
    private String imageUriString;
    private List<Integer> badges;

    public UserModelFacade() { badges = new ArrayList<>();}

    public UserModelFacade(UserModel user) {
        this.id = user.getId();
        this.presentedName = user.getPresentedName();
        this.description = user.getDescription();
        this.imageUriString = user.getImageUriString();
        this.badges = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public String getPresentedName() {
        return presentedName;
    }

    public String getDescription() {
        return description;
    }

    public String getImageUriString() {
        return this.imageUriString;
    }

    public List<Integer> getBadges() { return badges; }

    public void setBadges(List<Integer> badges) { this.badges = badges; }

    public void addBadge(int badge){ this.badges.add(badge); }

    public void setDescription(String newDesc) {
        this.description = newDesc;
    }
}

