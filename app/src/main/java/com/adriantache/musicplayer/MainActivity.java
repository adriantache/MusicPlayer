package com.adriantache.musicplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
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

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    //define a status to determine no tracks have been selected in the PlaylistActivity activity
    private static int STATUS_NO_TRACK_SELECTED = 99;
    //define audioFocusChangeListener to request and give audio focus
    AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    //define audio manager to request and release audio focus
    AudioManager audioManager;
    //create an ArrayList to hold all the tracks
    private ArrayList<Song> trackList = new ArrayList<>();
    //define views
    private ImageView play;
    private Switch repeatView;
    private Switch shuffleView;
    private TextView song_title;
    private TextView artist;
    private ImageView album_art;
    //keep track of current track ResID
    private int currentTrack;
    //create a MediaPlayer
    private MediaPlayer mediaPlayer;
    //keep track of array index, start at first position
    private int trackIndex = 0;
    //keep track of repeat and shuffle
    private boolean repeat = false;
    private boolean shuffle = false;
    //define broadcast receiver for detecting headphone removal
    private BroadcastReceiver noisyReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ImageView next_track;
        ImageView previous_track;
        LinearLayout track_box;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //find views
        play = findViewById(R.id.play_button);
        repeatView = findViewById(R.id.repeat);
        shuffleView = findViewById(R.id.shuffle);
        song_title = findViewById(R.id.song_title);
        artist = findViewById(R.id.artist);
        album_art = findViewById(R.id.album_art);
        track_box = findViewById(R.id.track_box);
        next_track = findViewById(R.id.next_track);
        previous_track = findViewById(R.id.previous_track);

        //set OnClickListeners
        album_art.setOnClickListener(this);
        track_box.setOnClickListener(this);
        play.setOnClickListener(this);
        repeatView.setOnClickListener(this);
        shuffleView.setOnClickListener(this);
        next_track.setOnClickListener(this);
        previous_track.setOnClickListener(this);

        //populate track index with songs
        populateTrackList();

        //manage audio focus
        audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int i) {
                switch (i) {
                    case AudioManager.AUDIOFOCUS_LOSS:
                        stopMusic();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:
                        if (mediaPlayer != null) if (mediaPlayer.isPlaying()) playButton();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:
                        if (mediaPlayer != null) if (mediaPlayer.isPlaying()) playButton();
                        break;
                    case AudioManager.AUDIOFOCUS_GAIN:
                        if (mediaPlayer != null) playButton();
                        break;
                }
            }
        };

        //manage NOISY (headphones removal)
        noisyReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                pauseMusic();
            }
        };

    }

    //set onClickListeners
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.album_art:
                goToPlaylist();
                break;
            case R.id.track_box:
                goToPlaylist();
                break;
            case R.id.play_button:
                playButton();
                break;
            case R.id.next_track:
                nextTrack();
                break;
            case R.id.previous_track:
                previousTrack();
                break;
            case R.id.repeat:
                repeat = repeatView.isChecked();
                setRepeat();
                break;
            case R.id.shuffle:
                shuffle = shuffleView.isChecked();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != STATUS_NO_TRACK_SELECTED) {
            trackIndex = resultCode - 1;
            nextTrack();
        }
    }

    //release the MediaPlayer on stop
    @Override
    public void onStop() {
        stopMusic();

        super.onStop();
    }

    //pause music on screen off, app switch etc.
    @Override
    protected void onPause() {
        super.onPause();

        pauseMusic();
    }

    //restart music when coming back to app
    @Override
    protected void onResume() {
        super.onResume();

        playMusic();
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

        updateTrackBox();
    }

    private void nextTrack() {
        if (!shuffle) {
            trackIndex++;
            if (trackIndex == trackList.size()) trackIndex = 0;
        } else {
            Random random = new Random();
            trackIndex = random.nextInt(trackList.size());
        }

        //get next track
        currentTrack = trackList.get(trackIndex).getSongResID();

        //create the media player and start the music
        createMediaPlayer();
    }

    private void previousTrack() {
        if (!shuffle) {
            trackIndex--;
            if (trackIndex == -1) trackIndex = trackList.size() - 1;
        } else {
            Random random = new Random();
            trackIndex = random.nextInt(trackList.size());
        }

        //get previous track
        currentTrack = trackList.get(trackIndex).getSongResID();

        //create the media player and start the music
        createMediaPlayer();
    }

    private void createMediaPlayer() {
        //first stop the media player, if playing
        stopMusic();

        //then create a new one
        mediaPlayer = MediaPlayer.create(this, currentTrack);

        //start the music
        playMusic();
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
                pauseMusic();
            } else {
                playMusic();
            }
        } else {
            createMediaPlayer();
        }
    }

    private void goToPlaylist() {
        Intent intent = new Intent(MainActivity.this, PlaylistActivity.class);
        intent.putParcelableArrayListExtra("trackList", trackList);
        startActivityForResult(intent, STATUS_NO_TRACK_SELECTED);
    }

    //stop the media player
    private void stopMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.stop();
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    //pause the media player
    private void pauseMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.isPlaying())
                mediaPlayer.pause();

            //remove receiver to detect headphone removal
            try {
                unregisterReceiver(noisyReceiver);
                noisyReceiver = null;
            } catch (IllegalArgumentException e) {
                if (BuildConfig.DEBUG) e.printStackTrace();
            }

            //remove audio focus
            audioManager.abandonAudioFocus(audioFocusChangeListener);

            //change icon to pause and animate
            Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.pause_play);
            play.setImageDrawable(play_drawable);
            if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();
        }
    }

    //play the selected track
    private void playMusic() {
        if (mediaPlayer != null) {
            //request audio focus
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            int result = 0;
            if (audioManager != null) {
                result = audioManager.requestAudioFocus(audioFocusChangeListener, AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
            }
            if (result != AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                return;
            }

            //start playback
            mediaPlayer.start();

            //register Intent Filter to use for detecting playback start and headphones removal
            IntentFilter filter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
            registerReceiver(noisyReceiver, filter);

            //move to next track after playback
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    nextTrack();
                }
            });

            //change icon to play and animate
            Drawable play_drawable = ContextCompat.getDrawable(this, R.drawable.play_pause);
            play.setImageDrawable(play_drawable);
            if (play_drawable instanceof Animatable) ((Animatable) play_drawable).start();

            //set song details in the track box
            updateTrackBox();
        }
    }

    //set song details in the track box
    private void updateTrackBox() {
        song_title.setText(trackList.get(trackIndex).getSongTitle());
        artist.setText(trackList.get(trackIndex).getAuthor());
        album_art.setImageResource(trackList.get(trackIndex).getAlbumArtResID());
    }
}
