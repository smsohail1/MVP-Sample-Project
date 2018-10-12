package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.feedback.FeedbackMVP;
import com.tcs.pickupapp.ui.feedback.FeedbackModel;
import com.tcs.pickupapp.ui.feedback.FeedbackPresenter;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by umair.irshad on 4/4/2018.
 */
@Module
public class FeedbackModule {

    @Provides
    public FeedbackMVP.Presenter providesBookingPresenter(FeedbackMVP.Model model, Utils utils){
        return new FeedbackPresenter(model,utils);
    }

    @Provides
    public FeedbackMVP.Model providesBookingModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase, com.tcs.pickupapp.data.rest.PickupAPI pickupAPI){
        return new FeedbackModel(appDatabase,pickupAPI);
    }
}
