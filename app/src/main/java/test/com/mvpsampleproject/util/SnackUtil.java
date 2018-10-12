package test.com.mvpsampleproject.util;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
public class SnackUtil {
    private Context context;

    public SnackUtil(Context context){
        this.context = context;
    }

    public void showSnackBarShortTime(View view, int messageResource)
    {
        if(context != null) {
            Snackbar.make(view,
                    messageResource,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    public void showSnackBarLongTime(View view, int messageResource)
    {
        if(context != null) {
            Snackbar.make(view,
                    messageResource,
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }

    public void showSnackBarShortTime(View view, String message)
    {
        if(context != null) {
            Snackbar.make(view,
                    message,
                    Snackbar.LENGTH_SHORT)
                    .show();
        }
    }

    public void showSnackBarLongTime(View view, String message)
    {
        if(context != null) {
            Snackbar.make(view,
                    message,
                    Snackbar.LENGTH_LONG)
                    .show();
        }
    }
}
