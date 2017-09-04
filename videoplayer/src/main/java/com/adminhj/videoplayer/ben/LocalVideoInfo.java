package com.adminhj.videoplayer.ben;

import java.io.Serializable;

/**
 * Created by AdminHeJun on 2017/2/22.
 * </p>
 * Content:this is video info ben
 * </p>
 * Modified:
 * </p>
 * Version:
 * </p>
 */

public class LocalVideoInfo implements Serializable {

    /**
     * this is video name
     */
    private String name = null;
    /**
     *  video artist
     */
    private String artist = null;

    /**
     * this is video duration
     */
    private Long duration = null;
    /**
     * this is video size
     */
    private Long size = null;
    /**
     * this is video store path
     */
    private String data = null;

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "LocalMusicInfo{" +
                "name='" + name + '\'' +
                ", artist='" + artist + '\'' +
                ", duration=" + duration +
                ", size=" + size +
                ", data='" + data + '\'' +
                '}';
    }
}
