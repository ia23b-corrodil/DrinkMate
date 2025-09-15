package com.example.drinkmate.ui.favorites;

import android.content.Intent; // Hinzugefügt
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.drinkmate.R;
import com.example.drinkmate.DrinkDetailActivity; // Hinzugefügt
import com.example.drinkmate.data.local.FavoriteDrink;
import com.example.drinkmate.viewmodel.FavoritesViewModel;

import java.util.List;

// Implementiert jetzt auch OnFavoriteItemClickListener
public class FavoritesFragment extends Fragment implements
        FavoritesAdapter.OnUnfavoriteClickListener,
        FavoritesAdapter.OnFavoriteItemClickListener {

    private FavoritesViewModel favoritesViewModel;
    private FavoritesAdapter favoritesAdapter;
    private RecyclerView recyclerViewFavorites;
    private ProgressBar progressBarFavorites;
    private TextView textViewEmptyFavorites;
    private TextView textViewFavoritesTitle;

    // Konstante für das neue Extra (muss in DrinkDetailActivity definiert werden)
    // Zur Vereinfachung hier direkt verwendet, besser wäre eine zentrale Konstanten-Datei
    public static final String EXTRA_LOAD_FAVORITE_FROM_DB = "com.example.drinkmate.LOAD_FAVORITE_FROM_DB";


    public FavoritesFragment() {
        // Erforderlicher leerer öffentlicher Konstruktor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        favoritesViewModel = new ViewModelProvider(this).get(FavoritesViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);

        recyclerViewFavorites = view.findViewById(R.id.recyclerViewFavorites);
        progressBarFavorites = view.findViewById(R.id.progressBarFavorites);
        textViewEmptyFavorites = view.findViewById(R.id.textViewEmptyFavorites);
        textViewFavoritesTitle = view.findViewById(R.id.textViewFavoritesTitle);

        setupRecyclerView();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressBarFavorites.setVisibility(View.VISIBLE);
        textViewEmptyFavorites.setVisibility(View.GONE);
        recyclerViewFavorites.setVisibility(View.GONE);
        textViewFavoritesTitle.setVisibility(View.VISIBLE);

        favoritesViewModel.getAllFavorites().observe(getViewLifecycleOwner(), new Observer<List<FavoriteDrink>>() {
            @Override
            public void onChanged(List<FavoriteDrink> favoriteDrinks) {
                progressBarFavorites.setVisibility(View.GONE);
                if (favoriteDrinks == null || favoriteDrinks.isEmpty()) {
                    textViewEmptyFavorites.setVisibility(View.VISIBLE);
                    recyclerViewFavorites.setVisibility(View.GONE);
                } else {
                    textViewEmptyFavorites.setVisibility(View.GONE);
                    recyclerViewFavorites.setVisibility(View.VISIBLE);
                    favoritesAdapter.setFavorites(favoriteDrinks);
                }
            }
        });
    }

    private void setupRecyclerView() {
        if (getContext() == null) return;
        // Das Fragment wird als Listener für beide Klick-Arten übergeben
        favoritesAdapter = new FavoritesAdapter(getContext(), this, this);
        recyclerViewFavorites.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewFavorites.setAdapter(favoritesAdapter);
    }

    @Override
    public void onUnfavoriteClick(FavoriteDrink favoriteDrink) {
        favoritesViewModel.removeFavorite(favoriteDrink);
        if (getContext() != null && favoriteDrink.getStrDrink() != null) {
            Toast.makeText(getContext(), "\"" + favoriteDrink.getStrDrink() + "\" wurde von den Favoriten entfernt.", Toast.LENGTH_SHORT).show();
        }
    }

    // Implementierung der neuen Klick-Methode für das gesamte Item
    @Override
    public void onFavoriteItemClick(FavoriteDrink favoriteDrink) {
        if (getContext() == null || favoriteDrink == null) return;

        Intent intent = new Intent(getContext(), DrinkDetailActivity.class);
        intent.putExtra(DrinkDetailActivity.EXTRA_DRINK_ID, favoriteDrink.getIdDrink());
        intent.putExtra(EXTRA_LOAD_FAVORITE_FROM_DB, true); // Signal zum Laden aus der DB
        startActivity(intent);
    }
}
