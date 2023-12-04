package es.incidence.core.manager.incidence;

import android.os.CountDownTimer;

public class IncidenceManager {
    private static IncidenceManager instance;

    private CountDownTimer countDownTimer;
    public static long secondsGlobal = 0;
    public static boolean alertTimeErrorContainerCall = false;


    public static IncidenceManager init()
    {
        if (IncidenceManager.instance == null)
        {
            IncidenceManager.instance = new IncidenceManager();
        }

        return instance;
    }

    public void onDestroyCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
