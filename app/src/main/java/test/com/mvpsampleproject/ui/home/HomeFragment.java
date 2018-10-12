package test.com.mvpsampleproject.ui.home;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;
import com.tcs.pickupapp.ui.adapter.HomeAdapter;
import com.tcs.pickupapp.ui.booking.BookingFragment;
import com.tcs.pickupapp.ui.dpuser.DpUserFragment;
import com.tcs.pickupapp.ui.generatecnsequence.GenerateCNSequenceFragment;
import com.tcs.pickupapp.ui.report.ReportFragment;
import com.tcs.pickupapp.ui.route_navigator.RouteNavigatorFragment;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.GridSpacingItemDecoration;
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
public class HomeFragment extends Fragment implements HomeMVP.View {

    @BindView(R.id.recyclerViewHome)
    protected RecyclerView recyclerViewHome;

    @Inject
    protected HomeMVP.Presenter presenter;
    @Inject
    protected Utils utils;
    @Inject
    protected ToastUtil toastUtil;
    @Inject
    protected SessionManager sessionManager;

    private Dialog dialog;

    private ProgressCustomDialogController progressDialogControllerPleaseWait;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setView(this);
        try {
            setTitle();
            turnGPSOn();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        initializeViews(v);
        utils.startBookingService(AppConstants.FLAG_SYNC, getActivity());

        return v;
    }

    public void setTitle() {
        ((BaseActivity) getActivity()).setTitle(getString(R.string.home));
    }

    public void initializeViews(View v) {
        ButterKnife.bind(this, v);
        presenter.setView(this);

        progressDialogControllerPleaseWait = new ProgressCustomDialogController(getActivity(), R.string.please_wait);

        recyclerViewHome.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerViewHome.addItemDecoration(new GridSpacingItemDecoration(2, 20, true));

        presenter.setHomeItems();
    }

    @Override
    public void setHomeRecyclerViewAdapter(HomeAdapter homeAdapter) {
        recyclerViewHome.setAdapter(homeAdapter);
    }

    @Override
    public void openBookingActivity() {
        Log.i("BatLevel ", utils.getBatteryLevel() + "");
        if (!(getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof BookingFragment)) {
            try {
                if (!utils.isTextNullOrEmpty(sessionManager.getKeyLowBatteryPercentage())) {
                    // if (!sessionManager.getKeyLowBatteryPercentage().equalsIgnoreCase("")) {
                    AppConstants.BATTERY_LEVEL = Integer.parseInt(sessionManager.getKeyLowBatteryPercentage());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            if (utils.getBatteryLevel() <= AppConstants.BATTERY_LEVEL) {
                // createBatteryDialog(utils.getBatteryLevel());
                showAlertDialog(utils.getBatteryLevel());
            } else {
                ((BaseActivity) getActivity()).addFragment(new BookingFragment().newInstance(0));
            }
        }

    }

    private void createBatteryDialog(int batLevel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Battery Level is " + batLevel + "%, booking is not allowed.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void showAlertDialog(int batLevel) {
        final Dialog dialog;
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.low_battery_dialog);

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        txtMessage.setText("Battery Level is " + batLevel + "%, booking is not allowed.");
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        Button submit = dialog.findViewById(R.id.submit);
        txtTitle.setText("Alert");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void openReportsActivity() {
        try {
            ((BaseActivity) getActivity()).addFragment(new ReportFragment());
            //((BaseActivity) getActivity()).addFragment(new RetakeReportFragment());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void openRetakeActivity() {
        if (!(getActivity().getSupportFragmentManager().findFragmentById(R.id.fragmentContainer) instanceof BookingFragment)) {
            try {

                AppConstants.BATTERY_LEVEL = Integer.parseInt(sessionManager.getKeyLowBatteryPercentage());

                if (utils.getBatteryLevel() <= AppConstants.BATTERY_LEVEL) {
                    //createBatteryDialog(utils.getBatteryLevel());
                    showAlertDialog(utils.getBatteryLevel());
                } else {
                    ((BaseActivity) getActivity()).addFragment(new BookingFragment().newInstance(1));
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void openGenerateCNActivity() {
        ((BaseActivity) getActivity()).addFragment(new GenerateCNSequenceFragment());
    }

    @Override
    public void openRouteNavigatorActivity() {
        ((BaseActivity) getActivity()).addFragment(new RouteNavigatorFragment());
    }

    @Override
    public void syncData() {
        utils.startBookingService(AppConstants.FLAG_RESET, getActivity());
    }

    @Override
    public void showToastShortTime(String message) {
        if (message == null) {
            return;
        }
        if (message.equals("")) {
            return;
        }
        toastUtil.showToastShortTime(message);
    }

    @Override
    public void showToastLongTime(String message) {
        if (message == null) {
            return;
        }
        if (message.equals("")) {
            return;
        }
        toastUtil.showToastLongTime(message);
    }

    @Override
    public String getDeviceIMEI() {
        return utils.getDeviceImeiNumber(getActivity());
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
    public void showDPUserDialog() {
        showDPUserLoginDialog();
    }

    private void showDPUserLoginDialog() {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_dp_user_login);

        final EditText edtUsername = dialog.findViewById(R.id.edtUsername);
        final EditText edtPassword = dialog.findViewById(R.id.edtPassword);
        Button btnLogin = dialog.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*if (utils.isEditTextNullOrEmpty(edtUsername)) {
                    toastUtil.showToastLongTime(R.string.username_cannot_be_empty);
                    return;
                }*/
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

                if (/*(edtUsername.getText().toString().equals(AppConstants.DP_USER_USERNAME)) &&*/
                        (edtPassword.getText().toString().equals(AppConstants.DP_USER_PASSWORD))) {
                    //Ddismiss dialog
                    dialog.dismiss();

                    // open DP USER activity here
                    ((BaseActivity) getActivity()).addFragment(new DpUserFragment());

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

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    private void turnGPSOn() {
        try {
            final LocationManager manager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } else {
                /*LocationRequest mLocationRequest;
                mLocationRequest = LocationRequest.create();
                mLocationRequest.setInterval(7000);
                mLocationRequest.setFastestInterval(2000);
                mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
                builder.addLocationRequest(mLocationRequest);
                LocationSettingsRequest mLocationSettingsRequest = builder.build();
                LocationServices.getSettingsClient(getActivity().getApplicationContext()).checkLocationSettings(mLocationSettingsRequest).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(),"Please turn on location from Settings",Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                        getActivity().finish();
                    }
                });
                LocationServices.getSettingsClient(getActivity().getApplicationContext()).checkLocationSettings(mLocationSettingsRequest).addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        utils.startGPSTrackerService(getActivity().getApplicationContext());
                    }
                });*/
                utils.startGPSTrackerService(getActivity().getApplicationContext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isLocationEnabled(Context context) {
        int locationMode = 0;
        String locationProviders;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            try {
                locationMode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE);
            } catch (Settings.SettingNotFoundException e) {
                e.printStackTrace();
                return false;
            }

            return locationMode != Settings.Secure.LOCATION_MODE_OFF;

        }else{
            locationProviders = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
            return !TextUtils.isEmpty(locationProviders);
        }


    }
}















