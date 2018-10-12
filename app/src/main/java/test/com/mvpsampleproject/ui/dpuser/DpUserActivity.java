package test.com.mvpsampleproject.ui.dpuser;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;

/**
 * Created by muhammad.sohail on 4/5/2018.
 */

public class DpUserActivity extends BaseActivity {
    @Override
    public int getLayout() {
        return R.layout.activity_base;
    }

    @Override
    public Fragment getFragment() {
        return new DpUserFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == -1) {
            new AlertDialog.Builder(DpUserActivity.this)
                    .setTitle("Permission Disabled")
                    .setMessage("Please enable the permission in \n  Settings>Applications>Pick Up>Permission")
                    .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getPackageName(), null);
                            intent.setData(uri);
                            startActivityForResult(intent, 102);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();

        }
    }
}
