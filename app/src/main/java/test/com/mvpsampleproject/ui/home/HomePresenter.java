package test.com.mvpsampleproject.ui.home;

import android.util.Log;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.data.rest.response.SignInResponse;
import com.tcs.pickupapp.ui.adapter.HomeAdapter;
import com.tcs.pickupapp.ui.home.model.HomeItem;
import com.tcs.pickupapp.ui.login.LoginModel;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shahrukh.malik on 09, April, 2018
 */
public class HomePresenter implements HomeMVP.Presenter,HomeAdapter.IHomeAdapter{
    private HomeMVP.View view;
    private HomeMVP.Model model;
    private HomeAdapter homeAdapter;
    private SessionManager sessionManager;
    private Utils utils;

    public HomePresenter(HomeMVP.Model model,SessionManager sessionManager,Utils utils){
        this.model = model;
        this.sessionManager = sessionManager;
        this.utils = utils;
    }

    @Override
    public void setView(HomeMVP.View view) {
        this.view = view;
    }

    @Override
    public void setHomeItems() {
        List<HomeItem> homeItems = new ArrayList<>();
        homeItems.add(new HomeItem(R.string.booking, R.drawable.icon_booking));
        homeItems.add(new HomeItem(R.string.reports, R.drawable.icon_reports));
        homeItems.add(new HomeItem(R.string.retake, R.drawable.icon_retake));
        homeItems.add(new HomeItem(R.string.fetch_data, R.drawable.icon_fetch_data));
        homeItems.add(new HomeItem(R.string.generate_cn, R.drawable.icon_barcode));
        homeItems.add(new HomeItem(R.string.route_navigator, R.drawable.icon_journey));
        homeItems.add(new HomeItem(R.string.dp_user, R.drawable.icon_dp_user));
        homeItems.add(new HomeItem(R.string.sync_data, R.drawable.icon_sync_data));
        homeAdapter = new HomeAdapter(homeItems,this);
        view.setHomeRecyclerViewAdapter(homeAdapter);
    }

    @Override
    public void onHomeItemClick(HomeItem homeItem) {
        switch (homeItem.getNameResId()){
            case R.string.booking:{
                view.openBookingActivity();
                break;
            }
            case R.string.reports:{
                view.openReportsActivity();
                break;
            }
            case R.string.retake:{
                view.openRetakeActivity();
                break;
            }
            case R.string.fetch_data:{
                if(!utils.isInternetAvailable()){
                    view.showToastLongTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
                    return;
                }
                fetchData();
                break;
            }
            case R.string.generate_cn:{
                view.openGenerateCNActivity();
                break;
            }
            case R.string.route_navigator:{
                String lastViewedDate = sessionManager.getRouteNavigatorLastViewedDate();
                if(lastViewedDate.equals(utils.getCurrentDate(AppConstants.DATE_FORMAT_SEVEN_FOR_ROUTE_VIEWED_DATE))){
                    view.showToastLongTime(utils.getStringFromResourceId(R.string.route_navigator_limit_message));
                }else {
                    view.openRouteNavigatorActivity();
                }
                break;
            }
            case R.string.dp_user:{
                view.showDPUserDialog();
                break;
            }
            case R.string.sync_data:{
                view.syncData();
                break;
            }
        }
    }

    private void fetchData(){
        view.showProgressDialogPleaseWait();
        model.signIn(sessionManager.getCourierCode(), sessionManager.getCourierPassword(), AppConstants.IMEI_NUMBER_OF_1002_COURIER, new com.tcs.pickupapp.data.rest.INetwork() {
        //model.signIn(sessionManager.getCourierCode(), sessionManager.getCourierPassword(), view.getDeviceIMEI(), new INetwork() {
            @Override
            public void onSuccess(Object response) {
                final SignInResponse signInResponse = (SignInResponse) response;
                if (signInResponse.getCourierInfo() == null) {
                    receivedNull("CourierInfo");
                    return;
                } else if (signInResponse.getCNDetails() == null) {
                    receivedNull("CNDetails");
                    return;
                } else if (signInResponse.getCustomerDetails() == null) {
                    receivedNull("CustomerDetails");
                    return;
                } else if (signInResponse.getHandlingInstructions() == null) {
                    receivedNull("HandlingInstructions");
                    return;
                } else if (signInResponse.getServices() == null) {
                    receivedNull("Services");
                    return;
                }
                sessionManager.createLoginSession(sessionManager.getCourierCode(),
                        signInResponse.getCourierInfo().getUserName(),
                        sessionManager.getCourierPassword(),
                        signInResponse.getCourierInfo().getRoute(),
                        signInResponse.getCourierInfo().getStationNumber(),
                        signInResponse.getSettings().get(0).getSettingvalue(),
                        signInResponse.getSettings().get(1).getSettingvalue() );
                saveCnSequenceData(signInResponse);
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                view.hideProgressDialogPleaseWait();
                view.showToastLongTime(errorResponse.getMessage());
            }

            @Override
            public void onFailure(Throwable t) {
                Exception exception = (Exception) t;
                t.printStackTrace();
                view.hideProgressDialogPleaseWait();
                view.showToastLongTime(exception.getLocalizedMessage());
            }
        });
    }

    private void saveCnSequenceData(final SignInResponse signInResponse) {
        model.saveCnSequenceData(signInResponse.getCNDetails(), new LoginModel.ILoginCnSequence() {
            @Override
            public void onSuccess() {
                Log.d(AppConstants.LOG_TAG_PICKUP, "CNDetails saved");
                saveCustomerInfoData(signInResponse);
            }

            @Override
            public void onError(Throwable ex) {
                Exception exception = (Exception) ex;
                ex.printStackTrace();
                view.showToastLongTime(exception.getLocalizedMessage());
            }
        });
    }

    private void saveCustomerInfoData(final SignInResponse signInResponse) {
        model.saveCustomerInfoData(signInResponse.getCustomerDetails(), new LoginModel.ILoginCustomerInfo() {
            @Override
            public void onSuccess() {
                Log.d(AppConstants.LOG_TAG_PICKUP, "CustomerDetails saved");
                saveHandlingInstructionData(signInResponse);
            }

            @Override
            public void onError(Throwable ex) {
                Exception exception = (Exception) ex;
                ex.printStackTrace();
                view.showToastLongTime(exception.getLocalizedMessage());
            }
        });
    }

    private void saveHandlingInstructionData(final SignInResponse signInResponse) {
        model.saveHandlingInstructions(signInResponse.getHandlingInstructions(), new LoginModel.ILoginHandlingInstruction() {
            @Override
            public void onSuccess() {
                Log.d(AppConstants.LOG_TAG_PICKUP, "HandlingInstructions saved");
                saveFeedbackData(signInResponse);
            }

            @Override
            public void onError(Throwable ex) {
                Exception exception = (Exception) ex;
                ex.printStackTrace();
                view.showToastLongTime(exception.getLocalizedMessage());
            }
        });
    }

    private void saveFeedbackData(final SignInResponse signInResponse) {
        model.saveFeedbackData(signInResponse.getFeedBack(), new LoginModel.ILoginFeedback() {
            @Override
            public void onSuccess() {
                Log.d(AppConstants.LOG_TAG_PICKUP, "Feedback saved");
                saveServiceData(signInResponse);
            }

            @Override
            public void onError(Throwable ex) {
                Exception exception = (Exception) ex;
                ex.printStackTrace();
                view.showToastLongTime(exception.getLocalizedMessage());
            }
        });
    }

    private void saveServiceData(SignInResponse signInResponse) {
        model.saveServiceData(signInResponse.getServices(), new LoginModel.ILoginService() {
            @Override
            public void onSuccess() {
                view.hideProgressDialogPleaseWait();
                Log.d(AppConstants.LOG_TAG_PICKUP, "All Data saved");
                view.showToastLongTime("All Data saved");
            }

            @Override
            public void onError(Throwable ex) {
                Exception exception = (Exception) ex;
                ex.printStackTrace();
                view.showToastLongTime(exception.getMessage());
            }
        });
    }

    private void receivedNull(String apiJsonNodeName) {
        view.hideProgressDialogPleaseWait();
        view.showToastLongTime(apiJsonNodeName + " is null");
    }
}











