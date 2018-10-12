package com.tcs.pickupapp.data.room.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import java.util.Arrays;

import io.reactivex.annotations.NonNull;

/**
 * Created by umair.irshad on 4/4/2018.
 */
@Entity(tableName = "booking")
public class Booking {

    @PrimaryKey(autoGenerate = true)
    @NonNull
    @ColumnInfo(name = "booking_id")
    private int bookingId;

    @ColumnInfo(name = "customer_name")
    private String customerName;
    @ColumnInfo(name = "customer_number")
    private String customerNumber;

    @ColumnInfo(name = "handling_instruction")
    private String handlingInstruction;
    @ColumnInfo(name = "handling_instruction_full_text")
    private String handlingInstructionFullText;
    @ColumnInfo(name = "cn_number")
    private String cnNumber;
    @ColumnInfo(name = "cn_type")
    private String cnType;
    @ColumnInfo(name = "created_date")
    private String createdDate;
    @ColumnInfo(name = "latitude")
    private String latitude;
    @ColumnInfo(name = "longitude")
    private String longitude;
    @ColumnInfo(name = "pieces")
    private String pieces;
    @ColumnInfo(name = "weight")
    private String weight;
    @ColumnInfo(name = "service_number")
    private String serviceNumber;
    @ColumnInfo(name = "service_name")
    private String serviceName;
    @ColumnInfo(name = "payment_mode")
    private String paymentMode;
    @ColumnInfo(name = "declared_value")
    private String declaredValue;
    @ColumnInfo(name = "shipper_name")
    private String shipperName;
    @ColumnInfo(name = "courier_code")
    private String courierCode;
    @ColumnInfo(name = "product")
    private String product;
    @ColumnInfo(name = "route")
    private String route;
    @ColumnInfo(name = "origin_station")
    private String originStation;

    @ColumnInfo(name = "other_charges")
    private String otherCharges;
    @ColumnInfo(name = "image")
    private byte[] image;
    @ColumnInfo(name = "imei")
    private String imei;
    @ColumnInfo(name = "transmit_status")
    private String transmitStatus;

    @ColumnInfo(name = "customer_ref")
    private String customerRef;

    @ColumnInfo(name = "no_of_attempts")
    private int noOfAttempts;

    @ColumnInfo(name = "dimensions")
    private String dimensions;

    @ColumnInfo(name = "isRetake")
    private int isRetake;

    @ColumnInfo(name = "is_save")
    private String isSave;

    private String dimensionWeight;
    private String defVal;
    private String toAddress;
    private String fromAddress;
    private String toName;
    private String fromName;
    private String toEmail;
    private String fromEmail;
    private String toPhone;
    private String fromPhone;

    @Ignore
    public Booking(String customerNumber, String customerName, String transmitStatus) {
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.transmitStatus = transmitStatus;
    }

    @Ignore
    public Booking(
            String customerName,
            String customerNumber,
            String handlingInstruction,
            String handlingInstructionFullText,
            String cnNumber, String cnType,
            String createdDate, String latitude,
            String longitude, String pieces,
            String weight, String serviceNumber, String serviceName,
            String paymentMode, String declaredValue,
            String shipperName, String courierCode,
            String product, String route,
            String originStation, String otherCharges,
            byte[] image, String imei, String transmitStatus,
            String customerRef, int noOfAttempts,
            int isRetake, String dimensionWeight,
            String defVal, String toAddress,
            String fromAddress, String toName,
            String fromName, String toEmail,
            String fromEmail, String toPhone, String fromPhone, String isSave) {
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.handlingInstruction = handlingInstruction;
        this.handlingInstructionFullText = handlingInstructionFullText;
        this.cnNumber = cnNumber;
        this.cnType = cnType;
        this.createdDate = createdDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pieces = pieces;
        this.weight = weight;
        this.serviceNumber = serviceNumber;
        this.serviceName = serviceName;
        this.paymentMode = paymentMode;
        this.declaredValue = declaredValue;
        this.shipperName = shipperName;
        this.courierCode = courierCode;
        this.product = product;
        this.route = route;
        this.originStation = originStation;
        this.otherCharges = otherCharges;
        this.image = image;
        this.imei = imei;
        this.transmitStatus = transmitStatus;
        this.customerRef = customerRef;
        this.noOfAttempts = noOfAttempts;
        this.isRetake = isRetake;
        this.dimensionWeight = dimensionWeight;
        this.defVal = defVal;
        this.toAddress = toAddress;
        this.fromAddress = fromAddress;
        this.toName = toName;
        this.fromName = fromName;
        this.toEmail = toEmail;
        this.fromEmail = fromEmail;
        this.toPhone = toPhone;
        this.fromPhone = fromPhone;
        this.isSave= isSave;
    }

    public Booking() {

    }

    public Booking(String customerName,
                   String customerNumber,
                   String handlingInstruction,
                   String handlingInstructionFullText,
                   String cnNumber,
                   String cnType,
                   String createdDate,
                   String latitude,
                   String longitude,
                   String pieces,
                   String weight,
                   String serviceNumber,
                   String paymentMode,
                   String declaredValue,
                   String shipperName,
                   String courierCode,
                   String product,
                   String route,
                   String originStation,
                   String otherCharges,
                   byte[] image,
                   String imei,
                   String transmitStatus,
                   String customerRef,
                   int isRetake,
                   int noOfAttempts,
                   String dimensions, String isSave) {
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.handlingInstruction = handlingInstruction;
        this.handlingInstructionFullText = handlingInstructionFullText;
        this.cnNumber = cnNumber;
        this.cnType = cnType;
        this.createdDate = createdDate;
        this.latitude = latitude;
        this.longitude = longitude;
        this.pieces = pieces;
        this.weight = weight;
        this.serviceNumber = serviceNumber;
        this.paymentMode = paymentMode;
        this.declaredValue = declaredValue;
        this.shipperName = shipperName;
        this.courierCode = courierCode;
        this.product = product;
        this.route = route;
        this.originStation = originStation;
        this.otherCharges = otherCharges;
        this.image = image;
        this.imei = imei;
        this.transmitStatus = transmitStatus;
        this.customerRef = customerRef;
        this.isRetake = isRetake;
        this.noOfAttempts = noOfAttempts;
        this.dimensions = dimensions;
        this.isSave = isSave;
    }

    //For bulk booking
    @Ignore
    public void BookingData(String cnNumber, String latitude, String longitude) {
        this.cnNumber = cnNumber;
        this.cnType = "B";
        this.transmitStatus = "NT";
        this.latitude = latitude;
        this.longitude = longitude;
    }


    public String getDimensions() {
        return dimensions;
    }

    public void setDimensions(String dimensions) {
        this.dimensions = dimensions;
    }

    public int getNoOfAttempts() {
        return noOfAttempts;
    }

    public void setNoOfAttempts(int noOfAttempts) {
        this.noOfAttempts = noOfAttempts;
    }

    public String getCustomerRef() {
        return customerRef;
    }

    public void setCustomerRef(String customerRef) {
        this.customerRef = customerRef;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

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

    public String getHandlingInstructionFullText() {
        return handlingInstructionFullText;
    }

    public void setHandlingInstructionFullText(String handlingInstructionFullText) {
        this.handlingInstructionFullText = handlingInstructionFullText;
    }

    public String getCnNumber() {
        return cnNumber;
    }

    public void setCnNumber(String cnNumber) {
        this.cnNumber = cnNumber;
    }

    public String getCnType() {
        return cnType;
    }

    public void setCnType(String cnType) {
        this.cnType = cnType;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
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

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
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

    public String getDeclaredValue() {
        return declaredValue;
    }

    public void setDeclaredValue(String declaredValue) {
        this.declaredValue = declaredValue;
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

    public int getIsRetake() {
        return isRetake;
    }

    public void setIsRetake(int isRetake) {
        this.isRetake = isRetake;
    }

    public String getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(String otherCharges) {
        this.otherCharges = otherCharges;
    }

    public byte[] getImage() {
        return image;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getTransmitStatus() {
        return transmitStatus;
    }

    public void setTransmitStatus(String transmitStatus) {
        this.transmitStatus = transmitStatus;
    }

    public String getDimensionWeight() {
        return dimensionWeight;
    }

    public void setDimensionWeight(String dimensionWeight) {
        this.dimensionWeight = dimensionWeight;
    }

    public String getDefVal() {
        return defVal;
    }

    public void setDefVal(String defVal) {
        this.defVal = defVal;
    }

    public String getToAddress() {
        return toAddress;
    }

    public void setToAddress(String toAddress) {
        this.toAddress = toAddress;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public String getToName() {
        return toName;
    }

    public void setToName(String toName) {
        this.toName = toName;
    }

    public String getFromName() {
        return fromName;
    }

    public void setFromName(String fromName) {
        this.fromName = fromName;
    }

    public String getToEmail() {
        return toEmail;
    }

    public void setToEmail(String toEmail) {
        this.toEmail = toEmail;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getToPhone() {
        return toPhone;
    }

    public void setToPhone(String toPhone) {
        this.toPhone = toPhone;
    }

    public String getFromPhone() {
        return fromPhone;
    }

    public void setFromPhone(String fromPhone) {
        this.fromPhone = fromPhone;
    }

    public String getIsSave() {
        return isSave;
    }

    public void setIsSave(String isSave) {
        this.isSave = isSave;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", customerName='" + customerName + '\'' +
                ", customerNumber='" + customerNumber + '\'' +
                ", handlingInstruction='" + handlingInstruction + '\'' +
                ", cnNumber='" + cnNumber + '\'' +
                ", cnType='" + cnType + '\'' +
                ", createdDate='" + createdDate + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                ", pieces='" + pieces + '\'' +
                ", weight='" + weight + '\'' +
                ", serviceNumber='" + serviceNumber + '\'' +
                ", paymentMode='" + paymentMode + '\'' +
                ", declaredValue='" + declaredValue + '\'' +
                ", shipperName='" + shipperName + '\'' +
                ", courierCode='" + courierCode + '\'' +
                ", product='" + product + '\'' +
                ", route='" + route + '\'' +
                ", originStation='" + originStation + '\'' +
                ", otherCharges='" + otherCharges + '\'' +
                ", image=" + Arrays.toString(image) +
                ", imei='" + imei + '\'' +
                ", transmitStatus='" + transmitStatus + '\'' +
                ", customerRef='" + customerRef + '\'' +
                ", noOfAttempts=" + noOfAttempts +
                ", dimensions='" + dimensions + '\'' +
                ", isRetake='" + isRetake + '\'' +
                ", dimensionWeight='" + dimensionWeight + '\'' +
                ", defVal='" + defVal + '\'' +
                ", toAddress='" + toAddress + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", toName='" + toName + '\'' +
                ", fromName='" + fromName + '\'' +
                ", toEmail='" + toEmail + '\'' +
                ", fromEmail='" + fromEmail + '\'' +
                ", toPhone='" + toPhone + '\'' +
                ", fromPhone='" + fromPhone + '\'' +
                '}';
    }
}
