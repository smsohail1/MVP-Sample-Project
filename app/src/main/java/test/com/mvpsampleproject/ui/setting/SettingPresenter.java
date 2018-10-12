package test.com.mvpsampleproject.ui.setting;

import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.Utils;

/**
 * Created by muhammad.sohail on 5/7/2018.
 */

public class SettingPresenter implements SettingMVP.Presenter {

    private SettingMVP.View view;
    private Utils utils;
    private SessionManager sessionManager;


    public SettingPresenter(Utils utils, SessionManager sessionManager) {
        this.utils = utils;
        this.sessionManager = sessionManager;

    }

    @Override
    public void setView(SettingMVP.View view) {
        this.view = view;
    }

    @Override
    public void okayBtnClick(String batteryPercentage, String MinimumCameraBarcodePercent) {
        if (batteryPercentage.equalsIgnoreCase("") ||
                batteryPercentage.isEmpty() ||
                batteryPercentage.length() == 0) {
            view.showToastShortTime("Please Set battery percent");
        } else if ((Integer.parseInt(batteryPercentage) <= 0 || Integer.parseInt(batteryPercentage) > 100)) {
            view.showToastShortTime("Please Set battery percent between 1 to 100");
        } else if (!batteryPercentage.equalsIgnoreCase("") ||
                !batteryPercentage.isEmpty()) {
            sessionManager.setLowBatteryPercentage(batteryPercentage);
            view.showToastShortTime("Battery percentage set successfully");
            view.hideSoftKeyboard();
        }


        if (MinimumCameraBarcodePercent.equalsIgnoreCase("") ||
                MinimumCameraBarcodePercent.isEmpty() ||
                MinimumCameraBarcodePercent.length() == 0) {
            view.showToastShortTime("Please Set camera and barcode percent");
        } else if ((Integer.parseInt(MinimumCameraBarcodePercent) <= 0 || Integer.parseInt(MinimumCameraBarcodePercent) > 100)) {
            view.showToastShortTime("Please Set camera and barcode percent between 1 to 100");
        } else if (!MinimumCameraBarcodePercent.equalsIgnoreCase("") ||
                !MinimumCameraBarcodePercent.isEmpty()) {
            sessionManager.setMinimumCameraBarcodePercent(MinimumCameraBarcodePercent);
            view.showToastShortTime("Camera and barcode percentage set successfully");
            view.hideSoftKeyboard();
        }
        view.clearField();

    }


    @Override
    public void onClickScreen() {
        view.hideSoftKeyboard();
    }
}
