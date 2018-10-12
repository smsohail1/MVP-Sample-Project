package com.tcs.pickupapp.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by shahrukh.malik on 16, April, 2018
 */
public class CNDetail {
    @SerializedName("CNFrom")
    @Expose
    private String cNFrom;
    @SerializedName("CNTo")
    @Expose
    private String cNTo;
    @SerializedName("ProductType")
    @Expose
    private String productType;

    public String getCNFrom() {
        return cNFrom;
    }

    public void setCNFrom(String cNFrom) {
        this.cNFrom = cNFrom;
    }

    public String getCNTo() {
        return cNTo;
    }

    public void setCNTo(String cNTo) {
        this.cNTo = cNTo;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
