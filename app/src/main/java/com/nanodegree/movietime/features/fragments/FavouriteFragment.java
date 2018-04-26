package com.nanodegree.movietime.features.fragments;


import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.features.adapters.MovieFavouriteAdapter;
import com.nanodegree.movietime.util.Contracts.FavouriteMovieEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.nanodegree.movietime.features.activities.HomeActivity.CURRENT_FRAGMENT;
import static com.nanodegree.movietime.util.Contracts.currentFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RelativeLayout internetLayout;
    private ProgressBar mProgressBar;
    @BindView(R.id.rv_favourite)
    RecyclerView rvFavourite;
    @BindView(R.id.tv_favourite)
    TextView tvFavourite;

    private MovieFavouriteAdapter movieFavouriteAdapter;
    private Cursor mCursor;
    private static final int FAVOURITE_LOADER_ID = 0;
    private static final String TAG = "FavouriteFragment";

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = getActivity().findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        internetLayout = getActivity().findViewById(R.id.layout_no_internet);
        internetLayout.setVisibility(View.GONE);

        rvFavourite.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.VERTICAL, false));
        rvFavourite.setNestedScrollingEnabled(true);

        movieFavouriteAdapter = new MovieFavouriteAdapter(view.getContext());
        rvFavourite.setHasFixedSize(true);
        rvFavourite.setAdapter(movieFavouriteAdapter);


        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long) viewHolder.itemView.getTag();
                if (removeMovie(id) > 0) {
                    getActivity().getSupportLoaderManager().restartLoader(FAVOURITE_LOADER_ID, null, FavouriteFragment.this);
                } else {
                    Toast.makeText(getContext(), "Sorry some error happen !", Toast.LENGTH_LONG).show();
                }
            }
        }).attachToRecyclerView(rvFavourite);

        getActivity().getSupportLoaderManager().initLoader(FAVOURITE_LOADER_ID, null, this);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null)
            getActivity().getSupportLoaderManager().restartLoader(FAVOURITE_LOADER_ID, null, this);
    }


    private int removeMovie(long id) {
        Uri uri = FavouriteMovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(String.valueOf(id)).build();
        return getActivity().getContentResolver().delete(uri, null, null);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<Cursor>(getContext()) {
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
                    return getActivity().getContentResolver().query(FavouriteMovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavouriteMovieEntry.COLUMN_MOVIE_RATING + " DESC");
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
        if (data.getCount() == 0) {
            rvFavourite.setVisibility(View.GONE);
            tvFavourite.setVisibility(View.VISIBLE);
        } else {
            movieFavouriteAdapter.swapCursor(data);
        }
    }


    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        movieFavouriteAdapter.swapCursor(null);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(CURRENT_FRAGMENT,currentFragment);
        super.onSaveInstanceState(outState);
    }
}
