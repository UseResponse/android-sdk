package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class FileForm {
    private String name;
    private String content;

    public FileForm(String name, String content) {
        this.name = name;
        this.content = content;
    }

    public String getName() {
        return name;
    }

    public String toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("name", name);
        object.put("content", content);

        return object.toString();
    }
}
