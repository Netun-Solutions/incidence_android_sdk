package com.e510.networking;

import android.content.Context;
import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.ANRequest;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.e510.commons.utils.DeviceUtils;
import com.e510.incidencelibrary.BuildConfig;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Networking
{
    private static final String TAG = "E510Network";
    private Context context;
    private boolean decryptResponses;
    private String authorization;
    private HashMap<String, String> customHeaders;

    private static Networking INSTANCE;
    public static Networking getInstance()
    {
        return INSTANCE;
    }
    public static Networking init(Context context, boolean decryptResponses) {
        if (INSTANCE == null)
        {
            INSTANCE = new Networking();
            INSTANCE.context = context;
            INSTANCE.decryptResponses = decryptResponses;
            INSTANCE.netWorkingListeners = new ArrayList<>();
            INSTANCE.customHeaders = new HashMap<>();

            AndroidNetworking.initialize(context);
            AndroidNetworking.setParserFactory(new JacksonParserFactory());
        }

        return INSTANCE;
    }

    private ArrayList<NetWorkingListener> netWorkingListeners;

    public static void addNetworkingListener(NetWorkingListener listener)
    {
        if (!INSTANCE.netWorkingListeners.contains(listener))
            INSTANCE.netWorkingListeners.add(listener);
    }

    public static void removeNetworkingListener(NetWorkingListener listener)
    {
        INSTANCE.netWorkingListeners.remove(listener);
    }

    private static void onNetworkResponse(Response response)
    {
        ArrayList<NetWorkingListener> listeners = INSTANCE.netWorkingListeners;
        for (int i = 0; i < listeners.size(); i++)
        {
            NetWorkingListener list = listeners.get(i);
            list.onCallResponse(response);
        }
    }

    public static void setAuthorization(String auth)
    {
        INSTANCE.authorization = auth;
    }

    private static void addBasicData(ANRequest.PostRequestBuilder request)
    {
        HashMap<String, String> headers = new HashMap<>();
        if (INSTANCE.authorization != null) {
            request.addHeaders("Authorization", "Bearer " + INSTANCE.authorization);
            headers.put("Authorization", "Bearer " + INSTANCE.authorization);
        }

        request.addBodyParameter("locale", DeviceUtils.getLocale());

        request.addHeaders(INSTANCE.customHeaders);
        headers.putAll(INSTANCE.customHeaders);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "headers: " + headers);
        }
    }

    private static void addBasicData(ANRequest.GetRequestBuilder request)
    {
        HashMap<String, String> headers = new HashMap<>();
        if (INSTANCE.authorization != null) {
            request.addHeaders("Authorization", "Bearer " + INSTANCE.authorization);
            headers.put("Authorization", "Bearer " + INSTANCE.authorization);
        }

        request.addHeaders(INSTANCE.customHeaders);
        headers.putAll(INSTANCE.customHeaders);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "headers: " + headers);
        }
    }

    public static void setBasicHeader(String key, String value)
    {
        INSTANCE.customHeaders.put(key, value);
    }

    public static boolean hasBasicHeader(String key)
    {
        return INSTANCE.customHeaders.containsKey(key);
    }

    public static void post(String url, Object params, final RequestListener requestListener)
    {
        //url = url.replaceAll("//", "/");

        final ANRequest.PostRequestBuilder request = AndroidNetworking.post(url)
                .setPriority(Priority.IMMEDIATE)
                .addApplicationJsonBody(params);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }
        post(request, requestListener);
    }

    public static void post(String url, JSONArray params, final RequestListener requestListener)
    {
        final ANRequest.PostRequestBuilder request = AndroidNetworking.post(url)
                .setPriority(Priority.IMMEDIATE)
                .addJSONArrayBody(params);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }
        post(request, requestListener);
    }

    public static void post(String url, JSONObject params, final RequestListener requestListener)
    {
        final ANRequest.PostRequestBuilder request = AndroidNetworking.post(url)
                .setPriority(Priority.IMMEDIATE)
                .addJSONObjectBody(params);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }
        post(request, requestListener);
    }

    private static void post(final ANRequest.PostRequestBuilder request, final RequestListener requestListener)
    {
        addBasicData(request);

        request.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onNetworkingResponse(response, requestListener, getInstance().decryptResponses);
                    }
                    @Override
                    public void onError(ANError error) {
                        onNetworkingError(error, requestListener);
                    }
                });
    }

    //Para tratar uno directamente la respuesta
    public static void postDirect(String url, HashMap<String, String> params, JSONObjectRequestListener jsonObjectRequestListener)
    {
        final ANRequest.PostRequestBuilder request = AndroidNetworking.post(url)
                .setPriority(Priority.IMMEDIATE)
                .addApplicationJsonBody(params);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }

        addBasicData(request);
        request.build().getAsJSONObject(jsonObjectRequestListener);
    }

    public static void postDirect(String url, JSONObject params, JSONObjectRequestListener jsonObjectRequestListener)
    {
        final ANRequest.PostRequestBuilder request = AndroidNetworking.post(url)
                .setPriority(Priority.IMMEDIATE)
                .addJSONObjectBody(params);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }

        addBasicData(request);
        request.build().getAsJSONObject(jsonObjectRequestListener);
    }

    public static void put(String url, HashMap<String, String> params, final RequestListener requestListener)
    {
        final ANRequest.PostRequestBuilder request = AndroidNetworking.put(url)
                .setPriority(Priority.IMMEDIATE)
                .addApplicationJsonBody(params);

        addBasicData(request);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }
        request.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onNetworkingResponse(response, requestListener, getInstance().decryptResponses);
                    }
                    @Override
                    public void onError(ANError error) {
                        onNetworkingError(error, requestListener);
                    }
                });
    }

    //Para tratar uno directamente la respuesta
    public static void putDirect(String url, HashMap<String, String> params, JSONObjectRequestListener jsonObjectRequestListener)
    {
        final ANRequest.PostRequestBuilder request = AndroidNetworking.put(url)
                .setPriority(Priority.IMMEDIATE)
                .addApplicationJsonBody(params);

        addBasicData(request);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }
        request.build().getAsJSONObject(jsonObjectRequestListener);
    }
    public static void putDirect(String url, JSONObject params, JSONObjectRequestListener jsonObjectRequestListener)
    {
        final ANRequest.PostRequestBuilder request = AndroidNetworking.put(url)
                .setPriority(Priority.IMMEDIATE)
                .addJSONObjectBody(params);

        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }

        addBasicData(request);
        request.build().getAsJSONObject(jsonObjectRequestListener);
    }

    public static void get(String url, HashMap<String, String> params, final RequestListener requestListener) {
        get(url, params, requestListener, getInstance().decryptResponses);
    }

    public static void get(String url, HashMap<String, String> params, final RequestListener requestListener, final boolean encrypt)
    {
        final ANRequest.GetRequestBuilder request = AndroidNetworking.get(url)
                .setPriority(Priority.IMMEDIATE)
                .addPathParameter(params);

        addBasicData(request);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }
        request.build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        onNetworkingResponse(response, requestListener, encrypt);
                    }
                    @Override
                    public void onError(ANError error) {
                        onNetworkingError(error, requestListener);
                    }
                });
    }

    //Para tratar uno directamente la respuesta
    public static void getDirect(String url, HashMap<String, String> params, JSONObjectRequestListener jsonObjectRequestListener)
    {
        final ANRequest.GetRequestBuilder request = AndroidNetworking.get(url)
                .setPriority(Priority.IMMEDIATE)
                .addPathParameter(params);

        addBasicData(request);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }

        request.build().getAsJSONObject(jsonObjectRequestListener);
    }

    //Para tratar uno directamente la respuesta
    public static void deleteDirect(String url, HashMap<String, String> params, JSONObjectRequestListener jsonObjectRequestListener)
    {
        final ANRequest.DeleteRequestBuilder request = AndroidNetworking.delete(url);
        request.setPriority(Priority.IMMEDIATE);
        request.addApplicationJsonBody(params);

        addBasicData(request);
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "request: " + url);
            Log.e(TAG, "body: " + params);
        }

        request.build().getAsJSONObject(jsonObjectRequestListener);
    }

    public static void onNetworkingResponse(JSONObject response, RequestListener requestListener, boolean encrypt)
    {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "response: " + response.toString());
        }
        Response res = Response.generate(response, encrypt);
        onNetworkResponse(res);

        if (requestListener != null) {
            requestListener.onFinish(res);
        }
    }

    public static void onNetworkingError(ANError error, RequestListener requestListener)
    {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "response error: " + error.getErrorBody());
        }
        if (requestListener != null) {
            requestListener.onFinish(Response.generate(error));
        }
    }
}
