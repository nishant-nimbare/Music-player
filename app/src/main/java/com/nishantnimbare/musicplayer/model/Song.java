package com.nishantnimbare.musicplayer.model;

public class Song {
    String name,path;
    int song_id;

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }

    public int getSong_id() {
        return song_id;
    }

    public Song(String name, String path, int song_id) {
        this.name = name;
        this.path = path;
        this.song_id = song_id;
    }
}
