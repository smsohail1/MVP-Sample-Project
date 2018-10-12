package test.com.mvpsampleproject.ui.booking.camerahelper;

import android.os.Environment;

import com.tcs.pickupapp.util.AppConstants;

import java.text.SimpleDateFormat;
import java.util.Date;

public class global {
    public static String barcodeDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + AppConstants.DIRECTORY_PICKUP_BOOKINGS;
    public static int PREFERED_PREVIEW_WIDTH = 1280;
    public static int PREFERED_PREVIEW_HEIGHT = 720;

    public static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MM/dd/yyyy%20HH:mm:ss%20a");
        return String.valueOf(dateFormat.format(new Date()));
    }
}
