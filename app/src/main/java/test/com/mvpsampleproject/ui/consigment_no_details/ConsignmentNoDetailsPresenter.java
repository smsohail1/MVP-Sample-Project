package test.com.mvpsampleproject.ui.consigment_no_details;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.rest.response.ErrorResponse;
import com.tcs.pickupapp.util.Utils;

/**
 * Created by muhammad.sohail on 5/17/2018.
 */

public class ConsignmentNoDetailsPresenter implements ConsignmentNoDetailsMVP.Presenter {

    private ConsignmentNoDetailsMVP.Model model;
    private Utils utils;


    private ConsignmentNoDetailsMVP.View view;

    public ConsignmentNoDetailsPresenter(ConsignmentNoDetailsMVP.Model model, Utils utils) {
        this.model = model;
        this.utils = utils;
    }

    @Override
    public void setView(ConsignmentNoDetailsMVP.View view) {
        this.view = view;
    }

    @Override
    public void onClickBtnGetConsignmentDetails(String consignmentNo, String courierCode) {
        if (!utils.isInternetAvailable()) {
            view.showToastLongTime(utils.getStringFromResourceId(R.string.please_check_internet_connection));
            return;
        }
        if (validateInputField(consignmentNo, courierCode)) {
            view.hideSoftKeyboard();
            view.showProgressDialogPleaseWait();
            model.getConsignmentDetails(consignmentNo, courierCode, new com.tcs.pickupapp.data.rest.INetwork() {
                @Override
                public void onSuccess(Object response) {
                    final com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse consignmentDetailsResponse = (com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse) response;
                    if (consignmentDetailsResponse.getConsignmentDetails() == null) {
                        view.hideProgressDialogPleaseWait();
                        view.showTxtConsignmentNotFound();
                        return;
                    } else {
                        view.hideProgressDialogPleaseWait();
                        view.showTxtConsignmentFound(consignmentDetailsResponse.getConsignmentDetails());
                    }
                }

                @Override
                public void onError(ErrorResponse errorResponse) {
                    view.hideProgressDialogPleaseWait();
                    view.showTxtConsignmentNotFound();
                }

                @Override
                public void onFailure(Throwable t) {
                    Exception exception = (Exception) t;
                    t.printStackTrace();
                    view.hideProgressDialogPleaseWait();
                    view.showTxtConsignmentNotFound();
                }
            });
        }
    }


    private boolean validateInputField(String consignmentNo, String courierCode) {

        if (utils.isTextNullOrEmpty(consignmentNo)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.cn_number));
            return false;
        }

        if (utils.isTextNullOrEmpty(courierCode)) {
            view.showToastShortTime(utils.getStringFromResourceId(R.string.courier_code_cannot_be_empty));
            return false;
        }


        return true;
    }


    private void receivedNull(String apiJsonNodeName) {
        view.hideProgressDialogPleaseWait();
        view.showToastLongTime(apiJsonNodeName + " not found");
    }

}
