package com.nanodegree.movietime.features;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.util.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity {

    @BindView(R.id.navigation) BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        addTopRatedFragment();
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.navigation_top_rated:
                        addTopRatedFragment();
                        return true;

                    case R.id.navigation_most_popular:
                        addMostPopularFragment();
                        return true;

                    case R.id.navigation_favourite:
                        addFavouriteFragment();
                        return true;
                }

                return false;
            }
        };

    private void addTopRatedFragment(){
        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        TopRatedFragment topRatedFragment = new TopRatedFragment();

        ActivityUtils.AddFragmentToActivity(getSupportFragmentManager(),
                topRatedFragment, R.id.container);
    }

       private void addMostPopularFragment(){

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        MostPopularFragment mostPopularFragment = new MostPopularFragment();

        ActivityUtils.AddFragmentToActivity(getSupportFragmentManager(),
                mostPopularFragment, R.id.container);
    }

    private void addFavouriteFragment(){

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        FavouriteFragment mostPopularFragment = new FavouriteFragment();

        ActivityUtils.AddFragmentToActivity(getSupportFragmentManager(),
                mostPopularFragment, R.id.container);
    }
}



