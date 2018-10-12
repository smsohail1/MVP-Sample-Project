package test.com.mvpsampleproject.ui.setting;

/**
 * Created by muhammad.sohail on 5/7/2018.
 */


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SettingFragment extends Fragment implements SettingMVP.View, View.OnClickListener {


    @BindView(R.id.edtMinimumBatteryPercentage)
    protected EditText edtMinimumBatteryPercentage;

    @BindView(R.id.edtMinimumCameraAndBarcodePercentage)
    protected EditText edtMinimumCameraAndBarcodePercentage;


    @BindView(R.id.btnOkay)
    protected Button btnOkay;

    @BindView(R.id.layoutRelative)
    protected RelativeLayout layoutRelative;

    @Inject
    protected SettingMVP.Presenter presenter;

    @Inject
    Utils utils;

    @Inject
    protected ToastUtil toastUtil;

    @Inject
    protected SessionManager sessionManager;

    public SettingFragment() {
        // Required empty public constructor
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
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        initializeViews(view);


        return view;
    }

    private void initializeViews(View v) {
        ButterKnife.bind(this, v);
        setTitle();
        presenter.setView(this);

        btnOkay.setOnClickListener(this);
        layoutRelative.setOnClickListener(this);
        try {
            edtMinimumBatteryPercentage.setText(sessionManager.getKeyLowBatteryPercentage());
            edtMinimumCameraAndBarcodePercentage.setText(sessionManager.getMinimumCameraBarcodePercent());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void setTitle() {
        ((BaseActivity) getActivity()).setTitle(getString(R.string.action_settings));
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
    public void hideSoftKeyboard() {
        utils.hideSoftKeyboard(edtMinimumBatteryPercentage);
        utils.hideSoftKeyboard(edtMinimumCameraAndBarcodePercentage);
    }

    @Override
    public void clearField() {
        edtMinimumBatteryPercentage.setText("");
        edtMinimumCameraAndBarcodePercentage.setText("");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnOkay:
                presenter.okayBtnClick(edtMinimumBatteryPercentage.getText().toString(), edtMinimumCameraAndBarcodePercentage.getText().toString());
                break;

            case R.id.layoutRelative:
                presenter.onClickScreen();
                break;

        }
    }
}
