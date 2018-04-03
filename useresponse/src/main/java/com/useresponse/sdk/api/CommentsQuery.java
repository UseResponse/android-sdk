package com.useresponse.sdk.api;

public class CommentsQuery {
    private int objectId;

    public CommentsQuery(int objectId) {
        this.objectId = objectId;
    }

    public int getObjectId() {
        return objectId;
    }
}
