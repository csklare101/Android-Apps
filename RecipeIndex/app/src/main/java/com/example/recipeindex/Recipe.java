package com.example.recipeindex;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;

public class Recipe implements Comparable<Recipe>, Serializable {
    private String title;
    private long prepTime;
    private long cookTime;
    private HashMap<String, Double> ingredients;
    private String instructions;
    private long totalTime;
    private Bitmap image;
    public Recipe(String title, long prepTime, long cookTime, HashMap<String, Double> ingredients, String instructions, Bitmap image) {
        this.title = title;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.totalTime = prepTime+cookTime;
        this.image = image;

    }

    public String getTitle() {
        return title;
    }

    public long getPrepTime() {
        return prepTime;
    }

    public long getCookTime() {
        return cookTime;
    }

    public HashMap<String, Double> getIngredients() {
        return ingredients;
    }

    public String getInstructions() {
        return instructions;
    }

    public long getTotalTime() {
        return totalTime;
    }

    public Bitmap getImage() {
        return image;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setPrepTime(long prepTime) {
        this.prepTime = prepTime;
    }

    public void setCookTime(long cookTime) {
        this.cookTime = cookTime;
    }

    public void setIngredients(HashMap<String, Double> ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setTotalTime(long totalTime) {
        this.totalTime = totalTime;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject jobj = new JSONObject();

        jobj.put("title", title);
        jobj.put("prepTime", prepTime);
        jobj.put("cookTime", cookTime);
        jobj.put("ingredients", ingredients);
        jobj.put("instructions", instructions);
        jobj.put("totalTime", totalTime);
        jobj.put("image", image);
        return jobj;
    }

    public static Recipe createFromJSON(JSONObject jobj) throws JSONException{
        String title = jobj.getString("title");
        long prepTime = jobj.getLong("prepTime");
        long cookTime = jobj.getLong("cookTime");
        HashMap<String, Double> ingredients = new HashMap<>();
        String ingredientString = jobj.getString("ingredients");
        ingredientString = ingredientString.replaceAll("[{}]","");
        ingredientString = ingredientString.replaceAll(", ","=");
        String[] parts = ingredientString.split("=");

        String key = "";
        double value;
        for(int i =0; i < parts.length; i++){
            if(i % 2 == 0){
                key = parts[i];
            }
            else{
                value = Double.parseDouble(parts[i]);
                if(!key.isEmpty() && value > 0){
                    ingredients.put(key, value);
                }
            }
        }
        String instructions = jobj.getString("instructions");
        if(jobj.has("image")) {
            Bitmap image = (Bitmap) jobj.get("image");
            return new Recipe(title,prepTime,cookTime,ingredients,instructions, image);
        }
        else {
            return new Recipe(title, prepTime, cookTime, ingredients, instructions, null);
        }
    }

    @Override
    public int compareTo(Recipe recipe) {
        return recipe.title.compareTo(this.title);
    }
}
