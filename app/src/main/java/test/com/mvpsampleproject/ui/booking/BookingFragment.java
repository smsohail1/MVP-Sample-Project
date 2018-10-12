package test.com.mvpsampleproject.ui.booking;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.location.LocationManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.tcs.pickupapp.App;
import com.tcs.pickupapp.R;
import com.tcs.pickupapp.data.room.model.GenerateSequence;
import com.tcs.pickupapp.data.room.model.HandlingInstruction;
import com.tcs.pickupapp.ui.BaseActivity;
import com.tcs.pickupapp.ui.adapter.AccountDetailAdapter;
import com.tcs.pickupapp.ui.adapter.AutocompleteCustomArrayAdapter;
import com.tcs.pickupapp.ui.booking.cameramodule.BarcodeCaptureActivity;
import com.tcs.pickupapp.ui.booking.cameramodule.CaptureCameraImageFragment;
import com.tcs.pickupapp.ui.booking.model.CustomerInformation;
import com.tcs.pickupapp.ui.bookinglist.BookingListFragment;
import com.tcs.pickupapp.ui.dimension.DimensionFragment;
import com.tcs.pickupapp.util.AppConstants;
import com.tcs.pickupapp.util.CustomAutoCompleteView;
import com.tcs.pickupapp.util.CustomView;
import com.tcs.pickupapp.util.ImageCompression;
import com.tcs.pickupapp.util.LogUtil;
import com.tcs.pickupapp.util.ProgressCustomDialogController;
import com.tcs.pickupapp.util.SessionManager;
import com.tcs.pickupapp.util.ToastUtil;
import com.tcs.pickupapp.util.Utils;
import com.tcs.pickupapp.util.callback.ServiceError;
import com.tcs.pickupapp.util.callback.ServiceListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingFragment extends Fragment implements View.OnFocusChangeListener, BookingMVP.View, DimensionFragment.OnInteractionListener {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    private static final int REQUEST_PERMISSION_SETTING = 102;
    private static final int POP_BACK = 103;

    @BindView(R.id.editAccountNumber)
    protected CustomAutoCompleteView editAccountNumber;
    @BindView(R.id.editCustomerReference)
    protected EditText editCustomerReference;
    @BindView(R.id.editCount)
    protected TextView editCount;
    @BindView(R.id.editWeight)
    protected EditText editWeight;
    @BindView(R.id.editPieces)
    protected EditText editPieces;
    @BindView(R.id.editOtherCharges)
    protected EditText editOtherCharges;
    @BindView(R.id.editDeclareCharges)
    protected EditText editDeclareCharges;
    @BindView(R.id.editConsignmentNo)
    protected EditText editConsignmentNo;
    @BindView(R.id.spinnerPaymentMode)
    protected MaterialSpinner spinnerPaymentMode;
    @BindView(R.id.spinnerService)
    // protected MaterialSpinner spinnerService;
    protected Spinner spinnerService;
    @BindView(R.id.spinnerHandlingIns)
//    protected MaterialSpinner spinnerHandlingIns;
    protected Spinner spinnerHandlingIns;
    @BindView(R.id.spinnerBookingType)
    protected MaterialSpinner spinnerBookingType;

    @BindView(R.id.txtCustomerName)
    protected TextView txtCustomerName;

    @BindView(R.id.txtProduct)
    protected MaterialSpinner spinnerProduct;

    @BindView(R.id.verifyButton)
    protected Button verifyButton;
    @BindView(R.id.listButton)
    protected Button listButton;
    @BindView(R.id.saveButton)
    protected Button saveButton;
    @BindView(R.id.dimensionButton)
    protected Button dimensionButton;
    @BindView(R.id.cameraButton)
    protected Button cameraButton;
    @BindView(R.id.recyclerViewConsignmentNoList)
    protected RecyclerView recyclerViewConsignmentNoList;
    @BindView(R.id.txtNoConsignmentNoAvailable)
    protected TextView txtNoConsignmentNoAvailable;
    @BindView(R.id.magnifier)
    protected CustomView magnifier;
    @BindView(R.id.main_layout)
    protected LinearLayout main_layout;
    @BindView(R.id.magnify_image)
    protected ImageView magnify_image;


    @Inject
    Utils utils;
    @Inject
    LogUtil logUtil;
    @Inject
    ToastUtil toastUtil;
    public static final String PRODUCT = "Product type";
    public static final int MEDIA_TYPE_IMAGE = 1;
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 101;
    String path;
    @Inject
    protected BookingMVP.Presenter presenter;
    @Inject
    protected SessionManager sessionManager;
    Uri photoURI = null;

    List<String> payment_modes = new ArrayList<>();
    List<String> services_array = new ArrayList<>();
    List<String> booking_type = new ArrayList<>();
    List<String> handling_instructions = new ArrayList<>();
    List<String> productType = new ArrayList<>();
    List<GenerateSequence> CNSequences = new ArrayList<>();
    private static final String ARG_isRetake = "isRetake";
    private int isRetake = 0;
    String _paymentModes = "Payment", _services = "Services", _bookingType = "Single", _handlingInstructions = "", _handlingInstructionsFullText = "", _productType = "";
    private com.tcs.pickupapp.data.room.model.Booking booking;
    private CustomerInformation customerInfo;
    private ProgressCustomDialogController progressDialogControllerPleaseWait;
    private String dimensions;
    private ImageCompression imageCompression;
    public static com.tcs.pickupapp.data.room.model.Booking testBooking;
    private ArrayAdapter<String> spinnerServiceAdapter;

    public BookingFragment() {
        // Required empty public constructor
    }

    public static BookingFragment newInstance(int _isRetake) {
        BookingFragment fragment = new BookingFragment();

        Bundle args = new Bundle();
        args.putInt(ARG_isRetake, _isRetake);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((App) getActivity().getApplication()).getAppComponent().inject(this);
        if (getArguments() != null) {
            isRetake = getArguments().getInt(ARG_isRetake);
        }
        String str = "";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_booking, container, false);
        setTitle();
        utils.setupParent(getActivity(), view);
        utils.startBookingService(AppConstants.FLAG_SYNC, getActivity());
        initializeViews(view);
        setUpSpinners();

        setForcusListeners();


        /*main_layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                    magnifier.setVisibility(View.VISIBLE);
                    magnifier.setImageBitmap(screenShot(main_layout));
                    main_layout.setVisibility(View.GONE);

                return false;
            }
        });*/

        magnify_image.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    utils.hideSoftKeyboard(editAccountNumber);
                    magnifier.setVisibility(View.VISIBLE);
                    magnifier.setImageBitmap(screenShot(main_layout));
                    main_layout.setVisibility(View.GONE);
                }

                return false;
            }
        });

        magnifier.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP: {
                        magnifier.setVisibility(View.GONE);
                        main_layout.setVisibility(View.VISIBLE);
                        break;
                    }
                }
                return false;
            }
        });

        main_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {

                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        magnifier.setVisibility(View.GONE);
                        main_layout.setVisibility(View.VISIBLE);
                        break;
                    }

                    case MotionEvent.ACTION_MOVE: {

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
       /* view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    cameraButton.performClick();
                    return true;
                }

                return false;
            }
        });*/
        //insertDummyBooking();

        return view;
    }

    int i = 0;

    private void insertDummyBooking() {

        for (i = 0; i < 100; i++) {
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    logUtil.AppLog_d("dummy", "314005230" + i);
                    presenter.insertBooking(new com.tcs.pickupapp.data.room.model.Booking("cus_name",
                            "000026",
                            "",
                            "",
                            "314005230" + i++,
                            "S",//cntype
                            utils.getCurrentDateTime(AppConstants.DATE_TIME_FORMAT_FOUR),
                            "",
                            "",
                            "10",
                            "1",
                            "",
                            "",
                            "",
                            "abc",
                            "1002",
                            "",
                            "",
                            "",
                            "",
                            new byte[]{},
                            "123123123123",
                            "NT",
                            "ref",
                            isRetake,
                            0,
                            "d|d|d|", "0"
                    /*,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""*/
                    ));
                }
            }, 1000);
        }

    }

    private void setForcusListeners() {
        editAccountNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editAccountNumber, hasFocus);
            }
        });

        editWeight.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editWeight, hasFocus);
            }
        });

        editPieces.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editPieces, hasFocus);
            }
        });

        editConsignmentNo.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editConsignmentNo, hasFocus);
            }
        });

        editCustomerReference.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editCustomerReference, hasFocus);
            }
        });

        editDeclareCharges.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editDeclareCharges, hasFocus);

            }
        });

        editOtherCharges.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditBackground(editOtherCharges, hasFocus);
            }
        });
    }

    private void changeEditBackground(EditText edit, boolean hasFocus) {
        final int sdk = android.os.Build.VERSION.SDK_INT;
        if (hasFocus) {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                edit.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_red_bg));
            } else {
                edit.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_red_bg));
            }
        } else {
            if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                edit.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_bg));
            } else {
                edit.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.edit_text_bg));
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        utils.hideSoftKeyboard(editConsignmentNo);
    }

    public void setTitle() {
        if (isRetake == 0)
            ((BaseActivity) getActivity()).setTitle(getString(R.string.booking));
        else
            ((BaseActivity) getActivity()).setTitle(getString(R.string.retake_booking));
    }

    private void setUpSpinners() {

        spinnerProduct.setEnabled(false);
        productType.add(PRODUCT);

        payment_modes.add("Payment Mode");
        payment_modes.add("Account");
        payment_modes.add("Cash");
        payment_modes.add("COD");

        /*services_array.add("Services");
        services_array.add("Over Night");
        services_array.add("Second Day");
        services_array.add("OLE");
        services_array.add("Prepaid");
        services_array.add("Return");
        services_array.add("Same Day");
        services_array.add("Sunday/Holiday");
        services_array.add("Extra Special");*/

        booking_type.add("Single");
        booking_type.add("Dimension");

        if (isRetake == 0) {
            booking_type.add("Bulk");
        } else {
            spinnerBookingType.setEnabled(false);
        }

        spinnerService.setEnabled(false);
        /*handling_instructions.add("Instructions");
        handling_instructions.add("HANDLING/BYHAND(SHS)");
        handling_instructions.add("HOLD FOR COLLECTION");
        handling_instructions.add("ELECTRONIC ITEM");
        handling_instructions.add("LAPTOP/SHIPPER RISK");
        handling_instructions.add("C.O.D.");
        handling_instructions.add("SHIPPER RISK");
        handling_instructions.add("OPEN CONSG");
        handling_instructions.add("RUSH DEL");*/

        spinnerPaymentMode.setItems(payment_modes);
        spinnerPaymentMode.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                _paymentModes = payment_modes.get(position);
            }
        });

        spinnerProduct.setItems(productType);
        spinnerProduct.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                _productType = productType.get(position);

                if (_productType != null && !_productType.equalsIgnoreCase("")) {
                    presenter.fetchServicesByProduct(_productType.trim().toString());
                }

            }
        });


        spinnerBookingType.setItems(booking_type);
        spinnerBookingType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                _bookingType = booking_type.get(position);
                if (_bookingType.equals("Bulk")) {
                    dimensions = "";
                    //editConsignmentNo.requestFocus();
                    // editConsignmentNo.setText("");
                }
            }
        });

    }

    public static boolean isScanned = false;

    private void initializeViews(View view) {
        ButterKnife.bind(this, view);

        changeEditBackground(editAccountNumber, true);

        recyclerViewConsignmentNoList.setLayoutManager(new LinearLayoutManager(getActivity()));

        progressDialogControllerPleaseWait = new ProgressCustomDialogController(getActivity(), R.string.please_wait);
        imageCompression = new ImageCompression();

        LoadPendingPickups("0", isRetake);

//        presenter.fetchCustomers(getActivity());

        presenter.fetchHandlingInstructions();
        //Comment below line due to fetch service according to product
        // presenter.fetchServices();
        presenter.fetchCNSequence();

        editAccountNumber.setThreshold(1);
        // add the listener so it will tries to suggest while the user types
        editAccountNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
//                CNSequences.clear();
                spinnerProduct.setItems(PRODUCT);
                spinnerProduct.setEnabled(false);
                editCount.setText("");
                editCount.setHint("Count");
                txtCustomerName.setText("");
                spinnerPaymentMode.setItems(payment_modes);
                spinnerPaymentMode.setEnabled(true);
                presenter.filterCustomerAccount(getActivity(), s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //   editConsignmentNo.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            }
        });

        editConsignmentNo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if (keyEvent != null || keyCode == EditorInfo.IME_ACTION_DONE || /*keyCode == KeyEvent.KEYCODE_ENTER ||*/ keyCode == EditorInfo.IME_ACTION_NEXT) {
                    if (!isScanned) {
                        isScanned = true;
                        cameraButton.callOnClick();
                    }
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        editAccountNumber.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                customerInfo = (CustomerInformation) parent.getItemAtPosition(position);


                if (customerInfo.getCustomerNumber() != null) {
                    editAccountNumber.setText(customerInfo.getCustomerNumber());
                    if (isRetake == 1) {
                        if (customerInfo.getCustomerNumber() != null) {
                            presenter.fetchRetakeBookingCountsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                        }
                    } else {
                        if (customerInfo.getCustomerNumber() != null) {
                            presenter.fetchBookingCountsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                        }
                        // presenter.fetchBookings(customerInfo.getCustomerNumber());
                    }
                }


                /*
                 * set product type
                 * fetching CN numbers against product type
                 * */
                if (customerInfo.getProducts() != null)

                {
                    _paymentModes = "Account";
                    spinnerPaymentMode.setItems("Account");
                    spinnerPaymentMode.setEnabled(false);
                    spinnerProduct.setItems(customerInfo.getProducts());
                    if (spinnerProduct.getItems().size() > 1) {
                        spinnerProduct.setEnabled(true);
                        productType = customerInfo.getProducts();
                    }
                    txtCustomerName.setText("Customer: " + customerInfo.getCustomerName());
                    if (CNSequences.size() == 0) {
                        presenter.fetchCNSequence();
                    }
                }


                if (customerInfo != null) {
                    if (isRetake == 1) {
                        if (customerInfo.getCustomerNumber() != null) {
                            presenter.fetchRetakeBookingsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                        }
                    } else {
                        if (customerInfo.getCustomerNumber() != null) {
                            presenter.fetchBookingsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                            //presenter.fetchBookingsByAccountNo(customerInfo.getCustomerNumber().toString());
                        }
                    }
                }


                if (customerInfo != null)

                {
                    if (customerInfo.getProducts() != null && customerInfo.getProducts().size() > 0) {
                        if (customerInfo.getProducts().get(0) != null) {
                            presenter.fetchServicesByProduct(customerInfo.getProducts().get(0));
                        }
//                        else if (customerInfo.getProducts().get(0) != null
//                                && customerInfo.getProducts().get(1) != null &&
//                                customerInfo.getProducts().size() == 2) {
//                            presenter.fetchServicesHavingMoreProducts(customerInfo.getProducts().get(0), customerInfo.getProducts().get(1));
//                        }
                    }
                }

                spinnerService.setEnabled(true);
                presenter.fetchHandlingInstructions();
                editCustomerReference.setText("");

            }
        });

        verifyButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (utils.isInternetAvailableMoreAccurate()) {
                    String customerNumber = editAccountNumber.getText().toString().trim();

                    if (customerNumber.equals("")) {
                        editAccountNumber.setError("Please enter account number");
                        utils.playErrorToneAndVibrate(getActivity());
                        editAccountNumber.requestFocus();
                        return;
                    }

                    presenter.verifyAccountNo(customerNumber);
                } else {
                    showToastShortTime(getString(R.string.please_check_internet_connection));
                }
            }
        });

        dimensionButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {

                //Comment below code as instrcution recieved by Mr.Ahsan Aness
             /*   if (_bookingType.equals("Bulk")) {
                    setScanFalse();
                    toastUtil.showToastShortTime("Select 'Dimension' from CN Type");
                    return;
                }
                if (_bookingType.equals("Single")) {
                    setScanFalse();
                    toastUtil.showToastShortTime("Select 'Dimension' from CN Type");
                    return;
                }*/


                final String consignment = editConsignmentNo.getText().toString().trim();
                if (utils.isValidCN(CNSequences, consignment, editConsignmentNo)) {
                    presenter.checkAssignment(consignment, new ServiceListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean b) {
                            if (!b || isRetake == 1) {

                                String piece = editPieces.getText().toString().trim();
                                String weight = editWeight.getText().toString().trim();

                                if (utils.isTextNullOrEmpty(weight)) {
                                    setScanFalse();
                                    editWeight.setError("Please enter Weight");
                                    utils.playErrorToneAndVibrate(getActivity());
                                    editWeight.requestFocus();
                                    return;
                                }

                                if (weight.startsWith(".")) {
//                                    editWeight.setError("invalid");
                                    weight = new StringBuffer(weight).insert(0, "0").toString();
                                }
                                weight = String.valueOf(Float.parseFloat(weight));

                                editWeight.setText(weight);
                                if (Float.parseFloat(weight) == 0 || Float.parseFloat(weight) < 0.5) {
                                    setScanFalse();
                                    editWeight.setError("Weight should be minimum 0.5");
                                    utils.playErrorToneAndVibrate(getActivity());
                                    editWeight.requestFocus();
                                    return;
                                }

                                if (utils.isTextNullOrEmpty(piece)) {
                                    setScanFalse();
                                    editPieces.setError("Please enter Pieces");
                                    utils.playErrorToneAndVibrate(getActivity());
                                    editPieces.requestFocus();
                                    return;
                                }
                                piece = String.valueOf(Integer.parseInt(piece));
                                editPieces.setText(piece);

                                if (Integer.parseInt(piece) < 1) {
                                    setScanFalse();
                                    editPieces.setError("Minimum 1 piece required");
                                    utils.playErrorToneAndVibrate(getActivity());
                                    editPieces.requestFocus();
                                    return;
                                } else {
                                    editPieces.setError(null);
                                }

                                removeEditErrors();
                                DimensionFragment dimensionFragment = DimensionFragment.newInstance(consignment, piece, weight);
                                dimensionFragment.setTargetFragment(BookingFragment.this, POP_BACK);
                                ((BaseActivity) getActivity()).addFragment(dimensionFragment);


                            } else {
                                setScanFalse();
                                editConsignmentNo.setError("DUPLICATE CN");
                                utils.playErrorToneAndVibrate(getActivity());
                                editConsignmentNo.requestFocus();

                            }
                        }

                        @Override
                        public void onError(ServiceError error) {
                            setScanFalse();
                            toastUtil.showToastShortTime(error.getMessage());
                        }
                    });
                } else {
                    setScanFalse();
                    /*editConsignmentNo.setError("Invalid CN");
                    utils.playErrorToneAndVibrate(getActivity());
                    editConsignmentNo.requestFocus();*/
                }
            }
        });

        cameraButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                CameraClickConditions();

            }
        });

        saveButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                LoadPendingPickupsWithEmail("0", isRetake);
//                presenter.generatePipeSeperatedBookingsData(editAccountNumber.getText().toString());
            }
        });

        listButton.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

                removeEditErrors();

                if (!utils.isEditTextEmpty(editAccountNumber)) {
                    ((BaseActivity) getActivity()).addFragment(BookingListFragment.newInstance(editAccountNumber.getText().toString(), isRetake));
                } else {
                    toastUtil.showToastShortTime("Please enter account number");
                }
            }
        });
    }


    private void createBatteryDialog(int batLevel) {
        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage("Battery Level is " + batLevel + "%, booking is not allowed.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        android.support.v7.app.AlertDialog alert = builder.create();
        alert.show();
    }

    private Dialog dialog;

    private void showAlertDialog(int batLevel) {
        dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.low_battery_dialog);

        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        txtMessage.setText("Battery Level is " + batLevel + "%, booking is not allowed.");
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);
        Button submit = dialog.findViewById(R.id.submit);
        txtTitle.setText("Alert");
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public Bitmap screenShot(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),
                view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private void removeEditErrors() {
        editWeight.setError(null);
        editConsignmentNo.setError(null);
        editAccountNumber.setError(null);
    }

    private void checkValidation() {
        if (validate()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestForPermissions();
            } else {
                hideSoftKeyboard();
                if (_bookingType.equals("Single") || _bookingType.equals("Dimension")) {
                    openCameraForImage();
                } else if (_bookingType.equals("Bulk")) {
                    openBarCodeFragment();
                }
            }
        } else {
            setScanFalse();
        }
    }

    /*private boolean isValidCN(String consignment) {
        boolean valid = false;

        if (utils.isTextNullOrEmpty(consignment)) {
            editConsignmentNo.setError("Please enter Consignment Number");
            utils.playErrorToneAndVibrate(getActivity());
            editConsignmentNo.requestFocus();
            ;
            valid = false;
        } else if (consignment.length() < MIN_CN_LENGTH) {
            editConsignmentNo.setError("length of CN should be " + String.valueOf(MIN_CN_LENGTH) + " or greater");
            utils.playErrorToneAndVibrate(getActivity());
            editConsignmentNo.requestFocus();
            valid = false;
        } else if (CNSequences.size() > 0) {
            *//*
     * check..
     * CN available in Generate CN Table or Not
     * *//*
            for (GenerateSequence cnSequence : CNSequences) {
                long cn = Long.parseLong(consignment);
                long from = Long.parseLong(cnSequence.getCN_from());
                long to = Long.parseLong(cnSequence.getCN_to());
                if (cn >= from && cn <= to) {
                    valid = true;
                    editConsignmentNo.setError(null);
                    break;
                }
            }
            if (!valid) {
                editConsignmentNo.setError("Invalid CN");
                utils.playErrorToneAndVibrate(getActivity());
                editConsignmentNo.requestFocus();
            }

        } else {
            presenter.fetchCNSequence();
            valid = false;

            editConsignmentNo.setError("Invalid CN");
            utils.playErrorToneAndVibrate(getActivity());
            editConsignmentNo.requestFocus();
        }

        return valid;
    }*/

    public void requestForPermissions() {

        if (ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            //Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.CAMERA)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                openAppSettingsDialog();
            } else {

                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                android.Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            checkBookingTypeOpenCamera();
        }
    }

    private void checkBookingTypeOpenCamera() {
        if ((_bookingType.equals("Single")) || (_bookingType.equals("Dimension"))) {
            openCameraForImage();
        } else {
            openBarCodeFragment();
        }
    }


    private void showCancelDialog(Context context, String title, String message) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.fragment_cancel_dialog);

        Button cancel = dialog.findViewById(R.id.cancel);
        Button submit = dialog.findViewById(R.id.submit);
        submit.setText("Go to settings");
        cancel.setText("Cancel");
        TextView txtMessage = dialog.findViewById(R.id.txtMessage);
        TextView txtTitle = dialog.findViewById(R.id.txtTitle);

        txtMessage.setText("" + message);
        txtTitle.setText("" + title);

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                intent.setData(uri);
                startActivityForResult(intent, REQUEST_PERMISSION_SETTING);

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


    private void openAppSettingsDialog() {
        showCancelDialog(getActivity(), "Permission Disabled",
                "Please enable the permission in \n  Settings>Applications>PickUp App>Permission ");
        /*new AlertDialog.Builder(getActivity())
                .setTitle("Permission Disabled")
                .setMessage("Please enable the permission in \n  Settings>Applications>PickUp App>Permission ")
                .setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();*/
    }

    public void openCameraForImage() {
        CaptureCameraImageFragment captureCameraImageFragment = CaptureCameraImageFragment.newInstance(editConsignmentNo.getText().toString());
        captureCameraImageFragment.setTargetFragment(BookingFragment.this, POP_BACK);
        ((BaseActivity) getActivity()).addFragment(captureCameraImageFragment);
    }

    private void openBarCodeFragment() {
        int Width_Spec = 2560;
        int Height_Spec = 1440;
       /* BarcodeCaptureFragment barcodeCaptureFragment = new BarcodeCaptureFragment().newInstance(true, booking, CNSequences, presenter);
        barcodeCaptureFragment.setTargetFragment(BookingFragment.this, POP_BACK);
        ((BaseActivity) getActivity()).addFragment(barcodeCaptureFragment, "barcodeFragment");*/
        BarcodeCaptureActivity barcodeCaptureFragment = new BarcodeCaptureActivity();
        Intent intent = new Intent(getActivity(), barcodeCaptureFragment.getClass());

        intent.putExtra(BarcodeCaptureActivity.AutoFocus, true);
        intent.putExtra(BarcodeCaptureActivity.UseFlash, false);
        intent.putExtra(BarcodeCaptureActivity.Width_Spec, Width_Spec);
        intent.putExtra(BarcodeCaptureActivity.Height_Spec, Height_Spec);
        intent.putExtra(BarcodeCaptureActivity.Bulk, true);
        BarcodeCaptureActivity.CNSequences = CNSequences;
        startActivityForResult(intent, BarcodeCaptureActivity.REQUEST_CAPTURE_BARCODE);
        barcodeCaptureFragment.GetValues(booking, CNSequences, presenter);

    }

    private boolean validate() {
        if (utils.isTextNullOrEmpty(editAccountNumber.getText().toString().trim())) {
            editAccountNumber.setError("Please enter account number");
            utils.playErrorToneAndVibrate(getActivity());
            editAccountNumber.requestFocus();
            return false;
        } else {
            editAccountNumber.setError(null);
        }

//        if (utils.isTextNullOrEmpty(spinnerProduct.getItems().get(spinnerProduct.getSelectedIndex()).toString())) {
        if (spinnerProduct.getItems().get(spinnerProduct.getSelectedIndex()).toString().equals(PRODUCT)) {
            showToastShortTime("Account not selected or not verified");
            utils.playErrorToneAndVibrate(getActivity());
            return false;
        }
        _productType = spinnerProduct.getItems().get(spinnerProduct.getSelectedIndex()).toString();

        if (utils.isTextNullOrEmpty(editWeight.getText().toString().trim())) {
            editWeight.setError("Please enter Weight");
            utils.playErrorToneAndVibrate(getActivity());
            editWeight.requestFocus();
            return false;
        }

        editWeight.setText(String.valueOf(Float.parseFloat(editWeight.getText().toString().trim())));
        if (editWeight.getText().toString().trim().startsWith(".")) {
            String weight = editWeight.getText().toString().trim();
            weight = new StringBuffer(weight).insert(0, "0").toString();
            editWeight.setText(String.valueOf(Float.parseFloat(weight)));
        }

        if (Float.parseFloat(editWeight.getText().toString().trim()) == 0 ||
                Float.parseFloat(editWeight.getText().toString().trim()) < 0.5) {
            editWeight.setError("Weight should be minimum 0.5");
            utils.playErrorToneAndVibrate(getActivity());
            editWeight.requestFocus();
            return false;
        } else {
            editWeight.setError(null);
        }

        if (utils.isTextNullOrEmpty(editPieces.getText().toString().trim())) {
            editPieces.setError("Please enter Pieces");
            utils.playErrorToneAndVibrate(getActivity());
            editPieces.requestFocus();
            return false;
        }

        editPieces.setText(String.valueOf(Integer.parseInt(editPieces.getText().toString().trim())));

        if (Integer.parseInt(editPieces.getText().toString().trim()) < 1) {
            editPieces.setError("Minimum 1 piece required");
            utils.playErrorToneAndVibrate(getActivity());
            editPieces.requestFocus();
            return false;
        } else {
            editPieces.setError(null);
        }

        if (_paymentModes.equals("Payment")) {
            showToastShortTime("Select payment type");
            utils.playErrorToneAndVibrate(getActivity());
            return false;
        } else if (_services.equals("Services")) {
            showToastShortTime("Select service type");
            utils.playErrorToneAndVibrate(getActivity());
            return false;
        }

        if (_bookingType.equals("Single")) {
            if (utils.isTextNullOrEmpty(editConsignmentNo.getText().toString().trim())) {
//            showToastShortTime("Please enter CN number");
                editConsignmentNo.setError("Please enter CN number");
                utils.playErrorToneAndVibrate(getActivity());
                editConsignmentNo.requestFocus();
                return false;
            } else if (!utils.isValidCN(CNSequences, editConsignmentNo.getText().toString().trim(), editConsignmentNo)) {
                editConsignmentNo.setError("Invalid CN");
                utils.playErrorToneAndVibrate(getActivity());
                editConsignmentNo.requestFocus();
                editConsignmentNo.setSelection(0, editConsignmentNo.getText().length());
                return false;
            } else {
                editConsignmentNo.setError(null);
            }
        } else if (_bookingType.equals("Bulk") && isRetake == 1) {
            showToastShortTime("Bulk Booking is not allowed on Retake");
            utils.playErrorToneAndVibrate(getActivity());
        }

        String declearCharges = editDeclareCharges.getText().toString().trim();
        if (!declearCharges.equals("") && Integer.parseInt(declearCharges) == 0) {
            editDeclareCharges.setError("Charges should be greater than 0");
            utils.playErrorToneAndVibrate(getActivity());
            editDeclareCharges.requestFocus();
            return false;
        }

        String otherCharges = editOtherCharges.getText().toString().trim();
        if (!otherCharges.equals("") && Integer.parseInt(otherCharges) == 0) {
            editOtherCharges.setError("Charges should be greater than 0");
            utils.playErrorToneAndVibrate(getActivity());
            editOtherCharges.requestFocus();
            return false;
        }

        try {
            if (_handlingInstructions.equals("")) {
                _handlingInstructionsFullText = "";
            }
            booking = new com.tcs.pickupapp.data.room.model.Booking(customerInfo.getCustomerName(),
                    editAccountNumber.getText().toString().trim(),
                    _handlingInstructions,
                    _handlingInstructionsFullText,
                    editConsignmentNo.getText().toString().trim(),
                    "S",//cntype
                    utils.getCurrentDateTime(AppConstants.DATE_TIME_FORMAT_FOUR),
                    sessionManager.getCurrentLatitude(),
                    sessionManager.getCurrentLongitude(),
                    editPieces.getText().toString().trim(),
                    editWeight.getText().toString().trim(),
                    _services,
                    _paymentModes,
                    editDeclareCharges.getText().toString().trim(),
                    customerInfo.getCustomerName(),
                    String.valueOf(sessionManager.getCourierInfo().getCourierCode()),
                    _productType,
                    customerInfo.getRoute(),
                    customerInfo.getStation(),
                    editOtherCharges.getText().toString().trim(),
                    new byte[]{},
                    utils.getDeviceImeiNumber(getActivity()),
                    "NT",
                    editCustomerReference.getText().toString().trim(),
                    isRetake,
                    0,
                    dimensions, "0"
                    /*,
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    "",
                    ""*/
            );
        } catch (Exception ex) {
            ex.printStackTrace();
            toastUtil.showToastShortTime(ex.getMessage());
            return false;
        }

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            setScanFalse();
            setTitle();
            byte[] bitmapData = data.getByteArrayExtra("ImageData");
            new CompressImage().execute(bitmapData);
            clearFields();


        } else if (requestCode == REQUEST_PERMISSION_SETTING) {
            requestForPermissions();
        } else if (requestCode == BarcodeCaptureActivity.REQUEST_CAPTURE_BARCODE && resultCode == CommonStatusCodes.SUCCESS) {
            spinnerHandlingIns.setSelection(0);
            clearFieldsForBulk();

        } else if (resultCode == Activity.RESULT_OK && requestCode == POP_BACK) {
            String pieces = data.getStringExtra("PIECES");
            String weight = data.getStringExtra("WEIGHT");
            editPieces.setText(pieces);
            editWeight.setText(weight);

//            presenter.fetchDimensions(editConsignmentNo.getText().toString());

            if (utils.isValidCN(CNSequences, editConsignmentNo.getText().toString().trim(), editConsignmentNo)) {
                presenter.checkAssignment(editConsignmentNo.getText().toString().trim(), new ServiceListener<Boolean>() {
                    @Override
                    public void onSuccess(Boolean b) {
                        if (!b || isRetake == 1) {
                            presenter.fetchDimensions(editConsignmentNo.getText().toString());
                        } else {
                            setScanFalse();
                            editConsignmentNo.setError("DUPLICATE CN");
                            utils.playErrorToneAndVibrate(getActivity());
                            editConsignmentNo.requestFocus();
                        }
                    }

                    @Override
                    public void onError(ServiceError error) {
                        setScanFalse();
                        toastUtil.showToastShortTime(error.getMessage());
                    }
                });
            } else {
                setScanFalse();
                        /*editConsignmentNo.setError("Invalid CN");
                        utils.playErrorToneAndVibrate(getActivity());
                        editConsignmentNo.requestFocus();*/
            }
        }
    }

    private void clearFields() {
        /*CLEAR ALL THE FIELDS EXCEPT CUSTOMER ACCOUNT*/
        photoURI = null;
        dimensions = "";
        //editConsignmentNo.setText("");
        // commented by Shahrukh. This is because we want to create image file with name same as consignment number.
        // This edittext will be cleared in createLogFile function
        editWeight.setText("0.5");
        editPieces.setText("1");
        editOtherCharges.setText("");
        editDeclareCharges.setText("");
        editOtherCharges.setText("");
        editCustomerReference.setText("");
        assignSpinnerVariables();
    }


    private void assignSpinnerVariables() {

        _bookingType = "Single";
        _handlingInstructions = "";
        _handlingInstructionsFullText = "";

        spinnerPaymentMode.setSelectedIndex(0);
        //spinnerService.setSelection(0); // added by Shahrukh on Ahsan Anis's request
        spinnerBookingType.setSelectedIndex(0);
        spinnerHandlingIns.setSelection(0);
//        spinnerHandlingIns.setSelectedIndex(0);
    }

    @Override
    public void showToastShortTime(String message) {
        toastUtil.showToastShortTime(message);
    }

    @Override
    public void showToastLongTime(String message) {
        toastUtil.showToastLongTime(message);
    }

    @Override
    public void setautocompleteAdapter(AutocompleteCustomArrayAdapter autocompleteCustomArrayAdapter) {
        editAccountNumber.setAdapter(autocompleteCustomArrayAdapter);
    }

    @Override
    public void verifyAccountNumberSuccess(CustomerInformation customerInformation) {
        Log.d("verify", customerInformation + "");
        toastUtil.showToastShortTime("Customer is verified");
        presenter.fetchCustomers(getActivity());

        customerInfo = customerInformation;

        if (customerInfo.getCustomerNumber() != null) {
            editAccountNumber.setText(customerInfo.getCustomerNumber());
            editAccountNumber.clearFocus();
            txtCustomerName.setText("Customer: " + customerInfo.getCustomerName());
            if (isRetake == 1) {
                if (customerInfo.getCustomerNumber() != null) {
                    presenter.fetchRetakeBookingCountsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                }
            } else {
                if (customerInfo.getCustomerNumber() != null) {
                    presenter.fetchBookingCountsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                }
                // presenter.fetchBookings(customerInfo.getCustomerNumber());
            }
        }




        /*
         * set product type
         * fetching CN numbers against product type
         * */
        if (customerInfo.getProducts() != null) {
            _paymentModes = "Account";
            spinnerPaymentMode.setItems("Account");
            spinnerPaymentMode.setEnabled(false);
            productType = customerInfo.getProducts();
            spinnerProduct.setItems(customerInfo.getProducts());
            if (spinnerProduct.getItems().size() > 1) {
                spinnerProduct.setEnabled(true);
            }
            presenter.fetchCNSequence();
        }


        if (customerInfo != null) {
            if (isRetake == 1) {
                if (customerInfo.getCustomerNumber() != null) {
                    presenter.fetchRetakeBookingsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                }
            } else {
                if (customerInfo.getCustomerNumber() != null) {
                    presenter.fetchBookingsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                    //presenter.fetchBookingsByAccountNo(customerInfo.getCustomerNumber().toString());
                }
            }
        }


        if (customerInfo != null) {
            if (customerInfo.getProducts() != null && customerInfo.getProducts().size() > 0) {
                if (customerInfo.getProducts().get(0) != null) {
                    presenter.fetchServicesByProduct(customerInfo.getProducts().get(0));
                }
//                else if (customerInfo.getProducts().get(0) != null
//                        && customerInfo.getProducts().get(1) != null &&
//                        customerInfo.getProducts().size() == 2
//                        ) {
//                    presenter.fetchServicesHavingMoreProducts(customerInfo.getProducts().get(0), customerInfo.getProducts().get(1));
//                }
            }
        }

        spinnerService.setEnabled(true);

    }

    @Override
    public void verifyAccountNumberError(ServiceError serviceError) {
        Log.d("verify", "msg: " + serviceError.getMessage());
        editCount.setText("0");
        showToastShortTime(serviceError.getMessage());
    }

    @Override
    public void logBookings(List<com.tcs.pickupapp.data.room.model.Booking> bookings) {
        logUtil.AppLog_v("Bookings", bookings.toString());
    }

    @Override
    public void startSyncService() {
        if (getActivity() != null) {
            utils.startBookingService(AppConstants.FLAG_SYNC, getActivity());
        }
    }

    @Override
    public void setCNSequences(List<GenerateSequence> generateSequences) {
        CNSequences.clear();
        CNSequences = generateSequences;
    }

    @Override

    public void showWaitDialog() {
        progressDialogControllerPleaseWait.showDialog();
    }

    @Override
    public void hideWaitDialog() {
        progressDialogControllerPleaseWait.hideDialog();
    }

    @Override
    public void setDimensions(String dimensions) {
        logUtil.AppLog_v("dimension", dimensions + "");
        this.dimensions = dimensions;
        checkValidation();
    }

    public void setBookingCount(int count) {
        editCount.setText(count + "");
    }

    @Override
    public void setHandlindingInstructions(final List<HandlingInstruction> list) {
        List<String> listString = new ArrayList<>();
        listString.clear();
        for (HandlingInstruction handlingInstruction : list) {
            listString.add(handlingInstruction.getName());
        }
        listString.add(0, "Select Handling Instructions");
        list.add(0, new HandlingInstruction("", "Select Handling Instructions"));
//        spinnerHandlingIns.setItems(listString);
//        spinnerHandlingIns.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
//
//            @Override
//            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
//                logUtil.AppLog_d("umair", "" + list.get(position).getName() + "->" + list.get(position).getNumber());
//                _handlingInstructions = list.get(position).getNumber();
//            }
//        });


// Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listString);
// Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerHandlingIns.setAdapter(dataAdapter);
        spinnerHandlingIns.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //logUtil.AppLog_d("umair", "" + list.get(position).getName() + "->" + list.get(position).getNumber()+ "->" + list.get(position).getName());
                _handlingInstructions = list.get(position).getNumber();
                _handlingInstructionsFullText = list.get(position).getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        spinnerHandlingIns.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                spinnerHandlingIns.performClick();
                editConsignmentNo.requestFocus();
                return true;
            }
        });
    }

    @Override
    public void setServices(final List<com.tcs.pickupapp.data.room.model.Service> list) {
//        spinnerService.setItems(list);


        List<String> listString = new ArrayList<>();
        listString.clear();
        for (com.tcs.pickupapp.data.room.model.Service service : list) {
            listString.add(service.getDescription());
        }
        listString.add(0, "Services");
        // spinnerService.setItems(listString);
//        spinnerService.setEnabled(true);

// Creating adapter for spinner
        spinnerServiceAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, listString);
// Drop down layout style - list view with radio button
        spinnerServiceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinnerService.setAdapter(spinnerServiceAdapter);
        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    _services = "Services";
                } else {
                    _services = list.get(position - 1).getDescription() + "|" + list.get(position - 1).getNumber();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


      /*  spinnerService.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {

            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                //logUtil.AppLog_d("umair", "" + list.get(position-1).getDescription() + "->" + list.get(position-1).getNumber());
                //if (position != 0) {
                if (position == 0) {
                    _services = "Services";
                } else {
                    _services = list.get(position - 1).getDescription() + "|" + list.get(position - 1).getNumber();
                }
                //}
            }
        });*/



        /*_services = list.get(0).getDescription() + "|" + list.get(0).getNumber();*/

//        spinnerService.setPrompt("services");
        /*spinnerService.setAdapter(new ServicesAdapter(getActivity(),list));
        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener(){
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position != 0) {
                    _services = list.get(position).getNumber();
                }
            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.setView(this, isRetake);
        setTitle();
        turnGPSOn();
        setTitle();
    }

    @Override
    public void updateWeightAndPiece(String pieces, String weight) {
        editPieces.setText(pieces);
        editWeight.setText(weight);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {

    }

    // Image Compression Code Below
// Added by Shahrukh Malik
    private class CompressImage extends AsyncTask<byte[], Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (!(getActivity().isFinishing())) {
                progressDialogControllerPleaseWait.showDialog();
            }
        }

        @Override
        protected String doInBackground(byte[]... params) {

            Bitmap bitmap = BitmapFactory.decodeByteArray(params[0], 0, params[0].length);

            String photoPath = "";
            if (bitmap != null) {
                photoPath = savePhoto(bitmap);
                bitmap.recycle();
            }

            Bitmap bitmapCompressed = imageCompression.getCompressedBitmapMoreOptimized(photoPath, getActivity());
            if (bitmap != null) {
                String newPath = savePhoto(bitmapCompressed);
                bitmap.recycle();
                return newPath;
            } else {
                return "";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialogControllerPleaseWait.hideDialog();
            try {
                File newFile = new File(s);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    photoURI = FileProvider.getUriForFile(getActivity(),
                            getString(R.string.file_provider_authority),
                            newFile);
                } else {
                    photoURI = Uri.fromFile(newFile);
                }
                booking.setImage(utils.uriToByteArr(photoURI));
                testBooking = booking;
                booking.setIsSave("0");
                presenter.insertBooking(booking);

                saveButton.setEnabled(true);

                if (isRetake == 1) {
                    if (customerInfo.getCustomerNumber() != null) {
                        presenter.fetchRetakeBookingCountsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                    }
                } else {
                    if (customerInfo.getCustomerNumber() != null) {
                        presenter.fetchBookingCountsByAccountNo(customerInfo.getCustomerNumber().toString(), isRetake);
                    }
                }

                presenter.setConsignmentAdapter(booking);
                createLogFile();
                clearFields();

            } catch (Exception ex) {
                toastUtil.showToastLongTime(ex.getMessage());
                ex.printStackTrace();
                return;
            }
        }

    }

    private void createLogFile() {
        String formattedDate = getCurrentDate();
        String dateforFile = getCurrentDateForFile();
        if (sessionManager != null) {
            if (sessionManager.getCourierCode() != null && !sessionManager.getCourierCode().equals("")) {
                generateNoteOnSD(sessionManager.getCourierCode(),
                        sessionManager.getCourierCode() + "|" +
                                sessionManager.getCurrentLatitude() + "|" +
                                sessionManager.getCurrentLongitude() + "|" +
                                dateforFile + "|" +
                                editConsignmentNo.getText().toString().trim() + "|" +
                                txtCustomerName.getText().toString().split(":")[1].trim() + "|" +
                                "PICKED" + "|" +
                                sessionManager.getCourierRoute() + "|" +
                                "PICKED" + "|",
                        formattedDate);
            }
        }
        editConsignmentNo.setText("");
    }

    public String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(AppConstants.DATE_TIME_FORMAT_THREE);
        return df.format(c.getTime());
    }

    public String getCurrentDateForFile() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(AppConstants.DATE_TIME_FORMAT_TWO);
        return df.format(c.getTime());
    }


    public void generateNoteOnSD(String courierCode, String sBody, String formattedDate) {
        try {
            File root = new File(Environment.getExternalStorageDirectory(), AppConstants.DIRECTORY_PICKUP_TRAVEL_LOG);
            Log.i("Body: ", sBody);
            if (!root.exists()) {
                root.mkdirs();
            }
            Thread.sleep(200);
            File gpxfile = null;
            // Checking if file exists in today's date
            File[] files = root.listFiles();
            boolean isFound = false;
            for (File file : files) {
                if (file.getName().length() > 18) {
                    String filenameWithoutTime = file.getName().split("_")[0];
                    String courierCodeInFile = file.getName().split("_")[2].split("\\.")[0];
                    String dateOnly = "PK" + formattedDate.split("_")[0];
                    if (filenameWithoutTime.equals(dateOnly)) {
                        if (courierCodeInFile.equals(courierCode)) {
                            gpxfile = file;
                            isFound = true;
                            break;
                        }
                    }
                }
            }
            if (!isFound) {
                gpxfile = new File(root, "PK" + formattedDate + courierCode + ".txt");
            }
            FileOutputStream outputStream = new FileOutputStream(gpxfile, true);
            outputStream.write(sBody.getBytes());
            String br = "\r\n";
            outputStream.write(br.getBytes());
            outputStream.flush();
            outputStream.close();
            refreshFileManager(gpxfile.getPath());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public String savePhoto(Bitmap bmp) {
        FileOutputStream out = null;
        try {
            File imageFile = utils.getOutputMediaFile(MEDIA_TYPE_IMAGE, editConsignmentNo.getText().toString());
            out = new FileOutputStream(imageFile);
            bmp.compress(Bitmap.CompressFormat.JPEG, 80, out);
            out.flush();
            out.close();
            imageCompression.refreshGallery(imageFile.getAbsolutePath(), getActivity());
            out = null;
            return imageFile.getAbsolutePath();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public void refreshFileManager(String filePath) {
        try {
            MediaScannerConnection
                    .scanFile(
                            getActivity(),
                            new String[]{filePath},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(
                                        String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned "
                                            + path + ":");
                                    Log.i("ExternalStorage", "-> uri="
                                            + uri);

                                }
                            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void turnGPSOn() {
        try {
            final LocationManager manager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            } else {
                utils.startGPSTrackerService(getActivity().getApplicationContext());
            }
        } catch (Exception e) {

        }
    }

    @Override
    public void setRecyclerViewAccountHistory(AccountDetailAdapter accountHistoryAdapter) {
        recyclerViewConsignmentNoList.setAdapter(accountHistoryAdapter);
    }

    @Override
    public void clearCNNumberField() {
        editConsignmentNo.setText("");
    }

    @Override
    public void setBookingCounts(long counts) {
        try {
            if (counts > 0) {
                saveButton.setEnabled(false);
            } else {
                saveButton.setEnabled(true);
            }

        } catch (Exception e) {

        }
    }

    @Override
    public void enableSaveButton() {
        saveButton.setEnabled(true);
    }


    @Override
    public void disableCustomerAccountSpinner(String accountNo) {
        editAccountNumber.setEnabled(false);
        editAccountNumber.setText(accountNo);
        presenter.verifyAccountNo(accountNo);
    }

    @Override
    public void enableCustomerAccountSpinner() {
        editAccountNumber.setEnabled(true);
    }


    private void clearFieldsForBulk() {
        /*CLEAR ALL THE FIELDS EXCEPT CUSTOMER ACCOUNT*/
        photoURI = null;
        dimensions = "";
        //editConsignmentNo.setText("");
        // commented by Shahrukh. This is because we want to create image file with name same as consignment number.
        // This edittext will be cleared in createLogFile function
        editWeight.setText("0.5");
        editPieces.setText("1");
        editOtherCharges.setText("");
        editDeclareCharges.setText("");
        editOtherCharges.setText("");
        editCustomerReference.setText("");
        assignSpinnerVariables();
    }

    @Override
    public void clearAllFields() {
        try {


            photoURI = null;
            dimensions = "";
            editConsignmentNo.setText("");
            editWeight.setText("0.5");
            editPieces.setText("1");
            editOtherCharges.setText("");
            editDeclareCharges.setText("");
            editOtherCharges.setText("");
            editCustomerReference.setText("");
            editAccountNumber.setText("");
            txtCustomerName.setText("");

            _bookingType = "Single";
            _handlingInstructions = "";
            _handlingInstructionsFullText = "";

            spinnerPaymentMode.setSelectedIndex(0);
            spinnerService.setSelection(0);
            spinnerBookingType.setSelectedIndex(0);
            spinnerHandlingIns.setSelection(0);

            spinnerServiceAdapter.clear();
            spinnerServiceAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    @Override
    public void setFocusOnEdtConsignment() {
        editConsignmentNo.requestFocus();
    }

    @Override
    public void showLoadPendingPickupsStatus(List<com.tcs.pickupapp.data.room.model.Booking> bookings) {
        try {
            //presenter.fetchCustomers(getActivity());
            //editAccountNumber.setEnabled(true);
            //editAccountNumber.setText(bookings.get(0).getCustomerNumber());

            if (bookings != null && bookings.size() > 0) {
                editAccountNumber.setEnabled(false);
                editAccountNumber.setText(bookings.get(0).getCustomerNumber());
            } else {
                editAccountNumber.setEnabled(true);
                presenter.fetchCustomers(getActivity());
            }

        } catch (Exception e) {
            presenter.fetchCustomers(getActivity());
            editAccountNumber.setEnabled(true);
            presenter.generatePipeSeperatedBookingsData(editAccountNumber.getText().toString());
        }

    }


    @Override
    public void showLoadPendingPickupsStatusWithEmail(List<com.tcs.pickupapp.data.room.model.Booking> bookings) {
        try {


            editAccountNumber.setEnabled(true);
            presenter.generatePipeSeperatedBookingsData(editAccountNumber.getText().toString());


        } catch (Exception e) {
            editAccountNumber.setEnabled(true);
            presenter.generatePipeSeperatedBookingsData(editAccountNumber.getText().toString());
        }

    }

    @Override
    public void showRecyclerViewReports() {
        recyclerViewConsignmentNoList.setVisibility(View.VISIBLE);
        txtNoConsignmentNoAvailable.setVisibility(View.GONE);
    }

    @Override
    public void showTxtNoAccountHistoryFound() {
        txtNoConsignmentNoAvailable.setVisibility(View.VISIBLE);
        recyclerViewConsignmentNoList.setVisibility(View.GONE);
    }

    public void CameraClickConditions() {
        Log.i("BatLevel ", utils.getBatteryLevel() + "");
        try {
            if (!utils.isTextNullOrEmpty(sessionManager.getMinimumCameraBarcodePercent())) {
                AppConstants.CAMERA_BARCODE_LEVEL = Integer.parseInt(sessionManager.getMinimumCameraBarcodePercent());
                setScanFalse();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (utils.getBatteryLevel() <= AppConstants.CAMERA_BARCODE_LEVEL) {
            // createBatteryDialog(utils.getBatteryLevel());
            showAlertDialog(utils.getBatteryLevel());
            setScanFalse();
        } else {
            final String consignment = editConsignmentNo.getText().toString().trim();
            if (_bookingType.equals("Bulk")) {
                checkValidation();
            } else if (_bookingType.equals("Dimension")) {
                dimensionButton.callOnClick();
            } else {
//                    if (isValidCN(editConsignmentNo.getText().toString().trim())) {
                if (utils.isValidCN(CNSequences, editConsignmentNo.getText().toString().trim(), editConsignmentNo)) {
                    presenter.checkAssignment(editConsignmentNo.getText().toString().trim(), new ServiceListener<Boolean>() {
                        @Override
                        public void onSuccess(Boolean b) {
                            if (!b || isRetake == 1) {
                                presenter.fetchDimensions(consignment);
                            } else {
                                setScanFalse();
                                editConsignmentNo.setError("DUPLICATE CN");
                                utils.playErrorToneAndVibrate(getActivity());
                                editConsignmentNo.requestFocus();
                            }
                        }

                        @Override
                        public void onError(ServiceError error) {
                            setScanFalse();
                            toastUtil.showToastShortTime(error.getMessage());
                        }
                    });
                } else {
                    setScanFalse();
                        /*editConsignmentNo.setError("Invalid CN");
                        utils.playErrorToneAndVibrate(getActivity());
                        editConsignmentNo.requestFocus();*/
                }
            }
        }
    }

    private void setScanFalse() {
        isScanned = false;
    }

    private void hideSoftKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
    }

    public void LoadPendingPickups(String isSaveData, int isRetake) {
        try {
            presenter.fetchLoadPendingPickups(getActivity(), isSaveData, isRetake);

        } catch (Exception e) {
            toastUtil.showToastShortTime(e.getMessage());
            presenter.fetchCustomers(getActivity());
            editAccountNumber.setEnabled(true);
        }
    }

    public void LoadPendingPickupsWithEmail(String isSaveData, int isRetake) {
        try {
            presenter.fetchLoadPendingPickupsWithEmail(isSaveData, isRetake);

        } catch (Exception e) {
            toastUtil.showToastShortTime(e.getMessage());
            presenter.fetchCustomers(getActivity());
            editAccountNumber.setEnabled(true);
        }
    }


}


