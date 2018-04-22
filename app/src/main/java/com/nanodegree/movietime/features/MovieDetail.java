package com.nanodegree.movietime.features;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.nanodegree.movietime.BuildConfig;
import com.nanodegree.movietime.R;
import com.nanodegree.movietime.data.model.OnItemClickListener;
import com.nanodegree.movietime.data.model.TrailerResults;
import com.nanodegree.movietime.data.model.request.ReviewRequest;
import com.nanodegree.movietime.data.model.request.ReviewResults;
import com.nanodegree.movietime.data.model.request.TrailerRequest;
import com.nanodegree.movietime.util.Contracts.FavouriteMovieEntry;
import com.nanodegree.movietime.util.GlideApp;
import com.nanodegree.movietime.util.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.android.volley.Request.Method.GET;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEDATE;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEID;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEOVERVIEW;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEPOSTERPATH;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIERATING;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIETITLE;
import static com.nanodegree.movietime.util.ActivityUtils.watchYoutubeVideo;
import static com.nanodegree.movietime.util.Contracts.BASE_IMAGE_URL;
import static com.nanodegree.movietime.util.Contracts.BASE_URL;
import static com.nanodegree.movietime.util.Contracts.IMAGE_SIZE_FULL;
import static com.nanodegree.movietime.util.Contracts.IMAGE_SIZE_POSTER;
import static com.nanodegree.movietime.util.Contracts.MOVIE_REVIEW;
import static com.nanodegree.movietime.util.Contracts.MOVIE_TRAILER;
import static com.nanodegree.movietime.util.Contracts.REVIEW_URL;

public class MovieDetail extends AppCompatActivity implements View.OnClickListener , LoaderManager.LoaderCallbacks<Cursor> {
    @BindView(R.id.iv_poster)
    ImageView ivPoster;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @BindView(R.id.app_bar)
    AppBarLayout appBarLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.tv_overview)
    TextView tvOverview;
    @BindView(R.id.ratingBar)
    MaterialRatingBar materialRatingBar;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.rv_trailers)
    RecyclerView rvTrailers;
    @BindView(R.id.tv_trailer_connection)
    TextView tvTrailerConnection;
    @BindView(R.id.rv_reviews)
    RecyclerView rvReviews;
    @BindView(R.id.tv_overview_connection)
    TextView tvReviewConnection;

    private final String TAG = "MovieDetail";

    private ArrayList<TrailerResults> trailerResults;
    private ArrayList<ReviewResults> reviewResults;


    private String txtTitle, txtDate, txtOverview;
    private Float rating;
    int movieId;
    private String movieUrlPoster = "";

    private static final int FAVOURITE_LOADER_ID = 1;
    private boolean isAdded;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitleEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvTrailers.setLayoutManager(linearLayoutManager);
        rvTrailers.setNestedScrollingEnabled(true);
        rvTrailers.setVisibility(View.GONE);

        rvReviews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        rvReviews.setNestedScrollingEnabled(true);
        rvReviews.setVisibility(View.GONE);

        Intent fromPosterActivityIntent = getIntent();
        fillUi(fromPosterActivityIntent);

        if (isAdded){
            fab.setImageResource(R.drawable.ic_fill_favorite);
        }

        fab.setOnClickListener(this);


        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.title_movie_detail));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });

        getSupportLoaderManager().initLoader(FAVOURITE_LOADER_ID, null, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(FAVOURITE_LOADER_ID, null, this);
    }

    private void fillUi(Intent fromPosterActivityIntent) {

        if (fromPosterActivityIntent.hasExtra(MOVIEPOSTERPATH)) {
            movieUrlPoster = BASE_IMAGE_URL + IMAGE_SIZE_POSTER + fromPosterActivityIntent.getStringExtra(MOVIEPOSTERPATH);
            String movieUrl = BASE_IMAGE_URL + IMAGE_SIZE_FULL + fromPosterActivityIntent.getStringExtra(MOVIEPOSTERPATH);
            String TAG = "MovieDetail";
            Log.d(TAG, "Movie Full Image URL: " + movieUrl);
            GlideApp.with(this).load(movieUrl).placeholder(R.drawable.placeholder_poster).into(ivPoster);
        } else {
            GlideApp.with(this).load(R.drawable.placeholder_poster).into(ivPoster);
        }
        if (fromPosterActivityIntent.hasExtra(MOVIETITLE)) {
            txtTitle = fromPosterActivityIntent.getStringExtra(MOVIETITLE).trim();
            tvTitle.setText(txtTitle);
        }
        if (fromPosterActivityIntent.hasExtra(MOVIERATING)) {
            rating = fromPosterActivityIntent.getFloatExtra(MOVIERATING, 5);
            rating /= 2;
            materialRatingBar.setRating(rating);
            materialRatingBar.setIsIndicator(true);
        }
        if (fromPosterActivityIntent.hasExtra(MOVIEOVERVIEW)) {
            txtOverview = fromPosterActivityIntent.getStringExtra(MOVIEOVERVIEW);
            tvOverview.setText(txtOverview);
        }

        if (fromPosterActivityIntent.hasExtra(MOVIEDATE)) {
            txtDate = fromPosterActivityIntent.getStringExtra(MOVIEDATE);
            tvDate.setText(txtDate);
        }

        if (fromPosterActivityIntent.hasExtra(MOVIEID)) {
            movieId = fromPosterActivityIntent.getIntExtra(MOVIEID, 0);
//            mEditor.putBoolean(String.valueOf(movieId),false);
//            mEditor.apply();
            if (isOnline()) {
                requestTrailers(movieId);
                requestReviews(movieId);
            } else {
                rvTrailers.setVisibility(View.GONE);
                rvReviews.setVisibility(View.GONE);
                tvTrailerConnection.setVisibility(View.VISIBLE);
                tvTrailerConnection.setVisibility(View.VISIBLE);
            }
            getSupportLoaderManager().initLoader(FAVOURITE_LOADER_ID, null, this);
        }
    }

    private Uri addToFavourite() {
        ContentValues cv = new ContentValues();
        cv.put(FavouriteMovieEntry.COLUMN_MOVIE_ID, movieId);
        cv.put(FavouriteMovieEntry.COLUMN_MOVIE_TITLE, txtTitle);
        cv.put(FavouriteMovieEntry.COLUMN_MOVIE_RATING, rating * 2);
        cv.put(FavouriteMovieEntry.COLUMN_MOVIE_OVERVIEW, txtOverview);
        cv.put(FavouriteMovieEntry.COLUMN_MOVIE_POSTER, movieUrlPoster);
        cv.put(FavouriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE, txtDate);
        return getContentResolver().insert(FavouriteMovieEntry.CONTENT_URI, cv);
    }

    private void requestTrailers(int movieId) {
        String apiKey = BuildConfig.API_KEY;
        String url = BASE_URL + movieId + MOVIE_TRAILER + "?api_key=" + apiKey;
        JSONObject body = new JSONObject();

        Log.d(TAG, "MovieDetail Trailers: url > " + url);


        final JsonObjectRequest request = new JsonObjectRequest(GET, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "MovieDetail Trailers: response > " + response.toString());

                        rvTrailers.setVisibility(View.VISIBLE);
                        tvTrailerConnection.setVisibility(View.GONE);

                        TrailerRequest trailerRequest =
                                new Gson().fromJson(response.toString(), TrailerRequest.class);

                        trailerResults = new ArrayList<>();
                        trailerResults.addAll(trailerRequest.getData());
                        Log.d(TAG, "onResponse: mResult.size > " + trailerResults.size());


                        MovieTrailerAdapter movieTrailerAdapter = new MovieTrailerAdapter(getApplicationContext(), trailerResults, new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                watchYoutubeVideo(getApplicationContext(), trailerResults.get(position).getKey());
                            }
                        });
                        rvTrailers.setHasFixedSize(true);
                        rvTrailers.setAdapter(movieTrailerAdapter);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

//        request.setRetryPolicy(3);
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    private void requestReviews(int movieId) {
        String apiKey = BuildConfig.API_KEY;
        String url = BASE_URL + movieId + MOVIE_REVIEW + "?api_key=" + apiKey;
        JSONObject body = new JSONObject();

        Log.d(TAG, "MovieDetail Reviews: url > " + url);

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "MovieDetail Reviews: response > " + response.toString());

                        rvReviews.setVisibility(View.VISIBLE);
                        tvReviewConnection.setVisibility(View.GONE);

                        ReviewRequest reviewRequest =
                                new Gson().fromJson(response.toString(), ReviewRequest.class);
                        reviewResults = new ArrayList<>();
                        reviewResults.addAll(reviewRequest.getData());
                        Log.d(TAG, "onResponse: mResult.size > " + reviewResults.size());


                        MovieReviewAdapter movieResultAdapter = new MovieReviewAdapter(getApplicationContext(), reviewResults, new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Intent fromDetailActivity = new Intent(getApplicationContext(), WebActivity.class);
                                fromDetailActivity.putExtra(REVIEW_URL, reviewResults.get(position).getUrl());
                                startActivity(fromDetailActivity);
//                                Toast.makeText(getApplicationContext(), reviewResults.get(position).getUrl() + " ", Toast.LENGTH_LONG).show();
                            }
                        });
                        rvReviews.setHasFixedSize(true);
                        rvReviews.setAdapter(movieResultAdapter);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }


    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


    @Override
    public void onClick(View view) {

        if (view == fab) {
            if (!isAdded) {
                Uri insertUri = addToFavourite();
                if (insertUri != null) {
                    fab.setImageResource(R.drawable.ic_fill_favorite);
                    isAdded = true;
                    Snackbar.make(view, "Added Successfully!", Snackbar.LENGTH_LONG).show();
                } else {
                    Snackbar.make(view, "Sorry, Some errors happen!", Snackbar.LENGTH_LONG).show();
                }
            } else {
                Snackbar.make(view, "Already Added!", Snackbar.LENGTH_LONG).show();
            }
        }
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        return new AsyncTaskLoader<Cursor>(getApplicationContext()) {
            Cursor mFavouriteData = null;

            @Override
            protected void onStartLoading() {
                if (mFavouriteData != null){
                    deliverResult(mFavouriteData);
                } else {
                    forceLoad();
                }
            }

            @Nullable
            @Override
            public Cursor loadInBackground() {
                try {
                    Uri uri = FavouriteMovieEntry.CONTENT_URI;
                    uri = uri.buildUpon().appendPath(String.valueOf(movieId)).build();
                    return  getApplicationContext().getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null
                            );
                } catch (Exception e){
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }

            }

            public void deliverResult(Cursor data){
                mFavouriteData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if (data.getCount() > 0){
           isAdded  = true;
           fab.setImageResource(R.drawable.ic_fill_favorite);
           }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean("isAdded",isAdded);
        outState.getInt("movieId",movieId);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        isAdded = savedInstanceState.getBoolean("isAdded");
        movieId = savedInstanceState.getInt("movieId");
        super.onRestoreInstanceState(savedInstanceState);
    }




}
