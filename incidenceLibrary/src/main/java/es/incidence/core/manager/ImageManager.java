package es.incidence.core.manager;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.e510.commons.activity.BaseActivity;
import com.e510.commons.view.TouchImageView;
import com.e510.incidencelibrary.R;

import java.io.File;

public class ImageManager
{
    public static boolean hasCameraPermission(Context context)
    {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public static void checkCameraPermission(Activity activity)
    {
        if (!hasCameraPermission(activity))
        {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CAMERA}, BaseActivity.PERMISSION_CAMERA);
        }
    }

    //loaders

    public static void loadUrlFileImage(Context context, String url, ImageView imageView)
    {
        loadImage(context, url, R.drawable.placeholder, imageView, false);
    }

    public static void loadImage(Context context, String url, ImageView imageView)
    {
        loadImage(context, url, R.drawable.placeholder, imageView, false);
    }

    public static void loadFitImage(Context context, String url, ImageView imageView)
    {
        loadImage(context, url, R.drawable.placeholder, imageView, true);
    }

    public static void loadImage(Context context, String url, Integer placeholder, ImageView imageView)
    {
        loadImage(context, url, placeholder, imageView, false);
    }

    public static void loadImage(Context context, String url, Integer placeholder, ImageView imageView, boolean fit)
    {
        try
        {
            RequestBuilder builder = Glide.with(context).load(url);

            if (fit)
            {
                builder.fitCenter();
            }
            else
            {
                builder.centerCrop();
            }

            //Bug reconocido. Si se pone placeholder y transition el placeholder no desaparece
            if (placeholder == null)
            {
                builder.transition(withCrossFade());
            }
            else
            {
                builder.placeholder(placeholder);
            }

            builder.into(imageView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void loadTouchImage(Context context, String url, Integer placeholder, final TouchImageView imageView)
    {
        try
        {
            RequestBuilder builder = Glide.with(context).load(url);
            builder.fitCenter();
            if (placeholder != null)
            {
                builder.placeholder(placeholder);
            }
            builder.into(new CustomTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    imageView.setImageDrawable(resource);
                }

                @Override
                public void onLoadCleared(@Nullable Drawable placeholder) {
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void loadImage(Context context, Uri uri, ImageView imageView)
    {
        try
        {
            Glide
                    .with(context)
                    .load(uri)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void loadImage(Context context, File file, ImageView imageView)
    {
        try
        {
            Glide
                    .with(context)
                    .load(file)
                    .centerCrop()
                    .placeholder(R.drawable.placeholder)
                    .into(imageView);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
