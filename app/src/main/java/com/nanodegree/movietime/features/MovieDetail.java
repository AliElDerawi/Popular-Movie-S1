package com.nanodegree.movietime.features;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.util.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.zhanghai.android.materialratingbar.MaterialRatingBar;

import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEDATE;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEOVERVIEW;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEPOSTERPATH;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIERATING;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIETITLE;
import static com.nanodegree.movietime.util.Contracts.BASE_IMAGE_URL;
import static com.nanodegree.movietime.util.Contracts.IMAGE_SIZE_FULL;

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



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbarLayout.setTitleEnabled(true);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
