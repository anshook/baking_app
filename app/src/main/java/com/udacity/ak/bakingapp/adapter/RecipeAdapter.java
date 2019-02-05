package com.udacity.ak.bakingapp.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.udacity.ak.bakingapp.R;
import com.udacity.ak.bakingapp.model.Recipe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    private List<Recipe> mRecipeList;
    private RecipeClickListener mClickListener;

    public RecipeAdapter(@NonNull Context context, @NonNull List<Recipe> recipeList){
        mRecipeList = recipeList;
    }
    /*

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
            Recipe recipe=getItem(position);

        if(convertView==null){
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.recyclerview_item_recipe,parent,false);
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
    */

    public interface RecipeClickListener {
        void onClick(int position);
    }

    public void setClickListener(RecipeClickListener RecipeClickListener) {
        this.mClickListener = RecipeClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Recipe recipe=mRecipeList.get(position);
        String imageUrl = recipe.getImage();
        String dishName = recipe.getName();
        if (!imageUrl.equals("")) {
            //Load image if present
            Picasso.with(holder.itemView.getContext()).load(imageUrl).into(holder.mDishImageView);
        }
        holder.mDishTextView.setText(dishName);
    }

    @Override
    public int getItemCount() {
        return mRecipeList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.iv_dish) ImageView mDishImageView;
        @BindView(R.id.tv_dish) TextView mDishTextView;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            mClickListener.onClick(getAdapterPosition());
        }

    }
}
