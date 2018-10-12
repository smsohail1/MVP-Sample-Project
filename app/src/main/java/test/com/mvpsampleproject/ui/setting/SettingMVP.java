package test.com.mvpsampleproject.ui.setting;

/**
 * Created by muhammad.sohail on 5/7/2018.
 */

public interface SettingMVP {
    interface View {
        void showToastShortTime(String message);

        void showToastLongTime(String message);

        void hideSoftKeyboard();

        void clearField();
    }

    interface Presenter {
        void setView(View view);

        void okayBtnClick(String batteryPercentage, String MinimumCameraBarcodePercent);

        void onClickScreen();
    }

    interface Model {

    }
}
