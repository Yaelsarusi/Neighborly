package com.example.neighborly;

public class UserModelDataHolder {
    private UserModel currentUser;

    public UserModel getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(UserModel currentUser) {
        this.currentUser = currentUser;
    }

    private static final UserModelDataHolder holder = new UserModelDataHolder();

    public static UserModelDataHolder getInstance() {
        return holder;
    }

}
