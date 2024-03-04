package es.incidence.core.fragment.beacon;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.e510.commons.utils.FontUtils;
import com.e510.incidencelibrary.R;

import es.incidence.core.Constants;
import es.incidence.core.domain.Beacon;
import es.incidence.core.fragment.IFragment;
import es.incidence.core.manager.ImageManager;
import es.incidence.core.utils.view.IButton;
import es.incidence.core.utils.view.INavigation;
import es.incidence.core.utils.view.IStepper;
import es.incidence.library.IncidenceLibraryManager;

public class BeaconDetailInfoFragment extends IFragment
{
    private static final String TAG = makeLogTag(BeaconDetailInfoFragment.class);

    public static final String KEY_BEACON = "KEY_BEACON";

    private INavigation navigation;
    public IStepper stepper;
    public IButton btnBlue;

    public TextView txtHeaderBeacon;
    public ImageView imageBeacon;

    private Beacon beacon;

    private int position = 0;

    public static BeaconDetailInfoFragment newInstance(Beacon beacon)
    {
        BeaconDetailInfoFragment fragment = new BeaconDetailInfoFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BEACON, beacon);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getTitleId() {
        return R.string.incidence_key_beacon;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            beacon = getArguments().getParcelable(KEY_BEACON);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_beacon_detail_info, container, false);
        setupUI(view);

        return view;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        navigation = rootView.findViewById(R.id.inavigation);
        navigation.setBackClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!onBackPressed()) {
                    closeThis();
                }
            }
        });

        stepper = rootView.findViewById(R.id.istepper);
        float[] steps = new float[]{1f, 0f, 0f};
        stepper.init(this, steps);

        String title = beacon.name;
        navigation.init(this, title, true);

        btnBlue = rootView.findViewById(R.id.btnBlue);
        btnBlue.setText(getString(R.string.incidence_key_continuar));
        btnBlue.setPrimaryColors();
        FontUtils.setTypeValueText(btnBlue, Constants.FONT_SEMIBOLD, getContext());
        btnBlue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickBlue();
            }
        });

        txtHeaderBeacon = rootView.findViewById(R.id.txtHeaderBeacon);
        imageBeacon = rootView.findViewById(R.id.imageBeacon);

        if (beacon.beaconType != null && beacon.beaconType.imageBeaconScreen1 != null) {
            ImageManager.loadImage(getContext(), beacon.beaconType.imageBeaconScreen1, null, imageBeacon, true);
        } else {
            imageBeacon.setImageResource(R.drawable.device_info_1);
        }

        if (beacon.beaconType != null && beacon.beaconType.textBeaconScreen1 != null) {
            txtHeaderBeacon.setText(beacon.beaconType.textBeaconScreen1);
        } else {
            txtHeaderBeacon.setText(R.string.incidence_key_device_desc_info1);
        }

        IncidenceLibraryManager.instance.setViewBackground(rootView);
        IncidenceLibraryManager.instance.setButtonBackground(btnBlue);
        IncidenceLibraryManager.instance.setButtonTextColor(btnBlue);
    }

    private void onClickBlue() {

        if (position == 0) {
            if (beacon.beaconType != null && beacon.beaconType.imageBeaconScreen2 != null) {
                ImageManager.loadImage(getContext(), beacon.beaconType.imageBeaconScreen2, null, imageBeacon, true);
            } else {
                imageBeacon.setImageResource(R.drawable.device_info_2);
            }

            if (beacon.beaconType != null && beacon.beaconType.textBeaconScreen2 != null) {
                txtHeaderBeacon.setText(beacon.beaconType.textBeaconScreen2);
            } else {
                txtHeaderBeacon.setText(getString(R.string.incidence_key_device_desc_info2));
            }

            position = 1;

            float[] steps = new float[]{1f, 1f, 0f};
            stepper.setSteps(steps);

        } else if (position == 1) {
            if (beacon.beaconType != null && beacon.beaconType.imageBeaconScreen3 != null) {
                ImageManager.loadImage(getContext(), beacon.beaconType.imageBeaconScreen3, null, imageBeacon, true);
            } else {
                imageBeacon.setImageResource(R.drawable.device_info_3);
            }

            if (beacon.beaconType != null && beacon.beaconType.textBeaconScreen3 != null) {
                txtHeaderBeacon.setText(beacon.beaconType.textBeaconScreen3);
            } else {
                txtHeaderBeacon.setText(getString(R.string.incidence_key_device_desc_info3));
            }

            position = 2;

            float[] steps = new float[]{1f, 1f, 1f};
            stepper.setSteps(steps);

            btnBlue.setText(getString(R.string.incidence_key_device_desc_info_finish));
        } else if (position == 2) {
            closeThis();
        }
    }
}