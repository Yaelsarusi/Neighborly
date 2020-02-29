package com.example.neighborly;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import java.util.List;
import java.util.Map;

public class SearchResultListAdapter extends BaseAdapter {
    private List<UserModelFacade> neighborsList;
    private Map<UserModelFacade,ItemModel> neighborsItemsMap;
    private LayoutInflater inflater;
    private Context context;
    private Activity activity;
    private String itemName;
    private Dialog popupDialog;

    public SearchResultListAdapter(Activity activity) {
        neighborsList = new ArrayList<>();
        this.activity = activity;
    }

    public SearchResultListAdapter(Activity activity, Map<UserModelFacade,ItemModel> map, Context context, String itemName, Dialog popupDialog) {
        this.neighborsItemsMap = map;
        this.neighborsList = new ArrayList<>(map.keySet());
        this.context = context;
        this.activity = activity;
        this.itemName = itemName;
        this.popupDialog = popupDialog;
    }

    public int getCount() {
        return neighborsList.size();
    }

    public Object getItem(int arg0) {
        return null;
    }

    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("ViewHolder")
    public View getView(final int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.search_result_details, parent, false);

        final UserModelFacade curNeighbor = neighborsList.get(position);
        TextView neighborName = row.findViewById(R.id.neighborName);
        neighborName.setText(curNeighbor.getPresentedName()+"'s ");

        ItemModel curItem = neighborsItemsMap.get(curNeighbor);
        TextView itemNameText = row.findViewById(R.id.itemName);
        itemNameText.setText(curItem.getPresentedName());
        TextView itemDesc = row.findViewById(R.id.itemDesc);
        itemDesc.setText(curItem.getDescription());
        ImageView picture = row.findViewById(R.id.itemPic);
        Glide.with(context).load(curItem.getImageUriString()).into(picture);

        LinearLayout startConversation = row.findViewById(R.id.neighbor);
        startConversation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModelFacade neighbor = neighborsList.get(position);
                Intent intent = new Intent(SearchResultListAdapter.this.activity, RequestActivity.class);
                intent.putExtra("requestType", RequestActivity.REQUEST_PRIVATE_CHAT);
                intent.putExtra("neighbor", neighbor.getId());
                intent.putExtra("itemName", itemName);
                popupDialog.dismiss();
                SearchResultListAdapter.this.activity.startActivity(intent);

            }
        });
        return (row);
    }
}