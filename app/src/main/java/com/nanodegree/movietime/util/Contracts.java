package com.nanodegree.movietime.util;

import android.provider.BaseColumns;

/**
 * Created by ali19 on 2/18/2018.
 */

public class Contracts {
    public static final String BASE_IMAGE_URL = "https://image.tmdb.org/t/p/";
    public static final String IMAGE_SIZE_POSTER = "w185/";
    public static final String IMAGE_SIZE_FULL = "w500/";
    public static final String BASE_URL = "http://api.themoviedb.org/3/movie/";
    public static final String MOST_POPULAR_MOVIE = "popular";
    public static final String MOVIE_TRAILER = "/videos";
    public static final String MOVIE_REVIEW = "/reviews";
    public static final String TOP_RATED_MOVIE = "top_rated";
    public static final String MY_PREF = "myPref";
    public static final String REVIEW_URL = "reviewUrl";

    private Contracts(){

    }

    public static class FavouriteMovieEntry implements BaseColumns{
        public static final String TABLE_NAME = "favouritemovie";
        public static final String COLUMN_MOVIE_ID = "movieId";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_POSTER = "poster";
        public static final String COLUMN_MOVIE_OVERVIEW = "overview";
        public static final String COLUMN_MOVIE_RATING = "rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "releaseDate";

    }

}
