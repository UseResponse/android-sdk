package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class TwilioVoice {
    private String identity;
    private String token;

    public TwilioVoice() {
    }

    public TwilioVoice(JSONObject object) throws JSONException {
        identity = object.getString("identity");
        token = object.getString("token");
    }

    public String getIdentity() {
        return identity;
    }

    public String getToken() {
        return token;
    }
}
