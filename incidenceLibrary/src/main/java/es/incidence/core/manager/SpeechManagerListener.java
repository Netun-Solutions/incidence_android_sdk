package es.incidence.core.manager;

public interface SpeechManagerListener {
    void onResults(String string);
    void onError();
}

