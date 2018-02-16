package com.useresponse.useresponseui.form;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

public class FormElementInputSpinner extends FormElementInputAbstract implements FormElementInputInterface {
    private ArrayAdapter<String> adapter;
    private List<String> optionValues;
    private List<String> optionTitles;
    String value = "";

    public FormElementInputSpinner(String name, String title) {
        super(name, title);
        optionValues = new ArrayList<String>();
        optionTitles = new ArrayList<String>();
    }

    @Override
    public boolean isValid() {
        if (isRequired() && (getValue().equals("") || getValue().equals("0"))) {
            lastError = getTitle() + " is required";
            return false;
        }

        lastError = null;
        return true;
    }

    @Override
    public String getValue() {
        return value;
    }

    public void setValue(int position) {
        this.value = optionValues.get(position);
    }

    @Override
    public String getType() {
        return "spinner";
    }

    public ArrayAdapter<String> getAdapter() {
        return adapter;
    }

    public void initAdapter(Context context) {
        adapter = new ArrayAdapter<String>(
                context,
                android.R.layout.simple_spinner_item,
                getOptionTitles()

        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    }

    public void addOption(String value, String title) {
        optionValues.add(value);
        optionTitles.add(title);
    }

    public List<String> getOptionTitles() {
        return optionTitles;
    }
}
