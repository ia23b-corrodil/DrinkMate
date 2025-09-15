package com.example.drinkmate.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(FavoriteDrink favoriteDrink);

    @Delete
    void delete(FavoriteDrink favoriteDrink);

    @Query("SELECT * FROM favorite_drinks ORDER BY drink_name ASC")
    LiveData<List<FavoriteDrink>> getAllFavorites();

    @Query("SELECT * FROM favorite_drinks WHERE id_drink = :idDrink")
    LiveData<FavoriteDrink> getFavoriteById(String idDrink);

    // Optional: Eine synchrone Methode, um schnell zu prüfen, ob ein Drink favorisiert ist.
    // Diese sollte idealerweise nicht auf dem UI-Thread aufgerufen werden.
    @Query("SELECT * FROM favorite_drinks WHERE id_drink = :idDrink LIMIT 1")
    FavoriteDrink getFavoriteByIdSync(String idDrink);

    // Optional: Eine Methode um zu prüfen ob ein Drink bereits ein Favorit ist, ohne das ganze Objekt zu laden
    @Query("SELECT COUNT(*) FROM favorite_drinks WHERE id_drink = :idDrink")
    LiveData<Integer> isFavorite(String idDrink);
}
