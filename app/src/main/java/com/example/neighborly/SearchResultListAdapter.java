package com.example.neighborly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchResultListAdapter extends BaseAdapter {
    List<UserModelFacade> neighborsList;
    Map<UserModelFacade,ItemModel> neighborsItemsMap;
    LayoutInflater inflater;
    Context context;
    Activity activity;

    public SearchResultListAdapter(Activity activity) {
        neighborsList = new ArrayList<>();
        this.activity = activity;
    }

    public SearchResultListAdapter(Activity activity, Map<UserModelFacade,ItemModel> map, Context context) {
        this.neighborsItemsMap = map;
        this.neighborsList = new ArrayList<>(map.keySet());
        this.context = context;
        this.activity = activity;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return neighborsList.size();
    }

    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.search_result_details, parent, false);

        UserModelFacade curNeighbor = neighborsList.get(position);
        Map<Integer, String> neighborMap = new HashMap<>();
        neighborMap.put(R.id.neighborName, curNeighbor.getPresentedName());
        setVales(row, neighborMap , R.id.neighborPic, curNeighbor.getImageUriString());

        ItemModel curItem = neighborsItemsMap.get(curNeighbor);
        Map<Integer, String> itemMap = new HashMap<>();
        itemMap.put(R.id.itemName, curItem.getName());
        itemMap.put(R.id.itemDesc, curItem.getDescription());
        setVales(row,itemMap, R.id.itemPic, curItem.getImageUriString());

        LinearLayout startConversation = row.findViewById(R.id.neighbor);
        startConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModelFacade neighbor = neighborsList.get(position);
                Intent intent = new Intent(SearchResultListAdapter.this.activity, RequestActivity.class);
                //intent.putExtra("context", RequestActivity.chatContext);
                intent.putExtra("neighbor", neighbor);
                SearchResultListAdapter.this.activity.startActivity(intent);
            }
        });
        return (row);
    }

    private void setVales(View row, Map<Integer, String> map, int picKey, String picValue){
        for(int key : map.keySet()){
            TextView keyView = row.findViewById(key);
            keyView.setText(map.get(key));
        }
        ImageView picture = row.findViewById(picKey);
        Glide.with(context).load(picValue).into(picture);
    }
}