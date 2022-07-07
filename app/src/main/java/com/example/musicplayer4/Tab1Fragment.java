package com.example.musicplayer4;

import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Tab1Fragment extends Fragment {
    private static final String TAG = "Tab1Fragment";

    Button playBtn;
    SeekBar positionBar;
    SeekBar volumeBar;
    TextView elapsedTimeLabel;
    TextView remainingTimeLabel;
    TextView placeholder;
    MediaPlayer mp = new MediaPlayer();
    int totalTime;
    String songPath;
    int sequence;
    int cPosition = 0;

    DataCommunication dataPasser;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (DataCommunication) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab1_fragment,container,false);

        elapsedTimeLabel = (TextView) view.findViewById(R.id.elapsedTimeLabel);
        remainingTimeLabel = (TextView) view.findViewById(R.id.remainingTimeLabel);
        //position bar
        positionBar = (SeekBar) view.findViewById(R.id.positionBar);

        playBtn = (Button) view.findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity activity = (MainActivity) getActivity();
                songPath = activity.getMyData();
                System.out.println(songPath);
                //mp = MediaPlayer.create(getActivity(), R.raw.a_place_to_start);
                if(!mp.isPlaying()) {
                    mp = MediaPlayer.create(getActivity(), Uri.parse(songPath));
                    totalTime = mp.getDuration();
                    //stopping
                    mp.seekTo(cPosition);
                    mp.start();
                    playBtn.setBackgroundResource(R.drawable.stop);
                }
                else {
                    //playing
                    mp.pause();
                    cPosition = mp.getCurrentPosition();
                    playBtn.setBackgroundResource(R.drawable.play);
                }

                mp.setVolume(30f, 30f);

                positionBar.setMax(totalTime);
                positionBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(fromUser) {
                            mp.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });
            }
        });

        mp.setLooping(true);
        mp.setVolume(30f, 30f);

        //volume bar
        volumeBar = (SeekBar) view.findViewById(R.id.volumeBar);
        volumeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                float volumeNum = progress / 100f;
                mp.setVolume(volumeNum, volumeNum);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        //update position bar and time label
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(mp != null) {
                    try {
                        Message msg = new Message();
                        msg.what = mp.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
        return view;
    }

    private Handler handler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            //update position bar
            positionBar.setProgress(currentPosition);

            //update labels
            String elapsedTime = createTimeTable(currentPosition);
            elapsedTimeLabel.setText(elapsedTime);

            String remainingTime = createTimeTable(totalTime-currentPosition);
            remainingTimeLabel.setText("- " + remainingTime);
        }
    };

    public String createTimeTable(int time) {
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;

        timeLabel = min + ":";
        if(sec < 10) timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }
}
