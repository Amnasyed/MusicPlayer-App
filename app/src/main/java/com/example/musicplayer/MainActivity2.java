package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.gauravk.audiovisualizer.visualizer.BarVisualizer;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Thread.sleep;

public class MainActivity2 extends AppCompatActivity {
        Button btnplay,btnnext,btnback,btnffrwd,btnfastrwd;
        TextView txtname,txtstart,txtstop;
        SeekBar seekmusic;
        BarVisualizer visualizer;
        String sname;
        public static final String EXTRA_NAME="song_name";
        static MediaPlayer mediaPlayer;
        ImageView imageView;
        int position;
        Thread updateseekbar;
        ArrayList<File> mySongs;

    @Override
    protected void onDestroy() {
        if(visualizer!=null){
            visualizer.release();
        }
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        btnplay=findViewById(R.id.playbtn);
        btnback=findViewById(R.id.backbtn);
        btnfastrwd=findViewById(R.id.fastrwd);
        btnnext=findViewById(R.id.forwardbtn);
        btnffrwd=findViewById(R.id.fastfrw);
        txtname=findViewById(R.id.songtext);
        imageView=findViewById(R.id.imageView);
        seekmusic=findViewById(R.id.seekbar);
        visualizer=findViewById(R.id.blast);
        txtstart=findViewById(R.id.textstart);
        txtstop=findViewById(R.id.textstop);

        if (mediaPlayer!=null)
        {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent i=getIntent();
        Bundle bundle=i.getExtras();
        mySongs=(ArrayList)bundle.getParcelableArrayList("songs");
        String songname=i.getStringExtra("newSong");
        position=bundle.getInt("pos",0);
        txtname.setSelected(true);

        Uri uri=Uri.parse(mySongs.get(position).toString());
        sname=mySongs.get(position).getName();
        mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
        mediaPlayer.start();

                        updateseekbar=new Thread(){
                            @Override
                            public void run() {
                                int totalDuration=mediaPlayer.getDuration();
                               int currentposition=0;
                    while(currentposition<totalDuration){
                        try {
                            sleep(500);
                            currentposition=mediaPlayer.getCurrentPosition();
                            seekmusic.setProgress(currentposition);
                        }
                        catch (InterruptedException|IllegalStateException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                };
                seekmusic.setMax(mediaPlayer.getDuration());
                updateseekbar.start();
                seekmusic.getProgressDrawable().setColorFilter(getResources().getColor(R.color.design_default_color_primary), PorterDuff.Mode.MULTIPLY);
                seekmusic.getThumb().setColorFilter(getResources().getColor(R.color.av_dark_blue),PorterDuff.Mode.SRC_IN);
                seekmusic.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
mediaPlayer.seekTo(seekBar.getProgress());
                    }
                });
                String endTime=createTime(mediaPlayer.getDuration());
                txtstop.setText(endTime);
                final Handler handler=new Handler();
                final int delay=1000;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String currenentTime=createTime(mediaPlayer.getCurrentPosition());
                        txtstart.setText(currenentTime);
                        handler.postDelayed(this,delay);

                    }
                },delay);


        btnplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.isPlaying()){
                    btnplay.setBackgroundResource(R.drawable.ic_baseline_play_arrow_24);
                    mediaPlayer.pause();
                }
                else {
                    btnplay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                    mediaPlayer.start();
                }
            }
        });
        btnnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position+1)%mySongs.size());
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();
                btnplay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                startAnimation(imageView);
                int audiosessionId=mediaPlayer.getAudioSessionId();
                if(audiosessionId!=-1){
                    visualizer.setAudioSessionId(audiosessionId);
                }
            }
        });
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                position=((position-1)<0)?(mySongs.size()-1):(position-1);
                Uri u=Uri.parse(mySongs.get(position).toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                sname=mySongs.get(position).getName();
                txtname.setText(sname);
                mediaPlayer.start();



                btnplay.setBackgroundResource(R.drawable.ic_baseline_pause_24);
                startAnimation(imageView);
                int audiosessionId=mediaPlayer.getAudioSessionId();
                if(audiosessionId!=-1){
                    visualizer.setAudioSessionId(audiosessionId);
                }
            }
        });
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnnext.performClick();
            }
        });

        int audiosessionId=mediaPlayer.getAudioSessionId();
        if(audiosessionId!=-1){
            visualizer.setAudioSessionId(audiosessionId);
        }
btnffrwd.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()+10000);
        }
    }
});
btnfastrwd.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
        }
    }
});


    }
    public void startAnimation(View view){
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createTime(int duration){
        String time=" ";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time+=min+":";
        if(sec<10){
            time+="0";

        }
        time+=sec;
        return time;

    }
}