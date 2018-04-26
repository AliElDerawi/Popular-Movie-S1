package com.nanodegree.movietime.features.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.data.model.OnItemClickListener;
import com.nanodegree.movietime.data.model.request.ReviewResults;

import java.util.ArrayList;

/**
 * Created by ali19 on 3/24/2018.
 */

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<ReviewResults> results;
    private final OnItemClickListener listener;

    public MovieReviewAdapter(Context mContext, ArrayList<ReviewResults> results, OnItemClickListener listener) {
        super();
        this.mContext = mContext;
        this.results = results;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_review_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvAuthor;
        TextView tvReview;
        TextView tvReadOnline;

        ViewHolder(View itemView) {
            super(itemView);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvReadOnline = itemView.findViewById(R.id.tv_read_online);
            tvReview = itemView.findViewById(R.id.tv_review);
            tvReadOnline.setOnClickListener(this);
        }

        private void onBind(int position) {
            String movieReview = results.get(position).getContent();
            tvReview.setText(movieReview);
            String movieAuthor = results.get(position).getAuthor();
            CharSequence concat = TextUtils.concat("  ", movieAuthor);
            tvAuthor.setText(concat);
            tvReadOnline.setPaintFlags(tvReadOnline.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        }

        @Override
        public void onClick(View view) {
            if (view == tvReadOnline) {
                    int position = getAdapterPosition();
                    listener.onItemClick(position);
            }
        }
    }
}
