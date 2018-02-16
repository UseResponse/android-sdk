package com.useresponse.useresponseui;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.useresponse.useresponseui.form.Form;
import com.useresponse.useresponseui.form.FormElementButton;
import com.useresponse.useresponseui.form.FormElementInputAbstract;
import com.useresponse.useresponseui.form.FormElementInputSpinner;
import com.useresponse.useresponseui.form.FormElementInputSpinnerMulti;
import com.useresponse.useresponseui.form.FormElementInputText;
import com.useresponse.useresponseui.form.FormElementInterface;
import com.useresponse.useresponseui.form.FormListAdapter;
import com.useresponse.useresponseui.utils.Cache;
import com.useresponse.useresponseui.utils.UseResponse;

import useresponseapi.ModelCustomField;
import useresponseapi.ModelCustomFieldIterator;
import useresponseapi.ModelCustomFieldOption;
import useresponseapi.ModelCustomFieldOptionIterator;
import useresponseapi.ModelTicket;
import useresponseapi.ModelTicketIterator;
import useresponseapi.TicketForm;
import useresponseapi.TicketsQuery;
import useresponseapi.Useresponseapi;

public class CreateTicketActivity extends AppCompatActivity {

    FormElementInputText subject;
    FormElementInputText body;
    FormElementButton submit;
    ListView formList;
    ProgressBar loader;
    FormListAdapter adapter;
    Form form;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_ticket);
        setTitle(getString(R.string.create_ticket_header));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!UseResponse.init(this)) {
            return;
        }

        form = new Form();
        formList = (ListView)findViewById(R.id.createTicketFormList);
        loader = (ProgressBar)findViewById(R.id.createTicketLoader);

        subject = new FormElementInputText("title", getString(R.string.subject));
        subject.setHint(getString(R.string.subject));
        subject.setRequired(true);
        subject.setMinLength(3);
        subject.setMaxLength(100);
        form.addElement(subject);

        body = new FormElementInputText("content", getString(R.string.description));
        body.setHint(getString(R.string.description));
        body.setMultiLine(true);
        form.addElement(body);

        if (Cache.getTicketCustomFields() != null) {
            addCustomFields();
            addSubmitButton();
            loader.setVisibility(View.GONE);
            formList.setVisibility(View.VISIBLE);
            adapter = new FormListAdapter(this, form.getElements());
            formList.setAdapter(adapter);
        } else {
            formList.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
            (new LoadCustomFieldsTask()).execute();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addSubmitButton() {
        if (submit == null) {
            submit = new FormElementButton();
            submit.setTitle(getString(R.string.submit));

            submit.setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (form.isValid()) {
                        (new CreateTicketTask()).execute();
                    } else {
                        Toast.makeText(CreateTicketActivity.this, form.getLastError(), Toast.LENGTH_LONG).show();
                    }
                }
            });

            form.addElement(submit);
        }
    }

    private void addCustomFields() {
        ModelCustomFieldIterator fields = Cache.getTicketCustomFields();

        if (fields != null) {
            while (fields.isValid()) {
                ModelCustomField field = fields.getNext();

                switch (field.getType()) {
                    case "text":
                    case "date":
                        FormElementInputText fieldText = new FormElementInputText(field.getSlug(), field.getTitle());
                        fieldText.setHint(field.getTitle());

                        if (field.getType().equals("date")) {
                            fieldText.setDate(true);
                        }

                        fieldText.setRequired(field.getIsRequired() > 0);
                        form.addElement(fieldText);
                        break;
                    case "select":
                        FormElementInputSpinner fieldSpinner = new FormElementInputSpinner(field.getSlug(), field.getTitle());
                        ModelCustomFieldOptionIterator spinnerOptions = field.getOptions();

                        fieldSpinner.addOption("", field.getTitle());

                        while (spinnerOptions.isValid()) {
                            ModelCustomFieldOption option = spinnerOptions.getNext();
                            fieldSpinner.addOption(option.getValue(), option.getTitle());
                        }

                        fieldSpinner.initAdapter(CreateTicketActivity.this);
                        fieldSpinner.setRequired(field.getIsRequired() > 0);
                        form.addElement(fieldSpinner);
                        break;
                    case "checkbox":
                        FormElementInputSpinnerMulti fieldSpinnerMulti = new FormElementInputSpinnerMulti(field.getSlug(), field.getTitle());
                        ModelCustomFieldOptionIterator spinnerMultiOptions = field.getOptions();

                        while (spinnerMultiOptions.isValid()) {
                            ModelCustomFieldOption option = spinnerMultiOptions.getNext();
                            fieldSpinnerMulti.addOption(option.getValue(), option.getTitle());
                        }

                        fieldSpinnerMulti.setRequired(field.getIsRequired() > 0);
                        form.addElement(fieldSpinnerMulti);
                        break;
                }
            }
        }
    }

    private class LoadCustomFieldsTask extends AsyncTask<Void, Void, Void> {
        ModelCustomFieldIterator fields;

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                fields = Useresponseapi.getCustomFields("tickets");
            } catch (Exception e) {
                Log.e("GoLog", e.getMessage());
                fields = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (fields != null) {
                Cache.setTicketCustomFields(fields);
                addCustomFields();
            }

            addSubmitButton();
            loader.setVisibility(View.GONE);
            formList.setVisibility(View.VISIBLE);
            adapter = new FormListAdapter(CreateTicketActivity.this, form.getElements());
            formList.setAdapter(adapter);
        }
    }

    private class CreateTicketTask extends AsyncTask<Void, Void, Void> {

        String error = null;
        int ticketId;

        @Override
        protected void onPreExecute() {
            formList.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                UseResponse.initIdentity(CreateTicketActivity.this, true);

                TicketForm ticketForm = new TicketForm();
                //ticketForm.setTitle(subject.getValue());
                //ticketForm.setContent(body.getValue());

                for (FormElementInterface element : form.getElements()) {
                    if (element.isInput()) {
                        FormElementInputAbstract input = (FormElementInputAbstract)element;
                        ticketForm.setCustomField(input.getName(), input.getValue());
                    }
                }

                ModelTicket ticket = Useresponseapi.createTicket(ticketForm);

                if (ticket == null || ticket.getId() <= 0) {
                    throw new Exception("Unable to create ticket");
                }

                ticketId = (int)ticket.getId();

                TicketsQuery query = new TicketsQuery();
                query.setPage(1);

                ModelTicketIterator tickets = Useresponseapi.getTickets(query);
                Cache.setAllTickets(tickets);
                RequestsActivity.needRefresh = true;
            } catch (Exception e) {
                Log.e("GoLog", e.getMessage());
                error = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            loader.setVisibility(View.GONE);
            formList.setVisibility(View.VISIBLE);

            if (error == null) {
                Intent intent = new Intent(CreateTicketActivity.this, RequestActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_TASK_ON_HOME);
                intent.putExtra("requestType", "ticket");
                intent.putExtra("requestId", ticketId);
                CreateTicketActivity.this.startActivity(intent);
                CreateTicketActivity.this.finish();
            } else {
                Toast.makeText(CreateTicketActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
