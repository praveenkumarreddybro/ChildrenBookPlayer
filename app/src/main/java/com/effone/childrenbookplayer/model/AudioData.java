package com.effone.childrenbookplayer.model;

import android.graphics.drawable.Drawable;

/**
 * Created by sumanth.peddinti on 7/25/2017.
 */

public class AudioData {
    private String fileName;
    private String songName;
    private int imgName;

    public int getImgName() {
        return imgName;
    }

    public void setImgName(int imgName) {
        this.imgName = imgName;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }
}
