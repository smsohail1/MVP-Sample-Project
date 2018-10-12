package test.com.mvpsampleproject.ui.booking.cameramodule;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.booking.camerahelper.global;

import java.util.ArrayList;
import java.util.List;

public class BarcodeCaptureListActivity extends AppCompatActivity {

    // use a compound button so either checkbox or switch widgets work.

    private ListView listBarcode;
    private RelativeLayout btnNewBarcode, btnBulkBarcode, layoutConsignmentNum;
    private EditText txtConsignmentNum;
    private TextView imgNew, imgBulk, imgConsignmentNum;
    private Typeface fontello;

    public String appPath = null;
    private Intent intent;
    private String ConsignmentNum;

    private int Width_Spec = 2560;
    private int Height_Spec = 1440;

    BitmapFactory.Options options;
    BitmapDrawable bitmapDrawable;
    public static ArrayList<String> barcodeList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.barcode_capture_list);

        listBarcode = (ListView) findViewById(R.id.listBarcode);

        btnNewBarcode = (RelativeLayout) findViewById(R.id.layoutNew);
        btnBulkBarcode = (RelativeLayout) findViewById(R.id.layoutBulk);

        txtConsignmentNum = (EditText) findViewById(R.id.txtConsignmentNum);

        imgNew = (TextView) findViewById(R.id.imgNew);
        imgBulk = (TextView) findViewById(R.id.imgBulk);
        imgConsignmentNum = (TextView) findViewById(R.id.imgConsignmentNum);

        layoutConsignmentNum = (RelativeLayout) findViewById(R.id.layoutConsignmentNum);

        fontello = Typeface.createFromAsset(getAssets(), "fonts/fontello.ttf");
        imgNew.setTypeface(fontello);
        imgBulk.setTypeface(fontello);
        imgConsignmentNum.setTypeface(fontello);

        ConsignmentNum = getIntent().getStringExtra("ConsignmentNum");
        if (ConsignmentNum != null) {
            layoutConsignmentNum.setVisibility(View.VISIBLE);
            layoutConsignmentNum.setFocusable(false);
            txtConsignmentNum.setText(ConsignmentNum);
        }
        options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        appPath = global.barcodeDir;

        btnNewBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentBarcodeCapture(v, false);
            }
        });

        btnBulkBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intentBarcodeCapture(v, true);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (getIntent().getStringExtra("appPackage") != null) {
            String appPackage = getIntent().getStringExtra("appPackage");
            //check package and demo text is available
            if (isPackageExisted(appPackage) && ConsignmentNum != null) {
                Intent launchIntent = getPackageManager()
                        .getLaunchIntentForPackage(appPackage);
                launchIntent.setAction(Intent.ACTION_SEND);

                /* To set string values from Barcodereader activity intent to BarcodeDemo activity */
                if (getIntent().getExtras() != null) {
                    for (String key : getIntent().getExtras().keySet()) {
                        Object value = getIntent().getExtras().get(key);
                        launchIntent.putExtra(key, value.toString());
                    }
                }

                if (barcodeList != null) {
                    launchIntent.putExtra("barcodeList", barcodeList);
                }
                startActivity(launchIntent);
                finish();
            }
        } else {
            super.onBackPressed();
        }
        barcodeList = null;
    }

    private void intentBarcodeCapture(View v, boolean isBulk) {
      /*  intent = new Intent(getApplicationContext(), BarcodeCaptureActivity.class);
        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
        intent.putExtra(BarcodeCaptureActivity.Width_Spec, Width_Spec);
        intent.putExtra(BarcodeCaptureActivity.Height_Spec, Height_Spec);
        intent.putExtra(BarcodeCaptureActivity.Bulk, isBulk);

        startActivityForResult(intent, BarcodeCaptureActivity.REQUEST_CAPTURE_BARCODE);*/
    }

    /* Developer: Anas Ahmed
    *
    *  Function to check package available
    *
    *  method: GET
    *  @params: targetPackage
    */
    public boolean isPackageExisted(String targetPackage) {
        List<ApplicationInfo> packages;
        PackageManager pm;

        pm = getPackageManager();
        packages = pm.getInstalledApplications(0);
        for (ApplicationInfo packageInfo : packages) {
            if (packageInfo.packageName.equals(targetPackage))
                return true;
        }
        return false;
    }


    /**
     * Called when an activity you launched exits, giving you the requestCode
     * you started it with, the resultCode it returned, and any additional
     * data from it.  The <var>resultCode</var> will be
     * {@link #RESULT_CANCELED} if the activity explicitly returned that,
     * didn't return any result, or crashed during its operation.
     * <p/>
     * <p>You will receive this call immediately before onResume() when your
     * activity is re-starting.
     * <p/>
     *
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode  The integer result code returned by the child activity
     *                    through its setResult().
     * @param data        An Intent, which can return result data to the caller
     *                    (various data can be attached to Intent "extras").
     * @see #startActivityForResult
     * @see #createPendingResult
     * @see #setResult(int)
     */
    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == BarcodeCaptureActivity.REQUEST_CAPTURE_BARCODE && resultCode == CommonStatusCodes.SUCCESS) {

            Bundle extras = data.getExtras();
            ArrayList<String> barCodeArr = (ArrayList<String>) extras.get(BarcodeCaptureActivity.BarcodeLabel);

            if (barcodeList == null) {
                barcodeList = new ArrayList<String>();
            }
            barcodeList.addAll(barCodeArr);

            Set<String> toRetain = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
            toRetain.addAll(barcodeList);
            Set<String> set = new LinkedHashSet<String>(barcodeList);
            set.retainAll(new LinkedHashSet<String>(toRetain));
            barcodeList = new ArrayList<String>(set);

            listBarcode.setAdapter(new BarcodeListAdapter(BarcodeCaptureListActivity.this, barcodeList));
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }*/
}
