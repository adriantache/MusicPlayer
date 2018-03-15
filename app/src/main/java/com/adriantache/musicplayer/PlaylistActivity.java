package com.adriantache.musicplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;

public class PlaylistActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        ArrayList<Song> trackList = getIntent().getParcelableArrayListExtra("trackList");

        ListView track_list = findViewById(R.id.track_list);
        CustomArrayAdapter customArrayAdapter = new CustomArrayAdapter(this, trackList);
        track_list.setAdapter(customArrayAdapter);

        track_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setResult(position);
                finish();
            }
        });
    }

    //prevent music from playing when we exit without selecting a track
    @Override
    public void onBackPressed() {
        //define a status to determine no tracks have been selected in the PlaylistActivity
        int STATUS_NO_TRACK_SELECTED = 99;

        setResult(STATUS_NO_TRACK_SELECTED);
        finish();
    }
}
