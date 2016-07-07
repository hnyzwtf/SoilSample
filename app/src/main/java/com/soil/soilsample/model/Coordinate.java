package com.soil.soilsample.model;

import android.os.Parcel;
import android.os.Parcelable;

/*
 *
 * coordinate parsed from kml file
 * */
public class Coordinate implements Parcelable {
	private double x;
	private double y;
	private int iconId;
	private String name;
	private String htmlContent;

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
	public String getHtmlContent() {
		return htmlContent;
	}
	public void setHtmlContent(String htmlContent) {
		this.htmlContent = htmlContent;
	}
	public int getIconId() {
		return iconId;
	}

	public void setIconId(int iconId) {
		this.iconId = iconId;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(x);
		dest.writeDouble(y);
		dest.writeInt(iconId);
		dest.writeString(name);
		dest.writeString(htmlContent);
	}
	public static final Creator<Coordinate> CREATOR = new Creator<Coordinate>()
	{

		@Override
		public Coordinate createFromParcel(Parcel source) {
			Coordinate coor = new Coordinate();
			coor.x = source.readDouble();
			coor.y = source.readDouble();
			coor.iconId = source.readInt();
			coor.name = source.readString();
			coor.htmlContent = source.readString();
			return coor;
		}

		@Override
		public Coordinate[] newArray(int size) {
			return new Coordinate[size];
		}
	};
}
