package com.udacity.ak.bakingapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.design.widget.Snackbar;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.ak.bakingapp.R;
import com.udacity.ak.bakingapp.adapter.RecipeAdapter;
import com.udacity.ak.bakingapp.model.Recipe;
import com.udacity.ak.bakingapp.utilities.AppConstants;
import com.udacity.ak.bakingapp.utilities.InternetCheck;
import com.udacity.ak.bakingapp.utilities.RestClient;
import com.udacity.ak.bakingapp.utilities.SimpleIdlingResource;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindDimen;
import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.RecipeClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RECIPE_LIST = "saved_recipe_list";

    @BindView(R.id.rv_recipes) RecyclerView mRecipesRecyclerView;
    @BindView(R.id.tv_empty) TextView mEmptyView;
    @BindView(R.id.pb_loading) ProgressBar mProgressBar;
    @BindString(R.string.internet_unavailable) String noInternetMessage;
    @BindDimen(R.dimen.col_width_grid) int colWidth;

    private List<Recipe> recipeList;
    private Disposable disposable;
    RecipeAdapter recipeAdapter;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //display ProgressBar while the grid loads
        mProgressBar.setVisibility(View.VISIBLE);

        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(false);
        }

        if(savedInstanceState != null) {
            recipeList = savedInstanceState.getParcelableArrayList(RECIPE_LIST);
            populateUI();
        }
        else{
            loadRecipes();
        }
        if (mIdlingResource != null) {
            mIdlingResource.setIdleState(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(disposable!=null) disposable.dispose();
    }

    private void loadRecipes() {
        new InternetCheck((Boolean internet) -> {
            if (!internet) {
                buildDialog(this).show();
            } else {
                getRecipeList();
            }
        });
    }

    private void getRecipeList() {
        RestClient.getRestService().getRecipes()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Recipe>>() {

                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(List<Recipe> recipes) {
                        recipeList = recipes;
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, e.getMessage());
                        Snackbar.make(mRecipesRecyclerView, e.getMessage(), Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onComplete() {
                        populateUI();
                    }
                });
    }

    private AlertDialog.Builder buildDialog(Context ctx) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setMessage(noInternetMessage);
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        return builder;
    }

    void populateUI(){
        recipeAdapter=new RecipeAdapter(MainActivity.this, recipeList);
        recipeAdapter.setClickListener(this);

        mProgressBar.setVisibility(View.GONE);
        int numberOfColumns = AppConstants.calculateNoOfColumns(this, colWidth);
        mRecipesRecyclerView.setHasFixedSize(true);
        mRecipesRecyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        mRecipesRecyclerView.setAdapter(recipeAdapter);
        mProgressBar.setVisibility(View.GONE);
        mEmptyView.setVisibility(recipeList.size() > 0 ? View.GONE : View.VISIBLE);
    }

    private void launchRecipeDetailActivity(Recipe selectedRecipe) {
        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivity.PARCEL_DATA, selectedRecipe);
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        currentState.putParcelableArrayList(RECIPE_LIST, (ArrayList<Recipe>)recipeList);
        super.onSaveInstanceState(currentState);
    }

    //Only called from test, creates and returns a new {@link SimpleIdlingResource}.
    @VisibleForTesting
    @NonNull
    public IdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @Override
    public void onClick(int position) {
        Recipe selectedRecipe = recipeList.get(position);
        launchRecipeDetailActivity(selectedRecipe);
    }
}
