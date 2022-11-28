package com.example.multinotes;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
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
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener{

    private RecyclerView recyclerView;
    private Notes testNote;
    //make an adapter here
    //make array list of notes here FINAL
    private final ArrayList<Notes> notesList = new ArrayList<>();
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private Notes currentNote;
    private NotesAdapter nAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler);
        //connect recylerview to adapter
        //set layout manager
        nAdapter = new NotesAdapter(notesList, this);
        recyclerView.setAdapter(nAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadDataFromFile();

        //add test notes, add to collection
        /*if(notesList.isEmpty()) {
            String testNoteText = "This is a test note, this will test how it will show on screen. 80 chars too!!!!";
            testNote = new Notes("Test Title", testNoteText, System.currentTimeMillis());
            notesList.add(testNote);
            //tell the adapter that I changed the list
            nAdapter.notifyItemRangeChanged(0, notesList.size());
        } */

        activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                this::handleResult);
    }

    public void handleResult(ActivityResult result){
        if(result == null || result.getData() == null){
          return;
        }

        Intent data = result.getData();
        if(result.getResultCode() == RESULT_OK){
            if(data.hasExtra("NEW_NOTE")) {
                Notes n = (Notes)data.getSerializableExtra("NEW_NOTE");
                notesList.add(n);
                Collections.sort(notesList, Collections.reverseOrder());
                nAdapter.notifyItemRangeChanged(0, notesList.size());
            }
            else if(data.hasExtra("EDIT_NOTE")){
                Notes n = (Notes) data.getSerializableExtra("EDIT_NOTE");

                currentNote.setTitle(n.getTitle());
                currentNote.setNoteText(n.getNoteText());
                currentNote.setLastSave(n.getLastSave());
                Collections.sort(notesList, Collections.reverseOrder());
                nAdapter.notifyItemRangeChanged(0,notesList.size());
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
        if(item.getItemId() == R.id.about) {
            Intent aboutIntent = new Intent(this, AboutActivity.class);
            startActivity(aboutIntent);
        }
        else if(item.getItemId() == R.id.addNote){
            Intent makeIntent = new Intent(this, NoteMakeActivity.class);
            activityResultLauncher.launch(makeIntent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        int pos = recyclerView.getChildLayoutPosition(view);
        currentNote = notesList.get(pos);
        Intent intent = new Intent(this, NoteMakeActivity.class);
        intent.putExtra("EDIT_NOTE", currentNote);
        activityResultLauncher.launch(intent);
    }

    @Override
    public boolean onLongClick(View view) {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);
        alertBuild.setPositiveButton("YES", (dialog, id) -> {
            int pos = recyclerView.getChildLayoutPosition(view);
            notesList.remove(pos);

            nAdapter.notifyItemRemoved(pos);
            setTitle(getString(R.string.app_name) + " (" + notesList.size() + ")");
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
        for(Notes n : notesList){
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
                notesList.add(Notes.createFromJSON(jsonArray.getJSONObject(j)));
            }
        }
        catch (Exception e) {
            Log.d(getClass().getSimpleName(), e.getMessage());
            return;
        }
        setTitle(getString(R.string.app_name) + "(" + notesList.size() + ")");
    }

    @Override
    protected void onPause(){
        saveDataToFile();
        super.onPause();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}