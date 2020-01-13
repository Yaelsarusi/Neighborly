package com.example.neighborly;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class FeedFragment extends Fragment {
    View feedView;
    Dialog popupRequestDialog;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        popupRequestDialog = new Dialog(this.getContext());
        feedView = inflater.inflate(R.layout.fragment_feed, container, false);

        Button searchButton = feedView.findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText searchText = feedView.findViewById(R.id.SearchText);
                ArrayList<ItemModel> n = searchForItem(searchText.getText().toString());
                showPopup(v);
            }
        });
        return feedView;
    }

    private ArrayList<ItemModel> searchForItem(String itemToSearch) {
        // todo - search for the item
        ArrayList<ItemModel> foundItems = new ArrayList<>();
        return foundItems;
    }

    private void showPopup(View v) {
        Button sandButton;
        popupRequestDialog.setContentView(R.layout.popup_add_request);
        sandButton = popupRequestDialog.findViewById(R.id.sand);
        sandButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupRequestDialog.dismiss();
            }
        });
        popupRequestDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popupRequestDialog.show();
    }

}
