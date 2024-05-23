package es.incidence.core.fragment.incidence.report;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;

import androidx.core.app.ActivityCompat;

import com.e510.commons.activity.BaseActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.domain.Beacon;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;

public abstract class IncidentReportBaseFragment extends IFragment
{
    private static final String TAG = makeLogTag(IncidentReportBaseFragment.class);

    protected Vehicle vehicle;
    protected User user;
    private Beacon beacon;

    private Handler handlerCallDgt;

    private boolean needCheckSelfPermission = true;

    @Override
    public void onResume() {
        super.onResume();

        loadBeacon();

        if (needCheckSelfPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                needCheckSelfPermission = false;
                ActivityCompat.requestPermissions(getBaseActivity(), new String[]{Manifest.permission.CALL_PHONE}, BaseActivity.PERMISSION_CALL_PHONE);
            }
        }
    }

    public void loadBeacon()
    {
        Api.getBeaconSdk(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {

                if (response.isSuccess())
                {
                    ArrayList<Beacon> list = response.getList("beacon", Beacon.class);
                    if (list.size() > 0) {
                        beacon = list.get(0);

                        refreshData();
                    }
                }
            }
        }, user, vehicle);
    }

    private void cancelHandler() {
        if (handlerCallDgt != null) {
            handlerCallDgt.removeCallbacksAndMessages(null);
            handlerCallDgt = null;
        }
    }

    private void callRetry(final Beacon beacon, int finalRepeatVoice) {
        if (finalRepeatVoice > 0) {
            handlerCallDgt = new Handler();
            handlerCallDgt.postDelayed(new Runnable() {
                @Override
                public void run() {
                    refreshData();
                }
            }, finalRepeatVoice * 1000);
        }

    }

    private void refreshData() {
        cancelHandler();

        int finalRepeatVoice = 15;

        if (beacon.imei != null) {
            /*
            Api.dgtCheck(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {

                    //hideHud();
                    //{"status":"success","data":{"imei":"869154040054509","incidenceId":39300,"dgt":0}}
                    if (response.isSuccess()) {
                        boolean dgtBol = false;
                        try {
                            JSONObject obj = response.get();
                            //JSONObject obj = new JSONObject("{\"dgt\":0,\"incidences\":[{\"hour\":\"17:23\",\"id\":1,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"27/10/2022\"},{\"hour\":\"22:10\",\"id\":2,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"21/10/2022\"},{\"hour\":\"11:20\",\"id\":3,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"16/10/2022\"},{\"hour\":\"09:33\",\"id\":4,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"08/10/2022\"},{\"hour\":\"10:00\",\"id\":5,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"01/10/2022\"}],\"expirationDate\":\"2037-12-31 23:59:59\",\"battery\":27.999999999999972,\"imei\":\"869154040054509\"}");
                            if (obj != null) {
                                JSONObject data = obj.optJSONObject("data");
                                if (data != null) {
                                    if (data.has("dgt")) {
                                        int dgt = data.getInt("dgt");
                                        dgtBol = dgt == 1;
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            callRetry(beacon, finalRepeatVoice);
                        }

                        if (Constants.FLAG_INCIDENCE_DGT != dgtBol) {
                            Constants.FLAG_INCIDENCE_DGT_CLOSED = false;
                        }
                        Constants.FLAG_INCIDENCE_DGT = dgtBol;
                        EventBus.getDefault().post(new Event(EventCode.INCICENDE_DGT_UPDATED));
                        showHideAlertDgt(dgtBol);
                    } else {
                        callRetry(beacon, finalRepeatVoice);
                    }
                }
            }, beacon.imei);
            */
            Api.getBeaconDetailSdk(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {

                    if (response.isSuccess())
                    {
                        boolean dgtBol = false;

                        try {
                            JSONObject obj = response.get();
                            //JSONObject obj = new JSONObject("{\"dgt\":0,\"incidences\":[{\"hour\":\"17:23\",\"id\":1,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"27/10/2022\"},{\"hour\":\"22:10\",\"id\":2,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"21/10/2022\"},{\"hour\":\"11:20\",\"id\":3,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"16/10/2022\"},{\"hour\":\"09:33\",\"id\":4,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"08/10/2022\"},{\"hour\":\"10:00\",\"id\":5,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"01/10/2022\"}],\"expirationDate\":\"2037-12-31 23:59:59\",\"battery\":27.999999999999972,\"imei\":\"869154040054509\"}");
                            if (obj != null) {
                                JSONObject data = obj.optJSONObject("data");
                                //JSONObject data = new JSONObject("{\"dgt\":0,\"incidences\":[{\"hour\":\"17:23\",\"id\":1,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"27/10/2022\"},{\"hour\":\"22:10\",\"id\":2,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"21/10/2022\"},{\"hour\":\"11:20\",\"id\":3,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"16/10/2022\"},{\"hour\":\"09:33\",\"id\":4,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"08/10/2022\"},{\"hour\":\"10:00\",\"id\":5,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"01/10/2022\"}],\"expirationDate\":\"2037-12-31 23:59:59\",\"battery\":27.999999999999972,\"imei\":\"869154040054509\"}");
                                if (data != null) {
                                    int dgt = 0;
                                    if (data.has("dgt")) dgt = data.getInt("dgt");
                                    dgtBol = dgt == 1;
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (Constants.FLAG_INCIDENCE_DGT != dgtBol) {
                            Constants.FLAG_INCIDENCE_DGT_CLOSED = false;
                        }
                        Constants.FLAG_INCIDENCE_DGT = dgtBol;
                        EventBus.getDefault().post(new Event(EventCode.INCICENDE_DGT_UPDATED));

                        callRetry(beacon, finalRepeatVoice);
                    }
                    else
                    {
                        callRetry(beacon, finalRepeatVoice);
                    }
                }

            }, user, vehicle);
        } else {
            callRetry(beacon, finalRepeatVoice);
        }
    }

    @Override
    public void onStop() {
        super.onStop();

        cancelHandler();
        Constants.FLAG_INCIDENCE_DGT_CLOSED = false;
    }
}
