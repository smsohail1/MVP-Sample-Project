package com.tcs.pickupapp.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shahrukh.malik on 16, April, 2018
 */
public class CourierInfo {
    @SerializedName("UserName")
    @Expose
    private String userName;
    @SerializedName("Route")
    @Expose
    private String route;
    @SerializedName("StationNumber")
    @Expose
    private String stationNumber;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public String getStationNumber() {
        return stationNumber;
    }

    public void setStationNumber(String stationNumber) {
        this.stationNumber = stationNumber;
    }
}
