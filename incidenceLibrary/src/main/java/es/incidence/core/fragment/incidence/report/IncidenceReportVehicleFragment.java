package es.incidence.core.fragment.incidence.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.incidencelibrary.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.adapter.ListAdapter;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.ListItem;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.manager.SpeechManager;

public class IncidenceReportVehicleFragment extends IncidenceReportFragment
{
    private ArrayList<ListItem> items;
    private ListAdapter adapter;
    private boolean isAccident;
    private static final String IS_ACCIDENT = "IS_ACCIDENT";

    public static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
    private boolean openFromNotification;

    @Override
    public boolean needEventBus() {
        return true;
    }

    public static IncidenceReportVehicleFragment newInstance(boolean isAccident, boolean openFromNotification)
    {
        IncidenceReportVehicleFragment fragment = new IncidenceReportVehicleFragment();

        Bundle bundle = new Bundle();
        bundle.putBoolean(IS_ACCIDENT, isAccident);
        bundle.putBoolean(KEY_NOTIFICATION, openFromNotification);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            isAccident = getArguments().getBoolean(IS_ACCIDENT);
            openFromNotification = getArguments().getBoolean(KEY_NOTIFICATION);
        }
    }

    @Override
    public void setupUI(View rootView) {
        holdSpeech = true;

        super.setupUI(rootView);

        btnRed.setVisibility(View.GONE);
        btnBlue.setVisibility(View.GONE);

        speechManager.setUpListener(this);
        /*
        layoutNavRight.setVisibility(View.VISIBLE);
        imgNavTitleRight.setVisibility(View.VISIBLE);
        imgNavTitleRight.setImageDrawable(Utils.getDrawable(getContext(), R.drawable.icon_phone));
        imgNavTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone = Constants.PHONE_CONTACT;
                Core.callPhone(phone);
            }
        });
        */
    }

    @Override
    public void setUpVoiceLiterals() {
        speechRecognizion = new ArrayList<String>();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                ListItem item = items.get(i);
                if (getNumberName(i + 1) != null && !getNumberName(i + 1).isEmpty()) {
                    speechRecognizion.add(getNumberName(i + 1));
                }
                speechRecognizion.add(item.title);
            }
        }
        if (items != null) {
            if (getNumberName(items.size()+1) != null) {
                speechRecognizion.add(getNumberName(items.size()+1));
            }
        }
        speechRecognizion.add(Core.getLiteralVoice("cancel", getContext()).toLowerCase());

        voiceDialogs = new ArrayList<String>();
        voiceDialogs.add(Core.getLiteralVoice("ask_report_choose_vehicle", getContext()));
        voiceDialogs.addAll(speechRecognizion);
    }


    @Override
    public void addContent()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_incidence_rep_vehicle, null);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        ListView listView = view.findViewById(R.id.listView);
        items = new ArrayList<>();
        adapter = new ListAdapter(this, ListAdapter.Type.TITLE, items);
        listView.setAdapter(adapter);

        layoutContent.addView(view);
    }

    @Override
    public void loadData()
    {
        showHud();
        Api.getVehicles(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {

                hideHud();

                if (response.isSuccess())
                {
                    ArrayList<ListItem> temp = new ArrayList<>();

                    ArrayList<Vehicle> tItems = response.getList("vehicles", Vehicle.class);
                    if (tItems != null)
                    {
                        for (int i = 0; i < tItems.size(); i++)
                        {
                            Vehicle vehicle = tItems.get(i);
                            //Solo los que tengan aseguradora
                            if (vehicle.insurance != null)
                            {
                                ListItem li = new ListItem(vehicle.getName(), vehicle.image, vehicle);
                                temp.add(li);
                            }
                        }
                    }

                    items.clear();
                    items.addAll(temp);
                    adapter.notifyDataSetChanged();
                    setUpVoiceLiterals();
                    if (SpeechManager.isEnabled) {
                        startSpeech(false);
                    }
                }
                else
                {
                    onBadResponse(response);
                }
            }
        });
    }

    @Override
    public void onClickRow(Object object) {

        speechStop();
        isShowingFragment = false;

        ListItem listItem = (ListItem) object;
        this.vehicle = (Vehicle) listItem.object;

        EventBus.getDefault().post(new Event(EventCode.INCICENDE_VEHICLE_SELECTED, vehicle));

        if (isAccident)
        {
            mListener.addFragmentAnimated(AccidentFragment.newInstance(vehicle, openFromNotification));
        }
        else
        {
            int parent = 2; //Avería es 2
            mListener.addFragmentAnimated(FaultFragment.newInstance(parent, vehicle, openFromNotification));
        }
    }

    @Override
    public void voiceRecognizionMatch(String string) {
        if (getNumberValue(string) != null) {
            int number = getNumberValue(string);
            if (items.size() >= number) {
                ListItem item = items.get(number-1);
                onClickRow(item);
            } else if (items.size()+1 == number) {
                onClickCancel();
            }
        } else {
            if (Core.getLiteralVoice("cancel", getContext()).toLowerCase().equals(string)) {
                onClickCancel();
            } else {
                for (ListItem item : items) {
                    if (item.title.toLowerCase().replace(" ", "").equals(string)) {
                        onClickRow(item);
                    }
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event)
    {
        if (event.code == EventCode.INCICENDE_TIME_CHANGED)
        {
            setUpTimeAlert();
        } else {
            super.onMessageEvent(event);
        }
    }
}
