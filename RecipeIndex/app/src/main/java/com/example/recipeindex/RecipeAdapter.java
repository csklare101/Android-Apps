package com.example.recipeindex;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeViewHolder> {
    private final ArrayList<Recipe> recipeList;
    private final MainActivity mainAct;

    public RecipeAdapter(ArrayList<Recipe> recipeList, MainActivity mainAct) {
        this.recipeList = recipeList;
        this.mainAct = mainAct;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_entry, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new RecipeViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder holder, int position) {
        Recipe r = recipeList.get(position);
        holder.title.setText(r.getTitle());

        if(r.getImage() == null) {
            holder.foodPicture.setImageResource(R.drawable.mysteryfood);
        }else{
            holder.foodPicture.setImageBitmap(r.getImage());
        }
    }

    @Override
    public int getItemCount() {
        return recipeList.size();
    }
}