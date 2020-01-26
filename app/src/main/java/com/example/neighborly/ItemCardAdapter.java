package com.example.neighborly;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ItemCardAdapter extends PagerAdapter {
    private int LOOPS_COUNT;
    private List<ItemModel> models;
    private Context context;

    public ItemCardAdapter(List<ItemModel> models, Context context, boolean infinite) {
        this.models = models;
        this.context = context;
        this.LOOPS_COUNT = infinite ? 1000 : 1;
    }

    @Override
    public int getCount() {
        if (models == null) {
            return 0;
        }
        return models.size() * LOOPS_COUNT;
    }

    // This is called when notifyDataSetChanged() is called
    @Override
    public int getItemPosition(Object object) {
        // refresh all fragments when data set changed
        int index = models.indexOf(object);
        if (index == -1) {
            return POSITION_NONE;
        }
        return index;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        int new_position = position % models.size();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.requests_card_item, container, false);

        ImageView imageView;

        imageView = view.findViewById(R.id.image);
        Picasso.get().load(Uri.parse(models.get(new_position).getImageUriString())).into(imageView);

        TextView title = view.findViewById(R.id.title);
        title.setText(models.get(new_position).getName());

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

}
