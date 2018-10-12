package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.report.ReportMVP;
import com.tcs.pickupapp.ui.report.ReportModel;
import com.tcs.pickupapp.ui.report.ReportPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
@Module
public class ReportModule {

    @Provides
    public ReportMVP.Presenter provideReportPresenter(ReportMVP.Model model){
        return new ReportPresenter(model);
    }

    @Provides
    public ReportMVP.Model provideReportModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase){
        return new ReportModel(appDatabase);
    }
}
