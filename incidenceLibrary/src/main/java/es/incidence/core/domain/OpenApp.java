package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class OpenApp implements Parcelable {

    public String androidPackage;
    public String androidDeeplink;
    public String androidGooglePlayURL;
    public String androidIntent;
    public String iosUniversalLink;

    protected OpenApp(Parcel in) {
        androidPackage = in.readString();
        androidDeeplink = in.readString();
        androidGooglePlayURL = in.readString();
        androidIntent = in.readString();
        iosUniversalLink = in.readString();
    }

    public static final Creator<OpenApp> CREATOR = new Creator<OpenApp>() {
        @Override
        public OpenApp createFromParcel(Parcel in) {
            return new OpenApp(in);
        }

        @Override
        public OpenApp[] newArray(int size) {
            return new OpenApp[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(androidPackage);
        parcel.writeString(androidDeeplink);
        parcel.writeString(androidGooglePlayURL);
        parcel.writeString(androidIntent);
        parcel.writeString(iosUniversalLink);
    }
}
