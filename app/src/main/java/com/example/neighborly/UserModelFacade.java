package com.example.neighborly;

import java.io.Serializable;

public class UserModelFacade implements Serializable {
    private String id;
    private String presentedName;
    private String description;
    // firebase does not handle uri, we hold a string instead
    private String imageUriString;

    public UserModelFacade() {}

    public UserModelFacade(UserModel user) {
        this.id = user.getId();
        this.presentedName = user.getPresentedName();
        this.description = user.getDescription();
        this.imageUriString = user.getImageUriString();
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

}
