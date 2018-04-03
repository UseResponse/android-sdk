package com.useresponse.sdk.form;

abstract public class FormElementInputAbstract implements FormElementInputInterface {
    private boolean required = false;
    private String name;
    private String title;
    protected String lastError = null;

    public FormElementInputAbstract(String name, String title) {
        this.name = name;
        this.title = title;
    }

    @Override
    public boolean isInput() {
        return true;
    }

    @Override
    public boolean isRequired() {
        return required;
    }

    @Override
    public String getName() {
        return name;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public String getLastError() {
        return lastError;
    }

    public String getTitle() {
        return title;
    }
}
