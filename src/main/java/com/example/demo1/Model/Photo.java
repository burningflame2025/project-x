package com.example.demo1.Model;

//===========photo========
public class Photo extends Media {
    private String format;

    public Photo(String filePath, String format) {
        super(filePath, MediaType.PHOTO);
        this.format = format;
    }

    public String getFormat() {
        return format;
    }

    @Override
    public String getMediaInfo() {
        return "Photo (" + format + ") - " + filePath;
    }
}
