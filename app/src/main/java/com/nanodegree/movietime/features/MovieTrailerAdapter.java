package com.nanodegree.movietime.features;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nanodegree.movietime.R;
import com.nanodegree.movietime.data.model.OnItemClickListenerTrailer;
import com.nanodegree.movietime.data.model.TrailerResults;

import java.util.ArrayList;

/**
 * Created by ali19 on 3/24/2018.
 */

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<TrailerResults> results;
    private final OnItemClickListenerTrailer listener;

    MovieTrailerAdapter(Context mContext, ArrayList<TrailerResults> results , OnItemClickListenerTrailer listener) {
        super();
        this.mContext = mContext;
        this.results = results;
        this.listener = listener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.movie_trailer_item, parent, false);
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

        TextView tvTitle;
        ImageView ivPlayer;

        ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_trailer_title);
            ivPlayer = itemView.findViewById(R.id.iv_player);
            ivPlayer.setOnClickListener(this);
            tvTitle.setOnClickListener(this);
        }

        private void onBind(int position) {
            String trailerName = results.get(position).getName();
            tvTitle.setText(trailerName);
        }

        @Override
        public void onClick(View view) {
            if (view == ivPlayer || view == tvTitle) {
                    int position = getAdapterPosition();
                    listener.onItemClick(results,position);
            }
        }
    }
}
