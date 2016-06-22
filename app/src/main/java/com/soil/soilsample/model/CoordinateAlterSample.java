package com.soil.soilsample.model;


import android.os.Parcel;
import android.os.Parcelable;

/*
 * 服务器返回的可替代样点，每一个样点包括一个name,x,y,costValue
 * */
public class CoordinateAlterSample implements Parcelable {
    private double x;
    private double y;
    private String name;
    private String costValue;

    public String getCostValue() {
        return costValue;
    }

    public void setCostValue(String costValue) {
        this.costValue = costValue;
    }

    public double getX() {
        return x;
    }
    public void setX(double x) {
        this.x = x;
    }
    public double getY() {
        return y;
    }
    public void setY(double y) {
        this.y = y;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeString(name);
        dest.writeString(costValue);
    }
    public static final Creator<CoordinateAlterSample> CREATOR = new Creator<CoordinateAlterSample>()
    {

        @Override
        public CoordinateAlterSample createFromParcel(Parcel source) {
            CoordinateAlterSample coor = new CoordinateAlterSample();
            coor.x = source.readDouble();
            coor.y = source.readDouble();
            coor.name = source.readString();
            coor.costValue = source.readString();
            return coor;
        }

        @Override
        public CoordinateAlterSample[] newArray(int size) {
            return new CoordinateAlterSample[size];
        }
    };
}