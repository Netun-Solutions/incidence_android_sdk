package es.incidence.core.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.fragment.add.AddBeaconFragment;
import es.incidence.core.fragment.beacon.BeaconDetailFragment;
import es.incidence.core.fragment.ecommerce.EcommerceFragment;
import es.incidence.core.fragment.error.ErrorFragment;
import es.incidence.core.fragment.incidence.ReportIncidenceSimpleFragment;
import es.incidence.core.fragment.incidence.report.IncidenceReportFragment;
import es.incidence.core.fragment.incidence.report.IncidenceReportOp1Fragment;
import es.incidence.core.manager.SettingsContentObserver;
import es.incidence.core.manager.SpeechManager;

public class SimpleMainActivity extends IActivity
{
    private String screen;

    private SettingsContentObserver mSettingsContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        Bundle b = getIntent().getExtras();
        screen = b.getString("screen");

        findViewById(R.id.toolbar).setVisibility(View.GONE);

        if (Constants.SCREEN_DEVICE_CREATE.equals(screen)) {
            User user = b.getParcelable("user");
            Vehicle vehicle = b.getParcelable("vehicle");

            showInitialFragment(AddBeaconFragment.newInstance(0, 1, vehicle, user, true));
        } else if (Constants.SCREEN_DEVICE_REVIEW.equals(screen)) {
            User user = b.getParcelable("user");
            Vehicle vehicle = b.getParcelable("vehicle");

            showInitialFragment(BeaconDetailFragment.newInstance(vehicle, user));
        } else if (Constants.FUNC_REPOR_INC.equals(screen)) {
            User user = b.getParcelable("user");
            Vehicle vehicle = b.getParcelable("vehicle");
            Incidence incidence = b.getParcelable("incidence");

            showInitialFragment(ReportIncidenceSimpleFragment.newInstance(vehicle, user, incidence, true));
        } else if (Constants.SCREEN_ECOMMERCE.equals(screen)) {
            User user = b.getParcelable("user");
            Vehicle vehicle = b.getParcelable("vehicle");

            showInitialFragment(EcommerceFragment.newInstance(vehicle, user));
        } else if (Constants.FUNC_CLOSE_INC.equals(screen)) {
            User user = b.getParcelable("user");
            Vehicle vehicle = b.getParcelable("vehicle");
            Incidence incidence = b.getParcelable("incidence");

            showInitialFragment(ReportIncidenceSimpleFragment.newInstance(vehicle, user, incidence, false));
        } else if (Constants.SCREEN_ERROR.equals(screen)) {
            String error = b.getString("error");

            showInitialFragment(ErrorFragment.newInstance(error));
        } else if (Constants.SCREEN_REPOR_INC.equals(screen)) {
            User user = b.getParcelable("user");
            Vehicle vehicle = b.getParcelable("vehicle");
            Boolean flowComplete = b.getBoolean("flowComplete");

            showInitialFragment(IncidenceReportFragment.newInstance(vehicle, user, false, flowComplete));
        } else if (Constants.SCREEN_REPOR_INC_SIMPLE_OP1.equals(screen)) {
            User user = b.getParcelable("user");
            Vehicle vehicle = b.getParcelable("vehicle");
            Boolean flowComplete = b.getBoolean("flowComplete");

            showInitialFragment(IncidenceReportOp1Fragment.newInstance(vehicle, user, false, flowComplete));
        }

        mSettingsContentObserver = new SettingsContentObserver(new Handler(), this);
        getApplicationContext().getContentResolver().registerContentObserver(
                android.provider.Settings.System.CONTENT_URI, true,
                mSettingsContentObserver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unregister broadcast listeners
        SpeechManager.destroy();
        getApplicationContext().getContentResolver().unregisterContentObserver(mSettingsContentObserver);
    }
}
