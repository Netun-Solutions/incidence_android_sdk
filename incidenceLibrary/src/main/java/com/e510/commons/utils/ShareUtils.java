package com.e510.commons.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class ShareUtils {
    public static void shareText(Context ctx, String text)
    {
        if (text != null)
        {
            Intent sendIntent = new Intent();
            sendIntent.setAction(Intent.ACTION_SEND);
            sendIntent.putExtra(Intent.EXTRA_TEXT, text);
            sendIntent.setType("text/plain");
            ctx.startActivity(sendIntent);
        }
    }

    public static void shareEmail(Context ctx, String email) {

        String[] array = {email};
        shareEmail(ctx, "", array, "");
    }

    public static void shareEmail(Context ctx, String subject, String[] emails, String text) {

        Intent i = new Intent(Intent.ACTION_SENDTO);
        i.setData(Uri.parse("mailto:"));
        i.putExtra(Intent.EXTRA_EMAIL, emails);
        i.putExtra(Intent.EXTRA_SUBJECT, subject);
        i.putExtra(Intent.EXTRA_TEXT, text);

        ctx.startActivity(i);
    }
}
