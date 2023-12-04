package es.incidence.core.fragment.incidence.report;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.manager.IResponse;
import es.incidence.core.manager.ImageManager;
import es.incidence.core.manager.insuranceCall.InsuranceCallController;
import es.incidence.core.manager.insuranceCall.InsuranceCallDelegate;

public class InsuranceCallingFragment extends IncidenceReportFragment implements InsuranceCallDelegate
{
    public static final String KEY_INCIDENCE = "KEY_INCIDENCE";
    private int incidenceTypeId;

    private boolean isSuccessReported = false;
    private Handler handlerSuccessReported;
    private Runnable runnableSuccessReported;

    @Override
    public boolean needEventBus() {
        return true;
    }

    public static InsuranceCallingFragment newInstance(Vehicle vehicle, int incidenceTypeId, boolean openFromNotification)
    {
        InsuranceCallingFragment fragment = new InsuranceCallingFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_VEHICLE, vehicle);
        bundle.putInt(KEY_INCIDENCE, incidenceTypeId);
        bundle.putBoolean(KEY_NOTIFICATION, openFromNotification);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(getArguments() != null) {
            incidenceTypeId = getArguments().getInt(KEY_INCIDENCE);
        }
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        btnRed.setVisibility(View.GONE);
        btnBlue.setText(R.string.accept);
        speechManager.setUpListener(this);
    }

    @Override
    public void setUpVoiceLiterals() {
        speechRecognizion = new ArrayList<String>();
        speechRecognizion.add(Core.getLiteralVoice("one", getContext()));
        speechRecognizion.add(Core.getLiteralVoice("accept", getContext()));
        speechRecognizion.add(Core.getLiteralVoice("two", getContext()));
        speechRecognizion.add(Core.getLiteralVoice("cancel", getContext()));

        voiceDialogs = new ArrayList<String>();
        if (vehicle.insurance.textIncidence != null)
        {
            voiceDialogs.add(vehicle.insurance.textIncidence);
        }
        voiceDialogs.add(Core.getLiteralVoice("incidence_tip_beacon", getContext()));
        voiceDialogs.add(Core.getLiteralVoice("incidence_tip_lights", getContext()));
        voiceDialogs.add(Core.getLiteralVoice("incidence_tip_vest", getContext()));
        voiceDialogs.add(Core.getLiteralVoice("incidence_tip_exit_car", getContext()));
        voiceDialogs.addAll(speechRecognizion);
    }

    @Override
    public void addContent()
    {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(R.layout.layout_incidence_insurance_calling, null);
        FontUtils.setTypeValueText(view, Constants.FONT_REGULAR, getContext());
        TextView txtDescription = view.findViewById(R.id.txtDescription);

        RelativeLayout layoutTips = view.findViewById(R.id.layoutTips);
        FontUtils.setTypeValueText(layoutTips, Constants.FONT_SEMIBOLD, getContext());
        GradientDrawable back =  Utils.createGradientDrawable(getContext(), R.color.incidence300, (int) 8);
        back.setAlpha(20);
        layoutTips.setBackground(back);

        ImageView imgInsurance = view.findViewById(R.id.imgInsurance);
        if (vehicle != null && vehicle.insurance != null)
        {
            ImageManager.loadImage(getContext(), vehicle.insurance.image, imgInsurance);

            if (vehicle.insurance.textIncidence != null)
            {
                txtDescription.setText(vehicle.insurance.textIncidence);
            }
        }
        else
        {
            btnBlue.setDisabledColors();
            btnBlue.setOnClickListener(null);
        }

        layoutContent.addView(view);
    }

    @Override
    public void onClickBlue()
    {
        //mListener.addFragmentAnimated(ReportMapFragment.newInstance(vehicle));
        speechStop();
        reportIncidence();
        EventBus.getDefault().post(new Event(EventCode.INCICENDE_TIME_STOP));
    }

    @Override
    public void onClickCancel() {
        speechStop();
        mListener.cleanAllBackStackEntries();
    }

    private void reportIncidence()
    {
        showHud();
        InsuranceCallController.reportIncidence(getContext(), this, incidenceTypeId, vehicle, this, openFromNotification);
    }

    @Override
    public void onLocationErrorResult()
    {
        hideHud();
        showAlert(R.string.alert_error_get_location_message);
    }

    @Override
    public void onBadResponseReport(IResponse response)
    {
        hideHud();
        onBadResponse(response);
    }

    @Override
    public void onSuccessReportToCall(Incidence incidence)
    {
        isSuccessReported = true;

        //check por si acaso
        runnableSuccessReported = new Runnable()
        {
            @Override
            public void run()
            {
                close();
            }
        };
        handlerSuccessReported = new Handler();
        handlerSuccessReported.postDelayed(runnableSuccessReported, 4000);
    }

    @Override
    public void onSuccessReport(Incidence incidence)
    {
        close();
    }

    private void close()
    {
        if (handlerSuccessReported != null && runnableSuccessReported != null) {
            handlerSuccessReported.removeCallbacks(runnableSuccessReported);
        }

        hideHud();
        mListener.cleanAllBackStackEntries();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event)
    {
        if (event.code == EventCode.APP_WILL_RESIGN_ACTIVE || event.code == EventCode.APP_DID_BECOME_ACTIVE)
        {
            if (isSuccessReported)
            {
                close();
            }
        } else if (event.code == EventCode.INCICENDE_TIME_CHANGED)
        {
            setUpTimeAlert();
        } else {
            super.onMessageEvent(event);
        }
    }

    @Override
    public void voiceRecognizionMatch(String string) {
        if (Core.getLiteralVoice("one", getContext()).toLowerCase().equals(string) || Core.getLiteralVoice("accept", getContext()).toLowerCase().equals(string)) {
            onClickBlue();
        } else if (Core.getLiteralVoice("two", getContext()).toLowerCase().equals(string) || Core.getLiteralVoice("cancel", getContext()).toLowerCase().equals(string)) {
            onClickCancel();
        }
    }
}