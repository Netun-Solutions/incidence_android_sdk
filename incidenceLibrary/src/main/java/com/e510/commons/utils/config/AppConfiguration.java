package com.e510.commons.utils.config;

import android.content.Context;

import com.e510.commons.utils.FontUtils;
import com.e510.commons.utils.LogUtil;
import com.google.gson.Gson;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class AppConfiguration
{
    public static final String MODULE_CORE = "core";
    public static final String MODULE_MENU = "menu";
    public static final String MODULE_ACCOUNT = "account";
    public static final String MODULE_SHOP = "shop";
    public static final String MODULE_VIDEOCALL = "videocall";



    public interface AppConfigurationListener
    {
        void onUpdated();
    }
    private AppConfigurationListener appConfigurationListener;

    private static final String TAG = "AppConfig";
    private static String DIRECTORY_NAME = "Core";
    public static String FILE_NAME = "AppConfiguration.json";
    private static String dirPath;
    private static String filePath;

    public String name;
    public String urlTerms;
    public String version;
    public String minimumVersion;
    public String updateURL;
    public String registerDevice;
    public String pushNotifications;
    public String coin;
    public ArrayList<WelcomeSlide> welcome;
    public Item initial;
    public Item initialWithoutUser;
    public Boolean needUser;
    public HashMap<String, Modules> modules;
    public Menu menu;
    public Appearance appearance;
    public int localizableVersion;

    private static AppConfiguration INSTANCE;
    public static AppConfiguration getInstance()
    {
        return INSTANCE;
    }

    private static AppConfiguration init(String json, AppConfigurationListener listener) {
        INSTANCE = new Gson().fromJson(json, AppConfiguration.class);
        INSTANCE.appConfigurationListener = listener;
        INSTANCE.updateFonts();
        return INSTANCE;
    }

    public static AppConfiguration init(Context context, AppConfigurationListener listener) {

        dirPath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + DIRECTORY_NAME;
        FileUtils.checkFolder(dirPath);

        filePath = dirPath + File.separator + FILE_NAME;
        File jsonFile = new File(filePath);
        if (jsonFile != null && jsonFile.exists())
        {
            return init(FileUtils.getStringFromFile(jsonFile.getAbsolutePath()), listener);
        }

        return init(context, FILE_NAME, listener);
    }
    public static AppConfiguration init(Context context, String fileName, AppConfigurationListener listener) {
        String json = FileUtils.getFromAssets(context, fileName);
        INSTANCE = new Gson().fromJson(json, AppConfiguration.class);
        INSTANCE.appConfigurationListener = listener;
        INSTANCE.updateFonts();
        return INSTANCE;
    }

    //OTHER METHODS

    public static void saveNewJsonConfig(String json, boolean checkVersion, boolean notify)
    {
        if (json != null) {
            try {
                AppConfiguration newAppConfiguration = new Gson().fromJson(json, AppConfiguration.class);

                if (!checkVersion || Integer.parseInt(newAppConfiguration.version) > Integer.parseInt(INSTANCE.version)) {
                    LogUtil.logE(TAG, "NEW APP CONFIG VERSION");

                    File jsonFile = new File(filePath);

                    //Para que no muestre la alerta la primera vez que se instala
                    if (!jsonFile.exists())
                        notify = false;

                    FileUtils.saveStringToFile(json, jsonFile);

                    AppConfigurationListener listener = INSTANCE.appConfigurationListener;
                    INSTANCE = new Gson().fromJson(json, AppConfiguration.class);
                    INSTANCE.appConfigurationListener = listener;
                    INSTANCE.updateFonts();


                    if (notify && listener != null) {
                        listener.onUpdated();
                    }
                }
            } catch (Exception ex) {
                LogUtil.logE(TAG, ex.getMessage(), ex);
            }
        }
    }

    public static void removeJsonFile(Context context)
    {
        dirPath = context.getExternalFilesDir(null).getAbsolutePath() + File.separator + DIRECTORY_NAME;
        FileUtils.checkFolder(dirPath);

        filePath = dirPath + File.separator + FILE_NAME;
        File jsonFile = new File(filePath);
        if (jsonFile != null && jsonFile.exists())
        {
            jsonFile.delete();
        }
    }

    public Modules getModule(String id)
    {
        Modules res = modules.get(id);
        return res;
    }

    private void updateFonts()
    {
        if (appearance.fonts != null && appearance.fonts.type != null)
        {
            if (appearance.fonts.type.primaryFont != null)
            {
                if (appearance.fonts.type.primaryFont.light != null)
                    FontUtils.PRIMARY_LIGHT = appearance.fonts.type.primaryFont.light + FontUtils.FONT_EXTENSION;
                if (appearance.fonts.type.primaryFont.medium != null)
                    FontUtils.PRIMARY_MEDIUM = appearance.fonts.type.primaryFont.medium + FontUtils.FONT_EXTENSION;
                if (appearance.fonts.type.primaryFont.regular != null)
                    FontUtils.PRIMARY_REGULAR = appearance.fonts.type.primaryFont.regular + FontUtils.FONT_EXTENSION;
                if (appearance.fonts.type.primaryFont.bold != null)
                    FontUtils.PRIMARY_BOLD = appearance.fonts.type.primaryFont.bold + FontUtils.FONT_EXTENSION;
            }

            if (appearance.fonts.type.secondaryFont != null)
            {
                if (appearance.fonts.type.secondaryFont.light != null)
                    FontUtils.SECONDARY_LIGHT = appearance.fonts.type.secondaryFont.light + FontUtils.FONT_EXTENSION;
                if (appearance.fonts.type.secondaryFont.medium != null)
                    FontUtils.SECONDARY_MEDIUM = appearance.fonts.type.secondaryFont.medium + FontUtils.FONT_EXTENSION;
                if (appearance.fonts.type.secondaryFont.regular != null)
                    FontUtils.SECONDARY_REGULAR = appearance.fonts.type.secondaryFont.regular + FontUtils.FONT_EXTENSION;
                if (appearance.fonts.type.secondaryFont.bold != null)
                    FontUtils.SECONDARY_BOLD = appearance.fonts.type.secondaryFont.bold + FontUtils.FONT_EXTENSION;
            }
        }
    }
}
