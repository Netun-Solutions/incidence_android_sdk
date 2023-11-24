package es.incidence.core.domain;

import android.os.Parcel;
import android.os.Parcelable;

public class EcommerceItem implements Parcelable {

    public String title;
    public String text;
    public String image;
    public String title_button;
    public String link;
    public String price;
    public String offer_price;


    protected EcommerceItem(Parcel in) {
        title = in.readString();
        text = in.readString();
        image = in.readString();
        title_button = in.readString();
        link = in.readString();
        price = in.readString();
        offer_price = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(text);
        dest.writeString(image);
        dest.writeString(title_button);
        dest.writeString(link);
        dest.writeString(price);
        dest.writeString(offer_price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<EcommerceItem> CREATOR = new Creator<EcommerceItem>() {
        @Override
        public EcommerceItem createFromParcel(Parcel in) {
            return new EcommerceItem(in);
        }

        @Override
        public EcommerceItem[] newArray(int size) {
            return new EcommerceItem[size];
        }
    };
}
