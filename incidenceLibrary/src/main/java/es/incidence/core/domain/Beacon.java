package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Beacon implements Parcelable
{
    public Integer id;
    public BeaconType beaconType;
    public String name;
    public Vehicle vehicle;

    //beacon properties
    public String uuid;
    public String major;
    public String minor;
    public String proximity;
    public int rssi;
    public int tx;
    public double accuracy;
    public String imei;

    //IoT
    public String iot;


    public Beacon(){}

    protected Beacon(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        vehicle = in.readParcelable(Vehicle.class.getClassLoader());
        uuid = in.readString();
        major = in.readString();
        minor = in.readString();
        proximity = in.readString();
        rssi = in.readInt();
        tx = in.readInt();
        accuracy = in.readDouble();
        iot = in.readString();
        imei = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(name);
        dest.writeParcelable(vehicle, flags);
        dest.writeString(uuid);
        dest.writeString(major);
        dest.writeString(minor);
        dest.writeString(proximity);
        dest.writeInt(rssi);
        dest.writeInt(tx);
        dest.writeDouble(accuracy);
        dest.writeString(iot);
        dest.writeString(imei);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Beacon> CREATOR = new Creator<Beacon>() {
        @Override
        public Beacon createFromParcel(Parcel in) {
            return new Beacon(in);
        }

        @Override
        public Beacon[] newArray(int size) {
            return new Beacon[size];
        }
    };

    public String getId()
    {
        if (iot != null && iot.length() > 0)
        {
            return iot;
        }
        else if (uuid != null && major != null && minor != null)
        {
            return uuid + "-" + major + "-" + minor;
        }

        return null;
    }
}
