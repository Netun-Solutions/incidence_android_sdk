package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class ColorType implements Parcelable {
    public int id;
    public String name;
    public String color;
    public String image;

    public ColorType() {}

    public ColorType(Parcel in) {
        id = in.readInt();
        name = in.readString();
        color = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeString(color);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ColorType> CREATOR = new Creator<ColorType>() {
        @Override
        public ColorType createFromParcel(Parcel in) {
            return new ColorType(in);
        }

        @Override
        public ColorType[] newArray(int size) {
            return new ColorType[size];
        }
    };
}
