package com.example.neighborly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.List;

public class NeighborsFragment extends Fragment {
    private DatabaseReference buildingRef;
    private View neighborsView;
    private List<UserModelFacade> neighborsList;
    private UserModel curUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        neighborsView = inflater.inflate(R.layout.fragment_neighbours, container, false);
        curUser = UserModelDataHolder.getInstance().getCurrentUser();
        buildingRef = FirebaseDatabase.getInstance().getReference().child("Buildings").child(curUser.getAddress());

        buildingRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                BuildingModel curBuilding = dataSnapshot.getValue(BuildingModel.class);
                neighborsList = curBuilding.getUserList();
                if (neighborsList != null){

                    neighborsList.remove(new UserModelFacade(curUser));
                }
                updateNeighborsScroll();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return neighborsView;
    }

    private void updateNeighborsScroll(){
        // maybe do something like this: https://stackoverflow.com/questions/40043289/how-to-put-many-objects-in-a-scroll-view-entry-in-android
        ListView neighborsListView = neighborsView.findViewById(R.id.neighborsDetailsList);
        neighborsListView.setAdapter(new NeighborsListAdapter(neighborsList, getContext()));
        //Toast.makeText(this, text[0], Toast.LENGTH_LONG).show();

    }
}
