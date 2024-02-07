package com.e510.commons.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.graphics.ColorUtils;
import androidx.core.graphics.drawable.DrawableCompat;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.incidencelibrary.R;

public class PrimaryButton extends RelativeLayout {

    private String title;
    private float radius;
    private boolean wrapped;
    private RelativeLayout layoutRound;
    private Drawable drawableLeft;
    private Drawable drawableRight;

    private TextView txtTitle;
    private ImageView imgLeft;
    private ImageView imgRight;

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public PrimaryButton(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public PrimaryButton(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public PrimaryButton(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.PrimaryField, defStyleAttr,0);

        try
        {
            title = a.getString(R.styleable.PrimaryField_topTitle);
            radius = a.getInt(R.styleable.PrimaryField_radius, Integer.parseInt(AppConfiguration.getInstance().appearance.roundedFields));
            wrapped = a.getBoolean(R.styleable.PrimaryField_wrapped, false);
            drawableLeft = a.getDrawable(R.styleable.PrimaryField_drawableLeft);
            drawableRight = a.getDrawable(R.styleable.PrimaryField_drawableRight);
        }
        finally
        {
            a.recycle();
        }

        LayoutInflater inflater = LayoutInflater.from(context);
        int idLayout = wrapped ? R.layout.primary_button_wrapped : R.layout.primary_button;
        ViewGroup view = (ViewGroup) inflater.inflate(idLayout, this, true);
        layoutRound = view.findViewById(R.id.layoutRound);
        txtTitle = view.findViewById(R.id.txtTitle);
        FontUtils.setTypeValueText(txtTitle, FontUtils.PRIMARY_BOLD, getContext());
        imgLeft = view.findViewById(R.id.imgLeft);
        imgRight = view.findViewById(R.id.imgRight);


        if (drawableLeft != null) {
            imgLeft.setVisibility(View.VISIBLE);
            imgLeft.setImageDrawable(drawableLeft);
            DrawableCompat.setTint(imgLeft.getDrawable(), Utils.getColor(context, R.color.white));
        }

        if (drawableRight != null) {
            imgRight.setVisibility(View.VISIBLE);
            imgRight.setImageDrawable(drawableRight);
            DrawableCompat.setTint(imgRight.getDrawable(), Utils.getColor(context, R.color.white));
        }

        if (wrapped)
        {
            if (radius > 0)
            {
                radius = radius * 30 / 50;
            }
        }

        setBackground(context, R.color.colorPrimary, R.color.colorPrimaryDark);

        if (title != null) {
            txtTitle.setText(title);
        }
    }

    public void setBackground(Context context, int backgroundColor, int backgroundSelectedColorId)
    {
        GradientDrawable back =  Utils.createGradientDrawable(context, backgroundColor, (int) radius);
        GradientDrawable backDark =  Utils.createGradientDrawable(context, backgroundSelectedColorId, (int) radius);

        setGradient(back, backDark);
    }

    private void setGradient(GradientDrawable back, GradientDrawable backDark)
    {
        StateListDrawable shape = new StateListDrawable();
        shape.addState(new int[]{android.R.attr.state_pressed}, backDark);
        shape.addState(new int[]{android.R.attr.state_focused}, backDark);
        shape.addState(new int[]{}, back);

        layoutRound.setBackground(shape);
    }

    public void setBackground(Context context, int backgroundColor)
    {
        GradientDrawable back =  Utils.createGradientDrawable(context, backgroundColor, (int) radius);
        layoutRound.setBackground(back);
    }

    public void setBackground(int backgroundColor)
    {
        //GradientDrawable back =  Utils.createGradientDrawable(context, backgroundColor, (int) radius);
        layoutRound.setBackgroundColor(backgroundColor);
    }

    public void setTextColor(int color) {
        txtTitle.setTextColor(color);
    }

    public void setTextSize(int size) {
        txtTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, size);
    }

    public void setText(int idString)
    {
        txtTitle.setText(idString);
    }

    public void setText(String text)
    {
        txtTitle.setText(text);
    }

    public void setTextToUpperCase()
    {
        txtTitle.setText(txtTitle.getText().toString().toUpperCase());
    }

    public void setHeight(int height)
    {
        LayoutParams params = (LayoutParams) txtTitle.getLayoutParams();
        params.height = Utils.dpToPx(height);
        txtTitle.setLayoutParams(params);
    }

    @Override
    public void setOnClickListener(@Nullable OnClickListener l) {
        layoutRound.setOnClickListener(l);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        layoutRound.setEnabled(enabled);

        if (enabled) {
            setBackground(getContext(), R.color.colorPrimary, R.color.colorPrimaryDark);
        }
        else {
            int colorPrimary = Utils.getColor(getContext(), R.color.colorPrimary);
            int colorWhite = Utils.getColor(getContext(), R.color.white);
            int color = ColorUtils.blendARGB(colorPrimary, colorWhite, 0.5f);

            GradientDrawable back =  Utils.createGradientDrawable(color, (int) radius);
            setGradient(back, back);
        }
    }
}
