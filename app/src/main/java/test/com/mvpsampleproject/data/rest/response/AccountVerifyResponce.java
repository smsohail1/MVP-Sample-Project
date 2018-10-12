package com.tcs.pickupapp.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;


/**
 * Created by umair.irshad on 4/17/2018.
 */

public class AccountVerifyResponce {

    @SerializedName("Code")
    @Expose
    private String code;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("CustomerInformation")
    @Expose
    private CustomerInformation customerInformation;

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

    public CustomerInformation getCustomerInformation() {
        return customerInformation;
    }

    public void setCustomerInformation(CustomerInformation customerInformation) {
        this.customerInformation = customerInformation;
    }
    /*private boolean status;
    private String error;
    @SerializedName("responce")
    private Responce responce;

    public AccountVerifyResponce(boolean status, String error, Responce responce) {
        this.status = status;
        this.error = error;
        this.responce = responce;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Responce getResponce() {
        return responce;
    }

    public void setResponce(Responce responce) {
        this.responce = responce;
    }
}
     class Responce {
        String number,name,stationNumber,product,route,latitude,londitude;

         public Responce(String number, String name, String stationNumber, String product, String route, String latitude, String londitude) {
             this.number = number;
             this.name = name;
             this.stationNumber = stationNumber;
             this.product = product;
             this.route = route;
             this.latitude = latitude;
             this.londitude = londitude;
         }

         public String getNumber() {
             return number;
         }

         public void setNumber(String number) {
             this.number = number;
         }

         public String getName() {
             return name;
         }

         public void setName(String name) {
             this.name = name;
         }

         public String getStationNumber() {
             return stationNumber;
         }

         public void setStationNumber(String stationNumber) {
             this.stationNumber = stationNumber;
         }

         public String getProduct() {
             return product;
         }

         public void setProduct(String product) {
             this.product = product;
         }

         public String getRoute() {
             return route;
         }

         public void setRoute(String route) {
             this.route = route;
         }

         public String getLatitude() {
             return latitude;
         }

         public void setLatitude(String latitude) {
             this.latitude = latitude;
         }

         public String getLonditude() {
             return londitude;
         }

         public void setLonditude(String londitude) {
             this.londitude = londitude;
         }*/
     }