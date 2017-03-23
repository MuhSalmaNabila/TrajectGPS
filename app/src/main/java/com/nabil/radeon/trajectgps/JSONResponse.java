package com.nabil.radeon.trajectgps;

import com.nabil.radeon.trajectgps.model.AndroidVersion;

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
}
