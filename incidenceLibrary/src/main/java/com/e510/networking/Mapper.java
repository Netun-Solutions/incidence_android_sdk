package com.e510.networking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Mapper
{
    public static String get(String key, String respObj)
    {
        String res = null;

        if (respObj != null)
        {
            try {
                JSONObject jsonObject = new JSONObject(respObj);
                res = jsonObject.getString(key);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (res != null && res.equals("null")) {
            res = null;
        }

        return res;
    }

    public static Object get(Class clase, String respObj)
    {
        Object res = null;

        if (respObj != null)
        {
            try {
                Gson gson = new Gson();
                res = gson.fromJson(respObj, clase);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    public static Object get(String key, Class clase, String respObj)
    {
        Object res = null;

        if (respObj != null)
        {
            try {
                JSONObject jsonObject = new JSONObject(respObj);
                String jsonStr = jsonObject.getString(key);

                Gson gson = new Gson();
                res = gson.fromJson(jsonStr, clase);
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return res;
    }

    public static JSONObject get(String respObj)
    {
        JSONObject res = null;

        if (respObj != null)
        {
            try {
                JSONObject jsonObject = new JSONObject(respObj);
                res = jsonObject;
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return res;
    }

    public static ArrayList getList(Class clase, String respObj)
    {
        Gson gson = new Gson();
        return getList(clase, null, respObj);
    }

    public static ArrayList getList(Class clase, String customDateFormat, String respObj)
    {
        ArrayList res = new ArrayList();

        if (respObj != null)
        {
            try {
                JSONArray jsonArray = new JSONArray(respObj);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    String jsonStr = jsonArray.getString(i);

                    Gson gson = customDateFormat == null ? new Gson() : new GsonBuilder().setDateFormat(customDateFormat).create();
                    Object o = gson.fromJson(jsonStr, clase);
                    res.add(o);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return res;
    }

    public static ArrayList getList(String key, Class clase, String respObj)
    {
        ArrayList res = new ArrayList();

        if (respObj != null)
        {
            try {

                JSONObject jsonObject = new JSONObject(respObj);
                String jsonStrKey = jsonObject.getString(key);

                JSONArray jsonArray = new JSONArray(jsonStrKey);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    String jsonStr = jsonArray.getString(i);

                    Gson gson = new Gson();
                    Object o = gson.fromJson(jsonStr, clase);
                    res.add(o);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return res;
    }
}
