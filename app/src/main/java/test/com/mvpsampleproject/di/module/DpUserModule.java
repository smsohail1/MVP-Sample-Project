package test.com.mvpsampleproject.di.module;


import android.content.Context;

import com.tcs.pickupapp.data.jdbc.JDBCApi;
import com.tcs.pickupapp.ui.dpuser.DpUserMVP;
import com.tcs.pickupapp.ui.dpuser.DpUserModel;
import com.tcs.pickupapp.ui.dpuser.DpUserPresenter;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by muhammad.sohail on 4/5/2018.
 */

@Module
public class DpUserModule {
//    @Provides
//    public DpUserMVP.Presenter providerDpUserPresenter(DpUserMVP.Model model) {
//        return new DpUserPresenter(model);
//    }

//    @Provides
//    public DpUserMVP.Model providerDpUserMVPModel(AppDatabase appDatabase) {
//        return new DpUserModel(appDatabase);
//    }

    @Provides
    public DpUserMVP.Presenter provideDpUserPresenter(DpUserMVP.Model model,SessionManager sessionManager, Utils utils,ToastUtil toastUtil,Context ctx) {
        return new DpUserPresenter(model,sessionManager,utils,toastUtil,ctx);
    }

    @Provides
    public DpUserMVP.Model provideDpUserModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase, JDBCApi jdbcApi, Utils utils, SessionManager sessionManager) {
        return new DpUserModel(appDatabase,jdbcApi,utils,sessionManager);
    }
}

