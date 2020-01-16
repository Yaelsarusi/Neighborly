package com.example.neighborly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NeighborsFragment extends Fragment {
    private FirebaseDatabase database;
    private DatabaseReference buildingRef;
    private String curUserAddress;
    private View neighborsView;
    private List<UserModelFacade> neighborsList;
    private String curUserId;
    private ListView neighborsListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        neighborsView = inflater.inflate(R.layout.fragment_neighbours, container, false);
        database = FirebaseDatabase.getInstance();
        curUserId = FirebaseAuth.getInstance().getUid();
        DatabaseReference curUserRef = database.getReference().child("Users").child(curUserId);

        // Create single value event listener because the user address cannot change, so retrieving the building name (to receive the building ref) needs to happen once
        curUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final UserModel curUser = dataSnapshot.getValue(UserModel.class);
                curUserAddress = curUser.getAddress();
                buildingRef = database.getReference().child("Buildings").child(curUserAddress);

                // Create multiple value event listener because the neighbors can change
                buildingRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        BuildingModel curBuilding = dataSnapshot.getValue(BuildingModel.class);
                        neighborsList = curBuilding.getUserIdList();
                        neighborsList.remove(curUserId);
                        updateNeighborsScroll();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        neighborsListView = neighborsView.findViewById(R.id.list_1);
        neighborsListView.setAdapter(new NeighborsListAdapter(neighborsList, getContext()));
        //Toast.makeText(this, text[0], Toast.LENGTH_LONG).show();
        return neighborsView;
    }

    private void updateNeighborsScroll(){
        // maybe do something like this: https://stackoverflow.com/questions/40043289/how-to-put-many-objects-in-a-scroll-view-entry-in-android

    }
}
