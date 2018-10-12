package com.tcs.pickupapp.data.rest;

import com.tcs.pickupapp.data.rest.response.AccountHistoryResponse;
import com.tcs.pickupapp.data.rest.response.AccountVerifyResponce;
import com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse;
import com.tcs.pickupapp.data.rest.response.FeedbackResponse;
import com.tcs.pickupapp.data.rest.response.MessageResponse;
import com.tcs.pickupapp.data.rest.response.NotificationResponse;
import com.tcs.pickupapp.data.rest.response.SignInResponse;


import com.tcs.pickupapp.data.rest.response.AccountHistoryResponse;
import com.tcs.pickupapp.data.rest.response.TTCResponse;
import com.tcs.pickupapp.ui.booking.BookingModel;
import com.tcs.pickupapp.ui.booking.service.model.BookingResponce;
import com.tcs.pickupapp.ui.customer_ack_email.model.CustomerAckResponce;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
public interface PickupAPI {

    String WEB_API_URL = "/PickupAPI/Api/Access/"; // UAT
    //   String WEB_API_URL = "/hhd/pickupapinew/api/access/"; // LIVE
    //String WEB_API_URL = "/Pickup/API/Access/"; // QA

    @FormUrlEncoded
    @POST(WEB_API_URL + "AuthenticateUSer")
    Call<SignInResponse> signIn(@Field("UserID") String courierCode,
                                @Field("Password") String password,
                                @Field("IMEI") String imei);


    @FormUrlEncoded
    @POST(WEB_API_URL + "VerifyCustomer")
    Call<AccountVerifyResponce> verify(@Field("CustomerNumber") String customerNumber,
                                       @Field("Station") String station);

    @FormUrlEncoded
    @POST(WEB_API_URL + "SubmitFeedback")
    Call<FeedbackResponse> submitFeedback(@Field("CourierCode") String CourierCode,
                                          @Field("FeedBacktypeID") String FeedBacktypeID,
                                          @Field("FeedbackMessage") String FeedbackMessage);

    @FormUrlEncoded
    @POST(WEB_API_URL + "CustomerAcknowledgement")
    Call<CustomerAckResponce> CustomerAcknowledgement(@Field("Datetime") String dateTime,
                                                      @Field("CourierCode") String courierCode);

    @POST(WEB_API_URL + "UplaodErrorFile")
    Call<MessageResponse> uploadErrorLogFile(@Body RequestBody requestBody);

    @POST(WEB_API_URL + "GetNotification")
    Call<NotificationResponse> getNotifications();


    @FormUrlEncoded
    @POST(WEB_API_URL + "AccountDetails")
    Call<AccountHistoryResponse> accountDetails(@Field("CustomerNumber") String customerNumber,
                                                @Field("CourierCode") String courierCode);


    @FormUrlEncoded
    @POST(WEB_API_URL + "ConsignmentDetails")
    Call<ConsignmentDetailsResponse> consignmentDetails(@Field("CourierCode") String courierCode,
                                                        @Field("CNNumber") String cnNumber);

    @FormUrlEncoded
    @POST(WEB_API_URL + "BookingAlerts")
    Call<MessageResponse> bookingAlerts(@Field("CustomerNumber") String customerNumber,
                                        @Field("PipeData") String pipeSeperatedData);
}



































