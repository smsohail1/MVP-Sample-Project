package test.com.mvpsampleproject.di.module;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.tcs.pickupapp.util.AppConstants;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
@Module
public class RoomModule {

    @Provides
    @Singleton
    public com.tcs.pickupapp.data.room.AppDatabase provideAppDatabase(Context context){
        return Room.databaseBuilder(context, com.tcs.pickupapp.data.room.AppDatabase.class, AppConstants.DATABASE_NAME)
                .allowMainThreadQueries() //temporary, should be done on seperate thread
                .build();
    }
}







