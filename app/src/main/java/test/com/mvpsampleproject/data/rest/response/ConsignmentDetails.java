package com.tcs.pickupapp.data.rest.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by muhammad.sohail on 5/17/2018.
 */

public class ConsignmentDetails {
    @SerializedName("CustomerNumber")
    @Expose
    private String customerNumber;
    @SerializedName("HandlingInstruction")
    @Expose
    private String handlingInstruction;
    @SerializedName("CNNumber")
    @Expose
    private String cNNumber;
    @SerializedName("CreateDate")
    @Expose
    private String createDate;
    @SerializedName("CNType")
    @Expose
    private String cNType;
    @SerializedName("Latitude")
    @Expose
    private String latitude;
    @SerializedName("Longitude")
    @Expose
    private String longitude;
    @SerializedName("Pieces")
    @Expose
    private String pieces;
    @SerializedName("Weight")
    @Expose
    private String weight;
    @SerializedName("ServiceName")
    @Expose
    private String serviceName;
    @SerializedName("PaymentMode")
    @Expose
    private String paymentMode;
    @SerializedName("DeclareValue")
    @Expose
    private String declareValue;
    @SerializedName("ShipperName")
    @Expose
    private String shipperName;
    @SerializedName("CourierCode")
    @Expose
    private String courierCode;
    @SerializedName("Product")
    @Expose
    private String product;
    @SerializedName("Route")
    @Expose
    private String route;
    @SerializedName("OriginStation")
    @Expose
    private String originStation;
    @SerializedName("OtherCharges")
    @Expose
    private String otherCharges;
    @SerializedName("IMEI")
    @Expose
    private String iMEI;
    @SerializedName("DimensionWeight")
    @Expose
    private String dimensionWeight;

    public String getCustomerNumber() {
        return customerNumber;
    }

    public void setCustomerNumber(String customerNumber) {
        this.customerNumber = customerNumber;
    }

    public String getHandlingInstruction() {
        return handlingInstruction;
    }

    public void setHandlingInstruction(String handlingInstruction) {
        this.handlingInstruction = handlingInstruction;
    }

    public String getCNNumber() {
        return cNNumber;
    }

    public void setCNNumber(String cNNumber) {
        this.cNNumber = cNNumber;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCNType() {
        return cNType;
    }

    public void setCNType(String cNType) {
        this.cNType = cNType;
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

    public String getPieces() {
        return pieces;
    }

    public void setPieces(String pieces) {
        this.pieces = pieces;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getDeclareValue() {
        return declareValue;
    }

    public void setDeclareValue(String declareValue) {
        this.declareValue = declareValue;
    }

    public String getShipperName() {
        return shipperName;
    }

    public void setShipperName(String shipperName) {
        this.shipperName = shipperName;
    }

    public String getCourierCode() {
        return courierCode;
    }

    public void setCourierCode(String courierCode) {
        this.courierCode = courierCode;
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

    public String getOriginStation() {
        return originStation;
    }

    public void setOriginStation(String originStation) {
        this.originStation = originStation;
    }

    public String getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(String otherCharges) {
        this.otherCharges = otherCharges;
    }

    public String getIMEI() {
        return iMEI;
    }

    public void setIMEI(String iMEI) {
        this.iMEI = iMEI;
    }

    public String getDimensionWeight() {
        return dimensionWeight;
    }

    public void setDimensionWeight(String dimensionWeight) {
        this.dimensionWeight = dimensionWeight;
    }
}
