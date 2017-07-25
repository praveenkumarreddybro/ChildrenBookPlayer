package com.effone.childrenbookplayer;

import android.app.Activity;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;

import com.effone.childrenbookplayer.model.AudioData;
import com.effone.childrenbookplayer.view.DefaultLrcBuilder;
import com.effone.childrenbookplayer.view.ILrcBuilder;
import com.effone.childrenbookplayer.view.ILrcView;
import com.effone.childrenbookplayer.view.LrcRow;
import com.effone.childrenbookplayer.view.LrcView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends Activity implements View.OnClickListener {

	public final static String TAG = "MainActivity";
	ILrcView mLrcView;
    private int mPalyTimerDuration = 1000;
    private Timer mTimer;
    private int mValues;
    private TimerTask mTask;
    private AudioData sample,sample1,sample2;
    private ArrayList<AudioData> sampleArrayList;
    private int fileValues=0;
    private ImageView mImgNext,mImgPrevious,mImgPause,mImgAudioPic;
    MediaPlayer mPlayer;
    public String getFromAssets(String fileName){
        try {
            InputStreamReader inputReader = new InputStreamReader( getResources().getAssets().open(fileName) );
            BufferedReader bufReader = new BufferedReader(inputReader);
            String line="";
            String Result="";
            while((line = bufReader.readLine()) != null){
            	if(line.trim().equals(""))
            		continue;
            	Result += line + "\r";
            }
            return Result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   mLrcView = new LrcView(this, null);
        Intent intent = getIntent();
        mValues = intent.getIntExtra("value",0);
        setContentView(R.layout.activity_main);


        mImgNext=(ImageView)findViewById(R.id.tv_next);
        mImgPrevious=(ImageView)findViewById(R.id.tv_previous);
        mImgPause=(ImageView)findViewById(R.id.tv_pause);
        mImgAudioPic=(ImageView)findViewById(R.id.img_schoolImages);
        mImgNext.setOnClickListener(this);
        mImgPrevious.setOnClickListener(this);
        mImgPause.setOnClickListener(this);

        gettingDataFromTheAssets();
        mPlayer = new MediaPlayer();
        mLrcView=(LrcView)findViewById(R.id.lrcView);
        //file:///android_asset/test.lrc;

        audioAndText(fileValues);
        Log.e("postion ",""+fileValues);
    }

    private void gettingDataFromTheAssets() {
        sampleArrayList=new ArrayList<>();
        sample=new AudioData();
        sample.setFileName("test.lrc");
        sample.setSongName("m.mp3");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sample.setImgName(R.drawable.image);
        }
        sampleArrayList.add(sample);
        sample1=new AudioData();
        sample1.setFileName("ftest.lrc");
        sample1.setSongName("f.mp3");
        sample1.setImgName(R.drawable.where_is_the_ball);
        sampleArrayList.add(sample1);
        sample2=new AudioData();
        sample2.setFileName("gtest.lrc");
        sample2.setSongName("g.mp3");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            sample2.setImgName(R.drawable.mummy_do_cry);
        }
        sampleArrayList.add(sample2);
    }

    private void audioAndText(int i) {
        String lrc = getFromAssets(""+sampleArrayList.get(i).getFileName());
        Log.d(TAG, "lrc:" + lrc);
        mImgAudioPic.setImageResource(sampleArrayList.get(i).getImgName());
        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrc);

        mLrcView.setLrc(rows);
        beginLrcPlay(i);

        mLrcView.setListener(new ILrcView.LrcViewListener() {

            public void onLrcSeeked(int newPosition, LrcRow row) {
                if (mPlayer != null) {
                    Log.d(TAG, "onLrcSeeked: " + row.time);
                    mPlayer.seekTo((int)row.time);
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    protected void onDestroy() {
    	super.onDestroy();
    	if (mPlayer != null) {
    		mPlayer.stop();
    	}
    }



    public void beginLrcPlay(int position){

    	try {
            AssetFileDescriptor FileDescriptor = getAssets().openFd(sampleArrayList.get(position).getSongName());

            mPlayer.setDataSource(FileDescriptor.getFileDescriptor(),
                    FileDescriptor.getStartOffset(),
                    FileDescriptor.getLength());

            FileDescriptor.close();

            if(mValues == 1) {
                mPlayer.prepare();
                mPlayer.start();
            }
            mPlayer.setOnPreparedListener(new OnPreparedListener() {

				public void onPrepared(MediaPlayer mp) {
					Log.d(TAG, "onPrepared");
					mp.start();
			        if(mTimer == null){
			        	mTimer = new Timer();
			        	mTask = new LrcTask();
			        	mTimer.scheduleAtFixedRate(mTask, 0, mPalyTimerDuration);
			        }
				}
			});

    	} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

    }

    public void stopLrcPlay(){
        if(mTimer != null){
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onClick(View v) {
        mLrcView.seekLrcToTime(0);
        switch (v.getId()){
            case R.id.tv_next:
                if(sampleArrayList.size()-1!=fileValues) {
                    ++fileValues;
                    operationalData(fileValues);
                }
                break;
            case R.id.tv_previous:
                if(fileValues>0) {
                    --fileValues;
                    operationalData(fileValues);
                }
                break;
            case R.id.tv_pause:
                if(mPlayer.isPlaying())
                mPlayer.pause();
                else
                    mPlayer.start();
                break;
        }
    }

    private void operationalData(int fileval) {
        mLrcView.seekLrcToTime(0);
        stopLrcPlay();
        mPlayer.reset();
        audioAndText(fileval);
    }

    class LrcTask extends TimerTask {
        long beginTime = -1;
        @Override
        public void run() {
            if(beginTime == -1) {
                beginTime = System.currentTimeMillis();
            }
            final long timePassed = mPlayer.getCurrentPosition();
            MainActivity.this.runOnUiThread(new Runnable() {

                public void run() {
                    mLrcView.seekLrcToTime(timePassed);
                }
            });
        }
    };
}
