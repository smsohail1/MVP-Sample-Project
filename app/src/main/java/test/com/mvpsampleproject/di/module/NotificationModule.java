package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.notification.NotificationMVP;
import com.tcs.pickupapp.ui.notification.NotificationModel;
import com.tcs.pickupapp.ui.notification.NotificationPresenter;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

@Module
public class NotificationModule {

    @Provides
    public NotificationMVP.Presenter provideNotificationPresenter(NotificationMVP.Model model, Utils utils){
        return new NotificationPresenter(model,utils);
    }

    @Provides
    public NotificationMVP.Model provideNotificationModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI){
        return new NotificationModel(pickupAPI);
    }

}
