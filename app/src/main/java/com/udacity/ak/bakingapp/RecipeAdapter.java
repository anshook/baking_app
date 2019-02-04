package com.udacity.ak.bakingapp;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.ak.bakingapp.model.Recipe;

import java.util.List;

public class RecipeAdapter extends ArrayAdapter<Recipe> {


    public RecipeAdapter(@NonNull Context context, @NonNull List<Recipe> objects){
            super(context,0,objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            Recipe recipe=getItem(position);

        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.gridview_list_recipes,parent,false);
        }

        ImageView dishImageView=(ImageView)convertView.findViewById(R.id.iv_dish);
        TextView dishTextView = (TextView)convertView.findViewById(R.id.tv_dish);

        String imageUrl = recipe.getImage();
        String dishName = recipe.getName();
        if (!imageUrl.equals("")) {
            //Load image if present
            Picasso.with(getContext()).load(imageUrl).into(dishImageView);
        }
        dishTextView.setText(dishName);
        return convertView;
    }
}
