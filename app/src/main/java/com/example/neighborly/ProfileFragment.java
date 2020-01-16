package com.example.neighborly;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

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
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment {
    private Button btnAddNewItem;
    private View profileView;
    private ImageView imageView;
    private DatabaseReference mDatabase;
    private ArrayList<ItemModel> userItems;
    private ViewPager itemCarouselViewPager;
    private ItemCardAdapter itemCarouselCardAdapter;
    private int itemCarouselPosition;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = auth.getCurrentUser();

        mDatabase.child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                UserModel user = dataSnapshot.getValue(UserModel.class);
                loadProfileDetails(user);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnAddNewItem = profileView.findViewById(R.id.buttonAddNewItem);
        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddItemActivity.class));
            }
        });

        mDatabase.child("Users").child(firebaseUser.getUid()).child("userItemModels").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                loadUserSavedItems(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        btnAddNewItem = profileView.findViewById(R.id.buttonAddNewItem);
        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), AddItemActivity.class));
            }
        });


        return profileView;
    }

    private void loadProfileDetails(UserModel user) {
        if (user == null){
            return;
        }

        TextView userName = profileView.findViewById(R.id.userName);
        TextView userDescription = profileView.findViewById(R.id.userDescription);
        TextView userAddress = profileView.findViewById(R.id.userAddress);
        ImageView userImage = profileView.findViewById(R.id.profilePicture);

        userName.setText(user.getUserPresentedName());
        userDescription.setText(user.getDescription());
        // TODO - Change this, it's not working for some reason
        Picasso.get().load(user.getImageUriString()).into(userImage);

        userAddress.setText(user.getAddress());

    }

    private void loadUserSavedItems(DataSnapshot dataSnapshot) {
        ArrayList<ItemModel> itemList = new ArrayList<ItemModel>();
        for (DataSnapshot item : dataSnapshot.getChildren()) {
            ItemModel itemModel = item.getValue(ItemModel.class);
            itemList.add(itemModel);
        }

        ProfileFragment.this.userItems = itemList;
        ProfileFragment.this.itemCarouselCardAdapter = new ItemCardAdapter(itemList, getContext(), true);
        itemCarouselViewPager = ProfileFragment.this.profileView.findViewById(R.id.userItemPager);
        itemCarouselViewPager.setPadding(200, 0, 200, 0);
        itemCarouselViewPager.setAdapter(ProfileFragment.this.itemCarouselCardAdapter);
    }

}
