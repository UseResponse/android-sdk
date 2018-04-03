package com.useresponse.sdk.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class CommentForm {
    private int objectId;
    private String content;
    private Map<String, String> files = new HashMap<>();

    public CommentForm(int objectId) {
        this.objectId = objectId;
    }

    public CommentForm setContent(String content) {
        this.content = content;
        return this;
    }

    public CommentForm attachFile(String token, String name) {
        files.put(token, name);
        return this;
    }

    public String toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("object_id", objectId);
        object.put("content", content);

        JSONArray filesArray = new JSONArray();

        for (Map.Entry<String, String> entry : files.entrySet()) {
            JSONObject fileObject = new JSONObject();
            fileObject.put("token", entry.getKey());
            fileObject.put("name", entry.getValue());
            filesArray.put(fileObject);
        }

        if (filesArray.length() > 0) {
            object.put("attachments", filesArray);
        }

        return object.toString();
    }
}
