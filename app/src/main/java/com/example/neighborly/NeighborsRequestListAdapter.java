package com.example.neighborly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class NeighborsRequestListAdapter extends BaseAdapter {
    List<RequestModel> requestList;
    LayoutInflater inflater;
    Context context;
    Activity activity;

    public NeighborsRequestListAdapter(Activity activity) {
        requestList = new ArrayList<>();
        this.activity = activity;
    }

    public NeighborsRequestListAdapter(Activity activity, List<RequestModel> requestList, Context context) {
        this.requestList = requestList;
        this.context = context;
        this.activity = activity;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return requestList.size();
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
        row = inflater.inflate(R.layout.request_details, parent, false);
        TextView name = row.findViewById(R.id.itemName);
        TextView description = row.findViewById(R.id.neighborRequestDesc);
        ImageView picture = row.findViewById(R.id.neighborsPic);
        RequestModel curRequest = requestList.get(position);
        name.setText(curRequest.getItemRequested());
        description.setText(curRequest.getRequestMsg());

        UserModelFacade curNeighbor = BuildingModelDataHolder.getInstance().getCurrentBuilding().getUserById(curRequest.getRequestUserId());
        Glide.with(context).load(curNeighbor.getImageUriString()).into(picture);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestModel request = requestList.get(position);
                Intent intent = new Intent(NeighborsRequestListAdapter.this.activity, RequestActivity.class);
                intent.putExtra("requestType", RequestActivity.REQUEST_ITEM);
                intent.putExtra("requestId", request.getRequestId());
                NeighborsRequestListAdapter.this.activity.startActivity(intent);
            }
        });
        return (row);
    }
}