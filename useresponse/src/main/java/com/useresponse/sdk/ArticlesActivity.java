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
import android.widget.TextView;
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

public class ArticlesActivity extends AppCompatActivity {
    ListView articlesList;
    ProgressBar loader;
    TextView notFound;
    ArrayList<Article> articles;
    boolean isSearchSet;
    boolean isCategorySet;
    boolean isTypeSet;
    String search;
    String type;
    int categoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_articles);
        setTitle(getString(R.string.kb_header_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (!UseResponse.init(this)) {
            return;
        }

        Intent intent = getIntent();
        search = intent.getStringExtra("search");
        type = intent.getStringExtra("type");
        categoryId = intent.getIntExtra("categoryId", -1);
        articlesList = (ListView)findViewById(R.id.articlesList);
        loader = (ProgressBar)findViewById(R.id.articlesLoader);
        notFound = (TextView)findViewById(R.id.resultNotFound);
        isSearchSet = search != null && search.length() > 0;
        isTypeSet = type != null && type.length() > 0;
        isCategorySet = categoryId >= 0;

        articlesList.setVisibility(View.GONE);
        notFound.setVisibility(View.GONE);
        loader.setVisibility(View.VISIBLE);

        if (!isSearchSet && Cache.getAllArticles() != null) {
            articles = Cache.getAllArticles();
            renderArticles();
        } else {
            (new LoadTask()).execute();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        final MenuItem searchItem = menu.findItem(R.id.searchButton);
        final SearchView searchView = (SearchView) searchItem.getActionView();

        if (isSearchSet) {
            searchItem.expandActionView();
            searchView.setQuery(search, false);

            searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return true;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    finish();
                    return false;
                }
            });
        }

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() == 0) return false;

                if (isSearchSet) {
                    search = query;
                    articlesList.setVisibility(View.GONE);
                    notFound.setVisibility(View.GONE);
                    loader.setVisibility(View.VISIBLE);
                    (new LoadTask()).execute();
                } else {
                    searchView.setQuery("", false);
                    searchItem.collapseActionView();

                    Intent intent = new Intent(ArticlesActivity.this, ArticlesActivity.class);
                    intent.putExtra("search", query);
                    ArticlesActivity.this.startActivity(intent);
                }

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

    private void renderArticles() {
        if (articles.size() == 0) {
            loader.setVisibility(View.GONE);
            articlesList.setVisibility(View.GONE);
            notFound.setVisibility(View.VISIBLE);
            return;
        }

        ArrayList<GroupedListItem> articlesArray = new ArrayList<>();
        ArrayList<Category> categories = ApiHelper.getArticlesCategories(articles);
        //final ModelArticle[] artMap = new ModelArticle[articles.size() + categories.size() + 1];
        final SparseIntArray artMap = new SparseIntArray();
        int index = 0;

        if (isSearchSet) {
            articlesArray.add(new GroupedListItem(getString(R.string.search_results), true, 0));
            index++;
        }

        for (Category category : categories) {
            if (!isSearchSet) {
                if (isCategorySet && categoryId != category.getId() || isTypeSet && !category.getType().equals(type)) {
                    continue;
                }

                articlesArray.add(new GroupedListItem(category.getName(), true, 0));
                index++;
            }

            int icon = category.getType().equals("faq") ? R.drawable.ic_type_faq : R.drawable.ic_type_article;

            for (Article article : articles) {
                if (article.getCategory().getId() == category.getId()) {
                    artMap.put(index, article.getId());
                    articlesArray.add(new GroupedListItem(article.getTitle(), false, icon));
                    index++;
                }
            }
        }

        loader.setVisibility(View.GONE);
        notFound.setVisibility(View.GONE);
        articlesList.setVisibility(View.VISIBLE);

        articlesList.setAdapter(new GroupedListAdapter(
                this,
                R.layout.article_section,
                R.layout.article_row,
                R.id.articleSectionName,
                R.id.articleName,
                0,
                R.id.articleIcon,
                articlesArray
        ));

        articlesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Article selectedArticle = null;

                for (Article article : articles) {
                    if (article.getId() == artMap.get(position)) {
                        selectedArticle = article;
                        break;
                    }
                }

                if (selectedArticle == null) {
                    Toast.makeText(ArticlesActivity.this, "Article is not found", Toast.LENGTH_LONG).show();
                    return;
                }

                Intent intent = new Intent(ArticlesActivity.this, ArticleActivity.class);
                intent.putExtra("articleTitle", selectedArticle.getTitle());
                intent.putExtra("articleBody", selectedArticle.getContent());
                ArticlesActivity.this.startActivity(intent);
            }
        });
    }

    private class LoadTask extends AsyncTask<Void, Void, Void> {
        private String error;

        @Override
        protected Void doInBackground(Void... params) {
            ArticlesQuery query = new ArticlesQuery();
            query.setForum(UseResponse.getForumId(ArticlesActivity.this));

            if (isSearchSet) {
                query.setSearch(search);
            }

            try {
                articles = Api.getArticles(query);

                if (!isSearchSet) {
                    Cache.setAllArticles(articles);
                }
            } catch (Exception e) {
                error = e.getMessage();
                Log.e("UrLog", e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (articles != null) {
                renderArticles();
            } else {
                Toast.makeText(ArticlesActivity.this, error, Toast.LENGTH_LONG).show();
            }
        }
    }
}
