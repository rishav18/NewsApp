package com.example.android.newsapp;

import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.newsapp.News;
import com.example.android.newsapp.NewsAdapter;
import com.example.android.newsapp.R;
import com.example.android.newsapp.SettingsActivity;
import com.example.android.newsapp.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<News>>, SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String LOG_TAG = MainActivity.class.getName();
    private static final String NEWS_URL = "https://content.guardianapis.com/search?";
    private static final String API_KEY = "f4f3518a-2e0b-40ac-a7e4-345300069c93";
    private static final int NEWS_LOADER_ID = 1;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ProgressBar progress_Bar;
    private NewsAdapter nAdapter;
    private LoaderManager loadManager;
    private TextView text_View;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progress_Bar = findViewById(R.id.progressBar);
        progress_Bar.setVisibility(View.VISIBLE);
        text_View = findViewById(R.id.empty_text_view);
        text_View.setVisibility(View.GONE);
        swipeRefreshLayout = findViewById(R.id.refresh_layout);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorPrimary));
        listView = findViewById(R.id.list);
        nAdapter = new NewsAdapter(this, new ArrayList<News>());
        listView.setAdapter(nAdapter);
        listView.setVisibility(View.GONE);
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        loadManager = getLoaderManager();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        prefs.registerOnSharedPreferenceChangeListener(this);
        if (networkInfo != null && networkInfo.isConnected()) {
            progress_Bar.setVisibility(View.VISIBLE);
            loadManager.initLoader(NEWS_LOADER_ID, null, MainActivity.this);
        } else {
            progress_Bar.setVisibility(View.GONE);
            text_View.setText(R.string.connection);
            text_View.setVisibility(View.VISIBLE);
        }
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                News currentItem = nAdapter.getItem(position);
                Uri newsUri = Uri.parse(currentItem.getUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, newsUri);
                startActivity(intent);
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                listView.setVisibility(View.GONE);
                loadManager.restartLoader(NEWS_LOADER_ID, null, MainActivity.this);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.settings_order_by_key)) ||
                key.equals(getString(R.string.settings_section_key))) {
            nAdapter.clear();
            text_View.setVisibility(View.GONE);
            progress_Bar.setVisibility(View.VISIBLE);
            getLoaderManager().restartLoader(NEWS_LOADER_ID, null, this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
        Log.e(LOG_TAG, "onCreateLoader");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        String section = sharedPrefs.getString(
                getString(R.string.settings_section_key),
                getString(R.string.settings_section_default));
        String orderBy = sharedPrefs.getString(getString(R.string.settings_order_by_key),
                getString(R.string.settings_order_by_default));

        Uri baseUri = Uri.parse(NEWS_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("order-by", orderBy);
        uriBuilder.appendQueryParameter("section", section);
        uriBuilder.appendQueryParameter("format", "json");
        uriBuilder.appendQueryParameter("show-tags", "contributor");
        uriBuilder.appendQueryParameter("api-key", API_KEY);
        return new NewsLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> news) {
        progress_Bar.setVisibility(View.GONE);
        if (news != null && !news.isEmpty()) {
            listView.setVisibility(View.VISIBLE);
            nAdapter.addAll(news);
        } else {
            text_View.setVisibility(View.VISIBLE);
            text_View.setText("No Data Found");
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {

        nAdapter.clear();
    }

    private static class NewsLoader extends AsyncTaskLoader<List<News>> {
        String mUrl;

        public NewsLoader(Context context, String url) {
            super(context);
            mUrl = url;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            forceLoad();
        }

        @Override
        public List<News> loadInBackground() {
            if (mUrl == null) {
                return null;
            }
            return Utils.fetchNewsData(mUrl);
        }
    }
}
