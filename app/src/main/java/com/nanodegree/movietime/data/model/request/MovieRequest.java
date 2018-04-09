package com.nanodegree.movietime.data.model.request;

import com.google.gson.annotations.SerializedName;
import com.nanodegree.movietime.data.model.Results;

import org.parceler.Parcel;

import java.util.ArrayList;
@Parcel
public class MovieRequest {

    @SerializedName("page")
    private int page;
    @SerializedName("total_results")
    private int totalResults;
    @SerializedName("total_pages")
    private int totalPages;
    @SerializedName("results")
    private ArrayList<Results> data = null;

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public ArrayList<Results> getData() {
        return data;
    }

    public void setData(ArrayList<Results> data) {
        this.data = data;
    }
}