package test.com.mvpsampleproject.util;

import android.util.Log;

/**
 * Created by umair.irshad on 4/9/2018.
 */

public class LogUtil {

    public void AppLog_d(String tag, String message){
        Log.d(tag,message);
    }

    public void AppLog_v(String tag, String message){
        Log.v(tag,message);
    }

    public void AppLog_e(String tag, String message){
        Log.e(tag,message);
    }

}
