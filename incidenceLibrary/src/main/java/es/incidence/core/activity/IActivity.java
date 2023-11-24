package es.incidence.core.activity;

import android.content.res.Resources;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.app.ViewPumpAppCompatDelegate;

import com.e510.commons.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;

import dev.b3nedikt.restring.Restring;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.library.IncidenceLibraryManager;

public class IActivity extends BaseActivity
{
    public static final int PERMISSION_BLUETOOTH_CONNECT = 5;

    private AppCompatDelegate appCompatDelegate = null;

    @NonNull
    @Override
    public AppCompatDelegate getDelegate() {
        if (appCompatDelegate == null) {
            appCompatDelegate = new ViewPumpAppCompatDelegate(
                    super.getDelegate(),
                    this,
                    Restring::wrapContext
            );
        }
        return appCompatDelegate;
    }

    @Override
    public Resources getResources() {
        return Restring.wrapContext(getBaseContext()).getResources();
    }

    @Override
    public void onStop() {
        int count = IncidenceLibraryManager.instance.getStateCounter();
        if (count == 1)
        {
            EventBus.getDefault().post(new Event(EventCode.APP_WILL_RESIGN_ACTIVE));
        }
        super.onStop();
    }
}
