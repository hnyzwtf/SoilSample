package com.soil.profile.model.DatabaseMdl;

import java.io.Serializable;

public class InfoGeo implements Serializable{
    private static final long serialVersionUID = -7236536618463045888L;
    private String imagePath;
    private double latitude;
    private double longtitude;
    private String position;

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

}

