package com.udacity.ak.bakingapp.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.VisibleForTesting;
import android.support.test.espresso.IdlingResource;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.udacity.ak.bakingapp.R;
import com.udacity.ak.bakingapp.RecipeAdapter;
import com.udacity.ak.bakingapp.model.Recipe;
import com.udacity.ak.bakingapp.utilities.InternetCheck;
import com.udacity.ak.bakingapp.utilities.RestClient;
import com.udacity.ak.bakingapp.utilities.SimpleIdlingResource;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String RECIPE_LIST = "saved_recipe_list";

    @BindView(R.id.gv_recipes) GridView mRecipesGridView;
    @BindView(R.id.tv_empty) TextView mEmptyView;
    @BindView(R.id.pb_loading) ProgressBar mProgressBar;
    @BindString(R.string.internet_unavailable) String noInternetMessage;

    private List<Recipe> recipeList;
    private Disposable disposable;

    @Nullable
    private SimpleIdlingResource mIdlingResource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        //display ProgressBar while the grid loads
        mRecipesGridView.setEmptyView(mProgressBar);

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
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
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
        RecipeAdapter recipeAdapter=new RecipeAdapter(MainActivity.this, recipeList);
        mRecipesGridView.setAdapter(recipeAdapter);
        mProgressBar.setVisibility(View.GONE);
        mRecipesGridView.setEmptyView(mEmptyView);

        mRecipesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Recipe selectedRecipe = (Recipe)parent.getItemAtPosition(position);
                launchRecipeDetailActivity(selectedRecipe);
            }
        });
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
}
