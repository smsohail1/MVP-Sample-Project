package test.com.mvpsampleproject.ui.login;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.google.gson.Gson;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.data.rest.response.HandlingInstructions;
import com.tcs.pickupapp.data.rest.response.Services;
import com.tcs.pickupapp.data.rest.response.SignInResponse;
import com.tcs.pickupapp.data.room.dao.CustomerInfoDao;
import com.tcs.pickupapp.data.room.dao.FeedbackDao;
import com.tcs.pickupapp.data.room.dao.GenerateSequenceDao;
import com.tcs.pickupapp.data.room.model.CourierInfo;
import com.tcs.pickupapp.data.room.model.CustomerInfo;
import com.tcs.pickupapp.data.room.model.Feedback;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.data.room.model.HandlingInstruction;
import com.tcs.pickupapp.di.module.WebServiceFactoryModule;
import com.tcs.pickupapp.ui.dpuser.DpUserModel;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.SessionManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muhammad.sohail on 4/3/2018.
 */

public class LoginModel implements LoginMVP.Model {
    private com.tcs.pickupapp.data.rest.PickupAPI pickupAPI;
    private com.tcs.pickupapp.data.room.AppDatabase appDatabase;
    private SessionManager sessionManager;
    private String uploadStatus = "";
    com.tcs.pickupapp.data.rest.WebService webService;

    public LoginModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, com.tcs.pickupapp.data.room.AppDatabase appDatabase, SessionManager sessionManager) {
        this.pickupAPI = pickupAPI;
        this.appDatabase = appDatabase;
        this.sessionManager = sessionManager;
    }


    @Override
    public void signIn(String courierCode, String password, String IMEI, final com.tcs.pickupapp.data.rest.INetwork iNetwork) {
        Call<SignInResponse> call = pickupAPI.signIn(courierCode, password, IMEI);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                try {
                    if (response == null) {
                        iNetwork.onFailure(new NullPointerException());
                    }
                    SignInResponse signInResponse = response.body();
                    if (signInResponse.getCode().equals("00")) {
                        iNetwork.onSuccess(signInResponse);
                    } else {
                        iNetwork.onError(new ErrorResponse(signInResponse.getCode(),
                                signInResponse.getMessage()));
                    }
                } catch (Exception ex) {
                    iNetwork.onFailure(ex);
                }
            }

            @Override
            public void onFailure(Call<SignInResponse> call, Throwable t) {
                iNetwork.onFailure(t);
            }
        });
    }

    @Override
    public void saveCourierInfoData(final SignInResponse signInResponse, final ILoginCourierInfo iLoginCourierInfo) {
        try {
            Observable.just(appDatabase.getCourierInfoDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.CourierInfoDao, List<CourierInfo>>() {
                        @Override
                        public List<CourierInfo> apply(com.tcs.pickupapp.data.room.dao.CourierInfoDao customerInfoDao) throws Exception {
                            return customerInfoDao.getAllCourierInfo();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<CourierInfo>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<CourierInfo> customerInfos) {
                            CourierInfo courierInfo = new CourierInfo(signInResponse.getCourierInfo().getUserName(), signInResponse.getCourierInfo().getRoute(), signInResponse.getCourierInfo().getStationNumber(), "");
                            appDatabase.getCourierInfoDao().insert(courierInfo);
                            iLoginCourierInfo.onSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            iLoginCourierInfo.onError((Exception) e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception ex) {
            iLoginCourierInfo.onError(ex);
        }
    }

    @Override
    public void saveCnSequenceData(final List<com.tcs.pickupapp.data.rest.response.CNDetail> cnList, final ILoginCnSequence iLoginCnSequence) {
        try {
            Observable.just(appDatabase.getCNSequenceDao())
                    .map(new Function<GenerateSequenceDao, List<GenerateSequence>>() {
                        @Override
                        public List<GenerateSequence> apply(GenerateSequenceDao generateSequenceDao) throws Exception {

                            /*Deleting Generate Seq Table*/
                            //generateSequenceDao.deleteAll();
                            // above code is commented by shahrukh, new code is written below
                            List<GenerateSequence> dbSequences = generateSequenceDao.getCNSequence();
                            for(int i=0;i<cnList.size();i++){
                                boolean isFound = false;
                                for(int j=0;j<dbSequences.size();j++){
                                    if(dbSequences.get(j).checkIfBothAreSame(cnList.get(i))){
                                        isFound = true;
                                        break;
                                    }
                                }
                                if(!isFound){
                                    GenerateSequence generateSequence = new GenerateSequence(cnList.get(i).getCNFrom(),
                                            cnList.get(i).getCNTo(),
                                            cnList.get(i).getProductType());
                                    generateSequenceDao.insertCNSequence(generateSequence);
                                }
                            }

                            /*Deleting Deleted Seq Table*/
                            //appDatabase.getDeletedCNSequenceDao().deleteAll();
                            // above code is commented by shahrukh, no need to delete deleted sequences data

                            /*Deleting Dimensions Table*/
                            /*appDatabase.getDimenstionDao().deleteAll();*/
                            // above code is commented by shahrukh, no need to delete dimensions data


                            return new ArrayList<>(); // because we are not returning any list in onNext method
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<GenerateSequence>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<GenerateSequence> cnSequence) {
                            iLoginCnSequence.onSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            iLoginCnSequence.onError(e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception ex) {
            iLoginCnSequence.onError(ex);
        }
    }

    @Override
    public void saveCustomerInfoData(final List<com.tcs.pickupapp.data.rest.response.CustomerDetail> customerDetails, final ILoginCustomerInfo iLoginCustomerInfo) {
        try {
            Observable.just(appDatabase.getCustomerInfoDao())
                    .map(new Function<CustomerInfoDao, List<CustomerInfo>>() {
                        @Override
                        public List<CustomerInfo> apply(CustomerInfoDao customerInfoDao) throws Exception {
                            customerInfoDao.deleteAll();
                            List<CustomerInfo> customerInfos = new ArrayList<>();
                            for (com.tcs.pickupapp.data.rest.response.CustomerDetail customerDetail : customerDetails) {
                                String productsList = new Gson().toJson(customerDetail.getProduct());
                                customerInfos.add(new CustomerInfo(customerDetail.getCustomerNumber(),
                                        customerDetail.getCustomerName(),
                                        customerDetail.getStation(),
                                        productsList,
                                        customerDetail.getRoute()));
                            }
                            customerInfoDao.insert(customerInfos);
                            return customerInfos;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<CustomerInfo>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<CustomerInfo> customerInfos) {
                            iLoginCustomerInfo.onSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            iLoginCustomerInfo.onError(e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception ex) {
            iLoginCustomerInfo.onError(ex);
        }

    }

    @Override
    public void saveFeedbackData(final List<com.tcs.pickupapp.data.rest.response.FeedBack> feedBack, final ILoginFeedback iLoginFeedback) {
        try {
            Observable.just(appDatabase.getFeedbackDao())
                    .map(new Function<FeedbackDao, List<Feedback>>() {
                        @Override
                        public List<Feedback> apply(FeedbackDao feedbackDao) throws Exception {
                            feedbackDao.deleteAll();
                            List<Feedback> customerInfos = new ArrayList<>();
                            for (com.tcs.pickupapp.data.rest.response.FeedBack feedBackDetails : feedBack) {
                                customerInfos.add(new Feedback(feedBackDetails.getId(),
                                        feedBackDetails.getFeedback()));
                            }
                            feedbackDao.insert(customerInfos);
                            return customerInfos;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<Feedback>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<Feedback> feedbacks) {
                            iLoginFeedback.onSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            iLoginFeedback.onError(e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception ex) {
            iLoginFeedback.onError(ex);
        }
    }

    @Override
    public void saveHandlingInstructions(final List<HandlingInstructions> handlingInstructions, final ILoginHandlingInstruction iLoginHandlingInstruction) {
        try {
            Observable.just(appDatabase.getHandlingInstructionDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.HandlingInstructionDao, List<HandlingInstruction>>() {
                        @Override
                        public List<HandlingInstruction> apply(com.tcs.pickupapp.data.room.dao.HandlingInstructionDao handlingInstructionDao) throws Exception {
                            handlingInstructionDao.deleteAll();
                            List<HandlingInstruction> handlingInstructionForModel = new ArrayList<>();
                            for (HandlingInstructions handlingInstructionnn : handlingInstructions) {
                                handlingInstructionForModel.add(new HandlingInstruction(handlingInstructionnn.getInstructionNumber(),
                                        handlingInstructionnn.getDetails()));
                            }
                            handlingInstructionDao.insert(handlingInstructionForModel);
                            return handlingInstructionForModel;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<HandlingInstruction>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<HandlingInstruction> handlingInstruction) {
                            iLoginHandlingInstruction.onSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            iLoginHandlingInstruction.onError(e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception ex) {
            iLoginHandlingInstruction.onError(ex);
        }
    }

    @Override
    public void saveServiceData(final List<Services> services, final ILoginService iLoginService) {
        try {
            Observable.just(appDatabase.getServiceDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.ServiceDao, List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Service> apply(com.tcs.pickupapp.data.room.dao.ServiceDao serviceDao) throws Exception {
                            serviceDao.deleteAll();
                            List<com.tcs.pickupapp.data.room.model.Service> servicesForModel = new ArrayList<>();
                            for (Services servicess : services) {
                                servicesForModel.add(new com.tcs.pickupapp.data.room.model.Service(servicess.getServiceNumber(),
                                        servicess.getDescription(),
                                        servicess.getProductNumber()));
                            }
                            serviceDao.insert(servicesForModel);
                            return servicesForModel;
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Service> service) {
                            iLoginService.onSuccess();
                        }

                        @Override
                        public void onError(Throwable e) {
                            iLoginService.onError((Exception) e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception ex) {
            iLoginService.onError(ex);
        }
    }

    @Override
    public void checkIfAllDataIsPresentInLocalDB(final ILoginDataIsPresentInLocalDB iLoginDataIsPresentInLocalDB) {
        Observable.just(appDatabase)
                .map(new Function<com.tcs.pickupapp.data.room.AppDatabase, Boolean>() {
                    @Override
                    public Boolean apply(com.tcs.pickupapp.data.room.AppDatabase appDatabase) throws Exception {
                        if (appDatabase.getCNSequenceDao().getCNSequenceCount() == 0) {
                            return false;
                        }
                        if (appDatabase.getCustomerInfoDao().getCustomerCount() == 0) {
                            return false;
                        }
                        if (appDatabase.getHandlingInstructionDao().getHandlingInstructionsCount() == 0) {
                            return false;
                        }
                        if (appDatabase.getFeedbackDao().getFeedbackCount() == 0) {
                            return false;
                        }
                        if (appDatabase.getServiceDao().getServicesCount() == 0) {
                            return false;
                        }
                        return true;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean aBoolean) {
                        if (aBoolean) {
                            iLoginDataIsPresentInLocalDB.onSuccess(true);
                        } else {
                            iLoginDataIsPresentInLocalDB.onSuccess(false);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        iLoginDataIsPresentInLocalDB.onError(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void fetchRecord(final DpUserModel.IDpUser idpUser) {
        try {
            Observable.just(appDatabase.getBookingDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {
                            return bookingDao.getAllBookings();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> booking) {
                            idpUser.onDpUserRecordReceived(booking);
                        }

                        @Override
                        public void onError(Throwable e) {
                            idpUser.onErrorReceived((Exception) e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception ex) {
            idpUser.onErrorReceived(ex);
        }
    }

    @Override
    public void fetchNTRecord(final DpUserModel.IDpUser idpUser) {
        try {
            Observable.just(appDatabase.getBookingDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {
                            return bookingDao.getBookingByTransmissionStatus("NT");
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> booking) {
                            idpUser.onDpUserRecordReceived(booking);
                        }

                        @Override
                        public void onError(Throwable e) {
                            idpUser.onErrorReceived((Exception) e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception ex) {
            idpUser.onErrorReceived(ex);
        }
    }

    @Override
    public void fetchPickupAppTravelog(final DpUserModel.IDpUserUploadTravelFileStatus iDpUserUploadTravelFileStatus) {
        try {
            Observable.just(appDatabase)
                    .map(new Function<com.tcs.pickupapp.data.room.AppDatabase, Boolean>() {
                        @Override
                        public Boolean apply(com.tcs.pickupapp.data.room.AppDatabase appDatabase) throws Exception {
                            try {
                                // create upload service client
                                webService = WebServiceFactoryModule.getInstance();

                                String path = Environment.getExternalStorageDirectory().toString() + "/" + AppConstants.DIRECTORY_PICKUP_TRAVEL_LOG;

                                File directory = new File(path);

                                if (!directory.exists()) {
                                    directory.mkdirs();
                                }

                                File[] files = directory.listFiles();


                                if (files.length > 0) {
                                    for (int i = 0; i < files.length; i++) {
                                        uploadFile(Uri.parse(files[i].getAbsolutePath()));
                                    }
                                } else {
                                    uploadStatus = "file does not exist";
                                    return false;
                                }


                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (aBoolean) {
                                iDpUserUploadTravelFileStatus.onSuccess(true, uploadStatus);
                            } else {
                                iDpUserUploadTravelFileStatus.onSuccess(false, uploadStatus);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            iDpUserUploadTravelFileStatus.onError((Exception) e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        } catch (Exception ex) {
            iDpUserUploadTravelFileStatus.onError(ex);
        }
    }

    @Override
    public void clearAllData(final DpUserModel.IDpUserClearDataStatus iDpUserClearDataStatus) {
        try {
            Observable.just(appDatabase)
                    .map(new Function<com.tcs.pickupapp.data.room.AppDatabase, Boolean>() {
                        @Override
                        public Boolean apply(com.tcs.pickupapp.data.room.AppDatabase appDatabase) throws Exception {
                            try {
                                appDatabase.getBookingDao().deleteAll();
                                appDatabase.getCourierInfoDao().deleteAll();
                                appDatabase.getCustomerAckDao().deleteAll();
                                appDatabase.getCustomerInfoDao().deleteAll();
                                appDatabase.getDeletedCNSequenceDao().deleteAll();
                                appDatabase.getDimenstionDao().deleteAll();
                                appDatabase.getErrorReportDao().deleteAllReports();
                                appDatabase.getFeedbackDao().deleteAll();
                                appDatabase.getCNSequenceDao().deleteAll();
                                appDatabase.getHandlingInstructionDao().deleteAll();
                                appDatabase.getServiceDao().deleteAll();
                                appDatabase.getStationDao().deleteAll();
                                sessionManager.clearData();

                                clearSDCardtTextFiles();

                                deletePictures();

                                return true;
                            } catch (Exception e) {
                                e.printStackTrace();
                                return false;
                            }
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Boolean>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Boolean aBoolean) {
                            if (aBoolean) {
                                iDpUserClearDataStatus.onSuccess(true);
                            } else {
                                iDpUserClearDataStatus.onSuccess(false);
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            iDpUserClearDataStatus.onError((Exception) e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        } catch (Exception ex) {
            iDpUserClearDataStatus.onError(ex);
        }
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


            file = new File(dir, "/PickupErrorFile.txt");
            if (file.exists()) {  // check if file exist
                file.delete();
            }


        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    private void deletePictures() {
        try {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + AppConstants.DIRECTORY_PICKUP_BOOKINGS);
            if (dir.exists() && dir.isDirectory()) {
                String[] children = dir.list();
                for (int i = 0; i < children.length; i++) {
                    new File(dir, children[i]).delete();

                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void uploadFile(final Uri fileUri) {
        // create upload service client
        // WebService webService = WebServiceFactoryModule.getInstance();
        // https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
        // use the FileUtils to get the actual file by uri
        final File file = new File(String.valueOf(fileUri));

        // create RequestBody instance from file
        final RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        // add another part within the multipart request
/*        String descriptionString = "hello, this is description speaking";
        RequestBody description =
                RequestBody.create(
                        okhttp3.MultipartBody.FORM, descriptionString);*/

        // finally, execute the request
        Call<ResponseBody> call = webService.upload(body);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call,
                                   Response<ResponseBody> response) {
                String APIResponse = null;
                try {
                    APIResponse = response.body().string();
                    File file = new File(String.valueOf(fileUri));
                    boolean deleted = file.delete();
                    Thread.sleep(1000);
                    if (response.body() != null && APIResponse.contains("Success") && deleted) {
                        Log.v("Upload", "success");
                        uploadStatus = "success";
                        //Toast.makeText(context, "Data synced !!!", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    uploadStatus = "";
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    uploadStatus = "";
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Log.e("Upload error:", t.getMessage());
                uploadStatus = "";
            }
        });

    }




    public interface ILogin {
        void onSuccess();

        void onError(Exception ex);
    }


    public interface ILoginCourierInfo {
        void onSuccess();

        void onError(Exception ex);
    }


    public interface ILoginCustomerInfo {
        void onSuccess();

        void onError(Throwable t);
    }

    public interface ILoginFeedback {
        void onSuccess();

        void onError(Throwable ex);
    }

    public interface ILoginCnSequence {
        void onSuccess();

        void onError(Throwable t);
    }

    public interface ILoginHandlingInstruction {
        void onSuccess();

        void onError(Throwable ex);
    }

    public interface ILoginService {
        void onSuccess();

        void onError(Throwable ex);
    }

    public interface ILoginDataIsPresentInLocalDB {
        void onSuccess(boolean isDataPresent);

        void onError(Throwable ex);
    }

    public interface ILoginDataIsStations {
        void onSuccess();

        void onError(Throwable ex);
    }
}










