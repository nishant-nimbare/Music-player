package com.nishantnimbare.musicplayer.activities;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.net.Uri;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nishantnimbare.musicplayer.R;
import com.nishantnimbare.musicplayer.model.Song;
import com.nishantnimbare.musicplayer.service.mService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ArrayList<Song> songs;
    boolean isPlaying=false;
    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;

    mService service;
    private ServiceConnection serviceConn;

    Intent serviceIntent;

    int songPos=-1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songs = new ArrayList<>();


        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());


        recyclerView.addItemDecoration(dividerItemDecoration);
        adapter = new myAdapter();
        recyclerView.setAdapter(adapter);


        serviceIntent =new Intent(MainActivity.this,mService.class);

        startService(serviceIntent);

        //binding the service
        serviceConn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder binder) {

                mService.myServiceBinder mBinder = (mService.myServiceBinder)binder;
                 service =(mService)mBinder.getService();

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };

        bindService(serviceIntent,serviceConn,Context.BIND_AUTO_CREATE);

        getMp3Songs();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(serviceConn);
        stopService(serviceIntent);
    }




    public void getMp3Songs() {

        Uri allsongsuri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        /*Cursor cursor = managedQuery(allsongsuri, null, selection, null, null);*/

        Cursor cursor =getContentResolver().query(allsongsuri,null,selection,null,null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                   String song_name = cursor
                            .getString(cursor
                                    .getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    int song_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media._ID));

                    String fullpath = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.DATA));

                    songs.add(new Song(song_name,fullpath,song_id));

/*
                    album_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    int album_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));

                    artist_name = cursor.getString(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    int artist_id = cursor.getInt(cursor
                            .getColumnIndex(MediaStore.Audio.Media.ARTIST_ID));*/
                 //   Log.e(TAG, "song "+song_name+"\n id "+song_id+"\n fullpath "+fullpath );

                } while (cursor.moveToNext());

            }
            cursor.close();
            /*db.closeDatabase();*/
        }

        Collections.sort(songs,new mComparator());
        adapter.notifyDataSetChanged();
    }

    public void playPrev(View view) {

        if(songPos==0){
            songPos=songs.size()-1;
        }else{
            songPos--;
        }

        service.playSong(songs.get(songPos).getPath());
        isPlaying=true;
        findViewById(R.id.btnplay).setBackgroundResource(R.drawable.pause);
    }

    public void playNext(View view) {

        songPos= (songPos+1)%songs.size();
        service.playSong(songs.get(songPos).getPath());
        isPlaying=true;
        findViewById(R.id.btnplay).setBackgroundResource(R.drawable.pause);
    }

    public void playClicked(View view) {
        if(isPlaying) {
            isPlaying = false;
            findViewById(R.id.btnplay).setBackgroundResource(R.drawable.play);
        }else{
            isPlaying=true;
            findViewById(R.id.btnplay).setBackgroundResource(R.drawable.pause);
        }

        if(songPos==-1){
            songPos=0;
            service.playSong(songs.get(songPos).getPath());

        }else {
            service.playClicked();
        }
    }

    //Recycler view adapter here

    public class myAdapter extends RecyclerView.Adapter<myAdapter.myViewholder>{


        @NonNull
        @Override
        public myViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View v= LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item,viewGroup,false);
            return new myViewholder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull myViewholder holder, int position) {

            Song current = songs.get(position);

            holder.songItem.setText(current.getName());
        }

        @Override
        public int getItemCount() {
            return songs.size();
        }

        public class myViewholder extends RecyclerView.ViewHolder{

            TextView songItem;
            public myViewholder(@NonNull View itemView) {
                super(itemView);
                songItem=(TextView)itemView.findViewById(R.id.item_song);

                songItem.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this, " "+songItem.getText(), Toast.LENGTH_SHORT).show();
                      songPos = getAdapterPosition();
                        service.playSong(songs.get(songPos).getPath());
                        isPlaying=true;
                        findViewById(R.id.btnplay).setBackgroundResource(R.drawable.pause);
                    }
                });
            }
        }
    }


    public class mComparator implements Comparator<Song> {

        @Override
        public int compare(Song s1, Song s2) {
            return s1.getName().compareTo(s2.getName());
        }

    }

}
