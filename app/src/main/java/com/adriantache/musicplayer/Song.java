package com.adriantache.musicplayer;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by TREE on 12/03/2018.
 * <p>
 * class to store all aspects of a song
 */

public class Song implements Parcelable{
    private int songResID = 0;
    private int albumArtResID = 0;
    private String songTitle = "Error";
    private String author = "Error";
    private String album = "Error";

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    private Song(Parcel in){
        this.songResID = in.readInt();
        this.albumArtResID = in.readInt();
        this.songTitle = in.readString();
        this.author = in.readString();
        this.album = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.songResID);
        dest.writeInt(albumArtResID);
        dest.writeString(songTitle);
        dest.writeString(author);
        dest.writeString(album);
    }

    Song(int songResID, int albumArtResID, String songTitle, String author, String album) {
        this.songResID = songResID;
        this.albumArtResID = albumArtResID;
        this.songTitle = songTitle;
        this.author = author;
        this.album = album;
    }

    public Song(int songResID, String songTitle, String author, String album) {
        this.songResID = songResID;
        this.songTitle = songTitle;
        this.author = author;
        this.album = album;
        this.albumArtResID = android.R.drawable.ic_delete;
    }

    public int getAlbumArtResID() {
        return albumArtResID;
    }

    public int getSongResID() {
        return songResID;
    }

    public String getAlbum() {
        return album;
    }

    public String getAuthor() {
        return author;
    }

    public String getSongTitle() {
        return songTitle;
    }
}
