package com.nanodegree.movietime.features.fragments;


import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.gson.Gson;
import com.nanodegree.movietime.BuildConfig;
import com.nanodegree.movietime.R;
import com.nanodegree.movietime.data.model.MovieResults;
import com.nanodegree.movietime.data.model.OnItemClickListener;
import com.nanodegree.movietime.data.model.request.MovieRequest;
import com.nanodegree.movietime.features.activities.MovieDetailActivity;
import com.nanodegree.movietime.features.adapters.MoviePosterAdapter;
import com.nanodegree.movietime.util.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.android.volley.Request.Method.GET;
import static com.nanodegree.movietime.features.activities.HomeActivity.CURRENT_FRAGMENT;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_DATE;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_ID;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_OVERVIEW;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_POSTER_PATH;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_RATING;
import static com.nanodegree.movietime.features.adapters.MoviePosterAdapter.MOVIE_TITLE;
import static com.nanodegree.movietime.util.ActivityUtils.isOnline;
import static com.nanodegree.movietime.util.ActivityUtils.showSnackBar;
import static com.nanodegree.movietime.util.Contracts.BASE_URL;
import static com.nanodegree.movietime.util.Contracts.BUNDLE_RECYCLER_LAYOUT;
import static com.nanodegree.movietime.util.Contracts.TOP_RATED_MOVIE;
import static com.nanodegree.movietime.util.Contracts.currentFragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopRatedFragment extends Fragment implements View.OnClickListener{
    @BindView(R.id.rv_image_poster)
    RecyclerView rvImagePoster;
    private RelativeLayout internetLayout;
    private ProgressBar mProgressBar ;
    private Button resetConnection;
    private ArrayList<MovieResults> results = new ArrayList<>();
    private final String TAG = "TopRatedFragment";
    public TopRatedFragment() {
        // Required empty public constructor
    }

    private Parcelable listState = null;
    private GridLayoutManager layoutManager;
    private int position ;



    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_top_rated, container, false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mProgressBar = getActivity().findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        internetLayout = getActivity().findViewById(R.id.layout_no_internet);
        resetConnection = getActivity().findViewById(R.id.btn_reset_connection);
        resetConnection.setOnClickListener(this);
        internetLayout.setVisibility(View.GONE);

        layoutManager = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.column_span));

        rvImagePoster.setLayoutManager(layoutManager);
        rvImagePoster.setNestedScrollingEnabled(true);


        requestVideo();

    }

    private void requestVideo(){
        if (isOnline(getContext())){
            requestTopRatedMovie();
        }else{
            internetLayout.setVisibility(View.VISIBLE);
        }
    }

    private void requestTopRatedMovie(){

        mProgressBar.setVisibility(View.VISIBLE);
        String apiKey = BuildConfig.API_KEY;
        String url = BASE_URL + TOP_RATED_MOVIE +"?api_key="+ apiKey;
        JSONObject body = new JSONObject();

        Log.d(TAG, "TopRatedFragment: url > " + url);


        final JsonObjectRequest request = new JsonObjectRequest(GET, url, body,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        Log.d(TAG,"TopRatedFragment: response > " + response.toString());

                        mProgressBar.setVisibility(View.GONE);

                        MovieRequest movieRequest =
                                new Gson().fromJson(response.toString(), MovieRequest.class);

                        results.addAll(movieRequest.getData());

                        Log.d(TAG, "onResponse: mResult.size > " + results.size());
                        MoviePosterAdapter moviePosterAdapter = new MoviePosterAdapter(getContext(), results, new OnItemClickListener() {
                            @Override
                            public void onItemClick(int position) {
                                Intent toMovieDetailIntent = new Intent(getContext(),MovieDetailActivity.class);
                                toMovieDetailIntent.putExtra(MOVIE_POSTER_PATH,results.get(position).getPosterPath());
                                toMovieDetailIntent.putExtra(MOVIE_OVERVIEW,results.get(position).getOverview());
                                toMovieDetailIntent.putExtra(MOVIE_RATING,results.get(position).getAverageScore());
                                toMovieDetailIntent.putExtra(MOVIE_TITLE,results.get(position).getTitle().trim());
                                toMovieDetailIntent.putExtra(MOVIE_DATE,results.get(position).getReleaseDate());
                                toMovieDetailIntent.putExtra(MOVIE_ID,results.get(position).getId());
                                startActivity(toMovieDetailIntent);
                            }
                        });

                        rvImagePoster.setAdapter(moviePosterAdapter);
                        rvImagePoster.setHasFixedSize(true);

                        if (listState != null){
                            rvImagePoster.getLayoutManager().onRestoreInstanceState(listState);
                            Log.d("Test02","ListState: " + listState);
                            listState = null;
                        }

                    }


                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                mProgressBar.setVisibility(View.GONE);
                if (isAdded())
                Toast.makeText(getActivity(), getString(R.string.sorry_error_happen),
                        Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(getContext()).addToRequestQueue(request);
    }


    @Override
    public void onClick(View view) {
        if (view == resetConnection){
            if (isOnline(view.getContext())){
                internetLayout.setVisibility(View.GONE);
                requestTopRatedMovie();
            } else {
                showSnackBar(getContext(),view,getContext().getResources().getString(R.string.no_internet_message));
            }
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(CURRENT_FRAGMENT,currentFragment);

        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, layoutManager.onSaveInstanceState());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null)
        listState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
    }
}

