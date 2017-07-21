 package com.effone.childrenbookplayer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

 public class FlashScreen extends AppCompatActivity implements View.OnClickListener {
    private TextView mTvReadItOn,mTvMyRead;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flash_screen);
        mTvReadItOn=(TextView)findViewById(R.id.read_iteself);
        mTvMyRead=(TextView)findViewById(R.id.read_it);
        mTvMyRead.setOnClickListener(this);
        mTvReadItOn.setOnClickListener(this);
    }

     @Override
     public void onClick(View v) {
         switch (v.getId()){
             case R.id.read_iteself:
                 Intent intenst=new Intent(this,MainActivity.class);
                 intenst.putExtra("value", 1); /// one stand for to play song
                 startActivity(intenst);
                 break;
             case R.id.read_it:
                 Intent intent=new Intent(this,MainActivity.class);
                 intent.putExtra("value", 0);/// 0 stand for to no songs dude
                 startActivity(intent);
                 break;
         }
     }
 }
