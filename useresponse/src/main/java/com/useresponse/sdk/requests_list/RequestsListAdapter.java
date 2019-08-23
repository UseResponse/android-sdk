package com.useresponse.sdk.requests_list;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.useresponse.sdk.R;
import com.useresponse.sdk.utils.Cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class RequestsListAdapter extends ArrayAdapter {
    private ArrayList<RequestsListInterface> items;
    private LayoutInflater inflater;
    private Context context;

    public RequestsListAdapter(Context context, ArrayList<RequestsListInterface> items) {
        super(context, 0, items);
        this.items = items;
        this.context = context;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        final RequestsListInterface item = items.get(position);

        if (item != null) {
            view = inflater.inflate(R.layout.request_row, null);
            TextView rowName = view.findViewById(R.id.requestRowName);
            TextView rowDescription = view.findViewById(R.id.requestRowDescription);
            TextView rowStatus = view.findViewById(R.id.requestRowStatus);
            TextView rowDate = view.findViewById(R.id.requestRowDate);
            ImageView rowPhoto = view.findViewById(R.id.requestPhoto);

            rowName.setText(item.getTitle());
            rowDescription.setText(item.getDescription());
            Cache.getPhotosLoader(context).process(item.getPhotoUrl(), rowPhoto);

            /*if (item.getRowType().equals("ticket")) {
                rowDate.setVisibility(View.GONE);
                rowStatus.setVisibility(View.VISIBLE);

                RequestsListTicket ticket = (RequestsListTicket)item;
                rowStatus.setText(ticket.getStatus().getTitle());
                rowStatus.setTextColor(Color.parseColor(ticket.getStatus().getTextColor()));
                rowStatus.setBackgroundColor(Color.parseColor(ticket.getStatus().getBgColor()));
            }*/

            //if (item.getRowType().equals("chat")) {
                rowStatus.setVisibility(View.GONE);
                rowDate.setVisibility(View.VISIBLE);
                rowDate.setText(new SimpleDateFormat("MMM dd", Locale.US).format(new Date(item.getUpdatedAt() * 1000L)));
            //}
        }

        return view;
    }
}
