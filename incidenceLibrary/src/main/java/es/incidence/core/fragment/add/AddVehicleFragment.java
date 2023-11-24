package es.incidence.core.fragment.add;

import android.os.Bundle;

import androidx.annotation.Nullable;

import es.incidence.core.fragment.sign.SignUpVehicleFragment;

public class AddVehicleFragment extends SignUpVehicleFragment
{
    public static final String KEY_BECOME_FROM_ADD_BEACON = "KEY_BECOME_FROM_ADD_BEACON";
    public boolean becomeFromAddBeacon;

    public static AddVehicleFragment newInstance(boolean becomeFromAddBeacon)
    {
        AddVehicleFragment fragment = new AddVehicleFragment();
        fragment.becomeFromAddBeacon = becomeFromAddBeacon;

        Bundle bundle = new Bundle();
        bundle.putBoolean(KEY_BECOME_FROM_ADD_BEACON, becomeFromAddBeacon);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            becomeFromAddBeacon = getArguments().getBoolean(KEY_BECOME_FROM_ADD_BEACON);
        }
    }


    @Override
    public boolean isRegistration() {
        return false;
    }

    @Override
    public int getStepBlock() {
        return 0;
    }

    @Override
    public int getTotalSteps() {
        if (becomeFromAddBeacon)
            return 1;

        return 2;
    }

    @Override
    public void printNextBlock()
    {
        int nextBlock = getStepBlock()+1;
        if (nextBlock < getTotalSteps())
        {
            //mListener.addFragmentAnimated(AddBeaconFragment.newInstance(nextBlock, getTotalSteps(), newVehicleCreated,false));
        }
        else
        {
            //registro completo.
            closeThis();
        }
    }
}
