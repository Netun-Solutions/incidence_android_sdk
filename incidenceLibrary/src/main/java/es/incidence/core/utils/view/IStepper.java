package es.incidence.core.utils.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.percentlayout.widget.PercentLayoutHelper;
import androidx.percentlayout.widget.PercentRelativeLayout;

import com.e510.commons.fragment.BaseFragment;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

public class IStepper extends RelativeLayout
{
    private BaseFragment baseFragment;

    private RelativeLayout layoutRoot;
    private LinearLayout layoutSteps;

    //valores de 0 a 1 para rango de progeso.
    private float[] steps;

    public IStepper(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public IStepper(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public IStepper(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_istepper, this, true);

        layoutRoot = view.findViewById(R.id.layoutRoot);
        layoutSteps = view.findViewById(R.id.layoutSteps);
    }

    public void init(BaseFragment baseFragment, float[] steps)
    {
        setBaseFragment(baseFragment);
        this.steps = steps;
        drawSteps();
    }

    public void setSteps(float[] steps)
    {
        this.steps = steps;
        drawSteps();
    }

    public void setBaseFragment(BaseFragment baseFragment)
    {
        this.baseFragment = baseFragment;
    }

    public void show() {
        this.setVisibility(View.VISIBLE);
    }

    public void hide() {
        this.setVisibility(View.GONE);
    }

    private void drawSteps()
    {
        layoutSteps.removeAllViews();
        layoutSteps.setWeightSum(steps.length);

        for (int i = 0; i < steps.length; i++)
        {
            float val = steps[i];

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, Utils.dpToPx(3));
            params.weight = 1;
            int margin = Utils.dpToPx(3);

            PercentRelativeLayout view = new PercentRelativeLayout(getContext());
            if (i == 0) {
                params.setMarginEnd(margin);
            } else if (i == steps.length -1) {
                params.setMarginStart(margin);
            } else {
                params.setMarginStart(margin);
                params.setMarginEnd(margin);
            }
            view.setLayoutParams(params);


            RelativeLayout viewGray = new RelativeLayout(getContext());
            viewGray.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            viewGray.setBackgroundResource(R.drawable.steps_rounded_corners);
            GradientDrawable drawable = (GradientDrawable) viewGray.getBackground();
            drawable.setColor(Utils.getColor(getContext(), R.color.incidence200));
            view.addView(viewGray);

            if (val > 0)
            {
                RelativeLayout viewProgress = new RelativeLayout(getContext());
                viewProgress.setBackgroundResource(R.drawable.steps_rounded_corners);
                GradientDrawable drawableProgress = (GradientDrawable) viewProgress.getBackground();
                drawableProgress.setColor(Utils.getColor(getContext(), R.color.incidence500));
                view.addView(viewProgress);

                PercentRelativeLayout.LayoutParams params2 = (PercentRelativeLayout.LayoutParams) viewProgress.getLayoutParams();
                PercentLayoutHelper.PercentLayoutInfo info = params2.getPercentLayoutInfo();
                info.widthPercent = val;
                //viewProgress.requestLayout();
            }

            layoutSteps.addView(view);
        }
    }
}
