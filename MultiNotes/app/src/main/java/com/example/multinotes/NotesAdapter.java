package com.example.multinotes;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotesAdapter extends RecyclerView.Adapter<NotesViewHolder> {
    private final ArrayList<Notes> notesList;
    private final MainActivity mainAct;
    private final SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM d, h:mm a", Locale.getDefault());

    public NotesAdapter(ArrayList<Notes> n, MainActivity ma){
        this.notesList = n;
        this.mainAct = ma;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_entry, parent, false);
        itemView.setOnClickListener(mainAct);
        itemView.setOnLongClickListener(mainAct);

        return new NotesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        Notes note = notesList.get(position);

        holder.title.setText(note.getTitle());
        if(note.getNoteText().length() >= 80){
            String shortNote = note.getNoteText().substring(0,80) + "...";
            holder.noteText.setText(shortNote);
        }
        else {
            holder.noteText.setText(note.getNoteText());
        }
        holder.lastSave.setText(sdf.format(new Date(note.getLastSave())));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }
}
