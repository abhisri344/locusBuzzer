package com.myapp.locusbuzzer;

import android.os.Parcel;
import android.os.Parcelable;

public class Task_Model implements Parcelable {
    private int id;
    private String title;
    private String desc;
    private int range;
    private String latitude;
    private String longitude;
    private String isActive;
    private String distance;

    public Task_Model(int id, String title, int range, String desc, String latitude, String longitude, String isActive, String distance) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.range = range;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isActive = isActive;
        this.distance = distance;
    }

    protected Task_Model(Parcel in) {
        id = in.readInt();
        title = in.readString();
        desc = in.readString();
        range = in.readInt();
        latitude = in.readString();
        longitude = in.readString();
        isActive = in.readString();
        distance = in.readString();
    }

//    @Override
//    public void writeToParcel(Parcel dest, int flags) {
//        dest.writeInt(id);
//        dest.writeString(title);
//        dest.writeString(desc);
//        dest.writeInt(range);
//        dest.writeString(latitude);
//        dest.writeString(longitude);
//        dest.writeString(isActive);
//        dest.writeString(distance);
//    }
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    public static final Creator<Task_Model> CREATOR = new Creator<Task_Model>() {
//        @Override
//        public Task_Model createFromParcel(Parcel in) {
//            return new Task_Model(in);
//        }
//
//        @Override
//        public Task_Model[] newArray(int size) {
//            return new Task_Model[size];
//        }
//    };


    public static final Creator<Task_Model> CREATOR = new Creator<Task_Model>() {
        @Override
        public Task_Model createFromParcel(Parcel in) {
            return new Task_Model(in);
        }

        @Override
        public Task_Model[] newArray(int size) {
            return new Task_Model[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getRange() {
        return range;
    }

    public void setRange(int range) {
        this.range = range;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getIsActive() {
        return isActive;
    }

    public void setIsActive(String isActive) {
        this.isActive = isActive;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "Task_Model{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", desc='" + desc + '\'' +
                ", range=" + range +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", isActive='" + isActive + '\'' +
                ", distance='" + distance + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(title);
        parcel.writeString(desc);
        parcel.writeInt(range);
        parcel.writeString(latitude);
        parcel.writeString(longitude);
        parcel.writeString(isActive);
        parcel.writeString(distance);
    }
}
