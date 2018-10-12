package test.com.mvpsampleproject.ui.booking.camerahelper;

/**
 * Created by muhammad.talha on 4/1/2016.
 */

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcs.pickupapp.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class TakePicture extends Activity implements SurfaceHolder.Callback {
    //a variable to store a reference to the Surface View at the main.xml file
    private SurfaceView sv;

    //a bitmap to display the captured image
    private Bitmap bmp;

    //Camera variables
    //a surface holder
    private SurfaceHolder sHolder;
    //a variable to control the camera
    private Camera mCamera;
    //the camera parameters
    private Parameters parameters;
    ImageView btnCaptureSingle;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.take_picture);

        //get the Surface View at the main.xml file
        sv = (SurfaceView) findViewById(R.id.surfaceView);
        btnCaptureSingle = (ImageView) findViewById(R.id.btnCaptureSingle);

        //Get a surface
        sHolder = sv.getHolder();

        //add the callback interface methods defined below as the Surface View callbacks
        sHolder.addCallback(this);

        //tells Android that this surface will have its data constantly replaced
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
        //get camera parameters
        parameters = mCamera.getParameters();
        parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (1600 <= size.width & size.width <= 1920) {
                //parameters.setPreviewSize(size.width, size.height);
                parameters.setPictureSize(parameters.getPreviewSize().width, parameters.getPreviewSize().height);
                break;
            }
        }
        //set camera parameters
        mCamera.setParameters(parameters);
        mCamera.startPreview();

        //sets what code should be executed after the picture is taken
        final Camera.PictureCallback mCall = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //decode the data obtained by the camera into a Bitmap
                bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                //set the iv_image

                FileOutputStream out = null;
                String appPath = global.barcodeDir;
                if (!new File(appPath).exists())
                    new File(appPath).mkdir();
                try {

                    out = new FileOutputStream(appPath + "/" + getIntent().getStringExtra("CNNN"));

                    out.write(data);
                    out.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // TODO Auto-generated method stub
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        callPopup();
                        onPause();
                    }
                });
            }
        };

        btnCaptureSingle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCamera.takePicture(null, null, mCall);
            }
        });
    }

    private void callPopup() {
        LayoutInflater layoutInflater
                = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.preview_barcode, null);
        final PopupWindow popupWindow = new PopupWindow(
                popupView,
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);

        TextView btnCancel = (TextView) popupView.findViewById(R.id.btnCancel);
        TextView btnOk = (TextView) popupView.findViewById(R.id.btnOk);
        TextView txtBarcode = (TextView) popupView.findViewById(R.id.txtBarcode);
        ImageView previewBarcode = (ImageView) popupView.findViewById(R.id.previewBarcode);

        Typeface fontello = Typeface.createFromAsset(getAssets(), "fonts/fontello.ttf");
        btnOk.setTypeface(fontello);
        btnCancel.setTypeface(fontello);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String appPath = global.barcodeDir;
        Bitmap bitmap;
        String barcodeLabel = "";
        barcodeLabel = getIntent().getStringExtra("CNNN");

        bitmap = BitmapFactory.decodeFile(appPath + "/" + barcodeLabel, options);
        bitmap = BitmapFactory.decodeFile(appPath + "/" + barcodeLabel, options);
        Log.d("appPath", "appPath: " + appPath + "/" + barcodeLabel);

        txtBarcode.setText(barcodeLabel);
        previewBarcode.setImageDrawable(new BitmapDrawable(getResources(), bitmap));

        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intnt = new Intent();
                setResult(RESULT_OK, intnt);
                finish();
            }
        });

        btnCancel.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                popupWindow.dismiss();
                onResume();
            }
        });

        //popupWindow.showAsDropDown(btnOpenPopup, 50, -30);
        popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera != null) {
            mCamera.startPreview();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw the preview.
        mCamera = Camera.open();
        try {
            mCamera.setPreviewDisplay(holder);

        } catch (IOException exception) {
            onPause();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        super.onDestroy();
        //stop the preview
        mCamera.stopPreview();
        //release the camera
        mCamera.release();
        //unbind the camera from this object
        mCamera = null;
    }
}