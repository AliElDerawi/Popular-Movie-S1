package com.nanodegree.movietime.data.model.request;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class ReviewResults {

    @SerializedName("author")
    private String author ;
    @SerializedName("content")
    private String content;
    @SerializedName("id")
    private String id;
    @SerializedName("url")
    private String url;


    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
