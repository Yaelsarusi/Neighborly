package com.example.neighborly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ProfileFragment extends Fragment {

    private View profileView;
    private ImageView imageView;
    private DatabaseReference mDatabase;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();
        mDatabase.child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadProfileDetails(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //loadProfileImage();

        return profileView;
    }

    private void loadProfileDetails(DataSnapshot dataSnapshot) {
        User user = dataSnapshot.getValue(User.class);

        TextView userName = profileView.findViewById(R.id.userName);
        TextView userDescription = profileView.findViewById(R.id.userDescription);
        TextView userAddress = profileView.findViewById(R.id.userAddress);

        userName.setText(user.getUserPresentedName());
        userDescription.setText(user.getDescription());
        userAddress.setText(user.getAddress());

        loadUserSavedItems(user.getUserItems());
    }

    private void loadUserSavedItems(List<Item> userItems) {
        // todo - init the item carousel
    }

    private void loadProfileImage(){
        // This is taken from here:
        // https://stackoverflow.com/questions/50816557/storing-and-displaying-image-using-glide-firebase-android
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String curUser = auth.getUid();

        // Reference to an image file in Cloud Storage
        // Todo - This maybe isn't how we will store the curUser Image. Need to decide how and to refactor this.
        StorageReference storageReference  = FirebaseStorage.getInstance().getReference().child(curUser).child("images/profile_image");
        imageView = profileView.findViewById(R.id.profilePicture);

        // Load the image using Glide
        Glide.with(ProfileFragment.this.getContext()).load(storageReference).into(imageView);
    }

}
