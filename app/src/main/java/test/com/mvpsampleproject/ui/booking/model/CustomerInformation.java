package test.com.mvpsampleproject.ui.booking.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by umair.irshad on 4/18/2018.
 */

public class CustomerInformation {

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
    private List<String> products;
    @SerializedName("Route")
    @Expose
    private String route;
    @SerializedName("Latitude")
    @Expose
    private String latitude;
    @SerializedName("Longitude")
    @Expose
    private String longitude;

    public CustomerInformation(String customerName, String customerNumber, String station, List<String> products, String route) {
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.station = station;
        this.products = products;
        this.route = route;
    }

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

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
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

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }


}
