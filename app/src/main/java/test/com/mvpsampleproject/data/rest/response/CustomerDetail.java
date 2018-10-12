package com.tcs.pickupapp.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.tcs.pickupapp.ui.booking.model.Product;

import org.json.JSONArray;

import java.util.List;

/**
 * Created by shahrukh.malik on 16, April, 2018
 */
public class CustomerDetail {
    @SerializedName("CustomerNumber")
    @Expose
    private String customerNumber;
    @SerializedName("CustomerName")
    @Expose
    private String customerName;
    @SerializedName("Station")
    @Expose
    private String station;
    @SerializedName("Product")
    @Expose
    private List<String> product;
    @SerializedName("Route")
    @Expose
    private String route;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public List<String> getProduct() {
        return product;
    }

    public void setProduct(List<String> product) {
        this.product = product;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }
}
