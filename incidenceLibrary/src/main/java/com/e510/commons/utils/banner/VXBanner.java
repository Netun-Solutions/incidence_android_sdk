package com.e510.commons.utils.banner;

import java.util.HashMap;

public class VXBanner {
    public String title;
    public String message;
    public HashMap<String, String> data;
    public String imageUrl;

    public VXBanner(String title, String message, HashMap<String, String> data, String imageUrl) {
        this.title = title;
        this.message = message;
        this.data = data;
        this.imageUrl = imageUrl;
    }
}
