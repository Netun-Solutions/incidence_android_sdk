package com.e510.commons.utils.banner;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import java.util.HashMap;

public class VXBannerManager
{
    public static final int NOTIFICATION_PUSH_ID = 10030;
    private final static String EXTRA_OPEN_FROM_NOFIFICATION = "APP_OPEN_FROM_NOTIFICATION";

    private static final String TAG = makeLogTag(VXBannerManager.class);
    private static VXBannerManager INSTANCE;
    private boolean isAnimatingNotification;

    public HashMap <String, String> pushOpenContent;

    public static VXBannerManager getInstance()
    {
        if (VXBannerManager.INSTANCE == null)
        {
            VXBannerManager.INSTANCE = new VXBannerManager();
        }

        return INSTANCE;
    }

    public View showNotification(Activity activity, String title, String subtitle, final VXBannerListener listener)
    {
        return showNotification(activity, title, subtitle, null, false, false, 0, 0, 0, false, listener);
    }
    public View showNotification(Activity activity, String title, String subtitle, Integer layoutId, boolean appearFromBottom, final VXBannerListener listener)
    {
        return showNotification(activity, title, subtitle, layoutId, appearFromBottom, false, 0, 0, 0, false, listener);
    }
    public View showNotification(Activity activity, String title, String subtitle, boolean hasImage, final VXBannerListener listener)
    {
        return showNotification(activity, title, subtitle, null, false, hasImage, 0, 0, 0, false, listener);
    }
    public View showNotification(Activity activity, String title, String subtitle, Integer layoutId, boolean appearFromBottom, boolean hasImage, int idBackgroundColor, int idTitleColor, int idSubtitleColor, boolean needVibrate, final VXBannerListener listener)
    {
        View notificationView = null;

        final RelativeLayout layout = activity.findViewById(R.id.layout_activity_main);

        if (layout != null)
        {
            if (layoutId == null)
                layoutId = R.layout.layout_notification;
            notificationView = LayoutInflater.from(activity).inflate(layoutId, layout, false);

            if (notificationView != null)
            {
                ImageView imageView = notificationView.findViewById(R.id.notif_image);
                TextView txtTitle = notificationView.findViewById(R.id.notif_title);
                TextView txtSubtitle = notificationView.findViewById(R.id.notif_subtitle);

                txtTitle.setText(title);
                txtSubtitle.setText(subtitle);

                if (!hasImage && imageView != null)
                {
                    imageView.setVisibility(View.GONE);
                }

                if (idBackgroundColor != 0)
                {
                    notificationView.setBackgroundColor(Utils.getColor(activity, idBackgroundColor));
                }

                if (idTitleColor != 0)
                {
                    txtTitle.setTextColor(Utils.getColor(activity, idTitleColor));
                }

                if (idSubtitleColor != 0)
                {
                    txtSubtitle.setTextColor(Utils.getColor(activity, idSubtitleColor));
                }


                showNotification(activity, notificationView, appearFromBottom, needVibrate, listener);
            }
        }

        return notificationView;
    }

    private void showNotification(Activity activity, final View notificationView, final boolean appearFromBottom, boolean needVibrate, final VXBannerListener listener)
    {
        if (!isAnimatingNotification)
        {
            try
            {
                isAnimatingNotification = true;
                final Handler handlerNotification = new Handler();
                final RelativeLayout layout = activity.findViewById(R.id.layout_activity_main);

                if (layout != null && notificationView != null)
                {
                    final Animation slideDown = appearFromBottom ? AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_down) : AnimationUtils.loadAnimation(activity, R.anim.slide_down);
                    final Animation slideUp = appearFromBottom ? AnimationUtils.loadAnimation(activity, R.anim.slide_bottom_up): AnimationUtils.loadAnimation(activity, R.anim.slide_up);

                    final Animation alphaTo0 = AnimationUtils.loadAnimation(activity, R.anim.alpha_100_to_0);

                    final GestureDetector mGestureDetector = new GestureDetector(activity, new CustomGestureListener(notificationView, CustomGestureListener.DIRECTION_VERTICAL)
                    {
                        @Override
                        public boolean onSwipeRight() {
                            return false;
                        }

                        @Override
                        public boolean onSwipeLeft() {
                            return false;

                        }

                        @Override
                        public boolean onTouch()
                        {
                            handlerNotification.removeCallbacksAndMessages(null);
                            notificationView.startAnimation(alphaTo0);

                            if (listener != null)
                            {
                                listener.onBannerClicked();
                            }

                            return false;
                        }

                        @Override
                        public boolean onSwipeUp()
                        {
                            if (!appearFromBottom)
                            {
                                handlerNotification.removeCallbacksAndMessages(null);
                                notificationView.startAnimation(slideUp);
                            }


                            return false;
                        }

                        @Override
                        public boolean onSwipeDown() {

                            if (appearFromBottom)
                            {
                                handlerNotification.removeCallbacksAndMessages(null);
                                notificationView.startAnimation(slideUp);
                            }

                            return false;
                        }
                    });

                    notificationView.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            return !mGestureDetector.onTouchEvent(event);
                        }
                    });

                    notificationView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            handlerNotification.removeCallbacksAndMessages(null);
                            notificationView.startAnimation(alphaTo0);

                            if (listener != null)
                            {
                                listener.onBannerClicked();
                            }
                        }
                    });

                    Animation.AnimationListener animationListener = new Animation.AnimationListener()
                    {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation){
                        }

                        @Override
                        public void onAnimationEnd(Animation animation)
                        {
                            if (animation == slideDown)
                            {
                                handlerNotification.postDelayed(new Runnable()
                                {
                                    public void run()
                                    {
                                        notificationView.startAnimation(slideUp);
                                    }
                                }, 2500);
                            }
                            else
                            {
                                layout.removeView(notificationView);
                                isAnimatingNotification = false;

                                if (listener != null)
                                {
                                    listener.onBannerClosed();
                                }
                            }
                        }
                    };

                    slideDown.setAnimationListener(animationListener);
                    slideUp.setAnimationListener(animationListener);
                    alphaTo0.setAnimationListener(animationListener);

                    layout.addView(notificationView);

                    // start the animation
                    notificationView.startAnimation(slideDown);

                    playSound(activity);

                    if (needVibrate)
                    {
                        Utils.playVibration(activity);
                    }
                }
                else
                {
                    isAnimatingNotification = false;
                }
            }
            catch (Exception e)
            {
                LogUtil.logE(TAG, e.getMessage());
                isAnimatingNotification = false;
            }
        }
    }

    private void playSound(Context context)
    {
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (am != null)
        {
            float actualVolume = (float) am.getStreamVolume(AudioManager.STREAM_SYSTEM);

            if (actualVolume > 0)
            {
                Utils.playResourceAudio(context, R.raw.notification);
            }
        }

    }

    public interface VXBannerListener
    {
        void onBannerClicked();
        void onBannerClosed();
    }

    //Notificaciones externas
    public void checkOpenPush(Bundle bundle)
    {
        pushOpenContent = null;
        if (bundle != null)
        {
            pushOpenContent = (HashMap<String, String>) bundle.get(EXTRA_OPEN_FROM_NOFIFICATION);
        }
    }

    public void cleanPushNotifications(Context context)
    {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_PUSH_ID);
    }

    public void showPushNotification(Application application, String title, String message, HashMap<String, String> data, Class activityClass)
    {
        String name = application.getString(R.string.default_notification_channel_name);
        String id = application.getString(R.string.default_notification_channel_id); // The user-visible name of the channel.
        String description = application.getString(R.string.default_notification_channel_description); // The user-visible description of the channel.

        Intent intent = new Intent(application.getBaseContext(), activityClass);
        putIntentExtraNotification(intent, data);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

        VXNotificationManager.showPush(application, intent, title, message, name, id, description, R.drawable.ic_notification, NOTIFICATION_PUSH_ID);
    }

    public void putIntentExtraNotification(Intent intent, HashMap<String, String> data)
    {
        if (intent != null && data != null)
            intent.putExtra(EXTRA_OPEN_FROM_NOFIFICATION, data);
    }
}
