package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.report_detail.ReportDetailMVP;
import com.tcs.pickupapp.ui.report_detail.ReportDetailModel;
import com.tcs.pickupapp.ui.report_detail.ReportDetailPresenter;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 13, April, 2018
 */
@Module
public class ReportDetailModule {

    @Provides
    public ReportDetailMVP.Presenter provideReportDetailPresenter(ReportDetailMVP.Model model){
        return new ReportDetailPresenter(model);
    }

    @Provides
    public ReportDetailMVP.Model provideReportDetailModel(com.tcs.pickupapp.data.room.AppDatabase appDatabase){
        return new ReportDetailModel(appDatabase);
    }
}
