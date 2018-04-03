package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TicketForm {
    private Map<String, String> properties = new HashMap<>();

    public TicketForm setTitle(String title) {
        properties.put("title", title);
        return this;
    }

    public TicketForm setContent(String content) {
        properties.put("content", content);
        return this;
    }

    public TicketForm setCustomField(String field, String value) {
        properties.put(field, value);
        return this;
    }

    public String toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("ownership", "helpdesk");
        object.put("object_type", "ticket");

        for (Map.Entry<String, String> entry : properties.entrySet()) {
            object.put(entry.getKey(), entry.getValue());
        }

        return object.toString();
    }
}
