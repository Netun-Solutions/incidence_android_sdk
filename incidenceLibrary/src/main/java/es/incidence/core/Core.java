package es.incidence.core;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.e510.commons.domain.Device;
import com.e510.commons.utils.DeviceUtils;
import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.Prefs;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.incidencelibrary.BuildConfig;
import com.e510.incidencelibrary.R;
import com.e510.networking.Mapper;
import com.google.gson.Gson;

import org.json.JSONObject;
import org.michaelbel.bottomsheet.BottomSheet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

import dev.b3nedikt.restring.Restring;
import dev.b3nedikt.reword.RewordInterceptor;
import dev.b3nedikt.viewpump.ViewPump;
import es.incidence.core.domain.BeaconType;
import es.incidence.core.domain.ColorType;
import es.incidence.core.domain.DeviceNotification;
import es.incidence.core.domain.Driver;
import es.incidence.core.domain.IDevice;
import es.incidence.core.domain.IncidenceType;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.domain.VehicleType;
import es.incidence.core.manager.Api;
import es.incidence.library.config.Environment;
//import es.incidence.core.manager.beacon.BeaconManager;
//import es.incidence.core.manager.beacon.BeaconService;
//import io.sentry.Sentry;

public class Core {
    private static final String TAG = makeLogTag(Core.class);
    //private static BaseApplication application;
    private static Application application;
    //public static SearchResult manualAddressSearchResult;

    //Android Auto
    public static Vehicle ANDROID_AUTO_vehicle;
    public static Integer ANDROID_AUTO_incidenceId;

    public static Integer ANDROID_AUTO_home_option_clicked;
    public static Vehicle ANDROID_AUTO_home_option_clicked_vehicle;


    public static void cleanAndroidAuto()
    {
        ANDROID_AUTO_vehicle = null;
        ANDROID_AUTO_incidenceId = null;
    }

    //public static void init(BaseApplication app) {
    public static void init(Application app, String apikey, Environment environment) {
        application = app;
        prepareConfigs(apikey, environment);
        Restring.init(app);
        ViewPump.init(RewordInterceptor.INSTANCE);
    }

    private static void prepareConfigs(String apikey, Environment environment) {
        if (BuildConfig.DEBUG) {
            LogUtil.config("", true);
        }

        Constants.setBaseUrl(environment);

        //JSON Config
        /*
        AppConfiguration.AppConfigurationListener appConfigurationListener = new AppConfiguration.AppConfigurationListener() {
            @Override
            public void onUpdated() {
                BaseActivity baseActivity = application.getCurrentActivity();
                baseActivity.showAlert(baseActivity.getString(R.string.app_name), baseActivity.getString(R.string.app_update_alert_message), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        application.restartApp();
                    }
                });
            }
        };
        */
        AppConfiguration.init(application, null);

        //Firebase
        //Firebase.init(application);

        //Api Networking
        Api.init(application, apikey);
    }

    public static void signOut() {
        removeData(Constants.KEY_USER);
        removeData(Constants.KEY_USER_TOKEN);
        removeData(Constants.KEY_USER_DEFAULT_VEHICLE_ID);
        removeData(Constants.KEY_USER_VEHICLES);
        removeData(Constants.KEY_USER_DEVICE_NOTIFICATIONS);
        removeData(Constants.KEY_LAST_INCIDENCE_REPORTED_DATE);
        saveData(Constants.KEY_USER_SIGNOUT, "1");

        //application.restartApp();
    }
    /*
    public static void startApp(BaseActivity currentActivity) {
        Intent intent;
        intent = new Intent(currentActivity, MainActivity.class);
        currentActivity.startActivity(intent);
        currentActivity.finish();
    }
    */

    public static void saveData(String key, String value) {
        Prefs.saveData(application, key, value);
    }

    public static String loadData(String key) {
        return Prefs.loadData(application, key);
    }

    public static void removeData(String key) {
        Prefs.removeData(application, key);
    }

    public static void updateApiLang() {
        Api.updateLang();
    }
    public static void registerDevice() {
        registerDevice(true, false);
    }
    public static void registerDeviceSdk() {
        registerDevice(true, true);
    }
    public static void registerDevice(boolean checkToken, boolean sdk) {
        Device d = DeviceUtils.getDevice(application);

        IDevice device = new IDevice();
        device.uuid = d.getDeviceId();
        //device.token = Firebase.getInstance().getPushToken();//d.getPushId();
        device.platform = d.getPlatform();
        device.version = d.getOs();
        device.manufacturer = Build.MANUFACTURER;
        device.model = Build.MODEL;
        device.appVersion = d.getAppVersion();
        device.appVersionNumber = d.getBuild();

        if (checkToken && device.token == null)
        {
            new Handler().postDelayed(new Runnable()
            {
                @Override
                public void run()
                {
                    //if (!BaseApplication.INSTANCE.isApplicationOnBackground())
                        registerDevice(false, sdk);
                }
            }, 4000);
        }

        if (checkToken || (!checkToken && device.token != null)) {
            if (sdk) Api.updateDeviceSdk(null, device);
            else Api.updateDevice(null, device);
        }
    }

    public static void getGeneralData() {
        if (loadData(Constants.KEY_USER_TOKEN) != null) {
            Api.getGeneralData(null);

            User user = getUser();
            if (user != null && user.id == null) { //para solucionar que antes no se enviaba el id
                Api.updateUser(null, user.name, user.phone, null, null, null, null, null);
            }
        }

        Api.getGlobals(null);
    }
    /*
    public static void trackGeoposition() {
        if (LocationManager.hasPermission(application)) {
            LocationManager.getLocation(application, new LocationManager.LocationListener() {
                @Override
                public void onLocationResult(Location location) {
                    Api.trackGeoposition(null, location.getLatitude() + "", location.getLongitude() + "");
                }
            });
        }
    }
    */
    public static void updateLiterals(boolean forceUpdate) {
        String valores = loadData(Constants.KEY_LITERALS_VALUES);
        if (valores != null) {
            LinkedHashMap<String, String> list = (LinkedHashMap<String, String>) Mapper.get(LinkedHashMap.class, valores);
            if (list != null) {
                final Map<String, CharSequence> map = new HashMap<>();

                for (Map.Entry<String, String> entry : list.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    value = value.replaceAll("%@", "%1s");
                    value = value.replaceAll("%02d:%02d", "%1s");
                    map.put(key, value);
                }

                Restring.putStrings(Locale.ENGLISH, map);

                Locale spanish = new Locale("es", "ES");
                Restring.putStrings(spanish, map);

                Locale current = application.getResources().getConfiguration().locale;
                Restring.putStrings(current, map);

                /* No hacemos reword porque sino los fields que le hemos sustituido el texto pod sus valores se vuelven a reiniciar con el string original
                final BaseActivity activity = application.getCurrentActivity();
                if (activity != null) {
                    final View rootView = activity.getWindow().getDecorView().findViewById(android.R.id.content);
                    Reword.reword(rootView);
                }*/

                if (forceUpdate)
                {
                    //application.restartApp();
                }
            }
        }
    }

    public static String getLiteral(String stringId) {
        String res = null;

        String valores = loadData(Constants.KEY_LITERALS_VALUES);
        if (valores != null) {
            LinkedHashMap<String, String> list = (LinkedHashMap<String, String>) Mapper.get(LinkedHashMap.class, valores);
            res = list.get(stringId);
        }

        return res;
    }

    public static String getLiteralVoice(String stringId, Context context) {
        String res = null;

        String valores = loadData(Constants.KEY_LITERALS_VOICE_VALUES);
        if (valores != null) {
            LinkedHashMap<String, String> list = (LinkedHashMap<String, String>) Mapper.get(LinkedHashMap.class, valores);
            res = list.get(stringId);
        }

        if (res == null) {
            int text_id = application.getResources().getIdentifier(stringId, "string", context.getPackageName());
            res = application.getResources().getString(text_id);
        }

        return res;
    }

    public static String getString(int stringId, Context defaultContext)
    {
        String res = getString(stringId);

        if (defaultContext != null && (res == null || res.length() == 0))
        {
            defaultContext.getString(stringId);
        }

        return res;
    }
    public static String getString(int stringId)
    {
        String res = "";
        /*
        BaseActivity baseActivity = application.getCurrentActivity();

        if (baseActivity != null)
        {
            res = baseActivity.getString(stringId);
        }
        else
        {
            String strId = application.getResources().getResourceEntryName(stringId);
            res = getLiteral(strId);
        }
        */
        String strId = application.getResources().getResourceEntryName(stringId);
        res = getLiteral(strId);

        return res;
    }

    public static String getString(int stringId, String str1)
    {
        String res = "";
        /*
        BaseActivity baseActivity = application.getCurrentActivity();

        if (baseActivity != null)
        {
            res = baseActivity.getString(stringId, str1);
        }
        else
        {
            String strId = application.getResources().getResourceEntryName(stringId);
            res = getLiteral(strId);
            if (res != null && res.length() > 0)
            {
                res = res.replace("%1s", str1);
            }
        }
        */
        String strId = application.getResources().getResourceEntryName(stringId);
        res = getLiteral(strId);
        if (res != null && res.length() > 0)
        {
            res = res.replace("%1s", str1);
        }

        return res;
    }


    ////////////////
    // Load objets

    public static User getUser() {
        User res = null;

        String user = loadData(Constants.KEY_USER);
        if (user != null) {
            res = (User) Mapper.get(User.class, user);
        }

        return res;
    }

    public static String getLanguage()
    {
        String language = Core.loadData(Constants.KEY_USER_LANG);
        if (language == null)
        {
            language = Locale.getDefault().getLanguage();
        }

        return language;
    }

    public static Locale getLocaleLanguage() {
        String localeString = getLanguage();
        if (localeString.equals("es") || localeString.equals("ca")) {
            return new Locale.Builder().setLanguageTag("es-ES").build();
        } else if (localeString.equals("en")) {
            return new Locale.Builder().setLanguageTag("en-US").build();
        }

        return application.getResources().getConfiguration().locale;
    }

    public static boolean isUserPrimaryForVehicle(Vehicle vehicle)
    {
        User user = getUser();
        boolean isUserPrimary = false;
        if (vehicle != null && vehicle.drivers != null)
        {
            for (int i = 0; i < vehicle.drivers.size(); i++)
            {
                Driver driver = vehicle.drivers.get(i);
                if (driver.isTypePrimary() && user.id != null && driver.id == Integer.parseInt(user.id))
                {
                    isUserPrimary = true;
                    break;
                }
            }
        }

        return isUserPrimary;
    }

    private static ArrayList getGeneralDataList(String key, Class clase) {
        ArrayList res = null;

        try {
            String general = loadData(Constants.KEY_GENERAL_DATA);
            if (general != null) {
                JSONObject json = new JSONObject(general);
                res = Mapper.getList(clase, json.getString(key));
            }
        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage());
        }

        return res;
    }

    public static ArrayList<BeaconType> getBeaconTypes() {
        return getGeneralDataList("beaconsTypes", BeaconType.class);
    }

    public static ArrayList<ColorType> getColors() {
        return getGeneralDataList("colors", ColorType.class);
    }

    public static ArrayList<IncidenceType> getIncidencesTypes() {
        return getGeneralDataList("incidencesTypes", IncidenceType.class);
    }

    public static ArrayList<IncidenceType> getIncidencesTypes(int parent) {
        ArrayList<IncidenceType> res = new ArrayList<>();
        ArrayList<IncidenceType> list = getGeneralDataList("incidencesTypes", IncidenceType.class);
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                IncidenceType it = list.get(i);
                if (it.parent == parent) {
                    res.add(it);
                }
            }
        }

        return res;
    }

    public static ArrayList<VehicleType> getVehiclesTypes() {
        return getGeneralDataList("vehiclesTypes", VehicleType.class);
    }

    public static VehicleType getVehicleType(int id) {
        VehicleType res = null;

        ArrayList<VehicleType> list = getVehiclesTypes();
        if (list != null) {
            for (int i = 0; i < list.size(); i++) {
                VehicleType type = list.get(i);
                if (type.id == id) {
                    res = type;
                    break;
                }
            }
        }

        return res;
    }

    public static ColorType getColorType(VehicleType vehicleType, int id) {
        ColorType res = null;

        if (vehicleType != null && vehicleType.colors != null) {
            for (int i = 0; i < vehicleType.colors.size(); i++) {
                ColorType type = vehicleType.colors.get(i);
                if (type.id == id) {
                    res = type;
                    break;
                }
            }
        }

        return res;
    }

    public static DeviceNotification getDeviceNotification(int notificationId) {
        DeviceNotification res = null;

        try {
            String general = loadData(Constants.KEY_USER_DEVICE_NOTIFICATIONS);
            if (general != null) {
                ArrayList list = Mapper.getList(DeviceNotification.class, general);
                for (int i = 0; i < list.size(); i++) {
                    DeviceNotification d = (DeviceNotification) list.get(i);
                    if (d.id == notificationId) {
                        res = d;
                        break;
                    }
                }
            }
        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage());
        }


        return res;
    }

    /*
    public static String getVehicleImage(Vehicle vehicle)
    {
        String urlImage = null;
        ArrayList<VehicleType> types = getVehiclesTypes();

        if (types != null && vehicle != null && vehicle.colorId != null && vehicle.vehicleTypeId != null)
        {
            for (int j = 0; j < types.size(); j++)
            {
                VehicleType type = types.get(j);
                if (type.id == Integer.parseInt(vehicle.vehicleTypeId))
                {
                    if (type.colors != null)
                    {
                        for (int k = 0; k < type.colors.size(); k++)
                        {
                            ColorType col = type.colors.get(k);
                            if (col.id == Integer.parseInt(vehicle.colorId))
                            {
                                urlImage = col.image;
                                break;
                            }
                        }
                    }
                    break;
                }
            }
        }

        return urlImage;
    }
    */

    public static ArrayList<Vehicle> getVehicles()
    {
        ArrayList<Vehicle> items = new ArrayList<>();
        String list = Core.loadData(Constants.KEY_USER_VEHICLES);
        if (list != null) {
            items = Mapper.getList(Vehicle.class, list);
        }

        return items;
    }

    public static void deleteVehicle(Vehicle vehicle)
    {
        replaceVehicle(vehicle, true);
    }
    public static void saveVehicle(Vehicle vehicle)
    {
        replaceVehicle(vehicle, false);
    }
    private static void replaceVehicle(Vehicle vehicle, boolean delete)
    {
        if (vehicle != null)
        {
            ArrayList<Vehicle> items = getVehicles();
            ArrayList<Vehicle> newItems = new ArrayList<>();
            boolean exist = false;

            for (int i = 0; i < items.size(); i++)
            {
                Vehicle v = items.get(i);
                if (v.id.equals(vehicle.id))
                {
                    if (!delete)
                    {
                        exist = true;
                        newItems.add(vehicle);
                    }
                }
                else
                {
                    newItems.add(v);
                }
            }

            if (!exist && !delete)
            {
                newItems.add(vehicle);
            }

            Gson gson = new Gson();
            String jsonStr = gson.toJson(newItems);
            saveData(Constants.KEY_USER_VEHICLES, jsonStr);
        }
    }

    public static Vehicle getVehicleFromBeacon(String beaconId)
    {
        ArrayList<Vehicle> items = getVehicles();
        Vehicle vehicle = items != null && items.size() == 1 ? items.get(0) : null;

        if (items != null && beaconId != null)
        {
            for (int i = 0; i < items.size(); i++)
            {
                Vehicle v = items.get(i);
                if (v.beacon != null)
                {
                    String beaconKey = v.beacon.getId();
                    if (beaconKey != null && beaconKey.equals(beaconId))
                    {
                        vehicle = v;
                        break;
                    }
                }
            }
        }

        return vehicle;
    }

    public static Vehicle getVehicle(String vehicleId)
    {
        ArrayList<Vehicle> items = getVehicles();
        Vehicle vehicle = items != null && items.size() == 1 ? items.get(0) : null;

        if (items != null && vehicleId != null)
        {
            for (int i = 0; i < items.size(); i++)
            {
                Vehicle v = items.get(i);
                if (v.id != null && v.id.equals(vehicleId))
                {
                    vehicle = v;
                    break;
                }
            }
        }

        return vehicle;
    }

    public static boolean hasAnyVehicleWithBeacon() //se revisan los beacons que van por beaconmanager. los IoT no.
    {
        boolean res = false;

        ArrayList<Vehicle> items = getVehicles();
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Vehicle vehicle = items.get(i);
                if (vehicle.beacon != null && vehicle.beacon.uuid != null && vehicle.beacon.beaconType != null && vehicle.beacon.beaconType.id == 1) {
                    res = true;
                    break;
                }
            }
        }

        return res;
    }

    public static void startNewApp(Context context, String packageName, String deepLink, String marketUrl) {
        try {
            Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
            if (intent != null) {
                // We found the activity now start the activity
                boolean started = false;
                try {
                    if (deepLink != null) {
                        intent.setData(Uri.parse(deepLink));
                        context.startActivity(intent);
                        started = true;
                    }
                } catch (Exception ex) {
                    LogUtil.logE(TAG, ex.getMessage());
                    //Sentry.captureException(ex);
                }

                if (!started) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            } else if (marketUrl != null) {
                // Bring user to the market or let them choose an app?
                intent = new Intent(Intent.ACTION_VIEW);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setData(Uri.parse(marketUrl));
                context.startActivity(intent);
            }
        } catch (Exception e) {
            LogUtil.logE(TAG, e.getMessage());
            //Sentry.captureException(e);
        }
    }

    public static void callPhone(String phone) {
        callPhone(phone, false);
    }

    public static void callPhone(String phone, boolean autoCall)
    {
        if (phone != null && phone.length() > 0)
        {
            if (autoCall)
            {
                Uri phoneNumber = Uri.parse("tel:" + phone.trim());
                Intent intent = autoCall ? new Intent(Intent.ACTION_CALL, phoneNumber) : new Intent(Intent.ACTION_DIAL, phoneNumber);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (autoCall && application.checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    Activity#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for Activity#requestPermissions for more details.
                    return;
                }

                application.startActivity(intent);
            }
            else
            {
                String[] items = {application.getString(R.string.call_to, phone), application.getString(R.string.cancel)};
                int[] icons = {R.drawable.ic_call_phone, R.drawable.transparent};

                //BottomSheet.Builder builder = new BottomSheet.Builder(application.getCurrentActivity());
                BottomSheet.Builder builder = new BottomSheet.Builder(application);
                builder.setItems(items, icons, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            callPhone(phone, true);
                        }
                        else if (which == 1) {
                        }
                    }
                });
                builder.show();
            }
        }
    }

    public static void activityStopped() {
        /*
        if (application.isApplicationOnBackground())
        {
            Prefs.removeData(application, Constants.NOTIFICATION_PUSH_ACTION_OPEN_INCIDENCE);

            EventBus.getDefault().post(new Event(EventCode.APP_DID_RESIGN_ACTIVE));

            if (Core.getUser() != null)
            {
                boolean initDetection = Core.hasAnyVehicleWithBeacon();

                if (initDetection)
                {
                    //Chequeamos si hay incidencia activa
                    String fechaMillis = Core.loadData(Constants.KEY_LAST_INCIDENCE_REPORTED_DATE);
                    if (fechaMillis != null)
                    {
                        long fechaInci = Long.parseLong(fechaMillis);
                        long ahora = DateUtils.getCurrentDate().getTimeInMillis();
                        long diff = ahora - fechaInci;
                        long dhours = diff / (60 * 60 * 1000);
                        if (dhours < 1)
                        {
                            initDetection = false;
                        }
                    }
                }

                if (initDetection) {
                    initBeaconDetect();
                }
            }
        }
        */
    }

    private static void initBeaconDetect()
    {
        /*
        try
        {
            BeaconManager.updateLang(application);

            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            if (mBluetoothAdapter == null) {
                // Device does not support Bluetooth
            } else if (!mBluetoothAdapter.isEnabled()) {
                // Bluetooth is not enabled :)
            } else {
                // Bluetooth is enabled

                Intent serviceIntent = new Intent(application, BeaconService.class);

                String initiated = Prefs.loadData(application, Constants.KEY_SERVICE_BEACON_STARTED);
                if (initiated != null)
                {
                    Prefs.removeData(application, Constants.KEY_SERVICE_BEACON_STARTED);
                    // Paramos el servicio si estuviera arrancado
                    application.stopService(serviceIntent);
                }

        //Intent serviceIntent2 = new Intent(this, BeaconService2.class);
        // Paramos el servicio si estuviera arrancado
        //stopService(serviceIntent2);
        //
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // Correct If
                    application.startForegroundService(serviceIntent); // Correct If
                } else {
                    application.startService(serviceIntent);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        */
    }
}
