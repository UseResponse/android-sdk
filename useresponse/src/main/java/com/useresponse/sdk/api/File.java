package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class File {
    private int id;
    private String name;
    private String ext;
    private String url;

    public File() {
    }

    public File(JSONObject object) throws JSONException {
        id = object.getInt("id");
        url = object.getString("directUrl");

        JSONObject nameData = object.getJSONObject("name");
        name = nameData.getString("full");
        ext = nameData.getString("ext");
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public boolean isImage() {
        if (ext == null || ext.length() == 0) {
            return false;
        }

        Map<String, Boolean> imgExt = new HashMap<>();
        imgExt.put("jpg", true);
        imgExt.put("jpeg", true);
        imgExt.put("png", true);
        imgExt.put("gif", true);
        imgExt.put("ico", true);
        imgExt.put("bmp", true);

        return imgExt.containsKey(ext) && imgExt.get(ext);
    }
}
