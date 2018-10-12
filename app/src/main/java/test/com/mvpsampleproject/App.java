package test.com.mvpsampleproject;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.tcs.pickupapp.di.component.AppComponent;
import com.tcs.pickupapp.di.component.DaggerAppComponent;
import com.tcs.pickupapp.di.module.AccountHistoryModule;
import com.tcs.pickupapp.di.module.AppModule;
import com.tcs.pickupapp.di.module.BookingListModule;
import com.tcs.pickupapp.di.module.BookingModule;
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
import com.tcs.pickupapp.di.module.RetrofitModule;
import com.tcs.pickupapp.di.module.RoomModule;
import com.tcs.pickupapp.di.module.RouteNavigatorModule;
import com.tcs.pickupapp.di.module.RouteSelectionModule;
import com.tcs.pickupapp.di.module.SettingModule;
import com.tcs.pickupapp.di.module.TransitTimeCalculatorModule;
import com.tcs.pickupapp.di.module.TransitTimeCalculatorStationModule;
import com.tcs.pickupapp.di.module.TtcUnscheduledPickupModule;
import com.tcs.pickupapp.di.module.UtilModule;
import com.tcs.pickupapp.di.module.WebServiceFactoryModule;

import io.fabric.sdk.android.Fabric;

/**
 * Created by abdul.ahad on 30-Mar-18.
 */

public class App extends Application {
    private AppComponent appComponent;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        appComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .utilModule(new UtilModule())
                .retrofitModule(new RetrofitModule())
                .roomModule(new RoomModule())
                .reportModule(new ReportModule())
                .reportDetailModule(new ReportDetailModule())
                .errorReportModule(new ErrorReportModule())
                .dimensionModule(new DimensionModule())
                .generateCNSequenceModule(new GenerateCNSequenceModule())
                .deletedCNSequenceModule(new DeletedCNSequenceModule())
                .bookingModule(new BookingModule())
                .homeModule(new HomeModule())
                .loginModule(new LoginModule())
                .customerAckModule(new CustomerAckModule())
                .bookingListModule(new BookingListModule())
                .dpUserModule(new DpUserModule())
                .feedbackModule(new FeedbackModule())
                .settingModule(new SettingModule())
                .webServiceFactoryModule(new WebServiceFactoryModule())

                .build();
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

}






