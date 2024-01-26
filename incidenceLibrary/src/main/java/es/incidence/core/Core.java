package es.incidence.core;

import static com.e510.commons.utils.LogUtil.makeLogTag;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;

import com.e510.commons.domain.Device;
import com.e510.commons.utils.DeviceUtils;
import com.e510.commons.utils.LogUtil;
import com.e510.commons.utils.Prefs;
import com.e510.commons.utils.config.AppConfiguration;
import com.e510.incidencelibrary.BuildConfig;
import com.e510.networking.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dev.b3nedikt.restring.Restring;
import dev.b3nedikt.reword.RewordInterceptor;
import dev.b3nedikt.viewpump.ViewPump;
import es.incidence.core.domain.IDevice;
import es.incidence.core.domain.IncidenceType;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.manager.Api;
import es.incidence.library.IncidenceLibraryManager;
import es.incidence.library.config.IncidenceEnvironment;
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
    public static void init(Application app, String apikey, IncidenceEnvironment environment) {
        application = app;
        prepareConfigs(apikey, environment);
        Restring.init(app);
        ViewPump.init(RewordInterceptor.INSTANCE);
    }

    private static void prepareConfigs(String apikey, IncidenceEnvironment environment) {
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
                baseActivity.showAlert(baseActivity.getString(R.string.incidence_key_app_name), baseActivity.getString(R.string.incidence_key_app_update_alert_message), new DialogInterface.OnClickListener() {
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


    public static void updateLiterals(String valores) {
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

                Core.saveData(Constants.KEY_LITERALS_VALUES, valores);
                Core.saveData(Constants.KEY_LITERALS_VOICE_VALUES, valores);
            }
        }
    }

    public static String getLiteralSDK(String stringId) {
        String res = null;

        String valores = loadData(Constants.KEY_LITERALS_VALUES);
        if (valores != null) {
            LinkedHashMap<String, String> list = (LinkedHashMap<String, String>) Mapper.get(LinkedHashMap.class, valores);
            res = list.get(stringId);
        }

        return res;
    }

    public static String getLiteralVoiceSDK(String stringId, Context context) {
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

    public static String getStringSDK(int stringId, Context defaultContext)
    {
        String res = getStringSDK(stringId);

        if (defaultContext != null && (res == null || res.length() == 0))
        {
            defaultContext.getString(stringId);
        }

        return res;
    }
    public static String getStringSDK(int stringId)
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
        res = getLiteralSDK(strId);

        return res;
    }

    public static String getStringSDK(int stringId, String str1)
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
        res = getLiteralSDK(strId);
        if (res != null && res.length() > 0)
        {
            res = res.replace("%1s", str1);
        }

        return res;
    }


    ////////////////
    // Load objets

    public static String getLanguage()
    {
        String language = Core.loadData(Constants.KEY_USER_LANG);
        if (language == null)
        {
            language = Locale.getDefault().getLanguage();
        }
        language = "es";
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

    public static ArrayList<IncidenceType> getIncidencesTypes(int parent) {
        ArrayList<IncidenceType> res = new ArrayList<>();

        //ArrayList<IncidenceType> list = getGeneralDataList("incidencesTypes", IncidenceType.class);
        List<IncidenceType> list = IncidenceLibraryManager.instance.incidencesTypes;
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



    public static boolean hasAnyVehicleWithBeacon() //se revisan los beacons que van por beaconmanager. los IoT no.
    {
        boolean res = false;



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

    /*
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
                String[] items = {application.getString(R.string.incidence_key_call_to, phone), application.getString(R.string.incidence_key_cancel)};
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
    */

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
