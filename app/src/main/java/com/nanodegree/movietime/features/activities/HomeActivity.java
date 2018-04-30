package com.nanodegree.movietime.features.activities;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.features.fragments.FavouriteFragment;
import com.nanodegree.movietime.features.fragments.MostPopularFragment;
import com.nanodegree.movietime.features.fragments.TopRatedFragment;
import com.nanodegree.movietime.util.ActivityUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nanodegree.movietime.util.Contracts.BUNDLE_RECYCLER_LAYOUT;
import static com.nanodegree.movietime.util.Contracts.currentFragment;
import static com.nanodegree.movietime.util.Contracts.listState;

public class HomeActivity extends AppCompatActivity {

    private final String mTopRatedFragment = "topRated";
    private final String mMostPopularFragment = "mostPopular";
    private final String mFavouriteFragment = "favourite";

    private final String TAG = "HomeActivity";
    public static final String CURRENT_FRAGMENT = "mCurrentFragment";

    @BindView(R.id.navigation) BottomNavigationView navigation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        if (!currentFragment.isEmpty()){
            selectLastFragment();
        }else
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
        currentFragment = mTopRatedFragment;

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        TopRatedFragment topRatedFragment = new TopRatedFragment();

        ActivityUtils.AddFragmentToActivity(getSupportFragmentManager(),
                topRatedFragment, R.id.container);
    }

       private void addMostPopularFragment(){
        currentFragment = mMostPopularFragment;

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        MostPopularFragment mostPopularFragment = new MostPopularFragment();

        ActivityUtils.AddFragmentToActivity(getSupportFragmentManager(),
                mostPopularFragment, R.id.container);
    }

    private void addFavouriteFragment(){
        currentFragment = mFavouriteFragment;

        for (int i = 0; i < getSupportFragmentManager().getBackStackEntryCount(); i++) {
            getSupportFragmentManager().popBackStack();
        }

        FavouriteFragment mostPopularFragment = new FavouriteFragment();

        ActivityUtils.AddFragmentToActivity(getSupportFragmentManager(),
                mostPopularFragment, R.id.container);
    }

    private void selectLastFragment(){

        switch (currentFragment){
            case mTopRatedFragment:
                addTopRatedFragment();
                break;
            case mMostPopularFragment:
                addMostPopularFragment();
                break;
            case mFavouriteFragment:
                addFavouriteFragment();
                break;
            default:
                addTopRatedFragment();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(CURRENT_FRAGMENT,currentFragment);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        currentFragment = savedInstanceState.getString(CURRENT_FRAGMENT);
        listState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        Log.d(TAG, "onRestoreInstanceState: " + currentFragment);
        super.onRestoreInstanceState(savedInstanceState);
    }
}



