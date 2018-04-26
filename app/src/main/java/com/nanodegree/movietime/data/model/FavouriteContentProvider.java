package com.nanodegree.movietime.data.model;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.nanodegree.movietime.util.Contracts;
import com.nanodegree.movietime.util.FavouriteMovieDbHelper;

import static com.nanodegree.movietime.util.Contracts.FavouriteMovieEntry.TABLE_NAME;

public class FavouriteContentProvider extends ContentProvider {

    private FavouriteMovieDbHelper mFavouriteDbHelper;
    public static final int FAVOURITE = 100;
    public static final int FAVOURITE_WITH_ID = 101;

    private static UriMatcher sUriMatcher = buildUriMatcher();


    public static UriMatcher buildUriMatcher(){
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.PATH_FAVOURITE,FAVOURITE);
        uriMatcher.addURI(Contracts.AUTHORITY,Contracts.PATH_FAVOURITE + "/#",FAVOURITE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavouriteDbHelper = new FavouriteMovieDbHelper(context);
        return true;
    }


    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection,
                        @Nullable String selection, @Nullable String[] selectionArgs,
                        @Nullable String sortOrder) {

        final SQLiteDatabase sqLiteDatabase = mFavouriteDbHelper.getReadableDatabase();
        Cursor mReturnCursor;
        int match = sUriMatcher.match(uri);
        switch (match){
            case FAVOURITE:
                mReturnCursor = sqLiteDatabase.query(TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                        );

                break;

            case FAVOURITE_WITH_ID:
                String id = uri.getPathSegments().get(1);
                String mSelection = "movieId=?";
                String[] mSelectionArgs = new String[]{id};
                mReturnCursor = sqLiteDatabase.query(TABLE_NAME,
                        projection,
                        mSelection,
                        mSelectionArgs,
                        null,
                        null,
                        null
                        );
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri:" + uri);
        }

        mReturnCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return mReturnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = mFavouriteDbHelper.getWritableDatabase();
        Uri returnUri;
        int match = sUriMatcher.match(uri);
        switch (match){
            case FAVOURITE:
                long id = sqLiteDatabase.insert(TABLE_NAME,null,contentValues);
                if (id >0){
                    returnUri = ContentUris.withAppendedId(uri,id);
                    getContext().getContentResolver().notifyChange(uri,null);
                }else {
                    throw new SQLException("Fail to insert row into " + uri);
                }
                break;

            default:
                throw  new UnsupportedOperationException("Unknown uri : " + uri);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase sqLiteDatabase = mFavouriteDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        String id = uri.getPathSegments().get(1);
        int returnNum;
        String mSelection = "_id=?";
        String[] mSelectionArgs = {id};
        switch (match){
            case FAVOURITE_WITH_ID:
                returnNum = sqLiteDatabase.delete(TABLE_NAME,mSelection,mSelectionArgs);
                if (returnNum >0)
                    getContext().getContentResolver().notifyChange(uri,null);
                break;

                default:
                    throw new UnsupportedOperationException("Unsupported Operation for the Uri: " + uri );
        }
        return returnNum;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }


}
