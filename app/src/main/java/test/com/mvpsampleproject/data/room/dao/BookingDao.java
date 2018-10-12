package com.tcs.pickupapp.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.tcs.pickupapp.data.room.model.Booking;
import com.tcs.pickupapp.data.room.model.ReportDetail;
import com.tcs.pickupapp.ui.courier_journey.model.VisitedCustomerDetail;


import java.util.List;

/**
 * Created by umair.irshad on 4/4/2018.
 */
@Dao
public interface BookingDao {

    @Insert
    void insert(Booking booking);

    @Update
    void update(Booking booking);

    @Delete
    void delete(Booking booking);

    @Query("SELECT DISTINCT(customer_number), customer_name, count(cn_number) as total_cns, sum(weight) as total_weight, sum(pieces) as total_pieces, sum(case when transmit_status = 'T' then 1 else 0 end) as total_transmitted, sum(case when transmit_status = 'NT' or transmit_status = 'IP' then 1 else 0 end) as total_pending FROM booking group by customer_number")
    List<GroupedReport> getGroupedReports();

    @Query("SELECT customer_ref,cn_number,weight,pieces,transmit_status FROM booking WHERE customer_name=:customerName and customer_number=:customerNumber GROUP by cn_number")
    List<ReportDetail> getReportDetail(String customerName, String customerNumber);

    @Query("SELECt * FROM booking")
    List<Booking> getAckBookings();

    @Query("SELECt * FROM booking")
    List<Booking> getAllBookings();

    @Query("SELECT b.customer_number,b.customer_name,((julianday((select bd.created_date from booking bd where bd.customer_number = b.customer_number order by bd.created_date DESC LIMIT 1)) - julianday((select ba.created_date from booking ba where ba.customer_number = b.customer_number order by ba.created_date ASC LIMIT 1))) * 24 * 60 * 60) as time_spent,count(b.cn_number) as total_cns,b.latitude,b.longitude FROM booking b where b.latitude <> '' and b.longitude <> '' group by b.customer_number")
    List<JourneyStats> getAllBookingsForJourneyStats();

    @Query("SELECt * FROM booking where customer_number=:customer_number ORDER BY customer_number DESC")
    List<Booking> getAccountBookings(String customer_number);


    @Query("SELECt *  FROM booking where customer_number=:customer_number and isRetake=:reTake and is_save = '0' group by cn_number ORDER BY customer_number DESC")
    List<Booking> getRetakeAccountBookings(String customer_number, int reTake);

    @Query("SELECt  * FROM booking where customer_number=:customer_number and isRetake=:reTake ORDER BY customer_number DESC")
    List<Booking> getBarcodeAccountBookings(String customer_number, int reTake);


    @Query("SELECt  * FROM booking where customer_number=:customer_number and isRetake=:reTake  and is_save = '0' ORDER BY customer_number DESC")
    List<Booking> getAccountBookings(String customer_number, int reTake);


    @Query("Select * from booking where transmit_status=:transmissionStatus")
    List<Booking> getBookingByTransmissionStatus(String transmissionStatus);

    @Query("Select * from booking where transmit_status=\'NT\' ORDER BY booking_id ASC LIMIT 1")
    Booking getNotTransmitedBooking();

    @Query("Select * from booking where transmit_status=\'NT\' and no_of_attempts < 6")
    List<Booking> getNTBooking();

    @Query("Select * from booking where transmit_status=\'NT\' and no_of_attempts < 6 LIMIT 1")
    Booking getNT();

    @Query("SELECt * FROM booking where cn_number=:cnNumber")
    Booking getBooking(String cnNumber);

    @Query("SELECt * FROM booking where cn_number=:cnNumber")
    List<Booking> getBookingsByCNNumber(String cnNumber);

    @Query("SELECt * FROM booking where cn_number between :fromCn and :toCn")
    List<Booking> getBookingByToAndFomCn(String fromCn, String toCn);

    @Query("SELECt * FROM booking where customer_number=:customerNumber")
    List<Booking> getBookingByAccount(String customerNumber);


    @Query("SELECt * FROM booking where customer_number=:customerNumber and isRetake=:reTake and is_save = '0' group by cn_number")
    List<Booking> getRetakeBookingByAccount(String customerNumber, int reTake);

    @Query("SELECt  * FROM booking where customer_number=:customerNumber and isRetake=:reTake  and is_save = '0'")
    List<Booking> getBookingByAccount(String customerNumber, int reTake);


    @Query("select * from booking where customer_number =:customerNumber order by created_date DESC LIMIT 1")
    Booking getBookingByCustomerNumberDesc(String customerNumber);

    @Query("select * from booking where customer_number =:customerNumber order by created_date ASC LIMIT 1")
    Booking getBookingByCustomerNumberAsc(String customerNumber);

    @Query("DELETE from booking where customer_number=:customerNumber")
    void deleteBookingsByCustomerNumber(String customerNumber);

    @Query("DELETE from booking where cn_number=:consignmentNumber")
    void deleteBookingsByConsignmentNumber(String consignmentNumber);

    @Query("Select * from booking where transmit_status=\'NT\'")
    List<Booking> getAllNTBookings();

    @Query("DELETE FROM booking")
    void deleteAll();

    @Query("SELECt DISTINCT customer_number FROM booking")
    List<String> getDistinctAccount();

    @Query("SELECt * FROM booking where customer_number=:customerNumber  ORDER BY created_date")
    List<Booking> getBookingsGroupByCustomerNumber(String customerNumber);

    @Query("SELECt * FROM booking where isRetake=:reTake")
    List<Booking> getBookingByRetake(int reTake);


    @Query("SELECt * FROM booking where isRetake=:isRetakee and is_save=:isSave ORDER BY created_date DESC")
    List<Booking> getLoadPendingPickups(String isSave, int isRetakee);


    @Query("UPDATE booking SET is_save=:isSave WHERE customer_number=:customerNo")
    void updateCustomerPendingStatus(String isSave, String customerNo);

}
