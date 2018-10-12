package com.tcs.pickupapp.data.room.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

/**
 * Created by muhammad.sohail on 5/11/2018.
 */

@Entity(tableName = "station")
public class Station {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "station_id_auto")
    private int stationIdAuto;
    @ColumnInfo(name = "station_Id")
    private String stationId;
    @ColumnInfo(name = "country_Code")
    private String countryCode;
    @ColumnInfo(name = "description")
    private String description;
    @ColumnInfo(name = "origin_type")
    private String originType;
    @ColumnInfo(name = "product_type")
    private String productType;

    public Station(String stationId, String countryCode, String description,String originType,String productType) {
        this.stationId = stationId;
        this.countryCode = countryCode;
        this.description = description;
        this.originType = originType;
        this.productType = productType;
    }


    public int getStationIdAuto() {
        return stationIdAuto;
    }

    public void setStationIdAuto(int stationIdAuto) {
        this.stationIdAuto = stationIdAuto;
    }

    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getOriginType() {
        return originType;
    }

    public void setOriginType(String originType) {
        this.originType = originType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }
}
