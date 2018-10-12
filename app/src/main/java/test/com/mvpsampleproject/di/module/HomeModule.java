package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.home.HomeMVP;
import com.tcs.pickupapp.ui.home.HomeModel;
import com.tcs.pickupapp.ui.home.HomePresenter;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 09, April, 2018
 */
@Module
public class HomeModule {

    @Provides
    public HomeMVP.Presenter provideHomePresenter(HomeMVP.Model model, SessionManager sessionManager, Utils utils){
        return new HomePresenter(model,sessionManager,utils);
    }

    @Provides
    public HomeMVP.Model provideHomeModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, com.tcs.pickupapp.data.room.AppDatabase appDatabase){
        return new HomeModel(pickupAPI,appDatabase);
    }
}









