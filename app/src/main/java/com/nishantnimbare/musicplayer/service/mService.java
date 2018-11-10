package com.nishantnimbare.musicplayer.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.MediaController;

import java.io.IOException;

public class mService extends Service {

    private static final String TAG = "mService";
/*
    public static final String ACTION_START ="com.nishantnimbare.musicplayer.action.start";
    public static final String ACTION_STOP ="com.nishantnimbare.musicplayer.action.stop";
    public static final String ACTION_PLAY ="com.nishantnimbare.musicplayer.action.play";
    public static final String ACTION_PREV ="com.nishantnimbare.musicplayer.action.prev";
    public static final String ACTION_NEXT ="com.nishantnimbare.musicplayer.action.next";*/
//    public static final int NOTI_ID =74216;



    MediaPlayer mediaPlayer;



    public class myServiceBinder extends Binder{
        public mService getService(){
            return mService.this;
        }
    }

    private IBinder myBinder = new myServiceBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind: service bound");
        return myBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        mediaPlayer = new MediaPlayer();
        Log.e(TAG, "onStartCommand: sevice started");

        return START_STICKY;
    }


   public void playSong(String path){

        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
        }

        try {

            mediaPlayer.reset();
            mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(path));
            Log.e(TAG, "onClick: dataSource set");
            Log.e(TAG, "path "+path);
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

    }

    public void playClicked() {

    if(mediaPlayer.isPlaying()){
        mediaPlayer.pause();
    }else{
        mediaPlayer.start();
    }

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
