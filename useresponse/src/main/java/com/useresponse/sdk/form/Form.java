package com.useresponse.sdk.form;

import java.util.ArrayList;

public class Form {
    private ArrayList<FormElementInterface> elements;
    private String lastError = null;

    public Form() {
        elements = new ArrayList<>();
    }

    public void addElement(FormElementInterface element) {
        elements.add(element);
    }

    public ArrayList<FormElementInterface> getElements() {
        return elements;
    }

    public boolean isValid() {
        lastError = null;
        boolean result = true;

        for (FormElementInterface element : elements) {
            if (element.isInput()) {
                FormElementInputInterface input = (FormElementInputInterface)element;

                if (!input.isValid()) {
                    lastError = input.getLastError();
                    result = false;
                    break;
                }
            }
        }

        return result;
    }

    public String getLastError() {
        return lastError;
    }
}
