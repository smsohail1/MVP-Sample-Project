package com.tcs.pickupapp.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.tcs.pickupapp.data.room.model.Station;

import java.util.List;

/**
 * Created by muhammad.sohail on 5/11/2018.
 */
@Dao
public interface StationDao {
    @Insert
    void insert(List<Station> stationList);

    @Update
    void update(Station station);

    @Delete
    void delete(Station station);

    @Query("Select * from station")
    List<Station> getAllStation();

    @Query("DELETE FROM station")
    void deleteAll();


    @Query("Select * from station where product_type=:productID and origin_type=:originType order by description asc")
    List<Station> getStationsByProductAndOriginType(String productID,String originType);

}
