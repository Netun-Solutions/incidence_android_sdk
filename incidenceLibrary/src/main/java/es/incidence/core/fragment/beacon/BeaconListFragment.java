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
import es.incidence.core.domain.Beacon;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.ListItem;
import es.incidence.core.entity.event.Event;
import es.incidence.core.entity.event.EventCode;
import es.incidence.core.fragment.common.ListFragment;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.core.utils.view.INotification;

public class BeaconListFragment extends ListFragment
{
    private static final String TAG = makeLogTag(BeaconListFragment.class);

    private ArrayList<Beacon> allBeacons = new ArrayList<>();

    private View addBeaconView;
    private RelativeLayout layoutRoot;

    public static BeaconListFragment newInstance()
    {
        BeaconListFragment fragment = new BeaconListFragment();

        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
        }
    }

    @Override
    public boolean needEventBus() {
        return true;
    }

    @Override
    public int getTitleId() {
        return R.string.devices;
    }

    @Override
    public void setupUI(View rootView) {
        super.setupUI(rootView);

        /*
        LayoutInflater inflater = LayoutInflater.from(getContext());
        addBeaconView = inflater.inflate(R.layout.row_list, null);
        FontUtils.setTypeValueText(addBeaconView, Constants.FONT_REGULAR, getContext());
        addBeaconView.findViewById(R.id.imgLeft).setVisibility(View.GONE);
        TextView txtTitle = addBeaconView.findViewById(R.id.txtTitle);
        txtTitle.setText(getString(R.string.add_new_device));
        txtTitle.setTextColor(Utils.getColor(getContext(), R.color.incidence500));
        ImageView imgRight = addBeaconView.findViewById(R.id.imgRight);
        imgRight.setImageDrawable(Utils.getDrawable(getContext(), R.drawable.icon_plus));

        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) imgRight.getLayoutParams();
        params.width = Utils.dpToPx(14);
        params.height = Utils.dpToPx(14);
        params.rightMargin = Utils.dpToPx(21);
        imgRight.setLayoutParams(params);

        addBeaconView.setVisibility(View.GONE);
        addBeaconView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.addFragmentAnimated(AddBeaconFragment.newInstance(0, 1));
            }
        });
        layoutContent.addView(addBeaconView);
        */
    }

    @Override
    public void loadData()
    {
        allBeacons.clear();
        ArrayList<Vehicle> vehicles = Core.getVehicles();
        for (Vehicle vehicle: vehicles) {
            allBeacons.add(vehicle.beacon);
        }
        refresh();
        /*
        showHud();
        Api.getBeacons(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {

                hideHud();

                if (response.isSuccess())
                {
                    allBeacons = response.getList("beacons", Beacon.class);
                    refresh();
                }
                else
                {
                    onBadResponse(response);
                }
            }
        });
        */
    }

    private void refresh()
    {
        ArrayList<ListItem> temp = new ArrayList<>();

        if (allBeacons != null)
        {
            for (int i = 0; i < allBeacons.size(); i++)
            {
                Beacon b1 = allBeacons.get(i);
                String drawable = b1.beaconType != null && b1.beaconType.id == 1 ? "beacon_icon_smart" : "beacon_icon_iot";
                ListItem li = new ListItem(b1.name, drawable, b1);
                li.leftDrawableSize = Utils.dpToPx(24);
                //li.exclamation = b1.vehicle == null;
                li.exclamation = false;
                temp.add(li);
            }
        }

        //Añadimos el añadir
        /*
        ListItem li = new ListItem(getString(R.string.add_new_device), null);
        li.object = "add_item";
        li.titleColor = Utils.getColor(getContext(), R.color.incidence500);
        li.rightDrawable = Utils.getDrawable(getContext(), R.drawable.icon_plus);
        li.rightDrawableSize = Utils.dpToPx(14);
        temp.add(li);
        */
        renewItems(temp);

        /*
        if (temp.size() == 0)
        {
            addBeaconView.setVisibility(View.VISIBLE);
        }
        else
        {
            addBeaconView.setVisibility(View.GONE);
        }
        */
    }

    @Override
    public void onClickRow(Object object)
    {
        ListItem listItem = (ListItem) object;
        /*
        if (listItem.object instanceof String)
        {
            //mListener.addFragmentAnimated(AddBeaconFragment.newInstance(0, 1, null, null, true));
        }
        else
        {
            Beacon beacon = (Beacon) listItem.object;
            mListener.addFragmentAnimated(BeaconDataFragment.newInstance(beacon));
        }
        */
        //Beacon beacon = new Beacon();
        Beacon beacon = (Beacon) listItem.object;
        showDeleteDevicePopUp(beacon);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(Event event)
    {
        if (event.code == EventCode.BEACON_DELETED)
        {
            Beacon beacon = (Beacon) event.object;
            ArrayList <Beacon> temp = new ArrayList<>();

            if (allBeacons != null) {
                for (int i = 0; i < allBeacons.size(); i++) {
                    Beacon b1 = allBeacons.get(i);
                    if (b1.id == beacon.id) {
                    } else {
                        temp.add(b1);
                    }
                }
            }
            allBeacons = temp;
            refresh();
        }
        else if (event.code == EventCode.BEACON_UPDATED)
        {
            Beacon beacon = (Beacon) event.object;
            ArrayList <Beacon> temp = new ArrayList<>();

            if (allBeacons != null) {
                for (int i = 0; i < allBeacons.size(); i++) {
                    Beacon b1 = allBeacons.get(i);
                    if (b1.id == beacon.id) {
                        temp.add(beacon);
                    } else {
                        temp.add(b1);
                    }
                }
            }
            allBeacons = temp;
            refresh();
        }
    }

    private void showDeleteDevicePopUp(Beacon beacon)
    {
        hideKeyboard();

        String title = null;
        int idString = (beacon.vehicle != null) ? R.string.delete_device_vinculated_message : R.string.delete_device_message;
        String name = (beacon.vehicle != null) ? beacon.vehicle.getName() : "";
        String message = getString(idString, name);
        ArrayList<String> options = new ArrayList<>();
        options.add(getString(R.string.cancel));
        options.add(getString(R.string.validate_device));
        options.add(getString(R.string.delete));
        ArrayList<Integer> optionsColors = new ArrayList<>();
        optionsColors.add(Utils.getColor(getContext(), R.color.black600));
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
                    else if (index == 1) {
                        //mListener.addFragmentAnimated(BeaconDetailFragment.newInstance(beacon));
                    }
                    else if (index == 2)
                    {
                        User user = Core.getUser();
                        Vehicle vehicle = Core.getVehicleFromBeacon(beacon.id+"");

                        //elimina
                        showHud();
                        Api.deleteBeaconSdk(new IRequestListener() {
                            @Override
                            public void onFinish(IResponse response) {
                                hideHud();
                                if (response.isSuccess())
                                {
                                    /*
                                    if (beacon.vehicle != null) { //cogemos el vehicle de getVehicles porque ahí si informa los drivers
                                        Vehicle vehicle = Core.getVehicle(beacon.vehicle.id);
                                        if (vehicle != null)
                                        {
                                            vehicle.beacon = null;
                                            Core.saveVehicle(vehicle);
                                            EventBus.getDefault().post(new Event(EventCode.VEHICLE_UPDATED, vehicle));
                                        }
                                    }
                                    */

                                    Vehicle v = Core.getVehicleFromBeacon(beacon.id+"");
                                    Core.deleteVehicle(v);

                                    EventBus.getDefault().post(new Event(EventCode.BEACON_DELETED, beacon));
                                    closeThis();
                                }
                                else
                                {
                                    onBadResponse(response);
                                }
                            }
                        }, user, vehicle);

                    }
                }
            }
        };

        RelativeLayout layoutToShow = getBaseActivity().findViewById(R.id.mainBaseLayout);
        INotification.shared(getContext()).showOptionsNotification(layoutToShow, title, message, options, optionsColors, listener);
        /*
        IdentityType dniIdentityType = new IdentityType();
        dniIdentityType.name = "dni"; // (tipo de documento de identidad: dni, nie, cif)

        VehicleType vehicleType = new VehicleType();
        vehicleType.name = "Coche";

        Policy policy = new Policy();
        policy.policyNumber = "127864736"; // (número de la póliza)
        policy.policyEnd = "2024-10-09"; // (fecha caducidad de la póliza)
        policy.identityType = dniIdentityType; // (tipo de documento identidad del asegurador)
        policy.dni = "00000000T"; // (documento de identidad del asegurador)

        ColorType color = new ColorType();
        color.name = "Rojo";

        User user = new User();
        user.externalUserId = "10453"; // (identificador externo del usuario)
        user.name = "Nombre TEST"; // (nombre del usuario)
        user.phone = "600001001"; // (teléfono)
        user.email = "sdkm@tridenia.com"; // (e-mail)
        user.identityType = dniIdentityType;
        user.dni = "87114879S"; // (número del documento de identidad)
        user.birthday = "1979-09-29"; // (fecha de Nacimiento)
        user.checkTerms = "1"; // (aceptación de la privacidad)

        Vehicle vehicle = new Vehicle();
        vehicle.licensePlate = "0011XXX"; // (matrícula del vehículo)
        vehicle.registrationYear = "2022"; // (fecha de matriculación)
        vehicle.vehicleType = vehicleType; // (tipo del vehículo)
        vehicle.brand = "Seat"; // (marca del vehículo)
        vehicle.model = "Laguna"; // (modelo del vehículo)
        vehicle.color = color; // (color del vehículo)
        vehicle.policy = policy;

        showHud();
        Api.deleteBeaconSdk(new IRequestListener() {
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

                    Vehicle v = Core.getVehicleFromBeacon(beacon.id+"");
                    Core.deleteVehicle(v);

                    EventBus.getDefault().post(new Event(EventCode.BEACON_DELETED, beacon));
                    closeThis();
                }
                else
                {
                    ArrayList<Vehicle> vehicles = Core.getVehicles();
                    for (Vehicle vehicle: vehicles) {
                        Core.deleteVehicle(vehicle);
                    }


                    onBadResponse(response);
                }
            }
        }, user, vehicle);
         */
    }
}
