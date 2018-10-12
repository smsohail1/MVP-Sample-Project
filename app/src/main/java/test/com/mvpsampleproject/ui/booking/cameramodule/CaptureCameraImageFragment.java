package test.com.mvpsampleproject.ui.booking.cameramodule;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.booking.BookingMVP;
import com.tcs.pickupapp.ui.booking.camerahelper.BarcodeGraphic;
import com.tcs.pickupapp.ui.booking.camerahelper.CameraSource;
import com.tcs.pickupapp.ui.booking.camerahelper.CameraSourcePreview;
import com.tcs.pickupapp.ui.booking.camerahelper.GraphicOverlay;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class CaptureCameraImageFragment extends Fragment {
    private static final String TAG = "Barcode-reader";
    //    ImageDialogAsyncTask imageDialogAsyncTask;
    // intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;
    // permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;
    // constants used to pass extra data in the intent

    public static final String BarcodeObject = "BarcodeObject";
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 101;

    @BindView(R.id.graphicOverlay)
    GraphicOverlay graphicOverlay;
    @BindView(R.id.preview)
    CameraSourcePreview preview;
    @BindView(R.id.btnToggleFlash)
    ToggleButton btnToggleFlash;
    @BindView(R.id.btnToggleFlashOnOrOff)
    ToggleButton btnToggleFlashOnOrOff;
    @BindView(R.id.topLayout)
    RelativeLayout topLayout;
    Unbinder unbinder;
  /*  @BindView(R.id.lyPreview)
    LinearLayout lyPreview;*/

    private CameraSource mCameraSource;
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    private String photopath = "";
    private String consignmentNumber = "";
    private static final String ARG_ConsignmentNumber = "ConsignmentNumber";

    private static BookingMVP.Presenter presenter;
    @Inject
    Utils utils;
    @Inject
    ToastUtil toastUtil;
    @Inject
    protected SessionManager sessionManager;


    /**
     * Initializes the UI and creates the detector pipeline.
     */

    public static CaptureCameraImageFragment newInstance(String _consignmentNumber) {
        CaptureCameraImageFragment fragment = new CaptureCameraImageFragment();

        Bundle args = new Bundle();
        args.putString(ARG_ConsignmentNumber, _consignmentNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
        if (getArguments() != null) {
            consignmentNumber = getArguments().getString(ARG_ConsignmentNumber);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_image_capture, container, false);
        utils.setupParent(getActivity(), view);
        initializeViews(view);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = 1440;
        int width = 2560;
        Log.d("Width/height ", width + "/" + height);
        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(true, false, width, height);
        } else {
            requestCameraPermission();
        }

        btnToggleFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                captureImage(consignmentNumber);
            }
        });

        btnToggleFlashOnOrOff.setVisibility(View.VISIBLE);
        btnToggleFlashOnOrOff.setBackgroundResource(R.drawable.flash);
        btnToggleFlashOnOrOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
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
        return view;
    }

    private void initializeViews(View view) {
        ButterKnife.bind(this, view);

        mGraphicOverlay = (GraphicOverlay<BarcodeGraphic>) view.findViewById(R.id.graphicOverlay);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }

    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(getActivity(), permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

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

        Context context = getActivity();

        // A barcode detector is created to track barcodes.  An associated multi-processor instance
        // is set to receive the barcode detection results, track the barcodes, and maintain
        // graphics for each barcode on screen.  The factory is used by the multi-processor to
        // create a separate tracker instance for each barcode.
        //BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
       // BarcodeTrackerFactory barcodeFactory = new BarcodeTrackerFactory(mGraphicOverlay, getActivity());
       // barcodeDetector.setProcessor(new MultiProcessor.Builder<>(barcodeFactory).build());

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
        CameraSource.Builder builder = new CameraSource.Builder(context,barcodeDetector)
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

    private void callPopup(final String consignmentNumber, final byte[] data) {
        LayoutInflater layoutInflater
                = (LayoutInflater) getActivity()
                .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.preview_captured_image, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView btnCancel = (TextView) popupView.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) popupView.findViewById(R.id.btnOk);
        TextView txtBarcode = (TextView) popupView.findViewById(R.id.txtBarcode);
        ImageView previewBarcode = (ImageView) popupView.findViewById(R.id.previewBarcode);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;

        //final Bitmap resizedBitmap = getCroppedImage(lyPreview, data);

        final Bitmap resizedBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        txtBarcode.setText(consignmentNumber);
        previewBarcode.setImageDrawable(new BitmapDrawable(getResources(), resizedBitmap));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((AppCompatActivity) getActivity()).getSupportActionBar().show();
                // byte[] data = BitmapToBytesArray(resizedBitmap);

                mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

                Intent intent = new Intent(getActivity(), CaptureCameraImageFragment.class);
                intent.putExtra("ImageData", data);
                getTargetFragment().onActivityResult(CAMERA_CAPTURE_IMAGE_REQUEST_CODE, RESULT_OK, intent);
                getFragmentManager().popBackStack();


                popupWindow.dismiss();
                mCameraSource.stop();

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
            // we have permission, so create the camerasource
        /*    boolean autoFocus = getIntent().getBooleanExtra(AutoFocus, false);
            boolean useFlash = getIntent().getBooleanExtra(UseFlash, false);
            int width = getIntent().getIntExtra(Width_Spec, 1600);
            int height = getIntent().getIntExtra(Height_Spec, 1024);
            createCameraSource(autoFocus, useFlash, width, height);*/
            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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

        try {
            // check that the device has play services available.
            int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                    getActivity());
            if (code != ConnectionResult.SUCCESS) {
                Dialog dlg =
                        GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
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
        } catch (Exception e) {

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
/*        BarcodeGraphic graphic = mGraphicOverlay.getFirstGraphic();
        Barcode barcode = null;
        if (graphic != null) {
            barcode = graphic.getBarcode();
            if (barcode != null) {
                Intent data = new Intent();
                data.putExtra(BarcodeObject, barcode);
                getActivity().setResult(CommonStatusCodes.SUCCESS, data);
                getActivity().finish();
            } else {
                Log.d(TAG, "barcode data is null");
            }
        } else {
            Log.d(TAG, "no barcode detected");
        }
        return barcode != null;*/
        return true;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
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
    public void onDestroyView() {
        super.onDestroyView();
        if (preview != null && preview.isActivated()) {
            preview.stop();
            preview.release();
        }
        unbinder.unbind();
    }

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

    public void captureImage(final String consignmentNumber) {

        final CameraSource.PictureCallback myPictureCallback_RAW = new CameraSource.PictureCallback() {

            @Override
            public void onPictureTaken(final byte[] data) {

                onPause();

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        callPopup(consignmentNumber, data);

                    }
                });
            }

        };

        mCameraSource.takePicture(null, myPictureCallback_RAW);
    }

    public byte[] BitmapToBytesArray(Bitmap bitmap) {
        // Bitmap bmp = Intent intent.getExtras().get("data");
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        bitmap.recycle();
        return byteArray;
    }

    public Bitmap getCroppedImage(View view, byte[] data) {

        Bitmap bitmap;
        bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);

        int dimension = Math.min(view.getHeight(), view.getWidth());
        Bitmap croppedBitmap = ThumbnailUtils.extractThumbnail(bitmap, dimension, dimension);

        // @SuppressLint("NewApi") Bitmap croppedBitmap = Bitmap.createBitmap(bitmap, view.getPaddingStart(), view.getPaddingEnd(), 300,200);
        return croppedBitmap;
    }
}
