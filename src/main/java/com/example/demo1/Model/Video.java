package com.example.demo1.Model;

//=======video==========
public class Video extends Media {
    private int quality;
    private String format;
    private int duration;

    public Video(String filePath, int quality, String format, int duration) {
        super(filePath, MediaType.VIDEO);
        this.quality = quality;
        this.format = format;
        this.duration = duration;
    }

    public int getQuality() {
        return quality;
    }

    public String getFormat() {
        return format;
    }

    public int getDuration() {
        return duration;
    }

    @Override
    public String getMediaInfo() {
        return "Video (" + quality + "-" + format + "-" + duration + ")-" + filePath;
    }
}

