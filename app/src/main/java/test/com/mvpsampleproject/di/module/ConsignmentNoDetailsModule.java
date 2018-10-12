package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.consigment_no_details.ConsignmentNoDetailsMVP;
import com.tcs.pickupapp.ui.consigment_no_details.ConsignmentNoDetailsModel;
import com.tcs.pickupapp.ui.consigment_no_details.ConsignmentNoDetailsPresenter;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by muhammad.sohail on 5/17/2018.
 */
@Module
public class ConsignmentNoDetailsModule {
    @Provides
    public ConsignmentNoDetailsMVP.Presenter providesConsignmentNoDetailsPresenter(ConsignmentNoDetailsMVP.Model model, Utils utils) {
        return new ConsignmentNoDetailsPresenter(model, utils);
    }

    @Provides
    public ConsignmentNoDetailsMVP.Model providesConsignmentNoDetailsModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI) {
        return new ConsignmentNoDetailsModel(pickupAPI);
    }
}
