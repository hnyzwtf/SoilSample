package com.soil.profile.model;

/**
 * Created by GIS on 2016/6/28 0028.
 */
public class KnowledgeItem {
    private int imageId;
    private String title;
    private String detail;

    public KnowledgeItem(int imageId, String title, String detail) {
        this.imageId = imageId;
        this.title = title;
        this.detail = detail;
    }

    public int getImageId() {
        return imageId;
    }
    public void setImageId(int imageId) {
        this.imageId = imageId;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDetail() {
        return detail;
    }
    public void setDetail(String detail) {
        this.detail = detail;
    }
}
