package test.com.mvpsampleproject.di.module;

import com.tcs.pickupapp.ui.route_navigator.RouteNavigatorMVP;
import com.tcs.pickupapp.ui.route_navigator.RouteNavigatorModel;
import com.tcs.pickupapp.ui.route_navigator.RouteNavigatorPresenter;

import dagger.Module;
import dagger.Provides;

@Module
public class RouteNavigatorModule {

    @Provides
    public RouteNavigatorMVP.Presenter provideRouteNavigatorPresenter(RouteNavigatorMVP.Model model){
        return new RouteNavigatorPresenter(model);
    }

    @Provides
    public RouteNavigatorMVP.Model provideRouteNavigatorModel(){
        return new RouteNavigatorModel();
    }

}











