package com.example.neighborly;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FeedFragment extends Fragment {
    private final FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static final String foundIntroText = "Hi %1s, we found neighbors that have the item you were looking for!";
    private static final String notFoundIntroText = "Hi %1s, we haven't found neighbors that have the item you were looking for. :(";
    private View feedView;
    private Dialog popupRequestDialog;
    private EditText searchText;
    private BuildingModel curBuilding;
    private UserModel curUser;
    private List<RequestModel> userOpenRequests;
    private List<RequestModel> neighborsOpenRequests;
    private List<UserModel> privateChats;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedView = inflater.inflate(R.layout.fragment_feed, container, false);
        curUser = UserModelDataHolder.getInstance().getCurrentUser();

        popupRequestDialog = new Dialog(this.getContext());

        ImageButton searchButton = feedView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = feedView.findViewById(R.id.SearchText);
                String itemName = searchText.getText().toString();
                showPopup(v, itemName, searchForItem(itemName));
            }
        });

        database.getReference().child(Constants.DB_BUILDINGS).child(curUser.getAddress()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();

                if (curBuilding != null) {
                    separateRequestsInBuilding();
                    updateNeighborsScroll();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        return feedView;
    }

    private Map<UserModelFacade, ItemModel> searchForItem(String itemToSearch) {
        final String cleanedSearch = ItemModel.cleanItemName(itemToSearch);
        final Map<UserModelFacade, ItemModel> foundItems = new HashMap<>();

        BuildingModel building = BuildingModelDataHolder.getInstance().getCurrentBuilding();
        List<ItemModel> buildingItems = building.getItemsList();
        if (buildingItems != null) {
            for (UserModelFacade neighbor : BuildingModelDataHolder.getInstance().getCurrentBuilding().getUsersList()) {
                if(neighbor == null || neighbor.getId().equals(curUser.getId())){
                    continue;
                }
                for (ItemModel item : buildingItems) {
                    if (item != null && item.getOwnerId().equals(neighbor.getId())) {
                        if (item.getName().equals(cleanedSearch)) {
                            foundItems.put(neighbor, item);
                            break;
                        }
                        else if (item.getName().contains(cleanedSearch)) {
                            foundItems.put(neighbor, item);
                        }
                    }
                }
            }
        }

        return foundItems;
    }

    private void showPopup(View view, final String itemName, Map<UserModelFacade, ItemModel> foundItems) {
        ImageButton sendButton;
        popupRequestDialog.setContentView(R.layout.popup_add_request);
        EditText requestMessageEditor = popupRequestDialog.findViewById(R.id.editRequestMessage);
        requestMessageEditor.setText(String.format(getString(R.string.neighborly_send_help_message), itemName));
        requestMessageEditor.setHint(String.format(getString(R.string.neighborly_send_help_message), itemName));
        TextView intro = popupRequestDialog.findViewById(R.id.intro);
        if(foundItems.size() == 0){
            intro.setText(String.format(notFoundIntroText, curUser.getPresentedName()));
            popupRequestDialog.findViewById(R.id.startChat).setVisibility(View.GONE);
            popupRequestDialog.findViewById(R.id.foundNeighbors).setVisibility(View.GONE);
        }
        else {
            intro.setText(String.format(foundIntroText, curUser.getPresentedName()));
            ListView neighborsListView = popupRequestDialog.findViewById(R.id.foundNeighbors);
            neighborsListView.setAdapter(new SearchResultListAdapter(getActivity(), foundItems, getContext(), itemName));
        }

        sendButton = popupRequestDialog.findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText requestMessageEditor = popupRequestDialog.findViewById(R.id.editRequestMessage);
                String newItemRequestContent = requestMessageEditor.getText().toString();
                UserModel curUser = UserModelDataHolder.getInstance().getCurrentUser();

                DatabaseReference requestsRef = database.getReference().child(Constants.REQUESTS);

                // Generate a reference to a new location and add some data using push()
                DatabaseReference requestKeyRef = requestsRef.push();

                // Get the unique ID generated by push() by accessing its key
                String requestId = requestKeyRef.getKey();
                if (requestId != null) {
                    RequestModel newRequest = new RequestModel(requestId, false, curUser.getId(),
                            newItemRequestContent, itemName);
                    Map<String, Object> requests = new HashMap<>();
                    requests.put(requestId, newRequest);

                    requestKeyRef.setValue(newRequest);
                    addRequestsUnderBuildingInDB(newRequest);

                    Intent intent = new Intent(getActivity(), RequestActivity.class);
                    intent.putExtra("requestType", RequestActivity.REQUEST_ITEM);
                    intent.putExtra("requestId", requestId);
                    startActivity(intent);

                    popupRequestDialog.dismiss();
                }
            }
        });
        popupRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupRequestDialog.show();
    }

    private void addRequestsUnderBuildingInDB(RequestModel newRequest) {
        DatabaseReference buildingsRef = database.getReference().child(Constants.DB_BUILDINGS);

        // update the building model
        BuildingModel currentBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();
        currentBuilding.addRequestToList(newRequest);

        Map<String, Object> buildings = new HashMap<>();
        buildings.put(currentBuilding.getAddress(), currentBuilding);
        buildingsRef.updateChildren(buildings);
    }

    private void separateRequestsInBuilding() {

        userOpenRequests = new ArrayList<>();
        neighborsOpenRequests = new ArrayList<>();
        for (RequestModel request : curBuilding.getRequestList()) {
            if(request == null || request.isResolved()) {
                continue;
            }
            if (request.getRequestUserId().equals(curUser.getId())) {
                userOpenRequests.add(request);
            } else {
                neighborsOpenRequests.add(request);
            }
        }
    }

    private void updateNeighborsScroll() {
        ListView neighborsRequestListView = feedView.findViewById(R.id.neighborsRequestList);
        neighborsRequestListView.setAdapter(new NeighborsRequestListAdapter(getActivity(), neighborsOpenRequests, getContext()));
    }

}
