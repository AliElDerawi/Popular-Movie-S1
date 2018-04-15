package com.nanodegree.movietime.data.model.request;

import com.google.gson.annotations.SerializedName;
import com.nanodegree.movietime.data.model.TrailerResults;

import org.parceler.Parcel;

import java.util.ArrayList;

@Parcel
public class TrailerRequest {

    @SerializedName("id")
    private int id;
    @SerializedName("results")
    private ArrayList<TrailerResults> data = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ArrayList<TrailerResults> getData() {
        return data;
    }

    public void setData(ArrayList<TrailerResults> data) {
        this.data = data;
    }
}
