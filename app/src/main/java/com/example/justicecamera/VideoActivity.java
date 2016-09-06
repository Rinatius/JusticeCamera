package com.example.justicecamera;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {

    ProgressDialog pd;
    VideoView video;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        video = (VideoView) findViewById(R.id.videoView2);
        Intent i = getIntent();
        url = i.getStringExtra(VideoInfo.VIDEO_URL);

        pd = new ProgressDialog(VideoActivity.this);
        pd.setTitle(getString(R.string.loading_info));
        pd.setMessage(getString(R.string.wait));
        pd.show();


        Uri uri = Uri.parse(url);
        video.setVideoURI(uri);

        video.setMediaController(new MediaController(VideoActivity.this));
        video.setOnPreparedListener(MyVideoViewPreparedListener);
        video.setOnErrorListener(myVideoViewErrorListener);
        video.requestFocus();
        video.start();
    }

    MediaPlayer.OnPreparedListener MyVideoViewPreparedListener
            = new MediaPlayer.OnPreparedListener() {

        @Override
        public void onPrepared(MediaPlayer arg0) {
             pd.dismiss();
            Toast.makeText(getApplicationContext(),
                    getString(R.string.media_loaded),
                    Toast.LENGTH_LONG).show();
        }
    };

    MediaPlayer.OnErrorListener myVideoViewErrorListener
            = new MediaPlayer.OnErrorListener() {

        @Override
        public boolean onError(MediaPlayer arg0, int arg1, int arg2) {

            Toast.makeText(getApplicationContext(),
                    "Error!!!",
                    Toast.LENGTH_LONG).show();
            return true;
        }
    };

    @Override
    public void onBackPressed() {
       finish();
    }
}
