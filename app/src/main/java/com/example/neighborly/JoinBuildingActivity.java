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

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;

public class JoinBuildingActivity extends AppCompatActivity {

    private CircleImageView buildingImage;
    private static final int PICK_IMAGE = 1;
    Uri newImageUri;
    Button btnDone;
    Dialog myDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_building);
        myDialog = new Dialog(this);

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
                //todo add firebase logic
                String address = ((EditText) findViewById(R.id.editTextStreetAddress)).getText().toString()
                        + ((EditText) findViewById(R.id.editTextCity)).getText().toString();
                // check for existing building
                if (address.isEmpty()) {
                    //add new building and share options
                    ShowPopup();
                } else {
                    Toast.makeText(JoinBuildingActivity.this, "added to building", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(JoinBuildingActivity.this, MainActivity.class));
                }

            }
        });
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

    public void ShowPopup() {
        TextView txtclose;
        Button btnFollow;
        myDialog.setContentView(R.layout.activity_add_building_popup);
        txtclose = (TextView) myDialog.findViewById(R.id.txtclose);
        btnFollow = (Button) myDialog.findViewById(R.id.btnContinue);
        txtclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // todo add to firebase
                startActivity(new Intent(JoinBuildingActivity.this, MainActivity.class));
            }
        });
    }
}
