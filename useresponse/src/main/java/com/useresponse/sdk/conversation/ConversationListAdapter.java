package com.useresponse.sdk.conversation;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.useresponse.sdk.R;
import com.useresponse.sdk.utils.Cache;

import java.util.ArrayList;

public class ConversationListAdapter extends ArrayAdapter {
    private ArrayList<ConversationListItem> items;
    private LayoutInflater inflater;
    private Context context;

    public ConversationListAdapter(Context context, ArrayList<ConversationListItem> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                            Cache.getImagesLoader().process(item.getText(), incomingImage);
                            break;
                        default:
                            incomingImage.setVisibility(View.GONE);
                            outgoingImage.setVisibility(View.GONE);
                            outgoingText.setVisibility(View.GONE);
                            incomingText.setVisibility(View.VISIBLE);


                            String fileName = item.getFileName();
                            if (fileName != null && fileName.length() > 0) {
                                incomingText.setText(fileName);

                                incomingText.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getText()));
                                        ConversationListAdapter.this.context.startActivity(browserIntent);
                                    }
                                });
                            } else {
                                incomingText.setText(item.getText());
                            }
                    }

                    Cache.getPhotosLoader(context).process(item.getPhotoUrl(), rowPhoto);

                    break;
                case "outgoing":

                    switch (item.getContentType()) {
                        case "image":
                            outgoingText.setVisibility(View.GONE);
                            incomingText.setVisibility(View.GONE);
                            incomingImage.setVisibility(View.GONE);
                            outgoingImage.setVisibility(View.VISIBLE);
                            Cache.getImagesLoader().process(item.getText(), outgoingImage);
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
}
