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
import com.useresponse.sdk.utils.Cache;
import com.useresponse.sdk.utils.UseResponse;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            IdentityData identityData = new IdentityData(
                    "d9d42888a65ac2aff33a2471f5a7de3238767c5561b44cd280f30c014dba6bd1",
                    "beldevoper@gmail.com"
            );
            identityData.setFirstName("Alex");
            identityData.setLastName("Mobile Tester");
            /*IdentityData identityData = new IdentityData(
                    "e261c5cb8d67802645987a35b5050c927976f43ebee69a4cac8c255ff95600f4",
                    "miss.lisa007@yandex.ru"
            );
            identityData.setFirstName("Miss");
            identityData.setLastName("Lisa");*/
            /*IdentityData identityData = new IdentityData(
                    "c70d55adde01752adb5fad66920e4ebffed4f94d56774fad780439749e4d4601",
                    "kurchik.sasha@mail.ru"
            );
            identityData.setFirstName("Alex");
            identityData.setLastName("Kurchik");*/
            Api.setIdentityData(identityData);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage() != null ? e.getMessage() : "Unknown error", Toast.LENGTH_LONG).show();
            return;
        }

        Button buttonCategories = findViewById(R.id.openCategories);
        buttonCategories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CategoriesActivity.class);
                //intent.putExtra("type", "article");
                MainActivity.this.startActivity(intent);
            }
        });

        Button buttonArticles = findViewById(R.id.openArticles);
        buttonArticles.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ArticlesActivity.class);
                intent.putExtra("type", "article");
                MainActivity.this.startActivity(intent);
            }
        });

        Button buttonTickets = findViewById(R.id.openTickets);
        buttonTickets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RequestsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });

        Button buttonChat = findViewById(R.id.singleChat);
        buttonChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UseResponse.openSingleChat(MainActivity.this);
            }
        });

        Button reLogin = findViewById(R.id.reLogin);
        reLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentityData identityData = new IdentityData(
                        "c70d55adde01752adb5fad66920e4ebffed4f94d56774fad780439749e4d4601",
                        "kurchik.sasha@mail.ru"
                );
                identityData.setFirstName("Alex");
                identityData.setLastName("Kurchik");
                Api.setIdentityData(identityData);
                UseResponse.clearIdentity(MainActivity.this);
                Cache.clear();
            }
        });
    }
}
