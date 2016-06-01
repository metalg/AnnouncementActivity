package com.gli.announcementcontroller;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by german on 5/28/16.
 */
public class TrackInfo implements Parcelable {

    private int track;
    private String language;

    public TrackInfo() {
        track =0;
        language="EN";
    }


    protected TrackInfo(Parcel in) {
        readFromParcel(in);
    }

    public TrackInfo(int track, String language) {
        this.track = track;
        this.language = language;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(track);
        dest.writeString(language);

    }

    public void readFromParcel(Parcel in) {
        track = in.readInt();
        language = in.readString();
    }

    @Override
    public String toString() {
        return "TrackInfo->" +this.track + " "+ this.language;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TrackInfo> CREATOR = new Creator<TrackInfo>() {
        @Override
        public TrackInfo createFromParcel(Parcel in) {
            return new TrackInfo(in);
        }

        @Override
        public TrackInfo[] newArray(int size) {
            return new TrackInfo[size];
        }
    };
}
