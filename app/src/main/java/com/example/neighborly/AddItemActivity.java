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
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import de.hdodenhof.circleimageview.CircleImageView;

public class AddItemActivity extends AppCompatActivity {

    public static final int ADD_NEW_ITEM = 0;
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final int PICK_IMAGE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 2;
    public static final int EDIT_EXISTING_ITEM = 3;
    private CircleImageView addItemImage;
    private int activityType;
    Uri newImageUri;
    Button btnAdd;
    UserModel currentUser;
    EditText name;
    EditText description;
    ItemModel item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        setToolbar();
        currentUser = UserModelDataHolder.getInstance().getCurrentUser();

        addItemImage = (CircleImageView) findViewById(R.id.itemImage);
        name = findViewById(R.id.editTextName);
        description = findViewById(R.id.editTextDescription);
        btnAdd = findViewById(R.id.buttonAdd);

        Intent activityIntent = getIntent();

        activityType = activityIntent.getIntExtra("activityType", 0);
        if (activityType == AddItemActivity.EDIT_EXISTING_ITEM){
            item = (ItemModel)activityIntent.getSerializableExtra("item");
            Picasso.get().load(Uri.parse(item.getImageUriString())).into(addItemImage);
            name.setText(item.getName());
            description.setText(item.getDescription());
        }

        addItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String newImageUriString;
                if (newImageUri == null) {
                    if (item != null) {
                        newImageUriString = item.getImageUriString();
                    } else {
                        newImageUriString = Uri.parse("android.resource://com.example.neighborly/drawable/sticker").toString();
                    }
                } else {
                    newImageUriString = newImageUri.toString();
                }

                ItemModel newItem = new ItemModel(newImageUriString, name.getText().toString(),
                        currentUser.getId(), description.getText().toString());
                addImageToStorage(newItem);
                UserModelDataHolder.getInstance().setCurrentUser(currentUser);
                startActivity(new Intent(AddItemActivity.this, MainActivity.class));
                finish();
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
                    }
                });

            }
        });

    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.back_button);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void addItemsUnderBuildingInDB(ItemModel newItem) {
        DatabaseReference buildingsRef = database.getReference().child(Constants.DB_BUILDINGS);

        // update the building model
        BuildingModel currentBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();
        currentBuilding.addItemToList(newItem);
        BuildingModelDataHolder.getInstance().setCurrentBuilding(currentBuilding);
    }

    private void addItemToUserInDB(ItemModel newItem) {
        // update the user model
        currentUser.addToItemsList(newItem);
        UserModelDataHolder.getInstance().setCurrentUser(currentUser);
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
