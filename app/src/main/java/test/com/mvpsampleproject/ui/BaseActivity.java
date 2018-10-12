package test.com.mvpsampleproject.ui;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tcs.pickupapp.App;
import com.tcs.pickupapp.BuildConfig;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.account_history.AccountHistoryFragment;
import com.tcs.pickupapp.ui.booking.BookingFragment;
import com.tcs.pickupapp.ui.consigment_no_details.ConsignmentNoDetailsFragment;
import com.tcs.pickupapp.ui.courier_journey.CourierJourneyFragment;
import com.tcs.pickupapp.ui.customer_ack_email.CustomerAckFragment;
import com.tcs.pickupapp.ui.delete_cn_sequence.DeleteCNSequenceFragment;
import com.tcs.pickupapp.ui.dimension.DimensionFragment;
import com.tcs.pickupapp.ui.error_report.ErrorReportFragment;
import com.tcs.pickupapp.ui.feedback.FeedbackFragment;
import com.tcs.pickupapp.ui.generatecnsequence.GenerateCNSequenceFragment;
import com.tcs.pickupapp.ui.home.HomeFragment;
import com.tcs.pickupapp.ui.login.LoginActivity;
import com.tcs.pickupapp.ui.notification.NotificationFragment;
import com.tcs.pickupapp.ui.report.ReportFragment;
import com.tcs.pickupapp.ui.setting.SettingFragment;
import com.tcs.pickupapp.ui.statistics.StatisticsFragment;
import com.tcs.pickupapp.ui.ttc_unscheduled.TariffAndTransitCalculatorWebViewFragment;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.CustomView;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static com.tcs.pickupapp.ui.booking.BookingFragment.isScanned;
import static com.tcs.pickupapp.util.AppConstants.DISCONNECT_TIMEOUT;

/**
 * Created by shahrukh.malik on 02, April, 2018
 */
public abstract class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.imgBack)
    protected ImageView imgBack;

    @BindView(R.id.txtScreenTitle)
    protected TextView txtScreenTitle;
    @BindView(R.id.imgNotifications)
    protected ImageView imgNotifications;
    @BindView(R.id.txtRouteDone)
    protected TextView txtRouteDone;

    @BindView(R.id.drawer_layout)
    protected DrawerLayout drawer;

    @BindView(R.id.nav_view)
    protected NavigationView navigationView;
    @BindView(R.id.fragmentContainer)
    protected FrameLayout fragmentContainer;
    @BindView(R.id.imgMagnification)
    protected ImageView imgMagnification;
    @BindView(R.id.magnifier)
    protected CustomView magnifier;

    private FragmentManager manager;
    private ActionBarDrawerToggle toggle;


    public abstract int getLayout();

    public abstract Fragment getFragment();

    /*BRIGHTNESS CONTROLLING*/
    //Content resolver used as a handle to the system's settings
    private ContentResolver cResolver;
    //Window object, that will store a reference to the current window
    private Window window;
    int brightness;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private static final int SENSOR_SENSITIVITY = 4;

    @Inject
    SessionManager sessionManager;
    @Inject
    Utils utils;
    @Inject
    ToastUtil toastUtil;
    @Inject
    com.tcs.pickupapp.data.room.AppDatabase appDatabase;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_nav);
        ((App) getApplication()).getAppComponent().inject(this);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        utils.setupParent(this, toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        enableHomeIcon(true);
        navigationView.setNavigationItemSelectedListener(this);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("cek", "home selected");
                if (drawer.isDrawerOpen(GravityCompat.START)) {
                    drawer.closeDrawer(GravityCompat.START);
                } else {
                    if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
                        enableHomeIcon(true);
                        popBackstack();
                    } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                        popBackstack();
                    } else {
                        drawer.openDrawer(GravityCompat.START);
                    }
                }
            }
        });
        imgNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
                    if (!(fragment instanceof NotificationFragment)) {
                        addFragment(new NotificationFragment());
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        initializeBrightnessControlling();
        initializeFragment();


        imgMagnification.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    //utils.hideSoftKeyboard(editAccountNumber);
//                    imgMagnification.setColorFilter(getResources().getColor(R.color.barcode_list_img));

                    imgMagnification.getLayoutParams().height = 75;
                    imgMagnification.getLayoutParams().width = 75;
                    imgMagnification.requestLayout();

                    magnifier.setVisibility(View.VISIBLE);
                    magnifier.setImageBitmap(screenShot(fragmentContainer));
                    fragmentContainer.setVisibility(View.GONE);
                }

                return false;
            }
        });

        magnifier.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP: {

                        imgMagnification.getLayoutParams().height = 55;
                        imgMagnification.getLayoutParams().width = 55;
                        imgMagnification.requestLayout();

                        magnifier.setVisibility(View.GONE);
                        fragmentContainer.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                return false;
            }
        });

        fragmentContainer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        Log.d("touchEvent", "down");

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        Log.d("touchEvent", "up");
                        magnifier.setVisibility(View.GONE);
                        fragmentContainer.setVisibility(View.VISIBLE);
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {
                        Log.d("touchEvent", "move");

                        long downTime = SystemClock.uptimeMillis();
                        long eventTime = SystemClock.uptimeMillis() + 100;
                /*float x = 0.0f;
                float y = 0.0f;*/
// List of meta states found here:     developer.android.com/reference/android/view/KeyEvent.html#getMetaState()
                        int metaState = 0;
                        MotionEvent motionEvent = MotionEvent.obtain(
                                downTime,
                                eventTime,
                                MotionEvent.ACTION_MOVE,
                                event.getX(),
                                event.getY() - 230,
                                metaState
                        );
                        magnifier.dispatchTouchEvent(motionEvent);
                    }
                }

                return false;
            }
        });

    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void initializeBrightnessControlling() {

        cResolver = getContentResolver();
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        window = getWindow();

        /*Opening Settings screen to allow for Brightness Controlling*/
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(BaseActivity.this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getApplicationContext().getPackageName()));
                startActivity(intent);
            } else {
                configuringBrightness();
            }
        } else {
            configuringBrightness();
        }
    }

    private void configuringBrightness() {
        try {
            /*If the brightness is set to auto, this disable the auto adjustment*/
            Settings.System.putInt(cResolver,
                    Settings.System.SCREEN_BRIGHTNESS_MODE, Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            /*Getting State of current brightness level*/
            brightness = Settings.System.getInt(cResolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            Log.e("Error", "Cannot access system brightness");
            e.printStackTrace();
        }
    }

    private void popBackstack() {
        getSupportFragmentManager().popBackStackImmediate();
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (fragment instanceof HomeFragment) {
            ((HomeFragment) fragment).setTitle();
        } else if (fragment instanceof ReportFragment) {
            ((ReportFragment) fragment).setTitle();
        } else if (fragment instanceof CustomerAckFragment) {
            ((CustomerAckFragment) fragment).setTitle();
        } else if (fragment instanceof GenerateCNSequenceFragment) {
            ((GenerateCNSequenceFragment) fragment).setTitle();
        } else if (fragment instanceof DeleteCNSequenceFragment) {
            ((DeleteCNSequenceFragment) fragment).setTitle();
        } else if (fragment instanceof DimensionFragment) {
            ((DimensionFragment) fragment).setTitle();
        } else if (fragment instanceof BookingFragment) {
            ((BookingFragment) fragment).setTitle();
        } else if (fragment instanceof StatisticsFragment) {
            ((StatisticsFragment) fragment).setTitle();
        } else if (fragment instanceof NotificationFragment) {
            ((NotificationFragment) fragment).setTitle();
        } else if (fragment instanceof SettingFragment) {
            ((SettingFragment) fragment).setTitle();
        } else if (fragment instanceof FeedbackFragment) {
            ((FeedbackFragment) fragment).setTitle();
        } else if (fragment instanceof CourierJourneyFragment) {
            /*((CourierJourneyFragment) fragment).setTitle();*/
        } else if (fragment instanceof AccountHistoryFragment) {
            ((AccountHistoryFragment) fragment).setTitle();
        }
        /*else if (fragment instanceof TtcUnscheduledPickupFragment) {
            ((TtcUnscheduledPickupFragment) fragment).setTitle();
        }*/
        else if (fragment instanceof TariffAndTransitCalculatorWebViewFragment) {
        ((TariffAndTransitCalculatorWebViewFragment) fragment).setTitle();
    }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        overridePendingTransition(R.anim.pull_in_right, R.anim.push_out_left);
    }

    private void initializeFragment() {
        FragmentManager manager = getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentContainer);
        if (fragment == null) {
            fragment = getFragment();
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .commit();
        }
    }

    boolean backPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            enableHomeIcon(true);
            super.onBackPressed();
            popBackstack();
            overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
        } else if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            isScanned = false;
            popBackstack();
        } else {
            if (backPressedOnce) {
                super.onBackPressed();
                overridePendingTransition(R.anim.pull_in_left, R.anim.push_out_right);
            }

            backPressedOnce = true;
            Toast.makeText(this, "Press back again to exit.", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedOnce = false;
                }
            }, 2000);
        }
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void setTitle(String title) {
        txtScreenTitle.setText(title);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Toast.makeText(this, "", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addActivity(Class c) {
        startActivity(new Intent(this, c));
    }

    public void addFragment(Fragment fragment) {
        manager = getSupportFragmentManager();
        if (fragment != null) {
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        }
        enableHomeIcon(false);
    }

    public void addFragment(Fragment fragment, String tag) {
        manager = getSupportFragmentManager();
        if (fragment != null) {
            manager.beginTransaction()
                    .add(R.id.fragmentContainer, fragment, tag)
                    .addToBackStack(null)
                    .commit();
        }
        enableHomeIcon(false);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_ack_email) {
            // Handle the camera action
            addFragment(new CustomerAckFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
            return true;

        } else if (id == R.id.nav_check_data) {
            checkData();
        } else if (id == R.id.nav_call_mobilink) {
            utils.callMobilink();
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (id == R.id.nav_error_reports) {
            //utils.startBookingService(AppConstants.FLAG_RESET, this);
            addFragment(new ErrorReportFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
        } else if (id == R.id.nav_feedback) {
            addFragment(new FeedbackFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
            return true;
        } else if (id == R.id.nav_statistics) {
            addFragment(new StatisticsFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
            return true;
        } else if (id == R.id.nav_courier_journey) {
            addFragment(new CourierJourneyFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
            return true;
        } else if (id == R.id.nav_deleted_cns) {
            addFragment(new DeleteCNSequenceFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
            return true;
        }
        if (id == R.id.nav_consignment_details) {
            addFragment(new ConsignmentNoDetailsFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
            return true;
        }

        if (id == R.id.nav_account_history) {
            addFragment(new AccountHistoryFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
            return true;
        }
        if (id == R.id.nav_ttc_unscheduled) {
            // addFragment(new TtcUnscheduledPickupFragment());
            addFragment(new TariffAndTransitCalculatorWebViewFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);

           /* drawer.closeDrawer(GravityCompat.START);

            try {
                PackageManager pm = BaseActivity.this.getApplicationContext().getPackageManager();
                boolean isInstalled = isPackageInstalled(AppConstants.TRANSIT_TIME_CALCULATOR_APP_PACKAGE, pm);
                if (isInstalled) {
                    //Version compatibility check
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        enableHomeIcon(true);
                        launchTransitTimeCalculatorApp();
                    } else {
                        Toast.makeText(getApplicationContext(), "Transit time calculator is not support on android version below Lollipop", Toast.LENGTH_LONG).show();
                        enableHomeIcon(true);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "Please install Tariff and Transit Calculator app.", Toast.LENGTH_SHORT).show();
                    enableHomeIcon(true);
                }
            } catch (Exception ex) {
                enableHomeIcon(true);
            }*/

            return true;
        }

      /* if (id == R.id.nav_setting) {
            addFragment(new SettingFragment());
            drawer.closeDrawer(GravityCompat.START);
            enableHomeIcon(false);
            return true;

        }*/

        else if (id == R.id.nav_logout) {

            showLogoutDialog(this, "Logout", "You want to Logout?");

            /*android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
            if (prev != null) {
                ft.remove(prev);
            }
            ft.addToBackStack(null);
            CustomDialog customDialog = new CustomDialog();
            customDialog.show(ft, "dialog");*/


            /*runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    AlertDialog dialog;
                    AlertDialog.Builder builder = new AlertDialog.Builder(BaseActivity.this);
                    builder.setMessage("You want to logout?");

                    builder.setCancelable(false);
                    builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            utils.stopGPSTrackerService(BaseActivity.this.getApplicationContext());
                            sessionManager.logoutUser();
                            startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                            finish();
                            dialog.dismiss();
                        }
                    });

                    builder.setNegativeButton("no", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialog = builder.create();
                    dialog.show();
                }
            });*/


        }

        //enableHomeIcon(false);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showLogoutDialog(Context context, String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_cancel_dialog);

        Button cancel = dialog.findViewById(R.id.cancel);
        Button submit = dialog.findViewById(R.id.submit);
        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);

        txtMessage.setText("" + message);
        txtTitle.setText("" + title);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                utils.stopGPSTrackerService(BaseActivity.this.getApplicationContext());
                sessionManager.logoutUser();
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                finish();
                dialog.dismiss();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void enableHomeIcon(boolean b) {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (b) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.menu_icon);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        } else {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.left_arrow);
            drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (sessionManager.isLoggedIn()) {
            resetDisconnectTimer();
        }

        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);

        try {
            View headerView = navigationView.getHeaderView(0);
            TextView txtCourierName = headerView.findViewById(R.id.txtCourierName);
            txtCourierName.setText(sessionManager.getCourierInfo().getName());
            TextView txtCourierDetails = headerView.findViewById(R.id.txtCourierDetails);
            txtCourierDetails.setText(sessionManager.getCourierInfo().getStation() + " | " +
                    sessionManager.getCourierInfo().getRoute());
            TextView txtAppVersion = headerView.findViewById(R.id.txtAppVersion);
            txtAppVersion.setText("Version " + String.valueOf(BuildConfig.VERSION_NAME));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {

            if (event.values[0] >= -SENSOR_SENSITIVITY && event.values[0] <= SENSOR_SENSITIVITY) {
                increaseBrightness();
            } else {
                decreaseBrightness();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void increaseBrightness() {
        try {
            //Set the system brightness using the brightness variable value
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, AppConstants.BRIGHTNESS_LEVEL);
            //Get the current window attributes
            WindowManager.LayoutParams layoutpars = window.getAttributes();
            //Set the brightness of this window
            layoutpars.screenBrightness = AppConstants.BRIGHTNESS_LEVEL / (float) 200;
            //Apply attribute changes to this window
            window.setAttributes(layoutpars);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please allow for system write permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private void decreaseBrightness() {
        try {
            Settings.System.putInt(cResolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
            //Get the current window attributes
            WindowManager.LayoutParams layoutpars = window.getAttributes();
            //Set the brightness of this window
            layoutpars.screenBrightness = brightness / (float) 255;
            //Apply attribute changes to this window
            window.setAttributes(layoutpars);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Please allow for system write permissions", Toast.LENGTH_SHORT).show();
        }
    }

    private void checkData() {
        io.reactivex.Observable.just(appDatabase)
                .map(new Function<com.tcs.pickupapp.data.room.AppDatabase, String>() {
                    @Override
                    public String apply(com.tcs.pickupapp.data.room.AppDatabase appDatabase) throws Exception {
                        List<com.tcs.pickupapp.data.room.model.Booking> bookings = appDatabase.getBookingDao().getAllBookings();
                        File imageDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + AppConstants.DIRECTORY_PICKUP_BOOKINGS);
                        if (imageDirectory.exists() && imageDirectory.isDirectory()) {
                            String[] files = imageDirectory.list();
                            if (bookings.size() == files.length) {
                                return "All Data is OK!";
                            } else if (bookings.size() > files.length) {
                                // database data is greater than images in gallery
                                List<String> notFoundImages = new ArrayList<>();
                                for (com.tcs.pickupapp.data.room.model.Booking booking : bookings) {
                                    boolean isFound = false;
                                    for (int i = 0; i < files.length; i++) {
                                        String filename = files[i].split("\\.")[0];
                                        if (booking.getCnNumber().equals(filename)) {
                                            isFound = true;
                                            break;
                                        }
                                    }
                                    if (!isFound) {
                                        notFoundImages.add(booking.getCnNumber());
                                    }
                                }
                                String messageToShow = "Warning! Following images not found in Gallery:";
                                for (String image : notFoundImages) {
                                    messageToShow += "\r\n" + image;
                                }
                                return messageToShow;
                            } else if (bookings.size() < files.length) {
                                // images in gallery are greater than images in gallery
                                List<String> notFoundConsignments = new ArrayList<>();
                                for (int i = 0; i < files.length; i++) {
                                    boolean isFound = false;
                                    for (com.tcs.pickupapp.data.room.model.Booking booking : bookings) {
                                        String filename = files[i].split("\\.")[0];
                                        if (filename.equals(booking.getCnNumber())) {
                                            isFound = true;
                                            break;
                                        }
                                    }
                                    if (!isFound) {
                                        notFoundConsignments.add(files[i].split("\\.")[0]);
                                    }
                                }
                                String messageToShow = "Warning! Following data not present in database:";
                                for (String image : notFoundConsignments) {
                                    messageToShow += "\r\n" + image;
                                }
                                return messageToShow;
                            }
                        } else {
                            // directory does not exists
                            if (bookings.size() > 0) {
                                return "Image Directory does not exitsts but booking data is present in Database";
                            } else if (bookings.size() == 0) {
                                return "All Data is OK!";
                            }
                        }

                        return "";
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        showAlertDialog(s);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Dialog dialog;

    private void showAlertDialog(String message) {
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.pop_up_check_data);

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        txtMessage.setText(message);
        Button btnOK = dialog.findViewById(R.id.btnOK);
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void launchTransitTimeCalculatorApp() {
        try {
            Intent launchIntent = getPackageManager().getLaunchIntentForPackage(AppConstants.TRANSIT_TIME_CALCULATOR_APP_PACKAGE);
            if (launchIntent != null) {
                startActivity(launchIntent);//null pointer check in case package name was not found
            }
        } catch (Exception e) {

        }
    }

    public TextView getTxtRouteDone() {
        return txtRouteDone;
    }

    public ImageView getImgNotifications() {
        return imgNotifications;
    }

    public ImageView getImgMagnification() {
        return imgMagnification;
    }


    private Handler disconnectHandler = new Handler() {
        public void handleMessage(Message msg) {
        }
    };

    private Runnable disconnectCallback = new Runnable() {
        @Override
        public void run() {
            // Perform any required operation on disconnect
            if (sessionManager.isLoggedIn()) {
                stopDisconnectTimer();
                utils.stopGPSTrackerService(BaseActivity.this.getApplicationContext());
                sessionManager.logoutUser();
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
                finish();
            }
        }
    };

    public void resetDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
        disconnectHandler.postDelayed(disconnectCallback, DISCONNECT_TIMEOUT);
    }

    public void stopDisconnectTimer() {
        disconnectHandler.removeCallbacks(disconnectCallback);
    }

    @Override
    public void onUserInteraction() {
        if (sessionManager.isLoggedIn()) {
            resetDisconnectTimer();
        }

    }


    @Override
    public void onStop() {
        super.onStop();
        if (sessionManager.isLoggedIn()) {
            resetDisconnectTimer();
        }
    }
}

















