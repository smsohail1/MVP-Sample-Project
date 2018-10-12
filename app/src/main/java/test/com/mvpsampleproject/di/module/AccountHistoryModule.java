package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.account_history.AccountHistoryMVP;
import com.tcs.pickupapp.ui.account_history.AccountHistoryModel;
import com.tcs.pickupapp.ui.account_history.AccountHistoryPresenter;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by muhammad.sohail on 5/7/2018.
 */
@Module
public class AccountHistoryModule {
    @Provides
    public AccountHistoryMVP.Presenter provideAccountDetailsPresenter(AccountHistoryMVP.Model model, SessionManager sessionManager, Utils utils) {
        return new AccountHistoryPresenter(model, sessionManager, utils);
    }

    @Provides
    public AccountHistoryMVP.Model provideDpUserModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI, com.tcs.pickupapp.data.room.AppDatabase appDatabase) {
        return new AccountHistoryModel(pickupAPI,appDatabase);
    }
}
