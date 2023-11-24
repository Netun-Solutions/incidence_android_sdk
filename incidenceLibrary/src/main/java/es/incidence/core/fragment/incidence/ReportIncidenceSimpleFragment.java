package es.incidence.core.fragment.incidence;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.view.INavigation;
import es.incidence.library.IncidenceLibraryManager;

public class ReportIncidenceSimpleFragment extends IFragment
{
    private static final String TAG = makeLogTag(ReportIncidenceSimpleFragment.class);
    public static final String KEY_AUTO_SELECTED_VEHICLE = "KEY_AUTO_SELECTED_VEHICLE";
    public static final String KEY_AUTO_SELECTED_USER = "KEY_AUTO_SELECTED_USER";
    public static final String KEY_AUTO_SELECTED_INCIDENCE = "KEY_AUTO_SELECTED_INCIDENCE";


    public static final String KEY_CREATE_INCIDENCE = "KEY_CREATE_INCIDENCE";

    public Vehicle autoSelectedVehicle;
    public User autoSelectedUser;
    public Incidence incidence;
    private boolean createIncidence;

    private Button btnCreate;

    public static ReportIncidenceSimpleFragment newInstance(Vehicle vehicle, User user, Incidence incidence, boolean createIncidence)
    {
        ReportIncidenceSimpleFragment fragment = new ReportIncidenceSimpleFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_AUTO_SELECTED_INCIDENCE, incidence);
        bundle.putParcelable(KEY_AUTO_SELECTED_VEHICLE, vehicle);
        bundle.putParcelable(KEY_AUTO_SELECTED_USER, user);
        bundle.putBoolean(KEY_CREATE_INCIDENCE, createIncidence);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getTitleId() {
        if (createIncidence) {
            return R.string.report_incidence;
        } else {
            return R.string.close_incidence;
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            autoSelectedVehicle = getArguments().getParcelable(KEY_AUTO_SELECTED_VEHICLE);
            autoSelectedUser = getArguments().getParcelable(KEY_AUTO_SELECTED_USER);
            incidence = getArguments().getParcelable(KEY_AUTO_SELECTED_INCIDENCE);
            createIncidence = getArguments().getBoolean(KEY_CREATE_INCIDENCE);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_create_incidence, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        INavigation navigation = rootView.findViewById(R.id.inavigation);
        navigation.init(this, getString(getTitleId()), true);

        RelativeLayout layoutRootReportIncidence = rootView.findViewById(R.id.layoutRootReportIncidence);

        btnCreate = rootView.findViewById(R.id.btnCreate);
        btnCreate.setText(getTitleId());
        //btnDeviceCreate.setPrimaryColors();
        FontUtils.setTypeValueText(btnCreate, Constants.FONT_SEMIBOLD, getContext());
        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBlue();
            }
        });

        IncidenceLibraryManager.instance.setViewBackground(layoutRootReportIncidence);
    }

    private void onClickBlue() {
        showHud();
        if (createIncidence) {
            Api.postIncidenceSdk(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    hideHud();
                    if (response.isSuccess())
                    {



                        closeThis();
                    }
                    else
                    {
                        onBadResponse(response);
                    }
                }
            }, autoSelectedUser, autoSelectedVehicle, incidence);
        }
        else {
            Api.putIncidenceSdk(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    hideHud();
                    if (response.isSuccess())
                    {



                        closeThis();
                    }
                    else
                    {
                        onBadResponse(response);
                    }
                }
            }, autoSelectedUser, autoSelectedVehicle, incidence);
        }
    }
}