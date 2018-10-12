package com.tcs.pickupapp.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by muhammad.sohail on 5/10/2018.
 */

public class EAIFAULT {
    @SerializedName("requestTime")
    @Expose
    private String requestTime;
    @SerializedName("returnStatus")
    @Expose
    private ReturnStatus returnStatus;

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }

    public ReturnStatus getReturnStatus() {
        return returnStatus;
    }

    public void setReturnStatus(ReturnStatus returnStatus) {
        this.returnStatus = returnStatus;
    }
}
