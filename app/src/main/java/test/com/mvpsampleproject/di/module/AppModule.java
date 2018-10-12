package test.com.mvpsampleproject.di.module;

import android.app.Application;
import android.content.Context;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
@Module
public class AppModule {
    private Application application;

    public AppModule(Application application){
        this.application = application;
    }

    @Provides
    public Context provideContext(){
        return application;
    }

}
