package com.nanodegree.movietime.features;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.util.Contracts.FavouriteMovieEntry;
import com.nanodegree.movietime.util.GlideApp;


public class MovieFavouriteAdapter extends RecyclerView.Adapter<MovieFavouriteAdapter.ViewHolder> {
    private Context mContext;
    private Cursor mCursor;

    public MovieFavouriteAdapter(Context context , Cursor cursor){
        this.mContext = context;
        this.mCursor = cursor;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_favourite_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(position);
    }

    public void swapCursor(Cursor newCursor){
        if (mCursor != null)
            mCursor.close();

        if (newCursor != null) {
            mCursor = newCursor;
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return mCursor.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle;
        TextView tvDate;
        TextView tvRating;
        TextView tvOverview;
        ImageView ivPoster;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDate = itemView.findViewById(R.id.tv_date);
            tvRating = itemView.findViewById(R.id.tv_rate);
            tvOverview = itemView.findViewById(R.id.tv_overview);
            ivPoster = itemView.findViewById(R.id.iv_poster);
        }

        private void onBind( int position ){
            if (!mCursor.moveToPosition(position))
                return;

            itemView.setTag(mCursor.getLong(mCursor.getColumnIndex(FavouriteMovieEntry._ID)));
            tvTitle.setText(mCursor.getString(mCursor.getColumnIndex(FavouriteMovieEntry.COLUMN_MOVIE_TITLE)));
            tvOverview.setText(mCursor.getString(mCursor.getColumnIndex(FavouriteMovieEntry.COLUMN_MOVIE_OVERVIEW)));
            tvDate.setText(mCursor.getString(mCursor.getColumnIndex(FavouriteMovieEntry.COLUMN_MOVIE_RELEASE_DATE)));
            tvRating.setText(String.valueOf(mCursor.getFloat(mCursor.getColumnIndex(FavouriteMovieEntry.COLUMN_MOVIE_RATING))));
            GlideApp.with(mContext).load(mCursor.getString(mCursor.getColumnIndex(FavouriteMovieEntry.COLUMN_MOVIE_POSTER))).error(R.drawable.placeholder_poster).placeholder(R.drawable.placeholder_poster).into(ivPoster);
        }
    }
}
