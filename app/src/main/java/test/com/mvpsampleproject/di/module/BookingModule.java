package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.booking.BookingMVP;
import com.tcs.pickupapp.ui.booking.BookingModel;
import com.tcs.pickupapp.ui.booking.BookingPresenter;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by umair.irshad on 4/4/2018.
 */
@Module
public class BookingModule {

    @Provides
    public BookingMVP.Presenter providesBookingPresenter(BookingMVP.Model model,SessionManager sessionManager){
        return new BookingPresenter(model,sessionManager);
    }

    @Provides
    public BookingMVP.Model providesBookingModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase, com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, SessionManager sessionManager, Utils util){
        return new BookingModel(appDatabase,pickupAPI,sessionManager,util);
    }
}
