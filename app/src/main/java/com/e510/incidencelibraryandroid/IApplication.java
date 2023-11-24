package com.e510.incidencelibraryandroid;

import android.app.Application;

import es.incidence.library.IncidenceLibraryManager;
import es.incidence.library.config.Environment;
import es.incidence.library.config.IncidenceLibraryConfig;

public class IApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        String apiKey = "bWFwZnJlX2NsaWVudDpkOTBlMTA3ZjdhNGU1NmQyYzlkMTJhMHM3ZTQ1ZDQ1MQ==";
        IncidenceLibraryConfig config = new IncidenceLibraryConfig.Builder()
                .setApikey(apiKey)
                .setEnvironment(Environment.PRE)
                .createIncidenceLibraryConfig();

        IncidenceLibraryManager.setup(this, config);
    }
}
