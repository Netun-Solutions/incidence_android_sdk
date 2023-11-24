package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class TutorialVideo implements Parcelable
{
    public String title;
    public String titleShort;
    public String url;
    public String img;
    public String code;

    protected TutorialVideo(Parcel in) {
        title = in.readString();
        titleShort = in.readString();
        url = in.readString();
        img = in.readString();
        code = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(titleShort);
        dest.writeString(url);
        dest.writeString(img);
        dest.writeString(code);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TutorialVideo> CREATOR = new Creator<TutorialVideo>() {
        @Override
        public TutorialVideo createFromParcel(Parcel in) {
            return new TutorialVideo(in);
        }

        @Override
        public TutorialVideo[] newArray(int size) {
            return new TutorialVideo[size];
        }
    };
}
