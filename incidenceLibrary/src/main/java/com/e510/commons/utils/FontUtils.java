package com.e510.commons.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

public class FontUtils
{
    public static final String FONT_EXTENSION = ".ttf";

    public static String PRIMARY_REGULAR = "Raleway-Regular.ttf";
    public static String PRIMARY_MEDIUM = "Raleway-Medium.ttf";
    public static String PRIMARY_LIGHT = "Raleway-Light.ttf";
    public static String PRIMARY_BOLD = "Raleway-Bold.ttf";

    public static String SECONDARY_REGULAR = "RockwellStd.ttf";
    public static String SECONDARY_MEDIUM = "RockwellStd.ttf";
    public static String SECONDARY_LIGHT = "RockwellStd-Light.ttf";
    public static String SECONDARY_BOLD = "RockwellStd-Bold.ttf";

    public static Typeface getTypeFace(String font, Context context)
    {
        Typeface typeFace = null;
        String path = "fonts/" + font;
        if (assetExists(context, path))
        {
            typeFace = Typeface.createFromAsset(context.getAssets(), path);
        }
        return typeFace;
    }

    public static void setTypeValueText(View view, Context context)
    {
        String path = "fonts/" + PRIMARY_MEDIUM;
        if (assetExists(context, path))
        {
            Typeface typeFace = Typeface.createFromAsset(context.getAssets(), path);
            setTypeValueTextAlgorithm(view, typeFace);
        }
    }

    public static void setTypeValueText(View view, String font, Context context)
    {
        String path = "fonts/" + font;
        if (assetExists(context, path))
        {
            Typeface typeFace = Typeface.createFromAsset(context.getAssets(), path);
            setTypeValueTextAlgorithm(view, typeFace);
        }
    }

    private static void setTypeValueTextAlgorithm(View view, Typeface typeFace)
    {
        if(view instanceof ViewGroup)
        {
            final int childcount = ((ViewGroup)view).getChildCount();
            for (int i = 0; i < childcount; i++)
            {
                View v = ((ViewGroup)view).getChildAt(i);

                if(v instanceof ViewGroup)
                {
                    setTypeValueTextAlgorithm(v, typeFace);
                }
                else
                {
                    setTypeValue(v, typeFace);
                }
            }
        }
        else
        {
            setTypeValue(view, typeFace);
        }
    }

    private static void setTypeValue(View view, Typeface typeFace)
    {
        if(view instanceof TextView)
        {
            ((TextView)view).setTypeface(typeFace);
            ((TextView)view).setIncludeFontPadding(false);
        }
        else if(view instanceof Button)
        {
            ((Button)view).setTypeface(typeFace);
            ((Button)view).setIncludeFontPadding(false);
        }
        else if(view instanceof EditText)
        {
            ((EditText)view).setTypeface(typeFace);
            ((EditText)view).setIncludeFontPadding(false);
        }
    }

    private static boolean assetExists(Context context, String pathInAssetsDir)
    {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = null;
        try {
            inputStream = assetManager.open(pathInAssetsDir);
            if(null != inputStream ) {
                return true;
            }
        }  catch(IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if(null != inputStream )
                    inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
