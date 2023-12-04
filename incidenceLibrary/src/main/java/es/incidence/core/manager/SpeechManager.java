package es.incidence.core.manager;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;

import com.sac.speech.GoogleVoiceTypingDisabledException;
import com.sac.speech.Speech;
import com.sac.speech.SpeechDelegate;
import com.sac.speech.SpeechRecognitionNotAvailable;

import java.util.List;

import es.incidence.core.Core;

public class SpeechManager implements SpeechDelegate, Speech.stopDueToDelay {

    private static final String TAG = makeLogTag(SpeechManager.class);

    public static boolean isStopping = false;
    public static boolean isEnabled = false;
    private SpeechManagerListener listener;
    private Context context;

    public void onInit(Context context) {
        this.context = context;
        Speech.init(this.context);
    }

    public void startRecognizing(Context context) {
        SpeechManager.isStopping = false;
        Speech.getInstance().setListener(this);

        try {
            Speech.getInstance().stopTextToSpeech();
            Speech.getInstance().setLocale(Core.getLocaleLanguage());
            //Speech.getInstance().setStopListeningAfterInactivity(100000);//10000
            //Speech.getInstance().setTransitionMinimumDelay(100000);//1200
            //Speech.getInstance().setGetPartialResults(false);
            Speech.getInstance().startListening(null, this);
            SpeechManager.isEnabled = true;
        } catch (SpeechRecognitionNotAvailable exc) {
            if (listener != null) {
                listener.onError();
            }
            unmuteBeepSoundOfRecorder();
            SpeechManager.isEnabled = false;
        } catch (GoogleVoiceTypingDisabledException exc) {
            if (listener != null) {
                listener.onError();
            }
            unmuteBeepSoundOfRecorder();
            SpeechManager.isEnabled = false;
        }
        muteBeepSoundOfRecorder();

    }

    public void setUpListener(SpeechManagerListener listener) {
        this.listener = listener;
    }

    public void stop() {
        SpeechManager.isStopping = true;
        if (Speech.getInstance().isListening()) {
            Speech.getInstance().stopListening();
            unmuteBeepSoundOfRecorder();
        }
    }

    public static boolean hasRecordAudioPermission(Context context) {
        if (Build.VERSION.SDK_INT >= 29) {
            return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == 0;
        } else {
            return hasPermission(context);
        }
    }

    public static boolean hasPermission(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == 0;
    }

    public static void requestPermission(int code, Activity activity) {
        if (!hasPermission(activity)) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.RECORD_AUDIO}, code);
        }
    }

    public static void destroy() {
        if (Speech.getInstanceNoExc() != null) {
            Speech.getInstanceNoExc().shutdown();
        }
    }

    @Override
    public void onSpecifiedCommandPronounced(String event) {
        if (SpeechManager.isEnabled && !SpeechManager.isStopping) {
            startRecognizing(this.context);
        } else {
            unmuteBeepSoundOfRecorder();
        }
    }

    @Override
    public void onStartOfSpeech() {
    }

    @Override
    public void onSpeechRmsChanged(float value) {
    }

    @Override
    public void onSpeechPartialResults(List<String> results) {
    }

    @Override
    public void onSpeechResult(String result) {
        if (listener != null && !SpeechManager.isStopping) {
            listener.onResults(result);
        }
    }

    private void muteBeepSoundOfRecorder() {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_MUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_MUTE, 0);
                //audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_MUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "muteBeepSoundOfRecorder");
        }
    }

    public void unmuteBeepSoundOfRecorder() {
        try {
            AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);

            if (audioManager != null) {
                audioManager.adjustStreamVolume(AudioManager.STREAM_NOTIFICATION, AudioManager.ADJUST_UNMUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_ALARM, AudioManager.ADJUST_UNMUTE, 0);
                //audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_UNMUTE, 0);
                audioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
            }
        } catch (Exception e) {
            Log.e(TAG, "unmuteBeepSoundOfRecorder");
        }
    }
}
