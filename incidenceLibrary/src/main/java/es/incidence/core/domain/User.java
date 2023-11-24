package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable
{
    public String id;
    public String externalUserId;
    public String alias;
    public String name;
    public String phone;
    public String email;
    public IdentityType identityType;
    public String dni;
    public String birthday;
    public String checkTerms;
    public String checkAdvertising;

    public User() {}


    protected User(Parcel in) {
        id = in.readString();
        externalUserId = in.readString();
        alias = in.readString();
        name = in.readString();
        phone = in.readString();
        email = in.readString();
        identityType = in.readParcelable(IdentityType.class.getClassLoader());
        dni = in.readString();
        birthday = in.readString();
        checkTerms = in.readString();
        checkAdvertising = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(externalUserId);
        dest.writeString(alias);
        dest.writeString(name);
        dest.writeString(phone);
        dest.writeString(email);
        dest.writeParcelable(identityType, flags);
        dest.writeString(dni);
        dest.writeString(birthday);
        dest.writeString(checkTerms);
        dest.writeString(checkAdvertising);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
