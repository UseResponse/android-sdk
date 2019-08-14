package com.useresponse.ursdktest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.useresponse.sdk.ArticlesActivity;
import com.useresponse.sdk.CategoriesActivity;
import com.useresponse.sdk.RequestsActivity;
import com.useresponse.sdk.api.Api;
import com.useresponse.sdk.api.IdentityData;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            IdentityData identityData = new IdentityData(
                    "b0ddbee99d8452b580373785fa2ad61bf6fe62139b5b2af772751f901eac64c8",
                    "cc402f30e1b93652bc4b849bbdc15d15@mobile"
            );
            identityData.setFirstName("Alex");
            identityData.setLastName("Mobile Tester");
            Api.setIdentityData(identityData);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() != null ? e.getMessage() : "Unknown error", Toast.LENGTH_LONG).show();
            return;
        }

        Button buttonCategories = (Button) findViewById(R.id.openCategories);
        buttonCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
                //intent.putExtra("type", "article");
                MainActivity.this.startActivity(intent);
            }
        });

        Button buttonArticles = (Button) findViewById(R.id.openArticles);
        buttonArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ArticlesActivity.class);
                intent.putExtra("type", "article");
                MainActivity.this.startActivity(intent);
            }
        });

        Button buttonTickets = (Button) findViewById(R.id.openTickets);
        buttonTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }
}
