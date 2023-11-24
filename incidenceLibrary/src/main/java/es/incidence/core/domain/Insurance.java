package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Insurance implements Parcelable
{
    public String id;
    public String name;
    public int relation; //1 Destacada - 0 no destacada.
    public String phone;
    public String internationaPhone;
    public String image;
    public String svg;
    public String textIncidence;
    public String textIncidenceAndroid;

    public Insurance() {}

    protected Insurance(Parcel in) {
        id = in.readString();
        name = in.readString();
        relation = in.readInt();
        phone = in.readString();
        internationaPhone = in.readString();
        image = in.readString();
        svg = in.readString();
        textIncidence = in.readString();
        textIncidenceAndroid = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeInt(relation);
        dest.writeString(phone);
        dest.writeString(internationaPhone);
        dest.writeString(image);
        dest.writeString(svg);
        dest.writeString(textIncidence);
        dest.writeString(textIncidenceAndroid);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Insurance> CREATOR = new Creator<Insurance>() {
        @Override
        public Insurance createFromParcel(Parcel in) {
            return new Insurance(in);
        }

        @Override
        public Insurance[] newArray(int size) {
            return new Insurance[size];
        }
    };
}
