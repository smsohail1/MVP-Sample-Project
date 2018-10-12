package test.com.mvpsampleproject.util;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.BatteryManager;
import android.os.Environment;
import android.os.Vibrator;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.ui.booking.service.BookingService;
import com.tcs.pickupapp.ui.courier_journey.service.GPSTrackerServiceShahrukh;
import com.tcs.pickupapp.ui.statistics.model.DistanceObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
public class Utils {
    private Context context;
    private static final int MIN_CN_LENGTH = 8;

    public Utils(Context context) {
        this.context = context;
    }

    public int getScreenWidth(Activity activity) {
        if (activity != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            return metrics.widthPixels;
        } else {
            return 0;
        }
    }

    public int getScreenHeight(Activity activity) {
        if (activity != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            return metrics.heightPixels;
        } else {
            return 0;
        }
    }

    public int getBatteryLevel() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int batLevel = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);

        return batLevel;
    }

    public boolean isInternetAvailable() {
        /*if(context != null) {
            try {
                ConnectivityManager cm =
                        (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                return isConnected;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        }else {
            return false;
        }*/
        return isInternetAvailableMoreAccurate();
    }


    private void openAlertDialog(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        // set title
        alertDialogBuilder.setTitle(title);
        // set dialog message
        alertDialogBuilder
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                })
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();
        // show it
        alertDialog.show();
    }


    public void callMobilink() {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + Uri.encode("*471*4#")));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            context.startActivity(intent);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public boolean isInternetAvailableMoreAccurate() {
        if (context != null) {
            try {
                boolean haveConnectedWifi = false;
                boolean haveConnectedMobile = false;
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                for (NetworkInfo ni : netInfo) {
                    if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                        if (ni.isConnected()) {
                            if (!(ni.getExtraInfo().toLowerCase().contains("theta")))
                                haveConnectedWifi = true;
                        }
                    if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                        if (ni.isConnected())
                            haveConnectedMobile = true;
                }
                return haveConnectedWifi || haveConnectedMobile;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public String getStringFromResourceId(int stringResourceId) {
        if (context != null) {
            return context.getResources().getString(stringResourceId);
        } else {
            return "";
        }
    }

    public boolean isTextNullOrEmpty(String text) {
        if (text == null) {
            return true;
        }
        if (text.isEmpty()) {
            return true;
        }
        if (text.equalsIgnoreCase("")) {
            return true;
        }
        return false;
    }

    public boolean isValidCN(List<GenerateSequence> CNSequences, String consignment, EditText editConsignmentNo) {
        boolean valid = false;

        if (isTextNullOrEmpty(consignment)) {

            if (editConsignmentNo != null) {
                editConsignmentNo.setError("Please enter Consignment Number");
                playErrorToneAndVibrate(context);
                editConsignmentNo.requestFocus();
            } else {
                Toast.makeText(context, "Please enter Consignment Number", Toast.LENGTH_SHORT).show();
                playErrorToneAndVibrate(context);
                editConsignmentNo.requestFocus();
            }

            valid = false;

        } else if (consignment.length() < MIN_CN_LENGTH) {
            if (editConsignmentNo != null) {
                editConsignmentNo.setError("length of CN should be " + String.valueOf(MIN_CN_LENGTH) + " or greater");
                playErrorToneAndVibrate(context);
                editConsignmentNo.requestFocus();
            } else {
                Toast.makeText(context, "length of CN should be " + String.valueOf(MIN_CN_LENGTH), Toast.LENGTH_SHORT).show();
            }
            valid = false;
        } else if (CNSequences.size() > 0) {
            /*
             * check..
             * CN available in Generate CN Table or Not
             * */
            for (GenerateSequence cnSequence : CNSequences) {
                long cn = Long.parseLong(consignment);
                long from = Long.parseLong(cnSequence.getCN_from());
                long to = Long.parseLong(cnSequence.getCN_to());
                if (cn >= from && cn <= to) {
                    valid = true;
                    editConsignmentNo.setError(null);
                    break;
                }
            }

            if (!valid) {
                editConsignmentNo.setError("Invalid CN");
                playErrorToneAndVibrate(context);
                editConsignmentNo.requestFocus();
                editConsignmentNo.setSelection(0, editConsignmentNo.getText().length());
            }

        } else {
            valid = false;
            editConsignmentNo.setError("Invalid CN");
            playErrorToneAndVibrate(context);
            editConsignmentNo.requestFocus();
            editConsignmentNo.setSelection(0, editConsignmentNo.getText().length());
        }

        return valid;
    }

    public void hideSoftKeyboard(EditText editText) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager)
                    context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(
                    editText.getWindowToken(), 0);
        }
    }

    public void showSoftKeyboard(EditText editText) {
        if (context != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public String removeLeadingAndTrailingSpaces(String str) {
        return str.replaceAll("^\\s+|\\s+$", "");
    }

    public boolean isEditTextNullOrEmpty(EditText edt) {
        if (edt.getText() != null) {
            if (edt.getText().toString().trim().isEmpty()) {
                return true;
            } else {
                String trimmed = removeLeadingAndTrailingSpaces(edt.getText().toString());
                edt.setText(trimmed);
                edt.setSelection(edt.getText().length());
                return false;
            }
        } else {
            return true;
        }
    }


    public boolean minCharactersLimit(String text, int minCharacterLength) {
        if (text == null) {
            return true;
        }
        if (text.isEmpty()) {
            return true;
        }
        if (text.equalsIgnoreCase("")) {
            return true;
        }
        if (text.length() < minCharacterLength) {
            return true;
        }

        return false;
    }


    public boolean isEditTextEmpty(EditText editText) {

        return editText.getText().toString().trim().equals("");
//        if(editText.getText().toString().trim().equals("")){
//            return true;
//        }else{
//         return false;
//        }
    }


    public File createImageFileWith() throws IOException {
        final String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String imageFileName = "JPEG_" + timestamp;
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "pics");
        storageDir.mkdirs();
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }


    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type, String consignment) {
        return Uri.fromFile(getOutputMediaFile(type, consignment));
    }

    public static final int MEDIA_TYPE_IMAGE = 1;

    public File getOutputMediaFile(int type, String consignment) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                AppConstants.DIRECTORY_PICKUP_BOOKINGS);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                // Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create " +
                // IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + consignment + ".JPG");
        } else {
            return null;
        }

        return mediaFile;
    }

    public String uriToBase64(Uri uri) {
        String encodedImage = null;
        try {
            final InputStream imageStream = context.getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            encodedImage = encodeImage(selectedImage);
        } catch (Exception e) {
            encodedImage = "";
        }

        return encodedImage;
    }

    public String uriToBase64(byte[] arr) {
        return Base64.encodeToString(arr, 0);
    }

    public String getDeviceImeiNumber(final Activity activity) {
        TelephonyManager telephonyManager = (TelephonyManager) activity.getApplicationContext().getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_PHONE_STATE}, 101);

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        return telephonyManager.getDeviceId();
    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);

        return encImage;
    }

    public byte[] uriToByteArr(Uri uri) {
        byte[] byteArray;
        try {
            final InputStream imageStream = context.getContentResolver().openInputStream(uri);
            final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
            byteArray = getBytesFromBitmap(selectedImage);
        } catch (Exception e) {
            e.printStackTrace();
//            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            byteArray = null;
        }
        return byteArray;
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        if (bitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
            return stream.toByteArray();
        }
        return null;
    }

    public String convertSecondsToTimeSpent(int totalSeconds) {
        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;
        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;
        if (hours == 0) {
            return minutes + " mins spent";
        } else if (hours == 1) {
            return hours + " hour " + minutes + " mins spent ";
        } else if (hours > 1) {
            return hours + " hours " + minutes + " mins spent ";
        }
        return "";
    }

    public String getCurrentDate(String format) {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(format);
        return df.format(c.getTime());
    }

    private String convertByteArrayToBase64Image(byte[] bytes) {
        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }

    public void showJDBCLogs(String message) {
        Log.d("JDBC_LOGS", message + new SimpleDateFormat(AppConstants.TIME_FORMAT_TWO).format(new Date()));
    }

    public String getCurrentDateTime(String format) {

        String dateTime = "";
//        SimpleDateFormat dateFormat = new SimpleDateFormat(AppConstants.DATE_TIME_FORMAT_ONE, Locale.ENGLISH);
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.ENGLISH);
        dateTime = String.valueOf(dateFormat.format(new Date()));
        return dateTime;
    }


    public void startGPSTrackerService(Context context) {
        if (!isMyServiceRunning(GPSTrackerServiceShahrukh.class)) {
            this.context.startService(new Intent(context, GPSTrackerServiceShahrukh.class));
        }
    }

    public void stopGPSTrackerService(Context context) {
        if (isMyServiceRunning(GPSTrackerServiceShahrukh.class)) {
            this.context.stopService(new Intent(context, GPSTrackerServiceShahrukh.class));
        }
    }

    public void startBookingService(String flag, Context context) {
        if (isInternetAvailableMoreAccurate()) {
            if (!isMyServiceRunning(BookingService.class)) {
                Intent i = new Intent(context, BookingService.class);
                i.putExtra("flag", flag);
                this.context.startService(i);
            }
        } else {
            Toast.makeText(context, context.getString(R.string.please_check_internet_connection), Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public long getGMTTimestampByJavaDateString(String dateStringOriginal) throws ParseException {
        DateFormat dateFormatGmt1 = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
        Date date = dateFormatGmt1.parse(dateStringOriginal);
        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        calendar.setTime(date);
        String dateString = String.valueOf(calendar.get(Calendar.YEAR)) + "-"
                + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-"
                + String.valueOf(calendar.get(Calendar.DAY_OF_MONTH)) + " "
                + String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)) + ":"
                + String.valueOf(calendar.get(Calendar.MINUTE)) + ":"
                + String.valueOf(calendar.get(Calendar.SECOND));

        DateFormat dateFormatGmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date date1 = dateFormatGmt.parse(dateString);
        long unixTime = (long) date1.getTime() / 1000;
        return unixTime;
    }

    public void setupParent(final Activity activity, View view) {
        //Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard2(activity);
                    return false;
                }
            });
        }
        //If a layout container, iterate over children
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupParent(activity, innerView);
            }
        }
    }

    private void hideSoftKeyboard2(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void playErrorToneAndVibrate(Context context) {
        // play on no origin found error
        // play on get consignment detail call
        try {
            //changing mode from silent to normal and increasing sound to max and vibrate
            AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            am.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            am.setStreamVolume(AudioManager.STREAM_NOTIFICATION,2, 0);
            am.setStreamVolume(AudioManager.STREAM_MUSIC, 2, 0);
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(1500);

            MediaPlayer mediaPlayer = MediaPlayer.create(context.getApplicationContext(), R.raw.error_tone_2);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    if (mediaPlayer != null) {
                        mediaPlayer.release();
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void enableWifiAndConnectToInternet() {
        if (context != null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (!wifiManager.isWifiEnabled() && !mWifi.isConnected()) {
                wifiManager.setWifiEnabled(true);
            }
        }
    }

    public boolean isWifiInternetAvailable() {
        if (context != null) {
            try {
                boolean haveConnectedWifi = false;
                ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo[] netInfo = cm.getAllNetworkInfo();
                for (NetworkInfo ni : netInfo) {
                    if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                        if (ni.isConnected()) {
                            if (!(ni.getExtraInfo().toLowerCase().contains("theta")))
                                haveConnectedWifi = true;
                        }
                }
                return haveConnectedWifi;
            } catch (Exception ex) {
                ex.printStackTrace();
                return false;
            }
        } else {
            return false;
        }
    }

    public void disableWifi() {
        if (context != null) {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            if (wifiManager.isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }
        }
    }

    public List<DistanceObject> readFileAndGetLatLongs(SessionManager sessionManager) throws Exception {
        String line = null;
        List<DistanceObject> latLngs = new ArrayList<>();
        File root = new File(Environment.getExternalStorageDirectory(), AppConstants.DIRECTORY_PICKUP_TRAVEL_LOG);
        File gpxfile = null;
        // Checking if file exists in today's date
        File[] files = root.listFiles();
        boolean isFound = false;
        for(File file : files){
            if(file.getName().length() > 18) {
                String filenameWithoutTime = file.getName().split("_")[0];
                String courierCodeInFile = file.getName().split("_")[2].split("\\.")[0];
                String dateOnly = "PK" + getCurrentDate().split("_")[0];
                if (filenameWithoutTime.equals(dateOnly)) {
                    if (courierCodeInFile.equals(sessionManager.getCourierCode())) {
                        gpxfile = file;
                        isFound = true;
                        break;
                    }
                }
            }
        }
        if(!isFound){
            throw new FileNotFoundException();
        }
        if (!gpxfile.exists()) {
            throw new FileNotFoundException();
        }
        FileInputStream fileInputStream = new FileInputStream(gpxfile);
        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        while ((line = bufferedReader.readLine()) != null) {
            String[] splits = line.split("\\|");
            latLngs.add(new DistanceObject(Double.parseDouble(splits[1]), Double.parseDouble(splits[2]), splits[3]));
        }
        fileInputStream.close();

        bufferedReader.close();

        return latLngs;
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(AppConstants.DATE_TIME_FORMAT_THREE);
        return df.format(c.getTime());
    }

    public Date getCurrentDateByGMTPlus5(){
        try {
            DateFormat dateFormatGmt = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT+5"));
            return dateFormatGmt.parse(new Date().toString());
        }catch (Exception ex){
            ex.printStackTrace();
            return new Date();
        }
    }

    public Date getCurrentDateButTime5AMAsGMTPlus5(){
        try {
            DateFormat dateFormatGmt = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            String[] splits = new Date().toString().split(" ");
            String dateString = splits[0]+" "+splits[1]+" "+splits[2]+" 05:00:00 "+splits[4]+" "+splits[5];
            return dateFormatGmt.parse(dateString);
        }catch (Exception ex){
            ex.printStackTrace();
            return new Date();
        }
    }

    public Date getCurrentDateButTime8AMAsGMTPlus5(){
        try {
            DateFormat dateFormatGmt = new SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy");
            dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
            String[] splits = new Date().toString().split(" ");
            String dateString = splits[0]+" "+splits[1]+" "+splits[2]+" 08:00:00 "+splits[4]+" "+splits[5];
            return dateFormatGmt.parse(dateString);
        }catch (Exception ex){
            ex.printStackTrace();
            return new Date();
        }
    }
}
