package com.e510.commons.utils.config;

import com.e510.commons.utils.DeviceUtils;

import java.util.HashMap;

public class Form {
    public String type;
    public String title;
    public String title_es;
    public String title_en;
    public String title_fr;
    public String title_de;
    public String icon;
    public String hint;
    public String identifier;
    public Boolean required;
    public Boolean editable;
    public HashMap<Integer, String> options;
    public String titleKey;
    public String hintKey;
    public HashMap<Integer, FieldOption> optionsExtra;

    public String localizedTitle() {
        if (DeviceUtils.getLocale().equals("es") && title_es != null) {
            return title_es;
        }
        if(DeviceUtils.getLocale().equals("en") && title_en != null) {
            return title_en;
        }
        if(DeviceUtils.getLocale().equals("fr") && title_fr != null) {
            return title_fr;
        }
        if(DeviceUtils.getLocale().equals("de") && title_de != null) {
            return title_de;
        }
        return title;
    }
}