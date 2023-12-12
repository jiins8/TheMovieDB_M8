package com.example.themoviedb;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> implements Filterable {

    private List<Movie> movies;
    private List<Movie> moviesList;
    private Context context;
    private OnItemClickListener onItemClickListener;
    private boolean showOverview;
    private int layoutResourceId;

    public interface OnItemClickListener {
        void onImageClick(int position);

        void onTitleClick(int position);
    }
    public CustomAdapter(List<Movie> movies, Context context, OnItemClickListener onItemClickListener, boolean showOverview, int layoutResourceId) {
        this.context = context;
        this.movies = movies;
        this.onItemClickListener = onItemClickListener;
        this.showOverview = showOverview;
        this.layoutResourceId = layoutResourceId;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPoster, ivGridPoster;
        private TextView tvTitle, tvReleaseDate, tvOverview, tvVoteAverage;

        public ViewHolder(View view) {
            super(view);

            ivPoster = view.findViewById(R.id.ivPoster);
            tvTitle = view.findViewById(R.id.tvTitle);
            tvReleaseDate = view.findViewById(R.id.tvReleaseDate);
            tvOverview = view.findViewById(R.id.tvOverview);
            tvVoteAverage = view.findViewById(R.id.tvVoteAverage);
            ivGridPoster = view.findViewById(R.id.ivGridPoster);
        }

        public void bindData(Movie movie, boolean showOverview) {
            if (ivPoster != null) {
                tvTitle.setText(movie.getTitle());
                tvReleaseDate.setText("Release Date: " + movie.getReleaseDate());
                tvVoteAverage.setText("Ratings: " + String.format(Locale.getDefault(), "%.1f%%", movie.getVoteAverage() * 10));

                if (showOverview) {
                    tvOverview.setVisibility(View.VISIBLE);
                    tvOverview.setText("Overview: " + movie.getOverview());
                } else {
                    tvOverview.setVisibility(View.GONE);
                }

                Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movie.getPosterPath()).into(ivPoster);
            } else if (ivGridPoster != null) {
                tvTitle.setText(movie.getTitle());
                Picasso.get().load("https://image.tmdb.org/t/p/w500/" + movie.getPosterPath()).into(ivGridPoster);
            }
        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                List<Movie> filteredList = new ArrayList<>();

                if (constraint == null || constraint.length() == 0) {
                    filteredList.addAll(moviesList);
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();

                    filteredList.addAll(moviesList.stream()
                            .filter(movie -> movie.getTitle().toLowerCase().contains(filterPattern))
                            .collect(Collectors.toList()));
                }

                FilterResults results = new FilterResults();
                results.values = filteredList;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                movies.clear();
                movies.addAll((List<Movie>) results.values);
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutResourceId, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Movie movie = movies.get(position);
        holder.bindData(movie, showOverview);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onImageClick(position);
            }
        });

        holder.tvTitle.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onTitleClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
        this.moviesList = new ArrayList<>(movies);  
        notifyDataSetChanged();
    }

    public List<Movie> getMovies() {
        return movies;
    }

    @Override
    public int getItemViewType(int position) {
        return layoutResourceId;
    }
}
