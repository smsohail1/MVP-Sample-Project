package com.tcs.pickupapp.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by muhammad.sohail on 5/17/2018.
 */

public class ConsignmentDetailsResponse {
    @SerializedName("code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("ConsignmentDetails")
    @Expose
    private ConsignmentDetails consignmentDetails;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ConsignmentDetails getConsignmentDetails() {
        return consignmentDetails;
    }

    public void setConsignmentDetails(ConsignmentDetails consignmentDetails) {
        this.consignmentDetails = consignmentDetails;
    }
}

