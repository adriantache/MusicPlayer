package com.adriantache.musicplayer;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by TREE on 13/03/2018.
 * <p>
 * Custom array adapter to accept my custom list layout
 */

public class CustomArrayAdapter extends ArrayAdapter<Song> {

    public CustomArrayAdapter(@NonNull Context context, @NonNull List<Song> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View listItemView = convertView;

        Song currentSong = getItem(position);

        if (listItemView == null)
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.track_box, parent, false);

        TextView song_title = listItemView.findViewById(R.id.song_title);
        song_title.setText(currentSong.getSongTitle());

        TextView author = listItemView.findViewById(R.id.artist);
        author.setText(currentSong.getAuthor());

        ImageView imageView = listItemView.findViewById(R.id.album_art);
        imageView.setImageResource(currentSong.getAlbumArtResID());

        return listItemView;
    }
}
