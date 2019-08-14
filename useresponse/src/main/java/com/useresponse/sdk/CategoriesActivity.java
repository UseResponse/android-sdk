package com.useresponse.sdk;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.useresponse.sdk.api.Api;
import com.useresponse.sdk.api.ApiHelper;
import com.useresponse.sdk.api.Article;
import com.useresponse.sdk.api.ArticlesQuery;
import com.useresponse.sdk.api.Category;
import com.useresponse.sdk.grouped_list.GroupedListAdapter;
import com.useresponse.sdk.grouped_list.GroupedListItem;
import com.useresponse.sdk.utils.Cache;
import com.useresponse.sdk.utils.UseResponse;

import java.util.ArrayList;

public class CategoriesActivity extends AppCompatActivity {

    private ListView categoriesList;
    private ProgressBar loader;
    private String activeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        setTitle(getString(R.string.kb_header_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!UseResponse.init(this)) {
            return;
        }

        Intent intent = getIntent();
        activeType = intent.getStringExtra("type");

        categoriesList = (ListView)findViewById(R.id.categoriesList);
        loader = (ProgressBar)findViewById(R.id.categoriesLoader);

        if (Cache.getAllArticles() == null) {
            categoriesList.setVisibility(View.GONE);
            loader.setVisibility(View.VISIBLE);
            (new LoadTask()).execute();
        } else {
            renderCategories();
        }
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

                Intent intent = new Intent(CategoriesActivity.this, ArticlesActivity.class);
                intent.putExtra("search", query);
                CategoriesActivity.this.startActivity(intent);
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

    private void renderCategories() {
        ArrayList<Article> articles = Cache.getAllArticles();
        ArrayList<Category> categories = ApiHelper.getArticlesCategories(articles);
        ArrayList<GroupedListItem> categoriesArray = new ArrayList<>();
        final SparseIntArray catMap = new SparseIntArray();
        int index = 0;
        int faqStarts = 0;
        boolean hasFaqs = false;

        class Type {
            private String slug;
            private String title;

            private Type(String slug, String title) {
                this.slug = slug;
                this.title = title;
            }
        }

        // types
        Type[] allTypes = {
                new Type("article", getString(R.string.articles)),
                new Type("faq", getString(R.string.faqs))
        };

        for (Type type : allTypes) {
            if (ApiHelper.hasArticlesType(articles, type.slug) && (activeType == null || activeType.length() == 0 || activeType.equals(type.slug))) {
                if (type.slug.equals("faq")) {
                    hasFaqs = true;

                    if (faqStarts == 0) {
                        faqStarts = index;
                    }
                }

                categoriesArray.add(new GroupedListItem(type.title, true, 0));
                catMap.put(index, 0);
                index++;

                for (Category category : categories) {
                    if (category.getType().equals(type.slug)) {
                        categoriesArray.add(new GroupedListItem(category.getName(), false, 0));
                        catMap.put(index, category.getId());
                        index++;
                    }
                }
            }
        }

        loader.setVisibility(View.GONE);
        categoriesList.setVisibility(View.VISIBLE);
        categoriesList.setAdapter(new GroupedListAdapter(
                this,
                R.layout.category_section,
                R.layout.category_row,
                R.id.categorySectionName,
                R.id.categoryName,
                0,
                0,
                categoriesArray
        ));

        final int faqStartsFinal = faqStarts;
        final boolean hasFaqsFinal = hasFaqs;

        categoriesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(CategoriesActivity.this, ArticlesActivity.class);
                intent.putExtra("categoryId", catMap.get(position));
                intent.putExtra("type", hasFaqsFinal && position >= faqStartsFinal ? "faq" : "article");
                CategoriesActivity.this.startActivity(intent);
            }
        });
    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {
        private String error;

        @Override
        protected Void doInBackground(Void... params) {
            try {
                Cache.setAllArticles(
                        Api.getArticles(
                                new ArticlesQuery()
                                        .setType("article")
                                        .setForum(UseResponse.getForumId(CategoriesActivity.this))
                        )
                );
            } catch (Exception e) {
                error = e.getMessage() != null ? e.getMessage() : "Unknown error";
                Log.e("UrLog", error);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (Cache.getAllArticles() != null) {
                renderCategories();
            } else {
                Toast.makeText(CategoriesActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
