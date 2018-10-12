package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.courier_journey.CourierJourneyMVP;
import com.tcs.pickupapp.ui.courier_journey.CourierJourneyModel;
import com.tcs.pickupapp.ui.courier_journey.CourierJourneyPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 08, May, 2018
 */
@Module
public class CourierJourneyModule {

    @Provides
    public CourierJourneyMVP.Presenter providesCourierJourneyPresenter(CourierJourneyMVP.Model model){
        return new CourierJourneyPresenter(model);
    }

    @Provides
    public CourierJourneyMVP.Model providesCourierJourneyModel(){
        return new CourierJourneyModel();
    }
}












