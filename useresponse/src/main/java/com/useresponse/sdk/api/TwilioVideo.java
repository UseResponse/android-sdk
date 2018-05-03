package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class TwilioVideo {
    private String identity;
    private String token;
    private String room;

    public TwilioVideo() {
    }

    public TwilioVideo(JSONObject object) throws JSONException {
        identity = object.getString("identity");
        token = object.getString("token");
        room = object.getString("room");
    }

    public String getIdentity() {
        return identity;
    }

    public String getToken() {
        return token;
    }

    public String getRoom() {
        return room;
    }
}
