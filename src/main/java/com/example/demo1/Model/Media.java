package com.example.demo1.Model;

//=======media======
public abstract class Media {
    protected String filePath;
    protected MediaType type;

    public Media(String filePath, MediaType type) {
        this.filePath = filePath;
        this.type = type;
    }

    public String getFilePath() {
        return filePath;
    }

    public MediaType getType() {
        return type;
    }

    public abstract String getMediaInfo();
}
