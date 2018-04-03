package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class CustomFieldOption {
    private String title;
    private String value;

    public CustomFieldOption() {
    }

    public CustomFieldOption(JSONObject object) throws JSONException {
        title = object.getString("title");
        value = object.getString("value");
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}
