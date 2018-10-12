package test.com.mvpsampleproject.ui.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;

public class HomeActivity extends BaseActivity {

   // private BroadcastReceiver mReceiver;

    @Override
    public int getLayout() {
        return R.layout.activity_base;
    }

    @Override
    public Fragment getFragment() {
//        return new HomeFragment();
        return new HomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // mReceiver = new BatteryBroadcastReceiver();

    }

/*    @Override
    protected void onStart() {
        registerReceiver(mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(mReceiver);
        super.onStop();
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
         //   if(level== 50){
            createBatteryDialog(level);
        //}
        }
    }


    private void createBatteryDialog(int batLevel) {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Alert");
        builder.setMessage(batLevel + "% battery remaining.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }*/


}
