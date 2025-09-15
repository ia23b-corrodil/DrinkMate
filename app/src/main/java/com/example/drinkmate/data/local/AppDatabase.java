package com.example.drinkmate.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.annotation.NonNull;

@Database(entities = {FavoriteDrink.class}, version = 2, exportSchema = false) // Version erhöht auf 2
public abstract class AppDatabase extends RoomDatabase {

    public abstract FavoriteDao favoriteDao();

    private static volatile AppDatabase INSTANCE;
    private static final String DATABASE_NAME = "drink_mate_db";

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                            .addMigrations(MIGRATION_1_2) // Migration hinzugefügt
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // Migration von Version 1 zu Version 2
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            // SQL-Befehle, um die Tabelle favorite_drinks zu erweitern
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN category TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN glass_type TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN instructions TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN instructions_de TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient1 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient2 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient3 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient4 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient5 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient6 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient7 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient8 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient9 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient10 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient11 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient12 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient13 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient14 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strIngredient15 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure1 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure2 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure3 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure4 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure5 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure6 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure7 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure8 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure9 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure10 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure11 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure12 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure13 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure14 TEXT");
            database.execSQL("ALTER TABLE favorite_drinks ADD COLUMN strMeasure15 TEXT");
            // Wichtig: Wenn die neuen Spalten NOT NULL sein müssten,
            // müssten wir hier auch DEFAULT-Werte angeben. Da sie in der Entität
            // nullable Strings sind, ist das hier nicht zwingend nötig (sie werden NULL sein).
        }
    };
}
