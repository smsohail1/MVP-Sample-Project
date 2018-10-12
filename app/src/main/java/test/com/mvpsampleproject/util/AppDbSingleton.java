package test.com.mvpsampleproject.util;

import android.arch.persistence.room.Room;
import android.content.Context;

/**
 * Created by umair.irshad on 4/16/2018.
 */

public class AppDbSingleton {

    private static AppDbSingleton appDbSingleton;
    private com.tcs.pickupapp.data.room.AppDatabase appDatabase;

    private AppDbSingleton(){}

    public static AppDbSingleton getInstance(){
        if(appDbSingleton == null){
            appDbSingleton = new AppDbSingleton();
        }
        return appDbSingleton;
    }

    public com.tcs.pickupapp.data.room.AppDatabase getAppDatabase(Context context){
        if(appDatabase == null){
            appDatabase =Room.databaseBuilder(context, com.tcs.pickupapp.data.room.AppDatabase.class, AppConstants.DATABASE_NAME)
                    .allowMainThreadQueries() //temporary, should be done on seperate thread
                    .build();
        }
       return appDatabase;
    }

}
