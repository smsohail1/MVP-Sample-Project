package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.error_report.ErrorReportMVP;
import com.tcs.pickupapp.ui.error_report.ErrorReportModel;
import com.tcs.pickupapp.ui.error_report.ErrorReportPresenter;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 16, April, 2018
 */
@Module
public class ErrorReportModule {

    @Provides
    public ErrorReportMVP.Presenter provideErrorReportPresenter(ErrorReportMVP.Model model, SessionManager sessionManager, Utils utils, com.tcs.pickupapp.data.room.AppDatabase appDatabase){
        return new ErrorReportPresenter(model,sessionManager,utils,appDatabase);
    }

    @Provides
    public ErrorReportMVP.Model provideErrorReportModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase, com.tcs.pickupapp.data.rest.PickupAPI pickupAPI){
        return new ErrorReportModel(appDatabase,pickupAPI);
    }

}











