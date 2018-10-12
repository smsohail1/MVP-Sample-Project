package test.com.mvpsampleproject.ui.booking;

import android.content.Context;
import android.util.Log;

import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.rest.response.MessageResponse;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.data.room.model.HandlingInstruction;
import com.tcs.pickupapp.ui.adapter.AccountDetailAdapter;
import com.tcs.pickupapp.ui.adapter.AutocompleteCustomArrayAdapter;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;
import com.tcs.pickupapp.ui.booking.service.model.BookingResponce;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.callback.ServiceError;
import com.tcs.pickupapp.util.callback.ServiceListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by umair.irshad on 4/4/2018.
 */

public class BookingPresenter implements BookingMVP.Presenter, AccountDetailAdapter.IAccountDetailAdapter {

    private BookingMVP.Model model;
    private BookingMVP.View view;
    private AutocompleteCustomArrayAdapter autocompleteCustomArrayAdapter;
    private List<CustomerInformation> customerInfoList;
    private AccountDetailAdapter adapter;
    private SessionManager sessionManager;
    private int isRetake;

    public BookingPresenter(BookingMVP.Model model, SessionManager sessionManager) {
        this.model = model;
        this.sessionManager = sessionManager;
    }

    @Override
    public void setView(BookingMVP.View view, int isRetake) {
        this.view = view;
        this.isRetake = isRetake;
    }


    @Override
    public void verifyAccountNo(String customerNumber) {
        view.showWaitDialog();
        model.getAccountNumberVerification(new ServiceListener<CustomerInformation>() {
            @Override
            public void onSuccess(CustomerInformation responce) {
                view.verifyAccountNumberSuccess(responce);
                view.hideWaitDialog();
            }

            @Override
            public void onError(ServiceError error) {
                view.verifyAccountNumberError(error);
                view.hideWaitDialog();
                view.showTxtNoAccountHistoryFound();
            }
        }, customerNumber);
    }

    @Override
    public void insertBooking(final com.tcs.pickupapp.data.room.model.Booking booking) {
        //TODO
        model.insert(new BookingModel.IBooking() {
            @Override
            public void onBookingSuccess() {
                view.showToastShortTime("Booking Confirmed");
                view.setFocusOnEdtConsignment();

                /*
                 * starting background thread to sync bookings
                 * */
                view.startSyncService();
            }

            @Override
            public void onBookingError(Exception ex) {
                view.showToastShortTime("Error while Booking: " + ex.getMessage());
            }
        }, booking);
    }

    @Override
    public void fetchBookings() {
        model.getAllBookings(new ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
                view.logBookings(bookingList);
            }

            @Override
            public void onError(ServiceError error) {

            }
        });
    }

    @Override
    public void syncBooking(com.tcs.pickupapp.data.room.model.Booking booking, final ServiceListener<BookingResponce> mListener) {
        try {
            String[] splits = booking.getServiceNumber().split("\\|");
            booking.setServiceNumber(splits[1]);
            booking.setServiceName(splits[0]);
        } catch (Exception ex) {
            booking.setServiceNumber(booking.getServiceNumber());
            booking.setServiceName(booking.getServiceNumber());
            ex.printStackTrace();
        }
        model.uploadBooking(booking, new ServiceListener<BookingResponce>() {
            @Override
            public void onSuccess(BookingResponce responce) {
                mListener.onSuccess(responce);
            }

            @Override
            public void onError(ServiceError error) {
                mListener.onError(new ServiceError(error.getMessage()));
            }
        });
    }

    @Override
    public void sendBookingAlerts(String customerNumber, String pipeSeperatedData, final ServiceListener<MessageResponse> mListener) {
        model.callBookingAlets(customerNumber, pipeSeperatedData, new ServiceListener<MessageResponse>() {
            @Override
            public void onSuccess(MessageResponse object) {
                mListener.onSuccess(object);
            }

            @Override
            public void onError(ServiceError error) {
                mListener.onError(new ServiceError(error.getMessage()));
            }
        });
    }

    @Override
    public void fetchCustomers(final Context context) {
        model.getCustomersInfo(new ServiceListener<List<CustomerInformation>>() {
            @Override
            public void onSuccess(List<CustomerInformation> cusInfoList) {
                if (cusInfoList.size() == 0) {
                    setupAdapter(context, new ArrayList<CustomerInformation>());
//                    setupAdapter(context, customerInfoList);
                    return;
                }
                Log.d("umair", "-customer's list fatched");
                customerInfoList = cusInfoList;
                setupAdapter(context, cusInfoList);
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }

    @Override
    public void fetchCNSequence() {
        model.getCNSequence(new ServiceListener<List<GenerateSequence>>() {
            @Override
            public void onSuccess(List<GenerateSequence> sequences) {
                if (sequences.size() != 0) {
                    view.setCNSequences(sequences);
                } else {
                    view.showToastShortTime("No sequence allocated");
                }
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }


    @Override
    public void fetchBookings(String consignment) {
        model.getBookingList(new ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookingLists) {
                view.setBookingCount(bookingLists.size());
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        }, consignment);
    }

    @Override
    public void fetchRetakeBookingCountsByAccountNo(String cn, int reTake) {
        model.getRetakeBookingList(new ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookingLists) {
                view.setBookingCount(bookingLists.size());
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        }, cn, reTake);
    }

    @Override
    public void fetchBookingCountsByAccountNo(String cn, int reTake) {
        model.getBookingList(new ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookingLists) {
                view.setBookingCount(bookingLists.size());
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        }, cn, reTake);
    }


    @Override
    public void checkAssignment(String cn, final ServiceListener<Boolean> mListener) {
        model.checkAssignment(cn, new ServiceListener<Boolean>() {
            @Override
            public void onSuccess(Boolean b) {
                mListener.onSuccess(b);
            }

            @Override
            public void onError(ServiceError error) {
                mListener.onError(error);
            }
        });
    }

    @Override
    public void filterCustomerAccount(final Context context, final CharSequence s) {
        /*model.getCustomersInfo(new ServiceListener<List<CustomerInformation>>() {
            @Override
            public void onSuccess(List<CustomerInformation> customerInfoList) {
                if (customerInfoList.size() == 0) {
                    setupAdapter(context, new ArrayList<CustomerInformation>());
                    return;
                }*/
        if (customerInfoList != null && customerInfoList.size() != 0) {
            List<CustomerInformation> filteredList = filter(customerInfoList, s);
            if (filteredList.size() != 0) {
                setupAdapter(context, filteredList);
            }
        }

            /*}

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });*/
    }

    @Override
    public void fetchDimensions(String s) {
        model.getDimensions(s, new ServiceListener<String>() {
            @Override
            public void onSuccess(String dimensions) {
                view.setDimensions(dimensions);
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }

    @Override
    public void fetchHandlingInstructions() {
        model.getHandlingInstructions(new ServiceListener<List<HandlingInstruction>>() {
            @Override
            public void onSuccess(List<HandlingInstruction> list) {
                view.setHandlindingInstructions(list);
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }

    @Override
    public void fetchServices() {
        model.getServices(new ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Service> list) {
                view.setServices(list);
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }

    @Override
    public void fetchServicesByProduct(String product) {
        model.getServicesByProduct(product, new ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Service> list) {
                view.setServices(list);
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }

    @Override
    public void fetchServicesHavingMoreProducts(String product1, String product2) {
        model.getServicesHavingMoreProducts(product1, product2, new ServiceListener<List<com.tcs.pickupapp.data.room.model.Service>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Service> list) {
                view.setServices(list);
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }


    @Override
    public void fetchBookingsByAccountNo(String customerNumber) {
        model.getConsignmentByAccountNo(customerNumber, new ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
                if (bookingList != null && bookingList.size() == 0) {
                    setDetailsAdapter(bookingList);
                    view.showTxtNoAccountHistoryFound();
                    return;
                } else {
                    view.showRecyclerViewReports();
                    setAdapter(bookingList);
                }
            }

            @Override
            public void onError(ServiceError error) {
                view.showTxtNoAccountHistoryFound();
                view.showToastShortTime(error.getMessage());
            }
        });
    }


    @Override
    public void fetchRetakeBookingsByAccountNo(String customerNumber, int isRetake) {
        model.getRetakeConsignmentByAccountNo(customerNumber, isRetake, new ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
                if (bookingList != null && bookingList.size() == 0) {
                    setDetailsAdapter(bookingList);
                    view.showTxtNoAccountHistoryFound();
                    return;
                } else {
                    view.showRecyclerViewReports();
                    setAdapter(bookingList);
                }
            }

            @Override
            public void onError(ServiceError error) {
                view.showTxtNoAccountHistoryFound();
                view.showToastShortTime(error.getMessage());
            }
        });
    }

    @Override
    public void fetchBookingsByAccountNo(String customerNumber, int isRetake) {
        model.getConsignmentByAccountNo(customerNumber, isRetake, new ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
                if (bookingList != null && bookingList.size() == 0) {
                    setDetailsAdapter(bookingList);
                    view.showTxtNoAccountHistoryFound();
                    return;
                } else {
                    view.showRecyclerViewReports();
                    setAdapter(bookingList);
                }
            }

            @Override
            public void onError(ServiceError error) {
                view.showTxtNoAccountHistoryFound();
                view.showToastShortTime(error.getMessage());
            }
        });
    }


    private void setAdapter(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
        Collections.reverse(bookingList);
        if (adapter == null) {
            adapter = new AccountDetailAdapter(bookingList, this);
            view.setRecyclerViewAccountHistory(adapter);
        } else {
            adapter.removeAll();
            adapter.addAll(bookingList);
        }
    }

    @Override
    public void setBulkAdapter(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
        Collections.reverse(bookingList);
        if (adapter == null) {
            adapter = new AccountDetailAdapter(bookingList, this);
            view.setRecyclerViewAccountHistory(adapter);
        } else {
            //adapter.removeAll();
            adapter.addBulkCN(bookingList);
        }
    }

    @Override
    public void enableSaveButton() {
        view.enableSaveButton();
    }

    @Override
    public void fetchLoadPendingPickups(final Context context, String isSave, final int isRetake) {
        model.fetchLoadPendingPickups(isSave, isRetake, new BookingModel.IPendingBooking() {
            @Override
            public void onBookingSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookings) {
                if (bookings != null && bookings.size() > 0) {
                    setDetailsAdapter(bookings);

//                    if (isRetake == 1) {
//                        fetchRetakeBookingCountsByAccountNo(bookings.get(0).getCustomerNumber(), isRetake);
//
//                    } else {
//                        fetchBookingCountsByAccountNo(bookings.get(0).getCustomerNumber(), isRetake);
//                    }
//
//                    if (isRetake == 1) {
//                        fetchRetakeBookingsByAccountNo(bookings.get(0).getCustomerNumber(), isRetake);
//                    } else {
//                        fetchBookingsByAccountNo(bookings.get(0).getCustomerNumber(), isRetake);
//                    }

                    if (bookings.get(0).getCustomerNumber() != null) {
                        view.disableCustomerAccountSpinner(bookings.get(0).getCustomerNumber());
                    }
                    return;
                }
                view.showRecyclerViewReports();
                view.showLoadPendingPickupsStatus(bookings);


            }

            @Override
            public void onBookingError(Exception ex) {
                view.showTxtNoAccountHistoryFound();
                view.showToastShortTime(ex.getMessage());

            }
        });

    }

    @Override
    public void fetchLoadPendingPickupsWithEmail(String isSave, final int isRetake) {
        model.fetchLoadPendingPickups(isSave, isRetake, new BookingModel.IPendingBooking() {
            @Override
            public void onBookingSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookings) {
                if (bookings != null && bookings.size() > 0) {
                    view.showLoadPendingPickupsStatusWithEmail(bookings);
                    return;
                }


            }

            @Override
            public void onBookingError(Exception ex) {
                view.showTxtNoAccountHistoryFound();
                view.showToastShortTime(ex.getMessage());

            }
        });

    }

    private void setDetailsAdapter(List<com.tcs.pickupapp.data.room.model.Booking> bookingList) {
        Collections.reverse(bookingList);
        if (adapter == null) {
            adapter = new AccountDetailAdapter(bookingList, this);
            view.setRecyclerViewAccountHistory(adapter);
        } else {
            adapter.removeAll();
        }
    }

    @Override
    public void setConsignmentAdapter(com.tcs.pickupapp.data.room.model.Booking bookingList) {
        if (adapter != null) {
            adapter.addSingleBooking(bookingList);
            view.showRecyclerViewReports();
        }
    }

    @Override
    public void generatePipeSeperatedBookingsData(final String customerNumber) {
        if (customerNumber.equals("")) {
            return;
        }
        model.getConsignmentByAccountNo(customerNumber, new ServiceListener<List<com.tcs.pickupapp.data.room.model.Booking>>() {
            @Override
            public void onSuccess(List<com.tcs.pickupapp.data.room.model.Booking> bookings) {
                if (bookings == null) {
                    view.showToastLongTime("No bookings found");
                    view.setBookingCounts(bookings.size());
                    return;
                }
                if (bookings.size() == 0) {
                    view.showToastLongTime("No bookings found");
                    view.setBookingCounts(bookings.size());
                    return;
                }
                String pipeSeperatedData = "";
                for (com.tcs.pickupapp.data.room.model.Booking booking : bookings) {
                    int isRetake = booking.getIsRetake();
                    String isRetakeToSend = "false";
                    if (isRetake == 1) {
                        isRetakeToSend = "true";
                    } else if (isRetake == 0) {
                        isRetakeToSend = "false";
                    }
                    pipeSeperatedData += booking.getCnNumber() + "!" +
                            booking.getCustomerNumber() + "!" +
                            booking.getWeight() + "!" +
                            booking.getPieces() + "!" +
                            booking.getTransmitStatus() + "!" +
                            booking.getServiceNumber().split("\\|")[1] + "!" +
                            booking.getProduct() + "!" +
                            booking.getCustomerRef() + "!" +
                            booking.getHandlingInstructionFullText() + "!!!" + isRetakeToSend + "~";
                }
                sessionManager.setBookingAlertsPipeData(pipeSeperatedData);
                model.updateCustomerPendingStatus(customerNumber, "1");
                view.enableCustomerAccountSpinner();
                view.showToastLongTime("Saved successfully");
                view.setBookingCounts(bookings.size());
                view.startSyncService();

                // Added by Shahrukh
                // this is for clearing all the fields on Save Button Click
                adapter.removeAll();
                view.clearAllFields();
            }

            @Override
            public void onError(ServiceError error) {
                if (error.getMessage() != null) {
                    view.showToastLongTime(error.getMessage());
                }
            }
        });
    }


    public List<CustomerInformation> filter(List<CustomerInformation> backupCusInfo, CharSequence c) {
        int length = c.length();
        List<CustomerInformation> customerInformationList = new ArrayList<>();

        if (length != 0) {
            for (int i = 0; i < backupCusInfo.size(); i++) {
                if (backupCusInfo.get(i).getCustomerNumber().toLowerCase().contains(c.toString().toLowerCase())) {
                    customerInformationList.add(backupCusInfo.get(i));
                }
            }
        } /*else {
            customerInformationList.addAll(backupCusInfo);
        }*/
        return customerInformationList;
    }


    private void setupAdapter(Context context, List<CustomerInformation> customerInfoList) {
        autocompleteCustomArrayAdapter = new AutocompleteCustomArrayAdapter(context, R.layout.autocomplete_listitem, customerInfoList);
        view.setautocompleteAdapter(autocompleteCustomArrayAdapter);

    }

    @Override
    public void fetchCustomersByAccountNo(final Context context, String accountNo) {
        model.getCustomersInfoByAccountNo(accountNo, new ServiceListener<List<CustomerInformation>>() {
            @Override
            public void onSuccess(List<CustomerInformation> cusInfoList) {
                if (cusInfoList.size() == 0) {
                    setupAdapter(context, new ArrayList<CustomerInformation>());
//                    setupAdapter(context, customerInfoList);
                    return;
                }
                Log.d("umair", "-customer's list fatched");
                customerInfoList = cusInfoList;
                setupAdapter(context, cusInfoList);
            }

            @Override
            public void onError(ServiceError error) {
                view.showToastShortTime(error.getMessage());
            }
        });
    }


    @Override
    public void onItemClick(com.tcs.pickupapp.data.room.model.Booking booking) {

    }
}
