package com.e510.commons.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.widget.ContentLoadingProgressBar;

import com.e510.commons.utils.Utils;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.incidencelibrary.R;

public class Hud extends RelativeLayout {

    private RelativeLayout layoutAlpha;

    public Hud(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public Hud(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public Hud(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_hud, this, true);

        layoutAlpha = view.findViewById(R.id.layoutAlpha);

        ContentLoadingProgressBar progressBar = view.findViewById(R.id.progress);
        int colorPrimary = Utils.getColor(context, R.color.colorPrimary);
        try {
            colorPrimary = Color.parseColor("#"+ AppConfiguration.getInstance().appearance.colors.primary);
        } catch (Exception e) {
        }
        progressBar.setIndeterminateTintList(ColorStateList.valueOf(colorPrimary));

        hide();
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }

    public void setOpaque()
    {
        setOpaque(1);
    }
    public void setOpaque(float alpha)
    {
        layoutAlpha.setAlpha(alpha);
    }
}
