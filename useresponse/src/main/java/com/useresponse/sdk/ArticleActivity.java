package com.useresponse.sdk;

import android.content.Intent;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.useresponse.sdk.utils.UseResponse;

public class ArticleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_article);
        setTitle(getString(R.string.kb_header_title));
        ActionBar sab = getSupportActionBar();
        if (sab != null) {
            sab.setDisplayHomeAsUpEnabled(true);
        }

        if (!UseResponse.init(this)) {
            return;
        }

        UseResponse.processButtonBack(this);

        WebView vw = (WebView)findViewById(R.id.articleBody);

        Intent intent = getIntent();
        String articleTitle = intent.getStringExtra("articleTitle");
        String articleBody = intent.getStringExtra("articleBody");

        String cssUrl = "https://www.useresponse.com/assets/css/mobile-app/article.css";
        vw.loadData("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssUrl + "\" media=\"all\"/>" +
                "<h2>" + articleTitle + "</h2><hr />" + articleBody, "text/html", null);

        vw.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                view.stopLoading();
                openInAppBrowser(request.getUrl());
                return false;
            }
        });

        vw.setHorizontalScrollBarEnabled(false);
        vw.setOnTouchListener(new View.OnTouchListener() {
            float m_downX;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }

                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                        break;
                    }

                }
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.searchButton);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() == 0) return false;

                searchView.setQuery("", false);
                searchItem.collapseActionView();

                Intent intent = new Intent(ArticleActivity.this, ArticlesActivity.class);
                intent.putExtra("search", query);
                ArticleActivity.this.startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
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

    private void openInAppBrowser(Uri uri)
    {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();
        intentBuilder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        customTabsIntent.launchUrl(this, uri);
    }
}
