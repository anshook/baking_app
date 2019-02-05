package com.udacity.ak.bakingapp.utilities;

import android.content.Context;
import android.util.DisplayMetrics;

public class AppConstants {
    public static final String SP_KEY_DISH_NAME = "pref_dish";
    public static final String SP_KEY_INGREDIENTS = "pref_ingredients";

    public static int calculateNoOfColumns(Context context, int colWidth) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / colWidth);
        if(noOfColumns<1) return 1;
        return noOfColumns;
    }
}
