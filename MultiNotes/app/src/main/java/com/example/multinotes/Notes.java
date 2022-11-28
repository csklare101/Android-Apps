package com.example.multinotes;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class Notes implements Comparable<Notes>, Serializable {
    private String title;
    private String noteText;
    private long lastSave;

    public Notes(String t, String nt, long ls){
        title = t;
        noteText = nt;
        lastSave = ls;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setNoteText(String noteText) {
        this.noteText = noteText;
    }

    public void setLastSave(long lastSave) {
        this.lastSave = lastSave;
    }

    public String getTitle() {
        return title;
    }

    public String getNoteText() {
        return noteText;
    }

    public long getLastSave() {
        return lastSave;
    }

    public JSONObject toJSON() throws JSONException{
        JSONObject jobj = new JSONObject();

        jobj.put("title", title);
        jobj.put("noteText", noteText);
        jobj.put("lastSave", lastSave);

        return jobj;
    }

    public static Notes createFromJSON(JSONObject jobj) throws JSONException{
        String title = jobj.getString("title");
        String noteText = jobj.getString("noteText");
        long lastSave = jobj.getLong("lastSave");

        return new Notes(title, noteText, lastSave);
    }

    @Override
    public String toString() {
        return "Notes{" +
                "title='" + title + '\'' +
                ", noteText='" + noteText + '\'' +
                ", lastSave=" + lastSave +
                '}';
    }


    @Override
    public int compareTo(Notes notes) {
        return (int) (lastSave - notes.lastSave);
    }
}
