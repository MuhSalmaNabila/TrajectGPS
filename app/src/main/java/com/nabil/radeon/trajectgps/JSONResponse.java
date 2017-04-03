package com.nabil.radeon.trajectgps;

import com.nabil.radeon.trajectgps.model.AndroidVersion;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class JSONResponse {
    private AndroidVersion[] person;
    public AndroidVersion[] getPerson() {
        return person;
    }

    private AndroidVersion[] route;
    public AndroidVersion[] getRoute() {
        return route;
    }

    private AndroidVersion[] marker;
    public AndroidVersion[] getMarker() {return marker; }

    private AndroidVersion[] profile;
    public AndroidVersion[] getProfile() { return profile; }

    private AndroidVersion[] dates;
    public AndroidVersion[] getDates() { return dates; }

//    private String value;
//    public String getValue() { return value; }
//
//    private ArrayList<AndroidVersion> result;
//    public ArrayList<AndroidVersion> getResult() { return result; }
}
