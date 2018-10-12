package test.com.mvpsampleproject.ui.dpuser;

import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.jdbc.IDBConnection;
import com.tcs.pickupapp.data.jdbc.JDBCApi;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.di.module.WebServiceFactoryModule;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
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
 * Created by muhammad.sohail on 4/10/2018.
 */

public class DpUserModel implements DpUserMVP.Model {

    private com.tcs.pickupapp.data.room.AppDatabase appDatabase;
    private JDBCApi jdbcApi;
    private Utils utils;
    private SessionManager sessionManager;
    private String uploadStatus = "";
    // create upload service client
    com.tcs.pickupapp.data.rest.WebService webService;

    public DpUserModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase, JDBCApi jdbcApi, Utils utils, SessionManager sessionManager) {
        this.appDatabase = appDatabase;
        this.jdbcApi = jdbcApi;
        this.utils = utils;
        this.sessionManager = sessionManager;
    }

    @Override
    public void fetchRecord(final IDpUser idpUser) {
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
    public void fetchNTRecord(final IDpUser idpUser) {
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
    public void uploadSingleBooking(com.tcs.pickupapp.data.room.model.Booking booking, final com.tcs.pickupapp.data.rest.INetwork iNetwork) {
        utils.showJDBCLogs("connection call made ");
        /*final String procedure =
                "{ call OrderBookingMobile('"+booking.getCustomerNumber()+"','"+booking.getHandlingInstruction()+"','"+booking.getCnNumber()+"','"+booking.getCreatedDate()+"','"+booking.getCnType()+"','"+booking.getLatitude()+"','"+booking.getLongitude()+"','"+booking.getPieces()+"','"+booking.getWeight()+"','"+booking.getPaymentMode()+"','"+booking.getDeclaredValue()+"','"+booking.getShipperName()+"','"+booking.getCourierCode()+"','"+booking.getProduct()+"','"+booking.getRoute()+"','"+booking.getOriginStation()+"','"+booking.getOtherCharges()+"','"+booking.getImage()+"','"+booking.getImei()+"','"+booking.getDimensionWeight()+"','"+booking.getDefVal()+"','','"+booking.getServiceNumber()+"','"+booking.getServiceNumber()+"',?) }";*/

        String str = "/9j/4AAQSkZJRgABAQAAAQABAAD/2wBDAAYEBQYFBAYGBQYHBwYIChAKCgkJChQODwwQFxQYGBcU\n" +
                "FhYaHSUfGhsjHBYWICwgIyYnKSopGR8tMC0oMCUoKSj/2wBDAQcHBwoIChMKChMoGhYaKCgoKCgo\n" +
                "KCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCgoKCj/wAARCAwYECADASIA\n" +
                "AhEBAxEB/8QAHQAAAgMBAQEBAQAAAAAAAAAAAgMAAQQFBgcICf/EAFYQAAEEAQMDAwIEBAQFAgAA\n" +
                "HwEAAgMRIQQSMQVBURMiYQZxFDKBkQcjobFCwdHwFTNS4fEkYggWJUNyNIJTkhcmNTZjc6KywkRU\n" +
                "g5MYRWTSJ1WElLP/xAAaAQEBAQEBAQEAAAAAAAAAAAAAAQIDBAUG/8QALhEBAQEAAQQCAgMBAAEE\n" +
                "AwADAAERAgMSITEEE0FRFCIyYQUVIzNCQ1KBcWKx/9oADAMBAAIRAxEAPwD5q7ewd891UYJA33fk\n" +
                "o3tAJzx8/wBEQABG7uFwd4gFGtpwL/8ACMOcMEXf9FQOf6cq27bPtv7lXWtE8O25OEQFtzISLuj2\n" +
                "KDJ5BpMIBbi2jyVN01bW0bsUEYB47uHdAwe0m8hE1pc6xJSWqMAAEWC7vSZpyXMJAJI5FJcbQ+7O\n" +
                "RwU9gY1ptxJIyAatYqwiV2we7LyPN0kltgGuRzdIpiCAYwPsSgL3EYoLc9CtgzZJStZDcXqCVzns";
        String str2 = "";
        for (int i = 0; i < 40; i++) {
            str2 += str;
        }
        Log.d("BOSS_DK", "String Size: " + str2.length());
        final String procedure = "{ call OrderBookingMobile('NBPHA','Laptop','3140052380','2018-05-10 20:48:45','S','24.8950996','67.1565654','1','0.5','Account','','NBP HAJJ FORM ACTIVITY','1002','B','4A','KHI','',"
                //+ "'"+str2+"'"
                + "?"
                + ",'359998044970871','','','','2nd Day Express|D','2nd Day Express|D',?) }";
        jdbcApi.openConnection(new IDBConnection() {
            @Override
            public void onConnected(Connection connection) {
                jdbcApi.executeSingleBookingProcedure(procedure, connection, iNetwork);
                utils.showJDBCLogs("connected            ");
            }

            @Override
            public void onErrorConnecting(Throwable throwable) {
                ErrorResponse errorResponse = new ErrorResponse();
                if (throwable.getMessage().contains("Network Adapter could not establish the connection")) {
                    errorResponse.setCode(utils.getStringFromResourceId(R.string.error));
                    errorResponse.setMessage(utils.getStringFromResourceId(R.string.database_connectivity_failed));
                } else {
                    errorResponse.setCode(utils.getStringFromResourceId(R.string.error));
                    errorResponse.setMessage(throwable.getMessage());
                }
                iNetwork.onError(errorResponse);
                utils.showJDBCLogs("unable to connect    ");
            }
        });
    }

    @Override
    public void fetchRecordsInFile(final IDPFileData idpFileData) {
        Observable.just(appDatabase)
                .map(new Function<com.tcs.pickupapp.data.room.AppDatabase, String>() {
                    @Override
                    public String apply(com.tcs.pickupapp.data.room.AppDatabase appDatabase) throws Exception {
                        List<com.tcs.pickupapp.data.room.model.Booking> bookings = appDatabase.getBookingDao().getAllBookings();
                        return getPipeSeperatedBookingData(bookings);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        idpFileData.onDPFileDataReceived(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        idpFileData.onErrorReceived((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    @Override
    public void fetchNTRecordsInFile(final IDPFileData idpFileData) {
        Observable.just(appDatabase)
                .map(new Function<com.tcs.pickupapp.data.room.AppDatabase, String>() {
                    @Override
                    public String apply(com.tcs.pickupapp.data.room.AppDatabase appDatabase) throws Exception {
                        List<com.tcs.pickupapp.data.room.model.Booking> bookings = appDatabase.getBookingDao().getAllNTBookings();
                        return getPipeSeperatedBookingData(bookings);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        idpFileData.onDPFileDataReceived(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        idpFileData.onErrorReceived((Exception) e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private String getPipeSeperatedBookingData(List<com.tcs.pickupapp.data.room.model.Booking> bookings){
        if (bookings == null) {
            return "No Data";
        }
        if (bookings.size() == 0) {
            return "No Data";
        }
        String body = "";
        for (com.tcs.pickupapp.data.room.model.Booking booking : bookings) {
            String[] serviceNameAndNum = booking.getServiceNumber().split("\\|");
            body += booking.getBookingId() + "|" +
                    booking.getCnNumber() + "|" +
                    booking.getCustomerNumber() + "|" +
                    booking.getHandlingInstructionFullText() + "|" +
                    booking.getDimensions() + "|" +
                    booking.getWeight() + "|" +
                    booking.getPieces() + "|" +
                    serviceNameAndNum[0] + "|" +
                    booking.getPaymentMode() + "|" +
                    booking.getDeclaredValue() + "|" +
                    booking.getCustomerRef() + "|" +
                    booking.getLatitude() + "|" +
                    booking.getLongitude() + "|" +
                    booking.getCourierCode() + "|" +
                    booking.getProduct() + "|" +
                    booking.getRoute() + "|" +
                    booking.getOriginStation() + "|" +
                    "|" +
                    "|" +
                    booking.getOtherCharges() + "|" +
                    "0|" +
                    "|" +
                    booking.getCreatedDate() + "|" +
                    booking.getTransmitStatus() + "|" +
                    "0|" +
                    booking.getDimensions() + "|";
            body += "\n";
            body += "\n";
            body += "\r";
            body += "\r";
            body += System.getProperty("line.separator");
            body += System.getProperty("line.separator");
        }
        return body;
    }

    @Override
    public void clearData(IDpUserClearData iDpUserClearData) {
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
            sessionManager.clearData();
            // appDatabase.getStationDao().deleteAll();

            iDpUserClearData.onDpUserClearData("success");
        } catch (Exception e) {
            e.printStackTrace();
            iDpUserClearData.onErrorReceived((Exception) e);
        }
    }


    @Override
    public void clearAllData(final IDpUserClearDataStatus iDpUserClearDataStatus) {
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

    @Override
    public void fetchPickupAppTravelog(final IDpUserUploadTravelFileStatus iDpUserUploadTravelFileStatus) {

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


    public interface IDpUser {
        void onDpUserRecordReceived(List<com.tcs.pickupapp.data.room.model.Booking> booking);
        void onErrorReceived(Exception ex);
    }

    public interface IDPFileData {
        void onDPFileDataReceived(String fileData);

        void onErrorReceived(Exception ex);
    }


    public interface IDpUserClearData {
        void onDpUserClearData(String fileData);

        void onErrorReceived(Exception ex);
    }


    public interface IDpUserClearDataStatus {
        void onSuccess(boolean status);

        void onError(Exception ex);
    }


    public interface IDpUserUploadTravelFileStatus {
        void onSuccess(boolean status, String statusUpload);

        void onError(Exception ex);
    }
}











