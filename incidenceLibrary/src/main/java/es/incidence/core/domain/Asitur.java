package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Asitur implements Parcelable {

    public int finish;
    public Double latitude;
    public Double longitude;

    protected Asitur(Parcel in) {
        finish = in.readInt();
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitude = null;
        } else {
            longitude = in.readDouble();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(finish);
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitude);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Asitur> CREATOR = new Creator<Asitur>() {
        @Override
        public Asitur createFromParcel(Parcel in) {
            return new Asitur(in);
        }

        @Override
        public Asitur[] newArray(int size) {
            return new Asitur[size];
        }
    };
}
