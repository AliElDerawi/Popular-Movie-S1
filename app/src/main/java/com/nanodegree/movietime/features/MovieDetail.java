package com.nanodegree.movietime.features;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import com.nanodegree.movietime.data.model.OnItemClickListenerTrailer;
import com.nanodegree.movietime.data.model.TrailerResults;
import com.nanodegree.movietime.data.model.request.TrailerRequest;
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
import static com.nanodegree.movietime.util.Contracts.MOVIE_REVIEW;
import static com.nanodegree.movietime.util.Contracts.MOVIE_TRAILER;

public class MovieDetail extends AppCompatActivity {
    @BindView(R.id.iv_poster)
    ImageView ivPoster;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout collapsingToolbarLayout ;
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
    private int movieId;
    private final String TAG = "MovieDetail";
    private ArrayList<TrailerResults> results  = new ArrayList<>();
    Toast mToast;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitleEnabled(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rvTrailers.setLayoutManager(linearLayoutManager);
        rvTrailers.setNestedScrollingEnabled(true);
        rvTrailers.setVisibility(View.INVISIBLE);
        rvTrailers.setVisibility(View.GONE);

        Intent fromPosterActivityIntent = getIntent();
        if (fromPosterActivityIntent.hasExtra(MOVIEPOSTERPATH)){
            String movieUrl = BASE_IMAGE_URL + IMAGE_SIZE_FULL + fromPosterActivityIntent.getStringExtra(MOVIEPOSTERPATH);
            String TAG = "MovieDetail";
            Log.d(TAG,"Movie Full Image URL: " + movieUrl);
            GlideApp.with(this).load(movieUrl).placeholder(R.drawable.placeholder_poster).into(ivPoster);
        } else {
            GlideApp.with(this).load(R.drawable.placeholder_poster).into(ivPoster);
        }
        if (fromPosterActivityIntent.hasExtra(MOVIETITLE)){
            tvTitle.setText(fromPosterActivityIntent.getStringExtra(MOVIETITLE).trim());
        }
        if (fromPosterActivityIntent.hasExtra(MOVIERATING)){
            Float rating = fromPosterActivityIntent.getFloatExtra(MOVIERATING,5);
            rating /=2;
            materialRatingBar.setRating(rating);
            materialRatingBar.setIsIndicator(true);
        }
        if (fromPosterActivityIntent.hasExtra(MOVIEOVERVIEW)){
            tvOverview.setText(fromPosterActivityIntent.getStringExtra(MOVIEOVERVIEW));
        }

        if (fromPosterActivityIntent.hasExtra(MOVIEDATE)){
            tvDate.setText(fromPosterActivityIntent.getStringExtra(MOVIEDATE));
        }

        if (fromPosterActivityIntent.hasExtra(MOVIEID)){
            movieId = fromPosterActivityIntent.getIntExtra(MOVIEID,0);
            if (isOnline()) {
                requestTrailers(movieId);
                requestReviews(movieId);
            } else {
                rvTrailers.setVisibility(View.GONE);
                tvTrailerConnection.setVisibility(View.VISIBLE);
            }
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Not implemented Yet", Snackbar.LENGTH_LONG).show();
            }
        });
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("Movie Detail");
                    isShow = true;
                } else if(isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void requestTrailers(int movieId){
        String apiKey = BuildConfig.API_KEY;
        String url = BASE_URL + movieId + MOVIE_TRAILER +"?api_key="+ apiKey;
        JSONObject body = new JSONObject();

        Log.d(TAG, "MovieDetail Trailers: url > " + url);


        final JsonObjectRequest request = new JsonObjectRequest(GET, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG,"MovieDetail Trailers: response > " + response.toString());

                        rvTrailers.setVisibility(View.VISIBLE);
                        tvTrailerConnection.setVisibility(View.GONE);

                        TrailerRequest trailerRequest =
                                new Gson().fromJson(response.toString(), TrailerRequest.class);

                        results.addAll(trailerRequest.getData());
                        Log.d(TAG, "onResponse: mResult.size > " + results.size());


                        MovieTrailerAdapter moviePosterAdapter = new MovieTrailerAdapter(getApplicationContext(), results, new OnItemClickListenerTrailer() {
                            @Override
                            public void onItemClick(ArrayList<TrailerResults> results, int position) {
//                                if (mToast != null){
//                                    mToast.cancel();
//                                }
//                                mToast = Toast.makeText(getApplicationContext(),results.get(position).getName(),Toast.LENGTH_LONG);
//                                mToast.show();
                                watchYoutubeVideo(getApplicationContext(),results.get(position).getKey());
                            }
                        });
                        rvTrailers.setHasFixedSize(true);
                        rvTrailers.setAdapter(moviePosterAdapter);
                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);

    }

    private void requestReviews(int movieId){
        String apiKey = BuildConfig.API_KEY;
        String url = BASE_URL + movieId + MOVIE_REVIEW +"?api_key="+ apiKey;
//        JSONObject body = new JSONObject();

        Log.d(TAG, "MovieDetail Reviews: url > " + url);


    }



    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
