package com.my_note.com.my_note_app;

public class Note {
    private String name;

    private String time;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Note(String name, String time) {
        this.name = name;
        this.time = time;
    }
}
