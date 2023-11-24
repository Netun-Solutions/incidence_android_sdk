package com.e510.commons.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.incidencelibrary.R;
import com.e510.commons.domain.Device;
import com.e510.commons.utils.DeviceUtils;
import com.e510.commons.utils.ShareUtils;
import com.e510.commons.utils.config.AppConfiguration;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static com.e510.commons.utils.LogUtil.makeLogTag;

public class ShareFragment extends BaseFragment {
    private static final String TAG = makeLogTag(ShareFragment.class);

    private static final String KEY_EXTRA = "KEY_EXTRA";
    private String stringShare;

    private static final String KEY_IS_EMAIL = "KEY_IS_EMAIL";
    private boolean isEmail;

    public static ShareFragment newEmailInstance(String emailShare){
        ShareFragment fragment = new ShareFragment();
        fragment.stringShare = emailShare;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA, emailShare);
        bundle.putBoolean(KEY_IS_EMAIL, true);
        return fragment;
    }

    public static ShareFragment newInstance(String stringShare){
        ShareFragment fragment = new ShareFragment();
        fragment.stringShare = stringShare;
        Bundle bundle = new Bundle();
        bundle.putString(KEY_EXTRA, stringShare);
        return fragment;
    }

    @Override
    public int getTitleId() {
        return R.string.share_title;
    }

    public String getStringShare() {
        return stringShare;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            isEmail = getArguments().getBoolean(KEY_IS_EMAIL);
            stringShare = getArguments().getString(KEY_EXTRA);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.fragment_share, container, false);
        setupUI(view);

        return  view;
    }

    @Override
    public void loadData()
    {
        String str = stringShare;
        if (str != null) {
            if (isEmail) {
                ShareUtils.shareEmail(getActivity(), str);
            } else {
                ShareUtils.shareText(getActivity(), str);
            }

        }
    }
}
