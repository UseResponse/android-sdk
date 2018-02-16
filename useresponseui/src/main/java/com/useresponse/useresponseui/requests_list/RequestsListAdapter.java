package com.useresponse.useresponseui.requests_list;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.useresponse.useresponseui.R;

import java.io.InputStream;
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
        final RequestsListInterface item = (RequestsListInterface) items.get(position);

        if (item != null) {
            view = inflater.inflate(R.layout.request_row, null);
            TextView rowName = (TextView)view.findViewById(R.id.requestRowName);
            TextView rowDescription = (TextView)view.findViewById(R.id.requestRowDescription);
            TextView rowStatus = (TextView)view.findViewById(R.id.requestRowStatus);
            TextView rowDate = (TextView)view.findViewById(R.id.requestRowDate);
            ImageView rowPhoto = (ImageView)view.findViewById(R.id.requestPhoto);

            rowName.setText(item.getTitle());
            rowDescription.setText(item.getDescription());
            new DownloadImageTask(rowPhoto).execute(item.getPhotoUrl());

            if (item.getRowType().equals("ticket")) {
                rowDate.setVisibility(View.GONE);
                rowStatus.setVisibility(View.VISIBLE);

                RequestsListTicket ticket = (RequestsListTicket)item;
                rowStatus.setText(ticket.getStatus().getTitle());
                rowStatus.setTextColor(Color.parseColor(ticket.getStatus().getColor().getText()));
                rowStatus.setBackgroundColor(Color.parseColor(ticket.getStatus().getColor().getBackground()));
            }

            if (item.getRowType().equals("chat")) {
                rowStatus.setVisibility(View.GONE);
                rowDate.setVisibility(View.VISIBLE);
                rowDate.setText(new SimpleDateFormat("MMM dd", Locale.US).format(new Date(item.getUpdatedAt() * 1000L)));
            }
        }

        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("GoLog", e.getMessage());
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            RoundedBitmapDrawable roundedBitmap = RoundedBitmapDrawableFactory.create(context.getResources(), result);
            roundedBitmap.setCircular(true);
            bmImage.setImageDrawable(roundedBitmap);
        }
    }
}
