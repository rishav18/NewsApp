package com.example.android.newsapp;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    private static final String LOG_TAG = Utils.class.getName();
    private static final int connectTimeout = 15000;
    private static final int readTimeout = 10000;

    private Utils() {
        throw new AssertionError();
    }

    private static URL createUrl(String requestUrl) {
        URL url = null;
        if (requestUrl == null) {
            return null;
        }
        try {
            url = new URL(requestUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error forming the url");
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;
        if (url == null) {
            Log.e(LOG_TAG, "null");
            return null;
        }
        Log.e(LOG_TAG, url.toString());
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setConnectTimeout(connectTimeout);
            urlConnection.setReadTimeout(readTimeout);
            urlConnection.connect();
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Wrong response code : " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error in requesting the data");
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        try {
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
                BufferedReader reader = new BufferedReader(inputStreamReader);
                String line = reader.readLine();
                while (line != null) {
                    output.append(line);
                    line = reader.readLine();
                }
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error reading data  from the stream");
        }
        return output.toString();
    }

    public static List<com.example.android.newsapp.News> fetchNewsData(String requestUrl) {
        if (requestUrl == null) {
            return null;
        }
        URL url = createUrl(requestUrl);
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making the http request");
        }
        return extractFromJson(jsonResponse);
    }

    private static List<com.example.android.newsapp.News> extractFromJson(String newslistJson) {
        if (TextUtils.isEmpty(newslistJson)) {
            return null;
        }
        List<com.example.android.newsapp.News> newsList = new ArrayList<>();
        try {
            JSONObject rootObject = new JSONObject(newslistJson);
            JSONObject responseObject = rootObject.getJSONObject("response");
            JSONArray resultsArray = responseObject.getJSONArray("results");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultObject = resultsArray.getJSONObject(i);
                String title = "";
                if (resultObject.has("webTitle")) {
                    title = resultObject.getString("webTitle");
                }
                String authors = "-";
                if (resultObject.has("tags")) {
                    JSONArray tagsArray = resultObject.getJSONArray("tags");
                    if (tagsArray.length() != 0) {
                        JSONObject tagsObject = tagsArray.getJSONObject(0);
                        authors = tagsObject.getString("webTitle");
                    }
                }
                String section_Name = "";
                if (resultObject.has("sectionName")) {
                    section_Name = resultObject.getString("sectionName");
                }

                String publication_Date = "";
                if (resultObject.has("webPublicationDate")) {
                    publication_Date = resultObject.getString("webPublicationDate");
                }

                String url = "";
                if (resultObject.has("webUrl")) {
                    url = resultObject.getString("webUrl");
                }
                com.example.android.newsapp.News news = new com.example.android.newsapp.News(title, authors, section_Name, publication_Date, url);
                newsList.add(news);
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error extracting data from the json");
        }
        return newsList;
    }


}
