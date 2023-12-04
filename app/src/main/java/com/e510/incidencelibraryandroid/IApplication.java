package com.e510.incidencelibraryandroid;

import android.app.Application;

import es.incidence.library.IncidenceLibraryManager;
import es.incidence.library.config.Environment;
import es.incidence.library.config.IncidenceLibraryConfig;

public class IApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        String apiKey = "Y29tLmU1MTAuaW5jaWRlbmNlbGlicmFyeWFuZHJvaWQ6ZDkwZTEwN2Y3YTRlNTZkMmM5ZDEyYTBzN2U0NWQwMDA=1";
        IncidenceLibraryConfig config = new IncidenceLibraryConfig.Builder()
                .setApikey(apiKey)
                .setEnvironment(Environment.PRE)
                .createIncidenceLibraryConfig();

        IncidenceLibraryManager.setup(this, config);
    }
}
