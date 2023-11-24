package com.e510.commons.fragment;

import android.os.Parcelable;

import java.io.Serializable;


public interface BaseFragmentListener extends Parcelable, Serializable {
    void onPerformBackPressed(Object object);
    void onClickRow(Object object);
    void onSwipeRefresh();
    void onBecomeFrontFromBackPressed();
}
