package com.example.android.newsapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class NewsAdapter extends ArrayAdapter<News> {

    private static final String LOG_TAG = NewsAdapter.class.getName();

    public NewsAdapter(@NonNull Context context, @NonNull List<News> objects) {
        super(context, 0, objects);
    }

    @TargetApi(Build.VERSION_CODES.N)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
        }
        News currentItem = getItem(position);
        TextView titleTextView = convertView.findViewById(R.id.title_text_view);
        titleTextView.setText(currentItem.getTitle());
        TextView sectionTextView = convertView.findViewById(R.id.section_text_view);
        sectionTextView.setText(currentItem.getSectionName());
        TextView authorTextView = convertView.findViewById(R.id.author_text_view);
        authorTextView.setText(currentItem.getAuthor());
        TextView dateTextView = convertView.findViewById(R.id.date_text_view);
        TextView timeTextView = convertView.findViewById(R.id.time_text_view);
        String dateString = currentItem.getPublicationDate();
        SimpleDateFormat to = new SimpleDateFormat("dd, MMM yyyy");
        SimpleDateFormat from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            dateTextView.setText(to.format(from.parse(dateString)));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error getting date");
        }
        to = new SimpleDateFormat("h:mm a");
        from = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        try {
            timeTextView.setText(to.format(from.parse(dateString)));
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error getting time");
        }
        return convertView;
    }
}
