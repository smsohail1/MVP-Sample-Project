package com.tcs.pickupapp.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by muhammad.sohail on 4/9/2018.
 */
@Dao
public interface CourierInfoDao {
    @Insert
    void insert(CourierInfo courierInfo);

    @Update
    void update(CourierInfo courierInfo);

    @Delete
    void delete(CourierInfo courierInfo);

    @Query("delete from courier_info where courier_info_id = :id")
    void delete(int id);

    @Query("Select * from courier_info")
    List<CourierInfo> getAllCourierInfo();

    @Query("Select * from courier_info where courier_info_id=:id")
    CourierInfo getRoomById(int id);


    @Query("DELETE FROM courier_info")
    void deleteAll();
}
