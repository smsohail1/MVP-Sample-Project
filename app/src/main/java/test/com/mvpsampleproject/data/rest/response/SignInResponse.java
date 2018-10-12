package test.com.mvpsampleproject.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by muhammad.sohail on 4/2/2018.
 */

public class SignInResponse {
    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("CourierInfo")
    @Expose
    private com.tcs.pickupapp.data.rest.response.CourierInfo courierInfo;
    @SerializedName("CNDetails")
    @Expose
    private List<com.tcs.pickupapp.data.rest.response.CNDetail> cNDetails = null;
    @SerializedName("CustomerDetails")
    @Expose
    private List<com.tcs.pickupapp.data.rest.response.CustomerDetail> customerDetails = null;
    @SerializedName("HandlingInstructions")
    @Expose
    private List<HandlingInstructions> handlingInstructions = null;
    @SerializedName("Services")
    @Expose
    private List<Services> services = null;
    @SerializedName("FeedBack")
    @Expose
    private List<com.tcs.pickupapp.data.rest.response.FeedBack> feedBack = null;
    @SerializedName("Settings")
    @Expose
    private List<Setting> settings = null;

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

    public com.tcs.pickupapp.data.rest.response.CourierInfo getCourierInfo() {
        return courierInfo;
    }

    public void setCourierInfo(com.tcs.pickupapp.data.rest.response.CourierInfo courierInfo) {
        this.courierInfo = courierInfo;
    }

    public List<com.tcs.pickupapp.data.rest.response.CNDetail> getCNDetails() {
        return cNDetails;
    }

    public void setCNDetails(List<com.tcs.pickupapp.data.rest.response.CNDetail> cNDetails) {
        this.cNDetails = cNDetails;
    }

    public List<com.tcs.pickupapp.data.rest.response.CustomerDetail> getCustomerDetails() {
        return customerDetails;
    }

    public void setCustomerDetails(List<com.tcs.pickupapp.data.rest.response.CustomerDetail> customerDetails) {
        this.customerDetails = customerDetails;
    }

    public List<HandlingInstructions> getHandlingInstructions() {
        return handlingInstructions;
    }

    public void setHandlingInstructions(List<HandlingInstructions> handlingInstructions) {
        this.handlingInstructions = handlingInstructions;
    }

    public List<Services> getServices() {
        return services;
    }

    public void setServices(List<Services> services) {
        this.services = services;
    }

    public List<com.tcs.pickupapp.data.rest.response.FeedBack> getFeedBack() {
        return feedBack;
    }

    public void setFeedBack(List<com.tcs.pickupapp.data.rest.response.FeedBack> feedBack) {
        this.feedBack = feedBack;
    }

    public List<Setting> getSettings() {
        return settings;
    }

    public void setSettings(List<Setting> settings) {
        this.settings = settings;
    }
}
