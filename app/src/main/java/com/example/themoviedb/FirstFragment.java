package com.example.themoviedb;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.themoviedb.databinding.FragmentFirstBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirstFragment extends Fragment implements CustomAdapter.OnItemClickListener {
    private CustomAdapter adapter;
    private MovieDBInterface apiInterface;
    private FragmentFirstBinding binding;
    private static final String PREF_KEY_USER_PREFERENCE = "user_preference";
    private static final String PREF_KEY_VIEW_TYPE = "view_type";

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        binding = FragmentFirstBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        setHasOptionsMenu(true);

        apiInterface = RetrofitClient.getClient();

        boolean isGridView = isGridView();

        RecyclerView rvMovies = binding.rvMovies;

        if (isGridView) {
            rvMovies.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        } else {
            rvMovies.setLayoutManager(new LinearLayoutManager(requireContext()));
        }

        adapter = new CustomAdapter(new ArrayList<>(), requireContext(), this, isGridView, isGridView ? R.layout.gv_pelis_grid : R.layout.rv_pelis_row);
        rvMovies.setAdapter(adapter);

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(
                requireContext(),
                R.array.user_preference,
                android.R.layout.simple_spinner_item
        );

        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.userPreferenceSpinner.setAdapter(spinnerAdapter);

        Spinner userPreferenceSpinner = binding.userPreferenceSpinner;
        userPreferenceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int pos, long id) {
                String selectedPreference = parentView.getItemAtPosition(pos).toString();

                saveUserPreference(selectedPreference);

                if (selectedPreference.equals("Ratings")) {
                    getTopRatedMoviesList();
                } else {
                    getPopularMoviesList();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });

        String savedPreference = getUserPreference();
        int position = spinnerAdapter.getPosition(savedPreference);
        userPreferenceSpinner.setSelection(position);

        ImageButton gridViewButton = binding.gridViewButton;
        ImageButton recyclerViewButton = binding.recyclerViewButton;

        gridViewButton.setOnClickListener(v -> switchToGrid());
        recyclerViewButton.setOnClickListener(v -> switchToList());

        return view;
    }

    private void saveUserPreference(String preference) {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(PREF_KEY_USER_PREFERENCE, preference);
        editor.apply();
    }

    private String getUserPreference() {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        return preferences.getString(PREF_KEY_USER_PREFERENCE, "Popularity");
    }

    private void saveViewType(boolean isGridView) {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_KEY_VIEW_TYPE, isGridView);
        editor.apply();
    }

    private boolean isGridView() {
        SharedPreferences preferences = requireActivity().getPreferences(Context.MODE_PRIVATE);
        return preferences.getBoolean(PREF_KEY_VIEW_TYPE, false);
    }

    private void switchToGrid() {
        RecyclerView rvMovies = binding.rvMovies;
        rvMovies.setLayoutManager(new GridLayoutManager(requireContext(), 2));

        adapter = new CustomAdapter(adapter.getMovies(), requireContext(), this, true, R.layout.gv_pelis_grid);
        rvMovies.setAdapter(adapter);

        saveViewType(true);
    }

    private void switchToList() {
        RecyclerView rvMovies = binding.rvMovies;
        rvMovies.setLayoutManager(new LinearLayoutManager(requireContext()));

        adapter = new CustomAdapter(adapter.getMovies(), requireContext(), this, false, R.layout.rv_pelis_row);
        rvMovies.setAdapter(adapter);

        saveViewType(false);
    }

    public void getTopRatedMoviesList() {
        Call<MoviesList> call = apiInterface.getTopRatedMoviesList(RetrofitClient.getApiKey());
        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                List<Movie> movies = response.body().getResults();
                adapter.setMovies(movies);
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Toast.makeText(requireContext(), "An error occurred while retrieving the top-rated movies", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getPopularMoviesList() {
        Call<MoviesList> call = apiInterface.getPopularMoviesList(RetrofitClient.getApiKey());
        call.enqueue(new Callback<MoviesList>() {
            @Override
            public void onResponse(Call<MoviesList> call, Response<MoviesList> response) {
                List<Movie> movies = response.body().getResults();
                adapter.setMovies(movies);
            }

            @Override
            public void onFailure(Call<MoviesList> call, Throwable t) {
                Toast.makeText(requireContext(), "An error occurred while retrieving the most popular movies", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void navigateToSecondFragment(int movieId) {
        Bundle bundle = new Bundle();
        bundle.putInt("id", movieId);
        NavHostFragment.findNavController(FirstFragment.this)
                .navigate(R.id.action_FirstFragment_to_SecondFragment, bundle);
    }

    @Override
    public void onImageClick(int position) {
        int movieId = adapter.getMovies().get(position).getId();
        navigateToSecondFragment(movieId);
    }

    @Override
    public void onTitleClick(int position) {
        int movieId = adapter.getMovies().get(position).getId();
        navigateToSecondFragment(movieId);
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search for a movie");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                return false;
            }
        });
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

