package com.e510.location;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.text.TextUtils;

import androidx.core.app.ActivityCompat;

import com.e510.incidencelibrary.R;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

import es.incidence.core.utils.IUtils;

public class LocationManager
{
    private static final String TAG = "LocationMgr";

    public static boolean hasPermission(Context context)
    {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(int code, Activity activity)
    {
        if (!hasPermission(activity))
        {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, code);
        }
    }

    public static void showPermissionRequiredDialog(final int code, final Activity activity, final DialogInterface.OnClickListener askListener)
    {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.dismiss();

                if (askListener != null)
                {
                    askListener.onClick(dialog, which);
                }
                else
                {
                    switch (which)
                    {
                        case DialogInterface.BUTTON_POSITIVE:
                            openSettingsPermissions(code, activity);
                            break;
                    }
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder
                .setTitle(IUtils.getAppLable(activity))
                .setMessage(activity.getString(R.string.incidence_key_required_location_permission_text))
                .setPositiveButton(activity.getString(R.string.incidence_key_required_location_ok_text), dialogClickListener)
                .setNegativeButton(activity.getString(R.string.incidence_key_required_location_cancel_text), dialogClickListener)
                .show();
    }

    public static void openSettingsPermissions(final int code, final Activity activity)
    {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            // Checking Permissions again
            requestPermission(code, activity);
        }
        else
        {
            // Open App's Settings Page for user to grand permission manually
            final Intent i = new Intent();
            i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.addCategory(Intent.CATEGORY_DEFAULT);
            i.setData(Uri.parse("package:" + activity.getPackageName()));
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
            activity.startActivity(i);
        }
    }

    public static boolean hasBackgroundPermission(Context context)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED;

        }

        return hasPermission(context);
    }

    public static void requestPermissionWithBackground(int code, Activity activity)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
        {
            if (!hasBackgroundPermission(activity))
            {
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION}, code);
            }
        }
        else
        {
            requestPermission(code, activity);
        }
    }

    public static boolean isLocationEnabled(Context context) {

        int locationMode;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);

            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        } else {
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }
    }

    public static void getLocation(Context context, final LocationListener listener)
    {
        if (hasPermission(context))
        {
            obtenerLocation(context, new LocationCallback(){
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (listener != null)
                    {
                        listener.onLocationResult(locationResult.getLastLocation());
                    }
                }
            });
        }
        else
        {
            if (listener != null)
            {
                listener.onLocationResult(null);
            }
        }
    }

    public static void getUltimaLocation(Context context, final LocationListener listener)
    {
        if (hasPermission(context))
        {
            obtenerUltimaLocation(context, listener);
        }
        else
        {
            if (listener != null)
            {
                listener.onLocationResult(null);
            }
        }
    }

    @SuppressLint("MissingPermission")
    private static void obtenerLocation(Context context, final LocationCallback callback)
    {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);

        LocationRequest request = LocationRequest.create();
        request.setInterval(10000); // Tiempo deseado entre actualizaciones
        request.setFastestInterval(5000); //Tiempo más rápiudo posible entre actualizaciones. Estamos diciendo que no aceptamos intervalos más cortos
        //request.setMaxWaitTime(10000);
        //request.setSmallestDisplacement(1);
        request.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        request.setNumUpdates(1);

        client.requestLocationUpdates(request, callback, null);
    }

    @SuppressLint("MissingPermission")
    private static void obtenerUltimaLocation(Context context, final LocationListener listener)
    {
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        client.getLastLocation().addOnSuccessListener(location -> {
            if (listener != null)
            {
                listener.onLocationResult(location);
            }
        });
    }

    public interface LocationListener
    {
        void onLocationResult(Location location);
    }
}
