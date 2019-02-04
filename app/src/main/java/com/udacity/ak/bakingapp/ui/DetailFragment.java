package com.udacity.ak.bakingapp.ui;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.udacity.ak.bakingapp.BakingAppWidget;
import com.udacity.ak.bakingapp.BuildConfig;
import com.udacity.ak.bakingapp.R;
import com.udacity.ak.bakingapp.StepAdapter;
import com.udacity.ak.bakingapp.model.Ingredient;
import com.udacity.ak.bakingapp.model.Recipe;
import com.udacity.ak.bakingapp.utilities.AppConstants;

import java.util.List;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

public class DetailFragment extends Fragment {
    private static final String TAG = DetailFragment.class.getSimpleName();

    @BindView(R.id.tv_ingredient_list) TextView mIngredientsView;
    @BindView(R.id.steps_list_view) RecyclerView mStepRecyclerView;
    @BindView(R.id.fab_widget) FloatingActionButton mWidgetButton;
    @BindString(R.string.widget_added_confirmation) String mWidgetConfirmText;

    public static final String ARG_RECIPE_DATA = "recipe_data";
    private Recipe recipe;
    private StepAdapter mStepAdapter;

    // Define a new interface OnStepClickListener that triggers a callback in the host activity
    OnStepClickListener mCallback;

    // OnStepClickListener interface, calls a method in the host activity named onStepSelected
    public interface OnStepClickListener {
        void onStepSelected(int position);
    }

    // Override onAttach to make sure that the container activity has implemented the callback
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        // This makes sure that the host activity has implemented the callback interface
        // If not, it throws an exception
        try {
            mCallback = (OnStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnStepClickListener");
        }
    }


    public DetailFragment() {
        // Required empty public constructor
    }

    public static DetailFragment newInstance(Recipe recipe) {
        DetailFragment fragment = new DetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_RECIPE_DATA,recipe);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            recipe = getArguments().getParcelable(ARG_RECIPE_DATA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ButterKnife.bind(this, view);
        populateUI();
        return view;
    }

    private void populateUI(){
        //Ingredient list
        List<Ingredient> ingredientList = recipe.getIngredients();
        StringBuffer sbIngredients = new StringBuffer();
        for (Ingredient ingredient : ingredientList) {
            if(sbIngredients.length()!=0)
                sbIngredients.append("<br/><br/>");
            sbIngredients.append(ingredient.getIngredient() + " " +
                            ingredient.getQuantity() + " " + ingredient.getMeasure());
        }
        mIngredientsView.setText(Html.fromHtml(sbIngredients.toString()));

        //Steps Recycler View
        mStepAdapter = new StepAdapter(recipe.getSteps(), new StepAdapter.StepClickListener() {
            @Override public void onClick(int itemIndex) {
                mCallback.onStepSelected(itemIndex);
            }
        });
        mStepRecyclerView.setAdapter(mStepAdapter);
        mStepRecyclerView.setHasFixedSize(true);mStepRecyclerView.setNestedScrollingEnabled(false);
        DividerItemDecoration decoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mStepRecyclerView.addItemDecoration(decoration);

        //Fab for adding recipe to home widget
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(BuildConfig.APPLICATION_ID, MODE_PRIVATE);
        mWidgetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sharedPreferences
                        .edit()
                        .putString(AppConstants.SP_KEY_DISH_NAME, recipe.getName())
                        .putString(AppConstants.SP_KEY_INGREDIENTS, sbIngredients.toString())
                        .apply();

                Snackbar.make(view, mWidgetConfirmText, Snackbar.LENGTH_LONG).show();

                //Update the Widget
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                int[] ids = appWidgetManager.getAppWidgetIds(new ComponentName(getActivity(), BakingAppWidget.class));
                BakingAppWidget bakingAppWidget = new BakingAppWidget();
                bakingAppWidget.onUpdate(getActivity(), appWidgetManager, ids);
            }
        });
    }

}
