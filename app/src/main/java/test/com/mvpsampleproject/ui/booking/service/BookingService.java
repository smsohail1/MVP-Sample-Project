package test.com.mvpsampleproject.ui.booking.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.tcs.pickupapp.App;
import com.tcs.pickupapp.data.rest.response.MessageResponse;
import com.tcs.pickupapp.data.room.model.ErrorReport;
import com.tcs.pickupapp.ui.booking.BookingMVP;
import com.tcs.pickupapp.ui.booking.service.model.BookingResponce;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.LogUtil;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;
import com.tcs.pickupapp.util.callback.ServiceError;
import com.tcs.pickupapp.util.callback.ServiceListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class BookingService extends Service {

    @Inject
    BookingMVP.Presenter presenter;

    @Inject
    com.tcs.pickupapp.data.room.AppDatabase appDatabase;

    @Inject
    SessionManager sessionManager;

    @Inject
    LogUtil logUtil;

    @Inject
    Utils utils;
    private boolean isAllowedToRun;

    private static String currentCNInProcess = "";

    @Override
    public void onCreate() {
        super.onCreate();
        ((App) getApplicationContext()).getAppComponent().inject(this);
        logUtil.AppLog_d("service", "onCreate");
    }

    public BookingService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        logUtil.AppLog_d("service", "onBind");
        throw null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logUtil.AppLog_d("service", "onStartCommand");
        String flag = intent.getStringExtra("flag");

        if (flag.equals(AppConstants.FLAG_RESET)) {
            /*
            * flag = 100
            * resetting no_of_attempt cout to 0
            * and,
            * start syncing
            *
            * */
            resetAttemptCount();
        } else {
            //startSyncingBookingsData();
            sendCustomerAcknowledgementBookingAlerts();
        }

        return START_REDELIVER_INTENT;
    }

    private void resetAttemptCount() {

        try {
            Observable.just(appDatabase.getBookingDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, Boolean>() {
                        @Override
                        public Boolean apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {

                            List<com.tcs.pickupapp.data.room.model.Booking> list = bookingDao.getAllNTBookings();
                            for (com.tcs.pickupapp.data.room.model.Booking booking : list) {
                                booking.setNoOfAttempts(0);
                                bookingDao.update(booking);
                                logUtil.AppLog_d("service-booking", "forLoop");
                            }
                            logUtil.AppLog_d("service-booking", "apply");
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
                        public void onNext(Boolean booking) {
                            logUtil.AppLog_d("service-booking", "onNext");
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            logUtil.AppLog_d("service-booking", "onComplete");
                            //startSyncingBookingsData();
                            sendCustomerAcknowledgementBookingAlerts();
                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
            appDatabase.getErrorReportDao().insert(new ErrorReport(
                    "resetAttemptCount",
                    "",
                    "",
                    e.getMessage(),
                    AppConstants.FROM_APP
            ));
        }
    }

    private void sendCustomerAcknowledgementBookingAlerts(){
        if (!utils.isInternetAvailableMoreAccurate()) {
            stopThisService();
            return;
        }
        final String pipeSeperatedData = sessionManager.getBookingAlertsPipeData();
        if(pipeSeperatedData.equals("no-data")){
            startSyncingBookingsData();
            return;
        }
        if(pipeSeperatedData.equals("")){
            startSyncingBookingsData();
            return;
        }
        Observable.just("")
                .map(new Function<String, Boolean>() {
                    @Override
                    public Boolean apply(String s) throws Exception {
                        presenter.sendBookingAlerts(pipeSeperatedData.split("!")[1],
                                pipeSeperatedData,
                                new ServiceListener<MessageResponse>() {
                                    @Override
                                    public void onSuccess(MessageResponse object) {
                                        String[] splits = pipeSeperatedData.split("~");
                                        List<String> cnNumbers = new ArrayList<>();
                                        for(String split : splits){
                                            cnNumbers.add(split.split("!")[0]);
                                        }
                                        sessionManager.setBookingAlertsPipeData("no-data");
                                        startSyncingBookingsData();
                                    }

                                    @Override
                                    public void onError(ServiceError error) {
                                        startSyncingBookingsData();

                                        // this is to retry the call, if SocketTimeout error occurs
                                        //sendCustomerAcknowledgementBookingAlerts();
                                    }
                                });
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
                    public void onNext(Boolean status) {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        startSyncingBookingsData();
                    }

                    @Override
                    public void onComplete() {
                        int a = 1 + 2;
                        String str = "";
                    }
                });
    }

    private void startSyncingBookingsData() {
        if (!utils.isInternetAvailableMoreAccurate()) {
            stopThisService();
            return;
        }
        logUtil.AppLog_d("service", "startSyncing");
        try {
            Observable.just(appDatabase.getBookingDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, Boolean>() {
                        @Override
                        public Boolean apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {

                            final com.tcs.pickupapp.data.room.model.Booking booking = bookingDao.getNT();
                            /*if (NT_List.size() == 0) {
                                //stopSelf();
                                stopThisService();
                                return false;
                            }*/


                            if (booking == null) {
                                isAllowedToRun = false;
                            } else {
                                isAllowedToRun = true;
                                currentCNInProcess = booking.getCnNumber();
//                            for (final Booking booking : NT_List) {
//                                if (booking != null) {

                                presenter.syncBooking(booking, new ServiceListener<BookingResponce>() {
                                    @Override
                                    public void onSuccess(BookingResponce responce) {
                                        switch (responce.getCode()) {
                                            case "305":  //transmitted
                                                setBookingStatus(responce.getCNNumber(), "T");
                                                logUtil.AppLog_d("service-booking", "T");
                                                break;
                                            case "306":  // not transmitted
                                                setBookingStatus(responce.getCNNumber(), "NT");
                                                logUtil.AppLog_d("service-booking", "NT");
                                                /*
                                                * insert into error report
                                                * */
                                                appDatabase.getErrorReportDao().insert(new ErrorReport(
                                                        booking.getCustomerName(),
                                                        booking.getCustomerNumber(),
                                                        responce.getCNNumber(),
                                                        responce.getMessage(),
                                                        AppConstants.FROM_API
                                                ));
                                                break;
                                            case "307":  // already exist
                                                logUtil.AppLog_d("service-booking", "AE-CN=" + responce.getCNNumber());
                                                setBookingStatus(responce.getCNNumber(), "T");
                                                logUtil.AppLog_d("service-booking", "T");
                                                break;
                                            default:
                                                logUtil.AppLog_d("service-booking", "unidentified error");
                                                setBookingStatus(responce.getCNNumber(), "NT");
                                                logUtil.AppLog_d("service-booking", "NT");
                                                /*
                                                * insert into error report
                                                * */
                                                appDatabase.getErrorReportDao().insert(new ErrorReport(
                                                        booking.getCustomerName(),
                                                        booking.getCustomerNumber(),
                                                        responce.getCNNumber(),
                                                        responce.getMessage(),
                                                        AppConstants.FROM_API
                                                ));
                                                break;
                                        }
                                    }

                                    @Override
                                    public void onError(ServiceError error) {
                                        logUtil.AppLog_d("service-booking", "" + error.getMessage());
                                        setBookingStatus(currentCNInProcess, "NT");
                                        logUtil.AppLog_d("service-booking", "NT");
                                                /*
                                                * INSERT INTO ErrorReport Table
                                                * */
                                        appDatabase.getErrorReportDao().insert(new ErrorReport(
                                                booking.getCustomerName(),
                                                booking.getCustomerNumber(),
                                                booking.getCnNumber(),
                                                error.getMessage(),
                                                AppConstants.FROM_API
                                        ));
                                    }
                                });
                            }
//                            }
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
                        public void onNext(Boolean booking) {
                            logUtil.AppLog_d("service-booking", "onNext");
                        }

                        @Override
                        public void onError(Throwable e) {

                        }

                        @Override
                        public void onComplete() {
                            logUtil.AppLog_d("service-booking", "onComplete");
                            if (!isAllowedToRun) {
                                stopThisService();
                            }
                        }

                    });
        } catch (Exception e) {
            e.printStackTrace();
            appDatabase.getErrorReportDao().insert(new ErrorReport(
                    "startSyncing",
                    "",
                    "",
                    e.getMessage(),
                    AppConstants.FROM_API
            ));
        }

    }

    /*
    * STATUS / T, NT
    * INCREAMENT in NO_OF_ATTEMPTS
    * */

    private void setBookingStatus(String cn, String status) {
        if (cn == null) {
            restartSyncing();
            return;
        }

        if (cn.equals("")) {
            restartSyncing();
            return;
        }
        /*Booking booking = appDatabase.getBookingDao().getBooking(cn);

        if (booking == null) {
            restartSyncing();
            return;
        }

        if (status.equals("NT")) {
            booking.setNoOfAttempts(booking.getNoOfAttempts() + 1);
        }
        booking.setTransmitStatus(status);
        appDatabase.getBookingDao().update(booking);
        logUtil.AppLog_d("service", "inserted-CN=" + booking.getCnNumber() + "" + status);

        restartSyncing();*/
        List<com.tcs.pickupapp.data.room.model.Booking> bookings = appDatabase.getBookingDao().getBookingsByCNNumber(cn);
        for(com.tcs.pickupapp.data.room.model.Booking booking : bookings){
            if (booking == null) {
                restartSyncing();
                return;
            }

            if (status.equals("NT")) {
                booking.setNoOfAttempts(booking.getNoOfAttempts() + 1);
            }
            booking.setTransmitStatus(status);
            appDatabase.getBookingDao().update(booking);
            logUtil.AppLog_d("service", "inserted-CN=" + booking.getCnNumber() + "" + status);
        }

        restartSyncing();
    }

    private void restartSyncing() {
        if (isAllowedToRun) {
            //startSyncingBookingsData();
            sendCustomerAcknowledgementBookingAlerts();
            logUtil.AppLog_d("service-booking", "onComplete-startSyncing");
        } else {
            stopThisService();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        logUtil.AppLog_d("service", "onDestroy");
    }

    private void stopThisService() {
        stopSelf();
        logUtil.AppLog_d("service", "stopThisService");
    }
}
















