package com.useresponse.useresponseui.grouped_list;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class GroupedListAdapter extends ArrayAdapter {
    private ArrayList<GroupedListItem> items;
    private LayoutInflater inflater;
    private int sectionLayout;
    private int itemLayout;
    private int sectionTitle;
    private int itemTitle;
    private int sectionImage;
    private int itemImage;

    public GroupedListAdapter(
            Context context,
            int sectionLayout,
            int itemLayout,
            int sectionTitle,
            int itemTitle,
            int sectionImage,
            int itemImage,
            ArrayList<GroupedListItem> items
    ) {
        super(context, 0, items);

        this.items         = items;
        this.sectionLayout = sectionLayout;
        this.itemLayout    = itemLayout;
        this.sectionTitle  = sectionTitle;
        this.itemTitle     = itemTitle;
        this.sectionImage  = sectionImage;
        this.itemImage     = itemImage;
        inflater           = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        final GroupedListItem item = (GroupedListItem)items.get(position);

        if (item != null) {
            TextView title;
            ImageView image;

            if (item.isSection()) {
                view = inflater.inflate(sectionLayout, null);
                view.setOnClickListener(null);
                view.setOnLongClickListener(null);
                view.setLongClickable(false);
                title = (TextView) view.findViewById(sectionTitle);
                image = (ImageView) view.findViewById(sectionImage);
            } else {
                view = inflater.inflate(itemLayout, null);
                title = (TextView) view.findViewById(itemTitle);
                image = (ImageView) view.findViewById(itemImage);
            }

            if (title != null && item.getTitle() != null && item.getTitle().length() > 0) {
                title.setText(item.getTitle());
            }

            if (image != null && item.getImage() > 0) {
                image.setImageResource(item.getImage());
            }
        }

        return view;
    }
}
