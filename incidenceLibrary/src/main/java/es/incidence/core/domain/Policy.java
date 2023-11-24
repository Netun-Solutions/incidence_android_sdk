package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class Policy implements Parcelable {

    public String id;
    public String policyNumber;
    public IdentityType identityType;
    public String dni;
    public String policyStart;
    public String policyEnd;


    public Policy() {}


    protected Policy(Parcel in) {
        id = in.readString();
        policyNumber = in.readString();
        identityType = in.readParcelable(IdentityType.class.getClassLoader());
        dni = in.readString();
        policyStart = in.readString();
        policyEnd = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(policyNumber);
        dest.writeParcelable(identityType, flags);
        dest.writeString(dni);
        dest.writeString(policyStart);
        dest.writeString(policyEnd);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Policy> CREATOR = new Creator<Policy>() {
        @Override
        public Policy createFromParcel(Parcel in) {
            return new Policy(in);
        }

        @Override
        public Policy[] newArray(int size) {
            return new Policy[size];
        }
    };
}
