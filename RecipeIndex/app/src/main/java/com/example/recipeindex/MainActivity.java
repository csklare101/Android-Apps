package com.example.recipeindex;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {
    private RecyclerView recyclerView;
    private final ArrayList<Recipe> recipeList = new ArrayList<>();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private RecipeAdapter recipeAdapter;

    private Recipe currentRecipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.recyclerView);
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), this::handleResult);

        recipeAdapter = new RecipeAdapter(recipeList, this);
        recyclerView.setAdapter(recipeAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDataFromFile();
        setTitle(getString(R.string.app_name) + " (" + recipeList.size() + ")");
    }

    public void handleResult(ActivityResult result){
        if(result == null || result.getData() == null){
            return;
        }

        Intent data = result.getData();
        if(result.getResultCode() == RESULT_OK){
            if(data.hasExtra("NEW_RECIPE")) {
                Recipe r = (Recipe) data.getSerializableExtra("NEW_RECIPE");
                recipeList.add(r);
                Collections.sort(recipeList, Collections.reverseOrder());
                recipeAdapter.notifyItemRangeChanged(0, recipeList.size());
                setTitle(getString(R.string.app_name) + " (" + recipeList.size() + ")");
                saveDataToFile();
            }
            else if(data.hasExtra("EDIT_RECIPE")){
                Recipe r = (Recipe) data.getSerializableExtra("EDIT_RECIPE");

                currentRecipe.setTitle(r.getTitle());
                currentRecipe.setPrepTime(r.getPrepTime());
                currentRecipe.setCookTime(r.getCookTime());
                currentRecipe.setIngredients(r.getIngredients());
                currentRecipe.setInstructions(r.getInstructions());
                currentRecipe.setTotalTime(r.getTotalTime());
                Collections.sort(recipeList, Collections.reverseOrder());

                recipeAdapter.notifyItemRangeChanged(0, recipeList.size());
                setTitle(getString(R.string.app_name) + " (" + recipeList.size() + ")");
                saveDataToFile();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.aboutOpt){
            Intent intent = new Intent(this, AboutActivity.class);
            activityResultLauncher.launch(intent);
        }
        if(item.getItemId() == R.id.newRecipeOpt){
            Intent intent = new Intent(this, RecipeMakerActivity.class);
            activityResultLauncher.launch(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        currentRecipe = recipeList.get(pos);
        Intent intent = new Intent(this, RecipeMakerActivity.class);
        intent.putExtra("EDIT_RECIPE", currentRecipe);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);
        alertBuild.setPositiveButton("YES", (dialog, id) -> {
            int pos = recyclerView.getChildLayoutPosition(view);
            recipeList.remove(pos);
            recipeAdapter.notifyItemRemoved(pos);
            setTitle(getString(R.string.app_name) + " (" + recipeList.size() + ")");
            saveDataToFile();
        });
        alertBuild.setNegativeButton("NO", (dialog, id) -> dialog.cancel());
        alertBuild.setTitle("Delete a note?");
        alertBuild.setMessage("Do you want to delete this note?");

        AlertDialog dialog = alertBuild.create();
        dialog.show();

        return true;
    }

    private void saveDataToFile(){
        JSONArray jArray = new JSONArray();
        for(Recipe n : recipeList){
            try{
                jArray.put(n.toJSON());
            }
            catch(JSONException je) {
                Log.d(getClass().getSimpleName(), je.getMessage());
                je.printStackTrace();
            }
        }

        try{
            FileOutputStream fos = getApplicationContext().openFileOutput("JSONText.json", MODE_PRIVATE);
            PrintWriter pr = new PrintWriter(fos);
            pr.println(jArray);
            pr.close();
            fos.close();
        }
        catch (Exception e){
            Log.d(getClass().getSimpleName(), e.getMessage());
        }
    }

    private void loadDataFromFile(){
        FileInputStream fis;
        HashMap<String, Double> loadHash = new HashMap<>();
        try{
            fis = getApplicationContext().openFileInput("JSONText.json");
        }
        catch(FileNotFoundException fnfe){
            Log.d(getClass().getSimpleName(), fnfe.getMessage());
            return;
        }

        StringBuilder fileContent = new StringBuilder();

        try{
            byte[] buffer = new byte[1024];
            int i;
            while ((i = fis.read(buffer)) != -1) {
                fileContent.append(new String(buffer, 0, i));
            }
            JSONArray jsonArray = new JSONArray(fileContent.toString());
            for (int j = 0; j < jsonArray.length(); j++) {

                recipeList.add(Recipe.createFromJSON(jsonArray.getJSONObject(j)));
            }
        }
        catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
            return;
        }
        setTitle(getString(R.string.app_name) + "(" + recipeList.size() + ")");
    }



    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}