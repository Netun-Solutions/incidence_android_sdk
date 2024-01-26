package es.incidence.library;

import android.app.Application;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.e510.commons.activity.BaseActivity;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.incidence.core.Constants;
import es.incidence.core.Core;
import es.incidence.core.activity.SimpleMainActivity;
import es.incidence.core.domain.Beacon;
import es.incidence.core.domain.Incidence;
import es.incidence.core.domain.IncidenceType;
import es.incidence.core.domain.Insurance;
import es.incidence.core.domain.User;
import es.incidence.core.domain.Vehicle;
import es.incidence.core.entity.AppConfig;
import es.incidence.core.manager.Api;
import es.incidence.core.manager.IActionListener;
import es.incidence.core.manager.IActionResponse;
import es.incidence.core.manager.IRequestListener;
import es.incidence.core.manager.IResponse;
import es.incidence.library.config.IncidenceLibraryConfig;

public class IncidenceLibraryManager {

    public static final String SCREEN_OK = "SCREEN_OK";
    public static final String SCREEN_KO = "SCREEN_KO";
    public static final String NO_VALID_API_KEY = "NO_VALID_API_KEY";
    public static final String CALLIG_VALIDATE_API_KEY = "CALLIG_VALIDATE_API_KEY";
    public static IncidenceLibraryManager instance;

    private Application context;

    private Boolean validApiKey = null;
    private AppConfig appearance;
    private List<String> screens = new ArrayList<>();
    private Insurance insurance;

    public List<IncidenceType> incidencesTypes = new ArrayList<>();

    private ArrayList<BaseActivity> activities = new ArrayList<>();
    protected int stateCounter = 0;


    private IncidenceLibraryManager(final Application context, IncidenceLibraryConfig incidenceLibraryConfig) {
        this.context = context;
    }

    public static void setup(final Application context, IncidenceLibraryConfig incidenceLibraryConfig) {
        if (instance == null) {
            instance = new IncidenceLibraryManager(context, incidenceLibraryConfig);

            Core.init(context, incidenceLibraryConfig.getApikey(), incidenceLibraryConfig.getEnvironment());
        }

        instance.validateApiKey();
    }

    private void validateApiKey() {
        String json = "";
        processConfigJson(json);
        Api.validateApiKey(new IRequestListener() {
            @Override
            public void onFinish(IResponse response) {

                if (response.isSuccess())
                {
                    instance.validApiKey = true;

                    screens = response.getList("functionalities", String.class);

                    insurance = (Insurance) response.get("insurance", Insurance.class);

                    appearance = (AppConfig) response.get("appearance", AppConfig.class);

                    incidencesTypes = response.getList("incidenceTypes", IncidenceType.class);

                    String valores = response.get("literals");
                    if (valores != null) {
                        //Strip slashes
                        valores = valores.replace("\\/", "/");
                        valores = valores.replace("\\n", "\n");

                        Core.updateLiterals(valores);
                    }

                    Core.registerDeviceSdk();

                } else {
                    instance.validApiKey = false;
                }
            }
        });
    }

    private void processConfigJson(String json) {
    }

    private String validateScreen(String screen) {
        if (validApiKey == null) {
            return CALLIG_VALIDATE_API_KEY;
        } else if (validApiKey == false) {
            return NO_VALID_API_KEY;
        } else if (instance.screens.contains(screen)) {
            return SCREEN_OK;
        } else {
            return SCREEN_KO;
        }
    }

    public Intent getDeviceReviewViewController(User user, Vehicle vehicle) {
        String res = validateScreen(Constants.SCREEN_DEVICE_REVIEW);
        if (res == SCREEN_OK) {
            Intent intent = createIntent(Constants.SCREEN_DEVICE_REVIEW);
            intent.putExtra("user", user);
            intent.putExtra("vehicle", vehicle);
            return intent;
        } else {
            return processScreenError(res);
        }
    }

    public Intent getDeviceCreateViewController(User user, Vehicle vehicle) {
        String res = validateScreen(Constants.SCREEN_DEVICE_CREATE);
        if (res == SCREEN_OK) {
            Intent intent = createIntent(Constants.SCREEN_DEVICE_CREATE);
            intent.putExtra("user", user);
            intent.putExtra("vehicle", vehicle);
            return intent;
        } else {
            return processScreenError(res);
        }
    }

    public Intent getEcommerceViewController(User user, Vehicle vehicle) {
        String res = validateScreen(Constants.SCREEN_ECOMMERCE);
        if (res == SCREEN_OK) {
            Intent intent = createIntent(Constants.SCREEN_ECOMMERCE);
            intent.putExtra("user", user);
            intent.putExtra("vehicle", vehicle);
            return intent;
        } else {
            return processScreenError(res);
        }
    }

    public Intent getReportIncViewControllerFlowComplete(User user, Vehicle vehicle) {
        String res = validateScreen(Constants.SCREEN_REPOR_INC);
        if (res == SCREEN_OK) {
            Intent intent = createIntent(Constants.SCREEN_REPOR_INC);
            intent.putExtra("user", user);
            intent.putExtra("vehicle", vehicle);
            intent.putExtra("flowComplete", true);
            return intent;
        } else {
            return processScreenError(res);
        }
    }

    public Intent getReportIncViewControllerFlowSimple(User user, Vehicle vehicle) {
        String res = validateScreen(Constants.SCREEN_REPOR_INC_SIMPLE);
        if (res == SCREEN_OK) {
            Intent intent = createIntent(Constants.SCREEN_REPOR_INC);
            intent.putExtra("user", user);
            intent.putExtra("vehicle", vehicle);
            intent.putExtra("flowComplete", false);
            return intent;
        } else {
            return processScreenError(res);
        }
    }

    private Intent createIntent(String screenDeviceList) {
        Intent intent = new Intent(context, SimpleMainActivity.class);
        Bundle b = new Bundle();
        b.putString("screen", screenDeviceList);
        intent.putExtras(b);
        return intent;
    }

    private Intent processScreenError(String error) {
        Intent intent = new Intent(context, SimpleMainActivity.class);
        Bundle b = new Bundle();
        b.putString("screen", Constants.SCREEN_ERROR);
        b.putString("error", error);
        intent.putExtras(b);
        return intent;
    }

    public void activityCreated(BaseActivity activity)
    {
        if (activity != null && !activities.contains(activity))
            activities.add(activity);
    }
    public void activityDestroyed(BaseActivity activity)
    {
        if (activity != null)
            activities.remove(activity);
    }

    public BaseActivity getCurrentActivity()
    {
        BaseActivity baseActivity = null;
        if (activities.size() > 0) {
            baseActivity = activities.get(activities.size()-1);
        }

        return baseActivity;
    }

    public int getActivitiesCount()
    {
        return activities.size();
    }

    public void activityStarted()
    {
        stateCounter++;
    }

    public void activityStopped()
    {
        stateCounter--;
    }

    public int getStateCounter()
    {
        return stateCounter;
    }

    public void setViewBackground(View view) {
        try {
            if (appearance != null && appearance.background_color != null) {
                int color = Color.parseColor(appearance.background_color);
                view.setBackgroundColor(color);
            }
        } catch (Exception e) {
            Log.e("", e.getMessage(), e);
        }
    }

    public void setTextColor(TextView view) {
        try {
            if (appearance != null && appearance.letter_color != null) {
                int color = Color.parseColor(appearance.letter_color);
                view.setTextColor(color);
            }
        } catch (Exception e) {
            Log.e("", e.getMessage(), e);
        }
    }

    public Integer getTextColor() {
        if (appearance != null && appearance.letter_color != null) {
            int color = Color.parseColor(appearance.letter_color);
            return color;
        }
        return null;
    }

    public Insurance getInsurance() {
        return insurance;
    }

    public void deleteBeaconFunc(User user, Vehicle vehicle, IActionListener iActionListener) {
        String res = validateScreen(Constants.FUNC_DEVICE_DELETE);
        if (res == SCREEN_OK) {
            Api.deleteBeaconSdk(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    if (iActionListener != null) {
                        IActionResponse actionResponse;
                        if (response.isSuccess())
                        {
                            actionResponse = new IActionResponse(true);
                        }
                        else
                        {
                            actionResponse = new IActionResponse(false, response.message);
                        }

                        iActionListener.onFinish(actionResponse);
                    }
                }
            }, user, vehicle);
        } else {
            IActionResponse actionResponse = new IActionResponse(false, res);
            iActionListener.onFinish(actionResponse);
        }
    }

    public void getBeaconFunc(User user, Vehicle vehicle, IActionListener iActionListener) {
        String res = validateScreen(Constants.FUNC_DEVICE_GET);
        if (res == SCREEN_OK) {
            Api.getBeaconSdk(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    if (iActionListener != null) {
                        IActionResponse actionResponse;
                        if (response.isSuccess())
                        {
                            ArrayList<Beacon> list = response.getList("beacon", Beacon.class);
                            if (list.size() > 0) {
                                actionResponse = new IActionResponse(true);
                            } else {
                                actionResponse = new IActionResponse(false, response.message);
                            }
                        }
                        else
                        {
                            actionResponse = new IActionResponse(false, response.message);
                        }

                        iActionListener.onFinish(actionResponse);
                    }
                }
            }, user, vehicle);
        } else {
            IActionResponse actionResponse = new IActionResponse(false, res);
            iActionListener.onFinish(actionResponse);
        }
    }

    public void createIncidenceFunc(User user, Vehicle vehicle, Incidence incidence, IActionListener iActionListener) {
        String res = validateScreen(Constants.FUNC_REPOR_INC);
        if (res == SCREEN_OK) {
            Api.postIncidenceSdk(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    if (iActionListener != null) {
                        IActionResponse actionResponse;
                        if (response.isSuccess())
                        {
                            String message = null;
                            try {
                                JSONObject jsonObject = response.get();
                                JSONObject incidenceObject = jsonObject.getJSONObject("incidence");
                                int id = incidenceObject.getInt("id");
                                String externalIncidenceTypeId = incidenceObject.getString("externalIncidenceTypeId");
                                incidence.id = id;
                                incidence.externalIncidenceId = externalIncidenceTypeId;

                                message = incidence.externalIncidenceId;

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            actionResponse = new IActionResponse(true, message);
                        }
                        else
                        {
                            actionResponse = new IActionResponse(false, response.message);
                        }

                        iActionListener.onFinish(actionResponse);
                    }
                }
            }, user, vehicle, incidence);
        } else {
            IActionResponse actionResponse = new IActionResponse(false, res);
            iActionListener.onFinish(actionResponse);
        }
    }

    public void closeIncidenceFunc(User user, Vehicle vehicle, Incidence incidence, IActionListener iActionListener) {
        String res = validateScreen(Constants.FUNC_CLOSE_INC);
        if (res == SCREEN_OK) {
            Api.putIncidenceSdk(new IRequestListener() {
                @Override
                public void onFinish(IResponse response) {
                    if (iActionListener != null) {
                        IActionResponse actionResponse;
                        if (response.isSuccess())
                        {
                            actionResponse = new IActionResponse(true);
                        }
                        else
                        {
                            actionResponse = new IActionResponse(false, response.message);
                        }

                        iActionListener.onFinish(actionResponse);
                    }
                }
            }, user, vehicle, incidence);
        } else {
            IActionResponse actionResponse = new IActionResponse(false, res);
            iActionListener.onFinish(actionResponse);
        }
    }
}
