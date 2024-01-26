package es.incidence.core.fragment.incidence.report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.manager.insuranceCall.InsuranceCallController;
import es.incidence.library.IncidenceLibraryManager;

public class AccidentFragment extends IncidenceReportFragment
{
    @Override
    public boolean needEventBus() {
        return true;
    }

    public static AccidentFragment newInstance(Vehicle vehicle, User user, boolean openFromNotification)
    {
        AccidentFragment fragment = new AccidentFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_VEHICLE, vehicle);
        bundle.putParcelable(KEY_USER, user);
        bundle.putBoolean(KEY_NOTIFICATION, openFromNotification);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        btnRed.setVisibility(View.GONE);
        btnBlue.setVisibility(View.GONE);
        speechManager.setUpListener(this);
    }

    @Override
    public void setUpVoiceLiterals() {
        speechRecognizion = new ArrayList<String>();
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_one", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_no_only_material_wounded", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_two", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_accident_with_wounded", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_three", getContext()));
        speechRecognizion.add(Core.getLiteralVoiceSDK("incidence_key_cancel", getContext()));

        voiceDialogs = new ArrayList<String>();
        voiceDialogs.add(Core.getLiteralVoiceSDK("incidence_key_ask_wounded", getContext()));
        voiceDialogs.addAll(speechRecognizion);
    }

    @Override
    public void addContent()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_incidence_accident, null);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());

        RelativeLayout layout1 = view.findViewById(R.id.layout1);
        TextView txtTitle1 = layout1.findViewById(R.id.txtTitle);
        txtTitle1.setText(R.string.incidence_key_no_only_material_wounded);
        layout1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onlyMaterial();
            }
        });

        RelativeLayout layout2 = view.findViewById(R.id.layout2);
        TextView txtTitle2 = layout2.findViewById(R.id.txtTitle);
        txtTitle2.setText(R.string.incidence_key_accident_with_wounded);
        txtTitle2.setTextColor(Utils.getColor(getContext(), R.color.error));
        layout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wounded();
            }
        });

        layoutContent.addView(view);


        if (IncidenceLibraryManager.instance.getInsurance() == null)
        {
            btnBlue.setDisabledColors();
            btnBlue.setOnClickListener(null);

            btnRed.setDisabledColors();
            btnRed.setOnClickListener(null);
        }
    }

    private void onlyMaterial()
    {
        speechStop();
        if (IncidenceLibraryManager.instance.getInsurance() != null && IncidenceLibraryManager.instance.getInsurance().phone != null)
        {
            InsuranceCallController.locateInsuranceCallPhone(getContext(), vehicle, new InsuranceCallController.LocationCallInsuranceListener() {
                @Override
                public void onGetPhone(String phone)
                {
                    if (phone != null && phone.length() > 0)
                    {
                        reportIncidence(Constants.ACCIDENT_TYPE_ONLY_MATERIAL+"", phone);
                    }
                }
            });
        }
        else
        {
            showAlert(R.string.incidence_key_alert_must_add_insurance);
        }
    }

    private void wounded()
    {
        speechStop();
        reportIncidence(Constants.ACCIDENT_TYPE_WOUNDED+"", Constants.PHONE_EMERGENCY);
    }

    @Override
    public void voiceRecognizionMatch(String string) {
        if (Core.getLiteralVoiceSDK("incidence_key_one", getContext()).toLowerCase().equals(string) || Core.getLiteralVoiceSDK("incidence_key_no_only_material_wounded", getContext()).toLowerCase().equals(string)) {
            onlyMaterial();
        } else if (Core.getLiteralVoiceSDK("incidence_key_two", getContext()).toLowerCase().equals(string) || Core.getLiteralVoiceSDK("incidence_key_accident_with_wounded", getContext()).toLowerCase().equals(string)) {
            wounded();
        } else if (Core.getLiteralVoiceSDK("incidence_key_three", getContext()).toLowerCase().equals(string) || Core.getLiteralVoiceSDK("incidence_key_cancel", getContext()).toLowerCase().equals(string)) {
            onClickCancel();
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
