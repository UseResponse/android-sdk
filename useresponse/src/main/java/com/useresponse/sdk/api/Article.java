package com.useresponse.sdk.api;

import org.json.JSONException;
import org.json.JSONObject;

public class Article {
    private int id;
    private String slug;
    private String title;
    private String content;
    private String url;
    private User author;
    private Type type;
    private Category category;

    public Article() {
    }

    public Article(JSONObject object) throws JSONException {
        this.id = object.getInt("id");
        this.slug = object.getString("slug");
        this.title = object.getString("title");
        this.content = object.getString("content");
        this.url = object.getString("url");
        this.author = new User(object.getJSONObject("author"));
        this.type = new Type(object.getJSONObject("type"));
        this.category = new Category(object.getJSONObject("category"));
    }

    public int getId() {
        return id;
    }

    public String getSlug() {
        return slug;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getUrl() {
        return url;
    }

    public User getAuthor() {
        return author;
    }

    public Type getType() {
        return type;
    }

    public Category getCategory() {
        return category;
    }
}
