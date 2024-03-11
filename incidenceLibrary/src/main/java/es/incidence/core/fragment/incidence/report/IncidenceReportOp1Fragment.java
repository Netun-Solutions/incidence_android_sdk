package es.incidence.core.fragment.incidence.report;

import static android.app.Activity.RESULT_OK;
import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.GradientDrawable;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import com.e510.commons.activity.BaseActivity;
import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;
import com.e510.location.LocationManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.IncidenceType;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.manager.SpeechManager;
import es.incidence.core.manager.SpeechManagerListener;
import es.incidence.core.manager.incidence.IncidenceManager;
import es.incidence.core.utils.IUtils;
import es.incidence.core.utils.view.INavigation;
import es.incidence.library.IncidenceLibraryManager;

public class IncidenceReportOp1Fragment extends IFragment implements SpeechManagerListener {

    private static final String TAG = makeLogTag(IncidenceReportOp1Fragment.class);
    public static final String KEY_VEHICLE = "KEY_VEHICLE";
    public static final String KEY_VEHICLE_TMP = "KEY_VEHICLE_TMP";
    public static final String KEY_USER = "KEY_USER";
    public static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
    public static final String KEY_FLOW_COMPLETE = "KEY_FLOW_COMPLETE";

    //params
    public Vehicle vehicle;
    public Vehicle vehicleTmp;
    public User user;
    public boolean openFromNotification;
    public boolean flowComplete;
    public boolean holdSpeech = false;

    public RelativeLayout layoutNavRight;
    public TextView txtNavTitleRight;
    public ImageView imgNavTitleRight;
    public ImageView imgNavTitleSecondRight;
    public INavigation navigation;

    public RelativeLayout layoutContent;

    public TextView btnCancel;
    public Button btnRed;
    public Button btnBlue;
    public RelativeLayout layoutBtnRed;
    public RelativeLayout layoutBtnBlue;

    private CountDownTimer countDownTimer;
    public ArrayList<String> voiceDialogs;
    public ArrayList<String> speechRecognizion;
    private TextToSpeech textToSpeech;
    public SpeechManager speechManager;

    private CardView alertVolumeErrorContainer;
    private TextView alertVolumeErrorTitle;
    private TextView alertVolumeErrorSubTitle;
    private ImageView alertVolumeErrorImgClose;

    private CardView alertTimeErrorContainer;
    private boolean alertTimeErrorContainerHided = false;
    protected boolean isShowingFragment = false;

    private TextView alertTimeErrorTitle;
    private TextView alertTimeErrorSubTitle;
    private TextView alertTimeErrorSubTitleDesc;
    private ImageView alertTimeErrorImgClose;

    private CardView alertDgtContainer;
    private TextView alertDgtTitle;
    private TextView alertDgtSubTitle;
    private ImageView alertDgtImgClose;
    private ImageView alertDgtImg;

    private CountDownTimer countDownTimerRepeatVoice;

    public static IncidenceReportOp1Fragment newInstance(Vehicle vehicle, User user, boolean openFromNotification, Boolean flowComplete) {
        return newInstance(vehicle, null, user, openFromNotification, flowComplete);
    }
    public static IncidenceReportOp1Fragment newInstance(Vehicle vehicle, Vehicle vehicleTmp, User user, boolean openFromNotification, Boolean flowComplete)
    {
        IncidenceReportOp1Fragment fragment = new IncidenceReportOp1Fragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_VEHICLE, vehicle);
        bundle.putParcelable(KEY_VEHICLE_TMP, vehicleTmp);
        bundle.putParcelable(KEY_USER, user);
        bundle.putBoolean(KEY_NOTIFICATION, openFromNotification);
        bundle.putBoolean(KEY_FLOW_COMPLETE, flowComplete);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IUtils.keepScreenOn(getActivity());
        if (getArguments() != null) {
            vehicle = getArguments().getParcelable(KEY_VEHICLE);
            //vehicleTmp = getArguments().getParcelable(KEY_VEHICLE_TMP);
            user = getArguments().getParcelable(KEY_USER);
            openFromNotification = getArguments().getBoolean(KEY_NOTIFICATION);
            flowComplete = getArguments().getBoolean(KEY_FLOW_COMPLETE);
        }
    }

    @Override
    public void onResume() {
        isShowingFragment = true;
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        IUtils.keepScreenOff(getActivity());
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        IncidenceManager.alertTimeErrorContainerCall = false;

        speechStop();
        super.onDestroy();
    }

    @Override
    public int getTitleId() {
        return R.string.incidence_key_report_incidence;
    }

    @Override
    public int getLayoutRootId() {
        return R.id.layoutRootIncidenceReport;
    }

    @Override
    public boolean needEventBus() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_incidence_report_op1, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void onBecomeFrontFromBackPressed() {
        super.onBecomeFrontFromBackPressed();
        if (SpeechManager.isEnabled) {
            startSpeech(false);
        }
        setUpSpeechButton();
        setUpVolumeAlert();

        isShowingFragment = true;
    }

    public void speechStop() {
        layoutContent.setKeepScreenOn(false);
        SpeechManager.isStopping = true;
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechManager != null) {
            speechManager.stop();
            speechManager.unmuteBeepSoundOfRecorder();
        }
        if (countDownTimerRepeatVoice != null) {
            countDownTimerRepeatVoice.cancel();
        }

        IncidenceManager.secondsGlobal = 0;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        layoutNavRight = rootView.findViewById(R.id.layoutNavRight);
        imgNavTitleRight = rootView.findViewById(R.id.imgNavTitleRight);
        txtNavTitleRight = rootView.findViewById(R.id.txtNavTitleRight);
        imgNavTitleSecondRight = rootView.findViewById(R.id.imgNavTitleSecondRight);

        FontUtils.setTypeValueText(txtNavTitleRight, Constants.FONT_SEMIBOLD, getContext());

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, getString(getTitleId()), true);

        layoutContent = rootView.findViewById(R.id.layoutContent);

        Integer textColor = IncidenceLibraryManager.instance.getTextColor();

        btnCancel = rootView.findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickCancel();
            }
        });
        FontUtils.setTypeValueText(btnCancel, Constants.FONT_SEMIBOLD, getContext());
        btnRed = rootView.findViewById(R.id.btnRed);
        btnRed.setText(getString(R.string.incidence_key_fault));
        FontUtils.setTypeValueText(btnRed, Constants.FONT_SEMIBOLD, getContext());
        btnRed.setTextColor(textColor);
        btnRed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBlue();
            }
        });
        btnBlue = rootView.findViewById(R.id.btnBlue);
        btnBlue.setText(getString(R.string.incidence_key_accident));
        FontUtils.setTypeValueText(btnBlue, Constants.FONT_SEMIBOLD, getContext());
        btnBlue.setTextColor(textColor);
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickRed();
            }
        });

        layoutBtnBlue = rootView.findViewById(R.id.layoutBtnBlue);
        layoutBtnRed = rootView.findViewById(R.id.layoutBtnRed);
        GradientDrawable drawableBlue = (GradientDrawable) layoutBtnBlue.getBackground();
        drawableBlue.setStroke(Utils.dpToPx(1), textColor);

        GradientDrawable drawableRed = (GradientDrawable) layoutBtnRed.getBackground();
        drawableRed.setStroke(Utils.dpToPx(1), textColor);


        layoutNavRight.setVisibility(View.VISIBLE);
        imgNavTitleSecondRight.setVisibility(View.VISIBLE);
        imgNavTitleSecondRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                microButtonClick();
            }
        });
        speechManager = new SpeechManager();
        speechManager.onInit(getContext());
        speechManager.setUpListener(this);

        //---
        alertVolumeErrorContainer = rootView.findViewById(R.id.alertVolumeErrorContainer);
        alertVolumeErrorTitle = rootView.findViewById(R.id.alertVolumeErrorTitle);
        alertVolumeErrorSubTitle = rootView.findViewById(R.id.alertVolumeErrorSubTitle);
        alertVolumeErrorImgClose = rootView.findViewById(R.id.alertVolumeErrorImgClose);
        FontUtils.setTypeValueText(alertVolumeErrorTitle, Constants.FONT_SEMIBOLD, getContext());
        FontUtils.setTypeValueText(alertVolumeErrorSubTitle, Constants.FONT_REGULAR, getContext());
        alertVolumeErrorImgClose.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            microButtonClick();
                                                        }
                                                    }
        );

        alertTimeErrorContainer = rootView.findViewById(R.id.alertTimeErrorContainer);
        alertTimeErrorTitle = rootView.findViewById(R.id.alertTimeErrorTitle);
        alertTimeErrorSubTitle = rootView.findViewById(R.id.alertTimeErrorSubTitle);
        alertTimeErrorSubTitleDesc = rootView.findViewById(R.id.alertTimeErrorSubTitleDesc);
        alertTimeErrorImgClose = rootView.findViewById(R.id.alertTimeErrorImgClose);
        FontUtils.setTypeValueText(alertTimeErrorTitle, Constants.FONT_SEMIBOLD, getContext());
        FontUtils.setTypeValueText(alertTimeErrorSubTitle, Constants.FONT_REGULAR, getContext());
        FontUtils.setTypeValueText(alertTimeErrorSubTitleDesc, Constants.FONT_SEMIBOLD, getContext());
        alertTimeErrorImgClose.setOnClickListener(new View.OnClickListener() {
                                                      @Override
                                                      public void onClick(View view) {
                                                          //microButtonClick();
                                                          alertTimeErrorContainerHided = true;
                                                          alertTimeErrorContainer.setVisibility(View.GONE);
                                                      }
                                                  }
        );

        alertDgtContainer = rootView.findViewById(R.id.alertDgtContainer);
        alertDgtTitle = rootView.findViewById(R.id.alertDgtTitle);
        alertDgtSubTitle = rootView.findViewById(R.id.alertDgtSubTitle);
        alertDgtImgClose = rootView.findViewById(R.id.alertDgtImgClose);
        alertDgtImg = rootView.findViewById(R.id.imgDgtDesc);
        FontUtils.setTypeValueText(alertDgtTitle, Constants.FONT_SEMIBOLD, getContext());
        FontUtils.setTypeValueText(alertDgtSubTitle, Constants.FONT_REGULAR, getContext());
        alertDgtImgClose.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    //microButtonClick();
                                                    alertDgtContainer.setVisibility(View.GONE);
                                                }
                                            }
        );
        alertDgtImg.setImageDrawable(Utils.getDrawable(getContext(), R.drawable.ico_conection));
        DrawableCompat.setTint(alertDgtImg.getDrawable(), Utils.getColor(getContext(), R.color.colorPrimary));
        alertDgtContainer.setVisibility(Constants.FLAG_INCIDENCE_DGT ? View.VISIBLE : View.GONE);
        //---

        setUpSpeechButton();
        setUpVolumeAlert();
        setUpTimeAlert();
        setUpVoiceLiterals();
        if (!holdSpeech) {
            if (SpeechManager.isEnabled) {
                startSpeech(false);
            }
        }
        activateLocation();
        addContent();
    }

    private void activateLocation()
    {
        if (!LocationManager.hasPermission(getContext())) {
            //A partir de Api 30 no se puede solicitar directamente el Always, sino no sale la alerta
            if (Build.VERSION.SDK_INT >= 30) {
                LocationManager.requestPermission(BaseActivity.PERMISSION_LOCATION_REQUEST_CODE, getBaseActivity());
            }
            else {
                LocationManager.requestPermissionWithBackground(BaseActivity.PERMISSION_LOCATION_REQUEST_CODE, getBaseActivity());
            }
        }
    }

    private void microButtonClick() {
        SpeechManager.isEnabled = !SpeechManager.isEnabled;
        if (SpeechManager.isEnabled) {
            if (SpeechManager.hasRecordAudioPermission(getContext())) {
                SpeechManager.isEnabled = true;
                startSpeech(false);
                setUpSpeechButton();
                setUpVolumeAlert();
            } else {
                SpeechManager.requestPermission(Constants.PERMISSION_RECORD_AUDIO_REQUEST_CODE, getBaseActivity());
            }
        } else {
            SpeechManager.isEnabled = false;
            speechStop();
            setUpSpeechButton();
            setUpVolumeAlert();
        }
    }

    public void setUpVoiceLiterals() {
        speechRecognizion = new ArrayList<String>();
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_one", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_accident", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_two", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_fault", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_three", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_cancel", getContext()));

        voiceDialogs = new ArrayList<String>();
        voiceDialogs.add(Core.getLiteralVoiceSDK("incidence_key_report_ask_what2", getContext()));
        String timeText = Core.getLiteralVoiceSDK("incidence_key_report_ask_what_descrip", getContext());
        timeText = String.format(timeText, "3");
        voiceDialogs.add(timeText);
        voiceDialogs.addAll(speechRecognizion);
    }

    public void startSpeech(boolean notUnderstand) {
        startSpeech(notUnderstand, false, null);
    }
    public void startSpeech(boolean notUnderstand, boolean emergency, String emergencyMessage) {

        layoutContent.setKeepScreenOn(true);

        if (speechManager != null) {
            speechManager.stop();
        }

        AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        if (volume == 0) {
            startVoiceRecognition();
        } else {
            if ((textToSpeech != null && !textToSpeech.isSpeaking()) || textToSpeech == null) {
                textToSpeech = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
                    @Override
                    public void onInit(int status) {
                        if (status == TextToSpeech.SUCCESS) {
                            //  int result = textToSpeech.setLanguage(getResources().getConfiguration().locale);
                            int result = textToSpeech.setLanguage(Core.getLocaleLanguage());

                            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                                startVoiceRecognition();
                            } else {
                                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                                    @Override
                                    public void onStart(String utteranceId) {
                                    }

                                    @Override
                                    public void onDone(String utteranceId) {
                                        if (utteranceId.equals(TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED)) {
                                            startVoiceRecognition();
                                        }
                                    }

                                    @Override
                                    public void onError(String utteranceId) {
                                        startVoiceRecognition();
                                    }
                                    public void onError(String utteranceId, int errorCode) {
                                        onError(utteranceId);
                                    }
                                });
                                speechManager.unmuteBeepSoundOfRecorder();
                                if (notUnderstand) {
                                    textToSpeech.speak(Core.getLiteralVoiceSDK("incidence_key_not_understand_voice", getContext()), TextToSpeech.QUEUE_FLUSH, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                                } else if (emergency) {
                                    textToSpeech.speak(emergencyMessage, TextToSpeech.QUEUE_FLUSH, null, null);
                                } else {
                                    for (int i = 0; i < voiceDialogs.size(); i++) {
                                        if (i == 0) {
                                            textToSpeech.speak(voiceDialogs.get(i), TextToSpeech.QUEUE_FLUSH, null, null);
                                        } else if (i == voiceDialogs.size() - 1) {
                                            textToSpeech.speak(voiceDialogs.get(i), TextToSpeech.QUEUE_ADD, null, TextToSpeech.ACTION_TTS_QUEUE_PROCESSING_COMPLETED);
                                        } else {
                                            textToSpeech.speak(voiceDialogs.get(i), TextToSpeech.QUEUE_ADD, null, null);
                                        }
                                    }
                                }
                            }
                        } else {
                            startVoiceRecognition();
                        }
                    }
                });
            } else {
                Log.e(TAG, "startSpeech: else");
            }
        }
    }

    private void startVoiceRecognition() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (textToSpeech != null) {
                    textToSpeech.stop();
                }
                speechManager.startRecognizing(getContext());
                startCountDownTimerRepeatVoice();
            }
        });
    }

    private void startCountDownTimerRepeatVoice() {
        //String repeatVoice = Core.loadData(Constants.KEY_CONFIG_REPEAT_VOICE);
        String repeatVoice = "15";
        if (repeatVoice != null) {
            if (countDownTimerRepeatVoice != null) {
                countDownTimerRepeatVoice.cancel();
            }

            int repeatVoiceNum = Integer.parseInt(repeatVoice);
            countDownTimerRepeatVoice = new CountDownTimer(1000 * repeatVoiceNum, 1000) {

                public void onTick(long millisUntilFinished) {
                    Context context = getContext();
                    if (context != null) {
                        long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                        String time = String.format("00:%02d", seconds);
                        long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                        if (minutes > 0) {
                            time = String.format("%02d:%02d", minutes, seconds);
                        }

                        String timeText = context.getString(R.string.incidence_key_report_ask_what_descrip, time);
                        LogUtil.logE(TAG, "onTickRepeatVoice: " + timeText);

                        if (seconds == 0) {
                            speechManager.stop();
                            startSpeech(false);
                        }
                    }
                }

                public void onFinish() {
                    //speechManager.stop();
                    //startSpeech(false);
                }
            };
            countDownTimerRepeatVoice.start();
        }
    }

    private void setUpSpeechButton() {
        if (SpeechManager.isEnabled) {
            imgNavTitleSecondRight.setImageDrawable(Utils.getDrawable(getContext(), R.drawable.ic_nav_micro_on));
        } else {
            imgNavTitleSecondRight.setImageDrawable(Utils.getDrawable(getContext(), R.drawable.ic_nav_micro_off));
        }
    }

    private void setUpVolumeAlert() {
        if (SpeechManager.isEnabled) {
            AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
            int volume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
            int minVolumeNeeded = maxVolume / 4;
            if (volume < minVolumeNeeded) {
                alertVolumeErrorContainer.setVisibility(View.VISIBLE);
            } else {
                alertVolumeErrorContainer.setVisibility(View.GONE);
            }
        } else {
            alertVolumeErrorContainer.setVisibility(View.GONE);
        }
    }

    protected void setUpTimeAlert() {
        if (IncidenceManager.secondsGlobal != 0 && IncidenceManager.secondsGlobal<=30) {
            String secondsText = getContext().getString(R.string.incidence_key_alert_llamada_emergencias, IncidenceManager.secondsGlobal);
            if (!alertTimeErrorContainerHided) {
                alertTimeErrorContainer.setVisibility(View.VISIBLE);

                IUtils.vibrate(getContext());
                if (!IncidenceManager.alertTimeErrorContainerCall && isShowingFragment && SpeechManager.isEnabled) {
                    IncidenceManager.alertTimeErrorContainerCall = true;

                    speechStop();

                    startSpeech(false, true, secondsText);
                    startCountDownTimerRepeatVoice();
                }
            }
            alertTimeErrorSubTitleDesc.setText(secondsText);
        } else {
            alertTimeErrorContainer.setVisibility(View.GONE);
        }
    }

    public void addContent()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_incidence_report_ask_op1, null);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        TextView txtTitle = view.findViewById(R.id.txtTitle);
        FontUtils.setTypeValueText(txtTitle, Constants.FONT_SEMIBOLD, getContext());

        TextView txtDescription = view.findViewById(R.id.txtDescription);


        txtDescription.setText(getContext().getString(R.string.incidence_key_report_ask_what_descrip, "03:00"));
        IncidenceManager.secondsGlobal = 0;
        countDownTimer = new CountDownTimer(60000 * 3, 1000) {

            public void onTick(long millisUntilFinished) {
                long seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                String time = String.format("00:%02d", seconds);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                if (minutes > 0)
                {
                    time = String.format("%02d:%02d", minutes, seconds);
                } else {
                    IncidenceManager.secondsGlobal = seconds;
                    EventBus.getDefault().post(new Event(EventCode.INCICENDE_TIME_CHANGED));
                }
                setUpTimeAlert();

                String timeText = getContext().getString(R.string.incidence_key_report_ask_what_descrip, time);
                LogUtil.logE(TAG, "onTick: " + timeText + " - " + seconds);
                txtDescription.setText(timeText);

                if (minutes == 0 && seconds == 0) {
                    txtDescription.setText(getContext().getString(R.string.incidence_key_report_ask_what_descrip, "00:00"));

                    EventBus.getDefault().post(new Event(EventCode.INCICENDE_TIME_STOP));

                    if (SpeechManager.isEnabled) startSpeech(false, true, getContext().getString(R.string.incidence_key_calling_emergency));
                    reportIncidence(Constants.ACCIDENT_TYPE_WOUNDED+"", Constants.PHONE_EMERGENCY);
                }
            }

            public void onFinish()
            {
                txtDescription.setText(getContext().getString(R.string.incidence_key_report_ask_what_descrip, "00:00"));
            }
        };
        countDownTimer.start();

        layoutContent.addView(view);
    }

    public void onClickCancel()
    {
        speechStop();
        //mListener.cleanAllBackStackEntries();

        alertTimeErrorContainer.setVisibility(View.GONE);
        alertVolumeErrorContainer.setVisibility(View.GONE);

        getActivity().finish();
    }

    public void dgtAlertUpdatedView() {
        alertDgtContainer.setVisibility(Constants.FLAG_INCIDENCE_DGT ? View.VISIBLE : View.GONE);
    }

    public void onClickBlue()
    {
        speechStop();
        isShowingFragment = false;

        int parent = Constants.FAULT; //Avería es 2
        ArrayList<IncidenceType> list = Core.getIncidencesTypes(parent);

        if (list.size() == 0) {
            reportIncidenceSdk(parent, user.phone);
        } else {
            mListener.addFragmentAnimated(FaultFragment.newInstance(parent, vehicle, user, openFromNotification));
        }
    }

    public void onClickRed()
    {
        speechStop();

        int parent = Constants.ACCIDENT; //Accidente es 1
        ArrayList<IncidenceType> list = Core.getIncidencesTypes(parent);
        if (list.size() == 0) {
            reportIncidenceSdk(parent, user.phone);
        } else {
            mListener.addFragmentAnimated(AccidentFragment.newInstance(vehicle, user, openFromNotification));
        }
    }

    @Override
    public void onResults(String string) {
        String match = "";
        Log.e(TAG, "onResults: " + string);
        if (!string.isEmpty()) {
            for (int i = 0; i < speechRecognizion.size(); i++) {

                String listValue = speechRecognizion.get(i).toLowerCase().replace(" ", "");
                String stringValue = string.toLowerCase().replace(" ", "");

                if (listValue.equals(stringValue)) {
                    match = stringValue;
                }
            }
        }

        if (!match.isEmpty()) {
            voiceRecognizionMatch(match);
        } else if (!string.isEmpty() && !SpeechManager.isStopping) {
            String text =  Core.getLiteralVoiceSDK("incidence_key_not_understand_voice", getContext()).toLowerCase().replace(",", "");
            if (string.toLowerCase().equals(text)) {
                startVoiceRecognition();
            } else {
                speechManager.unmuteBeepSoundOfRecorder();
                speechManager.stop();
                startCountDownTimerRepeatVoice();
                startSpeech(true);
            }

        }
    }

    public void voiceRecognizionMatch(String string) {
        if (Core.getLiteralVoiceSDK("incidence_key_one", getContext()).toLowerCase().equals(string) || Core.getLiteralVoiceSDK("incidence_key_fault", getContext()).toLowerCase().equals(string)) {
            onClickBlue();
        } else if (Core.getLiteralVoiceSDK("incidence_key_two", getContext()).toLowerCase().equals(string) || Core.getLiteralVoiceSDK("incidence_key_accident", getContext()).toLowerCase().equals(string)) {
            onClickRed();
        } else if (Core.getLiteralVoiceSDK("incidence_key_three", getContext()).toLowerCase().equals(string) || Core.getLiteralVoiceSDK("incidence_key_cancel", getContext()).toLowerCase().equals(string)) {
            onClickCancel();
        }
    }


    public String getNumberName(int index) {
        switch (index){
            case 1:
                return Core.getLiteralVoiceSDK("incidence_key_one", getContext()).toLowerCase();
            case 2:
                return Core.getLiteralVoiceSDK("incidence_key_two", getContext()).toLowerCase();
            case 3:
                return Core.getLiteralVoiceSDK("incidence_key_three", getContext()).toLowerCase();
            case 4:
                return Core.getLiteralVoiceSDK("incidence_key_four", getContext()).toLowerCase();
            case 5:
                return Core.getLiteralVoiceSDK("incidence_key_five", getContext()).toLowerCase();
            case 6:
                return Core.getLiteralVoiceSDK("incidence_key_six", getContext()).toLowerCase();
            case 7:
                return Core.getLiteralVoiceSDK("incidence_key_seven", getContext()).toLowerCase();
            case 8:
                return Core.getLiteralVoiceSDK("incidence_key_eight", getContext()).toLowerCase();
            case 9:
                return Core.getLiteralVoiceSDK("incidence_key_nine", getContext()).toLowerCase();
        }
        return "";
    }

    public Integer getNumberValue(String literal) {
        if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_one", getContext()).toLowerCase())) {
            return 1;
        } else if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_two", getContext()).toLowerCase())) {
            return 2;
        } else if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_three", getContext()).toLowerCase())) {
            return 3;
        } else if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_four", getContext()).toLowerCase())) {
            return 4;
        } else if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_five", getContext()).toLowerCase())) {
            return 5;
        } else if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_six", getContext()).toLowerCase())) {
            return 6;
        } else if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_seven", getContext()).toLowerCase())) {
            return 7;
        } else if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_eight", getContext()).toLowerCase())) {
            return 8;
        } else if (literal.equals(Core.getLiteralVoiceSDK("incidence_key_nine", getContext()).toLowerCase())) {
            return 9;
        }
        return null;
    }

    @Override
    public void onError() {
        speechStop();
        setUpSpeechButton();
        setUpVolumeAlert();
    }

    @Override
    public void onBaseRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        if (requestCode == Constants.PERMISSION_RECORD_AUDIO_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                SpeechManager.isEnabled = true;
                setUpSpeechButton();
                setUpVolumeAlert();
                startSpeech(false);
            } else {
                SpeechManager.isEnabled = false;
                setUpSpeechButton();
                setUpVolumeAlert();
                showAlert(R.string.incidence_key_alert_need_audio_permission_description);
            }
        }
    }

    protected void reportIncidence(String idIncidence, String phone)
    {
        if (LocationManager.hasPermission(getContext()))
        {
            showHud();
            /*
            if (Core.manualAddressSearchResult != null)
            {
                Location location = new Location("");
                location.setLatitude(Core.manualAddressSearchResult.getCoordinate().latitude());
                location.setLongitude(Core.manualAddressSearchResult.getCoordinate().longitude());
                location.setAltitude(Core.manualAddressSearchResult.getCoordinate().altitude());
                location.setAccuracy(0);
                location.setSpeed(0);

                reportLocation(idIncidence, phone, location);
            }
            else
            {
                LocationManager.getLocation(getContext(), new LocationManager.LocationListener() {
                    @Override
                    public void onLocationResult(Location location) {

                        if (location != null)
                        {
                            reportLocation(idIncidence, phone, location);
                        }
                        else
                        {
                            hideHud();
                            showAlert(R.string.incidence_key_activate_location_message);
                        }
                    }
                });
            }
            */
            LocationManager.getLocation(getContext(), new LocationManager.LocationListener() {
                @Override
                public void onLocationResult(Location location) {

                    if (location != null)
                    {
                        reportLocation(idIncidence, phone, location);
                    }
                    else
                    {
                        hideHud();
                        showAlert(R.string.incidence_key_activate_location_message);
                    }
                }
            });
        }
        else
        {
            showAlert(R.string.incidence_key_activate_location_message);
        }
    }

    protected void reportIncidenceSdk(int idIncidence, String phone) {
        IncidenceType incidenceType = new IncidenceType();
        //incidenceType.id = idIncidence;
        //incidenceType.externalId = "B30";
        incidenceType.externalId = idIncidence+"";

        Incidence incidence = new Incidence();
        incidence.incidenceType = incidenceType;
        //incidence.street = "Carrer Major, 2";
        //incidence.city = "Barcelona";
        //incidence.country = "España";
        //incidence.latitude = 41.4435945;
        //incidence.longitude = 2.2319534;
        //incidence.externalIncidenceId = externalIncidenceId;

        IncidenceLibraryManager.instance.createIncidenceFunc(user, vehicle, incidence, response -> {
            if (response.isSuccess()) {
                //MAKE OK ACTIONS
                Log.d(TAG, "SUCCESS");

                Intent data = new Intent();
                data.putExtra("incidence", incidence);
                getActivity().setResult(RESULT_OK, data);
            } else {
                //MAKE KO ACTIONS
                Log.d(TAG, "ERROR: " + response.message);
            }
            getActivity().finish();
        });
    }

    protected void reportLocation(String idIncidence, String phone, Location location)
    {
        String licensePlate = "", street = "", city = "", country = "";
        if (vehicle != null) {
            licensePlate = vehicle.licensePlate;
        } else if (vehicleTmp != null) {
            licensePlate = vehicleTmp.licensePlate;
        }

        IncidenceType incidenceType = new IncidenceType();
        //incidenceType.id = idIncidence;
        //incidenceType.externalId = "B30";
        incidenceType.externalId = idIncidence+"";

        Incidence incidence = new Incidence();
        incidence.incidenceType = incidenceType;
        incidence.street = street;
        incidence.city = city;
        incidence.country = country;
        incidence.latitude = location.getLatitude();
        incidence.longitude = location.getLongitude();
        //incidence.externalIncidenceId = externalIncidenceId;

        Api.postIncidenceSdk(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {
                hideHud();
                if (response.isSuccess())
                {
                    if (incidence != null) {
                        try {
                            JSONObject jsonObject = response.get();
                            JSONObject incidenceObject = jsonObject.getJSONObject("incidence");
                            int id = incidenceObject.getInt("id");
                            String externalIncidenceTypeId = incidenceObject.getString("externalIncidenceTypeId");
                            incidence.id = id;
                            incidence.externalIncidenceId = externalIncidenceTypeId;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        Intent data = new Intent();
                        data.putExtra("incidence", incidence);
                        getActivity().setResult(RESULT_OK, data);
                    }
                    getActivity().finish();
                }
                else
                {
                    onBadResponse(response);
                }
            }
        }, user, vehicle, incidence);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event) {
        if (event.code == EventCode.VOLUMEN_CHANGED) {
            setUpVolumeAlert();
        } else if (event.code == EventCode.INCICENDE_TIME_STOP) {
            if (countDownTimer != null) {
                countDownTimer.cancel();
            }
            speechStop();
            IncidenceManager.secondsGlobal = 0;
        } else if (event.code == EventCode.INCICENDE_VEHICLE_SELECTED) {
            vehicle = (Vehicle) event.object;
        } else if (event.code == EventCode.INCICENDE_DGT_UPDATED) {
            dgtAlertUpdatedView();
        }
    }
}
