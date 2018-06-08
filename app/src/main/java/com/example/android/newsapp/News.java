package com.example.android.newsapp;

public class News {
    private String Title;
    private String SectionName;
    private String Author;
    private String Url;
    private String PublicationDate;

    public News(String ntitle, String nauthor, String nsectionName, String npublicationDate, String nurl) {
        Title = ntitle;
        SectionName = nsectionName;
        Author = nauthor;
        Url = nurl;
        PublicationDate = npublicationDate;
    }

    public String getTitle() {
        return Title;
    }

    public String getSectionName() {
        return SectionName;
    }

    public String getAuthor() {
        return Author;
    }

    public String getUrl() {
        return Url;
    }

    public String getPublicationDate() {
        return PublicationDate;
    }
}
