package com.udacity.ak.bakingapp.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.udacity.ak.bakingapp.R;
import com.udacity.ak.bakingapp.model.Step;

import java.util.List;

import butterknife.BindString;
import butterknife.ButterKnife;

public class StepActivity extends AppCompatActivity {
    private static final String TAG = StepActivity.class.getSimpleName();

    private static final String ACTION_BAR_TITLE = "saved_action_bar_title";
    public static final String PARCEL_DATA_STEP = "parcel_data_step";
    public static final String DISH_NAME_EXTRA = "dish_name_extra";
    public static final String STEP_NUMBER_EXTRA = "step_number_extra";

    @BindString(R.string.step_title) String mStepTitle;
    private Step step;
    private String mActionBarTitle;
    private Integer mStepNumber;
    private String mDishName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step);
        ButterKnife.bind(this);

        //create fragments
        if(savedInstanceState == null) {
            step = (Step) getIntent().getParcelableExtra(PARCEL_DATA_STEP);
            mDishName = getIntent().getStringExtra(DISH_NAME_EXTRA);
            mStepNumber = getIntent().getIntExtra(STEP_NUMBER_EXTRA,0);

            mActionBarTitle = mDishName + " - " + mStepTitle + " " + mStepNumber;
            getSupportActionBar().setTitle(mActionBarTitle);

            StepFragment stepFragment = StepFragment.newInstance(step);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.step_detail_container, stepFragment)
                    .commit();
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
}

