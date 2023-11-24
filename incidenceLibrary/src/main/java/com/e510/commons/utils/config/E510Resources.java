package com.e510.commons.utils.config;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.os.Build;

import androidx.annotation.NonNull;

public class E510Resources extends Resources {

    private final String colorPrimary = "colorPrimary";
    private final String colorPrimaryDark = "colorPrimaryDark";

    private final String roundedFields = "roundedFields";
    private final String roundedRegisterForm = "roundedRegisterForm";

    public E510Resources(Resources original) {
        super(original.getAssets(), original.getDisplayMetrics(), original.getConfiguration());
        initConfiguration();
    }

    private AppConfiguration appConfiguration()
    {
        return AppConfiguration.getInstance();
    }

    private void initConfiguration()
    {
        configureDrawables();
    }

    @NonNull
    @Override
    public String getString(int id) throws NotFoundException {
        String key = getResourceEntryName(id);
        return super.getString(id);
    }



    @Override
    public int getColor(int id) throws NotFoundException {
        return getColor(id, null);
    }

    @Override
    public int getColor(int id, Theme theme) throws Resources.NotFoundException {

        String name = getResourceEntryName(id);

        switch (name) {
            case colorPrimary:
                return Color.parseColor("#"+appConfiguration().appearance.colors.primary);
            case colorPrimaryDark:
                return Color.parseColor("#"+appConfiguration().appearance.colors.primaryDark);
            default:

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return super.getColor(id, theme);
                }
                return super.getColor(id);
        }
    }

    @Override
    public float getDimension(int id) throws NotFoundException {
        switch (getResourceEntryName(id)) {
            case roundedFields:
                return Float.parseFloat(appConfiguration().appearance.roundedFields);
            case roundedRegisterForm:
                return Float.parseFloat(appConfiguration().appearance.roundedRegisterForm);
            default:
                return super.getDimension(id);
        }
    }

    private void setDrawableColor(Drawable background, int colorId)
    {
        if (background instanceof ShapeDrawable) {
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(getColor(colorId));
        } else if (background instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(getColor(colorId));
        } else if (background instanceof ColorDrawable) {
            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(getColor(colorId));
        }
    }

    private void setDrawableRadius(Drawable background, int dimenId){
        if (background instanceof GradientDrawable) {
            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setCornerRadius(getDimension(dimenId));
        }
    }

    private void configureDrawables()
    {/*
        Drawable drawable = getDrawable(R.drawable.rounded_primary);
        setDrawableColor(drawable, R.color.colorPrimary);
        setDrawableRadius(drawable, R.dimen.roundedFields);

        drawable = getDrawable(R.drawable.rounded_primary_dark);
        setDrawableColor(drawable, R.color.colorPrimaryDark);
        setDrawableRadius(drawable, R.dimen.roundedFields);

        drawable = getDrawable(R.drawable.rounded_white);
        setDrawableRadius(drawable, R.dimen.roundedFields);

        drawable = getDrawable(R.drawable.rounded_background);
        setDrawableRadius(drawable, R.dimen.roundedRegisterForm);
        */
    }
}
