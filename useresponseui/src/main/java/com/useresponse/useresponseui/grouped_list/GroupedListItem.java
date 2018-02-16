package com.useresponse.useresponseui.grouped_list;

public class GroupedListItem {
    private String title;
    private boolean section;
    private int image;

    public GroupedListItem(String title, boolean isSection, int image) {
        this.title = title;
        this.section = isSection;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public int getImage() {
        return image;
    }

    public boolean isSection() {
        return section;
    }
}
