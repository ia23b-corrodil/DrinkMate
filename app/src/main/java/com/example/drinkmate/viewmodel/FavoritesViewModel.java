package com.example.drinkmate.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.drinkmate.data.local.AppDatabase;
import com.example.drinkmate.data.local.FavoriteDao;
import com.example.drinkmate.data.local.FavoriteDrink;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FavoritesViewModel extends AndroidViewModel {

    private FavoriteDao favoriteDao;
    private LiveData<List<FavoriteDrink>> allFavorites;
    private ExecutorService executorService;

    public FavoritesViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getDatabase(application);
        favoriteDao = db.favoriteDao();
        allFavorites = favoriteDao.getAllFavorites();
        executorService = Executors.newSingleThreadExecutor();
    }

    public LiveData<List<FavoriteDrink>> getAllFavorites() {
        return allFavorites;
    }

    public void removeFavorite(FavoriteDrink favoriteDrink) {
        if (favoriteDrink == null) return;
        executorService.execute(() -> favoriteDao.delete(favoriteDrink));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}
