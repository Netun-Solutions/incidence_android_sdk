package es.incidence.core.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.library.IncidenceLibraryManager;
import io.github.douglasjunior.androidSimpleTooltip.SimpleTooltip;

public class Tooltip
{
    public static void showTooltip(Context context, View view, String message)
    {
        SimpleTooltip tooltip =
                new SimpleTooltip.Builder(context)
                        .anchorView(view)
                        //.gravity(Gravity.END)
                        .animated(false)
                        //.modal(true)
                        .dismissOnOutsideTouch(false)
                        .margin((float) Utils.dpToPx(3))
                        .arrowWidth((float)Utils.dpToPx(19))
                        .arrowHeight((float)Utils.dpToPx(9))
                        .arrowColor(Utils.getColor(context, R.color.incidencePrincipal))
                        .contentView(R.layout.layout_tooltip, R.id.txtTitle)
                        //.text(R.string.incidence_key_caducity_insurance_date_near)
                        .focusable(true)
                        .build();

        TextView textView = tooltip.findViewById(R.id.txtTitle);
        FontUtils.setTypeValueText(textView, Constants.FONT_REGULAR, context);
        textView.setText(message);

        tooltip.show();
    }

    public static void showTooltipDevice(Context context, View view) {
        String desc = context.getString(R.string.incidence_key_select_beacon_type_qr_info);
        showTooltipDevice(context, view, desc);
    }
    public static void showTooltipDevice(Context context, View view, String message)
    {
        Integer color = IncidenceLibraryManager.instance.getSupportBackgroundColor();
        if (color == null) {
            color = Utils.getColor(context, R.color.incidencePrincipal);
        }

        SimpleTooltip tooltip =
                new SimpleTooltip.Builder(context)
                        .anchorView(view)
                        //.gravity(Gravity.CENTER)
                        .animated(false)
                        //.modal(true)
                        .dismissOnOutsideTouch(false)
                        .margin((float) 0)
                        .padding((float) 0)
                        .arrowWidth((float)Utils.dpToPx(19))
                        .arrowHeight((float)Utils.dpToPx(9))
                        .arrowColor(color)
                        .contentView(R.layout.layout_tooltip_device, R.id.txtTitle)
                        //.text(R.string.incidence_key_caducity_insurance_date_near)
                        .focusable(true)
                        .setWidth(ViewGroup.LayoutParams.FILL_PARENT)
                        .build();

        TextView textView = tooltip.findViewById(R.id.txtTitle);
        FontUtils.setTypeValueText(textView, Constants.FONT_REGULAR, context);
        textView.setText(message);

        ImageView imgClose = tooltip.findViewById(R.id.imgClose);
        //imgClose.setColorFilter(color);

        RelativeLayout backTooltip = tooltip.findViewById(R.id.backTooltip);
        Drawable background = backTooltip.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.setColor(color);

        IncidenceLibraryManager.instance.setSupportTextColor(textView);
        IncidenceLibraryManager.instance.setSupportTintColor(imgClose);

        tooltip.show();
    }

    public static void showTooltipText(Context context, View view, String message)
    {
        Integer color = IncidenceLibraryManager.instance.getSupportBackgroundColor();
        if (color == null) {
            color = Utils.getColor(context, R.color.incidencePrincipal);
        }

        SimpleTooltip tooltip =
                new SimpleTooltip.Builder(context)
                        .anchorView(view)
                        //.gravity(Gravity.CENTER)
                        .animated(false)
                        //.modal(true)
                        .dismissOnOutsideTouch(false)
                        .margin((float) 0)
                        .padding((float) 0)
                        .arrowWidth((float)Utils.dpToPx(19))
                        .arrowHeight((float)Utils.dpToPx(9))
                        .arrowColor(color)
                        .contentView(R.layout.layout_tooltip_device, R.id.txtTitle)
                        //.text(R.string.incidence_key_caducity_insurance_date_near)
                        .focusable(true)
                        .setWidth(ViewGroup.LayoutParams.FILL_PARENT)
                        .build();

        TextView textView = tooltip.findViewById(R.id.txtTitle);
        FontUtils.setTypeValueText(textView, Constants.FONT_REGULAR, context);
        textView.setText(message);

        ImageView imageDevice = tooltip.findViewById(R.id.imgDevice);
        imageDevice.setVisibility(View.GONE);

        ImageView imgClose = tooltip.findViewById(R.id.imgClose);

        RelativeLayout backTooltip = tooltip.findViewById(R.id.backTooltip);
        Drawable background = backTooltip.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.setColor(color);

        IncidenceLibraryManager.instance.setSupportTextColor(textView);
        IncidenceLibraryManager.instance.setSupportTintColor(imgClose);

        tooltip.show();
    }
}
