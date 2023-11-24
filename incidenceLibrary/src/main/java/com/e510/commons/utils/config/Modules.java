package com.e510.commons.utils.config;

import java.util.ArrayList;

public class Modules {
    public String id;
    public ArrayList<String> needUser;
    public ArrayList<String> extra;
    public String url;
    public String homeImage;
    public ArrayList<Form> form;
    public ArrayList<Pages> pages;

    public boolean hasNeedUser(String value)
    {
        boolean res = false;

        if (value != null && needUser != null && needUser.contains(value))
        {
            res = true;
        }

        return res;
    }
}
