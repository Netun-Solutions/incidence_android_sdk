package com.e510.commons.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.provider.Settings;

import com.e510.commons.domain.Device;

import java.util.Locale;

public class DeviceUtils {
    public static Device getDevice(Context context) {
        Device d = new Device();
        //final String deviceId = Installation.id(context);
        //d.setDeviceId(deviceId);
        d.setDeviceId(getAndroidID(context));
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            d.setAppVersion(pInfo.versionName);
            d.setBuild(pInfo.versionCode + "");
            d.setPackageName(pInfo.packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        d.setLocale(getLocale());
        d.setPlatform("android");
        d.setOs(Build.VERSION.SDK_INT + "");
        d.setDevice(Build.MANUFACTURER + "(" + Build.MODEL + ")");
        //d.setPushId(Prefs.getPushTokenConfirmed());

        return d;
    }

    public static String getLocale()
    {
        return Locale.getDefault().getLanguage();
    }

    public static String getAndroidID(Context context)
    {
        String res = "";

        try
        {
            String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            if (androidId != null )
            {
                res = androidId;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }
}
