package com.example.neighborly;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;

public class FeedFragment extends Fragment {

    Integer[] colors = null;
    ArrayList<RequestModel> privateChats;
    ViewPager privateChatCarouselViewPager;
    PrivateChatAdapter privateChatCarouselAdapter;
    int privateChatCarouselPosition;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_feed, container, false);
        Button searchButton = view.findViewById(R.id.searchButton);
        return view;
    }

    protected void createPopup(ArrayList<ItemModel> itemModelList) {
        // https://www.awsrh.com/2017/10/custom-pop-up-window-with-android-studio.html
    }

}
