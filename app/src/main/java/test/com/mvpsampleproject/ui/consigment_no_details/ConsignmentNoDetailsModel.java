package test.com.mvpsampleproject.ui.consigment_no_details;

import com.tcs.pickupapp.data.rest.response.ErrorResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by muhammad.sohail on 5/17/2018.
 */

public class ConsignmentNoDetailsModel implements ConsignmentNoDetailsMVP.Model {


    private com.tcs.pickupapp.data.rest.PickupAPI pickupAPI;

    public ConsignmentNoDetailsModel(com.tcs.pickupapp.data.rest.PickupAPI pickupAPI) {
        this.pickupAPI = pickupAPI;

    }

    @Override
    public void getConsignmentDetails(String cnNumber, String courierCode, final com.tcs.pickupapp.data.rest.INetwork iNetwork) {
        Call<com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse> call = pickupAPI.consignmentDetails(courierCode,cnNumber);
        call.enqueue(new Callback<com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse>() {
            @Override
            public void onResponse(Call<com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse> call, Response<com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse> response) {
                try {
                    if (response == null) {
                        iNetwork.onFailure(new NullPointerException());
                    }
                    com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse consignmentDetailsResponse = response.body();

                    if (consignmentDetailsResponse.getCode().equals("332") &&
                            consignmentDetailsResponse.getCode().equalsIgnoreCase("No Record Found")) {
                        iNetwork.onError(new ErrorResponse(consignmentDetailsResponse.getCode(),
                                consignmentDetailsResponse.getMessage()));
                        return;
                    } else if (consignmentDetailsResponse.getCode().equals("00") &&
                            consignmentDetailsResponse.getMessage().equalsIgnoreCase("Success")) {
                        iNetwork.onSuccess(consignmentDetailsResponse);
                        return;
                    } else {
                        iNetwork.onError(new ErrorResponse(consignmentDetailsResponse.getCode(),
                                consignmentDetailsResponse.getMessage()));
                        return;
                    }
                } catch (Exception ex) {
                    iNetwork.onFailure(ex);
                }
            }

            @Override
            public void onFailure(Call<com.tcs.pickupapp.data.rest.response.ConsignmentDetailsResponse> call, Throwable t) {
                iNetwork.onFailure(t);
            }
        });
    }
}
