package com.e510.commons.utils.config;

import java.util.ArrayList;

public class Menu {

    public static final String TYPE_TAB = "tab";
    public static final String TYPE_SIDE = "side";

    public String type;
    public String selectedIndex;
    public ArrayList<Item> items;
    public String homeImage;

    public boolean isTab()
    {
        boolean res = false;

        if (type != null && type.equals(TYPE_TAB))
        {
            res = true;
        }

        return res;
    }
}
