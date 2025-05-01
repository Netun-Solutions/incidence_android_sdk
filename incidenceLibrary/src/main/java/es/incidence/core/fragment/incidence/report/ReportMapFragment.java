package es.incidence.core.fragment.incidence.report;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;
import com.mapbox.geojson.Point;

import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.fragment.common.MapFullFragment;
import es.incidence.core.utils.view.IButton;

public class ReportMapFragment extends MapFullFragment
{
    public static final String KEY_VEHICLE = "KEY_VEHICLE";
    //params
    public Vehicle vehicle;

    public static final String KEY_INCIDENCE = "KEY_INCIDENCE";
    public Incidence incidence;

    private Handler handlerScan;

    private TextView txtTitle;
    private TextView txtMessage;
    private IButton btnContinue;
    private TextView txtAddLater;
    private RelativeLayout layoutCallGrua;
    //private SwipeButton btnCancelGrua;

    public static ReportMapFragment newInstance(Vehicle vehicle, Incidence incidence)
    {
        ReportMapFragment fragment = new ReportMapFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_VEHICLE, vehicle);
        bundle.putParcelable(KEY_INCIDENCE, incidence);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            vehicle = getArguments().getParcelable(KEY_VEHICLE);
            incidence = getArguments().getParcelable(KEY_INCIDENCE);
        }
    }

    @Override
    public void onDestroy() {
        if (handlerScan != null)
        {
            handlerScan.removeCallbacksAndMessages(null);
        }
        super.onDestroy();
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        searchField.setVisibility(View.GONE);


        LayoutInflater inflater = LayoutInflater.from(getContext());
        //View view = inflater.inflate(R.layout.layout_bottomsheet_map_report, null);
        //FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        //view.setClipToOutline(true);
        float radius = Utils.dpToPx(16);
        /*GradientDrawable drawable = Utils.createGradientDrawable(getContext(), android.R.color.white, 0);
        drawable.setCornerRadii(new float [] { radius, radius,
                radius, radius,
                0, 0,
                0, 0});
        view.setBackground(drawable);


        txtTitle = view.findViewById(R.id.txtTitle);
        txtMessage = view.findViewById(R.id.txtMessage);

        FontUtils.setTypeValueText(view.findViewById(R.id.txtTitle), Constants.FONT_SEMIBOLD, getContext());
        btnContinue = view.findViewById(R.id.btnContinue);
        FontUtils.setTypeValueText(btnContinue, Constants.FONT_SEMIBOLD, getContext());
        btnContinue.setPrimaryColors();
        txtAddLater = view.findViewById(R.id.txtAddLater);
        FontUtils.setTypeValueText(txtAddLater, Constants.FONT_SEMIBOLD, getContext());

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addFragmentAnimated(IncidenceValorationFragment.newInstance(incidence));
            }
        });

        txtAddLater.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.cleanAllBackStackEntries();
            }
        });


        layoutCallGrua = view.findViewById(R.id.btnCallGrua);
        GradientDrawable drawableGrua = Utils.createGradientDrawable(getContext(), R.color.incidence100, 64);
        layoutCallGrua.setBackground(drawableGrua);
        layoutCallGrua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callGrua();
            }
        });
        ImageView imgCallGrua = layoutCallGrua.findViewById(R.id.imgCallGrua);
        imgCallGrua.setColorFilter(Utils.getColor(getContext(), R.color.incidence500));

        btnCancelGrua = view.findViewById(R.id.btnContinueSwipe);
        GradientDrawable drawableCancelGrua = Utils.createGradientDrawable(getContext(), R.color.error, 64);
        btnCancelGrua.setBackground(drawableCancelGrua);
        btnCancelGrua.setText(getString(R.string.contact_grua_cancel));
        btnCancelGrua.setDisabledDrawable(Utils.getDrawable(getContext(), R.drawable.icon_swipe_arrow));
        btnCancelGrua.setSwipeButtonListener(new SwipeButton.SwipeButtonListener() {
            @Override
            public void onSwiped() {
                cancelGrua();
            }
        });

        bottomSheetLayout.setVisibility(View.VISIBLE);
        bottomSheetLayoutBlur.setAlpha(0);
        bottomSheetLayoutBlur.setVisibility(View.GONE);
        bottomSheetLayout.setOnProgressListener(new BottomSheetLayout.OnProgressListener() {
            @Override
            public void onProgress(float v) {
                float opacity = v;
                if (opacity > 0.9) {
                    opacity = 0.9f;
                }
                bottomSheetLayoutBlur.setAlpha(opacity);
                bottomSheetLayoutBlur.setVisibility(opacity > 0 ? View.VISIBLE : View.GONE);
            }
        });
        RelativeLayout layout = bottomSheetLayout.findViewById(R.id.layoutContent);
        layout.addView(view);
        */
    }

    @Override
    public void loadData()
    {
        if (incidence != null)
        {
            if (incidence.latitude != null && incidence.longitude != null)
            {
                Point origin = Point.fromLngLat(incidence.longitude, incidence.latitude);
                drawPoint(origin);
            }

            loadGruaComing();
            loadAsitur();
        }
    }

    private void loadGruaComing()
    {
        txtTitle.setText(R.string.incidence_key_title_assist_coming);
        //txtMessage.setText(getString(R.string.subtitle_assist_coming, ""));
        //btnContinue.setVisibility(View.GONE);
        //txtAddLater.setVisibility(View.GONE);
        //layoutCallGrua.setVisibility(View.VISIBLE);
        //btnCancelGrua.setVisibility(View.VISIBLE);

        txtMessage.setVisibility(View.GONE);
        btnContinue.setVisibility(View.GONE);
        txtAddLater.setVisibility(View.GONE);
        layoutCallGrua.setVisibility(View.GONE);
        //btnCancelGrua.setVisibility(View.GONE);
    }

    private void loadGruaArrived()
    {
        txtTitle.setText(R.string.incidence_key_title_assist_finished);
        txtMessage.setText(R.string.incidence_key_subtitle_assist_finished);
        txtMessage.setVisibility(View.VISIBLE);
        btnContinue.setVisibility(View.VISIBLE);
        txtAddLater.setVisibility(View.VISIBLE);
        layoutCallGrua.setVisibility(View.GONE);
        //btnCancelGrua.setVisibility(View.GONE);
    }

    private void loadAsitur()
    {
        /* if (incidence.asitur != null)
        {
            showHud();
            Api.asiturIncidence(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    hideHud();
                    if (response.isSuccess())
                    {
                        Asitur asitur = (Asitur) response.get("asitur", Asitur.class);

                        if (asitur != null)
                        {
                            if (asitur.latitude != null && asitur.longitude != null)
                            {
                                if (incidence.latitude != null && incidence.longitude != null)
                                {
                                    Point origin = Point.fromLngLat(asitur.longitude, asitur.latitude);
                                    Point destination = Point.fromLngLat(incidence.longitude, incidence.latitude);
                                    Drawable d1 = getResources().getDrawable(R.drawable.icon_grua);
                                    Drawable d2 = getResources().getDrawable(R.drawable.icon_user_location);
                                    Drawable dExtra = getResources().getDrawable(R.drawable.icon_devices);

                                    drawRoute(origin, d1, destination, d2, imgCenterMap.getVisibility() != View.VISIBLE, false, null, dExtra);
                                }
                                else
                                {
                                    Point origin = Point.fromLngLat(asitur.longitude, asitur.latitude);
                                    Drawable d1 = getResources().getDrawable(R.drawable.icon_grua);
                                    drawPoint(origin, d1);
                                }
                            }
                            else if (incidence.latitude != null && incidence.longitude != null)
                            {
                                Point origin = Point.fromLngLat(incidence.longitude, incidence.latitude);
                                Drawable d1 = getResources().getDrawable(R.drawable.icon_user_location);
                                drawPoint(origin, d1);
                            }

                            if (asitur.finish == 1)
                            {
                                loadGruaArrived();
                                bottomSheetLayout.expand();

                                EventBus.getDefault().post(new Event(EventCode.INCIDENCE_REPORTED));
                            }
                            else {
                                if (handlerScan != null)
                                {
                                    handlerScan.removeCallbacksAndMessages(null);
                                }
                                handlerScan = new Handler(Looper.getMainLooper());
                                handlerScan.postDelayed(new Runnable()
                                {
                                    @Override
                                    public void run()
                                    {
                                        loadAsitur();
                                    }
                                }, 10000);
                            }
                        }
                        else
                        {
                            if (handlerScan != null)
                            {
                                handlerScan.removeCallbacksAndMessages(null);
                            }
                            handlerScan = new Handler(Looper.getMainLooper());
                            handlerScan.postDelayed(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    loadAsitur();
                                }
                            }, 10000);
                        }
                    }
                    else
                    {
                        onBadResponse(response);
                    }
                }
            }, incidence.id+"");
        } else */
        if (incidence.tracking != null)
        {
            //showHud();
            /*
            Api.trackingIncidence(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    //hideHud();
                    if (response.isSuccess())
                    {
                        String incidenceStatusText = response.get("incidenceStatusText");
                        String incidenceStatusTextSub = response.get("incidenceStatusTextSub");

                        txtTitle.setText(incidenceStatusText);

                        if (incidenceStatusTextSub != null && !"".equals(incidenceStatusTextSub)) {
                            txtMessage.setText(incidenceStatusTextSub);
                            txtMessage.setVisibility(View.VISIBLE);
                        } else {
                            txtMessage.setVisibility(View.GONE);
                        }

                        Double incidenceTrackingServiceLat = (Double) response.get("incidenceTrackingServiceLat", Double.class);
                        Double incidenceTrackingServiceLon = (Double) response.get("incidenceTrackingServiceLon", Double.class);
                        //Asitur asitur = (Asitur) response.get("asitur", Asitur.class);
                        Double beaconLat = (Double) response.get("beaconLat", Double.class);
                        Double beaconLon = (Double) response.get("beaconLon", Double.class);

                        Point pExtra = null;
                        if (beaconLat != null && beaconLon != null) {
                            pExtra = Point.fromLngLat(beaconLon, beaconLat);
                        }

                        if (incidenceTrackingServiceLat != null && incidenceTrackingServiceLon != null)
                        {
                            if (incidence.latitude != null && incidence.longitude != null)
                            {
                                Point origin = Point.fromLngLat(incidenceTrackingServiceLon, incidenceTrackingServiceLat);
                                Point destination = Point.fromLngLat(incidence.longitude, incidence.latitude);
                                Drawable d1 = getResources().getDrawable(R.drawable.icon_grua);
                                Drawable d2 = getResources().getDrawable(R.drawable.icon_user_location);
                                Drawable dExtra = getResources().getDrawable(R.drawable.icon_devices);

                                drawRoute(origin, d1, destination, d2, imgCenterMap.getVisibility() != View.VISIBLE, false, pExtra, dExtra);
                            }
                            else
                            {
                                Point origin = Point.fromLngLat(incidenceTrackingServiceLon, incidenceTrackingServiceLat);
                                Drawable d1 = getResources().getDrawable(R.drawable.icon_grua);
                                Drawable dExtra = getResources().getDrawable(R.drawable.icon_devices);
                                //drawPoint(origin, d1);
                                drawRoute(origin, d1, null, null, true, true, pExtra, dExtra);
                            }
                        }
                        else if (incidence.latitude != null && incidence.longitude != null)
                        {
                            Point origin = Point.fromLngLat(incidence.longitude, incidence.latitude);
                            Drawable d1 = getResources().getDrawable(R.drawable.icon_user_location);
                            Drawable dExtra = getResources().getDrawable(R.drawable.icon_devices);
                            //drawPoint(origin, d1);
                            drawRoute(origin, d1, null, null, true, true, pExtra, dExtra);
                        }
                        if (handlerScan != null)
                        {
                            handlerScan.removeCallbacksAndMessages(null);
                        }
                        handlerScan = new Handler(Looper.getMainLooper());
                        handlerScan.postDelayed(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                loadAsitur();
                            }
                        }, 10000);
                    }
                    else
                    {
                        onBadResponse(response);
                    }
                }
            }, incidence.id+"");
            */
        }
    }

    private void callGrua()
    {

    }

    private void cancelGrua()
    {
        //bottomSheetLayout.collapse();
    }
}
