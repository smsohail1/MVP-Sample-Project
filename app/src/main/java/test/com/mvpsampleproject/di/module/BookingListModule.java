package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.bookinglist.BookingListMVP;
import com.tcs.pickupapp.ui.bookinglist.BookingListModel;
import com.tcs.pickupapp.ui.bookinglist.BookingListPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by umair.irshad on 4/4/2018.
 */
@Module
public class BookingListModule {

    @Provides
    public BookingListMVP.Presenter providesBookingListPresenter(BookingListMVP.Model model){
        return new BookingListPresenter(model);
    }

    @Provides
    public BookingListMVP.Model providesBookingModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase){
        return new BookingListModel(appDatabase);
    }
}
