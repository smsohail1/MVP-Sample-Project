package test.com.mvpsampleproject.ui.account_history;

import android.content.Context;

import com.tcs.pickupapp.ui.adapter.AccountHistoryAdapter;
import com.tcs.pickupapp.ui.adapter.AutocompleteCustomArrayAdapter;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;
import com.tcs.pickupapp.util.callback.ServiceListener;

import java.util.List;

/**
 * Created by muhammad.sohail on 5/7/2018.
 */

public interface AccountHistoryMVP {

    interface View {
        void showToastShortTime(String message);

        void showToastLongTime(String message);

        void hideSoftKeyboard();

        void showProgressDialogPleaseWait();

        void hideProgressDialogPleaseWait();

        void setAutoCompleteAdapter(AutocompleteCustomArrayAdapter autocompleteCustomArrayAdapter);

        void showRecyclerViewReports();

        void showTxtNoAccountHistoryFound();

        void setRecyclerViewAccountHistory(AccountHistoryAdapter accountHistoryAdapter);


        void clearField();

    }

    interface Presenter {
        void setView(AccountHistoryMVP.View view);

        void onClickBtnGetAccountDetails(String accountNo, String courierCode);

        void onClickScreen();

        void filterCustomerAccount(Context context, CharSequence s);

        void fetchCustomers(Context context);


    }

    interface Model {
        void getAccountDetails(String accountNo, String courierCode, com.tcs.pickupapp.data.rest.INetwork iNetwork);

        void getCustomersInfo(ServiceListener<List<CustomerInformation>> mListener);


    }
}
