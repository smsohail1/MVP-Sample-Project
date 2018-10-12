package com.tcs.pickupapp.data.rest;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * Created by muhammad.sohail on 5/14/2018.
 */

public interface WebService {
    @Multipart
    @POST("SaveCoordinateFile")
    Call<ResponseBody> upload(
            @Part MultipartBody.Part file
    );
}
