package com.example.recipeindex;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class RecipeViewHolder extends RecyclerView.ViewHolder{
    TextView title;
    ImageView foodPicture;

    public RecipeViewHolder(@NonNull View v){
        super(v);
        title = v.findViewById(R.id.recipeTitle);
        foodPicture = v.findViewById(R.id.foodPicture);
    }
}
