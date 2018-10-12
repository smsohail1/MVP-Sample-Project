package com.tcs.pickupapp.data.room;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.support.annotation.Dimension;

import com.tcs.pickupapp.data.room.dao.BookingDao;
import com.tcs.pickupapp.data.room.dao.CourierInfoDao;
import com.tcs.pickupapp.data.room.dao.HandlingInstructionDao;
import com.tcs.pickupapp.data.room.dao.ServiceDao;
import com.tcs.pickupapp.data.room.dao.StationDao;
import com.tcs.pickupapp.data.room.model.Booking;
import com.tcs.pickupapp.data.room.model.Service;
import com.tcs.pickupapp.data.room.model.Station;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */


@Database(entities = {
        Dimension.class,
        GenerateSequence.class,
        DeletedCNSequence.class,
        Booking.class,
        CustomerInfo.class,
        HandlingInstruction.class,
        Service.class,
        com.tcs.pickupapp.data.rest.response.CourierInfo.class,
        ErrorReport.class,
        Feedback.class,
        CustomerAck.class,
        Station.class}, version = 1)

public abstract class AppDatabase extends RoomDatabase {

    public abstract DimensionDao getDimenstionDao();

    public abstract GenerateSequenceDao getCNSequenceDao();

    public abstract DeletedCNSequenceDao getDeletedCNSequenceDao();



   public abstract StationDao getStationDao();
}










