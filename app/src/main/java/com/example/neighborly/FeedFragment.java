package com.example.neighborly;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Space;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.flexbox.FlexboxLayout;
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
    private static final String foundIntroText = "We found neighbors that can help out!";
    private static final String notFoundIntroText = "We didn't find neighbors that have what you were looking for";
    private View feedView;
    private Dialog popupRequestDialog;
    private EditText searchText;
    private BuildingModel curBuilding;
    private UserModel curUser;
    private List<RequestModel> userOpenRequests;
    private List<RequestModel> neighborsOpenRequests;

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
                showSearchPopup(v, itemName, searchForItem(itemName));
            }
        });

        database.getReference().child(Constants.DB_BUILDINGS).child(curUser.getAddress()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                curBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();
                if (curBuilding != null) {
                    separateRequestsInBuilding();
                    addMyRequestsButtons((FlexboxLayout) feedView.findViewById(R.id.myRequests));
                    updateNeighborsScroll();
                    ProgressBar progressBar = feedView.findViewById(R.id.progressBar);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // ...
            }
        });

        return feedView;
    }

    // -------------- Search and popup --------------

    private Map<UserModelFacade, ItemModel> searchForItem(String itemToSearch) {
        final String cleanedSearch = ItemModel.cleanItemName(itemToSearch);
        final Map<UserModelFacade, ItemModel> foundItems = new HashMap<>();

        BuildingModel building = BuildingModelDataHolder.getInstance().getCurrentBuilding();
        List<ItemModel> buildingItems = building.getItemsList();
        if (buildingItems != null) {
            for (UserModelFacade neighbor : BuildingModelDataHolder.getInstance().getCurrentBuilding().getUsersList()) {
                if (neighbor == null || neighbor.getId().equals(curUser.getId())) {
                    continue;
                }
                for (ItemModel item : buildingItems) {
                    if (item != null && item.getOwnerId().equals(neighbor.getId())) {
                        if (item.getName().equals(cleanedSearch)) {
                            foundItems.put(neighbor, item);
                            break;
                        } else if (item.getName().contains(cleanedSearch)) {
                            foundItems.put(neighbor, item);
                        }
                    }
                }
            }
        }

        return foundItems;
    }

    private void showSearchPopup(View view, final String itemName, Map<UserModelFacade, ItemModel> foundItems) {
        popupRequestDialog.setContentView(R.layout.popup_add_request);
        TextView hello = popupRequestDialog.findViewById(R.id.hello);
        hello.setText(String.format("Hey %s!", (curUser.getPresentedName()+" ").split(" ")[0]));
        EditText requestMessageEditor = popupRequestDialog.findViewById(R.id.editRequestMessage);
        requestMessageEditor.setText(String.format(getString(R.string.neighborly_send_help_message), itemName));
        requestMessageEditor.setHint(String.format(getString(R.string.neighborly_send_help_message), itemName));
        TextView intro = popupRequestDialog.findViewById(R.id.intro);
        requestMessageEditor.requestFocus();
        if (foundItems.size() == 0) {
            Button btn = popupRequestDialog.findViewById(R.id.newRequestButton);
            btn.setText("Ask everyone in a new request");
            intro.setText(notFoundIntroText);
            popupRequestDialog.findViewById(R.id.foundNeighbors).setVisibility(View.GONE);
        } else {
            popupRequestDialog.findViewById(R.id.createNewRequest).setVisibility(View.GONE);
            popupRequestDialog.findViewById(R.id.newRequestButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popupRequestDialog.findViewById(R.id.createNewRequest).setVisibility(View.VISIBLE);
                }
            });

            intro.setText(foundIntroText);
            ListView neighborsListView = popupRequestDialog.findViewById(R.id.foundNeighbors);
            neighborsListView.setAdapter(new SearchResultListAdapter(getActivity(), foundItems, getContext(), itemName, popupRequestDialog));
        }

        Button sendButton = popupRequestDialog.findViewById(R.id.share_request);
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

                    searchText.setText("");
                    popupRequestDialog.dismiss();
                }
            }
        });

        TextView textClose = popupRequestDialog.findViewById(R.id.txtClose);
        textClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupRequestDialog.dismiss();
            }
        });

        popupRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupRequestDialog.show();
    }

    // -------------- My requests --------------

    private void addMyRequestsButtons(FlexboxLayout layoutRecepient) {
        layoutRecepient.removeAllViews();
        if (userOpenRequests.size() > 4) {
            ViewGroup.LayoutParams params = layoutRecepient.getLayoutParams();
            params.height = 160;
            layoutRecepient.setLayoutParams(params);
        }
        for (int i = 0 ; i < userOpenRequests.size() ; i++) {
            final RequestModel request = userOpenRequests.get(i);
            if (request != null) {
                Button button = new Button(feedView.getContext());
                button.setText(request.getItemPresentedName());
                Space space = new Space(feedView.getContext());

                createButton(button,request, space);
                layoutRecepient.addView(button);
                layoutRecepient.addView(space);

                if(i == 4){
                    Space lineBrake = new Space(feedView.getContext());
                    lineBrake.setMinimumWidth(layoutRecepient.getMinimumWidth());
                    lineBrake.setMinimumHeight(90);
                    layoutRecepient.addView(lineBrake);
                }
            }
        }
    }

    private void createButton(Button button, final RequestModel request, Space space){
        button.setLayoutParams(new LinearLayout.LayoutParams(130, 60));
        button.setAllCaps(false);
        button.setTextSize(14);

        int size_h = 13;
        int size_w = 20;
        button.setMinHeight(size_h);
        button.setMinWidth(size_w);
        button.setMinimumHeight(size_h);
        button.setMinimumWidth(size_w);
        button.setTextColor(getResources().getColor(R.color.white));
        button.setPadding(size_w,size_h,size_w,size_h);
        button.setBackground(ContextCompat.getDrawable(feedView.getContext(), R.drawable.rectangle_magenta));

        space.setMinimumWidth(17);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FeedFragment.this.getActivity(), RequestActivity.class);
                intent.putExtra("requestType", RequestActivity.REQUEST_ITEM);
                intent.putExtra("requestId", request.getRequestId());
                FeedFragment.this.getActivity().startActivity(intent);
            }
        });
    }

    // -------------- Other's requests --------------

    private void addRequestsUnderBuildingInDB(RequestModel newRequest) {
        // update the building model
        BuildingModel currentBuilding = BuildingModelDataHolder.getInstance().getCurrentBuilding();
        currentBuilding.addRequestToList(newRequest);
        BuildingModelDataHolder.getInstance().setCurrentBuilding(currentBuilding);
    }

    private void separateRequestsInBuilding() {
        userOpenRequests = new ArrayList<>();
        neighborsOpenRequests = new ArrayList<>();
        for (RequestModel request : curBuilding.getRequestList()) {
            if (request == null || request.isResolved()) {
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
