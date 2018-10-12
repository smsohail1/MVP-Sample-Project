package test.com.mvpsampleproject.ui.account_history;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.data.room.dao.CustomerInfoDao;
import com.tcs.pickupapp.data.room.model.CustomerInfo;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;
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
 * Created by muhammad.sohail on 5/7/2018.
 */

public class AccountHistoryModel implements AccountHistoryMVP.Model {

    private com.tcs.pickupapp.data.rest.PickupAPI pickupAPI;
    private com.tcs.pickupapp.data.room.AppDatabase appDatabase;

    public AccountHistoryModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, com.tcs.pickupapp.data.room.AppDatabase appDatabase) {
        this.pickupAPI = pickupAPI;
        this.appDatabase = appDatabase;
    }

    @Override
    public void getAccountDetails(String accountNo, String courierCode, final com.tcs.pickupapp.data.rest.INetwork iNetwork) {
        Call<com.tcs.pickupapp.data.rest.response.AccountHistoryResponse> call = pickupAPI.accountDetails(accountNo, courierCode);
        call.enqueue(new Callback<com.tcs.pickupapp.data.rest.response.AccountHistoryResponse>() {
            @Override
            public void onResponse(Call<com.tcs.pickupapp.data.rest.response.AccountHistoryResponse> call, Response<com.tcs.pickupapp.data.rest.response.AccountHistoryResponse> response) {
                try {
                    if (response == null) {
                        iNetwork.onFailure(new NullPointerException());
                    }
                    com.tcs.pickupapp.data.rest.response.AccountHistoryResponse accountHistoryResponse = response.body();

                    if (accountHistoryResponse.getCode().equals("332") &&
                            accountHistoryResponse.getCode().equalsIgnoreCase("No Record Found")) {
                        iNetwork.onError(new ErrorResponse(accountHistoryResponse.getCode(),
                                accountHistoryResponse.getMessage()));
                        return;
                    } else if (accountHistoryResponse.getCode().equals("00") &&
                            accountHistoryResponse.getMessage().equalsIgnoreCase("Success")) {
                        iNetwork.onSuccess(accountHistoryResponse);
                        return;
                    } else {
                        iNetwork.onError(new ErrorResponse(accountHistoryResponse.getCode(),
                                accountHistoryResponse.getMessage()));
                        return;
                    }
                } catch (Exception ex) {
                    iNetwork.onFailure(ex);
                }
            }

            @Override
            public void onFailure(Call<com.tcs.pickupapp.data.rest.response.AccountHistoryResponse> call, Throwable t) {
                iNetwork.onFailure(t);
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

}
