package test.com.mvpsampleproject.di.module;

import android.content.Context;

import com.tcs.pickupapp.util.LogUtil;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.SnackUtil;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
@Module
public class UtilModule {
    @Singleton
    @Provides
    public ToastUtil provideToastUtil(Context context){
        return new ToastUtil(context);
    }

    @Singleton
    @Provides
    public SnackUtil provideSnackUtil(Context context){
        return new SnackUtil(context);
    }

    @Singleton
    @Provides
    public Utils provideUtils(Context context){
        return new Utils(context);
    }

    @Singleton
    @Provides
    public LogUtil provideLogUtil(){
        return new LogUtil();
    }

    @Singleton
    @Provides
    public SessionManager provideSessionManager(Context context){
        return new SessionManager(context);
    }
}
