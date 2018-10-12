package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.customer_ack_email.CustomerAckMVP;
import com.tcs.pickupapp.ui.customer_ack_email.CustomerAckModel;
import com.tcs.pickupapp.ui.customer_ack_email.CustomerAckPresenter;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by umair.irshad on 4/6/2018.
 */
@Module
public class CustomerAckModule {

    @Provides
    public CustomerAckMVP.Presenter provideCustomerAckPresenter(CustomerAckMVP.Model model, SessionManager sessionManager){
        return new CustomerAckPresenter(model,sessionManager);
    }

    @Provides
    public CustomerAckMVP.Model provideCustomerAckModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase, com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, Utils utils){
        return new CustomerAckModel(appDatabase, pickupAPI, utils);
    }

}
