package test.com.mvpsampleproject.ui.home;

import com.google.gson.Gson;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.data.rest.response.HandlingInstructions;
import com.tcs.pickupapp.data.rest.response.Services;
import com.tcs.pickupapp.data.rest.response.SignInResponse;
import com.tcs.pickupapp.data.room.dao.CustomerInfoDao;
import com.tcs.pickupapp.data.room.dao.FeedbackDao;
import com.tcs.pickupapp.data.room.dao.GenerateSequenceDao;
import com.tcs.pickupapp.data.room.model.CustomerInfo;
import com.tcs.pickupapp.data.room.model.Feedback;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.data.room.model.HandlingInstruction;
import com.tcs.pickupapp.ui.login.LoginModel;

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
 * Created by shahrukh.malik on 09, April, 2018
 */
public class HomeModel implements HomeMVP.Model{
    private com.tcs.pickupapp.data.rest.PickupAPI pickupAPI;
    private com.tcs.pickupapp.data.room.AppDatabase appDatabase;

    public HomeModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, com.tcs.pickupapp.data.room.AppDatabase appDatabase) {
        this.pickupAPI = pickupAPI;
        this.appDatabase = appDatabase;
    }


    @Override
    public void signIn(String courierCode, String password, String IMEI, final com.tcs.pickupapp.data.rest.INetwork iNetwork) {
        Call<SignInResponse> call = pickupAPI.signIn(courierCode,password,IMEI);
        call.enqueue(new Callback<SignInResponse>() {
            @Override
            public void onResponse(Call<SignInResponse> call, Response<SignInResponse> response) {
                try {
                    if(response == null){
                        iNetwork.onFailure(new NullPointerException());
                    }
                    SignInResponse signInResponse = response.body();
                    if(signInResponse.getCode().equals("00")){
                        iNetwork.onSuccess(signInResponse);
                    } else {
                        iNetwork.onError(new ErrorResponse(signInResponse.getCode(),
                                signInResponse.getMessage()));
                    }
                }catch (Exception ex){
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
    public void saveCnSequenceData(final List<com.tcs.pickupapp.data.rest.response.CNDetail> cnList, final LoginModel.ILoginCnSequence iLoginCnSequence) {
        try {
            Observable.just(appDatabase.getCNSequenceDao())
                    .map(new Function<GenerateSequenceDao, List<GenerateSequence>>() {
                        @Override
                        public List<GenerateSequence> apply(GenerateSequenceDao generateSequenceDao) throws Exception {

                            //generateSequenceDao.deleteAll();
                            //appDatabase.getDeletedCNSequenceDao().deleteAll();
                            //appDatabase.getDimenstionDao().deleteAll();

                            List<GenerateSequence> dbSequences = generateSequenceDao.getCNSequence();
                            boolean isFound = false;
                            for(int i=0;i<cnList.size();i++){
                                isFound = false;
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
                            return dbSequences;
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
    public void saveCustomerInfoData(final List<com.tcs.pickupapp.data.rest.response.CustomerDetail> customerDetails, final LoginModel.ILoginCustomerInfo iLoginCustomerInfo) {
        try {
            Observable.just(appDatabase.getCustomerInfoDao())
                    .map(new Function<CustomerInfoDao, List<CustomerInfo>>() {
                        @Override
                        public List<CustomerInfo> apply(CustomerInfoDao customerInfoDao) throws Exception {
                            customerInfoDao.deleteAll();
                            List<CustomerInfo> customerInfos = new ArrayList<>();
                            for(com.tcs.pickupapp.data.rest.response.CustomerDetail customerDetail : customerDetails){
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
    public void saveHandlingInstructions(final List<HandlingInstructions> handlingInstructions, final LoginModel.ILoginHandlingInstruction iLoginHandlingInstruction) {
        try {
            Observable.just(appDatabase.getHandlingInstructionDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.HandlingInstructionDao, List<HandlingInstruction>>() {
                        @Override
                        public List<HandlingInstruction> apply(com.tcs.pickupapp.data.room.dao.HandlingInstructionDao handlingInstructionDao) throws Exception {
                            handlingInstructionDao.deleteAll();
                            List<HandlingInstruction> handlingInstructionForModel = new ArrayList<>();
                            for(HandlingInstructions handlingInstructionnn : handlingInstructions){
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
    public void saveServiceData(final List<Services> services, final LoginModel.ILoginService iLoginService) {
        try {
            Observable.just(appDatabase.getServiceDao())
                    .map(new Function<com.tcs.pickupapp.data.room.dao.ServiceDao, List<com.tcs.pickupapp.data.room.model.Service>>() {
                        @Override
                        public List<com.tcs.pickupapp.data.room.model.Service> apply(com.tcs.pickupapp.data.room.dao.ServiceDao serviceDao) throws Exception {
                            serviceDao.deleteAll();
                            List<com.tcs.pickupapp.data.room.model.Service> servicesForModel = new ArrayList<>();
                            for(Services servicess : services){
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
    public void saveFeedbackData(final List<com.tcs.pickupapp.data.rest.response.FeedBack> feedBack, final LoginModel.ILoginFeedback iLoginFeedback) {
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
}












