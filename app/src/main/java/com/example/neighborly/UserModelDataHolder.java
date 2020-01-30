package com.example.neighborly;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserModelDataHolder {
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private UserModel currentUser;

    public UserModel getCurrentUser() {
        return currentUser;
    }


    /**
     * This function changes the current hold user and updates it in the DB.
     * @param currentUser
     */
    public void setCurrentUser(UserModel currentUser) {
        this.currentUser = currentUser;

        DatabaseReference usersRef = database.getReference().child(Constants.DB_USERS);
        Map<String, Object> users = new HashMap<>();
        users.put(currentUser.getId(), currentUser);
        usersRef.updateChildren(users);
    }

    private static final UserModelDataHolder holder = new UserModelDataHolder();

    public static UserModelDataHolder getInstance() {
        return holder;
    }

}
