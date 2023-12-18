package es.incidence.core.fragment.incidence.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.domain.IncidenceType;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.manager.insuranceCall.InsuranceCallController;

public class FaultFragment extends IncidenceReportFragment
{
    public static final String KEY_PARENT = "KEY_PARENT";
    public int parent;

    public static final String KEY_NOTIFICATION = "KEY_NOTIFICATION";
    private boolean openFromNotification;

    @Override
    public boolean needEventBus() {
        return true;
    }

    public static FaultFragment newInstance(int parent, Vehicle vehicle, User user, boolean openFromNotification)
    {
        FaultFragment fragment = new FaultFragment();

        Bundle bundle = new Bundle();
        bundle.putInt(KEY_PARENT, parent);
        bundle.putParcelable(KEY_VEHICLE, vehicle);
        bundle.putParcelable(KEY_USER, user);
        bundle.putBoolean(KEY_NOTIFICATION, openFromNotification);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            parent = getArguments().getInt(KEY_PARENT);
            openFromNotification = getArguments().getBoolean(KEY_NOTIFICATION);
            vehicle = getArguments().getParcelable(KEY_VEHICLE);
            user = getArguments().getParcelable(KEY_USER);
        }
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        btnRed.setVisibility(View.GONE);
        btnBlue.setVisibility(View.GONE);

        layoutNavRight.setVisibility(View.VISIBLE);
        imgNavTitleRight.setVisibility(View.VISIBLE);
        imgNavTitleRight.setImageDrawable(Utils.getDrawable(getContext(), R.drawable.icon_phone));
        imgNavTitleRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InsuranceCallController.locateInsuranceCallPhone(getContext(), vehicle, new InsuranceCallController.LocationCallInsuranceListener() {
                    @Override
                    public void onGetPhone(String phone)
                    {
                        if (phone != null && phone.length() > 0)
                        {
                            Core.callPhone(phone);
                        }
                    }
                });
            }
        });
        speechManager.setUpListener(this);
    }


    @Override
    public void setUpVoiceLiterals() {
        speechRecognizion = new ArrayList<String>();
        ArrayList<IncidenceType> list = Core.getIncidencesTypes(parent);
        for (int i = 0; i < list.size(); i++)
        {
            IncidenceType incidence = list.get(i);
            if (getNumberName(i + 1) != null && !getNumberName(i + 1).isEmpty()) {
                speechRecognizion.add(getNumberName(i + 1));
            }
            speechRecognizion.add(incidence.name);
        }
        if (getNumberName(speechRecognizion.size()) != null) {
            speechRecognizion.add(getNumberName(list.size()+1));
        }
        speechRecognizion.add(Core.getLiteralVoice("cancel", getContext()).toLowerCase());

        voiceDialogs = new ArrayList<String>();
        if (parent > 2) {
            voiceDialogs.add(Core.getLiteralVoice("fault_fallo_title", getContext()));
        } else {
            voiceDialogs.add(Core.getLiteralVoice("ask_fault", getContext()));
        }
        voiceDialogs.addAll(speechRecognizion);
    }

    @Override
    public void addContent()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_incidence_fault, null);
        LinearLayout layoutRows = view.findViewById(R.id.layoutRows);

        ArrayList<IncidenceType> list = Core.getIncidencesTypes(parent);
        for (int i = 0; i < list.size(); i++)
        {
            IncidenceType it = list.get(i);

            View row = inflater.inflate(R.layout.row_incidence_fault, null);
            RelativeLayout layout1 = row.findViewById(R.id.layout1);
            TextView txtTitle1 = layout1.findViewById(R.id.txtTitle);
            txtTitle1.setText(it.name);
            layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    speechStop();
                    reportIncidence(it.id);
                }
            });

            layoutRows.addView(row);
        }

        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());
        layoutContent.addView(view);
    }

    private void reportIncidence(int incidenceTypeId)
    {
        speechStop();
        isShowingFragment = false;

        ArrayList<IncidenceType> list = Core.getIncidencesTypes(incidenceTypeId);
        if (list != null && list.size() > 0)
        {
            mListener.addFragmentAnimated(FaultFragment.newInstance(incidenceTypeId, vehicle, user, openFromNotification));
        }
        else
        {
            mListener.addFragmentAnimated(InsuranceCallingFragment.newInstance(vehicle, user, incidenceTypeId, openFromNotification));
        }
    }

    @Override
    public void voiceRecognizionMatch(String string) {
        ArrayList<IncidenceType> list = Core.getIncidencesTypes(parent);
        if (getNumberValue(string) != null) {
            int number = getNumberValue(string);
            if (list.size() >= number) {
                IncidenceType item = list.get(number-1);
                speechStop();
                reportIncidence(item.id);
            } else if (list.size()+1 == number) {
                onClickCancel();
            }
        } else {
            if (Core.getLiteralVoice("cancel", getContext()).toLowerCase().equals(string)) {
                onClickCancel();
            } else {
                for (IncidenceType item : list) {
                    if (item.name.toLowerCase().replace(" ", "").equals(string)) {
                        speechStop();
                        reportIncidence(item.id);
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