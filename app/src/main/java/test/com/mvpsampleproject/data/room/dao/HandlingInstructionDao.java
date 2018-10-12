package com.tcs.pickupapp.data.room.dao;


import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by muhammad.sohail on 4/3/2018.
 */
@Dao
public interface HandlingInstructionDao {

    @Insert
    void insert( List<HandlingInstruction> handlingInstruction);

    @Update
    void update(HandlingInstruction handlingInstruction);

    @Delete
    void delete(HandlingInstruction handlingInstruction);

    @Query("delete from handling_instruction where handling_instruction_id = :id")
    void delete(int id);

    @Query("DELETE FROM handling_instruction")
    void deleteAll();

    @Query("Select * from handling_instruction")
    List<HandlingInstruction> getAllHandlingInstruction();

    @Query("Select * from handling_instruction where handling_instruction_id=:id")
    HandlingInstruction getRoomById(int id);

    @Query("Select count(*) from handling_instruction")
    int getHandlingInstructionsCount();

}
