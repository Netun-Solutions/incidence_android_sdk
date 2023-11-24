package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class IncidenceType implements Parcelable {
    public int id;
    public String externalId;
    public int parent;
    public String name;

    public IncidenceType() {}
    protected IncidenceType(Parcel in) {
        id = in.readInt();
        externalId = in.readString();
        parent = in.readInt();
        name = in.readString();
    }

    public static final Creator<IncidenceType> CREATOR = new Creator<IncidenceType>() {
        @Override
        public IncidenceType createFromParcel(Parcel in) {
            return new IncidenceType(in);
        }

        @Override
        public IncidenceType[] newArray(int size) {
            return new IncidenceType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(externalId);
        dest.writeInt(parent);
        dest.writeString(name);
    }
}
