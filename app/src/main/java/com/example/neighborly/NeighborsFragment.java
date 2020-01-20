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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class NeighborsFragment extends Fragment {
    private View neighborsView;
    private UserModel curUser;
    private BuildingModel curBuilding;
    private List<UserModelFacade> neighborsList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        neighborsView = inflater.inflate(R.layout.fragment_neighbours, container, false);
        curUser = UserModelDataHolder.getInstance().getCurrentUser();
        curBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();

        neighborsList = new ArrayList<UserModelFacade>();

        // Delete curUser from the neighbors list.
        for (UserModelFacade neighbor: curBuilding.getUserList()){
            if (!neighbor.getId().equals(curUser.getId())) {
                neighborsList.add(new UserModelFacade(curUser));
            }
        }

        if (neighborsList.isEmpty()){
            Toast.makeText(getActivity(),
                    getString(R.string.emptyBuilding1Msg) + " " + getString(R.string.emptyBuilding2Msg),
                    Toast.LENGTH_LONG).show();
        }

        else {
            updateNeighborsScroll();
            Toast.makeText(getActivity(),
                    getString(R.string.SayHelloToYourNeighbors),
                    Toast.LENGTH_LONG).show();
        }

        return neighborsView;
    }

    private void updateNeighborsScroll(){
        ListView neighborsListView = neighborsView.findViewById(R.id.neighborsDetailsList);
        neighborsListView.setAdapter(new NeighborsListAdapter(getActivity(), neighborsList, getContext()));
    }
}
