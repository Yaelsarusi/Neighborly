package com.example.neighborly;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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

public class AddItemActivity extends Activity {

    private final FirebaseDatabase database = FirebaseDatabase.getInstance();

    public static final int ADD_NEW_ITEM = 0;
    private static final int PICK_IMAGE = 1;
    private static final int MY_CAMERA_PERMISSION_CODE = 2;
    public static final int EDIT_EXISTING_ITEM = 3;

    private CircleImageView addItemImage;
    private Uri newImageUri;
    private boolean imageChanged = false;
    private UserModel currentUser;
    private EditText name;
    private EditText description;
    private ItemModel item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_add_item);

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        getWindow().setLayout((int) (metrics.widthPixels*0.8), (int) (metrics.heightPixels*0.5));

        currentUser = UserModelDataHolder.getInstance().getCurrentUser();

        addItemImage = findViewById(R.id.itemImage);
        name = findViewById(R.id.editTextName);
        description = findViewById(R.id.editTextDescription);
        Button addButton = findViewById(R.id.buttonAdd);

        Intent activityIntent = getIntent();

        if (activityIntent.getIntExtra("activityType", 0) == AddItemActivity.EDIT_EXISTING_ITEM){
            item = (ItemModel)activityIntent.getSerializableExtra("item");
            if (!item.getImageUriString().isEmpty()){
                Picasso.get().load(Uri.parse(item.getImageUriString())).into(addItemImage);
            }
            description.setText(item.getDescription());
            name.setText(item.getName());

        }

        addItemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageChanged = true;
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE);
            }
        });

        addButton.setOnClickListener(new View.OnClickListener() {
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
                finish();
            }
        });

        TextView textClose = findViewById(R.id.txtClose);
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void addImageToStorage(final ItemModel newItem) {
        if(!imageChanged){
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.defualt_image);
            addItemImage.setImageBitmap(bitmap);
        }
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
