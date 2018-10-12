package test.com.mvpsampleproject.ui.dpuser;

import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.ui.booking.BookingFragment;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by muhammad.sohail on 4/5/2018.
 */

public class DpUserPresenter extends Application implements DpUserMVP.Presenter {

    private DpUserMVP.View view;
    private DpUserMVP.Model model;
    private SessionManager sessionManager;
    private Utils utils;
    private ToastUtil toastUtil;
    private Context ctx;
    private Dialog dialog;


    public DpUserPresenter(DpUserMVP.Model model, SessionManager sessionManager, Utils utils, ToastUtil toastUtil, Context ctx) {
        this.model = model;
        this.sessionManager = sessionManager;
        this.utils = utils;
        this.toastUtil = toastUtil;
        this.ctx = ctx;
    }

    @Override
    public void setView(DpUserMVP.View view) {
        this.view = view;
    }

    @Override
    public void onClickBtnClearData(final Context context) {


        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_cancel_dialog);

        Button cancel = dialog.findViewById(R.id.cancel);
        Button submit = dialog.findViewById(R.id.submit);
        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);

        cancel.setText("NO");
        submit.setText("YES");

        txtMessage.setText("Are you Sure you want to delete all data?");
        txtTitle.setText("Clear Data");
        dialog.setCancelable(false);

        submit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
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


//                                    model.clearAllData(new DpUserModel.IDpUserClearDataStatus() {
//                                        @Override
//                                        public void onSuccess(boolean status) {
//                                            if (status) {
//                                                //sessionManager.setIsLoggedIn(false);
//                                                //  clearApplicationData();
//                                                view.hideProgressDialogPleaseWait();
//                                                view.showToastLongTime("Clear data successfully.");
//                                                view.showLoginScreen();
//                                            } else {
//                                                view.hideProgressDialogPleaseWait();
//                                                view.showToastLongTime("Clear data failed.");
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onError(Exception ex) {
//                                            view.hideProgressDialogPleaseWait();
//                                            view.showToastLongTime(ex.getMessage());
//                                        }
//                                    });
////
//                                    String formattedDate = GetCurrentDate();
//                                    GetFilesFromFolder(sessionManager.getCourierCode(), formattedDate);

                        } else {
                            view.hideProgressDialogPleaseWait();
                            view.showToastLongTime("NT Records Found in booking. Please enter password to clear data.");
                            showDPUserLoginDialog(context);
                        }
                    }

                    @Override
                    public void onErrorReceived(Exception ex) {
                        view.hideProgressDialogPleaseWait();
                        view.showToastLongTime(ex.getMessage());
                    }
                });

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

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
                    view.showLoginScreen();
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


    private void showDPUserLoginDialog(Context context) {

        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_dp_user_login);

        final EditText edtUsername = dialog.findViewById(R.id.edtUsername);
        final EditText edtPassword = dialog.findViewById(R.id.edtPassword);
        Button btnLogin = dialog.findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                /*if (utils.isEditTextNullOrEmpty(edtUsername)) {
                    toastUtil.showToastLongTime(R.string.username_cannot_be_empty);
                    return;
                }*/
                if (utils.isEditTextNullOrEmpty(edtPassword)) {
                    toastUtil.showToastLongTime(R.string.password_cannot_be_empty);
                    utils.playErrorToneAndVibrate(ctx);

                    return;
                }
                if (utils.minCharactersLimit(edtPassword.getText().toString(), 3)) {
                    toastUtil.showToastLongTime(R.string.password_min_charater_limit);
                    utils.playErrorToneAndVibrate(ctx);

                    return;
                }

                if (/*(edtUsername.getText().toString().equals(AppConstants.DP_USER_USERNAME)) &&*/
                        (edtPassword.getText().toString().equals(AppConstants.DP_USER_PASSWORD))) {
                    //Dismiss dialog
                    dialog.dismiss();

                    view.showProgressDialogPleaseWait();
//                    clearSDCardtTextFiles();


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

//                    model.clearAllData(new DpUserModel.IDpUserClearDataStatus() {
//                        @Override
//                        public void onSuccess(boolean status) {
//                            if (status) {
//                                view.hideProgressDialogPleaseWait();
//                                view.showToastLongTime("Clear data successfully.");
//                                view.showLoginScreen();
//                            } else {
//                                view.hideProgressDialogPleaseWait();
//                                view.showToastLongTime("Clear data failed.");
//                            }
//                        }
//
//                        @Override
//                        public void onError(Exception ex) {
//                            view.hideProgressDialogPleaseWait();
//                            view.showToastLongTime(ex.getMessage());
//                        }
//                    });


                } else {
                    toastUtil.showToastLongTime(R.string.invalid_credentials);
                    utils.playErrorToneAndVibrate(ctx);
                    return;
                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @Override
    public void onClickLocalOMS() {
        model.uploadSingleBooking(BookingFragment.testBooking, new com.tcs.pickupapp.data.rest.INetwork() {
            @Override
            public void onSuccess(Object response) {
                String str = "";
            }

            @Override
            public void onError(ErrorResponse errorResponse) {
                String str = "";
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public void onClickBtnCallBuildFile() {
        /*model.fetchRecord(new DpUserModel.IDpUser() {
            @Override
            public void onDpUserRecordReceived(List<Booking> booking) {
                if (booking == null || booking.size() == 0) {
                    view.showToastLongTime("Unable to create file.\nReason:No record found.");
                    return;
                }


                generateFileOnSD("PickupDatafile.txt", generateBookingFile(booking));
            }

            @Override
            public void onErrorReceived(Exception ex) {

            }
        });*/

        view.showProgressDialogPleaseWait();
        model.fetchRecordsInFile(new DpUserModel.IDPFileData() {
            @Override
            public void onDPFileDataReceived(String fileData) {
                generateFileOnSD("PickupDatafile.txt", fileData);
                view.hideProgressDialogPleaseWait();
            }

            @Override
            public void onErrorReceived(Exception ex) {
                view.hideProgressDialogPleaseWait();
                ex.printStackTrace();
                view.showToastLongTime(ex.getMessage());
            }
        });
    }

    @Override
    public void onClickBtnCallBuildNTFile() {
        /*model.fetchNTRecord(new DpUserModel.IDpUser() {
            @Override
            public void onDpUserRecordReceived(List<Booking> booking) {
                if (booking == null || booking.size() == 0) {
                    view.showToastLongTime("Unable to create file.\nReason:No NT record found");
                    return;
                }

                generateFileOnSD("PickupDataNTfile.txt", generateBookingFile(booking));
            }

            @Override
            public void onErrorReceived(Exception ex) {
                view.showToastLongTime(ex.getMessage());
            }
        });*/


        view.showProgressDialogPleaseWait();
        model.fetchNTRecordsInFile(new DpUserModel.IDPFileData() {
            @Override
            public void onDPFileDataReceived(String fileData) {
                generateNTFileOnSD("PickupDataNTfile.txt", fileData);
                view.hideProgressDialogPleaseWait();
            }

            @Override
            public void onErrorReceived(Exception ex) {
                view.hideProgressDialogPleaseWait();
                ex.printStackTrace();
                view.showToastLongTime(ex.getMessage());
            }
        });
    }

    private void clearSDCardtTextFiles() {

        try {

            File dir = Environment.getExternalStorageDirectory();
            File file;
            //Get the text file
            file = new File(dir, "/PickupDatafile.txt");
            if (file.exists()) {  // check if file exist
                file.delete();
            }


            file = new File(dir, "/PickupDataNTfile.txt");
            if (file.exists()) {  // check if file exist
                file.delete();
            }


        } catch (Exception ex) {
            view.hideProgressDialogPleaseWait();
            view.showToastShortTime(ex.getMessage());
        }

    }

    private String generateBookingFile(List<com.tcs.pickupapp.data.room.model.Booking> booking) {
        String body = "";
        for (int i = 0; i < booking.size(); i++) {
            /*body += booking.get(i).getBookingId() + "|" +
                    booking.get(i).getCnNumber() + "|" +
                    booking.get(i).getCustomerNumber() + "|" +
                    *//*Dest*//*"|"+
            ;*/

            body += booking.get(i).getCustomerNumber() + "|" + booking.get(i).getHandlingInstruction()
                    + "|" + booking.get(i).getCnNumber() + "|" + booking.get(i).getCnType() + "|" + booking.get(i).getCreatedDate()
                    + "|" + booking.get(i).getLatitude() + "|" + booking.get(i).getLongitude() + "|" + booking.get(i).getPieces()
                    + "|" + booking.get(i).getWeight() + "|" + booking.get(i).getServiceNumber() + "|" + booking.get(i).getPaymentMode()
                    + "|" + booking.get(i).getDeclaredValue() + "|" + booking.get(i).getShipperName() + "|" + booking.get(i).getCourierCode()
                    + "|" + booking.get(i).getProduct() + "|" + booking.get(i).getRoute() + "|" + booking.get(i).getOriginStation()
                    + "|" + booking.get(i).getIsRetake() + "|" + booking.get(i).getOtherCharges() + "|" + booking.get(i).getImage()
                    + "|" + booking.get(i).getImei() + "|" + booking.get(i).getTransmitStatus() + "\n";
        }

        return body;
    }

    private void generateFileOnSD(String sFileName, String sBody) {
        try {
            //File root = new File(Environment.getExternalStorageDirectory(), "PickUp");
            File root = new File(Environment.getExternalStorageDirectory().getPath());
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            view.showToastLongTime("Booking file created");
            view.refreshGallery(gpxfile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void generateNTFileOnSD(String sFileName, String sBody) {
        try {
            //File root = new File(Environment.getExternalStorageDirectory(), "PickUp");
            File root = new File(Environment.getExternalStorageDirectory().getPath());
            if (!root.exists()) {
                root.mkdirs();
            }
            File gpxfile = new File(root, sFileName);
            FileWriter writer = new FileWriter(gpxfile);
            writer.append(sBody);
            writer.flush();
            writer.close();
            view.showToastLongTime("Booking NT file created");
            view.refreshGallery(gpxfile.getAbsolutePath());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void clearApplicationData(Context context) {
        File cache = context.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                //cache
                if (!s.equals("lib")) {
                    // if (s.equals("cache")) {
                    deleteDir(new File(appDir, s));
                }
            }
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }

}
