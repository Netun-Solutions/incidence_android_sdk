package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Driver implements Parcelable
{
    public Integer id;
    public String name;
    public Integer type; // 1 primario, 0 secundario.

    public Driver(){}

    protected Driver(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        name = in.readString();
        if (in.readByte() == 0) {
            type = null;
        } else {
            type = in.readInt();
        }
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
        if (type == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(type);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Driver> CREATOR = new Creator<Driver>() {
        @Override
        public Driver createFromParcel(Parcel in) {
            return new Driver(in);
        }

        @Override
        public Driver[] newArray(int size) {
            return new Driver[size];
        }
    };

    public boolean isTypePrimary()
    {
        boolean res = false;

        if (type != null && type == 1)
        {
            res = true;
        }

        return res;
    }
}
