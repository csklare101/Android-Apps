package com.example.recipeindex;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.HashMap;
import java.util.Locale;

public class RecipeMakerActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "RecipeMakerActivity";
    private EditText title;
    private EditText prepTime;
    private EditText cookTime;
    private TextView totalTime;
    private TextView ingredients;
    private EditText instructions;

    private ActivityResultLauncher<Intent> thumbActivityResultLauncher;
    private Bitmap currentImageFile;

    private HashMap<String, Double> ingredientsList = new HashMap<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_maker);

        title = findViewById(R.id.recipeNameText);
        prepTime = findViewById(R.id.prepTimeText);
        cookTime = findViewById(R.id.cookTimeText);

        prepTime.addTextChangedListener(textWatcher);
        cookTime.addTextChangedListener(textWatcher);

        totalTime = findViewById(R.id.totalTimeText);
        ingredients = findViewById(R.id.ingredientText);
        instructions = findViewById(R.id.recipeInstructionText);

        thumbActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleThumbResult);

        if(getIntent().hasExtra("EDIT_RECIPE")){
            Recipe r = (Recipe) getIntent().getSerializableExtra("EDIT_RECIPE");
            title.setText(r.getTitle());
            prepTime.setText((String.valueOf(r.getPrepTime())));
            cookTime.setText((String.valueOf(r.getCookTime())));
            totalTime.setText((String.valueOf(r.getTotalTime())));

            ingredientsList.putAll(r.getIngredients());
            updateIngredients();

            instructions.setText(r.getInstructions());
            if(r.getImage() != null){
                currentImageFile = r.getImage();
            }
        }
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(!prepTime.getText().toString().isEmpty() && !cookTime.getText().toString().isEmpty()){
                long prepTimeAd = Long.parseLong(prepTime.getText().toString());
                long cookTimeAd = Long.parseLong(cookTime.getText().toString());
                totalTime.setText(Long.toString(prepTimeAd+cookTimeAd));
            }
        }
    };

    public void handleThumbResult(ActivityResult result) {
        if (result == null || result.getData() == null) {
            Log.d(TAG, "handleResult: NULL ActivityResult received");
            return;
        }

        if (result.getResultCode() == RESULT_OK) {
            try {
                Intent data = result.getData();
                processCameraThumb(data.getExtras());
            } catch (Exception e) {
                Toast.makeText(this, "onActivityResult: " + e.getMessage(), Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }

    private void processCameraThumb(Bundle extras) {
        currentImageFile = (Bitmap) extras.get("data");
    }

    public void doThumb() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        thumbActivityResultLauncher.launch(takePictureIntent);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.addIngredientButt){
            AlertDialog.Builder ingredientName = new AlertDialog.Builder(this);
            ingredientName.setTitle("Enter Ingredient Name");
            ingredientName.setMessage("Please enter the ingredient name and measurement type, cannot be blank.\n Example: \"Sugar tbs\"");

            final EditText ingredient = new EditText(this);
            LinearLayout layout = new LinearLayout(this);

            layout.setOrientation(LinearLayout.VERTICAL);
            layout.addView(ingredient);
            ingredientName.setView(layout);
            ingredient.setInputType(InputType.TYPE_CLASS_TEXT);

            ingredientName.setPositiveButton("OK", (dialog, which) -> {
                if(ingredient.getText().toString().isEmpty()){
                    AlertDialog.Builder emptyString = new AlertDialog.Builder(this);
                    emptyString.setTitle("Empty title");
                    emptyString.setMessage("Ingredient has to have a title");
                    emptyString.create().show();
                }
                else {
                    AlertDialog.Builder ingredientAmmount = new AlertDialog.Builder(this);
                    ingredientAmmount.setTitle("Enter Ingredient Ammount");
                    ingredientAmmount.setMessage("Please enter the ingredient ammount, has to be more than 0");

                    final EditText ammount = new EditText(this);
                    LinearLayout layoutAmmount = new LinearLayout(this);
                    layoutAmmount.setOrientation(LinearLayout.VERTICAL);
                    layoutAmmount.addView(ammount);
                    ingredientAmmount.setView(layoutAmmount);
                    ammount.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                    ingredientAmmount.setPositiveButton("OK", (dialogA, whichA) -> {
                        if ( ammount.getText().toString().isEmpty() || Double.parseDouble(ammount.getText().toString()) <= 0) {
                            AlertDialog.Builder invalidAmmount = new AlertDialog.Builder(this);
                            invalidAmmount.setTitle("Empty ingredient");
                            invalidAmmount.setMessage("Ingredient has to have an ammount over 0!");
                            invalidAmmount.create().show();
                        }
                        else{
                            String ing = ingredient.getText().toString();
                            Double amm = Double.parseDouble(ammount.getText().toString());
                            ingredientsList.put(ing, amm);
                            updateIngredients();
                        }
                    });
                    ingredientAmmount.setNegativeButton("Cancel", (dialogA, whichA) -> {});
                    ingredientAmmount.create().show();
                }
            });
            ingredientName.setNegativeButton("Cancel", (dialog, which) -> {});
            ingredientName.create().show();
        }
    }

    public void updateIngredients(){
        String textValue = "";
        for(String s : ingredientsList.keySet()){
            textValue += s + "\t\t\t" + ingredientsList.get(s).toString() + "\n";
        }
        ingredients.setText(textValue);
    }


    public Recipe saveRecipeData(){
        if(!checkNullObjects()){
           String titleText = title.getText().toString();
           long prepTimeText = Long.parseLong(prepTime.getText().toString());
           long cookTimeText = Long.parseLong(cookTime.getText().toString());
           HashMap<String, Double> ingredients = new HashMap<>();
           ingredients.putAll(ingredientsList);
           String instructionsText = instructions.getText().toString();
           Bitmap image = currentImageFile;
           return new Recipe(titleText, prepTimeText, cookTimeText, ingredients, instructionsText, image);
        }
        else {
            return null;
        }
    }

    public boolean checkNullObjects(){
        if(title.getText().toString().isEmpty()){
            return true;
        }
        else if(prepTime.getText().toString().isEmpty()){
            return true;
        }
        else if(cookTime.getText().toString().isEmpty()){
            return true;
        }
        else if(ingredients.getText().toString().isEmpty()){
            return true;
        }
        else if(instructions.getText().toString().isEmpty()){
            return true;
        }
        return false;
    }

    public void invalidEntry(){
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);
        alertBuild.setTitle("Invalid Entry!");
        alertBuild.setMessage("A recipe must have each section filled!");
        alertBuild.create().show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu_make, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item){
        if(item.getItemId() == R.id.cameraMake){
            AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);
            alertBuild.setTitle("Coming Soon!");
            alertBuild.setMessage("Will have an optition to take a photo of the recipe, and display on the main activity.");
            alertBuild.create().show();
            //doThumb();
        }
        if(item.getItemId() == R.id.saveRecipeOpt) {
            save();
        }
        return super.onOptionsItemSelected(item);
    }

    public void save(){
        Recipe r = saveRecipeData();
        if (r != null) {
            Intent intent = new Intent();
            if (getIntent().hasExtra("EDIT_RECIPE")) {
                intent.putExtra("EDIT_RECIPE", r);
            } else {
                intent.putExtra("NEW_RECIPE", r);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
        else{
            invalidEntry();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);
        alertBuild.setTitle("Unsaved Recipe");
        alertBuild.setMessage("Do you want to save the changes you made?");
        alertBuild.setPositiveButton("YES", (dialog, id) -> {
            save();
        });
        alertBuild.setNegativeButton("NO", (dialog, id) -> super.onBackPressed());
        alertBuild.create().show();
    }
}
