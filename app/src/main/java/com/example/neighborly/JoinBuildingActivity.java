package com.example.neighborly;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinBuildingActivity extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE = 1;
    private Button btnDone;
    private Dialog dialog;
    private String address;
    private String description;
    private UserModel userLoggedIn;
    private Uri profilePhotoUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_building);
        dialog = new Dialog(this);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference users = FirebaseDatabase.getInstance().getReference().child(Constants.DB_USERS).child(auth.getUid());

        CircleImageView profileImage = findViewById(R.id.profileImage);
        profilePhotoUri = auth.getCurrentUser().getPhotoUrl();
        if (profilePhotoUri != null) {
            Glide.with(this).load(profilePhotoUri).into(profileImage);
        }

        TextView userName = findViewById(R.id.userName);
        userName.setText(auth.getCurrentUser().getDisplayName());

        // This call will only happen if user was already logged in
        users.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel curUser = dataSnapshot.getValue(UserModel.class);
                UserModelDataHolder.getInstance().setCurrentUser(curUser);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        btnDone = (Button) findViewById(R.id.buttonAdd);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = ((EditText) findViewById(R.id.editTextStreetAddress)).getText().toString()
                        + " " + ((EditText) findViewById(R.id.editTextCity)).getText().toString();

                description = ((EditText) findViewById(R.id.userDescriptionEdit)).getText().toString();

                DatabaseReference buildingRef = database.getReference().child(Constants.DB_BUILDINGS);

                userLoggedIn = createUserModelForLoggedInUser();

                // check for existing building
                buildingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        addUserToDatabase();
                        for (DataSnapshot building : dataSnapshot.getChildren()) {
                            BuildingModel buildingModel = (BuildingModel) building.getValue(BuildingModel.class);
                            if (buildingModel != null && buildingModel.getAddress() != null) {
                                if (buildingModel.getAddress().equals(address)) {
                                    // building exists, add user to it
                                    addUserToBuilding(buildingModel);
                                    Toast.makeText(JoinBuildingActivity.this, "added to building", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(JoinBuildingActivity.this, MainActivity.class));
                                    return;
                                }
                            }
                        }

                        // building does not exist, show new building popup
                        ShowNewBuildingPopup();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // ...
                    }
                });
            }
        });
    }

    private UserModel createUserModelForLoggedInUser() {
        UserModel user = UserModelDataHolder.getInstance().getCurrentUser();

        if (user == null) {
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            String photoUrl = "";
            if (profilePhotoUri != null) {
                photoUrl = profilePhotoUri.toString();
            }

            user = new UserModel(firebaseUser.getUid(), firebaseUser.getDisplayName(), address, photoUrl, description);
            // set data holder user model for reuse across the app
            UserModelDataHolder.getInstance().setCurrentUser(user);
        }
        return user;
    }

    private void addUserToDatabase() {
        DatabaseReference usersRef = database.getReference().child(Constants.DB_USERS);
        Map<String, Object> users = new HashMap<>();
        users.put(userLoggedIn.getId(), userLoggedIn);
        usersRef.updateChildren(users);
    }

    public void addUserToBuilding(BuildingModel buildingModel) {
        DatabaseReference buildingRef = database.getReference().child(Constants.DB_BUILDINGS);
        buildingModel.addUserToList(new UserModelFacade(userLoggedIn));
        Map<String, Object> buildings = new HashMap<>();
        buildings.put(buildingModel.getAddress(), buildingModel);
        buildingRef.updateChildren(buildings);
        BuildingModelDataHolder.getInstance().setCurrentBuilding(buildingModel);

    }

    public void ShowNewBuildingPopup() {
        dialog.setContentView(R.layout.activity_add_building_popup);
        TextView textClose = (TextView) dialog.findViewById(R.id.txtClose);
        Button btnDone = (Button) dialog.findViewById(R.id.buttonDone);
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addBuildingToDB();
                startActivity(new Intent(JoinBuildingActivity.this, MainActivity.class));
            }
        });
    }

    private void addBuildingToDB() {
        DatabaseReference buildingRef = database.getReference().child(Constants.DB_BUILDINGS);

        UserModelFacade newUserFacade = new UserModelFacade(userLoggedIn);
        BuildingModel newBuilding = new BuildingModel(address, newUserFacade);

        Map<String, Object> buildings = new HashMap<>();
        buildings.put(address, newBuilding);
        buildingRef.updateChildren(buildings);

        BuildingModelDataHolder.getInstance().setCurrentBuilding(newBuilding);
    }

}
