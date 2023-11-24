package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class VehicleType implements Parcelable {
    public int id;
    public String name;
    public ArrayList<ColorType> colors;

    public VehicleType() { }

    public VehicleType(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VehicleType> CREATOR = new Creator<VehicleType>() {
        @Override
        public VehicleType createFromParcel(Parcel in) {
            return new VehicleType(in);
        }

        @Override
        public VehicleType[] newArray(int size) {
            return new VehicleType[size];
        }
    };
}
