package com.nanodegree.movietime.features;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.data.model.Results;
import com.nanodegree.movietime.util.GlideApp;

import java.util.ArrayList;

import static com.nanodegree.movietime.util.Contracts.BASE_IMAGE_URL;
import static com.nanodegree.movietime.util.Contracts.IMAGE_SIZE_POSTER;

/**
 * Created by ali19 on 3/24/2018.
 */

public class MoviePosterAdapter extends RecyclerView.Adapter<MoviePosterAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<Results> results;
    private Toast mToast;
    static final String MOVIEPOSTERPATH = "path";
    static final String MOVIEOVERVIEW = "overview";
    static final String MOVIETITLE = "title";
    static final String MOVIERATING = "rating";
    static final String MOVIEDATE = "date";

    MoviePosterAdapter(Context mContext, ArrayList<Results> results) {
        super();
        this.mContext = mContext;
        this.results = results;
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

        ViewHolder(View itemView) {
            super(itemView);
            ivPoster = itemView.findViewById(R.id.iv_poster);
            ivPoster.setOnClickListener(this);
        }

        private void onBind(int position) {
            String movieUrl = BASE_IMAGE_URL + IMAGE_SIZE_POSTER + results.get(position).getPosterPath();
            GlideApp.with(mContext).load(movieUrl).error(R.drawable.placeholder_poster).placeholder(R.drawable.placeholder_poster).into(ivPoster);

        }

        @Override
        public void onClick(View view) {
            if (view == ivPoster) {
//                if (isOnline()) {
                    int position = getAdapterPosition();
                    int id = results.get(position).getId();
//                    if (mToast != null)
//                        mToast.cancel();
//                    mToast = Toast.makeText(view.getContext(), String.valueOf(id), Toast.LENGTH_LONG);
//                    mToast.show();
                    Intent toMovieDetailIntent = new Intent(view.getContext(),MovieDetail.class);
                    toMovieDetailIntent.putExtra(MOVIEPOSTERPATH,results.get(position).getPosterPath());
                    toMovieDetailIntent.putExtra(MOVIEOVERVIEW,results.get(position).getOverview());
                    toMovieDetailIntent.putExtra(MOVIERATING,results.get(position).getAverageScore());
                    toMovieDetailIntent.putExtra(MOVIETITLE,results.get(position).getTitle().trim());
                    toMovieDetailIntent.putExtra(MOVIEDATE,results.get(position).getReleaseDate());

                    view.getContext().startActivity(toMovieDetailIntent);
//                } else {
//                    Toast.makeText(view.getContext(),mContext.getResources().getString(R.string.no_internet_message),Toast.LENGTH_LONG).show();
//                }
            }
        }
    }
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
}
