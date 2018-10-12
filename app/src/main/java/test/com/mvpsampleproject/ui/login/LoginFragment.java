package test.com.mvpsampleproject.ui.login;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.home.HomeActivity;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.ProgressCustomDialogController;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements LoginMVP.View, View.OnClickListener {

    @BindView(R.id.edtCourierCode)
    protected EditText edtCourierCode;
    @BindView(R.id.edtPassword)
    protected EditText edtPassword;
    @BindView(R.id.btnSignIn)
    protected Button btnSignIn;
    @BindView(R.id.btnCallMobilink)
    protected Button btnCallMobilink;
    @BindView(R.id.RelativeLayout)
    protected RelativeLayout relativeLayout;

    @Inject
    protected LoginMVP.Presenter presenter;
    @Inject
    Utils utils;
    @Inject
    protected ToastUtil toastUtil;

    @Inject
    protected SessionManager sessionManager;

    private ProgressCustomDialogController progressDialogControllerPleaseWait;

    private String macAddress = "";

    public LoginFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);

        if (sessionManager.isLoggedIn()) {
            showHomeScreen();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setView(this);

        if (mPermissionDenied) {
            // Permissions were not granted
            showMissingPermissionError();
            mPermissionDenied = false;
        }
        checkPlayServices();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        initializeViews(v);
        setForcusListeners();

        utils.setupParent(getActivity(), v);
        requestPermissionsFromUser();

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        utils.hideSoftKeyboard(edtCourierCode);
    }

    private void initializeViews(View v) {
        ButterKnife.bind(this, v);
        presenter.setView(this);
        btnSignIn.setOnClickListener(this);
        btnCallMobilink.setOnClickListener(this);
        relativeLayout.setOnClickListener(this);

        edtCourierCode.requestFocus();

        // setupCustomToast();
        progressDialogControllerPleaseWait = new ProgressCustomDialogController(getActivity(), R.string.please_wait);

        utils.showSoftKeyboard(edtCourierCode);

    }


    private void setForcusListeners() {
        edtCourierCode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(edtCourierCode, hasFocus);
            }
        });

        edtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(edtPassword, hasFocus);
            }
        });

    }

    private void changeEditBackground(EditText edit, boolean hasFocus) {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (hasFocus) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                edit.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_login_red_bg));
            } else {
                edit.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_login_red_bg));
            }
        } else {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                edit.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_login_bg));
            } else {
                edit.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_login_bg));
            }
        }
    }

    private String getDeviceMacAddress() {
        WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wInfo = wifiManager.getConnectionInfo();
        macAddress = wInfo.getMacAddress();
        return macAddress;
    }


    @Override
    public void showToastShortTime(String message) {
        if (message == null) {
            return;
        }
        if (message.equals("")) {
            return;
        }
        if(message.contains("no address")){
            toastUtil.showToastShortTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
        }else if(message.toLowerCase().contains("failed")){
            toastUtil.showToastShortTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
        }else {
            toastUtil.showToastShortTime(message);
        }
    }

    @Override
    public void showToastLongTime(String message) {
        if (message == null) {
            return;
        }
        if (message.equals("")) {
            return;
        }
        if(message.contains("no address")){
            toastUtil.showToastShortTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
        }else if(message.toLowerCase().contains("failed")){
            toastUtil.showToastShortTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
        }else {
            toastUtil.showToastShortTime(message);
        }
    }

    @Override
    public void hideSoftKeyboard() {
        utils.hideSoftKeyboard(edtCourierCode);
        utils.hideSoftKeyboard(edtPassword);

    }

    @Override
    public void showProgressDialogPleaseWait() {
        progressDialogControllerPleaseWait.showDialog();
    }

    @Override
    public void hideProgressDialogPleaseWait() {
        progressDialogControllerPleaseWait.hideDialog();
    }


    @Override
    public void showHomeScreen() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void refreshGallery(String[] filePaths) {
        try {
            MediaScannerConnection
                    .scanFile(
                            getActivity(),
                            filePaths,
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
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void showLoginScreen() {
        utils.stopGPSTrackerService(getActivity().getApplicationContext());
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
    }

    @Override
    public void showClearDataPopup() {
        showClearDataDialog();
    }

    @Override
    public void showDataNotSyncedPopup() {
        showDataNotSyncedDialog();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnSignIn: {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                        || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_PHONE_STATE}, 101);
                } else {
                    presenter.checkNTBookings(edtCourierCode.getText().toString(),
                            edtPassword.getText().toString(),
                            /*utils.getDeviceImeiNumber(getActivity())*/AppConstants.IMEI_NUMBER_OF_1002_COURIER);
                }
                break;
            }
            case R.id.btnCallMobilink:
                presenter.onClickBtnCallMobilink();
                break;
            case R.id.RelativeLayout:
                presenter.onClickScreen();
                break;
        }

    }

    private Dialog dialog;
    private void showClearDataDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_clear_data);
        //dialog.setCancelable(false);

        Button btnOK = dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onClickBtnOKClearData();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showDataNotSyncedDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_data_not_sync);

        final EditText edtPassword = dialog.findViewById(R.id.edtPassword);
        Button btnLogin = dialog.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (utils.isEditTextNullOrEmpty(edtPassword)) {
                    toastUtil.showToastLongTime(R.string.password_cannot_be_empty);
                    utils.playErrorToneAndVibrate(getActivity());
                    return;
                }
                if (utils.minCharactersLimit(edtPassword.getText().toString(), 3)) {
                    toastUtil.showToastLongTime(R.string.password_min_charater_limit);
                    utils.playErrorToneAndVibrate(getActivity());
                    return;
                }

                if ((edtPassword.getText().toString().equals(AppConstants.DP_USER_PASSWORD))) {
                    showHomeScreen();
                } else {
                    toastUtil.showToastLongTime(R.string.invalid_credentials);
                    utils.playErrorToneAndVibrate(getActivity());
                    return;
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    /*private void requestPermissions() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.READ_PHONE_STATE}, 101);
        }
    }*/

    // PERMISSION CODE
    final private int REQUEST_CODE_ASK_PERMISSIONS = 125;
    private boolean mPermissionDenied = false;
    private void requestPermissionsFromUser() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        int hasReadPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
        int hasWritePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int hasReadPhoneStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
        int hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

        if ((hasCameraPermission != PackageManager.PERMISSION_GRANTED) ||
                (hasReadPermission != PackageManager.PERMISSION_GRANTED) ||
                (hasWritePermission != PackageManager.PERMISSION_GRANTED) ||
                (hasReadPhoneStatePermission != PackageManager.PERMISSION_GRANTED) ||
                (hasAccessFineLocationPermission != PackageManager.PERMISSION_GRANTED) ||
                (hasCallPhonePermission != PackageManager.PERMISSION_GRANTED)
                ) {
            requestPermissions(
                    new String[]{
                            Manifest.permission.CAMERA,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_PHONE_STATE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.CALL_PHONE
                    },
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        } else {
            //permissionsGranted();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if ((grantResults[0] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults[1] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults[2] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults[3] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults[4] == PackageManager.PERMISSION_GRANTED) &&
                        (grantResults[5] == PackageManager.PERMISSION_GRANTED)
                        ) {
                    // Permission Allowed
                    int hasCameraPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
                    int hasReadPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);
                    int hasWritePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int hasReadPhoneStatePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_PHONE_STATE);
                    int hasAccessFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
                    int hasCallPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE);

                    if ((hasCameraPermission == PackageManager.PERMISSION_GRANTED) &&
                            (hasReadPermission == PackageManager.PERMISSION_GRANTED) &&
                            (hasWritePermission == PackageManager.PERMISSION_GRANTED) &&
                            (hasReadPhoneStatePermission == PackageManager.PERMISSION_GRANTED) &&
                            (hasAccessFineLocationPermission == PackageManager.PERMISSION_GRANTED) &&
                            (hasCallPhonePermission == PackageManager.PERMISSION_GRANTED)
                            ) {
                        //permissionsGranted();
                    }
                } else {
                    // Permission Denied
                    mPermissionDenied = true;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        toastUtil.showToastShortTime(R.string.these_permissions_are_required);
        getActivity().finish();
    }

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(getActivity(), resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                toastUtil.showToastLongTime(utils.getStringFromResourceId(R.string.update_play_services));
                getActivity().finish();
            }
            return false;
        }
        return true;
    }


    /*public void settingsRequest() {
        LocationRequest mLocationRequest;
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(7000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        SettingsClient client = LocationServices.getSettingsClient(getActivity());
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                // ...
                String str = "";
            }
        });

        task.addOnFailureListener(getActivity(), new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(getActivity(),
                                1234);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }*/












}















