package com.nanodegree.movietime.features.activities;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nanodegree.movietime.features.adapters.MovieReviewAdapter;
import com.nanodegree.movietime.features.adapters.MovieTrailerAdapter;
import com.nanodegree.movietime.util.Contracts.FavouriteMovieEntry;
import com.nanodegree.movietime.util.GlideApp;
import com.nanodegree.movietime.util.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.android.volley.Request.Method.GET;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_DATE;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_ID;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_OVERVIEW;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_POSTER_PATH;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_RATING;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_TITLE;
import static com.nanodegree.movietime.util.ActivityUtils.getYear;
import static com.nanodegree.movietime.util.ActivityUtils.isOnline;
import static com.nanodegree.movietime.util.ActivityUtils.showSnackBar;
import static com.nanodegree.movietime.util.ActivityUtils.watchYoutubeVideo;
import static com.nanodegree.movietime.util.Contracts.BASE_IMAGE_URL;
import static com.nanodegree.movietime.util.Contracts.BASE_URL;
import static com.nanodegree.movietime.util.Contracts.IMAGE_SIZE_FULL;
import static com.nanodegree.movietime.util.Contracts.IMAGE_SIZE_POSTER;
import static com.nanodegree.movietime.util.Contracts.MOVIE_REVIEW;
import static com.nanodegree.movietime.util.Contracts.MOVIE_TRAILER;
import static com.nanodegree.movietime.util.Contracts.REVIEW_URL;

public class MovieDetailActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Cursor> {
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
    @BindView(R.id.tv_rating)
    TextView tvRating;

    private ShareActionProvider mShareActionProvider;

    private final String TAG = "MovieDetail";
    private final String IS_ADDED = "isAdded";
    private final String MY_MOVIE_ID = "movieId";

    private ArrayList<TrailerResults> trailerResults;
    private ArrayList<ReviewResults> reviewResults;


    private String txtTitle, txtDate, txtOverview;
    private Float rating;
    int movieId;
    private String movieUrlPoster = "";

    private static final int FAVOURITE_LOADER_ID = 1;
    private boolean isAdded;
    private String trailerUri;
    private AlertDialog alertDialog;
    private long movieFavouriteId = 0 ;


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

        if (isAdded) {
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

        if (fromPosterActivityIntent.hasExtra(MOVIE_POSTER_PATH)) {
            movieUrlPoster = BASE_IMAGE_URL + IMAGE_SIZE_POSTER + fromPosterActivityIntent.getStringExtra(MOVIE_POSTER_PATH);
            String movieUrl = BASE_IMAGE_URL + IMAGE_SIZE_FULL + fromPosterActivityIntent.getStringExtra(MOVIE_POSTER_PATH);
            String TAG = "MovieDetail";
            Log.d(TAG, "Movie Full Image URL: " + movieUrl);
            GlideApp.with(this).load(movieUrl).placeholder(R.drawable.placeholder_poster).into(ivPoster);
        } else {
            GlideApp.with(this).load(R.drawable.placeholder_poster).into(ivPoster);
        }
        if (fromPosterActivityIntent.hasExtra(MOVIE_TITLE)) {
            txtTitle = fromPosterActivityIntent.getStringExtra(MOVIE_TITLE).trim();
            tvTitle.setText(txtTitle);
        }
        if (fromPosterActivityIntent.hasExtra(MOVIE_RATING)) {
            rating = fromPosterActivityIntent.getFloatExtra(MOVIE_RATING, 5);
            rating /= 2;
            materialRatingBar.setRating(rating);
            materialRatingBar.setIsIndicator(true);
            tvRating.setText(String.valueOf(rating*2));
        }
        if (fromPosterActivityIntent.hasExtra(MOVIE_OVERVIEW)) {
            txtOverview = fromPosterActivityIntent.getStringExtra(MOVIE_OVERVIEW);
            tvOverview.setText(txtOverview);
        }

        if (fromPosterActivityIntent.hasExtra(MOVIE_DATE)) {
            txtDate = fromPosterActivityIntent.getStringExtra(MOVIE_DATE);
            tvDate.setText(getYear(txtDate));
        }

        if (fromPosterActivityIntent.hasExtra(MOVIE_ID)) {
            movieId = fromPosterActivityIntent.getIntExtra(MOVIE_ID, 0);

            if (isOnline(this)) {
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
        String url = BASE_URL + movieId + MOVIE_TRAILER + "?api_key=" + BuildConfig.API_KEY;
        JSONObject body = new JSONObject();

        Log.d(TAG, "MovieDetail Trailers: url > " + url);


        final JsonObjectRequest request = new JsonObjectRequest(GET, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "MovieDetail Trailers: response > " + response.toString());

                        TrailerRequest trailerRequest =
                                new Gson().fromJson(response.toString(), TrailerRequest.class);

                        if (trailerRequest.getData().size() > 0) {

                            rvTrailers.setVisibility(View.VISIBLE);
                            tvTrailerConnection.setVisibility(View.GONE);

                            trailerResults = new ArrayList<>();
                            trailerResults.addAll(trailerRequest.getData());
                            trailerUri = trailerRequest.getData().get(0).getKey();
                            Log.d(TAG, "onResponse: mResult.size > " + trailerResults.size());


                            MovieTrailerAdapter movieTrailerAdapter = new MovieTrailerAdapter(getApplicationContext(), trailerResults, new OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    watchYoutubeVideo(getApplicationContext(), trailerResults.get(position).getKey());
                                }
                            });
                            rvTrailers.setHasFixedSize(true);
                            rvTrailers.setAdapter(movieTrailerAdapter);

                        } else {

                            rvTrailers.setVisibility(View.GONE);
                            tvTrailerConnection.setVisibility(View.VISIBLE);
                            tvTrailerConnection.setText(R.string.message_empty_trailers);

                        }

                    }

                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    private void requestReviews(int movieId) {
        String url = BASE_URL + movieId + MOVIE_REVIEW + "?api_key=" + BuildConfig.API_KEY;
        JSONObject body = new JSONObject();

        Log.d(TAG, "MovieDetail Reviews: url > " + url);

        final JsonObjectRequest request = new JsonObjectRequest(GET, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG, "MovieDetail Reviews: response > " + response.toString());

                        ReviewRequest reviewRequest =
                                new Gson().fromJson(response.toString(), ReviewRequest.class);


                        if (reviewRequest.getData().size() > 0) {

                            rvReviews.setVisibility(View.VISIBLE);
                            tvReviewConnection.setVisibility(View.GONE);
                            reviewResults = new ArrayList<>();
                            reviewResults.addAll(reviewRequest.getData());
                            Log.d(TAG, "onResponse: mResult.size > " + reviewResults.size());


                            MovieReviewAdapter movieResultAdapter = new MovieReviewAdapter(getApplicationContext(), reviewResults, new OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    Intent fromDetailActivity = new Intent(getApplicationContext(), WebActivity.class);
                                    fromDetailActivity.putExtra(REVIEW_URL, reviewResults.get(position).getUrl());
                                    startActivity(fromDetailActivity);
//
                                }
                            });
                            rvReviews.setHasFixedSize(true);
                            rvReviews.setAdapter(movieResultAdapter);
                        } else {
                            tvReviewConnection.setVisibility(View.VISIBLE);
                            rvReviews.setVisibility(View.GONE);
                            tvReviewConnection.setText(R.string.message_empty_reviews);
                        }


                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);


    }

    @Override
    public void onClick(View view) {

        if (view == fab) {
            if (!isAdded) {
                Uri insertUri = addToFavourite();
                if (insertUri != null) {
                    fab.setImageResource(R.drawable.ic_fill_favorite);
                    isAdded = true;
                    showSnackBar(this,view,"Added Successfully!");
                } else {
                    showSnackBar(this,view,"Sorry, Some error happen!");
                }
            } else {
                showSnackBar(this,view,"Already Added!");
//                alertConfirm();
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
                if (mFavouriteData != null) {
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
                    return getApplicationContext().getContentResolver().query(uri,
                            null,
                            null,
                            null,
                            null
                    );
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }

            }

            public void deliverResult(Cursor data) {
                mFavouriteData = data;
                super.deliverResult(data);
            }
        };

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

        if (data.getCount() > 0) {
            if (data.moveToFirst()){
                movieFavouriteId = data.getLong(data.getColumnIndex(FavouriteMovieEntry._ID));
            }
            isAdded = true;
            fab.setImageResource(R.drawable.ic_fill_favorite);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putBoolean(IS_ADDED, isAdded);
        outState.getInt(MY_MOVIE_ID, movieId);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        isAdded = savedInstanceState.getBoolean(IS_ADDED);
        movieId = savedInstanceState.getInt(MY_MOVIE_ID);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.share,menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch and store ShareActionProvider
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.item_share){
            shareTrailer();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void shareTrailer(){
        if (!trailerUri.isEmpty()) {
            Uri shareUri = Uri.parse("http://www.youtube.com/watch?v=" + trailerUri);
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareUri.toString());
            shareIntent.setType("text/plain");
            setShareIntent(shareIntent);
            startActivity(Intent.createChooser(shareIntent, "SendBy"));
        } else {
            Toast.makeText(this,"There is no Trailer to Share",Toast.LENGTH_LONG).show();
        }
    }

    private void setShareIntent(Intent shareIntent) {
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }
    }

    private void alertConfirm() {

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this, 0);
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_confirm_unfavourite, null);
        dialogBuilder.setView(dialogView);

        TextView errorMessage = (TextView) dialogView.findViewById(R.id.tv_error);
        errorMessage.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        TextView title = (TextView) dialogView.findViewById(R.id.tv_title);
        title.setTextColor(ContextCompat.getColor(this, R.color.colorBlack));
        title.setTextSize(19);
        final TextView cancel = (TextView) dialogView.findViewById(R.id.tv_cancel);
        final TextView confirm = (TextView) dialogView.findViewById(R.id.tv_confirm);

        dialogBuilder.setCancelable(false);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = FavouriteMovieEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(String.valueOf(movieFavouriteId)).build();
                if (getContentResolver().delete(uri, null, null) > 0 ){
                    showSnackBar(v.getContext(),v,"Deleted Successfully!");
                    fab.setImageResource(R.drawable.ic_favorite);
                    isAdded = false;
                } else {
                    showSnackBar(v.getContext(),v,"Sorry some error happened!");
                    fab.setImageResource(R.drawable.ic_fill_favorite);
                    isAdded = true;
                }
                alertDialog.dismiss();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

}
