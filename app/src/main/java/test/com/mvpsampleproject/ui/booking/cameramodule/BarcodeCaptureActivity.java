/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package test.com.mvpsampleproject.ui.booking.cameramodule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.MultiProcessor;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.ui.booking.BookingMVP;
import com.tcs.pickupapp.ui.booking.camerahelper.BarcodeGraphic;
import com.tcs.pickupapp.ui.booking.camerahelper.BarcodeTrackerFactory;
import com.tcs.pickupapp.ui.booking.camerahelper.CameraSource;
import com.tcs.pickupapp.ui.booking.camerahelper.CameraSourcePreview;
import com.tcs.pickupapp.ui.booking.camerahelper.GraphicOverlay;
import com.tcs.pickupapp.ui.booking.camerahelper.global;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.ImageCompression;
import com.tcs.pickupapp.util.ProgressCustomDialogController;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;
import com.tcs.pickupapp.util.callback.ServiceError;
import com.tcs.pickupapp.util.callback.ServiceListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Activity for the multi-tracker app.  This app detects barcodes and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and ID of each barcode.
 */
public final class BarcodeCaptureActivity extends Activity {
    private static final String TAG = "Barcode-reader";
    //    ImageDialogAsyncTask imageDialogAsyncTask;
    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    public static List<GenerateSequence> CNSequences = new ArrayList<>();

    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    public static final int MEDIA_TYPE_IMAGE = 1;
    // constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String Width_Spec = "Width";
    public static final String Bulk = "isBulk";
    public static final String Height_Spec = "Height";
    public static final String BarcodeObject = "BarcodeObject";
    public static final String BarcodeLabel = "Barcode";
    public static final String BarcodeLabelDateTime = "BarcodeDateTime";
    public static final int REQUEST_CAPTURE_BARCODE = 100;


    @BindView(R.id.graphicOverlay)
    GraphicOverlay graphicOverlay;
    @BindView(R.id.preview)
    CameraSourcePreview preview;
    @BindView(R.id.btnToggleFlash)
    ToggleButton btnToggleFlash;
    @BindView(R.id.topLayout)
    RelativeLayout topLayout;
    Unbinder unbinder;

    private CameraSource mCameraSource;
    //    private CameraSourcePreview mPreview;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    // helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private String photopath = "";
    private boolean isBulk = true;
    private ArrayList<String> barcodeList;
    private ArrayList<String> barcodeDateTimeList;
    private static final String ARG_isBulk = "isBulk";
    private ProgressCustomDialogController progressDialogControllerPleaseWait;
    private ImageCompression imageCompression;
    private static BookingMVP.Presenter presenter;
    @Inject
    Utils utils;
    @Inject
    ToastUtil toastUtil;
    @Inject
    protected SessionManager sessionManager;
    private static com.tcs.pickupapp.data.room.model.Booking booking;
    //    String spinPayModPosition = "", spinSrvcPosition = "", spinProdPosition = "", spinHandPosition = "";
    //private Typeface fontello;
    //private Barcode _barcode;
    double latitude = 0, longitude = 0;
    String Weight = "", Pieces = "", Count = "", UserStation = "", CourCd = "", Route = "", Hand = "", Srvc = "", Decval = "",
            Shipper = "", shipphn = "", Prod = "", Retake = "", paymode = "", edGST = "", edCourChrgs = "",
            encodeSign = "", CUST = "", OthCharg = "";


    /**
     * Initializes the UI and creates the detector pipeline.
     */

    public void GetValues(com.tcs.pickupapp.data.room.model.Booking _booking, List<GenerateSequence> _CNSequences, BookingMVP.Presenter _presenter) {

        CNSequences = _CNSequences;
        booking = _booking;
        presenter = _presenter;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) this.getApplication()).getAppComponent().inject(this);
        setContentView(R.layout.barcode_capture);
        final ViewGroup view = (ViewGroup) ((ViewGroup) this
                .findViewById(android.R.id.content)).getChildAt(0);
        utils.setupParent(this, view);
        initializeViews(view);

        barcodeList = new ArrayList<String>();
        barcodeDateTimeList = new ArrayList<String>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = getIntent().getIntExtra(Width_Spec, 2560);
        int height = getIntent().getIntExtra(Height_Spec, 1440);
        Log.d("Width/height ", width + "/" + height);
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false, width, height);
        } else {
            requestCameraPermission();
        }
        BarcodeCaptureActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        gestureDetector = new GestureDetector(this, new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());

        btnToggleFlash.setBackgroundResource(R.drawable.flash);
        btnToggleFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
            }
        });

        unbinder = ButterKnife.bind(this, view);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                /*if (keyCode == KeyEvent.KEYCODE_BACK) {
                    getFragmentManager().popBackStack();
                    return true;
                }*/
                return false;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {

                boolean b = scaleGestureDetector.onTouchEvent(e);

                boolean c = gestureDetector.onTouchEvent(e);

                return b || c || true;

            }
        });
    }


   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.barcode_capture, container, false);
        utils.setupParent(getActivity(), view);
        initializeViews(view);

        barcodeList = new ArrayList<String>();
        barcodeDateTimeList = new ArrayList<String>();

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.d("Width/height ", width + "/" + height);
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false, width, height);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(getActivity(), new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(getActivity(), new ScaleListener());

        btnToggleFlash.setBackgroundResource(R.drawable.flash);
        btnToggleFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                } else {
                    mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                }
            }
        });

        unbinder = ButterKnife.bind(this, view);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                    getFragmentManager().popBackStack();
                    return true;
                }
                return false;
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent e) {

                boolean b = scaleGestureDetector.onTouchEvent(e);

                boolean c = gestureDetector.onTouchEvent(e);

                return b || c || true;

            }
        });
        return view;
    }*/


    private void initializeViews(View view) {
        ButterKnife.bind(this, view);
        progressDialogControllerPleaseWait = new ProgressCustomDialogController(this, R.string.please_wait);
        imageCompression = new ImageCompression();

        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) view.findViewById(R.id.graphicOverlay);
        //((AppCompatActivity) this).getSupportActionBar().hide();

    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(this, permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(BarcodeCaptureActivity.this, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }


    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the barcode detector to detect small barcodes
     * at long distances.
     * <p/>
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash, int width, int height) {

        Context context = this;

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        //BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, BarcodeCaptureActivity.this);
        barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

        if (!barcodeDetector.isOperational()) {
            // Note: The first time that an app using the barcode or face API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any barcodes
            // and/or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = context.registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(context, R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the barcode detector to detect small barcodes
        // at long distances.
        CameraSource.Builder builder = new CameraSource.Builder(context, barcodeDetector)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(width, height)
                .setRequestedFps(30.0f);

        // make sure that auto focus is an available option
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            builder = builder.setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null);
        }

        mCameraSource = builder
                .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                .build();
    }

    private String errorMessage = "Invalid Barcode";

    public void captureImage(final Barcode barcodeNo) {

        CameraSource.PictureCallback myPictureCallback_RAW = new CameraSource.PictureCallback() {

            @Override
            public void onPictureTaken(final byte[] data) {

                onPause();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        /* if (isValidCN(CNSequences, barcodeNo.displayValue)) {*/
                        //     if (utils.isValidCN())
                        if (utils.isValidCN(CNSequences, barcodeNo.displayValue, new EditText(BarcodeCaptureActivity.this))) {
                            presenter.checkAssignment(barcodeNo.displayValue, new ServiceListener<Boolean>() {
                                @Override
                                public void onSuccess(Boolean b) {
                                    if (!b) {
                                        callPopup(barcodeNo, global.getCurrentDateTime(), data);
                                    } else {
                                        onResume();
                                        toastUtil.showToastShortTime("Duplicate CN");

                                        //utils.playErrorToneAndVibrate(this);
                                    }
                                }

                                @Override
                                public void onError(ServiceError error) {
                                    onResume();
                                    toastUtil.showToastShortTime(error.getMessage());
                                }
                            });
                        }
                        /*}*/
                        else {

                            AlertDialog alertDialog = new AlertDialog.Builder(BarcodeCaptureActivity.this).create();
                            alertDialog.setTitle("Barcode error");

                            if (errorMessage == null) errorMessage = "Invalid Barcode";
                            alertDialog.setMessage(errorMessage);
                            alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    onResume();
                                }
                            });
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            onResume();
                                        }
                                    });
                            alertDialog.show();

                        }
                    }
                });
            }

        };

        mCameraSource.takePicture(null, myPictureCallback_RAW);
    }

    private String saveImage(byte[] data, Barcode barcode) {

        FileOutputStream out = null;
        File imageFile = utils.getOutputMediaFile(MEDIA_TYPE_IMAGE, barcode.displayValue);
        // File imageFile = new File(appPath);
        try {
            out = new FileOutputStream(imageFile);
//            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(imageFile)));
            out.write(data);
            out.close();
            return imageFile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            Log.e("FileNotFoundException", e.toString());
        } catch (IOException e) {
            Log.e("IOException", e.toString());
        }
        return "";
    }

    private void callPopup(final Barcode barcode, final String currentDateTime, final byte[] data) {
        LayoutInflater layoutInflater
                = (LayoutInflater) this
                .getSystemService(this.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.preview_barcode, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.MATCH_PARENT);

        TextView btnCancel = (TextView) popupView.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) popupView.findViewById(R.id.btnOk);
        TextView txtBarcode = (TextView) popupView.findViewById(R.id.txtBarcode);
        ImageView previewBarcode = (ImageView) popupView.findViewById(R.id.previewBarcode);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String appPath = global.barcodeDir;
        Bitmap bitmap;

        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        txtBarcode.setText(barcode.displayValue);
        previewBarcode.setImageDrawable(new BitmapDrawable(getResources(), bitmap));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int repeatIndex = -1;
                for (int i = 0; i < barcodeList.size(); i++) {
                    if (barcodeList.get(i).equals(barcode.displayValue)) {
                        repeatIndex = i;
                    }
                }
                if (repeatIndex > -1) {
                    barcodeList.remove(repeatIndex);
                    barcodeDateTimeList.remove(repeatIndex);
                }
                barcodeList.add(barcode.displayValue);
                barcodeDateTimeList.add(currentDateTime);

                if (data != null)
                    photopath = saveImage(data, barcode);

                if (isBulk) {
                    saveData(barcode.displayValue, photopath);
                    popupWindow.dismiss();
                    onResume();
                } else {
                    popupWindow.dismiss();
                    Intent intnt = new Intent();
                    intnt.putExtra(BarcodeLabel, barcodeList);
                    intnt.putExtra(BarcodeLabelDateTime, barcodeDateTimeList);
                    setResult(CommonStatusCodes.SUCCESS, intnt);
                    finish();
                }
            }
        });

        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                onResume();
            }
        });

        //popupWindow.showAsDropDown(btnOpenPopup, 50, -30);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            int width = getIntent().getIntExtra(Width_Spec, 2560);
            int height = getIntent().getIntExtra(Height_Spec, 1440);
            createCameraSource(autoFocus, useFlash, width, height);
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                this);
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(this, code, RC_HANDLE_GMS);
            dlg.show();
        }

        if (mCameraSource != null) {
            try {
                preview.start(mCameraSource, mGraphicOverlay);
//                preview1.start(mCameraSource, mGraphicOverlay);
            } catch (IOException e) {
                Log.e(TAG, "Unable to start camera source.", e);
                mCameraSource.release();
                mCameraSource = null;
            }
        }
    }

    /**
     * onTap is called to capture the oldest barcode currently detected and
     * return it to the caller.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {

        //TODO: use the tap position to select the barcode.
        BarcodeGraphic graphic = mGraphicOverlay.getFirstGraphic();
        Barcode barcode = null;
        if (graphic != null) {
            barcode = graphic.getBarcode();
            if (barcode != null) {
                Intent data = new Intent();
                data.putExtra(BarcodeObject, barcode);
                this.setResult(CommonStatusCodes.SUCCESS, data);
                this.finish();
            } else {
                Log.d(TAG, "barcode data is null");
            }
        } else {
            Log.d(TAG, "no barcode detected");
        }
        return barcode != null;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        //  ((AppCompatActivity) this).getSupportActionBar().show();
    }

    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (preview != null) {
            preview.stop();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (preview != null && preview.isActivated()) {
            preview.stop();
            preview.release();
        }
        unbinder.unbind();
    }

   /* @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (preview != null && preview.isActivated()) {
            preview.stop();
            preview.release();
        }
        unbinder.unbind();
    }*/

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }

    // public String cnNumber = "";

    private void saveData(String CN, String photopath) {
        if (isValidCN(CNSequences, CN)) {
            String[] data = new String[2];
            data[0] = photopath;
            data[1] = CN;
            // cnNumber = CN;
            new CompressImage().execute(data);
        }
    }


    private class CompressImage extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            //Bitmap bitmap = imageCompression.getCompressedBitmapMoreOptimized(params[0], BarcodeCaptureActivity.this);
            Bitmap bitmap = GetBitmapFromPath(params[0]);
            booking.BookingData(params[1], sessionManager.getCurrentLatitude(), sessionManager.getCurrentLongitude());
            if (bitmap != null) {
                String newPath = savePhoto(bitmap, params[1]);
                bitmap.recycle();
                return newPath;
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            progressDialogControllerPleaseWait.hideDialog();
            try {

                File newFile = new File(s);
                Uri photoURI;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoURI = FileProvider.getUriForFile(BarcodeCaptureActivity.this,
                            getString(R.string.file_provider_authority),
                            newFile);
                } else {
                    photoURI = Uri.fromFile(newFile);
                }

                booking.setImage(utils.uriToByteArr(photoURI));
                booking.setIsSave("0");
                presenter.insertBooking(booking);


                // presenter.fetchBookings(booking.getCustomerNumber());
                if (isBulk) {
                    presenter.fetchBookingCountsByAccountNo(booking.getCustomerNumber(), 0);
                } else {
                    presenter.fetchBookingCountsByAccountNo(booking.getCustomerNumber(), 1);

                }
                presenter.setConsignmentAdapter(getCloneBooking(booking));
                createLogFile();
                presenter.enableSaveButton();
               

            } catch (Exception ex) {
                toastUtil.showToastLongTime(ex.getMessage());
                ex.printStackTrace();
                return;
            }
        }
    }


    private com.tcs.pickupapp.data.room.model.Booking getCloneBooking(com.tcs.pickupapp.data.room.model.Booking booking) {

        com.tcs.pickupapp.data.room.model.Booking newBooking = new com.tcs.pickupapp.data.room.model.Booking();

        newBooking.setCustomerName(booking.getCustomerName());
        newBooking.setCustomerNumber(booking.getCustomerNumber());
        newBooking.setHandlingInstruction(booking.getHandlingInstruction());
        newBooking.setCnNumber(booking.getCnNumber());
        newBooking.setCnType(booking.getCnType());
        newBooking.setCreatedDate(booking.getCreatedDate());
        newBooking.setLatitude(booking.getLatitude());
        newBooking.setLongitude(booking.getLongitude());
        newBooking.setPieces(booking.getPieces());
        newBooking.setWeight(booking.getWeight());
        newBooking.setServiceNumber(booking.getServiceNumber());
        newBooking.setPaymentMode(booking.getPaymentMode());
        newBooking.setDeclaredValue(booking.getDeclaredValue());
        newBooking.setShipperName(booking.getCustomerRef());
        newBooking.setCourierCode(booking.getCourierCode());
        newBooking.setProduct(booking.getProduct());
        newBooking.setRoute(booking.getRoute());
        newBooking.setOriginStation(booking.getOriginStation());
        newBooking.setOtherCharges(booking.getOtherCharges());
        newBooking.setImage(booking.getImage());
        newBooking.setImei(booking.getImei());
        newBooking.setTransmitStatus(booking.getTransmitStatus());
        newBooking.setCustomerRef(booking.getCustomerRef());
        newBooking.setIsRetake(booking.getIsRetake());
        newBooking.setNoOfAttempts(booking.getNoOfAttempts());
        newBooking.setDimensions(booking.getDimensions());
        newBooking.setIsSave(booking.getIsSave());

        return newBooking;
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(AppConstants.DATE_TIME_FORMAT_THREE);
        return df.format(c.getTime());
    }

    public String getCurrentDateForFile() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(AppConstants.DATE_TIME_FORMAT_TWO);
        return df.format(c.getTime());
    }

    private void createLogFile() {
        String formattedDate = getCurrentDate();
        String dateforFile = getCurrentDateForFile();
        if (sessionManager != null) {
            if (sessionManager.getCourierCode() != null && !sessionManager.getCourierCode().equals("")) {
                generateNoteOnSD(sessionManager.getCourierCode(), sessionManager.getCourierCode() + "|" + sessionManager.getCurrentLatitude() + "|" + sessionManager.getCurrentLongitude() + "|" + dateforFile
                        + "|" + booking.getCnNumber().toString().trim() + "|" + booking.getCustomerName().toString().trim() + "|" + "PICKED" + "|" + sessionManager.getCourierRoute() + "|" + "PICKED" + "|", formattedDate);
            }
        }
    }

    public void generateNoteOnSD(String courierCode, String sBody, String formattedDate) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), AppConstants.DIRECTORY_PICKUP_TRAVEL_LOG);
            Log.i("Body: ", sBody);
            if (!root.exists()) {
                root.mkdirs();
            }
            Thread.sleep(200);
            File gpxfile = null;
            // Checking if file exists in today's date
            File[] files = root.listFiles();
            boolean isFound = false;
            for (File file : files) {
                if (file.getName().length() > 18) {
                    String filenameWithoutTime = file.getName().split("_")[0];
                    String courierCodeInFile = file.getName().split("_")[2].split("\\.")[0];
                    String dateOnly = "PK" + formattedDate.split("_")[0];
                    if (filenameWithoutTime.equals(dateOnly)) {
                        if (courierCodeInFile.equals(courierCode)) {
                            gpxfile = file;
                            isFound = true;
                            break;
                        }
                    }
                }
            }
            if (!isFound) {
                gpxfile = new File(root, "PK" + formattedDate + courierCode + ".txt");
            }
            FileOutputStream outputStream = new FileOutputStream(gpxfile, true);
            outputStream.write(sBody.getBytes());
            String br = "\r\n";
            outputStream.write(br.getBytes());
            outputStream.flush();
            outputStream.close();
            refreshFileManager(gpxfile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void refreshFileManager(String filePath) {
        try {
            MediaScannerConnection
                    .scanFile(
                            this,
                            new String[]{filePath},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(
                                        String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned "
                                            + path + ":");
                                    Log.i("ExternalStorage", "-> uri="
                                            + uri);

                                }
                            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public String savePhoto(Bitmap bmp, String consignmentNumber) {
        FileOutputStream out = null;
        try {
            File imageFile = utils.getOutputMediaFile(MEDIA_TYPE_IMAGE, consignmentNumber);
            out = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
            imageCompression.refreshGallery(imageFile.getAbsolutePath(), this);
            out = null;
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean isValidCN(List<GenerateSequence> CNSequences, String consignment) {
        boolean valid = false;
        final int MIN_CN_LENGTH = 8;

        if (isTextNullOrEmpty(consignment)) {

            Toast.makeText(this, "Please enter Consignment Number", Toast.LENGTH_SHORT).show();

            valid = false;

        } else if (consignment.length() < MIN_CN_LENGTH) {

            Toast.makeText(this, "length of CN should be " + String.valueOf(MIN_CN_LENGTH), Toast.LENGTH_SHORT).show();

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
                    break;
                }
            }

        }

        return valid;
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

    public Bitmap GetBitmapFromPath(String Path) {
        File image = new File(Path);
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
        return bitmap;
    }

    @Override
    public void onBackPressed() {

        if (isBulk) {
            Intent intnt = new Intent();
            intnt.putExtra(BarcodeLabel, barcodeList);
            intnt.putExtra(BarcodeLabelDateTime, barcodeDateTimeList);
            setResult(CommonStatusCodes.SUCCESS, intnt);
            BarcodeCaptureActivity.this.finish();
        } else {
            setResult(RESULT_OK, null);
            super.onBackPressed();
        }
    }

}
