package com.nanodegree.movietime.features;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
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
import com.nanodegree.movietime.util.MySingleton;

import org.json.JSONObject;

import java.util.ArrayList;

import static com.android.volley.Request.Method.GET;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEDATE;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEID;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEOVERVIEW;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIEPOSTERPATH;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIERATING;
import static com.nanodegree.movietime.features.MoviePosterAdapter.MOVIETITLE;
import static com.nanodegree.movietime.util.Contracts.BASE_URL;
import static com.nanodegree.movietime.util.Contracts.TOP_RATED_MOVIE;

/**
 * A simple {@link Fragment} subclass.
 */
public class TopRatedFragment extends Fragment implements View.OnClickListener{

    private RecyclerView rvImagePoster;
    private RelativeLayout internetLayout;
    private ProgressBar mProgressBar ;
    private Button resetConnection;
    private ArrayList<MovieResults> results = new ArrayList<>();
    private final String TAG = "TopRatedFragment";
    public TopRatedFragment() {
        // Required empty public constructor
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_top_rated, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        ButterKnife.bind(this,view);

        rvImagePoster = view.findViewById(R.id.rv_image_poster);
        mProgressBar = getActivity().findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        internetLayout = getActivity().findViewById(R.id.layout_no_internet);
        resetConnection = getActivity().findViewById(R.id.btn_reset_connection);
        resetConnection.setOnClickListener(this);
        internetLayout.setVisibility(View.GONE);

        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),
                getResources().getInteger(R.integer.column_span));
//        rvImagePoster.setLayoutManager(new GridLayoutManager(getContext(),2,GridLayoutManager.HORIZONTAL,false));
        rvImagePoster.setLayoutManager(layoutManager);
        rvImagePoster.setNestedScrollingEnabled(true);
        requestVideo();

    }

    private void requestVideo(){
        if (isOnline()){
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
//                        ArrayList<String> imageUrl = new ArrayList<>();
//                        ArrayList<String> imgId = new ArrayList<>();
//
//                        for (int i  = 0 ; i < data.size();i++){
//                            imageUrl.add(data.get(i).getPosterPath());
//                            imgId.add(String.valueOf(data.get(i).getId())
//                        }
                        Log.d(TAG, "onResponse: mResult.size > " + results.size());
                        MoviePosterAdapter moviePosterAdapter = new MoviePosterAdapter(getContext(), results, new OnItemClickListener() {
                            @Override
                            public void onItemClick(ArrayList<MovieResults> results, int position) {
                                Intent toMovieDetailIntent = new Intent(getContext(),MovieDetail.class);
                                toMovieDetailIntent.putExtra(MOVIEPOSTERPATH,results.get(position).getPosterPath());
                                toMovieDetailIntent.putExtra(MOVIEOVERVIEW,results.get(position).getOverview());
                                toMovieDetailIntent.putExtra(MOVIERATING,results.get(position).getAverageScore());
                                toMovieDetailIntent.putExtra(MOVIETITLE,results.get(position).getTitle().trim());
                                toMovieDetailIntent.putExtra(MOVIEDATE,results.get(position).getReleaseDate());
                                toMovieDetailIntent.putExtra(MOVIEID,results.get(position).getId());
                                startActivity(toMovieDetailIntent);
                            }
                        });
                        rvImagePoster.setAdapter(moviePosterAdapter);
                        rvImagePoster.setHasFixedSize(true);

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

    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View view) {
        if (view == resetConnection){
            if (isOnline()){
                internetLayout.setVisibility(View.GONE);
                requestTopRatedMovie();
            } else {
                Toast.makeText(view.getContext(),getContext().getResources().getString(R.string.no_internet_message),Toast.LENGTH_LONG).show();
            }
        }
    }
}
