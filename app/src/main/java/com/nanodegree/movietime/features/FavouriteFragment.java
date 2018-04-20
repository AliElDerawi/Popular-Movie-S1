package com.nanodegree.movietime.features;


import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.util.Contracts.FavouriteMovieEntry;
import com.nanodegree.movietime.util.FavouriteMovieDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private RelativeLayout internetLayout;
    private ProgressBar mProgressBar ;
    @BindView(R.id.rv_favourite)
    RecyclerView rvFavourite;
    @BindView(R.id.tv_favourite)
    TextView tvFavourite;

    private SQLiteDatabase sqLiteDatabase;
    private MovieFavouriteAdapter movieFavouriteAdapter;
    private Cursor mCursor;
    private static final int FAVOURITE_LOADER_ID = 0;

    public FavouriteFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_favourite, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = getActivity().findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        internetLayout = getActivity().findViewById(R.id.layout_no_internet);
        internetLayout.setVisibility(View.GONE);

        FavouriteMovieDbHelper dbHelper  = new FavouriteMovieDbHelper(view.getContext());
        sqLiteDatabase = dbHelper.getWritableDatabase();
        rvFavourite.setLayoutManager(new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false));
        rvFavourite.setNestedScrollingEnabled(true);



        mCursor = getAllMovie();
        if (mCursor.getCount() > 0){
            movieFavouriteAdapter = new MovieFavouriteAdapter(view.getContext(),mCursor);
            rvFavourite.setHasFixedSize(true);
            rvFavourite.setAdapter(movieFavouriteAdapter);

        } else {
            rvFavourite.setVisibility(View.GONE);
            tvFavourite.setVisibility(View.VISIBLE);
        }

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                long id = (long)viewHolder.itemView.getTag();
                if (removeMovie(id)){
                    movieFavouriteAdapter.swapCursor(getAllMovie());
                    Cursor cursor = getAllMovie();
                    if (cursor.getCount() == 0 ) {
                        rvFavourite.setVisibility(View.GONE);
                        tvFavourite.setVisibility(View.VISIBLE);
                    }
                }
            }
        }).attachToRecyclerView(rvFavourite);
    }

    private Cursor getAllMovie(){
        return sqLiteDatabase.query(
                FavouriteMovieEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                FavouriteMovieEntry.COLUMN_MOVIE_RATING + " DESC"
        );
    }

    private boolean removeMovie(long id){
        return sqLiteDatabase.delete(FavouriteMovieEntry.TABLE_NAME,FavouriteMovieEntry._ID + "=" + id,null)> 0 ;
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
