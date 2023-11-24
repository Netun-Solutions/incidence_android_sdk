package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Incidence implements Parcelable
{
    public Integer id;
    public String externalIncidenceId;
    public String street;
    public String city;
    public String country;
    public String dateCreated;
    public Double latitude;
    public Double longitude;
    public IncidenceType incidenceType;
    public OpenApp openApp;
    public int androidAuto;

    //0=Pending; 1=Assigned; 2=On route; 3=In destination; 4=Closed
    public int status;

    public ArrayList<Notification> notifications;

    public Integer rate;
    public Integer asitur;
    public Integer reporter;

    public Incidence(){}


    protected Incidence(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        externalIncidenceId = in.readString();
        street = in.readString();
        city = in.readString();
        country = in.readString();
        dateCreated = in.readString();
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
        incidenceType = in.readParcelable(IncidenceType.class.getClassLoader());
        openApp = in.readParcelable(OpenApp.class.getClassLoader());
        androidAuto = in.readInt();
        status = in.readInt();
        if (in.readByte() == 0) {
            rate = null;
        } else {
            rate = in.readInt();
        }
        if (in.readByte() == 0) {
            asitur = null;
        } else {
            asitur = in.readInt();
        }
        if (in.readByte() == 0) {
            reporter = null;
        } else {
            reporter = in.readInt();
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
        dest.writeString(externalIncidenceId);
        dest.writeString(street);
        dest.writeString(city);
        dest.writeString(country);
        dest.writeString(dateCreated);
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
        dest.writeParcelable(incidenceType, flags);
        dest.writeParcelable(openApp, flags);
        dest.writeInt(androidAuto);
        dest.writeInt(status);
        if (rate == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(rate);
        }
        if (asitur == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(asitur);
        }
        if (reporter == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(reporter);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Incidence> CREATOR = new Creator<Incidence>() {
        @Override
        public Incidence createFromParcel(Parcel in) {
            return new Incidence(in);
        }

        @Override
        public Incidence[] newArray(int size) {
            return new Incidence[size];
        }
    };

    public boolean isCanceled()
    {
        boolean res = false;

        if (status == 5)
        {
            res = true;
        }

        return res;
    }

    public void cancel()
    {
        status = 5;
    }

    public boolean isClosed()
    {
        boolean res = false;

        if (status == 4)
        {
            res = true;
        }

        return res;
    }

    public void close()
    {
        status = 4;
    }

    public String getTitle()
    {
        String res = "";

        if (incidenceType != null && incidenceType.name != null)
        {
            res = incidenceType.name;
        }

        return res;
    }
}
