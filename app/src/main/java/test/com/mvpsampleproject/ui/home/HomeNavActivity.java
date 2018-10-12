package test.com.mvpsampleproject.ui.home;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.ui.BaseActivity;

public class HomeNavActivity extends BaseActivity{

    @Override
    public int getLayout() {
        return R.layout.activity_home_nav;
    }

    @Override
    public Fragment getFragment() {
        return new HomeFragment();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        getTxtScreenTitle().setText(R.string.home);
        setTitle(getString(R.string.home));
    }
}
