package com.nishantnimbare.musicplayer.activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nishantnimbare.musicplayer.R;
import com.nishantnimbare.musicplayer.model.Song;
import com.nishantnimbare.musicplayer.service.mService;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    ArrayList<Song> songs;
    Button button;
    boolean isPlaying=false;
    RecyclerView.Adapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songs = new ArrayList<>();
        button=(Button)findViewById(R.id.start);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "onClick: button clicked");

                    if(isPlaying) {

                        Intent intent = new Intent(MainActivity.this,mService.class);
                        intent.setAction(mService.ACTION_PLAY);
                        startService(intent);
                        isPlaying=false;
                        button.setText("PLAY");

                    }else {

                        Intent intent = new Intent(MainActivity.this,mService.class);
                        intent.setAction(mService.ACTION_PLAY);
                        startService(intent);
                        isPlaying=true;
                        button.setText("PAUSE");
                    }

            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new myAdapter();
        recyclerView.setAdapter(adapter);

        getMp3Songs();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this,mService.class));
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
        adapter.notifyDataSetChanged();
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
                        Intent intent = new Intent(MainActivity.this,mService.class);
                        intent.putExtra("song_path",songs.get(getAdapterPosition()).getPath());
                        intent.setAction(mService.ACTION_START);
                        startService(intent);
                        isPlaying=true;
                        button.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }
}
