package com.tcs.pickupapp.data.room.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by muhammad.sohail on 4/3/2018.
 */


@Entity(tableName = "service")
public class Service {
    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "service_id")
    private int serviceId;
    @ColumnInfo(name = "number")
    private String number;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "product_number")
    private String productNumber;

    public Service(String number, String description, String productNumber) {
        this.number = number;
        this.description = description;
        this.productNumber = productNumber;
    }

    @NonNull
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(@NonNull int serviceId) {
        this.serviceId = serviceId;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProductNumber() {
        return productNumber;
    }

    public void setProductNumber(String productNumber) {
        this.productNumber = productNumber;
    }


}
