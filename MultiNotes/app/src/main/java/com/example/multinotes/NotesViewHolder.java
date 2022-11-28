package com.example.multinotes;

import android.text.InputFilter;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


public class NotesViewHolder extends RecyclerView.ViewHolder {
    TextView title;
    TextView noteText;
    TextView lastSave;

    public NotesViewHolder(@NonNull View v){
        super(v);
        title = v.findViewById(R.id.noteName);
        noteText = v.findViewById(R.id.noteText);
        if(noteText.getText().toString().length() >= 80){
            String shortNote = noteText.getText().toString().substring(0,80) + "...";
            noteText.setText(shortNote);
        }
        noteText.setMaxLines(2);
        lastSave = v.findViewById(R.id.dateText);
    }
}
