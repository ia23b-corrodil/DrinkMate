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

public class AlcoholicDrinksViewModel extends ViewModel {

    private static final String TAG = "AlcoholicDrinksVM";

    private MutableLiveData<List<Drink>> drinksLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>();

    private CocktailDbApiService apiService;

    public AlcoholicDrinksViewModel() {
        apiService = ApiClient.getClient();
        fetchAlcoholicDrinks();
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

    public void fetchAlcoholicDrinks() {
        isLoadingLiveData.setValue(true);
        errorMessageLiveData.setValue(null);

        apiService.filterByAlcoholic("Alcoholic").enqueue(new Callback<DrinksResponse>() {
            @Override
            public void onResponse(Call<DrinksResponse> call, Response<DrinksResponse> response) {
                isLoadingLiveData.setValue(false);
                if (response.isSuccessful() && response.body() != null) {
                    List<Drink> drinks = response.body().getDrinks();
                    if (drinks != null && !drinks.isEmpty()) {
                        drinksLiveData.setValue(drinks);
                    } else {
                        drinksLiveData.setValue(Collections.emptyList());
                        errorMessageLiveData.setValue("No alcoholic drinks found.");
                    }
                } else {
                    Log.e(TAG, "API Response not successful. Code: " + response.code());
                    drinksLiveData.setValue(Collections.emptyList());
                    errorMessageLiveData.setValue("Error fetching alcoholic drinks. Code: " + response.code());
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
}
