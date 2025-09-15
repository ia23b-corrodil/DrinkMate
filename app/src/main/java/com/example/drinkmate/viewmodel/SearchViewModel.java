package com.example.drinkmate.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import android.util.Log;

import com.example.drinkmate.model.Drink;
import com.example.drinkmate.model.DrinksResponse;
import com.example.drinkmate.network.ApiClient;
import com.example.drinkmate.network.CocktailDbApiService;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SearchViewModel extends ViewModel {

    private static final String TAG = "SearchViewModel";

    private MutableLiveData<List<Drink>> drinksLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>(); 

    private CocktailDbApiService apiService;

    public SearchViewModel() {
        apiService = ApiClient.getClient();
        isLoadingLiveData.setValue(false); 
    }

    public LiveData<List<Drink>> getDrinksLiveData() {
        return drinksLiveData;
    }

    public LiveData<Boolean> getIsLoadingLiveData() {
        return isLoadingLiveData;
    }

    public LiveData<String> getErrorMessageLiveData() {
        return errorMessageLiveData;
    }

    public void searchDrinksByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            drinksLiveData.setValue(Collections.emptyList());
            errorMessageLiveData.setValue("Please enter a search term.");
            return;
        }

        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null); 

        apiService.searchCocktailsByName(query).enqueue(new Callback<DrinksResponse>() {
            @Override
            public void onResponse(Call<DrinksResponse> call, Response<DrinksResponse> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Drink> drinks = response.body().getDrinks();
                    if (drinks != null && !drinks.isEmpty()) {
                        drinksLiveData.setValue(drinks);
                    } else {
                        drinksLiveData.setValue(Collections.emptyList());
                        errorMessageLiveData.setValue("No drinks found for \"" + query + "\".");
                    }
                } else {
                    Log.e(TAG, "API Response not successful or body is null. Code: " + response.code());
                    drinksLiveData.setValue(Collections.emptyList());
                    errorMessageLiveData.setValue("Error fetching drinks. Code: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<DrinksResponse> call, Throwable t) {
                isLoadingLiveData.setValue(false);
                Log.e(TAG, "API Call Failed: ", t);
                drinksLiveData.setValue(Collections.emptyList());
                errorMessageLiveData.setValue("Failed to connect to the API: " + t.getMessage());
            }
        });
    }

    public void clearSearch() {
        drinksLiveData.setValue(Collections.emptyList());
        errorMessageLiveData.setValue(null);
        isLoadingLiveData.setValue(false);
    }
}
