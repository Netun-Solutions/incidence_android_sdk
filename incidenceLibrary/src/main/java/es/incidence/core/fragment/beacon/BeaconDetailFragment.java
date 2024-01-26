package es.incidence.core.fragment.beacon;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.drawable.DrawableCompat;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.incidence.core.Constants;
import es.incidence.core.domain.Beacon;
import es.incidence.core.domain.IncidenceDGT;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.manager.ImageManager;
import es.incidence.core.utils.IUtils;
import es.incidence.core.utils.view.IButton;
import es.incidence.core.utils.view.INavigation;
import es.incidence.library.IncidenceLibraryManager;

public class BeaconDetailFragment extends IFragment
{
    private static final String TAG = makeLogTag(BeaconDetailFragment.class);

    public static final String KEY_AUTO_SELECTED_VEHICLE = "KEY_AUTO_SELECTED_VEHICLE";
    public static final String KEY_AUTO_SELECTED_USER = "KEY_AUTO_SELECTED_USER";
    public static final String KEY_AUTO_SELECTED_IMEI = "KEY_AUTO_SELECTED_IMEI";

    private INavigation navigation;
    private RelativeLayout layoutRootBeaconDetailFind;
    private RelativeLayout layoutRootBeaconDetailInfo;
    private TextView txtTimeBeacon;
    private TextView txtVolverFind;
    private TextView txtVolverInfo;
    private ProgressBar progressBar;
    private TextView txtSubTitleBattery;
    private TextView txtSubtitleFecha;
    private ImageView imgInfo;

    private ImageView imageBeacon;
	private ImageView imgDevice;

    private ListView listView;
    //private IncidenceDGTListAdapter adapter;

    private RelativeLayout layoutStopDevice;
    private CardView alertVolumeErrorContainer;
    private TextView alertVolumeErrorTitle;
    private TextView alertVolumeErrorSubTitle;
    private ImageView alertVolumeErrorImgClose;
    private ImageView imgDesc;

    private RelativeLayout layoutNewIncidence;
    private CardView alertNewIncidenceContainer;
    private TextView alertNewIncidenceTitle;
    private TextView alertNewIncidenceSubTitle;
    private ImageView alerNewIncidenceImgClose;
    private ImageView imgDescNewIncidence;
    public IButton btnBlue;

    private CountDownTimer countDownTimer;
    private Handler handlerVibrate;

    private Beacon beacon;
    public Vehicle autoSelectedVehicle;
    public User autoSelectedUser;

    private Double battery;
    private Integer alert = 0;
    private String expirationDate;
    private Integer dgt = 0;
    public ArrayList<IncidenceDGT> items;

    private long secondsRemainingConfig = 90;
    private long secondsRemaining = -1;

    private boolean closedAlertStopDevice = false;
    private boolean closedAlertNewIncidence = false;

    private Handler h;

    private boolean hasVibrate = false;

    public static BeaconDetailFragment newInstance(Vehicle vehicle, User user)
    {
        BeaconDetailFragment fragment = new BeaconDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_AUTO_SELECTED_VEHICLE, vehicle);
        bundle.putParcelable(KEY_AUTO_SELECTED_USER, user);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getTitleId() {
        return R.string.incidence_key_beacon;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            autoSelectedVehicle = getArguments().getParcelable(KEY_AUTO_SELECTED_VEHICLE);
            autoSelectedUser = getArguments().getParcelable(KEY_AUTO_SELECTED_USER);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_beacon_detail, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void onDestroy() {
        stopCountDownTimer();
        stopCountDownTimerVibrate();
        cancelHandler();
        super.onDestroy();
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.setBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickReturn();
            }
        });

        //String title = beacon.name;
        String title = getString(R.string.incidence_key_beacon);
        navigation.init(this, title, true);

        layoutRootBeaconDetailFind = rootView.findViewById(R.id.layoutRootBeaconDetailFind);
        layoutRootBeaconDetailInfo = rootView.findViewById(R.id.layoutRootBeaconDetailInfo);

        txtTimeBeacon = rootView.findViewById(R.id.txtTimeBeacon);

        progressBar = layoutRootBeaconDetailInfo.findViewById(R.id.progressBar);
        //progressBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
        //progressBar.setProgress(20);
        //progressBar.setMax(100);

        txtSubTitleBattery = layoutRootBeaconDetailInfo.findViewById(R.id.txtSubTitleBattery);
        txtSubtitleFecha = layoutRootBeaconDetailInfo.findViewById(R.id.txtSubtitleFecha);
        imgDevice = layoutRootBeaconDetailInfo.findViewById(R.id.imgDevice);

        //---
        layoutStopDevice = rootView.findViewById(R.id.layoutStopDevice);
        alertVolumeErrorContainer = layoutStopDevice.findViewById(R.id.alertVolumeErrorContainer);
        alertVolumeErrorTitle = layoutStopDevice.findViewById(R.id.alertVolumeErrorTitle);
        alertVolumeErrorSubTitle = layoutStopDevice.findViewById(R.id.alertVolumeErrorSubTitle);
        alertVolumeErrorImgClose = layoutStopDevice.findViewById(R.id.alertVolumeErrorImgClose);
        imgDesc = layoutStopDevice.findViewById(R.id.imgDesc);
        FontUtils.setTypeValueText(alertVolumeErrorTitle, Constants.FONT_SEMIBOLD, getContext());
        FontUtils.setTypeValueText(alertVolumeErrorSubTitle, Constants.FONT_REGULAR, getContext());
        alertVolumeErrorImgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeAlertStopDeviceView();
                }
            }
        );
        alertVolumeErrorTitle.setText(getString(R.string.incidence_key_stop_device));
        alertVolumeErrorSubTitle.setText(getString(R.string.incidence_key_stop_device_desc));
        //imgDesc.setImageDrawable(getContext().getDrawable(R.drawable.ico_conection));
        imgDesc.setImageDrawable(Utils.getDrawable(getContext(), R.drawable.ico_conection));
        DrawableCompat.setTint(imgDesc.getDrawable(), Utils.getColor(getContext(), R.color.colorPrimary));

        layoutNewIncidence = rootView.findViewById(R.id.layoutNewIncidence);
        alertNewIncidenceContainer = layoutNewIncidence.findViewById(R.id.alertVolumeErrorContainer);
        alertNewIncidenceTitle = layoutNewIncidence.findViewById(R.id.alertVolumeErrorTitle);
        alertNewIncidenceSubTitle = layoutNewIncidence.findViewById(R.id.alertVolumeErrorSubTitle);
        alerNewIncidenceImgClose = layoutNewIncidence.findViewById(R.id.alertVolumeErrorImgClose);
        imgDescNewIncidence = layoutNewIncidence.findViewById(R.id.imgDesc);
        btnBlue = layoutNewIncidence.findViewById(R.id.btnBlue);
        FontUtils.setTypeValueText(alertNewIncidenceTitle, Constants.FONT_SEMIBOLD, getContext());
        FontUtils.setTypeValueText(alertNewIncidenceSubTitle, Constants.FONT_REGULAR, getContext());
        alerNewIncidenceImgClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeAlertNewIncidenceView();
                }
            }
        );
        alertNewIncidenceTitle.setText(getString(R.string.incidence_key_alert_new_incidence_title));
        alertNewIncidenceSubTitle.setText(getString(R.string.incidence_key_alert_new_incidence_subtitle));
        //imgDesc.setImageDrawable(getContext().getDrawable(R.drawable.ico_conection));
        imgDescNewIncidence.setImageDrawable(Utils.getDrawable(getContext(), R.drawable.ico_conection));
        DrawableCompat.setTint(imgDescNewIncidence.getDrawable(), Utils.getColor(getContext(), R.color.colorPrimary));
        btnBlue.setVisibility(View.VISIBLE);
        btnBlue.setText(getString(R.string.incidence_key_report_incidence));
        btnBlue.setPrimaryColors();
        btnBlue.setHeight((int)getResources().getDimension(R.dimen.margin_normal_medium));
        FontUtils.setTypeValueText(btnBlue, Constants.FONT_SEMIBOLD, getContext());
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //mListener.addFragmentAnimated(IncidenceReportFragment.newInstance(beacon.vehicle, beacon.vehicle, false));
            }
        });
        //---

        txtVolverFind = layoutRootBeaconDetailFind.findViewById(R.id.txtVolverFind);
        txtVolverFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickReturn();
            }
        });
        FontUtils.setTypeValueText(txtVolverFind, Constants.FONT_SEMIBOLD, getContext());

        txtVolverInfo = layoutRootBeaconDetailInfo.findViewById(R.id.txtVolverInfo);
        txtVolverInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickReturn();
            }
        });
        FontUtils.setTypeValueText(txtVolverInfo, Constants.FONT_SEMIBOLD, getContext());

        listView = layoutRootBeaconDetailInfo.findViewById(R.id.listView);
        items = new ArrayList<>();
        //adapter = new IncidenceDGTListAdapter(this, items);
        //listView.setAdapter(adapter);

        imgInfo = layoutRootBeaconDetailInfo.findViewById(R.id.imgInfo);
        imgInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToBeaconInfo();
            }
        });

		imageBeacon = layoutRootBeaconDetailFind.findViewById(R.id.imageBeacon);
        



        IncidenceLibraryManager.instance.setViewBackground(rootView);
        //IncidenceLibraryManager.instance.setViewBackground(layoutRootBeaconDetailFind);

        startCountDownTimer();
    }

    public void updateUI() {

        String title = beacon.name;
        navigation.setTitle(title);

        if (beacon.beaconType != null && beacon.beaconType.imageBeacon != null) {
            ImageManager.loadImage(getContext(), beacon.beaconType.imageBeacon, null, imageBeacon, true);
        } else {
            imageBeacon.setImageResource(R.drawable.device_start);
        }
		
		int drawable = beacon.beaconType != null && beacon.beaconType.id == 1 ? R.drawable.beacon_icon_smart :  beacon.beaconType != null && beacon.beaconType.id == 3 ? R.drawable.beacon_icon_hella : R.drawable.beacon_icon_iot;
        imgDevice.setImageDrawable(getContext().getDrawable(drawable));
    }

    private void startCountDownTimer() {
        stopCountDownTimer();

        countDownTimer = new CountDownTimer(1000 * secondsRemainingConfig, 1000) {
        //countDownTimer = new CountDownTimer(1000 * 10, 1000) {

            public void onTick(long millisUntilFinished) {
                secondsRemaining = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished));
                String time = String.format("00:%02d", secondsRemaining);
                long minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished);
                if (minutes > 0)
                {
                    time = String.format("%02d:%02d", minutes, secondsRemaining);
                }

                LogUtil.logE(TAG, "onTick: " + time);
                txtTimeBeacon.setText(time);

                /*
                if ((seconds+1) % 5 == 0) {
                    refreshData();
                }
                */
            }

            public void onFinish()
            {
                showAlertConfirm(getString(R.string.incidence_key_start_device_without_network), getString(R.string.incidence_key_start_device_without_network_desc), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startCountDownTimer();
                    }
                }, getString(R.string.incidence_key_accept), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        onClickReturn();
                    }
                }, getString(R.string.incidence_key_cancel));
            }
        };
        countDownTimer.start();
    }

    private void startCountDownTimerVibrate() {
        if (handlerVibrate == null && !closedAlertNewIncidence) {
            IUtils.vibrate(getContext());

            handlerVibrate = new Handler(Looper.getMainLooper());
            handlerVibrate.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        handlerVibrate = null;
                        startCountDownTimerVibrate();
                    } catch (Exception e) {
                        Log.e(TAG, "Error: " + e.getMessage());
                    }
                }
            }, 1000);
        }
    }
    private void stopCountDownTimer() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
    }

    private void cancelHandler() {
        if (h != null) {
            h.removeCallbacksAndMessages(null);
            h = null;
        }
    }

    private void stopCountDownTimerVibrate() {
        if (handlerVibrate != null) {
            handlerVibrate.removeCallbacksAndMessages(null);
            handlerVibrate = null;
        }
    }

    private void onClickReturn() {
        cancelHandler();
        closeThis();
    }

    @Override
    public void loadData()
    {
        showHud();
        Api.getBeaconSdk(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {

                hideHud();

                if (response.isSuccess())
                {
                    ArrayList<Beacon> list = response.getList("beacon", Beacon.class);
                    if (list.size() > 0) {
                        beacon = list.get(0);

                        updateUI();
                        refreshData();
                    } else {
                        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onClickReturn();
                            }
                        };
                        showAlert(getString(R.string.incidence_key_nombre_app), "Sin balizas asociados", listener);
                    }
                }
                else
                {
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onClickReturn();
                        }
                    };
                    onBadResponse(response, listener);
                }
            }
        }, autoSelectedUser, autoSelectedVehicle);
    }

    private void refreshData() {
        Api.getBeaconDetailSdk(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {

                //hideHud();

                if (response.isSuccess())
                {
                    try {
                        JSONObject obj = response.get();
                        //JSONObject obj = new JSONObject("{\"dgt\":0,\"incidences\":[{\"hour\":\"17:23\",\"id\":1,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"27/10/2022\"},{\"hour\":\"22:10\",\"id\":2,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"21/10/2022\"},{\"hour\":\"11:20\",\"id\":3,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"16/10/2022\"},{\"hour\":\"09:33\",\"id\":4,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"08/10/2022\"},{\"hour\":\"10:00\",\"id\":5,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"01/10/2022\"}],\"expirationDate\":\"2037-12-31 23:59:59\",\"battery\":27.999999999999972,\"imei\":\"869154040054509\"}");
                        if (obj != null) {
                            JSONObject data = obj.optJSONObject("data");
                            //JSONObject data = new JSONObject("{\"dgt\":0,\"incidences\":[{\"hour\":\"17:23\",\"id\":1,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"27/10/2022\"},{\"hour\":\"22:10\",\"id\":2,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"21/10/2022\"},{\"hour\":\"11:20\",\"id\":3,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"16/10/2022\"},{\"hour\":\"09:33\",\"id\":4,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"08/10/2022\"},{\"hour\":\"10:00\",\"id\":5,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"01/10/2022\"}],\"expirationDate\":\"2037-12-31 23:59:59\",\"battery\":27.999999999999972,\"imei\":\"869154040054509\"}");
                            if (data != null) {
                                if (data.has("battery")) battery = data.getDouble("battery");
                                if (data.has("expirationDate")) expirationDate = data.getString("expirationDate");
                                if (data.has("alert")) alert = data.getInt("alert");
                                if (data.has("dgt")) dgt = data.getInt("dgt");


                                items.clear();
                                /*
                                JSONArray incidences = data.optJSONArray("incidences");
                                if (incidences != null) {
                                    for (int i = 0; i < incidences.length(); i++) {
                                        JSONObject incidence = incidences.getJSONObject(i);

                                        Double lat = null;
                                        Double lon = null;
                                        String date = null;
                                        String hour = null;
                                        if (incidence.has("lat")) lat = incidence.getDouble("lat");
                                        if (incidence.has("lon")) lon = incidence.getDouble("lon");
                                        if (incidence.has("date")) date = incidence.getString("date");
                                        if (incidence.has("hour")) hour = incidence.getString("hour");

                                        IncidenceDGT incidenceDGT = new IncidenceDGT();
                                        incidenceDGT.lat = lat;
                                        incidenceDGT.lon = lon;
                                        incidenceDGT.date = date;
                                        incidenceDGT.hour = hour;

                                        //ListItem li = new ListItem("20-05-2021, 16:45h", "");
                                        items.add(incidenceDGT);
                                    }
                                }
                                */
                                if (dgt == 0) {
                                    openAlertStopDeviceView();
                                } else if (dgt == 1) {
                                    /*
                                    if (!hasVibrate) {
                                        hasVibrate = true;
                                        IUtils.vibrate(getContext());
                                    }
                                    */
                                    startCountDownTimerVibrate();

                                    closeAlertStopDeviceView();
                                    openAlertNewIncidenceView();
                                }

                                changeView();
                                callRetry();
                            } else {
                                if (battery != null) {
                                    closeAlertStopDeviceView();
                                    closeAlertNewIncidenceView();
                                    stopCountDownTimerVibrate();
                                }
                                callRetry();
                            }
                        } else {
                            callRetry();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            onClickReturn();
                        }
                    };
                    stopCountDownTimer();
                    stopCountDownTimerVibrate();
                    onBadResponse(response, listener);
                }
            }

            private void callRetry() {
                if (secondsRemaining > 0) {
                    h = new Handler();
                    h.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            refreshData();
                        }
                    }, 5000);
                }

            }
        }, autoSelectedUser, autoSelectedVehicle);
    }

    private void openAlertStopDeviceView() {
        if (!closedAlertStopDevice) {
            alertVolumeErrorContainer.setVisibility(View.VISIBLE);
        }
    }

    private void closeAlertStopDeviceView() {
        closedAlertStopDevice = true;
        alertVolumeErrorContainer.setVisibility(View.GONE);
    }

    private void openAlertNewIncidenceView() {
        if (!closedAlertNewIncidence) {
            alertNewIncidenceContainer.setVisibility(View.VISIBLE);
        }
    }

    private void closeAlertNewIncidenceView() {
        closedAlertNewIncidence = true;
        alertNewIncidenceContainer.setVisibility(View.GONE);

        stopCountDownTimerVibrate();
    }

    private void changeView() {
        stopCountDownTimer();

        layoutRootBeaconDetailFind.setVisibility(View.GONE);
        layoutRootBeaconDetailInfo.setVisibility(View.VISIBLE);

        progressBar.setProgress(battery.intValue());
        //if (battery <= 20) {
        if (alert == 1) {
            progressBar.getProgressDrawable().setColorFilter(Color.RED, android.graphics.PorterDuff.Mode.SRC_IN);
            DrawableCompat.setTint(imgInfo.getDrawable(), Color.RED);
        }

        txtSubTitleBattery.setText(String.format("%.2f", battery) + "%");
        txtSubtitleFecha.setText(expirationDate);

        //adapter.notifyDataSetChanged();
    }

    private void goToBeaconInfo()
    {
        mListener.addFragmentAnimated(BeaconDetailInfoFragment.newInstance(beacon));
    }
}