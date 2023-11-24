package com.e510.commons.utils.config;

public class Fonts {
    public Type type;
    public Size size;

    public class Type {
        public Family primaryFont;
        public Family secondaryFont;
    }

    public class Family {
        public String light;
        public String medium;
        public String regular;
        public String bold;
    }

    public class Size {
        public Float huge;
        public Float big;
        public Float regular;
        public Float small;
    }
}
