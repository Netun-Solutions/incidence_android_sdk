package com.e510.commons.domain;

import java.util.Date;

public class Device {
    private String deviceId;
    private Integer idUsuario;
    private String pushId;
    private String platform;
    private String packageName;
    private String os;
    private String appVersion;
    private Date fechaAlta;
    private Date fechaUltimoLogin;
    private String build;
    private String locale;
    private String deviceString;

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }
    public String getDeviceId() {
        return this.deviceId;
    }
    public void setIdUsuario( Integer idUsuario ) {
        this.idUsuario = idUsuario;
    }
    public Integer getIdUsuario() {
        return this.idUsuario;
    }
    public void setPushId( String pushId ) {
        this.pushId = pushId;
    }
    public String getPushId() {
        return this.pushId;
    }
    public void setPlatform( String platform ) {
        this.platform = platform;
    }
    public String getPlatform() {
        return this.platform;
    }
    public void setOs( String os ) {
        this.os = os;
    }
    public String getOs() {
        return this.os;
    }
    public void setAppVersion( String appVersion ) {
        this.appVersion = appVersion;
    }
    public String getAppVersion() {
        return this.appVersion;
    }
    public void setFechaAlta( Date fechaAlta ) {
        this.fechaAlta = fechaAlta;
    }
    public Date getFechaAlta() {
        return this.fechaAlta;
    }
    public void setFechaUltimoLogin( Date fechaUltimoLogin ) {
        this.fechaUltimoLogin = fechaUltimoLogin;
    }
    public Date getFechaUltimoLogin() {
        return this.fechaUltimoLogin;
    }
    public void setBuild( String build ) {
        this.build = build;
    }
    public String getBuild() {
        return this.build;
    }
    public void setLocale( String locale ) {
        this.locale = locale;
    }
    public String getLocale() {
        return this.locale;
    }
    public void setDevice( String deviceString ) {
        this.deviceString = deviceString;
    }
    public String getDeviceString() {
        return this.deviceString;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    //----------------------------------------------------------------------
    // toString METHOD
    //----------------------------------------------------------------------

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append(deviceId);
        sb.append("|");
        sb.append(idUsuario);
        // attribute 'pushId' not usable (type = String Long Text)
        sb.append("|");
        sb.append(platform);
        sb.append("|");
        sb.append(packageName);
        sb.append("|");
        sb.append(os);
        sb.append("|");
        sb.append(appVersion);
        sb.append("|");
        sb.append(fechaAlta);
        sb.append("|");
        sb.append(fechaUltimoLogin);
        sb.append("|");
        sb.append(build);
        sb.append("|");
        sb.append(locale);
        sb.append("|");
        sb.append(deviceString);
        return sb.toString();
    }
}
