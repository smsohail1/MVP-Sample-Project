package test.com.mvpsampleproject.ui.booking;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tcs.pickupapp.data.rest.response.MessageResponse;
import com.tcs.pickupapp.data.room.dao.CustomerInfoDao;
import com.tcs.pickupapp.data.room.dao.DimensionDao;
import com.tcs.pickupapp.data.room.dao.GenerateSequenceDao;
import com.tcs.pickupapp.data.room.model.CustomerInfo;
import com.tcs.pickupapp.data.room.model.Dimension;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.data.room.model.HandlingInstruction;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;
import com.tcs.pickupapp.ui.booking.service.model.BookingResponce;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;
import com.tcs.pickupapp.util.callback.ServiceError;
import com.tcs.pickupapp.util.callback.ServiceListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by umair.irshad on 4/4/2018.
 */

public class BookingModel implements BookingMVP.Model {

    private com.tcs.pickupapp.data.room.AppDatabase appDatabase;
    private com.tcs.pickupapp.data.rest.PickupAPI pickupAPI;
    private SessionManager sessionManager;
    private Utils utils;

    public BookingModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase, com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, SessionManager sessionManager, Utils util) {
        this.appDatabase = appDatabase;
        this.pickupAPI = pickupAPI;
        this.sessionManager = sessionManager;
        this.utils = util;
    }

    @Override
    public void insert(final IBooking iBooking, com.tcs.pickupapp.data.room.model.Booking booking) {
        /* TODO
         * insert booking in local db
         * */
        try {
            com.tcs.pickupapp.data.room.dao.BookingDao bookingDao = appDatabase.getBookingDao();

            if (bookingDao.getBooking(booking.getCnNumber()) == null || booking.getIsRetake() == 1) {
                bookingDao.insert(booking);
            }


            iBooking.onBookingSuccess();
        } catch (Exception e) {
            iBooking.onBookingError(e);
        }
    }

    /*
     * fetch CourierInfo / for courier station
     * AND
     * api call to verify account number
     * */
    @Override
    public void getAccountNumberVerification(final ServiceListener<CustomerInformation> mListener, final String customerNumber) {

        Call<com.tcs.pickupapp.data.rest.response.AccountVerifyResponce> call = pickupAPI.verify(customerNumber, sessionManager.getCourierStation());
        call.enqueue(new Callback<com.tcs.pickupapp.data.rest.response.AccountVerifyResponce>() {
            @Override
            public void onResponse(@NonNull Call<com.tcs.pickupapp.data.rest.response.AccountVerifyResponce> call, @NonNull Response<com.tcs.pickupapp.data.rest.response.AccountVerifyResponce> response) {

                try {
                    if (response.body() != null) {
                        if (response.body().getMessage().equals("Success")) {
                            CustomerInformation verifiedCustomer = response.body().getCustomerInformation();

                            CustomerInfoDao customerInfoDao = appDatabase.getCustomerInfoDao();
                            CustomerInfo customerInfo =
                                    customerInfoDao.getCustomer(verifiedCustomer.getCustomerNumber());
                            if (customerInfo == null) {
                                CustomerInfo customerInfo1 =
                                        new CustomerInfo(
                                                verifiedCustomer.getCustomerNumber(),
                                                verifiedCustomer.getCustomerName(),
                                                verifiedCustomer.getStation(),
                                                new Gson().toJson(verifiedCustomer.getProducts()),
                                                verifiedCustomer.getRoute());
                                customerInfoDao.insert(customerInfo1);
                            }
                            mListener.onSuccess(verifiedCustomer);
                        } else {
                            mListener.onError(new ServiceError(response.body().getMessage()));
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mListener.onError(new ServiceError(e.getMessage()));
                }

            }

            @Override
            public void onFailure(@NonNull Call<com.tcs.pickupapp.data.rest.response.AccountVerifyResponce> call, @NonNull Throwable t) {
                mListener.onError(new ServiceError(t.getMessage(), t));
            }
        });

    }

    @Override
    public void getAllBookings(final ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> serviceListener) {
        try {
            Observable.just(appDatabase.getBookingDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {
                            return bookingDao.getAllBookings();
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
                            serviceListener.onSuccess(bookingList);
                        }

                        @Override
                        public void onError(Throwable e) {
                            serviceListener.onError(new ServiceError(e.getMessage(), e));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            serviceListener.onError(new ServiceError(e.getMessage()));

        }
    }


    @Override
    public void uploadBooking(com.tcs.pickupapp.data.room.model.Booking booking, final ServiceListener<BookingResponce> mListener) {
        Call<BookingResponce> call = pickupAPI.booking(
                booking.getCustomerNumber(),
                booking.getHandlingInstruction(),
                booking.getCnNumber(),
                booking.getCreatedDate(),
                booking.getCnType(),
                booking.getLatitude(),
                booking.getLongitude(),
                booking.getPieces(),
                booking.getWeight(),
                booking.getServiceNumber(),
                booking.getServiceName(),
                booking.getPaymentMode(),
                booking.getDeclaredValue(),
                booking.getCustomerRef(),
                booking.getCourierCode(),
                booking.getProduct(),
                booking.getRoute(),
                booking.getOriginStation(),
                booking.getOtherCharges(),
                utils.uriToBase64(booking.getImage()),
                booking.getImei(),
                booking.getDimensions(),
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                "",
                booking.getIsRetake()
        );


        call.enqueue(new Callback<BookingResponce>() {
            @Override
            public void onResponse(@NonNull Call<BookingResponce> call, @NonNull Response<BookingResponce> response) {
                mListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<BookingResponce> call, Throwable t) {
                mListener.onError(new ServiceError(t.getMessage(), t));
            }
        });
    }

    @Override
    public void callBookingAlets(String customerNumber, String pipeSeperatedData, final ServiceListener<MessageResponse> mListener) {
        Call<MessageResponse> call = pickupAPI.bookingAlerts(customerNumber, pipeSeperatedData);
        call.enqueue(new Callback<MessageResponse>() {
            @Override
            public void onResponse(Call<MessageResponse> call, Response<MessageResponse> response) {
                mListener.onSuccess(response.body());
            }

            @Override
            public void onFailure(Call<MessageResponse> call, Throwable t) {
                mListener.onError(new ServiceError(t.getMessage(), t));
            }
        });
    }

    @Override
    public void getCustomersInfo(final ServiceListener<List<CustomerInformation>> mListener) {
        try {
            Observable.just(appDatabase.getCustomerInfoDao()).
                    map(new Function<CustomerInfoDao, List<CustomerInformation>>() {
                        @Override
                        public List<CustomerInformation> apply(CustomerInfoDao customerInfoDao) throws Exception {
                            List<CustomerInfo> customerInfo = customerInfoDao.getAllCustomerInfo();

                            List<CustomerInformation> customerInfoList = new ArrayList<>();
                            for (CustomerInfo info : customerInfo) {
                                List<String> products = new Gson().fromJson(info.getProductList(),
                                        new TypeToken<List<String>>() {
                                        }.getType());
                                customerInfoList.add(
                                        new CustomerInformation(
                                                info.getName(),
                                                info.getNumber(),
                                                info.getStationNumber(),
                                                products,
                                                info.getRoute())
                                );
                            }

                            return customerInfoList;
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<CustomerInformation>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<CustomerInformation> customerInfo_list) {
                            mListener.onSuccess(customerInfo_list);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage(), e));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onError(new ServiceError(e.getMessage()));

        }
    }

    @Override
    public void getCustomersInfoByAccountNo(final String accountNo, final ServiceListener<List<CustomerInformation>> mListener) {
        try {
            Observable.just(appDatabase.getCustomerInfoDao()).
                    map(new Function<CustomerInfoDao, List<CustomerInformation>>() {
                        @Override
                        public List<CustomerInformation> apply(CustomerInfoDao customerInfoDao) throws Exception {
                            List<CustomerInfo> customerInfo = customerInfoDao.getCustomerInfoByAccountNo(accountNo);

                            List<CustomerInformation> customerInfoList = new ArrayList<>();
                            for (CustomerInfo info : customerInfo) {
                                List<String> products = new Gson().fromJson(info.getProductList(),
                                        new TypeToken<List<String>>() {
                                        }.getType());
                                customerInfoList.add(
                                        new CustomerInformation(
                                                info.getName(),
                                                info.getNumber(),
                                                info.getStationNumber(),
                                                products,
                                                info.getRoute())
                                );
                            }

                            return customerInfoList;
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<CustomerInformation>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<CustomerInformation> customerInfo_list) {
                            mListener.onSuccess(customerInfo_list);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage(), e));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onError(new ServiceError(e.getMessage()));

        }
    }


    @Override
    public void getCNSequence(final ServiceListener<List<GenerateSequence>> mListener) {
        try {
            Observable.just(appDatabase.getCNSequenceDao())
                    .map(new Function<GenerateSequenceDao, List<GenerateSequence>>() {
                        @Override
                        public List<GenerateSequence> apply(GenerateSequenceDao generateSequenceDao) throws Exception {
                            return generateSequenceDao.getCNSequence();
                        }
                    })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<List<GenerateSequence>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<GenerateSequence> generateSequences) {
                            mListener.onSuccess(generateSequences);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onError(new ServiceError(e.getMessage()));
        }

    }

    @Override
    public void checkAssignment(final String cn, final ServiceListener<Boolean> mListener) {
        try {
            Observable.just(appDatabase.getBookingDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, Boolean>() {
                        @Override
                        public Boolean apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {

                            /*
                             * Is CN already assigned or not
                             * */
                            if (bookingDao.getBooking(cn) == null) {
                                return false;
                            } else {
                                return true;
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
                        public void onNext(final Boolean isAvailable) {
                            mListener.onSuccess(isAvailable);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage(), e));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isValidCN(List<GenerateSequence> CNSequences, String s) {
        boolean valid = false;
        if (CNSequences.size() > 0) {
            for (GenerateSequence cnSequence : CNSequences) {
                long cn = Long.parseLong(s);
                long from = Long.parseLong(cnSequence.getCN_from());
                long to = Long.parseLong(cnSequence.getCN_to());
                if (cn >= from && cn <= to) {
                    valid = true;
//                    editConsignmentNo.setError(null);
                    break;
                }
            }
        } else {
            valid = false;
        }

        return valid;
    }

    @Override
    public void getBookingList(final ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener, final String customer_account) {
        try {
            Observable.just(appDatabase.getBookingDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingListDao) throws Exception {
                            return bookingListDao.getAccountBookings(customer_account);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> bookingLists) {
                            mListener.onSuccess(bookingLists);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getRetakeBookingList(final ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener, final String cn, final int reTake) {
        try {
            Observable.just(appDatabase.getBookingDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingListDao) throws Exception {
                            return bookingListDao.getRetakeAccountBookings(cn, reTake);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> bookingLists) {
                            mListener.onSuccess(bookingLists);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getBookingList(final ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener, final String cn, final int reTake) {
        try {
            Observable.just(appDatabase.getBookingDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingListDao) throws Exception {
                            return bookingListDao.getAccountBookings(cn, reTake);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> bookingLists) {
                            mListener.onSuccess(bookingLists);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getHandlingInstructions(final ServiceListener<List<HandlingInstruction>> mListener) {
        try {
            Observable.just(appDatabase.getHandlingInstructionDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.HandlingInstructionDao, List<HandlingInstruction>>() {
                        @Override
                        public List<HandlingInstruction> apply(com.tcs.pickupapp.data.room.dao.HandlingInstructionDao bookingListDao) throws Exception {
                            return bookingListDao.getAllHandlingInstruction();
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<HandlingInstruction>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<HandlingInstruction> handlingInstructions) {
                            mListener.onSuccess(handlingInstructions);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void getServices(final ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>> mListener) {
        try {
            Observable.just(appDatabase.getServiceDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.ServiceDao, List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Service> apply(com.tcs.pickupapp.data.room.dao.ServiceDao serviceDao) throws Exception {
                            return serviceDao.getAllService();
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Service> service) {
                            mListener.onSuccess(service);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onError(new ServiceError(e.getMessage()));
        }
    }


    @Override
    public void getServicesByProduct(final String product, final ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>> listener) {
        try {
            Observable.just(appDatabase.getServiceDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.ServiceDao, List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Service> apply(com.tcs.pickupapp.data.room.dao.ServiceDao serviceDao) throws Exception {
                            return serviceDao.getServicesByProduct(product);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Service> service) {
                            listener.onSuccess(service);
                        }

                        @Override
                        public void onError(Throwable e) {
                            listener.onError(new ServiceError(e.getMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError(new ServiceError(e.getMessage()));
        }
    }

    @Override
    public void getServicesHavingMoreProducts(final String product1, final String product2, final ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>> listener) {
        try {
            Observable.just(appDatabase.getServiceDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.ServiceDao, List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Service> apply(com.tcs.pickupapp.data.room.dao.ServiceDao serviceDao) throws Exception {
                            return serviceDao.getServicesHavingMoreProduct(product1, product2);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Service> service) {
                            listener.onSuccess(service);
                        }

                        @Override
                        public void onError(Throwable e) {
                            listener.onError(new ServiceError(e.getMessage()));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError(new ServiceError(e.getMessage()));
        }
    }


    @Override
    public void getConsignmentByAccountNo(final String accountNo, final ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener) {

        try {
            Observable.just(appDatabase.getBookingDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {
                            return bookingDao.getBookingByAccount(accountNo);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
                            mListener.onSuccess(bookingList);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage(), e));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            mListener.onError(new ServiceError(e.getMessage()));

        }
    }

    @Override
    public void getRetakeConsignmentByAccountNo(final String customerNumber, final int reTake, final ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener) {
        try {
            Observable.just(appDatabase.getBookingDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {
                            return bookingDao.getRetakeBookingByAccount(customerNumber, reTake);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
                            mListener.onSuccess(bookingList);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage(), e));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            mListener.onError(new ServiceError(e.getMessage()));

        }
    }

    @Override
    public void getConsignmentByAccountNo(final String customerNumber, final int reTake, final ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener) {
        try {
            Observable.just(appDatabase.getBookingDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {
                            return bookingDao.getBookingByAccount(customerNumber, reTake);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
                            mListener.onSuccess(bookingList);
                        }

                        @Override
                        public void onError(Throwable e) {
                            mListener.onError(new ServiceError(e.getMessage(), e));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            mListener.onError(new ServiceError(e.getMessage()));

        }
    }

    @Override
    public void fetchLoadPendingPickups(final String isSave, final int isRetake, final IPendingBooking iPendingBooking) {
        try {
            Observable.just(appDatabase.getBookingDao()).
                    map(new Function<com.tcs.pickupapp.data.room.dao.BookingDao, List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Booking> apply(com.tcs.pickupapp.data.room.dao.BookingDao bookingDao) throws Exception {
                            return bookingDao.getLoadPendingPickups(isSave,isRetake);
                        }
                    }).
                    subscribeOn(Schedulers.io()).
                    observeOn(AndroidSchedulers.mainThread()).
                    subscribe(new Observer<List<com.tcs.pickupapp.data.room.model.Booking>>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(List<com.tcs.pickupapp.data.room.model.Booking> bookings) {
                            iPendingBooking.onBookingSuccess(bookings);
                        }

                        @Override
                        public void onError(Throwable e) {
                            iPendingBooking.onBookingError((Exception) e);
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            iPendingBooking.onBookingError(e);
        }
    }

    @Override
    public void updateCustomerPendingStatus(final String customerNo, final String isSave) {
        try {
            appDatabase.getBookingDao().updateCustomerPendingStatus(isSave, customerNo);
        } catch (Exception e) {

        }
    }


    @Override
    public void getDimensions(final String cn, final ServiceListener<String> mListener) {
        try {
            Observable.just(appDatabase.getDimenstionDao()).map(new Function<DimensionDao, String>() {
                @Override
                public String apply(DimensionDao dimensionDao) {

                    List<Dimension> list = dimensionDao.getDimensions(cn);
                    String dimension = "";
                    for (Dimension d : list) {
                        dimension +=
                                d.getCnNumber() + "," +
                                        d.getPieces() + "," +
                                        d.getLength() + "," +
                                        d.getWidth() + "," +
                                        d.getHeight() + "," +
                                        d.getVolumetricWeight() + "," +
                                        d.getTotalVolumetricWeight() + "|";
                    }
                    return dimension;
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(String s) {
                            mListener.onSuccess(s);
                        }

                        @Override
                        public void onError(Throwable t) {
                            mListener.onError(new ServiceError(t.getMessage(), t));
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mListener.onError(new ServiceError(e.getMessage()));
        }
    }

    interface IBooking {
        void onBookingSuccess();

        void onBookingError(Exception ex);
    }

    interface IPendingBooking {
        void onBookingSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookings);

        void onBookingError(Exception ex);
    }
}
