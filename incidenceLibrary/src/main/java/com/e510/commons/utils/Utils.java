package com.e510.commons.utils;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.core.content.ContextCompat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.Random;
import java.util.zip.InflaterInputStream;

public class Utils {
    private static final String TAG = makeLogTag(Utils.class);

    public static int dpToPx(int dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int dpToPx(float dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px)
    {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }
    public static int getColor(Context context, int idColor)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                return ContextCompat.getColor(context, idColor);
            }
            else
            {
                return context.getResources().getColor(idColor);
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "Error: " + e.getMessage(), e);
        }
        return 0;
    }

    public static Drawable getDrawable(Context context, int idDrawable)
    {
        try
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            {
                return context.getDrawable(idDrawable);
            }
            else
            {
                return context.getResources().getDrawable(idDrawable);
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "Error: " + e.getMessage(), e);
        }
        return null;
    }

    public static Drawable setDrawableShadow(Context context, Drawable drawable, int idColor)
    {
        try
        {
            Bitmap originalBitmap = null;

            if (drawable instanceof BitmapDrawable) {
                BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
                if(bitmapDrawable.getBitmap() != null) {
                    originalBitmap = bitmapDrawable.getBitmap();
                }
            }
            if (originalBitmap == null)
            {
                if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
                    originalBitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
                } else {
                    originalBitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                }
            }

            BlurMaskFilter blurFilter = new BlurMaskFilter(8, BlurMaskFilter.Blur.OUTER);
            Paint shadowPaint = new Paint();
            shadowPaint.setMaskFilter(blurFilter);

            int[] offsetXY = new int[2];
            Bitmap shadowImage = originalBitmap.extractAlpha(shadowPaint, offsetXY);
            shadowImage = tintBitmap(shadowImage, Utils.getColor(context, idColor));

            /* Need to convert shadowImage from 8-bit to ARGB here. */
            Bitmap shadowImage32 = shadowImage.copy(Bitmap.Config.ARGB_8888, true);

            Canvas c = new Canvas(shadowImage32);
            c.drawBitmap(originalBitmap, -offsetXY[0], -offsetXY[1], null);

            return new BitmapDrawable(context.getResources(), shadowImage32);
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "Error: " + e.getMessage(), e);
        }
        return null;
    }

    private static Bitmap tintBitmap(Bitmap bitmap, int color) {
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return bitmapResult;
    }

    public static Drawable getDrawable(Context context, String nameDrawable)
    {
        try
        {
            int id = context.getResources().getIdentifier(nameDrawable, "drawable", context.getPackageName());
            return getDrawable(context, id);
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "Error: " + e.getMessage(), e);
        }
        return null;
    }

    public static GradientDrawable createGradientDrawable(Context ctx, int idColor, int radius)
    {
        GradientDrawable shape =  new GradientDrawable();
        shape.setColor(Utils.getColor(ctx, idColor));
        shape.setCornerRadius(Utils.dpToPx(radius));

        return shape;
    }

    public static GradientDrawable createGradientDrawable(int color, int radius)
    {
        GradientDrawable shape =  new GradientDrawable();
        shape.setColor(color);
        shape.setCornerRadius(Utils.dpToPx(radius));

        return shape;
    }

    public static GradientDrawable createGradientCircleDrawable(Context context, int idColor)
    {
        GradientDrawable shapeCircle = new GradientDrawable();
        shapeCircle.setShape(GradientDrawable.OVAL);
        shapeCircle.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shapeCircle.setColor(Utils.getColor(context, idColor));

        return createGradientCircleDrawable(Utils.getColor(context, idColor));
    }

    public static void setCornerRadius(GradientDrawable drawable, float topLeft,
                                float topRight, float bottomRight, float bottomLeft) {
        drawable.setCornerRadii(new float[] { topLeft, topLeft, topRight, topRight,
                bottomRight, bottomRight, bottomLeft, bottomLeft });
    }

    public static GradientDrawable createGradientCircleDrawable(int color)
    {
        GradientDrawable shapeCircle = new GradientDrawable();
        shapeCircle.setShape(GradientDrawable.OVAL);
        shapeCircle.setCornerRadii(new float[]{0, 0, 0, 0, 0, 0, 0, 0});
        shapeCircle.setColor(color);

        return shapeCircle;
    }

    public static void playResourceAudio(Context context, final int idSound)
    {
        try
        {
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (am != null)
            {
                final int volume = am.getStreamVolume(AudioManager.STREAM_SYSTEM);

                MediaPlayer mediaPlayer = new MediaPlayer();
                mediaPlayer.setVolume(volume, volume);
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_SYSTEM);
                String fileName = "android.resource://" + context.getPackageName() + "/" + idSound;
                mediaPlayer.setDataSource(context, Uri.parse(fileName));
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
                {
                    @Override
                    public void onCompletion(MediaPlayer mp)
                    {
                        mp.release();
                        mp = null;
                    }
                });
                mediaPlayer.prepare();
                mediaPlayer.start();
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "playResourceAudio: " + e.getMessage(), e);
        }
    }

    public static void playVibration(Context context)
    {
        try
        {
            Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(500);
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "playResourceAudio: " + e.getMessage(), e);
        }
    }

    public static int getActionBarSize(Context context) {
        TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(new int[] { android.R.attr.actionBarSize });
        int actionBarSize = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();
        return actionBarSize;
    }

    public static String gzUncompress(String text)
    {
        String result = "";

        try
        {
            byte[] zbytes = text.getBytes("ISO-8859-1");
            // Add extra byte to array when Inflater is set to true
            byte[] input = new byte[zbytes.length + 1];
            System.arraycopy(zbytes, 0, input, 0, zbytes.length);
            input[zbytes.length] = 0;
            ByteArrayInputStream bin = new ByteArrayInputStream(input);
            InflaterInputStream in = new InflaterInputStream(bin);
            ByteArrayOutputStream bout = new ByteArrayOutputStream(512);
            int b;
            while ((b = in.read()) != -1) {
                bout.write(b); }
            bout.close();
            result = bout.toString();
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "Error: " + e.getMessage(), e);
        }

        return result;
    }

    public static String combineParams(String text, boolean dim)
    {
        String p = "9IHgcxve0VvCHEQcQyTu";
        //String combinedText = "";
        StringBuffer combinedText = new StringBuffer();
        String combinedTextRes = "";

        try
        {
            String text64 = text;
            String pattern64 = StringUtils.formatNullStringBase64(p);

            if (dim)
            {
                text64 = StringUtils.formatNullStringBase64(text);
            }

            for (int i = 0; i < text64.length(); i++)
            {
                char s = text64.charAt(i);

                if (dim)
                {
                    String l = "";

                    if (pattern64.length() > i)
                    {
                        char c = pattern64.charAt(i);
                        l = "" + c;
                    }
                    else
                    {
                        Random r = new Random();
                        char c = (char)(r.nextInt(26) + 'a');
                        l = "" + c;
                    }

                    //combinedText += s + l;
                    combinedText.append(s).append(l);
                }
                else if (i%2 == 0)
                {
                    combinedText.append(s);
                }
            }

            if (!dim)
            {
                combinedTextRes = StringUtils.getStringFromBase64(combinedText.toString());
            }
        }
        catch (Exception e)
        {
        }

        return combinedTextRes;
    }

    public static boolean MapValue(String key, Object value, Class aClass, Object obj, String TAG)
    {
        boolean result = false;

        try
        {
            Field field = aClass.getField(key);

            if (field != null && value != null)
            {
                field.set(obj, Utils.changeObjectToClass(value, field.getType()));
                result = true;
            }
        }
        catch (NoSuchFieldException e)
        {
            LogUtil.logD(TAG, "NoSuchFieldException-MapValue: " + key + " - " + e.getMessage(), e);
        } catch (IllegalAccessException ea)
        {
            LogUtil.logD(TAG, "IllegalAccessException-MapValue: " + ea.getMessage(), ea);
        }
        //catch (Exception e)
        //{
        //    HLog.e(TAG, "Exception-MapValue: " + e.getMessage());
        //}

        return true;
    }

    public static Object changeObjectToClass(Object object, Class classType)
    {
        try
        {
            if (object != null && classType != null)
            {
                Class objectClass = object.getClass();

                if (objectClass != classType)
                {
                    if (objectClass == String.class)
                    {
                        if (classType == Integer.class || classType == int.class)
                        {
                            try {
                                object = Integer.parseInt((String) object);
                            } catch (IllegalArgumentException e) {
                                //Log.d(TAG, "Error changeObjectToClass " + e.getMessage(), e);
                                object = new Integer(0);
                            }
                        }
                        else if (classType == Long.class || classType == long.class)
                        {
                            object = Long.parseLong((String) object);
                        }
                        else if (classType == Float.class || classType == float.class)
                        {
                            object = Float.parseFloat((String) object);
                        }
                        else if (classType == Double.class || classType == double.class)
                        {
                            object = Double.parseDouble((String) object);
                        }
                        else if (classType == Date.class)
                        {
                            //DateFormat format = new SimpleDateFormat(DateUtils.DATE_FOR_DB, Locale.ENGLISH);
                            object = DateUtils.DATE_TIME.parse((String) object);
                        }
                        else if (classType == Boolean.class || classType == boolean.class)
                        {
                            object = Boolean.valueOf((String) object);
                        }
                    }
                    else if (objectClass == Integer.class)
                    {
                        if (classType == String.class)
                        {
                            object = String.valueOf(object);
                        }
                        else if (classType == Float.class || classType == float.class)
                        {
                            object = new Float((int)object);
                        }
                        else if (classType == Double.class || classType == double.class)
                        {
                            object = new Double((int)object);
                        }
                        else if (classType == Boolean.class || classType == boolean.class)
                        {
                            String stringObject =  (String) changeObjectToClass(object, String.class);
                            object = changeObjectToClass(stringObject, Boolean.class);
                        }
                        else if (classType == Long.class || classType == long.class)
                        {
                            object = new Long((int)object);
                        }
                    }
                    else if (objectClass == Float.class)
                    {
                        if (classType == String.class)
                        {
                            object = String.valueOf(object);
                        }
                        else if (classType == Integer.class || classType == int.class)
                        {
                            object = new Integer((int)Math.round((float)object));
                        }
                        else if (classType == Double.class || classType == double.class)
                        {
                            object = new Double((float)object);
                        }
                        else if (classType == Boolean.class || classType == boolean.class)
                        {
                            String stringObject =  (String) changeObjectToClass(object, String.class);
                            object = changeObjectToClass(stringObject, Boolean.class);
                        }
                    }
                    else if (objectClass == Double.class)
                    {
                        if (classType == String.class)
                        {
                            object = String.valueOf(object);
                        }
                        else if (classType == Float.class || classType == float.class)
                        {
                            object = new Float((double)object);
                        }
                        else if (classType == Integer.class || classType == int.class)
                        {
                            object = new Integer((int)Math.round((double)object));
                        }
                        else if (classType == Boolean.class || classType == boolean.class)
                        {
                            String stringObject =  (String) changeObjectToClass(object, String.class);
                            object = changeObjectToClass(stringObject, Boolean.class);
                        }
                    }
                    else if (objectClass == Date.class)
                    {
                        if (classType == String.class)
                        {
                            //DateFormat df = new SimpleDateFormat(LocalDataConnector.to_db_dtt_format, Locale.ENGLISH);
                            object = DateUtils.DATE_TIME.format((Date) object);
                        }
                    }
                    else if (objectClass == Long.class)
                    {
                        if (classType == Date.class)
                        {
                            object = new Date((Long)object);
                        }
                    }
                }
            }
        }
        catch (Exception e)
        {
            LogUtil.logE(TAG, "changeObjectToClass: " + e.getMessage(), e);
        }

        return object;
    }
}
