package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.retake_report.RetakeReportMVP;
import com.tcs.pickupapp.ui.retake_report.RetakeReportModel;
import com.tcs.pickupapp.ui.retake_report.RetakeReportPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 14, June, 2018
 */
@Module
public class RetakeReportModule {

    @Provides
    public RetakeReportMVP.Presenter provideRetakeReportPresenter(RetakeReportMVP.Model model){
        return new RetakeReportPresenter(model);
    }

    @Provides
    public RetakeReportMVP.Model provideRetakeReportModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase){
        return new RetakeReportModel(appDatabase);
    }
}
