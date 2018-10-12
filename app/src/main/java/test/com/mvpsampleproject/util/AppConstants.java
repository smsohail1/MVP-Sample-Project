package test.com.mvpsampleproject.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
public class AppConstants {

    public static final String BASE_URL_UAT = "http://uat-hsk.tcscourier.com";


    //Upload pickup travel log file
    //public static final String BASE_URL = "http://collect.tcscourier.com/";


    public static final String DATE_FORMAT_ONE = "yyyy-MM-dd HH:mm:ss z";
    public static final String DATE_FORMAT_TWO = "ddMMyy";
    public static final String DATE_FORMAT_THREE = "MMM dd";
    public static final String DATE_FORMAT_FOUR = "MMM dd, yyyy";
    public static final String DATE_FORMAT_FIVE = "dd/MM/yyyy";
    public static final String DATE_FORMAT_SIX = "MMMM   dd  yyyy";
    public static final String DATE_FORMAT_SEVEN_FOR_ROUTE_VIEWED_DATE = "yyyy-MM-dd";
    public static final String DATE_FORMAT_EIGHT = "MM/dd/yyyy";
    public static final String TIME_FORMAT_ONE = "hh:mm a";
    public static final String TIME_FORMAT_TWO = "hh:mm:ss";
    public static final String DATE_TIME_FORMAT_ONE = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_TIME_FORMAT_TWO = "dd/MM/yyyy HH:mm:ss";
    public static final String DATE_TIME_FORMAT_THREE = "ddMMyy_HHmmss_";
    public static final String DATE_TIME_FORMAT_FOUR = "MM/dd/yyyy HH:mm:ss a";
    public static final SimpleDateFormat TTCDateFormat = new SimpleDateFormat("yyyyMMdd", Locale.ENGLISH);
    public static final SimpleDateFormat TTCInputDate = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

    public static final int WIFI_PASSWORD_CONNECT_TIMEOUT = 16000;

    public static final String PREF_NAME = "com.tcs.pickupapp.mt";
    public static final String DATABASE_NAME = "com.tcs.pickupapp.d";


    public static int BATTERY_LEVEL = 5;
    public static final int BRIGHTNESS_LEVEL = 255;
    public static int CAMERA_BARCODE_LEVEL = 5;





    public static final String FLAG_RESET = "100";
    public static final String FLAG_SYNC = "200";

    public static final long DISCONNECT_TIMEOUT = 7200000; // 5 min = 5 * 60 * 1000 ms


}


















