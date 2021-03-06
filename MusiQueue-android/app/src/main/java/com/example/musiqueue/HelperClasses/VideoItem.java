package com.example.musiqueue.HelperClasses;

/**
 * Created by Brian on 1/27/2017.
 */
public class VideoItem {
    private String title;
    private String description;
    private String thumbnailURL;
    private String id;

    public VideoItem(String title, String description, String id) {
        this.title = title;
        this.description = description;
        this.id = id;
    }

    public VideoItem() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnail) {
        this.thumbnailURL = thumbnail;
    }

}
