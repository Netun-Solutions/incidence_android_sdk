package com.e510.networking;

import com.androidnetworking.error.ANError;
import com.e510.commons.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

public class Response {

    public static final int RS_CODE_OK = 0;
    public static final int RS_CODE_OK_USER_VALIDATED_MANUAL = 1;
    public static final int RS_CODE_ERROR_UNKNOWN = -1;
    public static final int RS_CODE_ERROR_LOGIN_FAILED = -2;
    public static final int RS_CODE_ERROR_SIN_RED = -3;
    public static final int RS_CODE_ERROR_CHANGE_PASSWORD = -4;
    public static final int RS_CODE_ERROR_TIMEOUT = -5;
    public static final int RS_CODE_ERROR_DUPLICATED_KEY = -6;
    public static final int RS_CODE_ERROR_ACCESS_CODE = -7;
    public static final int RS_CODE_ERROR_UNKNOWN_WITHOUT_ALERT = -8;
    public static final int RS_CODE_ERROR_COD_POSTAL_ERROR = -9;
    public static final int RS_CODE_ERROR_CLIENT_WITHOUT_PHONE = -10;
    public static final int RS_CODE_ERROR_SIZE_MAX = -11;
    public static final int RS_CODE_ERROR_IO = -12;
    public static final int RS_CODE_ERROR_RECOVERY_PASSWORD_INVALID_EMAIL = -13;
    public static final int RS_CODE_ERROR_EMAIL_SENDED = -14;
    public static final int RS_CODE_ERROR_EMAIL = -15;
    public static final int RS_CODE_ERROR_DATA_DEPRECATED = -16;
    public static final int RS_CODE_ERROR_LOGIN_ACCOUNT_DISABLED = -17;
    public static final int RS_CODE_ERROR_TRACKING_NOT_ALLOWED = -18;
    public static final int RS_CODE_ERROR_SUBSCRIPTION_NOT_ACTIVE = -19;
    public static final int RS_CODE_STRING_RESPONSE_FROM_SERVER = -20;
    public static final int RS_CODE_ERROR_FIELD_FORM = -25;


    public int code;
    public String description;
    public String respObj;

    public static Response generate(JSONObject jsonObject, boolean decrypt)
    {
        Response response = new Response();

        if (jsonObject != null) {
            response.code = jsonObject.optInt("code", RS_CODE_ERROR_UNKNOWN);

            response.description = jsonObject.optString("description", null);
            if (response.description != null && response.description.equals("null"))
                response.description = null;

            response.respObj = jsonObject.optString("respObj", null);
            if (response.respObj != null && response.respObj.equals("null")) {
                response.respObj = null;
            }

            if (decrypt && response.respObj != null)
            {
                String respObjClean = Utils.combineParams(response.respObj, false);
                String respObjCleanDecompressed = Utils.gzUncompress(respObjClean);
                response.respObj = respObjCleanDecompressed;
            }

            if (response.code == RS_CODE_STRING_RESPONSE_FROM_SERVER)
            {
                String message = response.get("alertMessage");
                if (message != null)
                    response.description = message;
            }
        }

        return response;
    }

    public static Response generate(ANError error)
    {
        Response response = new Response();
        response.code = RS_CODE_ERROR_UNKNOWN;

        if (error != null) {
            response.description = error.getErrorBody();
        }

        return response;
    }

    public boolean isSuccess() {
        return code == RS_CODE_OK;
    }

    public String get(String key)
    {
        return Mapper.get(key, respObj);
    }

    public Object get(Class clase)
    {
        return Mapper.get(clase, respObj);
    }

    public Object get(String key, Class clase)
    {
        return Mapper.get(key, clase, respObj);
    }

    public JSONObject get()
    {
        return Mapper.get(respObj);
    }

    public ArrayList getList(Class clase)
    {
        return Mapper.getList(clase, respObj);
    }

    public ArrayList getList(Class clase, String customDateFormat)
    {
        return Mapper.getList(clase, customDateFormat, respObj);
    }

    public ArrayList getList(String key, Class clase)
    {
        return Mapper.getList(key, clase, respObj);
    }
}
