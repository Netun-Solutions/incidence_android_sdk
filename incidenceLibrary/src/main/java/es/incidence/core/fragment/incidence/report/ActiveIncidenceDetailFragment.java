package es.incidence.core.fragment.incidence.report;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;
import com.mapbox.geojson.Point;
import com.ncorti.slidetoact.SlideToActView;

import org.json.JSONObject;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.adapter.ListAdapter;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.ListItem;
import es.incidence.core.fragment.common.MapBoxFragment;
import es.incidence.core.interfaces.OnHtmlMessageClickListener;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.view.INavigation;
import es.incidence.core.utils.view.INotification;
import es.incidence.library.IncidenceLibraryManager;

public class ActiveIncidenceDetailFragment extends MapBoxFragment implements SlideToActView.OnSlideCompleteListener, View.OnClickListener {

    private static final String TAG = makeLogTag(ActiveIncidenceDetailFragment.class);

    private static final int ROW_CALL_INSURER = 0;
    private static final int ROW_SHARE_LOCATION = 1;
    //private static final int ROW_SEND_MESSAGE = 2;

    public static final String KEY_VEHICLE = "KEY_VEHICLE";
    public static final String KEY_INCIDENCE = "KEY_INCIDENCE";
    //public static final String KEY_ADDRESS = "KEY_ADDRESS";
    public static final String KEY_USER = "KEY_USER";


    private RelativeLayout layoutRootActiveIncidence;

    private ScrollView incidenceDetailScrollView;
    private INavigation navigation;
    private TextView txtAddress1;
    private TextView txtAddress2;
    private TextView txtMessage, txtEstimatedArrival;
    private TextView txtPress;
    private ListView lvOptions;
    private RelativeLayout layoutFace;
    private SlideToActView slider;
    private LinearLayout llOptions;

    public ArrayList<ListItem> items;
    private ListAdapter adapter;

    public Vehicle vehicle;
    public Incidence incidence;
    private User user;
    //private String address;
    private int itemCount;
    private double estimatedTime;

    private String insurancePhone;

    private Handler handlerScan;

    public static ActiveIncidenceDetailFragment newInstance(Vehicle vehicle, User user, Incidence incidence) {
        ActiveIncidenceDetailFragment fragment = new ActiveIncidenceDetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_VEHICLE, vehicle);
        bundle.putParcelable(KEY_INCIDENCE, incidence);
        bundle.putParcelable(KEY_USER, user);
        //bundle.putString(KEY_ADDRESS, address);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            vehicle = getArguments().getParcelable(KEY_VEHICLE);
            incidence = getArguments().getParcelable(KEY_INCIDENCE);
            user = getArguments().getParcelable(KEY_USER);
            //address = getArguments().getString(KEY_ADDRESS);
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
    public int getTitleId() {
        return R.string.incidence_key_empty;
    }

    @Override
    public int getLayoutRootId() {
        return R.id.layoutRootIncidenceDetail;
    }

    @Override
    public boolean isMoveDisabled() {
        return true;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_active_incidence_detail, container, false);
        setupUI(view);
        overrideOnCreateView(view, savedInstanceState);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        estimatedTime = 12.8;

        layoutRootActiveIncidence = rootView.findViewById(R.id.layoutRootActiveIncidence);

        incidenceDetailScrollView = rootView.findViewById(R.id.incidenceDetailScrollView);
        incidenceDetailScrollView.setVisibility(View.GONE);

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, vehicle.getName() + " - " + incidence.getTitle(), true);

        int radius = 8;
        //GradientDrawable back = Utils.createGradientDrawable(getContext(), android.R.color.white, radius);

        lvOptions = rootView.findViewById(R.id.lvOptions);

        items = new ArrayList<>();
        adapter = new ListAdapter(this, ListAdapter.Type.TITLE, items,null);
        lvOptions.setAdapter(adapter);

        slider = rootView.findViewById(R.id.slider);
        txtAddress1 = rootView.findViewById(R.id.txtAddress1);
        txtAddress2 = rootView.findViewById(R.id.txtAddress2);
        txtMessage = rootView.findViewById(R.id.txtMessage);
        txtEstimatedArrival = rootView.findViewById(R.id.txtEstimatedArrival);
        txtPress = rootView.findViewById(R.id.txtPress);
        layoutFace = rootView.findViewById(R.id.layoutFace);
        layoutFace.setOnClickListener(this);

        slider.setText(getString(R.string.incidence_key_swipe_to_resolve_issue));

        txtAddress1.setText(incidence.city);
        txtAddress2.setText(incidence.street);
        FontUtils.setTypeValueText(txtAddress2, Constants.FONT_SEMIBOLD, getContext());

        txtMessage.setText(getContext().getString(R.string.incidence_key_help_is_on_the_way));
        FontUtils.setTypeValueText(txtMessage, Constants.FONT_SEMIBOLD, getContext());

        String text = getString(R.string.incidence_key_estimated_arrival_time) + " <font color='#004FFF'> " + estimatedTime + " hrs</font>";
        txtEstimatedArrival.setText(Html.fromHtml(text));


        slider.setOnSlideCompleteListener(this);
        slider.setAnimateCompletion(false);
        txtPress.setOnClickListener(this);

        ArrayList<ListItem> temp = new ArrayList<>();

        int color = Utils.getColor(getContext(), R.color.colorPrimary);
        Drawable drawable = Utils.getDrawable(getContext(), R.drawable.icon_phone);
        drawable.setTint(color);
        ListItem l1 = new ListItem(getString(R.string.incidence_key_call_insurer), ROW_CALL_INSURER);
        l1.rightDrawableSize = 60;
        l1.rightDrawable = drawable;
        temp.add(l1);

        ListItem l2 = new ListItem(getString(R.string.incidence_key_share_location), ROW_SHARE_LOCATION);
        temp.add(l2);

        //ListItem l3 = new ListItem(getString(R.string.incidence_key_send_message), ROW_SEND_MESSAGE);
        //temp.add(l3);

        itemCount = temp.size();
        addItems(temp);


        rootView.findViewById(R.id.layoutMapClicable).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addFragmentAnimated(ReportMapFragment.newInstance(vehicle, incidence));
            }
        });

        IncidenceLibraryManager.instance.setViewBackground(rootView);
    }

    @Override
    public void onClickRow(Object object) {
        if (object instanceof ListItem) {
            ListItem listItem = (ListItem) object;
            int row = (int) listItem.object;

            if (row == ROW_CALL_INSURER) {
                Core.callPhone(insurancePhone, false);
            } else if (row == ROW_SHARE_LOCATION) {
                Core.shareLocation(getContext(), incidence.longitude, incidence.latitude);
            //} else if (row == ROW_SEND_MESSAGE) {
            //    mListener.addFragmentAnimated(SendMessageFragment.newInstance());
            }
        }
    }

    @Override
    public void loadData() {
        if (incidence != null) {

            /*
            if (incidence.latitude != null && incidence.longitude != null) {
                Point origin = Point.fromLngLat(incidence.longitude, incidence.latitude);
                drawPoint(origin);
            }
            */
            loadData(true);
        }
    }

    public void loadData(boolean showHud) {
        if (showHud) showHud();

        Api.getIncidenceDetailSdk(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {

                if (showHud) hideHud();

                if (response.isSuccess())
                {
                    try {
                        //allBeacons = response.getList("beacons", Beacon.class);
                        //incidenceDetailScrollView.setVisibility(View.VISIBLE);

                        JSONObject obj = response.get();
                        //{"status":"success","incidence":{"id":1878,"vehicleIncidenceId":1957,"street":"Carrer De La Riba, 37","city":"Terrassa","country":"Spain","dateCreated":"03\/08\/2023 14:40","brand":"ford","model":"Mi vehículo","licensePlate":"E5453FTJ","registrationYear":2017,"dgt":"null","ima":"{\"data\":\"<?xml version=\\\"1.0\\\" encoding=\\\"UTF-8\\\" standalone=\\\"yes\\\"?><callResponse xmlns=\\\"http:\\\/\\\/not.ima.tm.fr\\\/telematic\\\"><stateCode>0<\\\/stateCode><uidIMA>113153032<\\\/uidIMA><\\\/callResponse>\"}","insuranceId":391,"insuranceName":"SEGURCAIXA","insurancePhone":"+34932750273","latitude":41.561412,"longitude":2.0177746,"incidenceType":{"id":13,"name":"Accident with injuries"},"status":5,"rate":-1,"deviceType":{"id":4,"name":"Help Flash IoT"},"incidenceStatusTextSub":"","incidenceEstimatedTime":"","incidenceStatusText":"Your incident has been reported"}}
                        //JSONObject obj = new JSONObject("{\"dgt\":0,\"incidences\":[{\"hour\":\"17:23\",\"id\":1,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"27/10/2022\"},{\"hour\":\"22:10\",\"id\":2,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"21/10/2022\"},{\"hour\":\"11:20\",\"id\":3,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"16/10/2022\"},{\"hour\":\"09:33\",\"id\":4,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"08/10/2022\"},{\"hour\":\"10:00\",\"id\":5,\"lat\":41.38879,\"lon\":2.1589900000000002,\"date\":\"01/10/2022\"}],\"expirationDate\":\"2037-12-31 23:59:59\",\"battery\":27.999999999999972,\"imei\":\"869154040054509\"}");
                        if (obj != null) {
                            incidence = (Incidence) response.get("incidence", Incidence.class);
                            JSONObject incidenceJson = obj.optJSONObject("incidence");
                            if (incidenceJson != null) {

                                int insuranceId = incidenceJson.getInt("insuranceId");
                                insurancePhone = incidenceJson.getString("insurancePhone");
                                String incidenceStatusText = incidenceJson.getString("incidenceStatusText");
                                String incidenceStatusTextSub = incidenceJson.getString("incidenceStatusTextSub");
                                String incidenceEstimatedTime = incidenceJson.getString("incidenceEstimatedTime");
                                if (incidenceJson.has("tracking")) {
                                    incidence.tracking = incidenceJson.getInt("tracking");
                                }
                                boolean incidenceIsCancelled = false;
                                boolean incidenceIsFinished = false;
                                if (incidenceJson.has("incidenceIsCancelled")) {
                                    incidenceIsCancelled = incidenceJson.getBoolean("incidenceIsCancelled");
                                }
                                if (incidenceJson.has("incidenceIsFinished")) {
                                    incidenceIsFinished = incidenceJson.getBoolean("incidenceIsFinished");
                                }

                                if (incidenceIsCancelled || incidenceIsFinished) {

                                    String messageHtml = incidenceJson.getString("message_html");
                                    if (messageHtml != null && !"".equals(messageHtml)) {
                                        boolean finalIncidenceIsCancelled = incidenceIsCancelled;
                                        showAlertHtml(getString(R.string.incidence_key_nombre_app), messageHtml, new OnHtmlMessageClickListener() {
                                            @Override
                                            public void htmlMessageClick() {
                                                if (finalIncidenceIsCancelled) {
                                                    cancelIncidence();
                                                } else {
                                                    rateIncidence();
                                                }
                                            }
                                        });
                                    } else {
                                        boolean finalIncidenceIsCancelled1 = incidenceIsCancelled;
                                        DialogInterface.OnClickListener dialogListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (finalIncidenceIsCancelled1) {
                                                    cancelIncidence();
                                                } else {
                                                    rateIncidence();
                                                }
                                            }
                                        };
                                        showAlert("La incidencia ya está cerrada", getString(R.string.incidence_key_accept), dialogListener, R.string.incidence_key_accept);
                                    }
                                    return;
                                }

                                Double beaconLat = null;
                                Double beaconLon = null;

                                if (incidenceJson.has("beaconLat")) {
                                    Double beaconLatOpt = incidenceJson.optDouble("beaconLat");
                                    beaconLat = beaconLatOpt.isNaN() ? null : beaconLatOpt;
                                }

                                if (incidenceJson.has("beaconLon")) {
                                    Double beaconLonOpt = incidenceJson.optDouble("beaconLon");
                                    beaconLon = beaconLonOpt.isNaN() ? null : beaconLonOpt;
                                }

                                Point pExtra = null;
                                if (beaconLat != null && beaconLon != null) {
                                    pExtra = Point.fromLngLat(beaconLon, beaconLat);
                                }

                                if (incidence.tracking == 1) {
                                    Double incidenceTrackingServiceLat = null;
                                    Double incidenceTrackingServiceLon = null;

                                    if (incidenceJson.has("incidenceTrackingServiceLat")) {
                                        Double incidenceTrackingServiceLatOpt = incidenceJson.optDouble("incidenceTrackingServiceLat");
                                        incidenceTrackingServiceLat = incidenceTrackingServiceLatOpt.isNaN() ? null : incidenceTrackingServiceLatOpt;
                                    }

                                    if (incidenceJson.has("incidenceTrackingServiceLon")) {
                                        Double incidenceTrackingServiceLonOpt = incidenceJson.optDouble("incidenceTrackingServiceLon");
                                        incidenceTrackingServiceLon = incidenceTrackingServiceLonOpt.isNaN() ? null : incidenceTrackingServiceLonOpt;
                                    }

                                    if (incidenceTrackingServiceLat != null && incidenceTrackingServiceLon != null && incidence.latitude != null && incidence.longitude != null) {
                                        Point origin = Point.fromLngLat(incidence.longitude, incidence.latitude);
                                        Point destination = Point.fromLngLat(incidenceTrackingServiceLon, incidenceTrackingServiceLat);
                                        Drawable d1 = getResources().getDrawable(R.drawable.icon_user_location);
                                        Drawable d2 = getResources().getDrawable(R.drawable.icon_grua);
                                        Drawable dExtra = getResources().getDrawable(R.drawable.icon_devices);



                                        drawRoute(origin, d1, destination, d2, true, false, pExtra, dExtra);
                                    } else {
                                        drawOrigenPoint(pExtra);
                                        /*
                                        //drawOrigenPoint();

                                        incidenceTrackingServiceLon = 41.534651;
                                        incidenceTrackingServiceLat = 2.082057;
                                        incidenceTrackingServiceLat = 41.231128;
                                        incidenceTrackingServiceLon = 1.780474;

                                        Point origin = Point.fromLngLat(incidence.longitude, incidence.latitude);
                                        Point destination = Point.fromLngLat(incidenceTrackingServiceLon, incidenceTrackingServiceLat);
                                        Drawable d1 = getResources().getDrawable(R.drawable.icon_user_location);
                                        Drawable d2 = getResources().getDrawable(R.drawable.icon_grua);

                                        drawRoute(origin, d1, destination, d2, true, false);
                                        */
                                    }
                                } else {
                                    drawOrigenPoint(pExtra);
                                }

                                if (insuranceId == 8) {
                                    txtMessage.setText(getContext().getString(R.string.incidence_key_help_is_on_the_way));

                                    String text = "";
                                    if (incidenceEstimatedTime != null && !incidenceEstimatedTime.isEmpty()) {
                                        text = incidenceStatusTextSub + " <font color='#004FFF'> " + incidenceEstimatedTime + " hrs</font>";
                                    }
                                    txtEstimatedArrival.setText(Html.fromHtml(text));
                                    txtEstimatedArrival.setVisibility(View.VISIBLE);
                                } else if (insuranceId == 33) {
                                    txtMessage.setText(incidenceStatusText);

                                    if (incidenceStatusTextSub != null && !"".equals(incidenceStatusTextSub)) {
                                        txtEstimatedArrival.setText(incidenceStatusTextSub);
                                        txtEstimatedArrival.setVisibility(View.VISIBLE);
                                    } else {
                                        txtEstimatedArrival.setVisibility(View.GONE);
                                    }

                                } else {
                                    txtMessage.setText(getContext().getString(R.string.incidence_key_help_is_on_the_way_v2));
                                    txtEstimatedArrival.setVisibility(View.GONE);
                                }
                            } else {
                                drawOrigenPoint();
                            }
                        } else {
                            drawOrigenPoint();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else
                {
                    //onBadResponse(response);
                    drawOrigenPoint();
                }

                incidenceDetailScrollView.setVisibility(View.VISIBLE);

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
                        loadData(false);
                    }
                }, 10000);
            }
        }, user, vehicle);
    }

    private void drawOrigenPoint() {
        drawOrigenPoint(null);
    }

    private void drawOrigenPoint(Point pExtra) {
        if (incidence.latitude != null && incidence.longitude != null) {
            Point origin = Point.fromLngLat(incidence.longitude, incidence.latitude);
            //drawPoint(origin);
            drawRoute(origin, null, true, pExtra);
        }
    }

    public void addItems(ArrayList<ListItem> newItems) {
        items.clear();
        items.addAll(newItems);
        ViewGroup.LayoutParams lp = (ViewGroup.LayoutParams) lvOptions.getLayoutParams();
        lp.height = 150 * itemCount;
        lvOptions.setLayoutParams(lp);
        adapter.notifyDataSetChanged();
    }

    private void showCloseIncidenceActive() {
        /*
        if (incidence != null) {
            String title = getString(R.string.incidence_wish_good);
            String message = getString(R.string.incidence_wish_good_desc);
            String titleButton = getString(R.string.rate_service);
            String titleButtonCancel = getString(R.string.rate_later);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rateIncidence();
                }
            };

            View.OnClickListener listenerCancel = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showHud();
                    Api.closeIncidence(new IRequestListener() {
                        @Override
                        public void onFinish(IResponse response) {
                            hideHud();
                            if (response.isSuccess()) {
                                Core.removeData(Constants.KEY_LAST_INCIDENCE_REPORTED_DATE);

                                incidence.close();
                                INotification.shared(getContext()).hideNoAnimated();
                                //closeThis();

                                mListener.removeFragment(ActiveIncidenceDetailFragment.this, true);
                                mListener.showInitialFragment(HomeFragment.newInstance());
                                //mListener.removeFragment(HomeFragment.newInstance());
                                //mListener.addFragment(HomeFragment.newInstance());

                            } else {
                                onBadResponse(response);
                            }
                        }
                    }, incidence.id);
                }
            };
            RelativeLayout layoutToShow = layoutRootActiveIncidence;
            INotification.shared(getContext()).showNotification(layoutToShow, title, message, titleButton, titleButtonCancel, listener, listenerCancel, true, false);
        }
        */
    }

    @Override
    public void onSlideComplete(@NonNull SlideToActView slideToActView) {
        showCloseIncidenceActive();
        slider.resetSlider();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.txtPress || v.getId() == R.id.txtMistake || v.getId() == R.id.imgFace || v.getId() == R.id.layoutFace) {
            reportedByMistakeAction();
        }
    }

    /*
    public void showRateIncidenceActive() {
        String title = getString(R.string.incidence_wish_good);
        String message = getString(R.string.incidence_wish_good_desc);
        String titleButton = getString(R.string.make_valoration);
        String titleCancelButton = getString(R.string.later);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INotification.shared(getContext()).hide();

                mListener.addFragmentAnimated(IncidenceValorationFragment.newInstance(incidence));
                mListener.removeFragment(ActiveIncidenceDetailFragment.this, true);
            }
        };

        View.OnClickListener listenerCancel = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                INotification.shared(getContext()).hide();
            }
        };

        RelativeLayout layoutToShow = layoutRootActiveIncidence;
        INotification.shared(getContext()).showNotification(layoutToShow, title, message, titleButton, titleCancelButton, listener, listenerCancel, false, false);
    }
    */

    private void reportedByMistakeAction() {
        if (incidence != null) {
            String message = getString(R.string.incidence_key_was_it_a_mistake);
            String mistake = getString(R.string.incidence_key_yes_it_was_a_mistake);
            String continueWithMistake = getString(R.string.incidence_key_no_continue_with_the_incidence);

            View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    cancelIncidence();
                }
            };

            View.OnClickListener listenerCancel = new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    closeThis();
                }
            };

            RelativeLayout layoutToShow = layoutRootActiveIncidence;
            INotification.shared(getContext()).showNotification(layoutToShow, "", message, mistake, continueWithMistake, listener, listenerCancel, true, true);
        }
    }

    private void cancelIncidence() {
        /*
        showHud();
        Api.cancelIncidence(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {
                if (response.isSuccess()) {
                    Core.removeData(Constants.KEY_LAST_INCIDENCE_REPORTED_DATE);
                    hideHud();
                    //closeThis();
                    INotification.shared(getContext()).hideNoAnimated();

                    mListener.showInitialFragment(HomeFragment.newInstance());
                } else {
                    hideHud();
                    onBadResponse(response);
                }
            }
        }, incidence.id);
        */
    }

    private void rateIncidence() {
        /*
        showHud();
        Api.closeIncidence(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {
                hideHud();
                if (response.isSuccess()) {
                    Core.removeData(Constants.KEY_LAST_INCIDENCE_REPORTED_DATE);

                    incidence.close();
                    INotification.shared(getContext()).hideNoAnimated();
                    //closeThis();
                    //showRateIncidenceActive();

                    mListener.showInitialFragment(HomeFragment.newInstance());
                    mListener.addFragment(IncidenceValorationFragment.newInstance(incidence));
                    //mListener.removeFragment(ActiveIncidenceDetailFragment.this, false);

                } else {
                    onBadResponse(response);
                }
            }
        }, incidence.id);
        */
    }
}

