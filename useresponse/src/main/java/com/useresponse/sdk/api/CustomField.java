package com.useresponse.sdk.api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CustomField {
    private int id;
    private String slug;
    private String type;
    private String title;
    private String description;
    private boolean isPrivate;
    private boolean isRequired;
    private ArrayList<CustomFieldOption> options = new ArrayList<>();

    public CustomField() {
    }

    public CustomField(JSONObject object) throws JSONException {
        id = object.getInt("id");
        slug = object.getString("slug");
        type = object.getString("type");
        title = object.getString("title");
        description = object.getString("description");
        isPrivate = object.getInt("is_private") > 0;
        isRequired = object.getInt("is_required") > 0;

        if (!object.isNull("options")) {
            JSONArray objectOptions = object.getJSONArray("options");

            for (int i = 0; i < objectOptions.length(); i++) {
                options.add(new CustomFieldOption(objectOptions.getJSONObject(i)));
            }
        }
    }

    public int getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getType() {
        return type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public boolean isRequired() {
        return isRequired;
    }

    public ArrayList<CustomFieldOption> getOptions() {
        return options;
    }
}
