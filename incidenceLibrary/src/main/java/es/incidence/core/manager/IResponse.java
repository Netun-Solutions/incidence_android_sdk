package es.incidence.core.manager;

import com.androidnetworking.error.ANError;
import com.e510.networking.Mapper;

import org.json.JSONObject;

import java.util.ArrayList;

public class IResponse
{
    public static final String RESPONSE_ERROR_CONNECTION = "connectionError";

    public String json;

    public String status;
    public String action;
    public String message;

    public static IResponse generate(JSONObject jsonObject)
    {
        IResponse response = new IResponse();

        if (jsonObject != null) {
            response.json = jsonObject.toString();
            response.status = jsonObject.optString("status", null);
            response.action = jsonObject.optString("action", null);
            response.message = jsonObject.optString("message", null);
        }

        return response;
    }

    public static IResponse generate(ANError error)
    {
        IResponse response = new IResponse();
        response.status = "error";

        if (error != null) {
            response.json = error.getErrorBody();
            try {
                if (response.json != null)
                {
                    JSONObject jsonObject = new JSONObject(response.json);
                    response.status = jsonObject.optString("status", null);
                    response.action = jsonObject.optString("action", null);
                    response.message = jsonObject.optString("message", null);
                }
                else if (error.getErrorDetail() != null && error.getErrorDetail().equalsIgnoreCase(RESPONSE_ERROR_CONNECTION))
                {
                    response.status = RESPONSE_ERROR_CONNECTION;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return response;
    }

    public boolean isSuccess() {
        return status != null && status.equals("success");
    }

    public String get(String key)
    {
        return Mapper.get(key, json);
    }

    public Object get(Class clase)
    {
        return get(clase, json);
    }

    public Object get(Class clase, String jsonStr)
    {
        return Mapper.get(clase, jsonStr);
    }

    public Object get(String key, Class clase)
    {
        return Mapper.get(key, clase, json);
    }

    public JSONObject get()
    {
        return Mapper.get(json);
    }

    public ArrayList getList(Class clase)
    {
        return Mapper.getList(clase, json);
    }

    public ArrayList getList(Class clase, String customDateFormat)
    {
        return Mapper.getList(clase, customDateFormat, json);
    }

    public ArrayList getList(String key, Class clase)
    {
        return Mapper.getList(key, clase, json);
    }
}
