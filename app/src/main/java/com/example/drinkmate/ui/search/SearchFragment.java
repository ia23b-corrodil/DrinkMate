package com.example.drinkmate.ui.search;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drinkmate.R;
import com.example.drinkmate.adapter.DrinkAdapter;
import com.example.drinkmate.model.Drink;
import com.example.drinkmate.DrinkDetailActivity;
import com.example.drinkmate.viewmodel.SearchViewModel;

import java.util.ArrayList;

public class SearchFragment extends Fragment {

    private SearchViewModel searchViewModel;
    private EditText editTextSearch;
    private RecyclerView recyclerViewDrinks;
    private DrinkAdapter drinkAdapter;
    private ProgressBar progressBar;
    private TextView textViewEmptySearch;

    public SearchFragment() {
        // Erforderlicher leerer öffentlicher Konstruktor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel = new ViewModelProvider(this).get(SearchViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editTextSearch = view.findViewById(R.id.editTextSearchFragment);
        recyclerViewDrinks = view.findViewById(R.id.recyclerViewDrinksFragment);
        progressBar = view.findViewById(R.id.progressBarFragment);
        textViewEmptySearch = view.findViewById(R.id.textViewEmptySearch);

        setupRecyclerView();
        setupSearchEditText();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        observeViewModel();
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        drinkAdapter = new DrinkAdapter(getContext(), new ArrayList<>(), drink -> {
            Intent intent = new Intent(getActivity(), DrinkDetailActivity.class);
            intent.putExtra(DrinkDetailActivity.EXTRA_DRINK_ID, drink.getIdDrink());
            startActivity(intent);
        });

        recyclerViewDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewDrinks.setAdapter(drinkAdapter);
    }

    private void setupSearchEditText() {
        editTextSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });
    }

    private void performSearch() {
        String query = editTextSearch.getText().toString().trim();
        if (!TextUtils.isEmpty(query)) {
            searchViewModel.searchDrinksByName(query);
        } else {
            Toast.makeText(getContext(), "Please enter a search term", Toast.LENGTH_SHORT).show();
            searchViewModel.clearSearch(); // Leert vorherige Ergebnisse, falls die Suche leer ist
        }
    }

    private void observeViewModel() {
        searchViewModel.getDrinksLiveData().observe(getViewLifecycleOwner(), drinks -> {
            if (drinks != null) {
                drinkAdapter.setDrinks(drinks);
                textViewEmptySearch.setVisibility(View.GONE);
                recyclerViewDrinks.setVisibility(View.VISIBLE);
            }
        });

        searchViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    recyclerViewDrinks.setVisibility(View.GONE);
                    textViewEmptySearch.setVisibility(View.GONE);
                }
            }
        });

        searchViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Boolean isLoading = searchViewModel.getIsLoadingLiveData().getValue();
                if (isLoading == null || !isLoading) {
                     if (drinkAdapter.getItemCount() == 0) {
                        textViewEmptySearch.setText(errorMessage);
                        textViewEmptySearch.setVisibility(View.VISIBLE);
                        recyclerViewDrinks.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                     }
                }
            } else {
                if (drinkAdapter.getItemCount() == 0 && (searchViewModel.getIsLoadingLiveData().getValue() == null || !searchViewModel.getIsLoadingLiveData().getValue())) {
                    // textViewEmptySearch.setVisibility(View.GONE); // Or handle this explicitly
                }
            }
        });
    }
}
