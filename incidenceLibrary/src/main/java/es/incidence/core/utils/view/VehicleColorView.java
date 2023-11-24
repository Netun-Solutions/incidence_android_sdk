package es.incidence.core.utils.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.commons.view.CircleImageView;
import com.e510.incidencelibrary.R;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.domain.ColorType;
import es.incidence.core.domain.VehicleType;
import es.incidence.core.manager.ImageManager;

public class VehicleColorView extends RelativeLayout
{
    private ImageView imgVehicle;
    private TextView txtAddLater;
    private IButton btnContinue;
    private LinearLayout layoutRows;
    private ArrayList<ColorType> colors;

    private VehicleType vehicleType;
    private ColorType vehicleColor;

    private OnClickListener dismissListener;
    private OnClickListener continueListener;

    public VehicleColorView(Context context)
    {
        super(context);
        init(context, null, 0);
    }

    public VehicleColorView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public VehicleColorView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(final Context context, AttributeSet attrs, int defStyleAttr)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_vehicle_select_color, this, true);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, context);

        txtAddLater = view.findViewById(R.id.txtAddLater);
        txtAddLater.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dismissListener != null)
                {
                    dismissListener.onClick(view);
                }
            }
        });
        FontUtils.setTypeValueText(txtAddLater, Constants.FONT_SEMIBOLD, context);

        btnContinue = view.findViewById(R.id.btnContinueColor);

        //Necesitamos activity para que agarre el string en el idioma del usuario.
        String strAccept = Core.getString(R.string.continuar, context);
        btnContinue.setText(strAccept);
        btnContinue.setDisabledColors();
        btnContinue.setClickable(false);
        FontUtils.setTypeValueText(btnContinue, Constants.FONT_SEMIBOLD, context);
        btnContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (vehicleColor != null)
                {
                    if (continueListener != null)
                    {
                        continueListener.onClick(view);
                    }
                }
            }
        });

        layoutRows = view.findViewById(R.id.layoutRows);
        imgVehicle = view.findViewById(R.id.imgVehicle);
    }

    private void setCircleSelected(CircleImageView circleImageView)
    {
        circleImageView.setAlpha(1f);
        circleImageView.setBorderColor(Utils.getColor(getContext(), R.color.colorPrimary));
        circleImageView.setBorderWidth(Utils.dpToPx(3));
    }

    private void setCircleUnselected(CircleImageView circleImageView)
    {
        setCircleUnselected(circleImageView, Utils.getColor(getContext(), android.R.color.transparent));
    }
    private void setCircleUnselected(CircleImageView circleImageView, int colorBorder)
    {
        circleImageView.setAlpha((vehicleColor == null) ? 1.0f : 0.2f);
        circleImageView.setBorderColor(colorBorder);
        circleImageView.setBorderWidth(Utils.dpToPx(1));
    }

    /*
    private String getImageVehicle()
    {
        String res = "car_black";

        if (vehicleType != null && vehicleColor != null)
        {
            res = vehicleType + "_" + vehicleColor;
        }
        else if (vehicleType != null)
        {
            res = vehicleType + "_black";
        }

        return res;
    }*/

    public ColorType getVehicleColor()
    {
        return vehicleColor;
    }

    public void setVehicleColor(ColorType color)
    {
        vehicleColor = color;
        updateColors();
        if (vehicleColor != null)
        {
            ImageManager.loadImage(getContext(), vehicleColor.image, null, imgVehicle);
        }
    }

    public VehicleType getVehicleType()
    {
        return vehicleType;
    }
    public void setVehicleType(VehicleType type)
    {
        vehicleType = type;
        setColors(vehicleType.colors);
    }

    public void setDismissTitle(String title)
    {
        txtAddLater.setText(title);
    }

    public void setAcceptTitle(String title)
    {
        btnContinue.setText(title);
    }

    public void onDismissClickListener(OnClickListener listener)
    {
        dismissListener = listener;
    }

    public void onContinueClickListener(OnClickListener listener)
    {
        continueListener = listener;
    }

    public void enableContinueButton()
    {
        btnContinue.setPrimaryColors();
        btnContinue.setClickable(true);
    }

    private void setColors(ArrayList<ColorType> colors1)
    {
        colors = colors1;

        if (colors.size() > 0)
        {
            ColorType color = colors.get(0);
            ImageManager.loadImage(getContext(), color.image, null, imgVehicle);
            //imgVehicle.setImageDrawable(Utils.getDrawable(getContext(), getImageVehicle()));
        }
        updateColors();
    }

    private void updateColors()
    {
        layoutRows.removeAllViews();

        LayoutInflater inflater = LayoutInflater.from(getContext());

        View row = null;
        int added = 0;

        for (int i = 0; i < colors.size(); i++) {
            ColorType color = colors.get(i);


            if (added == 0) {
                row = inflater.inflate(R.layout.row_vehicle_select_color, null);
                layoutRows.addView(row);
            }

            CircleImageView colorLayout = null;
            CircleImageView color1 = row.findViewById(R.id.color1);
            CircleImageView color2 = row.findViewById(R.id.color2);
            CircleImageView color3 = row.findViewById(R.id.color3);
            CircleImageView color4 = row.findViewById(R.id.color4);


            if (added == 0) {
                color1.setVisibility(View.VISIBLE);
                color1.setCircleBackgroundColor(Color.parseColor(color.color));
                added = 1;
                colorLayout = color1;
            }
            else if (added == 1)
            {
                color2.setVisibility(View.VISIBLE);
                color2.setCircleBackgroundColor(Color.parseColor(color.color));
                added = 2;
                colorLayout = color2;
            }
            else if (added == 2)
            {
                color3.setVisibility(View.VISIBLE);
                color3.setCircleBackgroundColor(Color.parseColor(color.color));
                added = 3;
                colorLayout = color3;
            }
            else if (added == 3)
            {
                color4.setVisibility(View.VISIBLE);
                color4.setCircleBackgroundColor(Color.parseColor(color.color));
                added = 0;
                colorLayout = color4;
            }

            if (vehicleColor != null && vehicleColor.id == color.id)
            {
                setCircleSelected(colorLayout);
            }
            else
            {
                setCircleUnselected(colorLayout);
                //setCircleUnselected(layoutColorWhite, Utils.getColor(context, android.R.color.black));
            }

            colorLayout.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    vehicleColor = color;
                    //imgVehicle.setImageDrawable(Utils.getDrawable(getContext(), getImageVehicle()));
                    ImageManager.loadImage(getContext(), color.image, null, imgVehicle);
                    enableContinueButton();
                    updateColors();
                }
            });
        }
    }
}
