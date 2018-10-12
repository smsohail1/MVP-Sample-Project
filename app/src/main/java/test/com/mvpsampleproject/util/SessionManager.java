package test.com.mvpsampleproject.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.tcs.pickupapp.data.room.model.CourierInfo;

import javax.inject.Inject;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
public class SessionManager {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;
    private int PRIVATE_MODE = 0;

    private static final String IS_LOGIN = "isLoggedIn";
    private static final String KEY_COURIER_CODE = "courier_code";
    private static final String KEY_COURIER_PASSWORD = "courier_password";
    private static final String KEY_NAME = "courier_name";
    private static final String KEY_ROUTE = "courier_route";
    private static final String KEY_STATION = "courier_station";
    private static final String KEY_OMS_IP = "oms_ip";
    private static final String KEY_OMS_SERVICE_NAME = "oms_service_name";
    private static final String KEY_LOW_BATTERY_PERCENTAGE = "minimum_low_battery_percentage";
    private static final String KEY_MINIMUM_BRIGHTNESS_LEVEL = "minimum_brightness_level";
    private static final String KEY_CURRENT_LATITUDE = "current_latitude";
    private static final String KEY_CURRENT_LONGITUDE = "current_longitude";
    private static final String KEY_TRANSIT_TIME_CALCULATOR_STATIONS = "transit_time_calculator_stations";
    private static final String KEY_MINIMUM_CAMERA_BARCODE_PERCENT = "minimum_camera_barcode_percentage";
    private static final String KEY_BOOKING_ALERTS_PIPE_DATA = "booking_alerts_pipe_data";
    private static final String KEY_ROUTE_NAVIGATOR_LAST_VIEWED_DATE = "route_last_viewed";

    @Inject
    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(AppConstants.PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String courierCode, String courierName, String courierPassword, String route, String station, String lowBatteryPercentage, String minimumBrightnessLevel) {
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_COURIER_CODE, courierCode);
        editor.putString(KEY_NAME, courierName);
        editor.putString(KEY_COURIER_PASSWORD, courierPassword);
        editor.putString(KEY_ROUTE, route);
        editor.putString(KEY_STATION, station);
        editor.putString(KEY_LOW_BATTERY_PERCENTAGE, lowBatteryPercentage);
        editor.putString(KEY_MINIMUM_BRIGHTNESS_LEVEL, minimumBrightnessLevel);
        editor.commit();
    }

    public void logoutUser() {
        editor.remove(IS_LOGIN);
        // no need to clear all below data according to old pickup app - added by Shahrukh Malik
        /*editor.remove(KEY_COURIER_CODE);
        editor.remove(KEY_NAME);
        editor.remove(KEY_COURIER_PASSWORD);
        editor.remove(KEY_ROUTE);
        editor.remove(KEY_STATION);
        editor.remove(KEY_LOW_BATTERY_PERCENTAGE);
        editor.remove(KEY_MINIMUM_BRIGHTNESS_LEVEL);*/
        editor.apply();
        editor.commit();
    }

    public void clearData() {
        editor.remove(IS_LOGIN);
        editor.remove(KEY_COURIER_CODE);
        editor.remove(KEY_NAME);
        editor.remove(KEY_COURIER_PASSWORD);
        editor.remove(KEY_ROUTE);
        editor.remove(KEY_STATION);
        editor.remove(KEY_OMS_IP);
        editor.remove(KEY_OMS_SERVICE_NAME);
        editor.remove(KEY_LOW_BATTERY_PERCENTAGE);
        editor.remove(KEY_MINIMUM_BRIGHTNESS_LEVEL);
        editor.remove(KEY_CURRENT_LATITUDE);
        editor.remove(KEY_CURRENT_LONGITUDE);
        editor.remove(KEY_TRANSIT_TIME_CALCULATOR_STATIONS);
        editor.remove(KEY_MINIMUM_CAMERA_BARCODE_PERCENT);
        editor.remove(KEY_BOOKING_ALERTS_PIPE_DATA);
        editor.apply();
        editor.commit();
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        editor.putBoolean(IS_LOGIN, isLoggedIn);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(IS_LOGIN, false);
    }

    public CourierInfo getCourierInfo() {
        return new CourierInfo(
                pref.getString(KEY_NAME, ""),
                pref.getString(KEY_ROUTE, ""),
                pref.getString(KEY_STATION, ""),
                pref.getString(KEY_COURIER_CODE, "")
        );
    }

    public String getCourierStation() {
        return pref.getString(KEY_STATION, "");
    }

    public void setOMSIP(String omsIp) {
        editor.putString(KEY_OMS_IP, omsIp);
        editor.commit();
    }

    public String getOMSIP() {
        return pref.getString(KEY_OMS_IP, "");
    }

    public void setOMSServiceName(String serviceName) {
        editor.putString(KEY_OMS_SERVICE_NAME, serviceName);
        editor.commit();
    }

    public String getOMSServiceName() {
        return pref.getString(KEY_OMS_SERVICE_NAME, "");
    }

    public String getKeyLowBatteryPercentage() {
        return pref.getString(KEY_LOW_BATTERY_PERCENTAGE, "");
    }

    public void setLowBatteryPercentage(String lowBatteryPercentage) {
        editor.putString(KEY_LOW_BATTERY_PERCENTAGE, lowBatteryPercentage);
        editor.commit();
    }

    public String getCourierCode() {
        return pref.getString(KEY_COURIER_CODE, "");
    }

    public String getCourierPassword() {
        return pref.getString(KEY_COURIER_PASSWORD, "");
    }

    public void setCurrentLatitude(String latitude) {
        editor.putString(KEY_CURRENT_LATITUDE, latitude);
        editor.commit();
    }

    public String getCurrentLatitude() {
        return pref.getString(KEY_CURRENT_LATITUDE, "");
    }

    public void setCurrentLongitude(String longitude) {
        editor.putString(KEY_CURRENT_LONGITUDE, longitude);
        editor.commit();
    }

    public String getCurrentLongitude() {
        return pref.getString(KEY_CURRENT_LONGITUDE, "");
    }


    public void setTransitTimeCalculatorStationStatus(String transitTimeCalculator) {
        editor.putString(KEY_TRANSIT_TIME_CALCULATOR_STATIONS, transitTimeCalculator);
        editor.commit();
    }

    public String getTransitTimeCalculatorStationStatus() {
        return pref.getString(KEY_TRANSIT_TIME_CALCULATOR_STATIONS, "");
    }


    public void setMinimumCameraBarcodePercent(String cameraBarcodePercent) {
        editor.putString(KEY_MINIMUM_CAMERA_BARCODE_PERCENT, cameraBarcodePercent);
        editor.commit();
    }

    public String getMinimumCameraBarcodePercent() {
        return pref.getString(KEY_MINIMUM_CAMERA_BARCODE_PERCENT, "");
    }

    public void setBookingAlertsPipeData(String pipeData) {
        editor.putString(KEY_BOOKING_ALERTS_PIPE_DATA, pipeData);
        editor.commit();
    }

    /*public void appendBookingAlertsPipeData(String pipeSeperatedData) {
        String existingPipeData = pref.getString(KEY_BOOKING_ALERTS_PIPE_DATA, "");
        if(existingPipeData.equals("")) {
            editor.putString(KEY_BOOKING_ALERTS_PIPE_DATA, pipeSeperatedData);
        }else {
            editor.putString(KEY_BOOKING_ALERTS_PIPE_DATA, existingPipeData+pipeSeperatedData);
        }
        editor.commit();
    }*/

    public String getBookingAlertsPipeData() {
        return pref.getString(KEY_BOOKING_ALERTS_PIPE_DATA, "");
    }

    public void setCourierRoute(String courierRoute) {
        editor.putString(KEY_ROUTE, courierRoute);
        editor.commit();
    }

    public String getCourierRoute() {
        return pref.getString(KEY_ROUTE, "");
    }

    public void setRouteNavigatorLastViewedDate(String date) {
        editor.putString(KEY_ROUTE_NAVIGATOR_LAST_VIEWED_DATE, date);
        editor.commit();
    }

    public String getRouteNavigatorLastViewedDate() {
        return pref.getString(KEY_ROUTE_NAVIGATOR_LAST_VIEWED_DATE, "");
    }
}

















