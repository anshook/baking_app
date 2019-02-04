package com.udacity.ak.bakingapp.utilities;


import com.udacity.ak.bakingapp.model.Recipe;
import java.util.List;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface RestInterface {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Observable<List<Recipe>> getRecipes();
}