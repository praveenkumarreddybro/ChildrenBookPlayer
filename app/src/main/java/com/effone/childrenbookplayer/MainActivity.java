package com.effone.childrenbookplayer;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.effone.childrenbookplayer.view.DefaultLrcBuilder;
import com.effone.childrenbookplayer.view.ILrcBuilder;
import com.effone.childrenbookplayer.view.ILrcView;
import com.effone.childrenbookplayer.view.LrcRow;
import com.effone.childrenbookplayer.view.LrcView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;



public class MainActivity extends Activity {

	public final static String TAG = "MainActivity";
	ILrcView mLrcView;
    private int mPalyTimerDuration = 1000;
    private Timer mTimer;
    private int mValues;
    private TimerTask mTask;

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
     //   mLrcView = new LrcView(this, null);
        Intent intent = getIntent();
        mValues = intent.getIntExtra("value",0);
        setContentView(R.layout.activity_main);
        mLrcView=(LrcView)findViewById(R.id.lrcView);
        //file:///android_asset/test.lrc;
        String lrc = getFromAssets("test.lrc");
        Log.d(TAG, "lrc:" + lrc);

        ILrcBuilder builder = new DefaultLrcBuilder();
        List<LrcRow> rows = builder.getLrcRows(lrc);

        mLrcView.setLrc(rows);
        beginLrcPlay();

        mLrcView.setListener(new ILrcView.LrcViewListener() {

			public void onLrcSeeked(int newPosition, LrcRow row) {
				if (mPlayer != null) {
					Log.d(TAG, "onLrcSeeked:" + row.time);
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


    MediaPlayer mPlayer;
    public void beginLrcPlay(){
    	mPlayer = new MediaPlayer();
    	try {
    		mPlayer.setDataSource(getAssets().openFd("m.mp3").getFileDescriptor());
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
            if(mValues == 1) {
                mPlayer.prepare();
                mPlayer.start();

            }
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
