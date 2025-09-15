package com.example.drinkmate.ui.nonalcoholic;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.drinkmate.viewmodel.NonAlcoholicDrinksViewModel;

import java.util.ArrayList;

public class NonAlcoholicDrinksFragment extends Fragment {

    private NonAlcoholicDrinksViewModel nonAlcoholicDrinksViewModel;
    private RecyclerView recyclerViewNonAlcoholicDrinks;
    private DrinkAdapter drinkAdapter;
    private ProgressBar progressBarNonAlcoholic;
    private TextView textViewEmptyNonAlcoholic;

    public NonAlcoholicDrinksFragment() {
        // Erforderlicher leerer öffentlicher Konstruktor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        nonAlcoholicDrinksViewModel = new ViewModelProvider(this).get(NonAlcoholicDrinksViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_non_alcoholic_drinks, container, false);

        recyclerViewNonAlcoholicDrinks = view.findViewById(R.id.recyclerViewNonAlcoholicDrinks);
        progressBarNonAlcoholic = view.findViewById(R.id.progressBarNonAlcoholic);
        textViewEmptyNonAlcoholic = view.findViewById(R.id.textViewEmptyNonAlcoholic);

        setupRecyclerView();
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
        recyclerViewNonAlcoholicDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewNonAlcoholicDrinks.setAdapter(drinkAdapter);
    }

    private void observeViewModel() {
        nonAlcoholicDrinksViewModel.getDrinksLiveData().observe(getViewLifecycleOwner(), drinks -> {
            if (drinks != null) {
                drinkAdapter.setDrinks(drinks);
                textViewEmptyNonAlcoholic.setVisibility(View.GONE);
                recyclerViewNonAlcoholicDrinks.setVisibility(View.VISIBLE);
            }
        });

        nonAlcoholicDrinksViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBarNonAlcoholic.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    recyclerViewNonAlcoholicDrinks.setVisibility(View.GONE);
                    textViewEmptyNonAlcoholic.setVisibility(View.GONE);
                }
            }
        });

        nonAlcoholicDrinksViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Boolean isLoading = nonAlcoholicDrinksViewModel.getIsLoadingLiveData().getValue();
                if (isLoading == null || !isLoading) {
                    if (drinkAdapter.getItemCount() == 0) {
                        textViewEmptyNonAlcoholic.setText(errorMessage);
                        textViewEmptyNonAlcoholic.setVisibility(View.VISIBLE);
                        recyclerViewNonAlcoholicDrinks.setVisibility(View.GONE);
                    } else {
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                if (drinkAdapter.getItemCount() == 0 && (nonAlcoholicDrinksViewModel.getIsLoadingLiveData().getValue() == null || !nonAlcoholicDrinksViewModel.getIsLoadingLiveData().getValue())) {
                    // textViewEmptyNonAlcoholic.setVisibility(View.GONE);
                }
            }
        });
    }
}
