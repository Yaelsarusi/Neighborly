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
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class FeedFragment extends Fragment {
    private static final String introText= "Hi %1s, we found neighbors that have the item you were looking for!";
    private View feedView;
    private Dialog popupRequestDialog;
    private EditText searchText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        feedView = inflater.inflate(R.layout.fragment_feed, container, false);
        popupRequestDialog = new Dialog(this.getContext());

        ImageButton searchButton = feedView.findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = feedView.findViewById(R.id.SearchText);
                showPopup(v, searchForItem(searchText.getText().toString()));
            }
        });

        return feedView;
    }

    private Map<UserModelFacade,ItemModel> searchForItem(String itemToSearch) {
        final String cleanedSearch = ItemModel.cleanItemName(itemToSearch);
        final Map<UserModelFacade,ItemModel> foundItems = new HashMap<>();

        BuildingModel building = BuildingModelDataHolder.getInstance().getCurrentBuilding();
        List<ItemModel> buildingItems = building.getItemsList();
        if (buildingItems != null){
            for (UserModelFacade neighbor : BuildingModelDataHolder.getInstance().getCurrentBuilding().getUsersList()) {
                for (ItemModel item : buildingItems) {
                    if (neighbor != null && item != null && item.getOwnerId().equals(neighbor.getId())) {
                        if (item.getName().contains(cleanedSearch)) {
                            foundItems.put(neighbor, item);
                            break;
                        }
                    }
                }

            }
        }

        return foundItems;
    }

    private void showPopup(View view, Map<UserModelFacade,ItemModel> foundItems) {
        Button sendButton;
        popupRequestDialog.setContentView(R.layout.popup_add_request);

        TextView intro = popupRequestDialog.findViewById(R.id.intro);
        intro.setText(String.format(introText, UserModelDataHolder.getInstance().getCurrentUser().getPresentedName()));
        ListView neighborsListView = popupRequestDialog.findViewById(R.id.foundNeighbors);
        neighborsListView.setAdapter(new SearchResultListAdapter(getActivity(), foundItems, getContext()));

        sendButton = popupRequestDialog.findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), RequestActivity.class);
                // todo get real id
                //intent.putExtra("context", RequestActivity.itemRequestContext);
                intent.putExtra("itemId", 0);
                intent.putExtra("itemName", searchText.getText().toString());
                startActivity(intent);
                popupRequestDialog.dismiss();
            }
        });
        popupRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupRequestDialog.show();
    }
}
