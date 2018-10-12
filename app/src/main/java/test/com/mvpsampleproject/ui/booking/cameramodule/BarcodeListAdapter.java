package test.com.mvpsampleproject.ui.booking.cameramodule;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.booking.camerahelper.TouchImageView;
import com.tcs.pickupapp.ui.booking.camerahelper.global;

import java.util.List;


public class BarcodeListAdapter extends BaseAdapter {

    private List<String> barcodeList;
    private Activity activity;
    private LayoutInflater inflater = null;
    private Typeface fontello;

    public BarcodeListAdapter(Activity _activity, List<String> _barcodeList) {
        // TODO Auto-generated constructor stub
        activity = _activity;
        barcodeList = _barcodeList;

        inflater = (LayoutInflater) activity.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return barcodeList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        EditText txtBarcode;
        TextView btnReadBarCode, btnPreviewImage;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        Holder holder = new Holder();

        final String barcode = barcodeList.get(position);

        View rowView = inflater.inflate(R.layout.barcode_list_item, null);

        holder.txtBarcode = (EditText) rowView.findViewById(R.id.txtBarcode);
        holder.btnReadBarCode = (TextView) rowView.findViewById(R.id.btnReadBarCode);
        holder.btnPreviewImage = (TextView) rowView.findViewById(R.id.btnPreviewImage);

        fontello = Typeface.createFromAsset(activity.getAssets(), "fonts/fontello.ttf");
        holder.btnReadBarCode.setTypeface(fontello);
        holder.btnPreviewImage.setTypeface(fontello);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String appPath = global.barcodeDir;
        Bitmap bitmap = BitmapFactory.decodeFile(appPath + "/" + barcode, options);

        holder.txtBarcode.setText(barcode);
        //holder.btnReadBarCode.setBackground(new BitmapDrawable(activity.getResources(), bitmap));

        holder.btnPreviewImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImage(barcode);
            }
        });

        holder.btnReadBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* Intent intent = new Intent(activity.getApplicationContext(), BarcodeCaptureActivity.class);
                intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
                intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
                intent.putExtra(BarcodeCaptureActivity.Width_Spec, 1440);
                intent.putExtra(BarcodeCaptureActivity.Height_Spec, 2560);

                BarcodeCaptureListActivity.barcodeList.remove(position);

                activity.startActivityForResult(intent, BarcodeCaptureActivity.REQUEST_CAPTURE_BARCODE);*/
            }
        });

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });


        return rowView;
    }

    public void showImage(String path) {

        if (path.equals("")) {
            return;
        }

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        String appPath = global.barcodeDir;

        Bitmap bitMap = BitmapFactory.decodeFile(appPath + "/" + path, options);

        BitmapDrawable bitmapDraw = new BitmapDrawable(activity.getResources(), RotateBitmap(bitMap, 90));


        Dialog builder = new Dialog(activity);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(android.graphics.Color.TRANSPARENT));
        builder.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                //nothing;
            }
        });

        TouchImageView imageView = new TouchImageView(activity);
        imageView.setImageDrawable(bitmapDraw);
        builder.addContentView(imageView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        builder.show();
    }

    /* Developer: Anas Ahmed
    *
    *  Function to return rotated image
    *
    *  method: GET
    *  @params: source
    *  @params: angle
    */
    private Bitmap RotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
}
