package es.incidence.core.fragment.beacon;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

import com.e510.commons.utils.Utils;
import com.e510.incidencelibrary.R;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;

import es.incidence.core.Core;
import es.incidence.core.adapter.ListAdapter;
import es.incidence.core.domain.Beacon;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.ListItem;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.fragment.common.ListFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.view.INotification;

public class BeaconDataFragment extends ListFragment
{
    private static final String TAG = makeLogTag(BeaconDataFragment.class);

    private static final int ROW_NAME = 0;
    private static final int ROW_MODEL = 1;
    private static final int ROW_VEHICLE = 2;
    private static final int ROW_VINCULATE_BEACON = 3;
    private static final int ROW_DELETE_BEACON = 4;
    private static final int ROW_VALIDATE_BEACON = 5;

    public static final String KEY_BEACON = "KEY_BEACON";
    public Beacon beacon;

    public static BeaconDataFragment newInstance(Beacon beacon)
    {
        BeaconDataFragment fragment = new BeaconDataFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable(KEY_BEACON, beacon);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public boolean needEventBus() {
        return true;
    }

    @Override
    public int getTitleId() {
        return R.string.beacon;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            beacon = getArguments().getParcelable(KEY_BEACON);
        }
    }

    @Override
    public ListAdapter.Type getType() {
        return ListAdapter.Type.TITLE_SUBTITLE;
    }

    @Override
    public void loadData()
    {
        if (beacon != null)
        {
            setNavigationTitle(getString(R.string.beacon));

            boolean isPrincipal = false;
            if (beacon.vehicle != null)
            { //cogemos el vehicle de getVehicles porque ahí si informa los drivers
                Vehicle vehicle = Core.getVehicle(beacon.vehicle.id);
                isPrincipal = Core.isUserPrimaryForVehicle(vehicle);
            }

            if (isPrincipal)
            {
                setNavigationButtonRight(R.drawable.icon_edit);
                setOnNavigationButtonRightClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickRightNavButton();
                    }
                });
            }

            ArrayList<ListItem> temp = new ArrayList<>();

            ListItem l1 = new ListItem(getString(R.string.name), beacon.name);
            l1.object = ROW_NAME;
            temp.add(l1);

            ListItem l2 = new ListItem(getString(R.string.model), (beacon.beaconType != null) ? beacon.beaconType.name : "");
            l2.object = ROW_MODEL;
            temp.add(l2);

            ListItem l3 = new ListItem(getString(R.string.link_with), (beacon.vehicle != null) ? beacon.vehicle.getName() : "-");
            l3.object = ROW_VEHICLE;
            temp.add(l3);

            renewItems(temp);

            //bottom
            clearLayoutBottom();
            if (beacon.vehicle == null)
            {
                ListItem lBottom = new ListItem(getString(R.string.link_with_vehicle), ROW_VINCULATE_BEACON);
                addRowBottom(lBottom);
            }

            //if (beacon.iot != null)
            if (beacon.beaconType != null && beacon.beaconType.id != 1)
            {
                ListItem lBottom = new ListItem(getString(R.string.validate_device), ROW_VALIDATE_BEACON);
                addRowBottom(lBottom);
            }

            if (isPrincipal)
            {
                ListItem lBottom = new ListItem(getString(R.string.delete_device), ROW_DELETE_BEACON);
                lBottom.titleColor = Utils.getColor(getContext(), R.color.error);
                lBottom.arrowColor = Utils.getColor(getContext(), R.color.error);
                addRowBottom(lBottom);
            }
        }
    }

    @Override
    public void onClickRow(Object object)
    {
        if (object instanceof ListItem)
        {
            ListItem listItem = (ListItem) object;
            int row = (int) listItem.object;

            if (row == ROW_VINCULATE_BEACON)
            {
                goToLinkDevice();
            }
            else if (row == ROW_DELETE_BEACON)
            {
                showDeleteDevicePopUp();
            }
            else if (row == ROW_VALIDATE_BEACON)
            {
                goToBeaconDetail();
            }
        }
    }

    private void goToBeaconDetail()
    {
        //mListener.addFragmentAnimated(BeaconDetailFragment.newInstance(beacon));
    }

    private void goToLinkDevice()
    {
        Vehicle vehicle = beacon != null ? beacon.vehicle : null;
        //mListener.addFragmentAnimated(AddBeaconFragment.newInstance(0, 1, vehicle,true));
    }

    private void showDeleteDevicePopUp()
    {
        hideKeyboard();

        String title = null;
        int idString = (beacon.vehicle != null) ? R.string.delete_device_vinculated_message : R.string.delete_device_message;
        String name = (beacon.vehicle != null) ? beacon.vehicle.getName() : "";
        String message = getString(idString, name);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.cancel));
        options.add(getString(R.string.delete));
        ArrayList<Integer> optionsColors = new ArrayList<>();
        optionsColors.add(Utils.getColor(getContext(), R.color.black600));
        optionsColors.add(Utils.getColor(getContext(), R.color.error));

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getTag() != null)
                {
                    int index = (int)view.getTag();
                    if (index == 0)
                    {
                        //cancela
                    }
                    else if (index == 1)
                    {
                        //elimina
                        showHud();
                        Api.deleteBeacon(new IRequestListener() {
                            @Override
                            public void onFinish(IResponse response) {
                                hideHud();
                                if (response.isSuccess())
                                {
                                    if (beacon.vehicle != null) { //cogemos el vehicle de getVehicles porque ahí si informa los drivers
                                        Vehicle vehicle = Core.getVehicle(beacon.vehicle.id);
                                        if (vehicle != null)
                                        {
                                            vehicle.beacon = null;
                                            Core.saveVehicle(vehicle);
                                            EventBus.getDefault().post(new Event(EventCode.VEHICLE_UPDATED, vehicle));
                                        }
                                    }

                                    EventBus.getDefault().post(new Event(EventCode.BEACON_DELETED, beacon));
                                    closeThis();
                                }
                                else
                                {
                                    onBadResponse(response);
                                }
                            }
                        }, beacon);

                    }
                }
            }
        };

        RelativeLayout layoutToShow = getBaseActivity().findViewById(R.id.mainBaseLayout);
        INotification.shared(getContext()).showOptionsNotification(layoutToShow, title, message, options, optionsColors, listener);
    }

    public void onClickRightNavButton()
    {
        mListener.addFragmentAnimated(BeaconDataEditFragment.newInstance(beacon));
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event)
    {
        if (event.code == EventCode.BEACON_UPDATED)
        {
            beacon = (Beacon) event.object;
            loadData();
        }
    }
}