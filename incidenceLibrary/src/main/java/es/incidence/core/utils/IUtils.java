package es.incidence.core.utils;

import android.app.Activity;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.WindowManager;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class IUtils
{
    public static boolean isFormattedPhone(String rawPhone) {
        String[] separate = rawPhone.split(" ");
        if (separate == null || separate.length == 0) {
            return true;
        }

        if (separate.length == 1) {
            if (separate[0].length() <= 3) {
                return true;
            } else {
                return false;
            }
        }

        if (separate.length == 2) {
            if (separate[0].length() == 3 && separate[1].length() <= 3) {
                return true;
            } else {
                return false;
            }
        }

        if (separate.length >= 3) {
            if (separate[0].length() == 3 && separate[1].length() == 3) {
                return true;
            } else {
                return false;
            }
        }

        return true;
    }

    public static String formatPhoneNumber(String rawPhone) {
        rawPhone = rawPhone.replaceAll(" ", "");
        String phoneFormat = "";
        if (rawPhone.length() > 3) {
            phoneFormat += rawPhone.substring(0, 3);
            rawPhone = rawPhone.substring(3);
        } else {
            return rawPhone;
        }
        phoneFormat += " ";
        if (rawPhone.length() > 3) {
            phoneFormat += rawPhone.substring(0, 3);
            rawPhone = rawPhone.substring(3);
        } else {
            phoneFormat += rawPhone;
            return phoneFormat;
        }

        phoneFormat += " " + rawPhone;

        return phoneFormat;
    }

    public static String getLocationCountry(Context context, Location location)
    {
        String res = null;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address obj = addresses.get(0);

            String country = obj.getCountryName();
            String countryCode = obj.getCountryCode();
            //Log.e("IUtils", "country: " + country);
            //Log.e("IUtils", "countryCode: " + countryCode);
            res = country;
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    public static boolean isLocationInSpain(Context context, Location location)
    {
        boolean res = false;

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            Address obj = addresses.get(0);

            String countryCode = obj.getCountryCode();
            if (countryCode != null && countryCode.equalsIgnoreCase("ES"))
            {
                res = true;
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return res;
    }

    public static void keepScreenOn(Activity activity){
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void keepScreenOff(Activity activity){
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public static void vibrate(Context context) {
        Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }
}
