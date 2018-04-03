package com.useresponse.sdk.form;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

public class FormElementInputSpinnerMulti extends FormElementInputAbstract implements FormElementInputInterface {
    private List<String> optionValues;
    private List<String> optionTitles;
    private List<Boolean> optionState;

    public FormElementInputSpinnerMulti(String name, String title) {
        super(name, title);
        optionValues = new ArrayList<String>();
        optionTitles = new ArrayList<String>();
        optionState = new ArrayList<Boolean>();
    }

    @Override
    public boolean isValid() {
        if (isRequired() && getValue().length() == 0) {
            lastError = getTitle() + " is required";
            return false;
        }

        lastError = null;
        return true;
    }

    @Override
    public String getValue() {
        List<String> result = new ArrayList<String>();

        for (int i = 0; i < optionState.size(); i++) {
            if (optionState.get(i)) {
                result.add(optionValues.get(i));
            }
        }

        return TextUtils.join("|", result);
    }

    @Override
    public String getType() {
        return "spinner_multi";
    }

    public void addOption(String value, String title) {
        optionValues.add(value);
        optionTitles.add(title);
        optionState.add(false);
    }

    public List<String> getOptionTitles() {
        return optionTitles;
    }

    public void setValue(int i, boolean val) {
        optionState.set(i, val);
    }
}
