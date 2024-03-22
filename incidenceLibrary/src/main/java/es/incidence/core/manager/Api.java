package es.incidence.core.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.e510.commons.domain.Device;
import com.e510.commons.utils.DeviceUtils;
import com.e510.incidencelibrary.BuildConfig;
import com.e510.networking.NetWorkingListener;
import com.e510.networking.Networking;
import com.e510.networking.Response;

import org.json.JSONObject;

import java.util.HashMap;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.domain.Beacon;
import es.incidence.core.domain.IDevice;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;

public class Api
{
    private static final String TAG = "Api";

    private static final String HEADER_DEVICE_ID = "deviceId";

    private static final String HEADER_AUTHORIZATION = "Authorization";
    private static final String HEADER_APP = "app";
    private static final String HEADER_LANG = "lang";
    //private static final String HEADER_TOKEN = "token";
    private static final String HEADER_PLATFORM = "platform";

    public static void init(Context context, String apikey)
    {
        Networking.init(context, false);

        /*
        String token = getToken();
        if (token != null) {
            //Networking.setAuthorization(token);
            Networking.setBasicHeader(HEADER_TOKEN, token);
        }
        */
        Device d = DeviceUtils.getDevice(context);
        Networking.setBasicHeader(HEADER_AUTHORIZATION, apikey);
        Networking.setBasicHeader(HEADER_APP, d.getPackageName());
        //Networking.setBasicHeader(HEADER_APP, "mapfre.com.app");
        Networking.setBasicHeader(HEADER_LANG, Core.getLanguage());
        Networking.setBasicHeader(HEADER_PLATFORM, "android");

        String deviceId = Core.loadData(Constants.KEY_DEVICE_ID);
        if (deviceId != null)
            Networking.setBasicHeader(HEADER_DEVICE_ID, deviceId);

        Networking.addNetworkingListener(new NetWorkingListener() {
            @Override
            public void onCallResponse(Response response) {
                /*
                if (response.code == Response.RS_CODE_ERROR_CHANGE_PASSWORD) {
                    BaseActivity baseActivity = getCurrentActivity();
                    baseActivity.showAlert(baseActivity.getString(R.string.incidence_key_app_name), baseActivity.getString(R.string.incidence_key_session_timeout), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            restartApp();
                        }
                    });
                }
                */
            }
        });
    }

    public static void updateLang()
    {
        Networking.setBasicHeader(HEADER_LANG, Core.getLanguage());
        //Networking.setBasicHeader(HEADER_LANG, "en");
    }

    private static void log(String message)
    {
        if (BuildConfig.DEBUG)
        {
            Log.e(TAG, message);
        }
    }

    private static JSONObjectRequestListener getSimpleListener(final IRequestListener viewListener)
    {
        JSONObjectRequestListener requestListener = new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response)
            {
                IResponse res = IResponse.generate(response);
                log("Response: " + res.json);

                if (viewListener != null)
                    viewListener.onFinish(res);
            }

            @Override
            public void onError(ANError anError)
            {
                IResponse res = IResponse.generate(anError);
                log("Response: " + res.json);

                if (viewListener != null)
                    viewListener.onFinish(res);
            }
        };

        return requestListener;
    }

    public static boolean hasConnection(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /////////////////////
    /// INIT DEVICE
    //

    public static void updateDevice(final IRequestListener viewListener, IDevice device) {
        String url = Constants.BASE_URL + "/" + "device";
        updateDevice(viewListener, device, url);
    }
    public static void updateDeviceSdk(final IRequestListener viewListener, IDevice device) {
        String url = Constants.BASE_URL + "/" + "sdk/device";
        updateDevice(viewListener, device, url);
    }
    private static void updateDevice(final IRequestListener viewListener, IDevice device, String url)
    {
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();
        params.put("uuid", device.uuid);
        if (device.token != null)
            params.put("token", device.token);
        params.put("platform", device.platform);
        params.put("version", device.version);
        params.put("manufacturer", device.manufacturer);
        params.put("model", device.model);
        params.put("appVersion", device.appVersion);
        params.put("appVersionNumber", device.appVersionNumber);
        params.put("response", "{}");

        JSONObjectRequestListener requestListener = new JSONObjectRequestListener() {
            @Override
            public void onResponse(JSONObject response)
            {
                IResponse res = IResponse.generate(response);
                log("Response: " + res.json);

                if (res.isSuccess())
                {
                    IDevice newDevice = (IDevice) res.get("device", IDevice.class);
                    Core.saveData(Constants.KEY_DEVICE_ID, newDevice.id);
                    Networking.setBasicHeader(HEADER_DEVICE_ID, newDevice.id);
                }
                if (viewListener != null)
                    viewListener.onFinish(res);
            }

            @Override
            public void onError(ANError anError)
            {
                IResponse res = IResponse.generate(anError);
                log("Response: " + res.json);

                if (viewListener != null)
                    viewListener.onFinish(res);
            }
        };

        if (Networking.hasBasicHeader(HEADER_DEVICE_ID)) {
            Networking.putDirect(url, params, requestListener);
        } else {
            Networking.postDirect(url, params, requestListener);
        }
    }

    /////////////////////
    /// INIT SIGN
    //
    public static void validateApiKey(final IRequestListener viewListener)
    {
        String url = Constants.BASE_URL + "/" + "sdk/config";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.getDirect(url, params, requestListener);
    }

    public static void addBeaconSdk(final IRequestListener viewListener, User user, Beacon beacon, Vehicle vehicle)
    {
        String url = Constants.BASE_URL + "/" + "sdk/beacon";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();

        params.put("external_user_id", user.externalUserId); // (identificador externo del usuario)
        params.put("name", user.name); // (nombre del usuario)
        params.put("phone", user.phone); // (teléfono)
        params.put("email", user.email); // (e-mail)
        params.put("identity_type", String.valueOf(user.identityType.name)); // (tipo de documento de identidad: dni, nie, cif)
        params.put("dni", user.dni); // (número del documento de identidad)
        params.put("birthday", user.birthday); // (fecha de Nacimiento)
        params.put("check_terms", user.checkTerms); // (aceptación de la privacidad)
        params.put("external_vehicle_id", vehicle.externalVehicleId); // (identificador externo del vehículo)
        params.put("license_plate", vehicle.licensePlate); // (matrícula del vehículo)
        params.put("registration_year", vehicle.registrationYear); // (fecha de matriculación)
        params.put("vehicle_type", String.valueOf(vehicle.vehicleType.name)); // (tipo del vehículo)
        params.put("brand", vehicle.brand); // (marca del vehículo)
        params.put("model", vehicle.model); // (modelo del vehículo)
        params.put("color", String.valueOf(vehicle.color.name)); // (color del vehículo)
        params.put("policy_number", vehicle.policy.policyNumber); // (número de la póliza)
        params.put("policy_end", vehicle.policy.policyEnd); // (fecha caducidad de la póliza)
        params.put("policy_identity_type", String.valueOf(vehicle.policy.identityType.name)); // (tipo de documento identidad del asegurador)
        params.put("policy_dni", vehicle.policy.dni); // (documento de identidad del asegurador)
        params.put("imei", beacon.uuid); // (imei)
        //params.put("imei", "8473847394739847");

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.postDirect(url, params, requestListener);
    }

    public static void deleteBeaconSdk(final IRequestListener viewListener, User user, Vehicle vehicle)
    {
        //Networking.setBasicHeader(HEADER_TOKEN, getToken());

        String url = Constants.BASE_URL + "/" + "sdk/beacon";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();
        params.put("external_user_id", user.externalUserId); // (identificador externo del usuario)
        params.put("name", user.name); // (nombre del usuario)
        params.put("phone", user.phone); // (teléfono)
        params.put("email", user.email); // (e-mail)
        params.put("identity_type", String.valueOf(user.identityType.name)); // (tipo de documento de identidad: dni, nie, cif)
        params.put("dni", user.dni); // (número del documento de identidad)
        params.put("birthday", user.birthday); // (fecha de Nacimiento)
        params.put("check_terms", user.checkTerms); // (aceptación de la privacidad)
        params.put("external_vehicle_id", vehicle.externalVehicleId); // (identificador externo del vehículo)
        params.put("license_plate", vehicle.licensePlate); // (matrícula del vehículo)
        params.put("registration_year", vehicle.registrationYear); // (fecha de matriculación)
        params.put("vehicle_type", String.valueOf(vehicle.vehicleType.name)); // (tipo del vehículo)
        params.put("brand", vehicle.brand); // (marca del vehículo)
        params.put("model", vehicle.model); // (modelo del vehículo)
        params.put("color", String.valueOf(vehicle.color.name)); // (color del vehículo)
        params.put("policy_number", vehicle.policy.policyNumber); // (número de la póliza)
        params.put("policy_end", vehicle.policy.policyEnd); // (fecha caducidad de la póliza)
        params.put("policy_identity_type", String.valueOf(vehicle.policy.identityType.name)); // (tipo de documento identidad del asegurador)
        params.put("policy_dni", vehicle.policy.dni); // (documento de identidad del asegurador)

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.deleteDirect(url, params, requestListener);
    }

    private static void validate(final IRequestListener viewListener, String value)
    {
        String url = Constants.BASE_URL + "/" + "validations" + "/" + value;
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.getDirect(url, params, requestListener);
    }

    public static void getEcommercesSdk(final IRequestListener viewListener, User user, Vehicle vehicle)
    {
        //Networking.setBasicHeader(HEADER_TOKEN, getToken());

        String url = Constants.BASE_URL + "/sdk/ecommerces";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();
        params.put("external_user_id", user.externalUserId); // (identificador externo del usuario)
        params.put("name", user.name); // (nombre del usuario)
        params.put("phone", user.phone); // (teléfono)
        params.put("email", user.email); // (e-mail)
        params.put("identity_type", String.valueOf(user.identityType.name)); // (tipo de documento de identidad: dni, nie, cif)
        params.put("dni", user.dni); // (número del documento de identidad)
        params.put("birthday", user.birthday); // (fecha de Nacimiento)
        params.put("check_terms", user.checkTerms); // (aceptación de la privacidad)
        params.put("external_vehicle_id", vehicle.externalVehicleId); // (identificador externo del vehículo)
        params.put("license_plate", vehicle.licensePlate); // (matrícula del vehículo)
        params.put("registration_year", vehicle.registrationYear); // (fecha de matriculación)
        params.put("vehicle_type", String.valueOf(vehicle.vehicleType.name)); // (tipo del vehículo)
        params.put("brand", vehicle.brand); // (marca del vehículo)
        params.put("model", vehicle.model); // (modelo del vehículo)
        params.put("color", String.valueOf(vehicle.color.name)); // (color del vehículo)
        params.put("policy_number", vehicle.policy.policyNumber); // (número de la póliza)
        params.put("policy_end", vehicle.policy.policyEnd); // (fecha caducidad de la póliza)
        params.put("policy_identity_type", String.valueOf(vehicle.policy.identityType.name)); // (tipo de documento identidad del asegurador)
        params.put("policy_dni", vehicle.policy.dni); // (documento de identidad del asegurador)

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.putDirect(url, params, requestListener);
    }

    public static void getBeaconSdk(final IRequestListener viewListener, User user, Vehicle vehicle)
    {
        //Networking.setBasicHeader(HEADER_TOKEN, getToken());

        String url = Constants.BASE_URL + "/sdk/beacon";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();

        params.put("external_user_id", user.externalUserId); // (identificador externo del usuario)
        params.put("name", user.name); // (nombre del usuario)
        params.put("phone", user.phone); // (teléfono)
        params.put("email", user.email); // (e-mail)
        params.put("identity_type", String.valueOf(user.identityType.name)); // (tipo de documento de identidad: dni, nie, cif)
        params.put("dni", user.dni); // (número del documento de identidad)
        params.put("birthday", user.birthday); // (fecha de Nacimiento)
        params.put("check_terms", user.checkTerms); // (aceptación de la privacidad)
        params.put("external_vehicle_id", vehicle.externalVehicleId); // (identificador externo del vehículo)
        params.put("license_plate", vehicle.licensePlate); // (matrícula del vehículo)
        params.put("registration_year", vehicle.registrationYear); // (fecha de matriculación)
        params.put("vehicle_type", String.valueOf(vehicle.vehicleType.name)); // (tipo del vehículo)
        params.put("brand", vehicle.brand); // (marca del vehículo)
        params.put("model", vehicle.model); // (modelo del vehículo)
        params.put("color", String.valueOf(vehicle.color.name)); // (color del vehículo)
        params.put("policy_number", vehicle.policy.policyNumber); // (número de la póliza)
        params.put("policy_end", vehicle.policy.policyEnd); // (fecha caducidad de la póliza)
        params.put("policy_identity_type", String.valueOf(vehicle.policy.identityType.name)); // (tipo de documento identidad del asegurador)
        params.put("policy_dni", vehicle.policy.dni); // (documento de identidad del asegurador)

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.putDirect(url, params, requestListener);
    }

    public static void getGeoSdk(final IRequestListener viewListener, User user, Vehicle vehicle)
    {
        //Networking.setBasicHeader(HEADER_TOKEN, getToken());

        String url = Constants.BASE_URL + "/sdk/iot_coordinates";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();

        params.put("external_user_id", user.externalUserId); // (identificador externo del usuario)
        params.put("name", user.name); // (nombre del usuario)
        params.put("phone", user.phone); // (teléfono)
        params.put("email", user.email); // (e-mail)
        params.put("identity_type", String.valueOf(user.identityType.name)); // (tipo de documento de identidad: dni, nie, cif)
        params.put("dni", user.dni); // (número del documento de identidad)
        params.put("birthday", user.birthday); // (fecha de Nacimiento)
        params.put("check_terms", user.checkTerms); // (aceptación de la privacidad)
        params.put("external_vehicle_id", vehicle.externalVehicleId); // (identificador externo del vehículo)
        params.put("license_plate", vehicle.licensePlate); // (matrícula del vehículo)
        params.put("registration_year", vehicle.registrationYear); // (fecha de matriculación)
        params.put("vehicle_type", String.valueOf(vehicle.vehicleType.name)); // (tipo del vehículo)
        params.put("brand", vehicle.brand); // (marca del vehículo)
        params.put("model", vehicle.model); // (modelo del vehículo)
        params.put("color", String.valueOf(vehicle.color.name)); // (color del vehículo)
        params.put("policy_number", vehicle.policy.policyNumber); // (número de la póliza)
        params.put("policy_end", vehicle.policy.policyEnd); // (fecha caducidad de la póliza)
        params.put("policy_identity_type", String.valueOf(vehicle.policy.identityType.name)); // (tipo de documento identidad del asegurador)
        params.put("policy_dni", vehicle.policy.dni); // (documento de identidad del asegurador)

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.putDirect(url, params, requestListener);
    }

    public static void getBeaconDetailSdk(final IRequestListener viewListener, User user, Vehicle vehicle)
    {
        //Networking.setBasicHeader(HEADER_TOKEN, getToken());

        String url = Constants.BASE_URL + "/sdk/iot_check";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();

        params.put("external_user_id", user.externalUserId); // (identificador externo del usuario)
        params.put("name", user.name); // (nombre del usuario)
        params.put("phone", user.phone); // (teléfono)
        params.put("email", user.email); // (e-mail)
        params.put("identity_type", String.valueOf(user.identityType.name)); // (tipo de documento de identidad: dni, nie, cif)
        params.put("dni", user.dni); // (número del documento de identidad)
        params.put("birthday", user.birthday); // (fecha de Nacimiento)
        params.put("check_terms", user.checkTerms); // (aceptación de la privacidad)
        params.put("external_vehicle_id", vehicle.externalVehicleId); // (identificador externo del vehículo)
        params.put("license_plate", vehicle.licensePlate); // (matrícula del vehículo)
        params.put("registration_year", vehicle.registrationYear); // (fecha de matriculación)
        params.put("vehicle_type", String.valueOf(vehicle.vehicleType.name)); // (tipo del vehículo)
        params.put("brand", vehicle.brand); // (marca del vehículo)
        params.put("model", vehicle.model); // (modelo del vehículo)
        params.put("color", String.valueOf(vehicle.color.name)); // (color del vehículo)
        params.put("policy_number", vehicle.policy.policyNumber); // (número de la póliza)
        params.put("policy_end", vehicle.policy.policyEnd); // (fecha caducidad de la póliza)
        params.put("policy_identity_type", String.valueOf(vehicle.policy.identityType.name)); // (tipo de documento identidad del asegurador)
        params.put("policy_dni", vehicle.policy.dni); // (documento de identidad del asegurador)

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.putDirect(url, params, requestListener);
    }

    public static void postIncidenceSdk(final IRequestListener viewListener, User user, Vehicle vehicle, Incidence incidence)
    {
        //Networking.setBasicHeader(HEADER_TOKEN, getToken());

        String url = Constants.BASE_URL + "/sdk/incidence";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();

        params.put("external_user_id", user.externalUserId); // (identificador externo del usuario)
        params.put("name", user.name); // (nombre del usuario)
        params.put("phone", user.phone); // (teléfono)
        params.put("email", user.email); // (e-mail)
        params.put("identity_type", String.valueOf(user.identityType.name)); // (tipo de documento de identidad: dni, nie, cif)
        params.put("dni", user.dni); // (número del documento de identidad)
        params.put("birthday", user.birthday); // (fecha de Nacimiento)
        params.put("check_terms", user.checkTerms); // (aceptación de la privacidad)
        params.put("external_vehicle_id", vehicle.externalVehicleId); // (identificador externo del vehículo)
        params.put("license_plate", vehicle.licensePlate); // (matrícula del vehículo)
        params.put("registration_year", vehicle.registrationYear); // (fecha de matriculación)
        params.put("vehicle_type", String.valueOf(vehicle.vehicleType.name)); // (tipo del vehículo)
        params.put("brand", vehicle.brand); // (marca del vehículo)
        params.put("model", vehicle.model); // (modelo del vehículo)
        params.put("color", String.valueOf(vehicle.color.name)); // (color del vehículo)
        params.put("policy_number", vehicle.policy.policyNumber); // (número de la póliza)
        params.put("policy_end", vehicle.policy.policyEnd); // (fecha caducidad de la póliza)
        params.put("policy_identity_type", String.valueOf(vehicle.policy.identityType.name)); // (tipo de documento identidad del asegurador)
        params.put("policy_dni", vehicle.policy.dni); // (documento de identidad del asegurador)

        params.put("incidenceTypeId", String.valueOf(incidence.incidenceType.externalId)); // (identificador numérico del tipo de incidencia)
        params.put("street", incidence.street);
        params.put("city", incidence.city);
        params.put("country", incidence.country);
        params.put("latitude", incidence.latitude != null ? String.valueOf(incidence.latitude) : "");
        params.put("longitude", incidence.longitude != null ? String.valueOf(incidence.longitude) : "");
        params.put("fromNotification", "0"); //  (0: reportado manualmente. 1: reportado por baliza)
        params.put("externalIncidenceId", incidence.externalIncidenceId);

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.postDirect(url, params, requestListener);
        //Networking.postDirect2(url, params, requestListener);
    }

    public static void putIncidenceSdk(final IRequestListener viewListener, User user, Vehicle vehicle, Incidence incidence)
    {
        //Networking.setBasicHeader(HEADER_TOKEN, getToken());

        String url = Constants.BASE_URL + "/sdk/incidence";
        log("Request: " + url);

        HashMap<String, String> params = new HashMap<>();

        params.put("external_user_id", user.externalUserId); // (identificador externo del usuario)
        params.put("name", user.name); // (nombre del usuario)
        params.put("phone", user.phone); // (teléfono)
        params.put("email", user.email); // (e-mail)
        params.put("identity_type", String.valueOf(user.identityType.name)); // (tipo de documento de identidad: dni, nie, cif)
        params.put("dni", user.dni); // (número del documento de identidad)
        params.put("birthday", user.birthday); // (fecha de Nacimiento)
        params.put("check_terms", user.checkTerms); // (aceptación de la privacidad)
        params.put("external_vehicle_id", vehicle.externalVehicleId); // (identificador externo del vehículo)
        params.put("license_plate", vehicle.licensePlate); // (matrícula del vehículo)
        params.put("registration_year", vehicle.registrationYear); // (fecha de matriculación)
        params.put("vehicle_type", String.valueOf(vehicle.vehicleType.name)); // (tipo del vehículo)
        params.put("brand", vehicle.brand); // (marca del vehículo)
        params.put("model", vehicle.model); // (modelo del vehículo)
        params.put("color", String.valueOf(vehicle.color.name)); // (color del vehículo)
        params.put("policy_number", vehicle.policy.policyNumber); // (número de la póliza)
        params.put("policy_end", vehicle.policy.policyEnd); // (fecha caducidad de la póliza)
        params.put("policy_identity_type", String.valueOf(vehicle.policy.identityType.name)); // (tipo de documento identidad del asegurador)
        params.put("policy_dni", vehicle.policy.dni); // (documento de identidad del asegurador)

        params.put("externalIncidenceId", incidence.externalIncidenceId);

        JSONObjectRequestListener requestListener = getSimpleListener(viewListener);
        Networking.putDirect(url, params, requestListener);
    }
}
