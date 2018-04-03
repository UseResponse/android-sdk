package com.useresponse.sdk.api;

public class ArticlesQuery {
    private String type;
    private int category = -1;
    private String search;
    private boolean featured = false;
    private int forum = 0;

    public ArticlesQuery setType(String type) {
        this.type = type;
        return this;
    }

    public String getType() {
        return type != null ? type : "";
    }

    public ArticlesQuery setCategory(int category) {
        this.category = category;
        return this;
    }

    public int getCategory() {
        return category;
    }

    public ArticlesQuery setSearch(String search) {
        this.search = search;
        return this;
    }

    public String getSearch() {
        return search != null ? search : "";
    }

    public ArticlesQuery setFeatured(boolean featured) {
        this.featured = featured;
        return this;
    }

    public boolean isFeatured() {
        return featured;
    }

    public ArticlesQuery setForum(int forum) {
        this.forum = forum;
        return this;
    }

    public int getForum() {
        return forum;
    }
}
