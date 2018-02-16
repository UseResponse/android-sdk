package com.useresponse.useresponseui.form;

public class FormElementInputText extends FormElementInputAbstract implements FormElementInputInterface {
    private String value = "";
    private boolean multiLine = false;
    private boolean date = false;
    private String hint = "";
    private int minLength = 0;
    private int maxLength = 0;

    public FormElementInputText(String name, String title) {
        super(name, title);
    }

    @Override
    public boolean isValid() {
        if (isRequired() && value.length() == 0) {
            lastError = getTitle() + " is required";
            return false;
        }

        if (minLength > 0 && value.length() < minLength) {
            lastError = getTitle() + " is too short";
            return false;
        }

        if (maxLength > 0 && value.length() > maxLength) {
            lastError = getTitle() + " is too long";
            return false;
        }

        lastError = null;
        return true;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value.trim();
    }

    @Override
    public String getType() {
        return "text";
    }

    public boolean isMultiLine() {
        return multiLine;
    }

    public void setMultiLine(boolean multiLine) {
        this.multiLine = multiLine;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getHint() {
        return hint;
    }

    public boolean isDate() {
        return date;
    }

    public void setDate(boolean date) {
        this.date = date;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }
}
