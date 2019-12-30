package com.example.neighborly;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Integer[] colors = null;
    ArrayList<Request> privateChats;
    ViewPager privateChatCarouselViewPager;
    PrivateChatAdapter privateChatCarouselAdapter;
    int privateChatCarouselPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(listener);

        //The if statement is to prevent initializing when the device is rotated
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new FeedFragment()).commit();
        }

        Button searchButton = findViewById(R.id.searchButton);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                MainActivity.this.searchAction();
            }
        });

        createPrivateChatCarousel();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener listener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selected = null;

            switch (menuItem.getItemId()){
                case R.id.nev_feed:
                    selected = new FeedFragment();
                    break;
                case R.id.nev_profile:
                    selected = new PrifileFragment();
                    break;
                case R.id.nev_neighbors:
                    selected = new NeighboursFragment();
                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    selected).commit();
            return true;
        }
    };



    protected void searchAction(){
        EditText text = findViewById(R.id.SearchText);
        String searchText = text.getText().toString();

        // Todo - Add Firebase search here + popup like in the design
        ArrayList<Item> itemList = new ArrayList<Item>();
        boolean wasFound = searchDatabase(itemList, searchText);

    }

    protected boolean searchDatabase(ArrayList<Item> itemList, String searchText){
        // Todo - Retrieve items in the building, match the search text.
        //  Return True if found matching (and add them to the list).

        return false;

    }

    protected void createPopup(ArrayList<Item> itemList) {

        // https://www.awsrh.com/2017/10/custom-pop-up-window-with-android-studio.html
    }


    private void createPrivateChatCarousel() {
        this.privateChats = fetchAllPrivateChatsFromDB();
        privateChatCarouselAdapter = new PrivateChatAdapter(privateChats, this, false);
        privateChatCarouselViewPager = findViewById(R.id.privateChats);
        privateChatCarouselViewPager.setAdapter(privateChatCarouselAdapter);
        privateChatCarouselPosition = 0;

        privateChatCarouselViewPager.setPadding(300, 0, 300, 0);

        privateChatCarouselViewPager.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // Edit button is shown only if the item is a burger, therefore we can assume at this point the item in the orderCarouselPosition is a burger.
                Request curChat = (Request) MainActivity.this.privateChats.get(MainActivity.this.privateChatCarouselPosition);
                Intent intent = new Intent(MainActivity.this, RequestActivity.class);
                intent.putExtra("curChat", curChat);
                startActivity(intent);
            }
        });

        privateChatCarouselViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //position of the selected item
                MainActivity.this.privateChatCarouselPosition = position;
            }

            @Override
            public void onPageSelected(int position) {
                //position of the selected item
                MainActivity.this.privateChatCarouselPosition = position % privateChats.size();
            }

            @Override
            public void onPageScrollStateChanged(int state) { }

        });
    }

    //Todo - Implement this
    private ArrayList<Request> fetchAllPrivateChatsFromDB() {
        return new ArrayList<Request>();
    }

}
