package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class IdentityType implements Parcelable
{
    public int id;
    public String name;

    public IdentityType() { }

    public IdentityType(Parcel in) {
        id = in.readInt();
        name = in.readString();
    }

    public static final Creator<IdentityType> CREATOR = new Creator<IdentityType>() {
        @Override
        public IdentityType createFromParcel(Parcel in) {
            return new IdentityType(in);
        }

        @Override
        public IdentityType[] newArray(int size) {
            return new IdentityType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }
}
