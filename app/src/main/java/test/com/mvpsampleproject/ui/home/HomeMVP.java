package test.com.mvpsampleproject.ui.home;

import com.tcs.pickupapp.data.rest.response.HandlingInstructions;
import com.tcs.pickupapp.data.rest.response.Services;
import com.tcs.pickupapp.ui.adapter.HomeAdapter;
import com.tcs.pickupapp.ui.login.LoginModel;

import java.util.List;

/**
 * Created by shahrukh.malik on 09, April, 2018
 */
public interface HomeMVP {

    interface View{
        void setHomeRecyclerViewAdapter(HomeAdapter homeAdapter);
        void openBookingActivity();
        void openReportsActivity();
        void openRetakeActivity();
        void openGenerateCNActivity();
        void openRouteNavigatorActivity();
        void syncData();
        void showDPUserDialog();
        void showProgressDialogPleaseWait();
        void hideProgressDialogPleaseWait();
        void showToastShortTime(String message);
        void showToastLongTime(String message);
        String getDeviceIMEI();
    }

    interface Presenter{
        void setView(HomeMVP.View view);
        void setHomeItems();
    }

    interface Model{
        void signIn(String courierCode, String password, String IMEI, com.tcs.pickupapp.data.rest.INetwork iNetwork);
        void saveCustomerInfoData(List<com.tcs.pickupapp.data.rest.response.CustomerDetail> customerDetails, LoginModel.ILoginCustomerInfo iLoginCustomerInfo);
        void saveCnSequenceData(List<com.tcs.pickupapp.data.rest.response.CNDetail> cnList, LoginModel.ILoginCnSequence iLoginCnSequence);
        void saveHandlingInstructions(List<HandlingInstructions> handlingInstructions, LoginModel.ILoginHandlingInstruction iLoginHandlingInstruction);
        void saveServiceData(List<Services> services, LoginModel.ILoginService iLoginService);
        void saveFeedbackData(List<com.tcs.pickupapp.data.rest.response.FeedBack> feedBack, LoginModel.ILoginFeedback iLoginFeedback);
    }


}
