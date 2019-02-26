package com.example.admin.video;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageSwitcher;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Toast;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.source.hls.HlsMediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";


    private Context context;
    private SimpleExoPlayer simpleExoPlayer;
    private com.google.android.exoplayer2.trackselection.TrackSelector trackSelector;
    private SimpleExoPlayer player;
    private SeekBar seekBar;

    private SimpleExoPlayer exoPlayer;
    private PlayerView playerView;

    private DefaultBandwidthMeter bandwidthMeter;

    private Handler mainHandler;
    private ExtractorsFactory extractorsFactory;
    private DataSource.Factory mediaDataSourceFactory;
    private Dialog mFullScreenDialog;
    private View mExoPlayerView;
    private boolean mExoPlayerFullscreen;
    private ImageSwitcher mFullScreenIcon;
    private Window window;
    private float brightness;
    private ContentResolver conresolver;
    private SeekBar volumeSeekBar;
    private MediaPlayer mp;
    private AudioManager am;
    int Volume=0;
    private ProgressBar progressBar;

    public MainActivity() {
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        setContentView(R.layout.activity_main);


        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        playerView = (PlayerView) findViewById(R.id.player_view);
        playerView.requestFocus();

        bandwidthMeter = new DefaultBandwidthMeter();
        TrackSelection.Factory videoTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);

        trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        player = ExoPlayerFactory.newSimpleInstance(this, trackSelector);


        playerView.setPlayer(player);



        DataSource.Factory mediaDataSourceFactory = new DefaultDataSourceFactory(this, bandwidthMeter, new DefaultHttpDataSourceFactory(Util.getUserAgent(this, "simpleAudioApp"), bandwidthMeter)
        );

        player.setPlayWhenReady(true);
        MediaSource mediaSource = new HlsMediaSource.Factory(mediaDataSourceFactory).createMediaSource(Uri.parse("http://198.98.181.244/live/smil:rjtv.smil/playlist.m3u8"));


//        MediaSource mediaSource = new HlsMediaSource.Factory(Uri.parse("http://198.98.181.244/live/smil:rjtv.smil/playlist.m3u8"),
//                mediaDataSourceFactory, extractorsFactory, null, null);
        player.prepare(mediaSource);

        getSupportActionBar().hide();


        player.addListener(new ExoPlayer.DefaultEventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Toast.makeText(MainActivity.this,"The Video are loading" , Toast.LENGTH_SHORT).show();
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if(playWhenReady==true){
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {

            }

            @Override
            public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Toast.makeText(MainActivity.this , " The Video Cant display" , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onPositionDiscontinuity(int reason) {

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

            }

            @Override
            public void onSeekProcessed() {

            }



        });






        seekBar = (SeekBar)findViewById(R.id.seekBar);

        window = getWindow();
        seekBar.setMax(10);
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 10;
        window.setAttributes(layoutParams);

            seekBar.setProgress(5);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            public void onStopTrackingTouch(SeekBar seekBar)
            {
                Log.i(TAG, "onStopTrackingTouch: ");
//                Settings.System.putInt(conresolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
                Toast.makeText(getApplicationContext(), "Brightness: " + (brightness), Toast.LENGTH_SHORT).show();
            }

            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
            {
                Log.i(TAG, "onProgressChanged: " + progress);

                brightness = (float) progress / 10;
                WindowManager.LayoutParams layoutpars = window.getAttributes();
                layoutpars.screenBrightness = brightness;
                window.setAttributes(layoutpars);


            }
        });





        volumeSeekBar  =(SeekBar) findViewById(R.id.seekbar_sound);
        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        int maxVolume = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int curVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);

        volumeSeekBar.setMax(maxVolume);
        volumeSeekBar.setProgress(curVolume);

        volumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                am.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                Volume = progress;

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                Toast.makeText(getApplicationContext(), "Volume: " + Integer.toString(Volume), Toast.LENGTH_SHORT).show();

            }
        });






    }


}