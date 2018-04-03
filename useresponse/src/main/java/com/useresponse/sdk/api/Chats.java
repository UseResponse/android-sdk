package com.useresponse.sdk.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Chats extends PaginationAbstract {
    private ArrayList<Chat> chats = new ArrayList<>();

    public Chats() {
        super();
    }

    public Chats(JSONObject object) throws JSONException {
        super(object);

        if (!object.isNull("data")) {
            JSONArray responseChats = object.getJSONArray("data");

            for (int i = 0; i < responseChats.length(); i++) {
                chats.add(new Chat(responseChats.getJSONObject(i)));
            }
        }
    }

    public ArrayList<Chat> getChats() {
        return chats;
    }
}
