package com.example.neighborly;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class AddItemActivity extends AppCompatActivity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 2;
    private CircleImageView addItemImage;
    Uri newImageUri;
    Button btnAdd;
    UserModel currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        addItemImage = (CircleImageView) findViewById(R.id.itemImage);
        addItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        currentUser = UserModelDataHolder.getInstance().getCurrentUser();

        btnAdd = findViewById(R.id.buttonAdd);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText name = findViewById(R.id.editTextName);
                EditText description = findViewById(R.id.editTextDescription);

                if (newImageUri == null)
                {
                    newImageUri = Uri.parse("android.resource://com.example.neighborly/drawable/sticker");
                }

                ItemModel newItem = new ItemModel(newImageUri.toString(), name.getText().toString(),
                        currentUser.getId(), description.getText().toString());

                addImageToStorage(newItem);

                currentUser.addItemToList(newItem);
                UserModelDataHolder.getInstance().setCurrentUser(currentUser);

                startActivity(new Intent(AddItemActivity.this, MainActivity.class));
            }
        });
    }

    private void addImageToStorage(final ItemModel newItem) {
        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReferenceFromUrl(getString(R.string.ItemImagesStorageUrl));

        // Create a reference to "itemName.jpg"
        String imageReference = newItem.getName() + ".jpg";
        final StorageReference itemImageRef = storageRef.child(imageReference);

        // Get the data from an ImageView as bytes
        addItemImage.setDrawingCacheEnabled(true);
        addItemImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) addItemImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        final UploadTask uploadTask = itemImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // update the items image uri to reference firestorage
                itemImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        newItem.setImageUriString(uri.toString());
                        // upload to db with updated image uri
                        addItemsUnderBuildingInDB(newItem);
                        addItemToUserInDB(newItem);

                        //Do what you need to do with url
                    }});

                }});

    }

    private void addItemsUnderBuildingInDB(ItemModel newItem) {
        DatabaseReference itemsRef = database.getReference().child("Buildings").child(currentUser.getAddress()).child("Items");

        Map<String, Object> items = new HashMap<>();
        items.put(newItem.getName(), newItem);
        itemsRef.updateChildren(items);
    }

    private void addItemToUserInDB(ItemModel newItem) {
        DatabaseReference usersRef = database.getReference().child("Users");

        // update the user model
        currentUser.addItemToList(newItem);

        Map<String, Object> users = new HashMap<>();
        users.put(currentUser.getId(), currentUser);
        usersRef.updateChildren(users);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, PICK_IMAGE);
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK) {
                newImageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), newImageUri);
                    addItemImage.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
