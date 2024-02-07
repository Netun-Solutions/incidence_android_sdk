package com.e510.incidencelibraryandroid;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.app.Application;
import android.util.Log;

import es.incidence.library.IncidenceLibraryManager;
import es.incidence.library.config.IncidenceEnvironment;
import es.incidence.library.config.IncidenceLibraryConfig;

public class IApplication extends Application {
    private static final String TAG = makeLogTag(IApplication.class);
    @Override
    public void onCreate() {
        super.onCreate();

        String apiKey = "Y29tLmU1MTAuaW5jaWRlbmNlbGlicmFyeWFuZHJvaWQ6ZDkwZTEwN2Y3YTRlNTZkMmM5ZDEyYTBzN2U0NWQwMDA=";
        IncidenceLibraryConfig config = new IncidenceLibraryConfig.Builder()
                .setApikey(apiKey)
                .setEnvironment(IncidenceEnvironment.PRE)
                .createIncidenceLibraryConfig();

        IncidenceLibraryManager.setup(this, config, response -> {
            if (response.isSuccess()) {
                //MAKE OK ACTIONS
                Log.d(TAG, "SETUP OK");
            } else {
                //MAKE KO ACTIONS
                Log.d(TAG, "SETUP KO");
            }
        });
    }
}
