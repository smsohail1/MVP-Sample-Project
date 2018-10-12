package test.com.mvpsampleproject.ui.booking;

import android.content.Context;

import com.tcs.pickupapp.data.rest.response.MessageResponse;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.data.room.model.HandlingInstruction;
import com.tcs.pickupapp.ui.adapter.AccountDetailAdapter;
import com.tcs.pickupapp.ui.adapter.AutocompleteCustomArrayAdapter;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;
import com.tcs.pickupapp.ui.booking.service.model.BookingResponce;
import com.tcs.pickupapp.util.callback.ServiceError;
import com.tcs.pickupapp.util.callback.ServiceListener;

import java.util.List;

/**
 * Created by umair.irshad on 4/4/2018.
 */

public interface BookingMVP {

    interface View {
        void showToastShortTime(String message);

        void showToastLongTime(String message);

        void setautocompleteAdapter(AutocompleteCustomArrayAdapter autocompleteCustomArrayAdapter);

        void verifyAccountNumberSuccess(CustomerInformation accountVerifyResponce);

        void verifyAccountNumberError(ServiceError serviceError);

        void logBookings(List<com.tcs.pickupapp.data.room.model.Booking> bookings);

        void startSyncService();

        void setCNSequences(List<GenerateSequence> generateSequences);

        void showWaitDialog();

        void hideWaitDialog();

        void setDimensions(String dimensions);

        void setBookingCount(int count);

        void setHandlindingInstructions(List<HandlingInstruction> list);

        void setServices(List<com.tcs.pickupapp.data.room.model.Service> list);

        void showRecyclerViewReports();

        void showTxtNoAccountHistoryFound();

        void setRecyclerViewAccountHistory(AccountDetailAdapter accountDetailAdapter);

        void clearCNNumberField();

        void setBookingCounts(long counts);

        void enableSaveButton();

        void clearAllFields();

        void setFocusOnEdtConsignment();

        void showLoadPendingPickupsStatus(List<com.tcs.pickupapp.data.room.model.Booking> bookings);

        void showLoadPendingPickupsStatusWithEmail(List<com.tcs.pickupapp.data.room.model.Booking> bookings);

        void disableCustomerAccountSpinner(String accountNo);

        void enableCustomerAccountSpinner();

    }

    interface Presenter {
        void setView(View view, int isRetake);

        void verifyAccountNo(String customerNumber);

        void insertBooking(com.tcs.pickupapp.data.room.model.Booking booking);

        void fetchBookings();

        void syncBooking(com.tcs.pickupapp.data.room.model.Booking booking, ServiceListener<BookingResponce> mListener);

        void sendBookingAlerts(String customerNumber, String pipeSeperatedData, ServiceListener<MessageResponse> mListener);

        void fetchCustomers(Context context);

        void fetchCNSequence();

        void checkAssignment(String cn, ServiceListener<Boolean> mListener);

        void fetchBookings(String cn);

        void fetchRetakeBookingCountsByAccountNo(String cn, int reTake);

        void fetchBookingCountsByAccountNo(String cn, int reTake);

        void filterCustomerAccount(Context context, CharSequence s);

        void fetchDimensions(String s);

        void fetchHandlingInstructions();

        void fetchServices();

        void fetchBookingsByAccountNo(String customerNumber);

        void fetchRetakeBookingsByAccountNo(String customerNumber, int isRetake);

        void fetchBookingsByAccountNo(String customerNumber, int isRetake);

        void setConsignmentAdapter(com.tcs.pickupapp.data.room.model.Booking bookingList);

        void fetchServicesByProduct(String product);

        void fetchServicesHavingMoreProducts(String product1, String product2);

        void generatePipeSeperatedBookingsData(String customerNumber);

        void setBulkAdapter(List<com.tcs.pickupapp.data.room.model.Booking> accountDetail);

        void enableSaveButton();

        void fetchLoadPendingPickups(Context context, String isSave, int isRetake);

        void fetchLoadPendingPickupsWithEmail(String isSave, int isRetake);

        void fetchCustomersByAccountNo(Context context, String accountNo);


    }

    interface Model {
        void insert(BookingModel.IBooking iBooking, com.tcs.pickupapp.data.room.model.Booking booking);

        void getAccountNumberVerification(ServiceListener<CustomerInformation> mListener, String customerNumber);

        void getAllBookings(ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener);

        void uploadBooking(com.tcs.pickupapp.data.room.model.Booking booking, ServiceListener<BookingResponce> mListener);

        void callBookingAlets(String customerNumber, String pipeSeperatedData, ServiceListener<MessageResponse> mListener);

        void getCustomersInfo(ServiceListener<List<CustomerInformation>> mListener);

        void getCustomersInfoByAccountNo(String accountNo, ServiceListener<List<CustomerInformation>> mListener);

        void getCNSequence(ServiceListener<List<GenerateSequence>> mListener);

        void checkAssignment(String cn, ServiceListener<Boolean> listener);

        void getDimensions(String s, ServiceListener<String> mListener);

        void getBookingList(ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener, String cn);

        void getRetakeBookingList(ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener, String cn, int reTake);

        void getBookingList(ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener, String cn, int reTake);


        void getHandlingInstructions(ServiceListener<List<HandlingInstruction>> listener);

        void getServices(ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>> listener);

        void getServicesByProduct(String product, ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>> listener);

        void getServicesHavingMoreProducts(String product1, String product2, ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>> listener);

        void getConsignmentByAccountNo(String customerNumber, ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener);

        void getRetakeConsignmentByAccountNo(String customerNumber, int reTake, ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener);

        void getConsignmentByAccountNo(String customerNumber, int reTake, ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>> mListener);

        void fetchLoadPendingPickups(String isSave, int isRetake, BookingModel.IPendingBooking iPendingBooking);


        void updateCustomerPendingStatus(String customertNo, String isSave);

    }

}
