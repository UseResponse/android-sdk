package com.useresponse.useresponseui.form;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.useresponse.useresponseui.R;
import com.useresponse.useresponseui.utils.MultiSpinner;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FormListAdapter extends ArrayAdapter {
    private ArrayList<FormElementInterface> items;
    private LayoutInflater inflater;
    private Context context;

    public FormListAdapter(Context context, ArrayList<FormElementInterface> items) {
        super(context, 0, items);
        this.context = context;
        this.items = items;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        final FormElementInterface item = (FormElementInterface) items.get(position);

        if (item != null) {
            switch (item.getType()) {
                case "text":
                    view = inflater.inflate(R.layout.form_element_input_text, null);
                    final FormElementInputText formInputText = (FormElementInputText)item;
                    final EditText text = (EditText)view.findViewById(R.id.formElementInputText);
                    text.setHint(formInputText.getHint());

                    if (formInputText.isMultiLine()) {
                        text.setSingleLine(false);
                        text.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
                    }

                    if (formInputText.isDate()) {
                        final Calendar myCalendar = Calendar.getInstance();
                        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                myCalendar.set(Calendar.YEAR, year);
                                myCalendar.set(Calendar.MONTH, monthOfYear);
                                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                String myFormat = "yyyy-MM-dd";
                                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                                text.setText(sdf.format(myCalendar.getTime()));
                            }

                        };

                        text.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                new DatePickerDialog(
                                        context,
                                        date,
                                        myCalendar.get(Calendar.YEAR),
                                        myCalendar.get(Calendar.MONTH),
                                        myCalendar.get(Calendar.DAY_OF_MONTH)
                                ).show();
                            }
                        });

                        text.setFocusable(false);
                    }

                    text.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            formInputText.setValue(s.toString());
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    break;
                case "spinner":
                    view = inflater.inflate(R.layout.form_element_input_spinner, null);
                    final FormElementInputSpinner formInputSpinner = (FormElementInputSpinner)item;
                    Spinner spinner = (Spinner)view.findViewById(R.id.formElementInputSpinner);
                    spinner.setAdapter(formInputSpinner.getAdapter());
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            formInputSpinner.setValue(position);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            formInputSpinner.setValue(0);
                        }
                    });
                    break;
                case "spinner_multi":
                    view = inflater.inflate(R.layout.form_element_input_spinner_multi, null);
                    final FormElementInputSpinnerMulti formInputSpinnerMulti = (FormElementInputSpinnerMulti)item;
                    MultiSpinner multiSpinner = (MultiSpinner)view.findViewById(R.id.formElementInputSpinnerMulti);
                    multiSpinner.setItems(formInputSpinnerMulti.getOptionTitles(), formInputSpinnerMulti.getTitle());
                    multiSpinner.setListener(new MultiSpinner.MultiSpinnerListener() {
                        @Override
                        public void onItemsSelected(boolean[] selected) {
                            for (int i = 0; i < selected.length; i++) {
                                formInputSpinnerMulti.setValue(i, selected[i]);
                            }
                        }
                    });
                    break;
                case "button":
                    view = inflater.inflate(R.layout.form_element_button, null);
                    FormElementButton formButton = (FormElementButton)item;
                    Button button = (Button)view.findViewById(R.id.formElementButton);
                    button.setText(formButton.getTitle());
                    button.setEnabled(formButton.isEnabled());

                    if (formButton.getListener() != null) {
                        button.setOnClickListener(formButton.getListener());
                    }
                    break;
            }
        }

        return view;
    }
}
