package test.com.mvpsampleproject.di.component;

import com.tcs.pickupapp.di.module.AccountHistoryModule;
import com.tcs.pickupapp.di.module.AppModule;
import com.tcs.pickupapp.di.module.BookingListModule;
import com.tcs.pickupapp.di.module.BookingModule;
import com.tcs.pickupapp.di.module.ConsignmentNoDetailsModule;
import com.tcs.pickupapp.di.module.CourierJourneyModule;
import com.tcs.pickupapp.di.module.CustomerAckModule;
import com.tcs.pickupapp.di.module.DeletedCNSequenceModule;
import com.tcs.pickupapp.di.module.DimensionModule;
import com.tcs.pickupapp.di.module.DpUserModule;
import com.tcs.pickupapp.di.module.ErrorReportModule;
import com.tcs.pickupapp.di.module.FeedbackModule;
import com.tcs.pickupapp.di.module.GenerateCNSequenceModule;
import com.tcs.pickupapp.di.module.HomeModule;
import com.tcs.pickupapp.di.module.JDBCModule;
import com.tcs.pickupapp.di.module.LoginModule;
import com.tcs.pickupapp.di.module.NotificationModule;
import com.tcs.pickupapp.di.module.ReportDetailModule;
import com.tcs.pickupapp.di.module.ReportModule;
import com.tcs.pickupapp.di.module.RetakeReportModule;
import com.tcs.pickupapp.di.module.RetrofitModule;
import com.tcs.pickupapp.di.module.RoomModule;
import com.tcs.pickupapp.di.module.RouteNavigatorModule;
import com.tcs.pickupapp.di.module.RouteSelectionModule;
import com.tcs.pickupapp.di.module.SettingModule;
import com.tcs.pickupapp.di.module.StatisticsModule;
import com.tcs.pickupapp.di.module.TransitTimeCalculatorModule;
import com.tcs.pickupapp.di.module.TransitTimeCalculatorStationModule;
import com.tcs.pickupapp.di.module.TtcUnscheduledPickupModule;
import com.tcs.pickupapp.di.module.UtilModule;
import com.tcs.pickupapp.di.module.WebServiceFactoryModule;
import com.tcs.pickupapp.ui.BaseActivity;
import com.tcs.pickupapp.ui.account_history.AccountHistoryFragment;
import com.tcs.pickupapp.ui.booking.BookingFragment;
import com.tcs.pickupapp.ui.booking.cameramodule.BarcodeCaptureActivity;
import com.tcs.pickupapp.ui.booking.cameramodule.CaptureCameraImageFragment;
import com.tcs.pickupapp.ui.booking.service.BookingService;
import com.tcs.pickupapp.ui.bookinglist.BookingListFragment;
import com.tcs.pickupapp.ui.consigment_no_details.ConsignmentNoDetailsFragment;
import com.tcs.pickupapp.ui.courier_journey.CourierJourneyFragment;
import com.tcs.pickupapp.ui.courier_journey.service.GPSTrackerService;
import com.tcs.pickupapp.ui.courier_journey.service.GPSTrackerServiceShahrukh;
import com.tcs.pickupapp.ui.customer_ack_email.CustomerAckFragment;
import com.tcs.pickupapp.ui.delete_cn_sequence.DeleteCNSequenceFragment;
import com.tcs.pickupapp.ui.dimension.DimensionFragment;
import com.tcs.pickupapp.ui.dpuser.DpUserFragment;
import com.tcs.pickupapp.ui.error_report.ErrorReportFragment;
import com.tcs.pickupapp.ui.feedback.FeedbackFragment;
import com.tcs.pickupapp.ui.generatecnsequence.GenerateCNSequenceFragment;
import com.tcs.pickupapp.ui.home.HomeFragment;
import com.tcs.pickupapp.ui.login.LoginFragment;
import com.tcs.pickupapp.ui.notification.NotificationFragment;
import com.tcs.pickupapp.ui.report.ReportFragment;
import com.tcs.pickupapp.ui.report_detail.ReportDetailFragment;
import com.tcs.pickupapp.ui.retake_report.RetakeReportFragment;
import com.tcs.pickupapp.ui.route_navigator.RouteNavigatorFragment;
import com.tcs.pickupapp.ui.route_selection.RouteSelectionFragment;
import com.tcs.pickupapp.ui.setting.SettingFragment;
import com.tcs.pickupapp.ui.statistics.StatisticsFragment;
import com.tcs.pickupapp.ui.ttc_unscheduled.TariffAndTransitCalculatorWebViewFragment;
import com.tcs.pickupapp.ui.ttc_unscheduled.TtcUnscheduledPickupFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
@Component(modules = {
        AppModule.class,
        UtilModule.class,
        RetrofitModule.class,
        RoomModule.class,
        ReportModule.class,
        ReportDetailModule.class,
        ErrorReportModule.class,
        DimensionModule.class,
        GenerateCNSequenceModule.class,
        DeletedCNSequenceModule.class,
        BookingModule.class,
        HomeModule.class,
        LoginModule.class,
        CustomerAckModule.class,
//        DpUserModule.class,
        BookingListModule.class,
        DpUserModule.class,
        FeedbackModule.class,
        NotificationModule.class,
        SettingModule.class,
        StatisticsModule.class,
        AccountHistoryModule.class,
        CourierJourneyModule.class,
        TtcUnscheduledPickupModule.class,
        JDBCModule.class,
        TransitTimeCalculatorModule.class,
        TransitTimeCalculatorStationModule.class,
        WebServiceFactoryModule.class,
        RouteNavigatorModule.class,
        ConsignmentNoDetailsModule.class,
        RouteSelectionModule.class,
        RetakeReportModule.class
})
@Singleton
public interface AppComponent {

    void inject(ReportFragment reportFragment);

    void inject(ReportDetailFragment reportDetailFragment);

    void inject(ErrorReportFragment errorReportFragment);

    void inject(DimensionFragment dimensionFragment);

    void inject(GenerateCNSequenceFragment generateCNSequenceFragment);

    void inject(DeleteCNSequenceFragment deleteCNSequenceFragment);

    void inject(BookingFragment bookingFragment);

    void inject(HomeFragment homeFragment);

    void inject(LoginFragment loginFragment);

    void inject(CustomerAckFragment customerAckFragment);
    void inject(RetakeReportFragment retakeReportFragment);

}




