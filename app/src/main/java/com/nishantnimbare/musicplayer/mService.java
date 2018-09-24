package com.nishantnimbare.musicplayer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.io.File;
import java.io.IOException;

public class mService extends Service {

    private static final String TAG = "mService";

    public static final String ACTION_START ="com.nishantnimbare.musicplayer.action.start";
    public static final String ACTION_STOP ="com.nishantnimbare.musicplayer.action.stop";
    public static final String ACTION_PLAY ="com.nishantnimbare.musicplayer.action.play";
    public static final String ACTION_PREV ="com.nishantnimbare.musicplayer.action.prev";
    public static final String ACTION_NEXT ="com.nishantnimbare.musicplayer.action.next";
    public static final int NOTI_ID =74216;



    MediaPlayer mediaPlayer;
    Notification notification;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        if(intent.getAction().equals(ACTION_START)) {
            mediaPlayer = new MediaPlayer();

            final String directory = Environment.getExternalStorageDirectory().toString();
            String path = directory + File.separator + "demo.mp3";


            try {
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(path));
                Log.e(TAG, "onClick: dataSource set");
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

            //making notification

            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Intent playIntent = new Intent(getApplicationContext(), mService.class);
            playIntent.setAction(ACTION_PLAY);
            PendingIntent pendPlayIntent = PendingIntent.getService(this, 1, playIntent, 0);

             notification = new NotificationCompat.Builder(this,"musictestapp")
                    .setContentTitle("music app")
                    .setTicker("music app")
                    .setContentText("hawa hawa")
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .setContentIntent(pendingIntent)
                    .setOngoing(true)
                    .addAction(R.drawable.ic_play_arrow, "Play", pendPlayIntent)
                    .build();

            startForeground(NOTI_ID,notification);



        }else if(intent.getAction().equals(ACTION_PLAY)){

            Log.e(TAG, "onStartCommand: play clicked");
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
            }else{
                mediaPlayer.start();
            }
/*            stopSelf();
            NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(NOTI_ID);*/

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

        NotificationManager manager=(NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(NOTI_ID);

    }
}
