package com.tcs.pickupapp.data.room.model;

import android.arch.persistence.room.ColumnInfo;

/**
 * Created by shahrukh.malik on 16, April, 2018
 */
public class ReportDetail {
    @ColumnInfo(name = "customer_ref")
    private String customerReference;
    @ColumnInfo(name = "cn_number")
    private String consignmentNumber;
    @ColumnInfo(name = "weight")
    private double weight;
    @ColumnInfo(name = "pieces")
    private int pieces;
    @ColumnInfo(name = "transmit_status")
    private String transmitStatus;

    public String getCustomerReference() {
        return customerReference;
    }

    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    public String getConsignmentNumber() {
        return consignmentNumber;
    }

    public void setConsignmentNumber(String consignmentNumber) {
        this.consignmentNumber = consignmentNumber;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getPieces() {
        return pieces;
    }

    public void setPieces(int pieces) {
        this.pieces = pieces;
    }

    public String getTransmitStatus() {
        return transmitStatus;
    }

    public void setTransmitStatus(String transmitStatus) {
        this.transmitStatus = transmitStatus;
    }
}
