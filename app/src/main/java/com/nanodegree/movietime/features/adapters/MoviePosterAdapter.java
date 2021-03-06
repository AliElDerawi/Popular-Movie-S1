package com.nanodegree.movietime.features.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.data.model.MovieResults;
import com.nanodegree.movietime.data.model.OnItemClickListener;
import com.nanodegree.movietime.util.GlideApp;

import java.util.ArrayList;

import static com.nanodegree.movietime.util.Contracts.BASE_IMAGE_URL;
import static com.nanodegree.movietime.util.Contracts.IMAGE_SIZE_POSTER;

/**
 * Created by ali19 on 3/24/2018.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<MovieResults> results;
    public static final String MOVIE_POSTER_PATH = "path";
    public static final String MOVIE_OVERVIEW = "overview";
    public static final String MOVIE_TITLE = "title";
    public static final String MOVIE_RATING = "rating";
    public static final String MOVIE_DATE = "date";
    public static final String MOVIE_ID = "id";
    private final OnItemClickListener listener;

    public MoviePosterAdapter(Context mContext, ArrayList<MovieResults> results, OnItemClickListener listener) {
        super();
        this.mContext = mContext;
        this.results = results;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_poster_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivPoster;
        TextView tvRating;

        ViewHolder(View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvRating = itemView.findViewById(R.id.tv_rating);
            ivPoster.setOnClickListener(this);
        }

        private void onBind(int position) {
            String movieUrl = BASE_IMAGE_URL + IMAGE_SIZE_POSTER + results.get(position).getPosterPath();
            GlideApp.with(mContext).load(movieUrl).error(R.drawable.placeholder_poster).placeholder(R.drawable.placeholder_poster).into(ivPoster);
            tvRating.setText(String.valueOf(results.get(position).getAverageScore()));

        }

        @Override
        public void onClick(View view) {
            if (view == ivPoster) {
                    int position = getAdapterPosition();
                    listener.onItemClick(position);
            }
        }
    }
}
