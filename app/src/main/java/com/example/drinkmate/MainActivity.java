package com.example.drinkmate;

import android.content.Intent; // Hinzugefügt
import android.os.Bundle;
import android.text.TextUtils; // Hinzugefügt, falls benötigt für String-Vergleiche
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.splashscreen.SplashScreen;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.drinkmate.ui.alcoholic.AlcoholicDrinksFragment;
import com.example.drinkmate.ui.favorites.FavoritesFragment;
import com.example.drinkmate.ui.nonalcoholic.NonAlcoholicDrinksFragment;
import com.example.drinkmate.ui.search.SearchFragment;

/**
 * Die Hauptaktivität der Anwendung.
 * Verantwortlich für die Einrichtung der Hauptbenutzeroberfläche, einschließlich der Toolbar und eines Fragment-Containers.
 * Verwaltet die Navigation zwischen verschiedenen Fragmenten (Suche, Alkoholische Getränke, Nicht-alkoholische Getränke, Favoriten)
 * über ein Popup-Menü und behandelt Navigations-Intents von anderen Aktivitäten.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Toolbar toolbar;
    private View customToolbarView;

    // Fragment Tags (bestehend)
    private static final String FAVORITES_FRAGMENT_TAG = "FAVORITES_FRAGMENT_TAG";
    private static final String SEARCH_FRAGMENT_TAG = "SEARCH_FRAGMENT_TAG";
    private static final String ALCOHOLIC_DRINKS_FRAGMENT_TAG = "ALCOHOLIC_DRINKS_FRAGMENT_TAG";
    private static final String NON_ALCOHOLIC_DRINKS_FRAGMENT_TAG = "NON_ALCOHOLIC_DRINKS_FRAGMENT_TAG";

    // Konstante für Intent Extra (sollte mit der in DrinkDetailActivity übereinstimmen)
    public static final String EXTRA_NAVIGATE_TO_FRAGMENT = "com.example.drinkmate.NAVIGATE_TO_FRAGMENT";

    // Navigationsziel-Konstanten (sollten mit denen in DrinkDetailActivity übereinstimmen)
    // Diese dienen hier zum Abgleich mit dem empfangenen Extra-Wert
    public static final String NAV_TARGET_SEARCH = "NAV_SEARCH";
    public static final String NAV_TARGET_ALCOHOLIC = "NAV_ALCOHOLIC";
    public static final String NAV_TARGET_NON_ALCOHOLIC = "NAV_NON_ALCOHOLIC";
    public static final String NAV_TARGET_FAVORITES = "NAV_FAVORITES";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SplashScreen.installSplashScreen(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        LayoutInflater inflater = LayoutInflater.from(this);
        customToolbarView = inflater.inflate(R.layout.toolbar_custom_title, toolbar, false);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
            Toolbar.LayoutParams.WRAP_CONTENT,
            Toolbar.LayoutParams.MATCH_PARENT
        );
        toolbar.addView(customToolbarView, layoutParams);

        customToolbarView.setOnClickListener(v -> showScreenSelectionMenu(v));

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        // Verarbeite den Intent, falls er Navigations-Extras enthält
        boolean navigatedByIntent = handleIntentNavigation(getIntent());

        if (!navigatedByIntent && savedInstanceState == null) {
            // Lade das Standardfragment nur, wenn nicht durch Intent navigiert wurde
            // und es der erste Start ist (kein savedInstanceState)
            loadFragment(new SearchFragment(), SEARCH_FRAGMENT_TAG);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent); // Wichtig, um den neuen Intent für die Activity zu setzen
        handleIntentNavigation(intent);
    }

    private boolean handleIntentNavigation(Intent intent) {
        if (intent != null && intent.hasExtra(EXTRA_NAVIGATE_TO_FRAGMENT)) {
            String targetFragment = intent.getStringExtra(EXTRA_NAVIGATE_TO_FRAGMENT);
            if (targetFragment != null) {
                // Entferne das Extra, damit es bei Konfigurationsänderungen nicht erneut verarbeitet wird
                intent.removeExtra(EXTRA_NAVIGATE_TO_FRAGMENT);

                if (TextUtils.equals(targetFragment, NAV_TARGET_SEARCH)) {
                    loadFragment(new SearchFragment(), SEARCH_FRAGMENT_TAG);
                    return true;
                } else if (TextUtils.equals(targetFragment, NAV_TARGET_ALCOHOLIC)) {
                    loadFragment(new AlcoholicDrinksFragment(), ALCOHOLIC_DRINKS_FRAGMENT_TAG);
                    return true;
                } else if (TextUtils.equals(targetFragment, NAV_TARGET_NON_ALCOHOLIC)) {
                    loadFragment(new NonAlcoholicDrinksFragment(), NON_ALCOHOLIC_DRINKS_FRAGMENT_TAG);
                    return true;
                } else if (TextUtils.equals(targetFragment, NAV_TARGET_FAVORITES)) {
                    loadFragment(new FavoritesFragment(), FAVORITES_FRAGMENT_TAG);
                    return true;
                }
            }
        }
        return false; // Keine Navigation durch Intent erfolgt
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        if (itemId == R.id.action_open_menu_burger) {
            View anchorView = findViewById(R.id.action_open_menu_burger);
            if (anchorView == null) {
                anchorView = customToolbarView; // Nutze die Custom View als besseren Anker
            }
            showScreenSelectionMenu(anchorView);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showScreenSelectionMenu(View anchorView) {
        PopupMenu popup = new PopupMenu(this, anchorView);
        popup.getMenuInflater().inflate(R.menu.screen_navigation_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(menuItem -> {
            int itemId = menuItem.getItemId();
            if (itemId == R.id.nav_search) {
                loadFragment(new SearchFragment(), SEARCH_FRAGMENT_TAG);
                return true;
            } else if (itemId == R.id.nav_alcoholic) {
                loadFragment(new AlcoholicDrinksFragment(), ALCOHOLIC_DRINKS_FRAGMENT_TAG);
                return true;
            } else if (itemId == R.id.nav_non_alcoholic) {
                loadFragment(new NonAlcoholicDrinksFragment(), NON_ALCOHOLIC_DRINKS_FRAGMENT_TAG);
                return true;
            } else if (itemId == R.id.nav_favorites) {
                loadFragment(new FavoritesFragment(), FAVORITES_FRAGMENT_TAG);
                return true;
            }
            return false;
        });
        popup.show();
    }

    private void loadFragment(Fragment fragment, String tag) {
        Fragment existingFragment = getSupportFragmentManager().findFragmentByTag(tag);
        // Lade das Fragment nur, wenn es nicht bereits das sichtbare Fragment ist.
        // Die Prüfung auf existingFragment == null ist hier nicht mehr ganz korrekt,
        // da das Fragment existieren, aber nicht sichtbar sein könnte.
        // Besser: Prüfen, ob das aktuell im Container befindliche Fragment einen anderen Tag hat.
        // Für Einfachheit belassen wir es vorerst bei der bestehenden Logik.
        // Bei Bedarf kann dies verfeinert werden, um unnötige replace-Operationen zu vermeiden.
        if (existingFragment == null || !existingFragment.isVisible()) {
             // Es ist besser, sicherzustellen, dass nicht dasselbe Fragment erneut geladen wird,
             // wenn es bereits angezeigt wird.
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
            if (currentFragment != null && currentFragment.getTag() != null && currentFragment.getTag().equals(tag)) {
                // Das gewünschte Fragment ist bereits sichtbar.
                return;
            }

            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment, tag);
            transaction.commit();
        }
    }
}
