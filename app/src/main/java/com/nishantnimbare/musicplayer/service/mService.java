package com.nishantnimbare.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;

public class mService extends Service {

    private static final String TAG = "mService";

    public static final String ACTION_START ="com.nishantnimbare.musicplayer.action.start";
    public static final String ACTION_STOP ="com.nishantnimbare.musicplayer.action.stop";
    public static final String ACTION_PLAY ="com.nishantnimbare.musicplayer.action.play";
    public static final String ACTION_PREV ="com.nishantnimbare.musicplayer.action.prev";
    public static final String ACTION_NEXT ="com.nishantnimbare.musicplayer.action.next";
//    public static final int NOTI_ID =74216;



    MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String song_path=intent.getStringExtra("song_path");


        if(intent.getAction().equals(ACTION_START)) {
            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(song_path));
                Log.e(TAG, "onClick: dataSource set");
                Log.e(TAG, "path "+song_path);
                mediaPlayer.prepareAsync();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        Log.e(TAG, "onPrepared: mediaPlayer started");

                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }


        }else if(intent.getAction().equals(ACTION_PLAY)){

            Log.e(TAG, "onStartCommand: play clicked");
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                Log.e(TAG, "paused!!");
            }else{
                mediaPlayer.start();
                Log.e(TAG, "played!!");
            }

        }else {
            Log.e(TAG, "onStartCommand: action "+intent.getAction());
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        Log.e(TAG, "onClick: media Player stopped");
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer=null;


    }
}
