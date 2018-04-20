package com.nanodegree.movietime.util;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nanodegree.movietime.util.Contracts.*;

public class FavouriteMovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favouritemovie.db";
    private static final int DATABASE_VERSION = 1;


    public FavouriteMovieDbHelper(Context context){
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_FAVOURITEMOVIE_TABLE = "CREATE TABLE " + FavouriteMovieEntry.TABLE_NAME
                + " ("
                + FavouriteMovieEntry._ID  + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + FavouriteMovieEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL,"
                + FavouriteMovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL,"
                + FavouriteMovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL,"
                + FavouriteMovieEntry.COLUMN_MOVIE_RATING + " REAL NOT NULL,"
                + FavouriteMovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL,"
                + FavouriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL"
                + "); ";

            sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITEMOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteMovieEntry.TABLE_NAME );
        onCreate(sqLiteDatabase);
    }
}
