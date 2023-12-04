package es.incidence.core.activity;

import android.os.Bundle;
import android.view.View;

import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.fragment.add.AddBeaconFragment;
import es.incidence.core.fragment.beacon.BeaconDetailFragment;
import es.incidence.core.fragment.beacon.BeaconListFragment;
import es.incidence.core.fragment.ecommerce.EcommerceFragment;
import es.incidence.core.fragment.error.ErrorFragment;
import es.incidence.core.fragment.incidence.ReportIncidenceSimpleFragment;
import es.incidence.core.fragment.incidence.report.IncidenceReportFragment;

public class SimpleMainActivity extends IActivity
{
    private String screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_activity_main);

        Bundle b = getIntent().getExtras();
        screen = b.getString("screen");

        findViewById(R.id.toolbar).setVisibility(View.GONE);

        if (Constants.SCREEN_DEVICE_LIST.equals(screen)) {
            showInitialFragment(BeaconListFragment.newInstance());
        } else if (Constants.SCREEN_DEVICE_CREATE.equals(screen)) {


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


            showInitialFragment(IncidenceReportFragment.newInstance(vehicle, false));
        }
    }
}
