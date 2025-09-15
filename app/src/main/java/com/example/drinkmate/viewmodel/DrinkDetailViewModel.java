package com.example.drinkmate.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.drinkmate.data.local.AppDatabase;
import com.example.drinkmate.data.local.FavoriteDao;
import com.example.drinkmate.data.local.FavoriteDrink;
import com.example.drinkmate.model.Drink;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DrinkDetailViewModel extends AndroidViewModel {

    private FavoriteDao favoriteDao;
    private ExecutorService executorService;
    // isFavoriteStatus sollte nicht als Membervariable gehalten werden, wenn es von drinkId abhängt,
    // da sonst für verschiedene Drinks derselbe Status zurückgegeben werden könnte.
    // Besser: Direkt vom DAO holen bei jedem Aufruf.

    public DrinkDetailViewModel(@NonNull Application application) {
        super(application);
        favoriteDao = AppDatabase.getDatabase(application).favoriteDao();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<Integer> getIsFavoriteStatus(String drinkId) {
        // Direkt vom DAO holen, um sicherzustellen, dass es für die aktuelle drinkId ist
        return favoriteDao.isFavorite(drinkId);
    }

    public void addFavorite(Drink drink) {
        if (drink == null) return;
        // Alle Felder vom Drink-Objekt in das FavoriteDrink-Objekt mappen
        FavoriteDrink favoriteDrink = new FavoriteDrink(
                drink.getIdDrink(),
                drink.getStrDrink(),
                drink.getStrDrinkThumb(),
                drink.getStrAlcoholic(),
                drink.getStrCategory(),
                drink.getStrGlass(),
                drink.getStrInstructions(),
                drink.getStrInstructionsDE(),
                drink.getStrIngredient1(),
                drink.getStrIngredient2(),
                drink.getStrIngredient3(),
                drink.getStrIngredient4(),
                drink.getStrIngredient5(),
                drink.getStrIngredient6(),
                drink.getStrIngredient7(),
                drink.getStrIngredient8(),
                drink.getStrIngredient9(),
                drink.getStrIngredient10(),
                drink.getStrIngredient11(),
                drink.getStrIngredient12(),
                drink.getStrIngredient13(),
                drink.getStrIngredient14(),
                drink.getStrIngredient15(),
                drink.getStrMeasure1(),
                drink.getStrMeasure2(),
                drink.getStrMeasure3(),
                drink.getStrMeasure4(),
                drink.getStrMeasure5(),
                drink.getStrMeasure6(),
                drink.getStrMeasure7(),
                drink.getStrMeasure8(),
                drink.getStrMeasure9(),
                drink.getStrMeasure10(),
                drink.getStrMeasure11(),
                drink.getStrMeasure12(),
                drink.getStrMeasure13(),
                drink.getStrMeasure14(),
                drink.getStrMeasure15()
        );
        executorService.execute(() -> favoriteDao.insert(favoriteDrink));
    }

    public void removeFavorite(String drinkId) {
        if (drinkId == null || drinkId.isEmpty()) return;
        // Um ein FavoriteDrink-Objekt für die Delete-Methode zu erstellen,
        // benötigen wir theoretisch alle Felder, aber Room's @Delete
        // verwendet standardmäßig den Primärschlüssel.
        // Ein temporäres Objekt mit nur der ID sollte funktionieren, wenn die DAO-Methode
        // @Delete ein FavoriteDrink-Objekt erwartet.
        // Wir erstellen es hier aber mit allen Feldern (als null/leer),
        // damit es dem Konstruktor entspricht, auch wenn nicht alle für @Delete benötigt werden.
        FavoriteDrink tempFavoriteDrink = new FavoriteDrink(drinkId,
                null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, null, null, null, null, null, null, null, null, null, null);
        executorService.execute(() -> favoriteDao.delete(tempFavoriteDrink));
    }

    // Neue Methode, um die Details eines favorisierten Drinks aus der DB zu laden
    public LiveData<FavoriteDrink> getFavoriteDrinkDetails(String drinkId) {
        return favoriteDao.getFavoriteById(drinkId);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
