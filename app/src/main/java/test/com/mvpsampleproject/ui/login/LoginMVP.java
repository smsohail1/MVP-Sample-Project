package test.com.mvpsampleproject.ui.login;

import com.tcs.pickupapp.data.rest.response.HandlingInstructions;
import com.tcs.pickupapp.data.rest.response.Services;
import com.tcs.pickupapp.data.rest.response.SignInResponse;
import com.tcs.pickupapp.ui.dpuser.DpUserModel;

import java.util.List;

/**
 * Created by muhammad.sohail on 4/3/2018.
 */

public interface LoginMVP {

    interface View {
        void showToastShortTime(String message);

        void showToastLongTime(String message);

        void hideSoftKeyboard();

        void showProgressDialogPleaseWait();

        void hideProgressDialogPleaseWait();

        void showHomeScreen();

        void refreshGallery(String[] filePaths);

        void showLoginScreen();

        void showClearDataPopup();

        void showDataNotSyncedPopup();
    }

    interface Presenter {
        void setView(LoginMVP.View view);

        void onClickBtnLogin(String courierCode, String password, String IMEI);

        void onClickBtnCallMobilink();

        void onClickScreen();

        void onClickBtnOKClearData();

        void checkNTBookings(String courierCode, String password, String IMEI);
    }

    interface Model {
        //    void login(String courierCode, String password, LoginModel.ILogin iLogin);
        void signIn(String courierCode, String password, String IMEI, com.tcs.pickupapp.data.rest.INetwork iNetwork);

        void saveCourierInfoData(SignInResponse signInResponse, LoginModel.ILoginCourierInfo iLoginCourierInfo);

        void saveCustomerInfoData(List<com.tcs.pickupapp.data.rest.response.CustomerDetail> customerDetails, LoginModel.ILoginCustomerInfo iLoginCustomerInfo);

        void saveCnSequenceData(List<com.tcs.pickupapp.data.rest.response.CNDetail> cnList, LoginModel.ILoginCnSequence iLoginCnSequence);

        void saveHandlingInstructions(List<HandlingInstructions> handlingInstructions, LoginModel.ILoginHandlingInstruction iLoginHandlingInstruction);

        void saveServiceData(List<Services> services, LoginModel.ILoginService iLoginService);

        void saveFeedbackData(List<com.tcs.pickupapp.data.rest.response.FeedBack> feedBack, LoginModel.ILoginFeedback iLoginFeedback);

        void checkIfAllDataIsPresentInLocalDB(LoginModel.ILoginDataIsPresentInLocalDB iLoginDataIsPresentInLocalDB);

        void fetchRecord(DpUserModel.IDpUser idpUser);

        void fetchNTRecord(DpUserModel.IDpUser idpUser);

        void fetchPickupAppTravelog(DpUserModel.IDpUserUploadTravelFileStatus iDpUserUploadTravelFileStatus);

        void clearAllData(DpUserModel.IDpUserClearDataStatus iDpUserClearDataStatus);
    }

}
