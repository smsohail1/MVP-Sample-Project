package test.com.mvpsampleproject.ui.consigment_no_details;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;
import com.tcs.pickupapp.util.ProgressCustomDialogController;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by muhammad.sohail on 5/17/2018.
 */

public class ConsignmentNoDetailsFragment extends Fragment implements ConsignmentNoDetailsMVP.View, View.OnClickListener, View.OnFocusChangeListener {

    @BindView(R.id.consignmentButton)
    protected Button consignmentButton;
    @BindView(R.id.editTextConsignmentNo)
    protected EditText editTextConsignmentNo;
    @BindView(R.id.txtNoConsignmentDetailsAvailable)
    protected TextView txtNoConsignmentDetailsAvailable;
    @BindView(R.id.detailsLayout)
    protected LinearLayout detailsLayout;
    @BindView(R.id.txtCustomerNo)
    protected TextView txtCustomerNo;
    @BindView(R.id.txtProduct)
    protected TextView txtProduct;
    @BindView(R.id.txtService)
    protected TextView txtService;


    @Inject
    protected ConsignmentNoDetailsMVP.Presenter presenter;
    @Inject
    Utils utils;
    @Inject
    protected ToastUtil toastUtil;
    @Inject
    protected SessionManager sessionManager;

    private ProgressCustomDialogController progressDialogControllerPleaseWait;


    public ConsignmentNoDetailsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);

    }


    @Override
    public void onResume() {
        super.onResume();
        presenter.setView(this);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_consignment_details, container, false);
        initializeViews(v);
        setTitle();
        setForcusListeners();

        return v;
    }


    private void initializeViews(View v) {
        ButterKnife.bind(this, v);
        consignmentButton.setOnClickListener(this);

        editTextConsignmentNo.requestFocus();

        progressDialogControllerPleaseWait = new ProgressCustomDialogController(getActivity(), R.string.please_wait);


        utils.showSoftKeyboard(editTextConsignmentNo);
    }

    public void setTitle() {
        ((BaseActivity) getActivity()).
                setTitle(getString(R.string.cn_details));
    }

    private void setForcusListeners() {
        editTextConsignmentNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editTextConsignmentNo, hasFocus);
            }
        });
    }


    private void changeEditBackground(EditText edit, boolean hasFocus) {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (hasFocus) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                edit.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_red_bg));
            } else {
                edit.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_red_bg));
            }
        } else {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                edit.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_bg));
            } else {
                edit.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_bg));
            }
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.consignmentButton:
                String consignmentNumber = editTextConsignmentNo.getText().toString().trim();
                if (utils.isTextNullOrEmpty(consignmentNumber)) {
                    editTextConsignmentNo.setError("Please enter consignment number");
                    utils.playErrorToneAndVibrate(getActivity());
                    editTextConsignmentNo.requestFocus();
                    return;
                }

                presenter.onClickBtnGetConsignmentDetails(consignmentNumber, sessionManager.getCourierCode());
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
    public void hideSoftKeyboard() {
        utils.hideSoftKeyboard(editTextConsignmentNo);
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
    public void showTxtConsignmentNotFound() {
        txtNoConsignmentDetailsAvailable.setVisibility(View.VISIBLE);
        detailsLayout.setVisibility(View.GONE);
        utils.playErrorToneAndVibrate(getActivity());
        txtCustomerNo.setText("");
        txtProduct.setText("");
        txtService.setText("");
        editTextConsignmentNo.requestFocus();
    }

    @Override
    public void showTxtConsignmentFound(com.tcs.pickupapp.data.rest.response.ConsignmentDetails consignmentDetails) {
        txtNoConsignmentDetailsAvailable.setVisibility(View.GONE);
        detailsLayout.setVisibility(View.VISIBLE);
        txtCustomerNo.setText(consignmentDetails.getCustomerNumber());
        txtProduct.setText(consignmentDetails.getProduct());
        txtService.setText(consignmentDetails.getServiceName());
    }


    @Override
    public void clearField() {
        editTextConsignmentNo.setText("");

    }


    @Override
    public void onStop() {
        super.onStop();
        utils.hideSoftKeyboard(editTextConsignmentNo);
    }
}
