package com.tcs.pickupapp.data.room.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.tcs.pickupapp.data.room.model.Service;

import java.util.List;

/**
 * Created by muhammad.sohail on 4/3/2018.
 */
@Dao
public interface ServiceDao {
    @Insert
    void insert(List<Service> service);

    @Update
    void update(Service service);

    @Delete
    void delete(Service service);

    @Query("delete from service where service_id = :id")
    void delete(int id);

    @Query("Select * from service")
    List<Service> getAllService();

    @Query("DELETE FROM service")
    void deleteAll();

    @Query("Select * from service where service_id=:id")
    Service getRoomById(int id);

    @Query("Select count(*) from service")
    int getServicesCount();

    @Query("Select * from service where product_number=:product")
    List<Service> getServicesByProduct(String product);


    @Query("Select * from service where product_number=:product1 or product_number=:product2")
    List<Service> getServicesHavingMoreProduct(String product1, String product2);

}
