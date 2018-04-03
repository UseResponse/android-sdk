package com.useresponse.sdk.form;

public interface FormElementInputInterface extends FormElementInterface {
    public boolean isRequired();
    public boolean isValid();
    public String getName();
    public String getValue();
    public String getLastError();
}
