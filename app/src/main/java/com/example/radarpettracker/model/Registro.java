package com.example.radarpettracker.model;

public class Registro {
    private long id;
    private static final String UUID = "4b3c88c4-41bd-11e9-b210-d663bd873d93"; //Unique Identifier
    private String imei;
    private String timeline;
    private String latitude;
    private String longitude;

    public Registro(String imei, String timeline, String latitude, String longitude) {
        this.imei = imei;
        this.timeline = timeline;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static String getUUID() {
        return UUID;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTimeline() {
        return timeline;
    }

    public void setTimeline(String register) {
        this.timeline = register;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
