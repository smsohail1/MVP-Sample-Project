package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.login.LoginMVP;
import com.tcs.pickupapp.ui.login.LoginModel;
import com.tcs.pickupapp.ui.login.LoginPresenter;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by muhammad.sohail on 4/3/2018.
 */
@Module
public class LoginModule {

    @Provides
    public LoginMVP.Presenter provideLoginPresenter(LoginMVP.Model model, SessionManager sessionManager, Utils utils) {
        return new LoginPresenter(model, sessionManager, utils);
    }

    @Provides
    public LoginMVP.Model provideLoginModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, com.tcs.pickupapp.data.room.AppDatabase appDatabase, SessionManager sessionManager) {
        return new LoginModel(pickupAPI, appDatabase,sessionManager);
    }
}
