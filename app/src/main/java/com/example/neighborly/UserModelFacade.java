package com.example.neighborly;

import java.util.ArrayList;
import java.util.List;

public class UserModelFacade {
    private String id;
    private String userPresentedName;
    private String description;

    public UserModelFacade() {}

    public UserModelFacade(UserModel user) {
        this.id = user.getId();
        this.userPresentedName = user.getUserPresentedName();
        this.description = user.getDescription();
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

}
