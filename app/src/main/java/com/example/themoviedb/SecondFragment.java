package com.example.themoviedb;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.themoviedb.databinding.FragmentSecondBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SecondFragment extends Fragment {

    private CustomAdapter adapter;
    private FragmentSecondBinding binding;
    private MovieDBInterface apiInterface;
    private int movieId;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentSecondBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        if (getArguments() != null) {
            movieId = getArguments().getInt("id", 0);
        }

        apiInterface = RetrofitClient.getClient();

        RecyclerView rvMovieDetails = binding.rvMovieDetails;
        rvMovieDetails.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new CustomAdapter(new ArrayList<>(), requireContext(), null, true, R.layout.rv_pelis_details);
        rvMovieDetails.setAdapter(adapter);

        getMovie(movieId);

        return view;
    }

    public void getMovie(int movieId) {
        Call<Movie> call = apiInterface.getMovieDetails(movieId, RetrofitClient.getApiKey());
        call.enqueue(new Callback<Movie>() {
            @Override
            public void onResponse(Call<Movie> call, Response<Movie> response) {
                Movie movieDetails = response.body();
                List<Movie> movies = new ArrayList<>();
                movies.add(movieDetails);
                adapter.setMovies(movies);
            }

            @Override
            public void onFailure(Call<Movie> call, Throwable t) {
                Toast.makeText(requireContext(), "An error occurred while retrieving the movie details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
