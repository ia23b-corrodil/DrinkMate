package com.example.drinkmate.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class DrinksResponse {

    @SerializedName("drinks") // Der Schlüssel im JSON, der die Liste der Getränke enthält
    private List<Drink> drinks;

    // Getter
    public List<Drink> getDrinks() {
        return drinks;
    }
}