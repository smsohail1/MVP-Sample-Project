package test.com.mvpsampleproject.ui.login;

import android.os.Environment;
import android.util.Log;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.data.rest.response.SignInResponse;
import com.tcs.pickupapp.ui.dpuser.DpUserModel;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import java.io.File;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by muhammad.sohail on 4/3/2018.
 */

public class LoginPresenter implements LoginMVP.Presenter {
    private LoginMVP.View view;
    private LoginMVP.Model model;
    private SessionManager sessionManager;
    private Utils utils;

    public LoginPresenter(LoginMVP.Model model, SessionManager sessionManager, Utils utils) {
        this.model = model;
        this.sessionManager = sessionManager;
        this.utils = utils;
    }

    @Override
    public void setView(LoginMVP.View view) {
        this.view = view;
    }

    @Override
    public void onClickBtnLogin(final String courierCode, final String password, String IMEI) {
        // validate
        if (!utils.isInternetAvailable()) {
            if (((sessionManager.getCourierCode().equals(courierCode))) && ((sessionManager.getCourierPassword().equals(password)))) {
                model.checkIfAllDataIsPresentInLocalDB(new LoginModel.ILoginDataIsPresentInLocalDB() {
                    @Override
                    public void onSuccess(boolean isDataPresent) {
                        if (isDataPresent) {
                            view.showHomeScreen();
                        } else {
                            view.showToastLongTime(utils.getStringFromResourceId(R.string.some_required_data_is_missing));
                        }
                    }

                    @Override
                    public void onError(Throwable ex) {
                        view.showToastLongTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
                        ex.printStackTrace();
                    }
                });
            } else {
                //view.showToastLongTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
                view.showToastLongTime(utils.getStringFromResourceId(R.string.invalid_credentials));
            }
        } else {
            if (validateInputFields(courierCode, password, IMEI)) {
                view.hideSoftKeyboard();

                if(!sessionManager.getCourierCode().equals("")){
                    if(!sessionManager.getCourierCode().equals(courierCode)) {
                        view.showToastLongTime(utils.getStringFromResourceId(R.string.please_clear_data_of_last_logged_in_user));
                        return;
                    }
                }

                view.showProgressDialogPleaseWait();
                model.signIn(courierCode, password, AppConstants.IMEI_NUMBER_OF_1002_COURIER, new com.tcs.pickupapp.data.rest.INetwork() {
                //model.signIn(courierCode, password, IMEI, new INetwork() {
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
                        sessionManager.createLoginSession(courierCode,
                                signInResponse.getCourierInfo().getUserName(),
                                password,
                                signInResponse.getCourierInfo().getRoute(),
                                signInResponse.getCourierInfo().getStationNumber(),
                                signInResponse.getSettings().get(0).getSettingvalue(),
                                signInResponse.getSettings().get(1).getSettingvalue());
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
                        if (exception instanceof SocketTimeoutException) {
                            view.showToastLongTime("Socket Timeout. Try again.");
                        } else {
                            view.showToastLongTime(exception.getLocalizedMessage());
                        }
                    }
                });
            }
        }
    }

    @Override
    public void onClickScreen() {
        view.hideSoftKeyboard();
    }

    @Override
    public void onClickBtnCallMobilink() {
        utils.callMobilink();
    }

    private void receivedNull(String apiJsonNodeName) {
        view.hideProgressDialogPleaseWait();
        view.showToastLongTime(apiJsonNodeName + " is null");
    }

    private boolean validateInputFields(String courierCode, String password, String IMEI) {

        if (utils.isTextNullOrEmpty(courierCode)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.courier_code_cannot_be_empty));
            return false;
        }
        if (utils.minCharactersLimit(courierCode, 3)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.courier_code_min_charater_limit));
            return false;
        }

        if (utils.isTextNullOrEmpty(password)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.password_cannot_be_empty));
            return false;
        }

        if (utils.minCharactersLimit(password, 3)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.password_min_charater_limit));
            return false;
        }

        if (utils.isTextNullOrEmpty(IMEI)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.imei_number_cannot_be_empty));
            return false;
        }


        return true;
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
                view.hideProgressDialogPleaseWait();
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
                view.hideProgressDialogPleaseWait();
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
                view.hideProgressDialogPleaseWait();
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
                view.hideProgressDialogPleaseWait();
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
                  Log.d(AppConstants.LOG_TAG_PICKUP, "All Data saved");
                 view.showHomeScreen();
            }

            @Override
            public void onError(Throwable ex) {
                view.hideProgressDialogPleaseWait();
                Exception exception = (Exception) ex;
                ex.printStackTrace();
                view.showToastLongTime(exception.getMessage());
            }
        });
    }

    @Override
    public void onClickBtnOKClearData() {
        if (!utils.isInternetAvailable()) {
            view.showToastLongTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
            return;
        }
        view.showProgressDialogPleaseWait();
        model.fetchNTRecord(new DpUserModel.IDpUser() {
            @Override
            public void onDpUserRecordReceived(List<com.tcs.pickupapp.data.room.model.Booking> booking) {
                if (booking == null || booking.size() == 0) {

                    //  clearSDCardtTextFiles();


                    model.fetchPickupAppTravelog(new DpUserModel.IDpUserUploadTravelFileStatus() {
                        @Override
                        public void onSuccess(boolean status, String statusUpload) {
                            if (status && statusUpload.equalsIgnoreCase("success")) {
                                view.showToastLongTime("Data synced !!!");
                                clearAllData();
                            } else {
                                if (!utils.isInternetAvailable()) {
                                    view.showToastLongTime(utils.getStringFromResourceId(R.string.pickup_travel_file_upload_error));
                                } else if (statusUpload.equalsIgnoreCase("file does not exist")) {
                                    view.showToastLongTime("PickupAppTravelLog files does not exist");
                                }
                                clearAllData();
                            }
                        }

                        @Override
                        public void onError(Exception ex) {
                            view.showToastLongTime(ex.getMessage());
                            clearAllData();
                        }
                    });
                } else {
                    view.hideProgressDialogPleaseWait();
                    view.showToastLongTime("NT Records Found in booking. Please enter password to clear data.");
                    /*showDPUserLoginDialog(context);*/
                }
            }

            @Override
            public void onErrorReceived(Exception ex) {
                view.hideProgressDialogPleaseWait();
                view.showToastLongTime(ex.getMessage());
            }
        });
    }

    @Override
    public void checkNTBookings(final String courierCode, final String password, final String IMEI) {
        if (!validateInputFields(courierCode, password, IMEI)) {
            return;
        }
        view.showProgressDialogPleaseWait();
        model.fetchRecord(new DpUserModel.IDpUser() {
            @Override
            public void onDpUserRecordReceived(List<com.tcs.pickupapp.data.room.model.Booking> bookings) {
                view.hideProgressDialogPleaseWait();
                // check current time here
                Date dateNow = utils.getCurrentDateByGMTPlus5();
                Date date5AM = utils.getCurrentDateButTime5AMAsGMTPlus5();
                Date date8AM = utils.getCurrentDateButTime8AMAsGMTPlus5();
                if( (dateNow.after(date5AM)) && dateNow.before(date8AM)) {
                    if (bookings.size() == 0) {
                        // call login functionality
                        onClickBtnLogin(courierCode, password, IMEI);
                        return;
                    }
                    boolean isNTRecordFound = false;
                    for (com.tcs.pickupapp.data.room.model.Booking booking : bookings) {
                        if (booking.getTransmitStatus().equals("NT")) {
                            isNTRecordFound = true;
                            break;
                        }
                    }
                    if (isNTRecordFound) {
                        view.showDataNotSyncedPopup();
                    } else {
                        if(!sessionManager.getCourierCode().equals(courierCode)) {
                            view.showClearDataPopup();
                        }else {
                            onClickBtnLogin(courierCode, password, IMEI);
                        }
                    }
                }else {
                    onClickBtnLogin(courierCode, password, IMEI);
                    return;
                }
            }

            @Override
            public void onErrorReceived(Exception ex) {
                view.hideProgressDialogPleaseWait();
                if(ex.getMessage() != null) {
                    view.showToastLongTime(ex.getMessage());
                }else {
                    view.showToastLongTime(utils.getStringFromResourceId(R.string.something_went_wrong));
                }
            }
        });
    }

    private void clearAllData() {
        final List<String> filePathsToBeDeleted = new ArrayList<>();
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + AppConstants.DIRECTORY_PICKUP_BOOKINGS);
        if (dir.exists() && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                filePathsToBeDeleted.add(new File(dir, children[i]).getAbsolutePath());
            }
        }
        model.clearAllData(new DpUserModel.IDpUserClearDataStatus() {
            @Override
            public void onSuccess(boolean status) {
                if (status) {
                    String[] filePathsArray = new String[filePathsToBeDeleted.size()];
                    filePathsArray = filePathsToBeDeleted.toArray(filePathsArray);
                    view.refreshGallery(filePathsArray);
                    view.hideProgressDialogPleaseWait();
                    view.showToastLongTime("Clear data successfully.");
                    //view.showLoginScreen();
                } else {
                    view.hideProgressDialogPleaseWait();
                    view.showToastLongTime("Clear data failed.");
                }
            }

            @Override
            public void onError(Exception ex) {
                view.hideProgressDialogPleaseWait();
                view.showToastLongTime(ex.getMessage());
            }
        });
    }
}




















