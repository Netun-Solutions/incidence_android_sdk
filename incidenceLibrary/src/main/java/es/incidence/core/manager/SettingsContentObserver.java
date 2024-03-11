package es.incidence.core.manager;

import android.content.Context;
import android.database.ContentObserver;
import android.media.AudioManager;
import android.os.Handler;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;

public class SettingsContentObserver extends ContentObserver {

    private String TAG = SettingsContentObserver.class.getSimpleName();

    private Context context;

    public SettingsContentObserver(Handler handler, Context context) {
        super(handler);
        this.context = context;
    }

    @Override
    public boolean deliverSelfNotifications() {
        return super.deliverSelfNotifications();
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Log.v(TAG, "Settings change detected " + selfChange);
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        Log.d(TAG, "Change Volumen to: " + currentVolume);

        EventBus.getDefault().post(new Event(EventCode.VOLUMEN_CHANGED));
    }
}
