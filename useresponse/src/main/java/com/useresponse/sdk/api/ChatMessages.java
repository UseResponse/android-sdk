package com.useresponse.sdk.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ChatMessages extends PaginationAbstract {
    private ArrayList<ChatMessage> messages = new ArrayList<>();

    public ChatMessages() {
        super();
    }

    public ChatMessages(JSONObject object) throws JSONException {
        super(object);

        if (!object.isNull("data")) {
            JSONArray responseChatMessages = object.getJSONArray("data");

            for (int i = 0; i < responseChatMessages.length(); i++) {
                messages.add(new ChatMessage(responseChatMessages.getJSONObject(i)));
            }
        }
    }

    public ArrayList<ChatMessage> getMessages() {
        return messages;
    }
}
