package test.com.mvpsampleproject.ui.consigment_no_details;

/**
 * Created by muhammad.sohail on 5/17/2018.
 */

public interface ConsignmentNoDetailsMVP {


    interface View {
        void showToastShortTime(String message);

        void showToastLongTime(String message);

        void hideSoftKeyboard();

        void showProgressDialogPleaseWait();

        void hideProgressDialogPleaseWait();


        void showTxtConsignmentNotFound();

        void showTxtConsignmentFound(com.tcs.pickupapp.data.rest.response.ConsignmentDetails consignmentDetails);

        void clearField();

    }


    interface Presenter {
        void setView(ConsignmentNoDetailsMVP.View view);

        void onClickBtnGetConsignmentDetails(String consignmentNo, String courierCode);


    }

    interface Model {
        void getConsignmentDetails(String cnNumber, String courierCode, com.tcs.pickupapp.data.rest.INetwork iNetwork);

    }

}
