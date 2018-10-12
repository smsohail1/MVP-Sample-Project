package test.com.mvpsampleproject.util;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.tcs.pickupapp.R;


/**
 * Created by shahrukh.malik on 3/13/2018.
 */

public class ProgressCustomDialogController {
    private Dialog dialog;
    private Context context;
    private Utils utils;

    public ProgressCustomDialogController(Context context, int mesgRes){
        this.context = context;
        this.dialog = new Dialog(context);
        this.dialog.setCancelable(false);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setContentView(R.layout.layout_dialog_av_loader_with_text);
        this.utils = new Utils(context);
        ((TextView)dialog.findViewById(R.id.txtMesg)).setText(utils.getStringFromResourceId(mesgRes));
    }

    public void showDialog(){
        this.dialog.show();
    }

    public void hideDialog(){
        this.dialog.dismiss();
    }
}

