package com.cg.lrceditor;

import java.io.Serializable;

public class LyricItem implements Serializable {
    public boolean isSelected = false;
    private Timestamp timestamp;
    private String lyric;

    public LyricItem(String lyric, Timestamp timestamp) {
        this.lyric = lyric;
        this.timestamp = timestamp;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public String getLyric() {
        return lyric;
    }

    public void setLyric(String lyric) {
        this.lyric = lyric;
    }
}