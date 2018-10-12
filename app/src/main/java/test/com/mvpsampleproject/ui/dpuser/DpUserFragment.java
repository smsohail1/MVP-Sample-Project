package test.com.mvpsampleproject.ui.dpuser;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;
import com.tcs.pickupapp.ui.login.LoginActivity;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.ProgressCustomDialogController;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by muhammad.sohail on 4/5/2018.
 */

public class DpUserFragment extends Fragment implements DpUserMVP.View, View.OnClickListener {


    @BindView(R.id.clearData)
    protected LinearLayout clearData;
    @BindView(R.id.buildFile)
    protected LinearLayout buildFile;
    @BindView(R.id.buildNTFile)
    protected LinearLayout buildNTFile;
    @BindView(R.id.linearLocalOMS)
    protected LinearLayout linearLocalOMS;
    @BindView(R.id.txtWifiSettings)
    protected TextView txtWifiSettings;

    @Inject
    protected DpUserMVP.Presenter presenter;
    @Inject
    Utils utils;
    @Inject
    protected ToastUtil toastUtil;

    @Inject
    protected SessionManager sessionManager;

    private Dialog dialog;
    private ProgressCustomDialogController progressDialogControllerPleaseWait;

    public DpUserFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
//        try {
//            ((BaseActivity)getActivity()).setTitle(getString(R.string.dp_user));
//        }catch (Exception ex){
//            ex.printStackTrace();
//        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setView(this);
        //utils.enableWifiAndConnectToInternet();
    }

    @Override
    public void onStop() {
        super.onStop();
        //utils.disableWifi();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_dp_user, container, false);
        setTitle();
        initializeViews(v);
        requestPermissions();

        return v;
    }

    public void setTitle() {
        ((BaseActivity) getActivity()).
                setTitle(getString(R.string.dp_user));
    }

    private void initializeViews(View v) {
        ButterKnife.bind(this, v);
        clearData.setOnClickListener(this);
        linearLocalOMS.setOnClickListener(this);
        buildFile.setOnClickListener(this);
        buildNTFile.setOnClickListener(this);
        txtWifiSettings.setOnClickListener(this);

        progressDialogControllerPleaseWait = new ProgressCustomDialogController(getActivity(), R.string.please_wait);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.clearData: {
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                } else {
                    presenter.onClickBtnClearData(getActivity());
                }
                break;
            }
            case R.id.linearLocalOMS: {
                presenter.onClickLocalOMS();
                break;
            }
            case R.id.txtWifiSettings: {
                showWifiScanDialog();
                break;
            }
            case R.id.buildFile:
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    presenter.onClickBtnCallBuildFile();
                }
                break;

            case R.id.buildNTFile:
                if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                } else {
                    presenter.onClickBtnCallBuildNTFile();
                }
                break;
        }
    }

    @Override
    public void showToastShortTime(String message) {
        toastUtil.showToastShortTime(message);

    }

    @Override
    public void showToastLongTime(String message) {
        toastUtil.showToastLongTime(message);
    }

    @Override
    public void showLoginScreen() {
        utils.stopGPSTrackerService(getActivity().getApplicationContext());
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        getActivity().startActivity(intent);
        getActivity().finish();
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
    public void refreshGallery(String filePath) {
        try {
            MediaScannerConnection
                    .scanFile(
                            getActivity(),
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
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }


    private void requestPermissions() {
        if (ActivityCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
    }


    View viewCursorScanWifi;

    private void showWifiScanDialog() {
        utils.enableWifiAndConnectToInternet();
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_scan_wifi);
        //dialog.setCancelable(false);

        viewCursorScanWifi = dialog.findViewById(R.id.viewCursorScanWifi);
        final EditText edtWifiBarcode = dialog.findViewById(R.id.edtWifiBarcode);
        edtWifiBarcode.setInputType(InputType.TYPE_NULL);
        edtWifiBarcode.setTransformationMethod(PasswordTransformationMethod.getInstance());
        edtWifiBarcode.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (countDownTimerForScanWifi == null) {
                    startCursorBlinkingAnimationScanWifi();
                }
                utils.hideSoftKeyboard(edtWifiBarcode);
            }
        });
        startCursorBlinkingAnimationScanWifi();
        edtWifiBarcode.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if (keyEvent != null) {
                    if (keyCode == EditorInfo.IME_ACTION_DONE || keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        try {
                            String wifiBarcode = edtWifiBarcode.getText().toString();
                            if (utils.isTextNullOrEmpty(wifiBarcode)) {
                                showToastLongTime("Invalid Barcode, Scan Again...");
                                edtWifiBarcode.setText("");
                                return false;
                            }
                            String[] split1 = wifiBarcode.split("____");
                            String ssid = split1[0];
                            String password = split1[1];
                            if (ssid.equals("0")) {
                                showToastLongTime("Invalid Barcode, Scan Again...");
                                edtWifiBarcode.setText("");
                                return false;
                            }
                            if (password.equals("0")) {
                                showToastLongTime("Invalid Barcode, Scan Again...");
                                edtWifiBarcode.setText("");
                                return false;
                            }
                            connectToSSID(ssid, password);
                            dialog.dismiss();
                            return true;
                        } catch (Exception ex) {
                            showToastLongTime("Invalid Barcode, Scan Again...");
                            edtWifiBarcode.setText("");
                            ex.printStackTrace();
                            return false;
                        }
                    }
                }
                return false;
            }
        });

        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                try {
                    stopCursorBlinkingAnimationScanWifi();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void connectToSSID(String ssid, String password) {
        try {
            WifiManager wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            WifiConfiguration wificonfiguration = new WifiConfiguration();
            StringBuffer stringbuffer = new StringBuffer("\"");
            stringbuffer.append((new StringBuilder(String.valueOf(ssid))).append("\"").toString());
            wificonfiguration.SSID = stringbuffer.toString();
            wificonfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            wificonfiguration.allowedAuthAlgorithms.set(0);
            wificonfiguration.status = 2;
            wificonfiguration.preSharedKey = "\"" + password + "\"";
            int networkId = wifiManager.addNetwork(wificonfiguration);
            if (networkId > -1) {
                boolean status = wifiManager.enableNetwork(networkId, true);
                progressDialogControllerPleaseWait.showDialog();
                startTimer(AppConstants.WIFI_PASSWORD_CONNECT_TIMEOUT);
            } else {
                showToastLongTime("Failed to connect to " + String.valueOf(ssid));
            }
        } catch (Exception ex) {
            showToastLongTime(ex.getMessage());
            progressDialogControllerPleaseWait.hideDialog();
            ex.printStackTrace();
        }
    }

    private Timer timer;
    private TimerTask timerTask;

    public void startTimer(int duration) {
        timer = new Timer();
        initializeTimerTask();
        timer.schedule(timerTask, duration);
    }

    public void initializeTimerTask() {
        try {
            timerTask = new TimerTask() {
                public void run() {
                    /*if(utils.isWifiInternetAvailable()){
                        showToastLongTime("Connected");
                    }else {
                        showToastLongTime("Failed to connect");
                    }*/
                    stoptimertask();
                }
            };
        } catch (Exception ex) {
            showToastLongTime(ex.getMessage());
            progressDialogControllerPleaseWait.hideDialog();
            ex.printStackTrace();
        }
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }

        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                progressDialogControllerPleaseWait.hideDialog();
                if (utils.isWifiInternetAvailable()) {
                    showToastLongTime("Connected");
                } else {
                    showToastLongTime("Failed to connect");
                }
            }
        });
    }

    private CountDownTimer countDownTimerForScanWifi;

    private void startCursorBlinkingAnimationScanWifi() {
        try {
            countDownTimerForScanWifi = new CountDownTimer(300000, 500) {
                public void onTick(long millisUntilFinished) {
                    if (viewCursorScanWifi.getVisibility() == View.VISIBLE) {
                        viewCursorScanWifi.setVisibility(View.GONE);
                    } else {
                        viewCursorScanWifi.setVisibility(View.VISIBLE);
                    }
                }

                public void onFinish() {
                    try {
                        if (countDownTimerForScanWifi != null) {
                            countDownTimerForScanWifi.cancel();
                            countDownTimerForScanWifi = null;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            };
            countDownTimerForScanWifi.start();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void stopCursorBlinkingAnimationScanWifi() {
        if (viewCursorScanWifi != null) {
            viewCursorScanWifi.setVisibility(View.GONE);
        }
        if (countDownTimerForScanWifi != null) {
            countDownTimerForScanWifi.cancel();
            countDownTimerForScanWifi = null;
        }
    }
}









