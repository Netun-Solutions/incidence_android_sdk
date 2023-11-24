package es.incidence.core.utils.view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.ScreenUtils;
import com.e510.commons.utils.Utils;
import com.e510.commons.utils.swipe.SwipeToHideViewListener;
import com.e510.incidencelibrary.R;

import java.util.ArrayList;

import es.incidence.core.Constants;

public class INotification extends RelativeLayout
{
    private RelativeLayout layoutRoot;
    private View layoutBlur;
    private RelativeLayout layoutRound;
    private TextView txtTitle;
    private TextView txtMessage;
    private IButton btnNotifContinue;
    private OnClickListener listener;
    private OnClickListener listenerCancel;
    private TextView txtNotifCancel;
    private ScrollView scrollLayoutOptions;
    private LinearLayout layoutOptions;

    private String cancelText;
    private boolean canHide;
    private SwipeToHideViewListener.SwipeToHideCompletionListener dismissListener;

    private static INotification notification;

    public static INotification shared(final Context context) {

        if (notification == null) {
            notification = new INotification(context);
        }
        return notification;
    }

    public INotification(Context context) {
        super(context);
        init(context);
    }

    private void init(final Context context)
    {
        LayoutInflater inflater = LayoutInflater.from(context);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.layout_inotification, this, true);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        layoutRoot = view.findViewById(R.id.layoutRoot);
        layoutBlur = view.findViewById(R.id.layoutBlur);
        layoutRound = view.findViewById(R.id.layoutRound);
        layoutRound.setClipToOutline(true);
        float radius = Utils.dpToPx(16);
        GradientDrawable drawable = Utils.createGradientDrawable(getContext(), android.R.color.white, 0);
        drawable.setCornerRadii(new float [] { radius, radius,
                radius, radius,
                0, 0,
                0, 0});
        layoutRound.setBackground(drawable);

        GradientDrawable drawable2 = Utils.createGradientDrawable(getContext(), R.color.grey200, 3);
        View line = view.findViewById(R.id.line);
        line.setBackground(drawable2);

        txtTitle = view.findViewById(R.id.txtTitle);
        FontUtils.setTypeValueText(txtTitle, Constants.FONT_SEMIBOLD, getContext());

        txtMessage = view.findViewById(R.id.txtMessage);

        scrollLayoutOptions = view.findViewById(R.id.scrollLayoutOptions);
        layoutOptions = view.findViewById(R.id.layoutOptions);

        txtNotifCancel = view.findViewById(R.id.txtNotifCancel);
        FontUtils.setTypeValueText(txtNotifCancel, Constants.FONT_SEMIBOLD, getContext());
        cancelText = context.getString(R.string.cancel);
        txtNotifCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listenerCancel != null) {
                    listenerCancel.onClick(view);
                }
            }
        });

        btnNotifContinue = view.findViewById(R.id.btnNotifContinue);
        btnNotifContinue.setPrimaryColors();
        FontUtils.setTypeValueText(btnNotifContinue, Constants.FONT_SEMIBOLD, getContext());
        btnNotifContinue.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                if(listener != null) {
                    listener.onClick(view);
                }
            }
        });

        SwipeToHideViewListener cardSwipeListener = new SwipeToHideViewListener(layoutRound, true, new SwipeToHideViewListener.SwipeToHideCompletionListener() {
            @Override
            public void viewDismissed() {
                if (canHide && dismissListener != null)
                {
                    dismissListener.viewDismissed();
                }
            }
        });
        layoutRoot.setOnTouchListener(cardSwipeListener);


        OnClickListener clickListenerBlur = new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canHide)
                {
                    Animation slide = notification.getAnimationHide();
                    slide.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            ((ViewGroup) notification.getParent()).removeView(notification);
                            notification = null;
                        }
                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    notification.animateHide(slide);
                }
            }
        };
        layoutBlur.setOnClickListener(clickListenerBlur);
    }

    private void setTitle(String title)
    {
        txtTitle.setText(title);
        txtTitle.setVisibility(title != null ? View.VISIBLE : View.INVISIBLE);
    }

    private void setTitleButton(String title)
    {
        btnNotifContinue.setText(title);
    }

    private void setTitleCancelButton(String title)
    {
        txtNotifCancel.setText(title);
        txtNotifCancel.setVisibility(title != null ? View.VISIBLE : View.GONE);
    }

    private void setMessage(String message)
    {
        txtMessage.setText(message);
    }

    private void setMessageMarginTop()
    {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) txtMessage.getLayoutParams();
        params.topMargin = Utils.dpToPx(40);
        txtMessage.setLayoutParams(params);
    }

    private void setMessageTextColor( int color)
    {
        txtMessage.setTextColor(color);
    }

    private void hideTitle()
    {
        txtTitle.setVisibility(View.GONE);
    }

    private void hideMessage()
    {
        txtMessage.setVisibility(View.GONE);
    }

    private void setOnClickAcceptListener(OnClickListener listener)
    {
        this.listener = listener;
    }

    private void setOnClickCancelListener(OnClickListener listener)
    {
        this.listenerCancel = listener;
    }

    private void setDismissListener(SwipeToHideViewListener.SwipeToHideCompletionListener dismissListener)
    {
        this.dismissListener = dismissListener;
    }

    private void setOptions(ArrayList<View> options)
    {
        btnNotifContinue.setVisibility(View.GONE);
        txtNotifCancel.setVisibility(View.GONE);

        scrollLayoutOptions.setVisibility(View.VISIBLE);
        layoutOptions.removeAllViews();
        for (int i = 0; i < options.size(); i++)
        {
            layoutOptions.addView(options.get(i));
        }
    }

    private void animateShow()
    {
        Animation slide = AnimationUtils.loadAnimation(getContext(), R.anim.enter_from_bottom);
        layoutRound.startAnimation(slide);
        Animation slide2 = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_0_to_100);
        layoutBlur.startAnimation(slide2);
    }

    private Animation getAnimationHide()
    {
        Animation slide = AnimationUtils.loadAnimation(getContext(), R.anim.exit_to_bottom);
        return slide;
    }

    private void animateHide(Animation slide)
    {
        layoutRound.startAnimation(slide);
        Animation slide2 = AnimationUtils.loadAnimation(getContext(), R.anim.alpha_100_to_0);
        layoutBlur.startAnimation(slide2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (scrollLayoutOptions.getVisibility() == View.VISIBLE)
        {
            int height = (int) (ScreenUtils.getScreenHeight(getContext()) * 0.77);
            int scrollHeight = scrollLayoutOptions.getMeasuredHeight();

            if (scrollHeight > height)
            {
                scrollLayoutOptions.getLayoutParams().height = height;
            }
        }
    }


    /////ACTIONS


    public void showNotification(ViewGroup layoutToShow, String message)
    {
        showNotification(layoutToShow, message, null);
    }

    public void showNotification(ViewGroup layoutToShow, String message, View.OnClickListener listenerButton)
    {
        showNotification(layoutToShow, null, message, null, cancelText, null, null, true);
    }
    public void showNotification(ViewGroup layoutToShow, String title, String message, String titleButton, String titleCancelButton, View.OnClickListener listenerButton, View.OnClickListener listenerButtonCancel, boolean canHide)
    {
        boolean hasOtherNotification = false;
        if (notification != null) {
            if (notification.isShowing())
            {
                hasOtherNotification = true;
            }
            layoutToShow.removeView(notification);
            notification = null;
        }

        notification = new INotification(getContext());
        if (title != null) {
            notification.setTitle(title);
        }
        notification.setMessage(message);
        if (titleButton != null) {
            notification.setTitleButton(titleButton);
        }
        notification.setTitleCancelButton(titleCancelButton);

        notification.canHide = canHide;
        notification.setOnClickAcceptListener(listenerButton);
        notification.setOnClickCancelListener(listenerButtonCancel);
        notification.setDismissListener(new SwipeToHideViewListener.SwipeToHideCompletionListener() {
            @Override
            public void viewDismissed() {
                layoutToShow.removeView(notification);
                notification = null;
                if (listenerButtonCancel != null) {
                    listenerButtonCancel.onClick(null);
                }
            }
        });
        layoutToShow.addView(notification);
        if (!hasOtherNotification)
            notification.animateShow();
    }

    public void showOptionsNotification(ViewGroup layoutToShow, String title, String message, ArrayList<String> options, View.OnClickListener listener)
    {
        showOptionsNotification(layoutToShow, title, message, options, null, listener);
    }
    public void showOptionsNotification(ViewGroup layoutToShow, String title, String message, ArrayList<String> options, ArrayList<Integer> optionsTextColors, View.OnClickListener listener)
    {
        showOptionsNotification(layoutToShow, title, message, options, optionsTextColors, listener, true);
    }
    public void showOptionsNotification(ViewGroup layoutToShow, String title, String message, ArrayList<String> options, ArrayList<Integer> optionsTextColors, View.OnClickListener listener, boolean canHide)
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        ArrayList<View> views = new ArrayList<>();

        for (int i = 0; i < options.size(); i++)
        {
            String itemTitle = options.get(i);

            View view = inflater.inflate(R.layout.row_option, null);
            FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());
            TextView txtTitle = view.findViewById(R.id.txtTitle);
            txtTitle.setText(itemTitle);

            if (optionsTextColors != null && optionsTextColors.size() > i)
            {
                int color = optionsTextColors.get(i);
                txtTitle.setTextColor(color);
                ImageView imgArrow = view.findViewById(R.id.imgRight);
                imgArrow.setColorFilter(color);
            }

            views.add(view);
        }

        showOptionsViewNotification(layoutToShow, title, message, views, listener, canHide);
    }

    public void showOptionsViewNotification(ViewGroup layoutToShow, ArrayList<View> options, View.OnClickListener listener)
    {
        showOptionsViewNotification(layoutToShow, null, null, options, listener, true);
    }

    public void showOptionsViewNotification(ViewGroup layoutToShow, String title, String message, ArrayList<View> options, View.OnClickListener listener, boolean canHide)
    {
        //RelativeLayout layoutToShow = findViewById(R.id.mainBaseLayout);

        if (notification != null) {
            layoutToShow.removeView(notification);
            notification = null;
        }

        ArrayList<View> items = new ArrayList<>();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        View.OnClickListener onClickListenerOption = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hide(view, listener);
            }
        };

        for (int i = 0; i < options.size(); i++)
        {
            View view = options.get(i);

            view.setTag(i);
            view.setOnClickListener(onClickListenerOption);
            items.add(view);

            View separator = inflater.inflate(R.layout.row_separator, null);
            items.add(separator);
        }

        notification = new INotification(getContext());
        if (title == null)
            notification.hideTitle();
        else
            notification.setTitle(title);
        if (message == null)
            notification.hideMessage();
        else {
            if (title == null)
            {
                notification.setMessageMarginTop();
            }
            notification.setMessage(message);
            notification.setMessageTextColor(Utils.getColor(getContext(), R.color.black500));
        }

        notification.canHide = canHide;

        notification.setOptions(items);
        notification.setDismissListener(new SwipeToHideViewListener.SwipeToHideCompletionListener() {
            @Override
            public void viewDismissed() {
                layoutToShow.removeView(notification);
                notification = null;
            }
        });
        layoutToShow.addView(notification);
        notification.animateShow();
    }

    public void removeFrom(ViewGroup layoutToShow)
    {
        if (layoutToShow != null)
        {
            if (notification != null)
            {
                ViewGroup layoutParent = (ViewGroup) notification.getParent();
                if (layoutParent != null && layoutParent == layoutToShow)
                {
                    layoutToShow.removeView(notification);
                    notification = null;
                }
            }
        }
    }

    public void hide() {
        hide(null, null);
    }
    public void hide(View layoutClick, View.OnClickListener listenerClick)
    {
        if (notification != null)
        {
            Animation slide = notification.getAnimationHide();
            slide.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }
                @Override
                public void onAnimationEnd(Animation animation) {

                    ViewGroup layoutParent = (ViewGroup) notification.getParent();
                    if (layoutParent != null)
                    {
                        layoutParent.removeView(notification);
                    }

                    notification = null;

                    if (layoutClick != null && listenerClick != null)
                    {
                        listenerClick.onClick(layoutClick);
                    }
                }
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
            });
            notification.animateHide(slide);
        }
    }

    public boolean isShowing()
    {
        boolean res = false;

        if (notification != null)
        {
            ViewGroup layoutParent = (ViewGroup) notification.getParent();
            if (layoutParent != null) {
                res = true;
            }
        }

        return res;
    }
}
