package com.udacity.ak.bakingapp.ui;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.udacity.ak.bakingapp.R;
import com.udacity.ak.bakingapp.model.Recipe;
import com.udacity.ak.bakingapp.model.Step;

import butterknife.BindString;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity implements DetailFragment.OnStepClickListener{
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String ACTION_BAR_TITLE = "saved_action_bar_title";

    @BindString(R.string.serving_title) String mServingText;
    public static final String PARCEL_DATA = "parcel_data";
    private Recipe recipe;
    private boolean mTwoPane;
    private String mActionBarTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        //create fragments
        if(getIntent().hasExtra(PARCEL_DATA)) {
            recipe = (Recipe) getIntent().getParcelableExtra(PARCEL_DATA);
        }
        if (findViewById(R.id.step_detail_container) != null) {
            mTwoPane = true;
        }

        if(savedInstanceState == null) {

            mActionBarTitle = recipe.getName() + " - "
                    + mServingText + " " + recipe.getServings();
            getSupportActionBar().setTitle(mActionBarTitle);

            DetailFragment detailFragment = DetailFragment.newInstance(recipe);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.recipe_detail_container, detailFragment)
                    .commit();

            if (mTwoPane) {
                StepFragment stepFragment = StepFragment.newInstance(recipe.getSteps().get(0));
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.step_detail_container, stepFragment)
                        .commit();
            }
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mActionBarTitle = savedInstanceState.getString(ACTION_BAR_TITLE);
        getSupportActionBar().setTitle(mActionBarTitle);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(ACTION_BAR_TITLE, mActionBarTitle);
    }

    @Override
    public void onStepSelected(int position) {
        Step step = recipe.getSteps().get(position);
        if (mTwoPane){
            StepFragment stepFragment = StepFragment.newInstance(step);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.step_detail_container, stepFragment)
                    .commit();
        }
        else {
            Intent intent = new Intent(this, StepActivity.class);
            intent.putExtra(StepActivity.PARCEL_DATA_STEP, step);
            intent.putExtra(StepActivity.DISH_NAME_EXTRA, recipe.getName());
            intent.putExtra(StepActivity.STEP_NUMBER_EXTRA, position);
            startActivity(intent);
        }
    }
}
