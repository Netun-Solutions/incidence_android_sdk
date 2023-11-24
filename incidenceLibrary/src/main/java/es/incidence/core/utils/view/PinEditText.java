package es.incidence.core.utils.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;

import com.e510.commons.utils.Utils;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.commons.view.VXEditText;
import com.e510.incidencelibrary.R;

public class PinEditText extends VXEditText {


    private OnFocusChangeListener otherFocusListener;

    public PinEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public PinEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinEditText(Context context) {
        super(context);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    private void init()
    {
        int radius = Integer.parseInt(AppConfiguration.getInstance().appearance.roundedFields);
        GradientDrawable back =  Utils.createGradientDrawable(getContext(), android.R.color.white, radius);
        this.setBackground(back);

        super.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (!b)
                {
                    GradientDrawable drawable = (GradientDrawable)PinEditText.this.getBackground();
                    drawable.setStroke(0, Utils.getColor(getContext(), R.color.incidence400));
                    setHint("-");
                }
                else
                {
                    GradientDrawable drawable = (GradientDrawable)PinEditText.this.getBackground();
                    drawable.setStroke(Utils.dpToPx(1), Utils.getColor(getContext(), R.color.incidence400));
                    setHint("");
                }

                if (otherFocusListener != null)
                {
                    otherFocusListener.onFocusChange(view, b);
                }
            }
        });
    }

    @Override
    public void setOnFocusChangeListener(OnFocusChangeListener l) {
        this.otherFocusListener = l;
    }
}
