package es.incidence.core.fragment.add;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.Nullable;

import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.fragment.sign.SignUpBeaconFragment;

public class AddBeaconFragment extends SignUpBeaconFragment
{
    private int step;
    private int totalSteps;
    private boolean cleaned;
    private boolean finished;
    private boolean fromBeacon;

    private static final String TAG_STEP = "TAG_STEP";
    private static final String TAG_TOTAL_STEPS = "TAG_TOTAL_STEPS";
    private static final String TAG_FROM_ADD_BEACON = "TAG_FROM_ADD_BEACON";


    public static AddBeaconFragment newInstance(int step, int totalSteps, Vehicle vehicle, User user, Boolean fromBeacon)
    {
        AddBeaconFragment fragment = new AddBeaconFragment();
        fragment.step = step;
        fragment.totalSteps = totalSteps;
        fragment.fromBeacon = fromBeacon;

        Bundle bundle = new Bundle();
        bundle.putInt(TAG_STEP, step);
        bundle.putInt(TAG_TOTAL_STEPS, totalSteps);
        bundle.putParcelable(KEY_AUTO_SELECTED_VEHICLE, vehicle);
        bundle.putParcelable(KEY_AUTO_SELECTED_USER, user);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            step = getArguments().getInt(TAG_STEP, 0);
            totalSteps = getArguments().getInt(TAG_TOTAL_STEPS, 1);
        }
    }

    @Override
    public Boolean fromAddBeacon() {
        return fromBeacon;
    }

    @Override
    public boolean isRegistration() {
        return false;
    }

    @Override
    public boolean onBackPressed() {
        boolean res = isScanning;

        if (isIoT)
        {
            res = super.onBackPressed();
        }
        else if (!finished && !res && !cleaned && getTotalSteps() > 1)
        {
            cleaned = true;
            //mListener.cleanAllBackStackEntries();
            //Para no hacer el clean directamente desde el onBackPressed, sino cerraba toda la app.
            res = true;
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mListener.cleanAllBackStackEntries();
                }
            }, 500);
        }

        return res;
    }

    @Override
    public int getStepBlock() {
        return step;
    }

    @Override
    public int getTotalSteps() {
        return totalSteps;
    }

    @Override
    public void printNextBlock()
    {
        int nextBlock = getStepBlock()+1;
        if (nextBlock < getTotalSteps())
        {
            //mListener.addFragmentAnimated(AddBeaconFragment.newInstance());
        }
        else
        {
            //registro completo.
            finished = true;
            getActivity().finish();
        }
    }
}
