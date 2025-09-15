package com.example.drinkmate.network;

import com.example.drinkmate.model.DrinksResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface CocktailDbApiService {

    @GET("search.php")
    Call<DrinksResponse> searchCocktailsByName(@Query("s") String searchTerm);

    @GET("lookup.php") // Endpunkt für die Detailsuche nach ID
    Call<DrinksResponse> getCocktailDetailsById(@Query("i") String id);

    @GET("filter.php") // Endpunkt für das Filtern nach alkoholischer Eigenschaft
    Call<DrinksResponse> filterByAlcoholic(@Query("a") String alcoholicType); // z.B. "Alcoholic" oder "Non_Alcoholic"
}
