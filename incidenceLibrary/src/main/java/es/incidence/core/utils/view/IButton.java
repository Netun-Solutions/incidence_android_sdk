package es.incidence.core.utils.view;


import android.content.Context;
import android.util.AttributeSet;

import com.e510.commons.utils.Utils;
import com.e510.commons.view.PrimaryButton;
import com.e510.incidencelibrary.R;

public class IButton extends PrimaryButton
{
    private boolean disabled = false;

    public IButton(Context context) {
        super(context);
        setup();
    }

    public IButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    public IButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setup();
    }

    private void setup()
    {
        setTextSize(16);
        setWhiteColors();
        setHeight((int)getResources().getDimension(R.dimen.button_height));
    }

    public void setWhiteColors()
    {
        setBackground(getContext(), android.R.color.white, R.color.lightGray);
        setTextColor(Utils.getColor(getContext(), R.color.colorPrimary));
        disabled = false;
    }

    public void setPrimaryColors()
    {
        setPrimaryColors(R.color.colorPrimary, R.color.colorPrimaryDark);
        disabled = false;
    }

    public void setPrimaryColors(int idColor, int idColorDark)
    {
        setBackground(getContext(), idColor, idColorDark);
        setTextColor(Utils.getColor(getContext(), android.R.color.white));
        disabled = false;
    }

    public void setDisabledColors()
    {
        setBackground(getContext(), R.color.grey200);
        setTextColor(Utils.getColor(getContext(), R.color.black400));
        disabled = true;
    }

    public boolean isDisabled()
    {
        return disabled;
    }
}