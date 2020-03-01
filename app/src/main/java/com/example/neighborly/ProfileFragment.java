package com.example.neighborly;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private Button btnAddNewItem;
    private Button editToggleButton;
    private View profileView;
    private ViewPager itemCarouselViewPager;
    private UserModel curUser;
    private boolean editMode;
    public ItemCardAdapter itemCarouselCardAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        profileView = inflater.inflate(R.layout.fragment_profile, container, false);

        curUser = UserModelDataHolder.getInstance().getCurrentUser();

        loadProfileDetails();

        btnAddNewItem = profileView.findViewById(R.id.buttonAddNewItem);
        btnAddNewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent newItemIntent = new Intent(getActivity(), AddItemActivity.class);
                newItemIntent.putExtra("activityType", AddItemActivity.ADD_NEW_ITEM);
                getActivity().startActivity(newItemIntent);
            }
        });

        DatabaseReference userItemsRef = database.getReference().child(Constants.DB_USERS).child(curUser.getId());

        // update current building when changed in DB
        userItemsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                itemCarouselCardAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        setUserSavedItemsCarousel();

        editToggleButton = profileView.findViewById(R.id.editToggleButton);
        setToViewMode();

        editToggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editMode){
                    updateDescription();
                    setToViewMode();
                    loadProfileDetails();
                } else {
                    setToEditMode();
                }
            }
        });

        return profileView;
    }

    private void setToViewMode() {
        editMode = false;
        // Todo - Change edit button to edit picture
        EditText descEdit = profileView.findViewById(R.id.userDescriptionEdit);
        TextView descView = profileView.findViewById(R.id.userDescription);
        descEdit.setVisibility(View.INVISIBLE);
        descView.setVisibility(View.VISIBLE);
    }

    private void setToEditMode() {
        editMode = true;
        // Todo - Change edit button to V picture
        EditText descEdit = profileView.findViewById(R.id.userDescriptionEdit);
        TextView descView = profileView.findViewById(R.id.userDescription);
        descEdit.setText(curUser.getDescription());
        descEdit.setVisibility(View.VISIBLE);
        descView.setVisibility(View.INVISIBLE);
    }

    private void loadProfileDetails() {
        if (curUser == null){
            return;
        }

        TextView userName = profileView.findViewById(R.id.userName);
        TextView userDescription = profileView.findViewById(R.id.userDescription);
        TextView userAddress = profileView.findViewById(R.id.userAddress);
        CircleImageView userImage = profileView.findViewById(R.id.profilePicture);

        userName.setText(curUser.getPresentedName());
        userDescription.setText(curUser.getDescription());

        Glide.with(getContext()).load(curUser.getImageUriString()).into(userImage);

        userAddress.setText(curUser.getAddress());

    }

    private void setUserSavedItemsCarousel() {
        ProfileFragment.this.itemCarouselCardAdapter = new ItemCardAdapter(curUser.getItemsList(), getContext(), false);
        itemCarouselViewPager = ProfileFragment.this.profileView.findViewById(R.id.userItemPager);
        itemCarouselViewPager.setPadding(200, 0, 200, 0);
        itemCarouselViewPager.setAdapter(ProfileFragment.this.itemCarouselCardAdapter);

    }

    private void updateDescription(){
        EditText searchText = profileView.findViewById(R.id.userDescriptionEdit);
        String newDesc = searchText.getText().toString();
        curUser.setDescription(newDesc);
        database.getReference().child(Constants.DB_USERS).child(curUser.getId()).child("description").setValue(curUser.getDescription());

        BuildingModel curBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();
        curBuilding.setUserDescriptionById(curUser.getId(), newDesc);
        BuildingModelDataHolder.getInstance().setCurrentBuilding(curBuilding);

    }


}
