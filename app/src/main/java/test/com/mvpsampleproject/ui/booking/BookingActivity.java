package test.com.mvpsampleproject.ui.booking;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;

public class BookingActivity extends BaseActivity {

    @Override
    public int getLayout() {
        return R.layout.activity_base;
    }

    @Override
    public Fragment getFragment() {
        return new BookingFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle(getString(R.string.booking));
    }
}
