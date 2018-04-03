package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class PaginationAbstract {
    private int totalObjects = 0;
    private int objectsPerPage = 0;
    private int totalPages = 0;
    private int currentPage = 1;

    public PaginationAbstract() {
    }

    public PaginationAbstract(JSONObject object) throws JSONException {
        totalObjects = object.getInt("totalObjects");
        objectsPerPage = object.getInt("objectsPerPage");
        totalPages = object.getInt("totalPages");
        currentPage = object.getInt("currentPage");
    }

    public int getTotalObjects() {
        return totalObjects;
    }

    public int getObjectsPerPage() {
        return objectsPerPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getCurrentPage() {
        return currentPage;
    }
}
