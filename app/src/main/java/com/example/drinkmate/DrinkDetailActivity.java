package com.example.drinkmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.drinkmate.data.local.FavoriteDrink;
import com.example.drinkmate.model.Drink;
import com.example.drinkmate.model.DrinksResponse;
import com.example.drinkmate.network.ApiClient;
import com.example.drinkmate.network.CocktailDbApiService;
import com.example.drinkmate.viewmodel.DrinkDetailViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrinkDetailActivity extends AppCompatActivity {

    private static final String TAG = "DrinkDetailActivity";
    public static final String EXTRA_DRINK_ID = "com.example.drinkmate.EXTRA_DRINK_ID";
    public static final String EXTRA_NAVIGATE_TO_FRAGMENT = "com.example.drinkmate.NAVIGATE_TO_FRAGMENT";
    // Konstante für das Laden aus der DB
    public static final String EXTRA_LOAD_FAVORITE_FROM_DB = "com.example.drinkmate.LOAD_FAVORITE_FROM_DB";

    public static final String NAV_SEARCH = "NAV_SEARCH";
    public static final String NAV_ALCOHOLIC = "NAV_ALCOHOLIC";
    public static final String NAV_NON_ALCOHOLIC = "NAV_NON_ALCOHOLIC";
    public static final String NAV_FAVORITES = "NAV_FAVORITES";

    private ImageView imageViewDetailDrink;
    private TextView textViewDetailDrinkName;
    private TextView textViewDetailCategory;
    private TextView textViewDetailAlcoholic;
    private TextView textViewDetailGlass;
    private TextView textViewDetailIngredients;
    private TextView textViewDetailInstructions;
    private ProgressBar progressBarDetail;
    private FloatingActionButton fabFavorite;

    private Toolbar toolbar;

    private CocktailDbApiService apiService; // Nur für API-Ladevorgänge
    private String drinkId;
    private Drink currentApiDrink; // Wird verwendet, wenn von API geladen
    private DrinkDetailViewModel drinkDetailViewModel;
    private boolean isCurrentlyFavoriteForApiDrink = false; // Status für API-geladenen Drink
    private boolean loadedFromDatabase = false; // Flag, um den Lademodus zu kennzeichnen

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drink_detail);

        toolbar = findViewById(R.id.toolbar_drink_detail); // ID aus deinem XML
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("");
        }

        initializeViews();
        drinkDetailViewModel = new ViewModelProvider(this).get(DrinkDetailViewModel.class);

        drinkId = getIntent().getStringExtra(EXTRA_DRINK_ID);
        loadedFromDatabase = getIntent().getBooleanExtra(EXTRA_LOAD_FAVORITE_FROM_DB, false);

        if (drinkId == null || drinkId.isEmpty()) {
            Log.e(TAG, "Drink ID not passed to DrinkDetailActivity");
            Toast.makeText(this, "Error: Drink ID missing.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        if (loadedFromDatabase) {
            loadFavoriteDetailsFromDb(drinkId);
        } else {
            apiService = ApiClient.getClient(); // Nur initialisieren, wenn von API geladen wird
            loadDrinkDetailsFromApi(drinkId);
        }
    }

    private void initializeViews() {
        imageViewDetailDrink = findViewById(R.id.imageViewDetailDrink);
        textViewDetailDrinkName = findViewById(R.id.textViewDetailDrinkName);
        textViewDetailCategory = findViewById(R.id.textViewDetailCategory);
        textViewDetailAlcoholic = findViewById(R.id.textViewDetailAlcoholic);
        textViewDetailGlass = findViewById(R.id.textViewDetailGlass);
        textViewDetailIngredients = findViewById(R.id.textViewDetailIngredients);
        textViewDetailInstructions = findViewById(R.id.textViewDetailInstructions);
        progressBarDetail = findViewById(R.id.progressBarDetail);
        fabFavorite = findViewById(R.id.fabFavorite);
    }

    private void loadDrinkDetailsFromApi(String id) {
        progressBarDetail.setVisibility(View.VISIBLE);
        setUiElementsVisibility(View.GONE);
        fabFavorite.setVisibility(View.GONE);

        apiService.getCocktailDetailsById(id).enqueue(new Callback<DrinksResponse>() {
            @Override
            public void onResponse(@NonNull Call<DrinksResponse> call, @NonNull Response<DrinksResponse> response) {
                progressBarDetail.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null && response.body().getDrinks() != null && !response.body().getDrinks().isEmpty()) {
                    currentApiDrink = response.body().getDrinks().get(0);
                    populateUiWithApiDrink(currentApiDrink);
                    setUiElementsVisibility(View.VISIBLE);
                    fabFavorite.setVisibility(View.VISIBLE);
                    setupFabForApiLoadedDrink(currentApiDrink);
                } else {
                    handleLoadError("Error fetching drink details from API. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<DrinksResponse> call, @NonNull Throwable t) {
                progressBarDetail.setVisibility(View.GONE);
                handleLoadError("Failed to connect to the API: " + t.getMessage());
            }
        });
    }

    private void loadFavoriteDetailsFromDb(String id) {
        progressBarDetail.setVisibility(View.VISIBLE);
        setUiElementsVisibility(View.GONE);
        fabFavorite.setVisibility(View.GONE);

        drinkDetailViewModel.getFavoriteDrinkDetails(id).observe(this, favoriteDrink -> {
            progressBarDetail.setVisibility(View.GONE);
            if (favoriteDrink != null) {
                populateUiWithDbFavorite(favoriteDrink);
                setUiElementsVisibility(View.VISIBLE);
                fabFavorite.setVisibility(View.VISIBLE);
                setupFabForDbLoadedDrink(favoriteDrink);
            } else {
                // Dieser Fall sollte selten auftreten, wenn die ID korrekt vom FavoritesFragment kommt
                // und der Drink nicht zwischenzeitlich gelöscht wurde.
                handleLoadError("Favorite drink not found in database. It might have been removed.");
                // Optional: Activity schließen oder eine spezifischere Fehlermeldung anzeigen
                // finish();
            }
        });
    }

    private void populateUiWithApiDrink(Drink drink) {
        if (drink == null) return;
        updateCommonUiElements(
                drink.getStrDrink(),
                drink.getStrCategory(),
                drink.getStrAlcoholic(),
                drink.getStrGlass(),
                drink.getStrDrinkThumb(),
                drink.getIngredientsWithMeasures(),
                drink.getStrInstructionsDE(), // Bevorzuge Deutsch
                drink.getStrInstructions()  // Fallback auf Standard
        );
    }

    private void populateUiWithDbFavorite(FavoriteDrink favoriteDrink) {
        if (favoriteDrink == null) return;
        updateCommonUiElements(
                favoriteDrink.getStrDrink(),
                favoriteDrink.getStrCategory(),
                favoriteDrink.getStrAlcoholic(),
                favoriteDrink.getStrGlass(),
                favoriteDrink.getStrDrinkThumb(),
                favoriteDrink.getIngredientsWithMeasures(),
                favoriteDrink.getStrInstructionsDE(), // Bevorzuge Deutsch
                favoriteDrink.getStrInstructions()  // Fallback auf Standard
        );
    }

    private void updateCommonUiElements(String name, String category, String alcoholic, String glass,
                                        String thumbUrl, List<String> ingredientsWithMeasures,
                                        String instructionsDE, String instructionsDefault) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(name != null ? name : "Drink Details");
        }
        textViewDetailDrinkName.setText(name);
        textViewDetailCategory.setText(category != null ? category : "N/A");
        textViewDetailAlcoholic.setText(alcoholic != null ? alcoholic : "N/A");
        textViewDetailGlass.setText(glass != null ? glass : "N/A");

        Glide.with(this)
                .load(thumbUrl)
                .error(R.drawable.ic_error_image) // Sicherstellen, dass ic_error_image existiert
                .into(imageViewDetailDrink);

        if (ingredientsWithMeasures != null && !ingredientsWithMeasures.isEmpty()) {
            textViewDetailIngredients.setText(TextUtils.join("\n", ingredientsWithMeasures));
        } else {
            textViewDetailIngredients.setText("N/A");
        }

        String instructionsToDisplay = instructionsDE;
        if (instructionsToDisplay == null || instructionsToDisplay.trim().isEmpty()) {
            instructionsToDisplay = instructionsDefault;
        }
        textViewDetailInstructions.setText(instructionsToDisplay != null ? instructionsToDisplay.trim() : "N/A");
    }


    private void setupFabForApiLoadedDrink(Drink drink) {
        final String drinkIdForFab = drink.getIdDrink();
        final String drinkNameForFab = drink.getStrDrink();

        drinkDetailViewModel.getIsFavoriteStatus(drinkIdForFab).observe(this, count -> {
            isCurrentlyFavoriteForApiDrink = (count != null && count > 0);
            if (isCurrentlyFavoriteForApiDrink) {
                fabFavorite.setImageDrawable(ContextCompat.getDrawable(DrinkDetailActivity.this, R.drawable.ic_favorite_filled));
            } else {
                fabFavorite.setImageDrawable(ContextCompat.getDrawable(DrinkDetailActivity.this, R.drawable.ic_favorite_border));
            }
        });

        fabFavorite.setOnClickListener(v -> {
            if (currentApiDrink == null) return; // Stelle sicher, dass currentApiDrink nicht null ist
            if (isCurrentlyFavoriteForApiDrink) {
                drinkDetailViewModel.removeFavorite(drinkIdForFab);
                Toast.makeText(DrinkDetailActivity.this, "\"" + drinkNameForFab + "\" removed from favorites", Toast.LENGTH_SHORT).show();
            } else {
                // Wichtig: Das vollständige 'Drink'-Objekt übergeben, damit alle Details gespeichert werden
                drinkDetailViewModel.addFavorite(currentApiDrink);
                Toast.makeText(DrinkDetailActivity.this, "\"" + drinkNameForFab + "\" added to favorites", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupFabForDbLoadedDrink(FavoriteDrink favoriteDrink) {
        // Wenn aus DB geladen, ist es bereits ein Favorit
        fabFavorite.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_favorite_filled));
        final String drinkIdForFab = favoriteDrink.getIdDrink();
        final String drinkNameForFab = favoriteDrink.getStrDrink();

        fabFavorite.setOnClickListener(v -> {
            drinkDetailViewModel.removeFavorite(drinkIdForFab);
            Toast.makeText(this, "\"" + drinkNameForFab + "\" removed from favorites", Toast.LENGTH_SHORT).show();
            // Optional: Nach dem Entfernen die Activity schließen, da der Anzeigegrund (Favorit) weg ist
            finish();
        });
    }

    private void handleLoadError(String message) {
        setUiElementsVisibility(View.VISIBLE); // Zeige UI-Elemente an, um Fehlermeldung darzustellen
        fabFavorite.setVisibility(View.GONE); // FAB bei Fehler ausblenden
        Log.e(TAG, message);
        Toast.makeText(DrinkDetailActivity.this, message, Toast.LENGTH_LONG).show();
        // Hier könnte man auch einen Fehlertext in der UI anzeigen statt nur Toast
        textViewDetailDrinkName.setText("Error");
        textViewDetailIngredients.setText(message);
    }

    private void setUiElementsVisibility(int visibility) {
        imageViewDetailDrink.setVisibility(visibility);
        textViewDetailDrinkName.setVisibility(visibility);
        findViewById(R.id.textViewDetailCategoryLabel).setVisibility(visibility);
        textViewDetailCategory.setVisibility(visibility);
        findViewById(R.id.textViewDetailAlcoholicLabel).setVisibility(visibility);
        textViewDetailAlcoholic.setVisibility(visibility);
        findViewById(R.id.textViewDetailGlassLabel).setVisibility(visibility);
        textViewDetailGlass.setVisibility(visibility);
        findViewById(R.id.textViewDetailIngredientsLabel).setVisibility(visibility);
        textViewDetailIngredients.setVisibility(visibility);
        findViewById(R.id.textViewDetailInstructionsLabel).setVisibility(visibility);
        textViewDetailInstructions.setVisibility(visibility);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == android.R.id.home) {
            finish();
            return true;
        } else if (itemId == R.id.action_open_menu_burger) {
            View menuItemView = findViewById(R.id.action_open_menu_burger);
            if (menuItemView == null) {
                menuItemView = getWindow().getDecorView().findViewById(android.R.id.content);
            }
            showNavigationPopupMenu(menuItemView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNavigationPopupMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.screen_navigation_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            String navigationTarget = null;

            if (itemId == R.id.nav_search) {
                navigationTarget = NAV_SEARCH;
            } else if (itemId == R.id.nav_alcoholic) {
                navigationTarget = NAV_ALCOHOLIC;
            } else if (itemId == R.id.nav_non_alcoholic) {
                navigationTarget = NAV_NON_ALCOHOLIC;
            } else if (itemId == R.id.nav_favorites) {
                navigationTarget = NAV_FAVORITES;
            }

            if (navigationTarget != null) {
                navigateToMain(navigationTarget);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void navigateToMain(String fragmentTarget) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra(EXTRA_NAVIGATE_TO_FRAGMENT, fragmentTarget);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}
