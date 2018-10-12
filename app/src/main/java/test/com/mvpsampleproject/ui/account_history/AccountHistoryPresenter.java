package test.com.mvpsampleproject.ui.account_history;

import android.content.Context;
import android.util.Log;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.ui.adapter.AccountHistoryAdapter;
import com.tcs.pickupapp.ui.adapter.AutocompleteCustomArrayAdapter;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;
import com.tcs.pickupapp.util.callback.ServiceError;
import com.tcs.pickupapp.util.callback.ServiceListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.sohail on 5/7/2018.
 */

public class AccountHistoryPresenter implements AccountHistoryMVP.Presenter, AccountHistoryAdapter.IAccountHistoryAdapter {

    private AccountHistoryMVP.View view;
    private AccountHistoryMVP.Model model;
    private AccountHistoryAdapter adapter;
    private SessionManager sessionManager;
    private Utils utils;
    private AutocompleteCustomArrayAdapter autocompleteCustomArrayAdapter;
    private List<CustomerInformation> customerInfoList;

    public AccountHistoryPresenter(AccountHistoryMVP.Model model, SessionManager sessionManager, Utils utils) {
        this.model = model;
        this.sessionManager = sessionManager;
        this.utils = utils;
    }

    @Override
    public void setView(AccountHistoryMVP.View view) {
        this.view = view;
    }

    @Override
    public void onClickBtnGetAccountDetails(String accountNo, String courierCode) {
        if (!utils.isInternetAvailable()) {
            view.showToastLongTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
            return;
        }
        if (validateInputField(accountNo, courierCode)) {
            view.hideSoftKeyboard();
            view.showProgressDialogPleaseWait();
            model.getAccountDetails(accountNo, courierCode, new com.tcs.pickupapp.data.rest.INetwork() {
                @Override
                public void onSuccess(Object response) {
                    final com.tcs.pickupapp.data.rest.response.AccountHistoryResponse accountHistoryResponse = (com.tcs.pickupapp.data.rest.response.AccountHistoryResponse) response;
                    if (accountHistoryResponse.getAccountDetails() == null || accountHistoryResponse.getAccountDetails().size() == 0) {
                        view.clearField();
                        receivedNull("Account Details");
                        view.showTxtNoAccountHistoryFound();
                        return;
                    } else {

                        view.showRecyclerViewReports();
                        setAdapter(accountHistoryResponse.getAccountDetails());

                    }
                }

                @Override
                public void onError(ErrorResponse errorResponse) {
                    view.hideProgressDialogPleaseWait();
                    view.showTxtNoAccountHistoryFound();
                    //  view.showToastLongTime(errorResponse.getMessage());
                }

                @Override
                public void onFailure(Throwable t) {
                    Exception exception = (Exception) t;
                    t.printStackTrace();
                    view.hideProgressDialogPleaseWait();
                    view.showTxtNoAccountHistoryFound();
                    //  view.showToastLongTime(exception.getLocalizedMessage());
                }
            });
        }

    }

    private void setAdapter(List<com.tcs.pickupapp.data.rest.response.AccountDetail> accountHistoryResponses) {
        if (adapter == null) {
            adapter = new AccountHistoryAdapter(accountHistoryResponses, this);
            view.setRecyclerViewAccountHistory(adapter);
            view.hideProgressDialogPleaseWait();
        } else {
            view.hideProgressDialogPleaseWait();
            adapter.addAll(accountHistoryResponses);
        }
    }


    @Override
    public void onItemClick(com.tcs.pickupapp.data.rest.response.AccountDetail generateSequence) {

    }

    @Override
    public void onClickScreen() {
        view.hideSoftKeyboard();

    }


    private boolean validateInputField(String accountNo, String courierCode) {

        if (utils.isTextNullOrEmpty(accountNo)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.account_no));
            return false;
        }

        if (utils.isTextNullOrEmpty(courierCode)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.courier_code_cannot_be_empty));
            return false;
        }


        return true;
    }

    private void receivedNull(String apiJsonNodeName) {
        view.hideProgressDialogPleaseWait();
        view.showToastLongTime(apiJsonNodeName + " is null");
    }


    @Override
    public void filterCustomerAccount(final Context context, final CharSequence s) {
        /*model.getCustomersInfo(new ServiceListener<List<CustomerInformation>>() {
            @Override
            public void onSuccess(List<CustomerInformation> customerInfoList) {
                if (customerInfoList.size() == 0) {
                    setupAdapter(context, new ArrayList<CustomerInformation>());
                    return;
                }*/
        if (customerInfoList != null && customerInfoList.size() != 0) {
            List<CustomerInformation> filteredList = filter(customerInfoList, s);
            if (filteredList.size() != 0) {
                setupAdapter(context, filteredList);
            }
        }

            /*}

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });*/
    }

    public List<CustomerInformation> filter(List<CustomerInformation> backupCusInfo, CharSequence c) {
        int length = c.length();
        List<CustomerInformation> customerInformationList = new ArrayList<>();

        if (length != 0) {
            for (int i = 0; i < backupCusInfo.size(); i++) {
                if (backupCusInfo.get(i).getCustomerNumber().toLowerCase().contains(c.toString().toLowerCase())) {
                    customerInformationList.add(backupCusInfo.get(i));
                }
            }
        } /*else {
            customerInformationList.addAll(backupCusInfo);
        }*/
        return customerInformationList;
    }

    private void setupAdapter(Context context, List<CustomerInformation> customerInfoList) {
        autocompleteCustomArrayAdapter = new AutocompleteCustomArrayAdapter(context, R.layout.autocomplete_listitem, customerInfoList);
        view.setAutoCompleteAdapter(autocompleteCustomArrayAdapter);

    }


    @Override
    public void fetchCustomers(final Context context) {
        model.getCustomersInfo(new ServiceListener<List<CustomerInformation>>() {
            @Override
            public void onSuccess(List<CustomerInformation> cusInfoList) {
                if (cusInfoList.size() == 0) {
                    setupAdapter(context, new ArrayList<CustomerInformation>());
//                    setupAdapter(context, customerInfoList);
                    return;
                }
                Log.d("customer", "-customer's list fatched");
                customerInfoList = cusInfoList;
                setupAdapter(context, cusInfoList);
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }


}
