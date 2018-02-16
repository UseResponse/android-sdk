package com.useresponse.ursdktest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.useresponse.useresponseui.ArticlesActivity;
import com.useresponse.useresponseui.CategoriesActivity;
import com.useresponse.useresponseui.RequestsActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonCategories = (Button) findViewById(R.id.openCategories);
        buttonCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
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
