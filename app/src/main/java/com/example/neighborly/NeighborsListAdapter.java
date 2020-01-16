package com.example.neighborly;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class NeighborsListAdapter extends BaseAdapter {
    List<UserModelFacade> neighborsList;
    LayoutInflater inflater;
    Context context;

    public NeighborsListAdapter() {
        neighborsList = new ArrayList<>();
    }

    public NeighborsListAdapter(List<UserModelFacade> neighborsList, Context context) {
        this.neighborsList = neighborsList;
        this.context = context;
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

    public View getView(int position, View convertView, ViewGroup parent) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row;
        row = inflater.inflate(R.layout.neighbors_details, parent, false);
        TextView name = row.findViewById(R.id.neighborName);
        TextView description = row.findViewById(R.id.neighborDesc);
        ImageView picture = row.findViewById(R.id.neighborsPic);
        UserModelFacade curNeighbor = neighborsList.get(position);
        name.setText(curNeighbor.getUserPresentedName());
        description.setText(curNeighbor.getDescription());

        // Uncomment after merge with main branch:
        //Picasso.get().load(curNeighbor.getImageUriString()).into(picture);
        return (row);
    }
}