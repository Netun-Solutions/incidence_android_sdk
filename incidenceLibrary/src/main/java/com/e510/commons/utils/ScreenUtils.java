package com.e510.commons.utils;

import android.content.Context;
import android.graphics.Point;
import android.view.Display;
import android.view.WindowManager;

public class ScreenUtils {
    public static Point getWindowSize(Context context){
        WindowManager wm    = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display     = wm.getDefaultDisplay();
        Point size          = new Point();
        display.getSize(size);

        return size;
    }

    public static double getScreenWidth(Context context)
    {
        Point size = ScreenUtils.getWindowSize(context);
        return size.x;
    }

    public static double getScreenHeight(Context context)
    {
        Point size = ScreenUtils.getWindowSize(context);
        return size.y;
    }
}
