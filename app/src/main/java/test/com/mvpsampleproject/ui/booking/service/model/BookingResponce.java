package test.com.mvpsampleproject.ui.booking.service.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by umair.irshad on 4/16/2018.
 */

public class BookingResponce {

    /*private boolean status;
    private JSONObject responce;
    private String error;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public JSONObject getResponce() {
        return responce;
    }

    public void setResponce(JSONObject responce) {
        this.responce = responce;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }*/


    /*
    *
    * */

    @SerializedName("Code")
    private String code;

    @SerializedName("Message")
    private String message;

    @SerializedName("CNNumber")
    private String CNNumber;

    public BookingResponce(String code, String message, String CNNumber) {
        this.code = code;
        this.message = message;
        this.CNNumber = CNNumber;
    }

    public String getCNNumber() {
        return CNNumber;
    }

    public void setCNNumber(String CNNumber) {
        this.CNNumber = CNNumber;
    }

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
}
