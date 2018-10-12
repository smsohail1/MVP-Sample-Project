package test.com.mvpsampleproject.ui.account_history;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;
import com.tcs.pickupapp.ui.adapter.AccountHistoryAdapter;
import com.tcs.pickupapp.ui.adapter.AutocompleteCustomArrayAdapter;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;
import com.tcs.pickupapp.util.CustomAutoCompleteView;
import com.tcs.pickupapp.util.ProgressCustomDialogController;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by muhammad.sohail on 5/7/2018.
 */

public class AccountHistoryFragment extends Fragment implements AccountHistoryMVP.View, View.OnClickListener, View.OnFocusChangeListener {

    @BindView(R.id.historyButton)
    protected Button historyButton;
    @BindView(R.id.editAccountNumber)
    protected CustomAutoCompleteView editAccountNumber;
    @BindView(R.id.relativeParent)
    protected RelativeLayout relativeParent;
    @BindView(R.id.recyclerViewCNSequenceList)
    protected RecyclerView recyclerViewCNSequenceList;
    @BindView(R.id.txtNoAccountDetailsAvailable)
    protected TextView txtNoAccountDetailsAvailable;
    @BindView(R.id.labelLayout)
    protected LinearLayout labelLayout;


    @Inject
    protected AccountHistoryMVP.Presenter presenter;
    @Inject
    Utils utils;
    @Inject
    protected ToastUtil toastUtil;
    @Inject
    protected SessionManager sessionManager;

    private CustomerInformation customerInfo;

    private ProgressCustomDialogController progressDialogControllerPleaseWait;


    public AccountHistoryFragment() {

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
        View v = inflater.inflate(R.layout.fragment_account_history, container, false);
        initializeViews(v);
        setTitle();
        setForcusListeners();

        return v;
    }

    public void setTitle() {
        ((BaseActivity) getActivity()).
                setTitle(getString(R.string.account_history));
    }

    private void setForcusListeners() {
        editAccountNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editAccountNumber, hasFocus);
            }
        });
    }

    private void initializeViews(View v) {
        ButterKnife.bind(this, v);

        recyclerViewCNSequenceList.setLayoutManager(new LinearLayoutManager(getActivity()));
        historyButton.setOnClickListener(this);

        progressDialogControllerPleaseWait = new ProgressCustomDialogController(getActivity(), R.string.please_wait);
        changeEditBackground(editAccountNumber, true);

        presenter.fetchCustomers(getActivity());

        editAccountNumber.setThreshold(1);
        // add the listener so it will tries to suggest while the user types
        editAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                presenter.filterCustomerAccount(getActivity(), s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        editAccountNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                customerInfo = (CustomerInformation) adapterView.getItemAtPosition(position);
                if (customerInfo.getCustomerNumber() != null) {
                    editAccountNumber.setText(customerInfo.getCustomerNumber());
                    txtNoAccountDetailsAvailable.setVisibility(View.GONE);
                }

            }
        });
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
        utils.hideSoftKeyboard(editAccountNumber);
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
    public void setRecyclerViewAccountHistory(AccountHistoryAdapter accountHistoryAdapter) {
        recyclerViewCNSequenceList.setAdapter(accountHistoryAdapter);
    }

    @Override
    public void clearField() {
        editAccountNumber.setText("");

    }

    @Override
    public void showRecyclerViewReports() {
        recyclerViewCNSequenceList.setVisibility(View.VISIBLE);
        txtNoAccountDetailsAvailable.setVisibility(View.GONE);
        labelLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void showTxtNoAccountHistoryFound() {
        txtNoAccountDetailsAvailable.setVisibility(View.VISIBLE);
        labelLayout.setVisibility(View.GONE);
        recyclerViewCNSequenceList.setVisibility(View.GONE);
        utils.playErrorToneAndVibrate(getActivity());
        editAccountNumber.requestFocus();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.historyButton:
                String customerNumber = editAccountNumber.getText().toString().trim();
                if (utils.isTextNullOrEmpty(customerNumber)) {
                    editAccountNumber.setError("Please enter account number");
                    utils.playErrorToneAndVibrate(getActivity());
                    editAccountNumber.requestFocus();
                    return;
                }
               /* else if (customerInfo == null) {
                    editAccountNumber.setError("Please enter valid account no");
                    utils.playErrorToneAndVibrate(getActivity());
                    editAccountNumber.requestFocus();
                    return;
                }*/

                presenter.onClickBtnGetAccountDetails(customerNumber, sessionManager.getCourierCode());
                break;

            case R.id.relativeParent:
                presenter.onClickScreen();
                break;


        }
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
    public void setAutoCompleteAdapter(AutocompleteCustomArrayAdapter autoCompleteCustomArrayAdapter) {
        editAccountNumber.setAdapter(autoCompleteCustomArrayAdapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        utils.hideSoftKeyboard(editAccountNumber);
    }
}
