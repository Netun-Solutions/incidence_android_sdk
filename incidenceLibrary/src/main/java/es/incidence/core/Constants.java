package es.incidence.core;


import es.incidence.library.config.IncidenceEnvironment;

public class Constants
{
    private static final String BASE_URL_TEST = "https://api-test.incidence.eu";
    private static final String BASE_URL_PRE = "https://api-pre.incidence.eu";
    private static final String BASE_URL_PRO = "https://api-pro.incidence.eu";
    public static String BASE_URL = "";

    public static boolean FLAG_INCIDENCE_DGT = false;

    public static final String SCREEN_DEVELOPER = "DEVELOPER";
    //public static final String SCREEN_DEVICE_LIST = "SCREEN_DEVICE_LIST";
    public static final String SCREEN_DEVICE_CREATE = "SCREEN_DEVICE_CREATE";
    public static final String FUNC_DEVICE_DELETE = "FUNC_DEVICE_DELETE";
    public static final String SCREEN_DEVICE_REVIEW = "SCREEN_DEVICE_REVIEW";
    public static final String SCREEN_ECOMMERCE = "SCREEN_ECOMMERCE";
    public static final String FUNC_REPOR_INC = "FUNC_REPOR_INC";
    public static final String FUNC_CLOSE_INC = "FUNC_CLOSE_INC";
    public static final String SCREEN_REPOR_INC = "SCREEN_REPOR_INC";
    public static final String SCREEN_REPOR_INC_SIMPLE = "SCREEN_REPOR_INC_SIMPLE";
    public static final String SCREEN_ERROR = "ERROR";

    public static void setBaseUrl(IncidenceEnvironment environment)
    {
        if (environment == IncidenceEnvironment.TEST)
        {
            BASE_URL = BASE_URL_TEST;
        }
        else if (environment == IncidenceEnvironment.PRE)
        {
            BASE_URL = BASE_URL_PRE;
        }
        else if (environment == IncidenceEnvironment.PRO)
        {
            BASE_URL = BASE_URL_PRO;
        }
    }


    public static final String KEY_SEGMENT = "x3xDy1VF1eYQm7n1KEvCwF";

    public static final String PHONE_CONTACT = "+34910608864";
    public static final String PHONE_EMERGENCY = "+34913536306";
    public static final String EMAIL_CONTACT = "infoapp@incidence.eu";
    public static final String URL_FAQS = "https://incidence.eu/#preguntas-frecuentes-anchor";


    public static final String FONT_THIN = "Silka-Thin.ttf";
    public static final String FONT_LIGHT = "Silka-Light.ttf";
    public static final String FONT_REGULAR = "Silka-Regular.ttf";
    public static final String FONT_MEDIUM = "Silka-Medium.ttf";
    public static final String FONT_SEMIBOLD = "Silka-SemiBold.ttf";
    public static final String FONT_BOLD = "Silka-Bold.ttf";


    public static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";
/*
    public static final String KEY_USER_TOKEN = "KEY_USER_TOKEN";
    public static final String KEY_USER = "KEY_USER";
    public static final String KEY_USER_DEVICE_NOTIFICATIONS = "KEY_USER_DEVICE_NOTIFICATIONS";
    public static final String KEY_USER_DEFAULT_VEHICLE_ID = "KEY_USER_DEFAULT_VEHICLE_ID";
    public static final String KEY_USER_VEHICLES = "KEY_USER_VEHICLES";
    public static final String KEY_USER_SIGNOUT = "KEY_USER_SIGNOUT";
    public static final String KEY_GENERAL_DATA = "KEY_GENERAL_DATA";
    public static final String KEY_LITERALS_LANG = "KEY_LITERALS_LANG";
    public static final String KEY_LITERALS_VERSION = "KEY_LITERALS_VERSION";*/
    public static final String KEY_USER_LANG = "KEY_USER_LANG";
    public static final String KEY_LITERALS_VALUES = "KEY_LITERALS_VALUES";
    public static final String KEY_LITERALS_VOICE_VALUES = "KEY_LITERALS_VOICE_VALUES";/*
    public static final String KEY_LAST_INCIDENCE_REPORTED_DATE = "KEY_LAST_INCIDENCE_REPORTED_DATE";
    public static final String KEY_SERVICE_BEACON_STARTED = "KEY_SERVICE_BEACON_STARTED";
    public static final String KEY_CONFIG_EXPIRE_POLICY_TIME = "KEY_CONFIG_EXPIRE_POLICY_TIME";
    public static final String KEY_CONFIG_RETRY_SECON_DRIVER_REQUEST = "KEY_CONFIG_RETRY_SECON_DRIVER_REQUEST";
    public static final String KEY_CONFIG_MAP_REFRESH_TIME = "KEY_CONFIG_MAP_REFRESH_TIME";
    public static final String KEY_CONFIG_EXPIRE_SMS_TIME = "KEY_CONFIG_EXPIRE_SMS_TIME";
    public static final String KEY_CONFIG_EXPIRE_CANCEL_TIME = "KEY_CONFIG_EXPIRE_CANCEL_TIME";
    public static final String KEY_CONFIG_HOME_VIDEO = "KEY_CONFIG_HOME_VIDEO";
    public static final String KEY_CONFIG_SHOW_IOT = "KEY_CONFIG_SHOW_IOT";
    public static final String KEY_CONFIG_REPEAT_VOICE = "KEY_CONFIG_REPEAT_VOICE";
    public static final String KEY_CONFIG_TEST_META_KEY = "KEY_CONFIG_TEST_META_KEY";
    public static final String KEY_YOUTUBE_ID = "KEY_YOUTUBE_ID";

    public static final String NOTIFICATION_ACTION_ADD_VEHICLE = "openAddVehicle";
    public static final String NOTIFICATION_ACTION_ADD_BEACON = "openAddBeacon";
    public static final String NOTIFICATION_ACTION_OPEN_VEHICLE_ID = "openVehicleId";
    public static final String NOTIFICATION_ACTION_OPEN_POLICY_ID = "openPolicyId";
    public static final String NOTIFICATION_ACTION_OPEN_RATE_INCIDENCE = "openRateIncidence";
    public static final String NOTIFICATION_ACTION_OPEN_USER = "openUser";
    public static final String NOTIFICATION_ACTION_OPEN_VEHICLE_DRIVERS = "openVehicleDrivers";
    public static final String NOTIFICATION_ACTION_RESEND_DRIVER_REQUEST = "resendDriverRequest";

    public static final String NOTIFICATION_PUSH_ACTION_OPEN_INCIDENCE = "openIncidence";

    public static final int NOTIFICATION_STATUS_READ = 1;
    public static final int NOTIFICATION_STATUS_DELETE = 2;

    public static final String VALIDATE_USER_DNI_EXISTS = "user_dni_exists";
    public static final String VALIDATE_USER_NIE_EXISTS = "user_nie_exists";
    public static final String VALIDATE_USER_EMAIL_EXISTS = "user_email_exists";
    public static final String VALIDATE_USER_PHONE_EXISTS = "user_phone_exists";
    */
    public static final String WS_RESPONSE_ACTION_INVALID_SESSION = "invalid_session";


    public static final int ACCIDENT = 1;
    public static final int FAULT = 2;
    public static final int ACCIDENT_TYPE_ONLY_MATERIAL = 12;
    public static final int ACCIDENT_TYPE_WOUNDED = 13;

    public static final int PERMISSION_LOCATION_BACKGROUND_REQUEST_CODE = 3453;
    public static final int PERMISSION_RECORD_AUDIO_REQUEST_CODE = 2413;
}
