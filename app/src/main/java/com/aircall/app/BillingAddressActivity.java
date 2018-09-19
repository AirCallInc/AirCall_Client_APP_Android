package com.aircall.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SelectCityDialog;
import com.aircall.app.Dialog.SelectStateDialog;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.GetAddress.getAddressResponce;
import com.aircall.app.Model.GetAllState.GetAllStateResponce;
import com.aircall.app.Model.Payment.MyCartData;
import com.aircall.app.Model.Payment.MyCartResponce;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BillingAddressActivity extends Activity {

    @Bind(R.id.etFirstName)
    EditText etFirstName;

    @Bind(R.id.etLastName)
    EditText etLastName;

    @Bind(R.id.etAddress)
    EditText etAddress;

    @Bind(R.id.etState)
    EditText etState;

    @Bind(R.id.etCity)
    EditText etCity;

    @Bind(R.id.etPhoneNumber)
    EditText etPhoneNumber;

    @Bind(R.id.etMobileNumber)
    EditText etMobileNumber;

    @Bind(R.id.etZipcode)
    EditText etZipcode;

    @Bind(R.id.tvProceed)
    TextView tvProceed;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.etCompany)
    EditText etCompany;

    public GlobalClass globalClass;
    private ProgressDialogFragment progressDialogFragment;
    private Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private MyCartData billingAddress = new MyCartData();
    private GetAllStateResponce states;
    private String TotalAmount, CommonId = "", From = "", NId = "", PDFUrl = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_billing_address);
        ButterKnife.bind(this);
        init();
        clickEvent();
    }

    private void init() {
        ivBack.setEnabled(false);
        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        Intent intent = getIntent();
        TotalAmount = intent.getStringExtra("TotalAmount");
        if (intent.getStringExtra("From") != null) {
            From = intent.getStringExtra("From");
            CommonId = intent.getStringExtra("CommonId");
            NId = intent.getStringExtra("NId");
        }
        if (!globalClass.checkInternetConnection()) {
            ivBack.setEnabled(true);
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");

        } else {
            getMyCart();
        }
        HideKeyboard();
    }

    private void clickEvent() {
        /**
         * Select State using SelectStateDialog
         */
        etState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard();
                DialogFragment ds = new SelectStateDialog(globalClass,
                        new UsernameDialogInteface() {
                            @Override
                            public void submitClick(String stateName, String stateId) {
                                etState.setText(stateName);
                                etCity.setText("");
                                etZipcode.setText("");
                                billingAddress.StateName = stateName;
                                billingAddress.State = stateId;
                                for (int i = 0; i < states.Data.size(); i++) {
                                    if (states.Data.get(i).Id == stateId) {
                                        states.Data.get(i).IsSelected = true;
                                    } else {
                                        states.Data.get(i).IsSelected = false;
                                    }
                                }
                            }
                        }, states);
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        /**
         * Select City using SelectCityDialog
         */
        etCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard();
                DialogFragment ds = new SelectCityDialog(globalClass, billingAddress.State,
                        new UsernameDialogInteface() {
                            @Override
                            public void submitClick(String cityName, String cityId) {
                                etCity.setText(cityName);
                                etZipcode.setText("");
                                billingAddress.CityName = cityName;
                                billingAddress.City = cityId;
                            }
                        }, billingAddress.City);
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        /**
         * Move to PaymentMethodActivity for payment with billingAddress, name.
         * (Also commonId if it's for renew plan and from notification)
         */
        tvProceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard();
                if (validation()) {
                    //ValidateZipCode
                    validateZipcode();

                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    /**
     * Web API for add new address of client
     */
    public void validateZipcode() {
        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getAllAddress(sharedpreferences.getString("Id", ""), new Callback<getAddressResponce>() {

                    @Override
                    public void success(getAddressResponce GetAddressResponce, Response response) {
                        hideProgressDialog();

                        if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!GetAddressResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", GetAddressResponce.Token);
                                editor.apply();
                            }

                            billingAddress.Address = etAddress.getText().toString().trim();
                            billingAddress.ZipCode = etZipcode.getText().toString().trim();
                            billingAddress.HomeNumber = etPhoneNumber.getText().toString().trim();
                            billingAddress.MobileNumber = etMobileNumber.getText().toString().trim();
                            Gson gson = new Gson();
                            String billingAdrs = gson.toJson(billingAddress);
                            Intent intent = new Intent(BillingAddressActivity.this, PaymentMethodActivity.class);
                            intent.putExtra("billingAddress", billingAdrs);
                            intent.putExtra("FirstName", etFirstName.getText().toString().trim());
                            intent.putExtra("LastName", etLastName.getText().toString().trim());
                            //Added Company
                            intent.putExtra("Company", etCompany.getText().toString().trim());
                            intent.putExtra("TotalAmount", TotalAmount);
                            intent.putExtra("From", From);
                            intent.putExtra("CommonId", CommonId);
                            intent.putExtra("NId", NId);
                            startActivity(intent);


                        } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                            DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {
                                            globalClass.Clientlogout();
                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");
                        } else {

                            DialogFragment ds = new SingleButtonAlert(GetAddressResponce.Message,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {

                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");

                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        hideProgressDialog();
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {
                                    }
                                });
                        ds.setCancelable(true);
                        ds.show(getFragmentManager(), "");
                    }
                });
    }

    private Boolean validation() {
        Boolean isValid = true;
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
        SpannableStringBuilder ssbuilder;
        if (etFirstName.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.FirstName);
            ssbuilder.setSpan(fgcspan, 0, etFirstName.length(), 0);
            etFirstName.requestFocus();
            etFirstName.setError(ssbuilder);
        } else if (etLastName.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.LastName);
            ssbuilder.setSpan(fgcspan, 0, etLastName.length(), 0);
            etLastName.requestFocus();
            etLastName.setError(ssbuilder);
        } else if (etAddress.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.Address);
            ssbuilder.setSpan(fgcspan, 0, etAddress.length(), 0);
            etAddress.requestFocus();
            etAddress.setError(ssbuilder);
        } else if (etState.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.State);
            ssbuilder.setSpan(fgcspan, 0, etState.length(), 0);
            etState.requestFocus();
            etState.setError(ssbuilder);
        } else if (etCity.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.City);
            ssbuilder.setSpan(fgcspan, 0, etCity.length(), 0);
            etCity.requestFocus();
            etCity.setError(ssbuilder);
        } else if (etZipcode.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.Zipcode);
            ssbuilder.setSpan(fgcspan, 0, etZipcode.length(), 0);
            etZipcode.requestFocus();
            etZipcode.setError(ssbuilder);
        }
        return isValid;
    }

    /**
     * Get default address information and contact detail to show billing information
     */
    public void getMyCart() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.myCart(sharedpreferences.getString("Id", ""), new Callback<MyCartResponce>() {

            @Override
            public void success(MyCartResponce myCartResponce, Response response) {


                if (myCartResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!myCartResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", myCartResponce.Token);
                        editor.apply();
                    }
                    billingAddress = myCartResponce.Data;

                    getAddressStateList();

                } else if (myCartResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    ivBack.setEnabled(true);
                    hideProgressDialog();
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                } else {
                    ivBack.setEnabled(true);
                    hideProgressDialog();
                    DialogFragment ds = new SingleButtonAlert(myCartResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ivBack.setEnabled(true);
                hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                            }
                        });
                ds.setCancelable(true);
                ds.show(getFragmentManager(), "");

            }
        });

    }

    /**
     * Get State List
     */
    public void getAddressStateList() {

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class,
                sharedpreferences.getString("Token", ""));

        webserviceApi.getAllState(new Callback<GetAllStateResponce>() {
            @Override
            public void success(GetAllStateResponce getAllStateResponce, Response response) {
                ivBack.setEnabled(true);
                hideProgressDialog();
                if (getAllStateResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!getAllStateResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getAllStateResponce.Token);
                        editor.apply();
                    }
                    states = getAllStateResponce;
                    for (int i = 0; i < states.Data.size(); i++) {
                        if (states.Data.get(i).IsDefault) {
                            states.Data.get(i).IsSelected = true;
                            billingAddress.StateName = states.Data.get(i).Name;
                            billingAddress.State = states.Data.get(i).Id;
                            break;
                        }
                    }
                    showDataInEditText();
                } else if (getAllStateResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ivBack.setEnabled(true);
                hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(true);
                ds.show(getFragmentManager(), "");
            }
        });
    }

    /**
     * Set Data in EditText from billingAddress and sharedPreference
     */
    private void showDataInEditText() {
        etFirstName.setText(sharedpreferences.getString("FirstName", ""));
        etLastName.setText(sharedpreferences.getString("LastName", ""));
        etCompany.setText(sharedpreferences.getString("Company", ""));
        etAddress.setText(billingAddress.Address);
        etState.setText(billingAddress.StateName);
        etCity.setText(billingAddress.CityName);
        etZipcode.setText(billingAddress.ZipCode);
        etPhoneNumber.setText(billingAddress.HomeNumber);
        etMobileNumber.setText(billingAddress.MobileNumber);
    }

    public void HideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showProgressDailog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        progressDialogFragment.show(getFragmentManager(), "");
        progressDialogFragment.setCancelable(false);
    }

    public void hideProgressDialog() {
        try {
            if (progressDialogFragment != null) {
                progressDialogFragment.dismiss();
            }
        } catch (Exception e) {
        }
    }
}
