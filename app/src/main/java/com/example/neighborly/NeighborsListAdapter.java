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
import com.google.android.flexbox.FlexboxLayout;
import java.util.ArrayList;
import java.util.List;

public class NeighborsListAdapter extends BaseAdapter {
    private List<UserModelFacade> neighborsList;
    private Context context;
    private Activity activity;

    public NeighborsListAdapter(Activity activity) {
        neighborsList = new ArrayList<>();
        this.activity = activity;
    }

    public NeighborsListAdapter(Activity activity, List<UserModelFacade> neighborsList, Context context) {
        this.neighborsList = neighborsList;
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
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.neighbors_details, parent, false);
        TextView name = row.findViewById(R.id.neighborName);
        TextView description = row.findViewById(R.id.neighborDesc);
        ImageView picture = row.findViewById(R.id.neighborsPic);
        UserModelFacade curNeighbor = neighborsList.get(position);
        name.setText(curNeighbor.getPresentedName());
        description.setText(curNeighbor.getDescription());

        Glide.with(context).load(curNeighbor.getImageUriString()).into(picture);

        setNeighborBadges(row, (FlexboxLayout) row.findViewById(R.id.neighborBadges), curNeighbor);

        row.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserModelFacade neighbor = neighborsList.get(position);
                Intent intent = new Intent(NeighborsListAdapter.this.activity, RequestActivity.class);
                intent.putExtra("requestType", RequestActivity.REQUEST_PRIVATE_CHAT);
                intent.putExtra("neighbor", neighbor.getId());
                NeighborsListAdapter.this.activity.startActivity(intent);
            }
        });

        return (row);
    }

    private void setNeighborBadges(View row, FlexboxLayout layoutRecepient, UserModelFacade curNeighbor) {
        layoutRecepient.removeAllViews();
        List<Integer> neighborBadges = curNeighbor.getBadges();

        if (neighborBadges.isEmpty()){
            layoutRecepient.setVisibility(View.INVISIBLE);
            return;
        }

        for (final int badge : neighborBadges) {
            ImageView badgeImage = new ImageView(row.getContext());
            badgeImage.setImageResource(badge);
            layoutRecepient.addView(badgeImage);
        }
    }
}