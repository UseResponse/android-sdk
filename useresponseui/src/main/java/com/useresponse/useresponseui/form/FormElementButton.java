package com.useresponse.useresponseui.form;

import android.view.View;

public class FormElementButton implements FormElementInterface {
    private String title = "Button";
    private View.OnClickListener listener;
    private boolean enabled = true;

    @Override
    public boolean isInput() {
        return false;
    }

    @Override
    public String getType() {
        return "button";
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
