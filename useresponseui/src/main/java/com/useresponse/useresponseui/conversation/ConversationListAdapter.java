package com.useresponse.useresponseui.conversation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.useresponse.useresponseui.R;

import java.io.InputStream;
import java.util.ArrayList;

public class ConversationListAdapter extends ArrayAdapter {
    private ArrayList<ConversationListItem> items;
    private LayoutInflater inflater;
    private Context context;
    private Photos photos;

    public ConversationListAdapter(Context context, ArrayList<ConversationListItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        photos = new Photos(context);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        final ConversationListItem item = (ConversationListItem) items.get(position);

        if (item != null) {
            view = inflater.inflate(R.layout.request_message, null);
            TextView incomingText = (TextView)view.findViewById(R.id.requestIncomingMessageText);
            TextView outgoingText = (TextView)view.findViewById(R.id.requestOutgoingMessageText);
            ImageView rowPhoto = (ImageView)view.findViewById(R.id.requestIncomingMessagePhoto);
            ImageView incomingImage = (ImageView)view.findViewById(R.id.requestIncomingMessageImage);
            ImageView outgoingImage = (ImageView)view.findViewById(R.id.requestOutgoingMessageImage);

            switch (item.getType()) {
                case "incoming":

                    switch (item.getContentType()) {
                        case "image":
                            outgoingImage.setVisibility(View.GONE);
                            outgoingText.setVisibility(View.GONE);
                            incomingText.setVisibility(View.GONE);
                            incomingImage.setVisibility(View.VISIBLE);
                            new DownloadImageTask(incomingImage).execute(item.getText());
                            break;
                        default:
                            incomingImage.setVisibility(View.GONE);
                            outgoingImage.setVisibility(View.GONE);
                            outgoingText.setVisibility(View.GONE);
                            incomingText.setVisibility(View.VISIBLE);
                            incomingText.setText(item.getText());
                    }

                    photos.process(item.getPhotoUrl(), rowPhoto);

                    break;
                case "outgoing":

                    switch (item.getContentType()) {
                        case "image":
                            outgoingText.setVisibility(View.GONE);
                            incomingText.setVisibility(View.GONE);
                            incomingImage.setVisibility(View.GONE);
                            outgoingImage.setVisibility(View.VISIBLE);
                            new DownloadImageTask(outgoingImage).execute(item.getText());
                            break;
                        default:
                            incomingImage.setVisibility(View.GONE);
                            outgoingImage.setVisibility(View.GONE);
                            incomingText.setVisibility(View.GONE);
                            outgoingText.setVisibility(View.VISIBLE);
                            outgoingText.setText(item.getText());
                    }

                    rowPhoto.setVisibility(View.GONE);
                    break;
                default:
                    return view;
            }

            view.setOnClickListener(null);
            view.setOnLongClickListener(null);
            view.setLongClickable(false);
        }

        return view;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        private ImageView bmImage;
        private String url;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            url = urls[0];
            Bitmap mIcon11 = null;

            try {
                InputStream in = new java.net.URL(url).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("GoLog", e.getMessage());
            }

            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
}
