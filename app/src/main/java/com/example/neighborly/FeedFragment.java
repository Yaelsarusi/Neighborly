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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class FeedFragment extends Fragment {
    private View feedView;
    private Dialog popupRequestDialog;
    private EditText searchText;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupRequestDialog = new Dialog(this.getContext());
        feedView = inflater.inflate(R.layout.fragment_feed, container, false);

        ImageButton searchButton = feedView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchText = feedView.findViewById(R.id.SearchText);
                ArrayList<ItemModel> itemsFound = searchForItem(searchText.getText().toString());
                showPopup(v, itemsFound);
            }
        });

        return feedView;
    }


    private ArrayList<ItemModel> searchForItem(String itemToSearch) {
        final String cleanedSearch = ItemModel.cleanItemName(itemToSearch);
        final ArrayList<ItemModel> foundItems = new ArrayList<>();

        BuildingModel building = BuildingModelDataHolder.getInstance().getCurrentBuilding();
        List<ItemModel> buildingitems = building.getItemsList();
        if (buildingitems != null){
            for (ItemModel item : buildingitems) {
                if (item.getName().contains(cleanedSearch)){
                    foundItems.add(item);
                }
            }
        }

        return foundItems;
    }

    private void showPopup(View view, ArrayList<ItemModel> itemsFound) {
        Button sendButton;
        popupRequestDialog.setContentView(R.layout.popup_add_request);

        StringBuilder allN = new StringBuilder();

        for (ItemModel i: itemsFound) {
            allN.append(i.getOwnerId());
        }
        TextView t = popupRequestDialog.findViewById(R.id.found_neighbors);
        t.setText(allN);

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
