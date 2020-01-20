package com.example.neighborly;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class UserModelFacade implements Serializable {
    private String id;
    private String userPresentedName;
    private String description;
    // firebase does not handle uri, we hold a string instead
    private String imageUriString;

    public UserModelFacade() {}

    public UserModelFacade(UserModel user) {
        this.id = user.getId();
        this.userPresentedName = user.getUserPresentedName();
        this.description = user.getDescription();
        this.imageUriString = user.getImageUriString();
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

}
