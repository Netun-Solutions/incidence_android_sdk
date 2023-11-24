package com.e510.commons.utils.banner;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import static com.e510.commons.utils.LogUtil.makeLogTag;

public class VXNotificationManager
{
    private static final String TAG = makeLogTag(VXNotificationManager.class);

    public static void showPush(Application app, Intent intent, String title, String messageBody, String nameChannel, String idChannel, String descriptionChannel, int icon, int idNotification)
    {
        try
        {
            android.app.NotificationManager notificationManager = (android.app.NotificationManager) app.getSystemService(Context.NOTIFICATION_SERVICE);



            PendingIntent pendingIntent = PendingIntent.getActivity(app, 0 /* Request code */, intent,
                    PendingIntent.FLAG_ONE_SHOT);

            NotificationCompat.Builder builder;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                int importance = android.app.NotificationManager.IMPORTANCE_HIGH;

                NotificationChannel mChannel = notificationManager.getNotificationChannel(idChannel);

                if (mChannel == null) {
                    mChannel = new NotificationChannel(idChannel, nameChannel, importance);
                    mChannel.setDescription(descriptionChannel);
                    mChannel.enableVibration(true);
                    mChannel.enableLights(true);
                    mChannel.setLightColor(Color.RED);
                    mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                    notificationManager.createNotificationChannel(mChannel);
                }

                builder = new NotificationCompat.Builder(app, idChannel);

                builder.setContentTitle(title)       // required
                        .setSmallIcon(icon) // required
                        .setContentText(messageBody)  // required
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker(messageBody + " Ticker")
                        .setLights(Color.RED, 500, 500)
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

            } else {

                builder = new NotificationCompat.Builder(app);


                builder.setContentTitle(title)       // required
                        .setSmallIcon(icon) // required
                        .setContentText(messageBody)  // required
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setTicker(messageBody + " Ticker")
                        .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                        .setLights(Color.RED, 500, 500)
                        .setPriority(Notification.PRIORITY_HIGH);

            }

            notificationManager.notify(idNotification /* ID of notification */, builder.build());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
