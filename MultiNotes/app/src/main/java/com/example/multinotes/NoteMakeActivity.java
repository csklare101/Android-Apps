package com.example.multinotes;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class NoteMakeActivity extends AppCompatActivity {
    private EditText title;
    private EditText noteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_page);

        title = findViewById(R.id.noteTitleText);
        noteText = findViewById(R.id.noteContentText);

        if(getIntent().hasExtra("EDIT_NOTE")) {
            Notes n = (Notes) getIntent().getSerializableExtra("EDIT_NOTE");
            title.setText(n.getTitle());
            noteText.setText(n.getNoteText());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.opt_menu_note, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.saveNote){
            Notes n = saveNoteData();
            if(n.getTitle().length() > 0){
                if(n != null && n.getTitle().length() > 0){
                    Intent intent = new Intent();
                    if(getIntent().hasExtra("EDIT_NOTE")){
                        intent.putExtra("EDIT_NOTE", n);
                    }
                    else{
                        intent.putExtra("NEW_NOTE", n);
                    }
                setResult(RESULT_OK,intent);
            }
                finish();
            }
            else{
                checkNullTitle(n);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    public Notes saveNoteData(){
        String titleText = title.getText().toString();
        if(titleText.trim().isEmpty()){
        }

        String noteTextString = noteText.getText().toString();
        if(noteTextString.trim().isEmpty()){
            return null;
        }

        return new Notes(titleText, noteTextString, System.currentTimeMillis());
    }

    public void checkNullTitle(Notes n){
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);
        if(n.getTitle() == null || n.getTitle().length() == 0) {

            alertBuild.setPositiveButton("OK", (dialog, id) -> {
                Toast.makeText(this, "Cannot make note with no title!", Toast.LENGTH_SHORT).show();
                finish();
            });
            alertBuild.setNegativeButton("CANCEL", (dialog, id) -> { dialog.cancel();
            });

            alertBuild.setTitle("Can not have a note with no title!");
            alertBuild.setMessage("Exit without saving?");
            AlertDialog dialog = alertBuild.create();
            dialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertBuild = new AlertDialog.Builder(this);

        alertBuild.setPositiveButton("YES", (dialog, id) -> {
            Notes n = saveNoteData();
            if(n != null && n.getTitle().length() > 0){
                Intent intent = new Intent();
                if(getIntent().hasExtra("EDIT_NOTE")){
                    intent.putExtra("EDIT_NOTE", n);
                }
                else{
                    intent.putExtra("NEW_NOTE", n);
                }
                setResult(RESULT_OK,intent);
            }
            if(n.getTitle().length() != 0) {
                super.onBackPressed();
            }
            else{
                checkNullTitle(n);
            }
        });

        alertBuild.setNegativeButton("NO", (dialog, id) -> super.onBackPressed());
        alertBuild.setTitle("Unsaved Note");
        alertBuild.setMessage("Do you want to save the changes you made?");

        AlertDialog dialog = alertBuild.create();
        dialog.show();
    }
}
