package com.e510.commons.utils.config;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Base64;

import androidx.core.app.ActivityCompat;

import com.e510.commons.activity.BaseActivity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtils {

    private static final int COMPRESS_QUALITY = 100;

    public static boolean hasPermission(Context context)
    {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestPermission(Activity activity)
    {
        if (!hasPermission(activity))
        {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, BaseActivity.PERMISSION_WRITE_REQUEST_CODE);
        }
    }

    public static void checkFolder(String path)
    {
        try
        {
            File dir = new File(path);

            if(!dir.exists())
            {
                dir.mkdir();
            }

            File fNoMediaBD = new File(dir, ".nomedia");
            if (!fNoMediaBD.exists()) {
                fNoMediaBD.createNewFile();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String getFromAssets(Context context, String fileName) {
        String jsonString;
        try {
            InputStream is = context.getAssets().open(fileName);

            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            jsonString = new String(buffer, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return jsonString;
    }

    public static void saveStringToFile(String string, File file)
    {
        try
        {
            if (!file.exists())
                file.createNewFile();

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(string);
            bw.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public static String getStringFromFile (String filePath) {
        try
        {
            File fl = new File(filePath);
            FileInputStream fin = new FileInputStream(fl);
            String ret = convertStreamToString(fin);
            //Make sure you close all streams.
            fin.close();
            return ret;
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static void removeDirectory(File dir)
    {
        if (dir.isDirectory())
        {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++)
            {
                new File(dir, children[i]).delete();
            }
        }
    }

    public static void createDirectory(File dir)
    {
        createDirectory(dir, false);
    }

    public static void createDirectory(File dir, boolean withNoMedia)
    {
        if(!dir.exists())
        {
            dir.mkdir();

            if (withNoMedia)
            {
                File fNoMediaBD = new File(dir, ".nomedia");
                if (!fNoMediaBD.exists()) {
                    try {
                        fNoMediaBD.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean copy(File src, File dst) throws IOException
    {
        boolean res = false;
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                res = true;
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }

        return res;
    }

    public static String convertStreamToString(InputStream is) {
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();
            return sb.toString();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }

        return "";
    }

    public static String getFotoFileBase64(String fotoPath)
    {
        //final int WIDTH = 600;
        //final int HEIGHT = 800;

        String incidenceImgBase64 = null;

        try
        {
            if (fotoPath != null)
            {
                Bitmap incidenceImg = getOrientedBitmap(fotoPath);
                /*
                if (incidenceImg.getHeight() > incidenceImg.getWidth()) {
                    incidenceImg = resize(incidenceImg, WIDTH, HEIGHT);
                } else {
                    incidenceImg = resize(incidenceImg, HEIGHT, WIDTH);
                }
                */

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                incidenceImg.compress(Bitmap.CompressFormat.JPEG, COMPRESS_QUALITY, baos);
                byte[] byteArrayImage = baos.toByteArray();
                incidenceImgBase64 = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);

                return incidenceImgBase64;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return incidenceImgBase64;
    }

    private static Bitmap getOrientedBitmap(String path)
    {
        Bitmap bitmap = null;

        try
        {
            bitmap = BitmapFactory.decodeFile(path);

            ExifInterface ei = new ExifInterface(path);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch(orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    bitmap = rotateImage(bitmap, 90);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    bitmap = rotateImage(bitmap, 180);
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    bitmap = rotateImage(bitmap, 270);
                    break;

                case ExifInterface.ORIENTATION_NORMAL:

                default:
                    break;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return bitmap;
    }

    private static Bitmap rotateImage(Bitmap source, float angle) {

        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
