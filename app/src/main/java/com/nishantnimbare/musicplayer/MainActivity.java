package com.nishantnimbare.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button=(Button)findViewById(R.id.start);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.e(TAG, "onClick: button clicked");

                    if(button.getText().toString().equals(getString(R.string.start))) {

                        startService(new Intent(MainActivity.this,mService.class).setAction(mService.ACTION_START));
                        button.setText("Stop");
                    }else {

                        stopService(new Intent(MainActivity.this,mService.class));
                        button.setText(R.string.start);
                    }

            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(new Intent(MainActivity.this,mService.class));
    }
}
