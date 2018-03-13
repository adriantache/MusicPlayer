package com.adriantache.musicplayer;

import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    //create an ArrayList to hold all the tracks
    public ArrayList<Song> trackList = new ArrayList<>();

    //define views
    ImageView play;
    ImageView next_track;
    ImageView previous_track;
    Switch repeatView;
    Switch shuffleView;
    TextView song_title;
    TextView artist;
    ImageView album_art;
    LinearLayout track_box;

    //keep track of current track ResID
    int currentTrack;

    //create a MediaPlayer
    MediaPlayer mediaPlayer;

    //keep track of array index, start at first position
    int trackIndex = 0;

    //keep track of repeat and shuffle
    boolean repeat = false;
    boolean shuffle = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find views
        play = findViewById(R.id.play_button);
        next_track = findViewById(R.id.next_track);
        previous_track = findViewById(R.id.previous_track);
        repeatView = findViewById(R.id.repeat);
        shuffleView = findViewById(R.id.shuffle);
        song_title = findViewById(R.id.song_title);
        artist = findViewById(R.id.artist);
        album_art = findViewById(R.id.album_art);
        track_box = findViewById(R.id.track_box);

        //set onClickListeners
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton();
            }
        });
        next_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextTrack();
            }
        });
        previous_track.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousTrack();
            }
        });
        repeatView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeat = repeatView.isChecked();
                setRepeat();
            }
        });
        shuffleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuffle = shuffleView.isChecked();
            }
        });
        track_box.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Playlist.class);
                intent.putParcelableArrayListExtra("trackList", trackList);
                startActivityForResult(intent, 99);
            }
        });
        album_art.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Playlist.class);
                intent.putParcelableArrayListExtra("trackList", trackList);
                startActivityForResult(intent, 99);
            }
        });

        //populate track index with songs
        populateTrackList();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != 99) {
            trackIndex = resultCode - 1;
            nextTrack();
        }
    }

    //this may be unnecessary; stops music on destroy
    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        super.onDestroy();
    }

    //pause music on screen off, app switch etc.
    @Override
    protected void onPause() {
        super.onPause();

        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();

            //change icon to pause and animate
            Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.pause_play);
            play.setImageDrawable(play_drawable);
            if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();
        }
    }

    //restart music when coming back to app
    @Override
    protected void onResume() {
        super.onResume();

        if (mediaPlayer != null) {
            mediaPlayer.start();

            //change icon to play and animate
            Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.play_pause);
            play.setImageDrawable(play_drawable);
            if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();
        }
    }

    //function to create array of tracks to navigate
    private void populateTrackList() {
        trackList.add(new Song(R.raw.demo, R.drawable.winamp, "Llama Whippin' Intro", "DJ Mike Llama", "Beats of Burdon"));
        trackList.add(new Song(R.raw.bass1, R.drawable.synthwave, "129 Am Bass SP 01", "Samplephonics", "80s Synthwave Demo"));
        trackList.add(new Song(R.raw.bass2, R.drawable.synthwave, "108 Cm Bass SP 02", "Samplephonics", "80s Synthwave Demo"));
        trackList.add(new Song(R.raw.drumloop1, R.drawable.synthwave, "130 Drumloop SP 01", "Samplephonics", "80s Synthwave Demo"));
        trackList.add(new Song(R.raw.drumloop2, R.drawable.synthwave, "100 Drumloop SP 02", "Samplephonics", "80s Synthwave Demo"));
        trackList.add(new Song(R.raw.ruff, R.drawable.synthwave, "130 Cm Riff SP 01", "Samplephonics", "80s Synthwave Demo"));

        currentTrack = trackList.get(trackIndex).getSongResID();

        song_title.setText(trackList.get(trackIndex).getSongTitle());
        artist.setText(trackList.get(trackIndex).getAuthor());
        album_art.setImageResource(trackList.get(trackIndex).getAlbumArtResID());
    }

    private void nextTrack() {
        if (!shuffle) {
            trackIndex++;
            if (trackIndex == trackList.size()) trackIndex = 0;
        } else {
            Random random = new Random();
            trackIndex = random.nextInt(trackList.size());
        }

        currentTrack = trackList.get(trackIndex).getSongResID();
        createMediaPlayer();

        song_title.setText(trackList.get(trackIndex).getSongTitle());
        artist.setText(trackList.get(trackIndex).getAuthor());
        album_art.setImageResource(trackList.get(trackIndex).getAlbumArtResID());

        //change icon to play and animate
        Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.play_pause);
        play.setImageDrawable(play_drawable);
        if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();
    }

    private void previousTrack() {
        if (!shuffle) {
            trackIndex--;
            if (trackIndex == -1) trackIndex = trackList.size() - 1;
        } else {
            Random random = new Random();
            trackIndex = random.nextInt(trackList.size());
        }

        currentTrack = trackList.get(trackIndex).getSongResID();
        createMediaPlayer();

        song_title.setText(trackList.get(trackIndex).getSongTitle());
        artist.setText(trackList.get(trackIndex).getAuthor());
        album_art.setImageResource(trackList.get(trackIndex).getAlbumArtResID());

        //change icon to play and animate
        Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.play_pause);
        play.setImageDrawable(play_drawable);
        if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();
    }

    private void createMediaPlayer() {
        //first stop the media player, if playing
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }

        //then create a new one
        mediaPlayer = MediaPlayer.create(this, currentTrack);
        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                nextTrack();
            }
        });
    }

    private void setRepeat() {
        if (mediaPlayer != null) {
            if (repeat) mediaPlayer.setLooping(true);
            else mediaPlayer.setLooping(false);
        }
    }

    public void playButton() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();

                //change icon to pause and animate
                Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.pause_play);
                play.setImageDrawable(play_drawable);
                if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();
            } else {
                mediaPlayer.start();

                //change icon to play and animate
                Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.play_pause);
                play.setImageDrawable(play_drawable);
                if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();
            }
        } else {
            createMediaPlayer();

            //change icon to play and animate
            Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.play_pause);
            play.setImageDrawable(play_drawable);
            if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();
        }
    }
}
