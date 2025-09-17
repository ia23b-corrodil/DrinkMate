package com.example.drinkmate.ui.alcoholic;

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
import com.example.drinkmate.viewmodel.AlcoholicDrinksViewModel;
import java.util.ArrayList;

/**
 * Ein Fragment zur Anzeige einer Liste alkoholischer Getränke.
 * Es verwendet {@link AlcoholicDrinksViewModel} zum Laden und Verwalten der Getränkedaten
 * und einen {@link DrinkAdapter} zur Darstellung der Getränke in einer RecyclerView.
 * Das Fragment zeigt Ladeindikatoren, Fehlermeldungen und leere Zustände an.
 * Beim Klicken auf ein Getränk wird die {@link DrinkDetailActivity} gestartet.
 */
public class AlcoholicDrinksFragment extends Fragment {

    private AlcoholicDrinksViewModel alcoholicDrinksViewModel;
    private RecyclerView recyclerViewAlcoholicDrinks;
    private DrinkAdapter drinkAdapter;
    private ProgressBar progressBarAlcoholic;
    private TextView textViewEmptyAlcoholic;
    // private TextView textViewTitleAlcoholic; // Kann referenziert werden, wenn dynamische Änderungen nötig sind

    public AlcoholicDrinksFragment() {
        // Erforderlicher leerer öffentlicher Konstruktor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        alcoholicDrinksViewModel = new ViewModelProvider(this).get(AlcoholicDrinksViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_alcoholic_drinks, container, false);

        recyclerViewAlcoholicDrinks = view.findViewById(R.id.recyclerViewAlcoholicDrinks);
        progressBarAlcoholic = view.findViewById(R.id.progressBarAlcoholic);
        textViewEmptyAlcoholic = view.findViewById(R.id.textViewEmptyAlcoholic);
        // textViewTitleAlcoholic = view.findViewById(R.id.textViewTitleAlcoholic);

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
        recyclerViewAlcoholicDrinks.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAlcoholicDrinks.setAdapter(drinkAdapter);
    }

    private void observeViewModel() {
        alcoholicDrinksViewModel.getDrinksLiveData().observe(getViewLifecycleOwner(), drinks -> {
            if (drinks != null) {
                drinkAdapter.setDrinks(drinks);
                textViewEmptyAlcoholic.setVisibility(View.GONE);
                recyclerViewAlcoholicDrinks.setVisibility(View.VISIBLE);
            }
        });

        alcoholicDrinksViewModel.getIsLoadingLiveData().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                progressBarAlcoholic.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    recyclerViewAlcoholicDrinks.setVisibility(View.GONE);
                    textViewEmptyAlcoholic.setVisibility(View.GONE);
                }
            }
        });

        alcoholicDrinksViewModel.getErrorMessageLiveData().observe(getViewLifecycleOwner(), errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                Boolean isLoading = alcoholicDrinksViewModel.getIsLoadingLiveData().getValue();
                if (isLoading == null || !isLoading) {
                    if (drinkAdapter.getItemCount() == 0) { // Nur anzeigen, wenn Liste wirklich leer ist
                        textViewEmptyAlcoholic.setText(errorMessage);
                        textViewEmptyAlcoholic.setVisibility(View.VISIBLE);
                        recyclerViewAlcoholicDrinks.setVisibility(View.GONE);
                    } else {
                        // Optional: Zeige Fehler als Toast, wenn bereits Daten angezeigt werden
                        Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                 // Wenn kein Fehler und Liste leer, sicherstellen, dass textViewEmpty verschwindet
                if (drinkAdapter.getItemCount() == 0 && (alcoholicDrinksViewModel.getIsLoadingLiveData().getValue() == null || !alcoholicDrinksViewModel.getIsLoadingLiveData().getValue())) {
                     //textViewEmptyAlcoholic.setVisibility(View.GONE); // Wird durch drinksLiveData-Observer abgedeckt
                }
            }
        });
    }
}
