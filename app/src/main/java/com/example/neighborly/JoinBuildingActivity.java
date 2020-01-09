package com.example.neighborly;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinBuildingActivity extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE = 1;
    private CircleImageView buildingImage;
    Uri newImageUri;
    Button btnDone;
    Dialog dialog;
    String address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_building);
        dialog = new Dialog(this);

        buildingImage = (CircleImageView) findViewById(R.id.addItemImage);
        buildingImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        btnDone = (Button) findViewById(R.id.buttonAdd);
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                address = ((EditText) findViewById(R.id.editTextStreetAddress)).getText().toString()
                        + ((EditText) findViewById(R.id.editTextCity)).getText().toString();
                // check for existing building
                DatabaseReference buildingRef = database.getReference().child("Buildings");
                buildingRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot building : dataSnapshot.getChildren()) {
                            BuildingModel buildingModel = building.getValue(BuildingModel.class);
                            if (buildingModel != null && buildingModel.getAddress() != null) {
                                if (buildingModel.getAddress().equals(address)) {
                                    // building exists, add user to it
                                    addUserAndAssignBuildingInDB(address);
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

    private void addBuildingToDB(String address) {
        DatabaseReference buildingRef = database.getReference().child("Buildings");

        String userId = FirebaseAuth.getInstance().getUid();
        BuildingModel newBuilding = new BuildingModel(address, userId);

        Map<String, Object> buildings = new HashMap<>();
        buildings.put(address, newBuilding);
        buildingRef.updateChildren(buildings);
    }

    private void addUserAndAssignBuildingInDB(String address) {
        DatabaseReference usersRef = database.getReference().child("Users");

        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        UserModel newUser = new UserModel(firebaseUser.getUid(), firebaseUser.getDisplayName(), address);

        Map<String, Object> users = new HashMap<>();
        users.put(firebaseUser.getUid(), newUser);
        usersRef.updateChildren(users);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                newImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), newImageUri);
                    buildingImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void ShowNewBuildingPopup() {
        dialog.setContentView(R.layout.activity_add_building_popup);
        TextView textClose = (TextView) dialog.findViewById(R.id.txtclose);
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
                addBuildingToDB(address);
                addUserAndAssignBuildingInDB(address);
                startActivity(new Intent(JoinBuildingActivity.this, MainActivity.class));
            }
        });
    }
}
