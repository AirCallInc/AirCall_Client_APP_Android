package com.aircall.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

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

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddAddressActivity extends Activity {

    public GlobalClass globalClass;
    private ProgressDialogFragment progressDialogFragment;
    public SharedPreferences sharedpreferences;
    public SharedPreferences.Editor editor;
    private String strStateIdForCity;
    private String strStateIdForState;

    @Bind(R.id.etAddAddress)
    EditText etAddAddress;

    @Bind(R.id.etAddAddressState)
    EditText etAddAddressState;

    @Bind(R.id.etAddAddressCity)
    EditText etAddAddressCity;

    @Bind(R.id.etAddAddressZipcode)
    EditText etAddAddressZipcode;

    @Bind(R.id.tvSubmitAddAddress)
    TextView tvSubmitAddAddress;

    @Bind(R.id.tvHeaderName)
    TextView tvHeaderName;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.llSwitch)
    LinearLayout llSwitch;

    @Bind(R.id.llAddAddressCity)
    LinearLayout llAddAddressCity;

    @Bind(R.id.toggleDefault)
    ToggleButton toggleDefault;

    String changeHeaderEdit = "Edit Address";
    String changeHeaderAdd = "Add Address";
    String strEdit;
    String strAdd;
    String Id;
    Boolean IsDefaultAddress, AllowDelete, IsShouldOpenDialog = true;
    String strClientId;
    GetAllStateResponce states;
    Intent intent;
    SpannableStringBuilder ssbuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.bind(this);

        init();
        clickEvents();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addAddressList();
    }*/

    private void init() {

        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        intent = getIntent();

        String editAddress = intent.getStringExtra("EditAddress");
        strEdit = editAddress;
        Id = intent.getStringExtra("id");
        strClientId = intent.getStringExtra("clientId");
        String strAddress = intent.getStringExtra("address");
        strStateIdForCity = intent.getStringExtra("city");
        strStateIdForState = intent.getStringExtra("state");
        String strZipcode = intent.getStringExtra("zipcode");
        IsDefaultAddress = getIntent().getExtras().getBoolean("isDefaultAddress");

        /**
         * Check if activity start for add address or edit address, and set all the field as per need
         */
        if (changeHeaderEdit.equals(intent.getStringExtra("EditAddress"))) {
            tvHeaderName.setText("Edit Address");
            if (intent.getIntExtra("AddressCount", 1) == 1) {
                llSwitch.setVisibility(View.GONE);
            } else {
                llSwitch.setVisibility(View.VISIBLE);
            }
            AllowDelete = intent.getBooleanExtra("AllowDelete", false);
            etAddAddress.setText(strAddress);
            etAddAddressState.setText(intent.getStringExtra("stateName"));
            etAddAddressCity.setText(intent.getStringExtra("cityName"));
            etAddAddressZipcode.setText(strZipcode);

            if (AllowDelete) {
                llAddAddressCity.setEnabled(true);
                etAddAddressCity.setEnabled(true);
                etAddAddressState.setEnabled(true);
                etAddAddressZipcode.setFocusable(true);
            } else {
                llAddAddressCity.setEnabled(false);
                etAddAddressCity.setEnabled(false);
                etAddAddressState.setEnabled(false);
                etAddAddressZipcode.setFocusable(false);
            }

            tvSubmitAddAddress.setText("Edit");
            if (intent.getBooleanExtra("isDefaultAddress", false)) {
                toggleDefault.setChecked(true);
                //toggleDefault.setEnabled(false);
            } else {
                toggleDefault.setChecked(false);
            }
        } else {
            tvHeaderName.setText("Add Address");
            tvSubmitAddAddress.setText("Add");
            llSwitch.setVisibility(View.GONE);
            llAddAddressCity.setEnabled(true);
            etAddAddressCity.setEnabled(true);
            etAddAddressState.setEnabled(true);
            etAddAddressZipcode.setFocusable(true);
        }

        /**
         * Get state
         */
        if (globalClass.checkInternetConnection()) {
            getAddressStateList();
        } else {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        }
    }


    public void clickEvents() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(AddAddressActivity.this, AddressListActivity.class);
//                startActivityForResult(intent, 3);
                finish();
            }
        });

        /**
         * Open SelectStateDialog along with states which we have already fetched
         */
        etAddAddressState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etAddAddressState.setError(null);
                DialogFragment ds = new SelectStateDialog(globalClass,
                        new UsernameDialogInteface() {
                            @Override
                            public void submitClick(String stateName, String stateId) {
                                etAddAddressState.setText(stateName);
                                etAddAddressCity.setText("");
                                etAddAddressZipcode.setText("");
                                strStateIdForState = stateId;
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
         * Open SelectCityDialog along with selected statesId
         */

        etAddAddressCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etAddAddressCity.setError(null);
                if (strStateIdForState != null) {
                    DialogFragment ds = new SelectCityDialog(globalClass, strStateIdForState,
                            new UsernameDialogInteface() {
                                @Override
                                public void submitClick(String cityName, String cityId) {
                                    if (cityName.equalsIgnoreCase("") || cityId.equalsIgnoreCase("")) {
                                        etAddAddressZipcode.requestFocus();
                                    } else {
                                        etAddAddressCity.setText(cityName);
                                        etAddAddressZipcode.setText("");
                                        strStateIdForCity = cityId;
                                    }
                                }
                            }, strStateIdForCity);
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.SelectState,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                }

            }
        });

        etAddAddressCity.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus && IsShouldOpenDialog) {
                    etAddAddressCity.setError(null);
                    HideKeyboard();
                    if (strStateIdForState != null) {

                        DialogFragment ds = new SelectCityDialog(globalClass, strStateIdForState,
                                new UsernameDialogInteface() {
                                    @Override
                                    public void submitClick(String cityName, String cityId) {
                                        if (cityName.equalsIgnoreCase("") || cityId.equalsIgnoreCase("")) {
                                            etAddAddressZipcode.requestFocus();
                                        } else {
                                            etAddAddressCity.setText(cityName);
                                            etAddAddressZipcode.setText("");
                                            strStateIdForCity = cityId;
                                            //HideKeyboard();
                                            etAddAddressZipcode.requestFocus();
                                        }

                                    }
                                }, strStateIdForCity);
                        ds.setCancelable(false);
                        ds.show(getFragmentManager(), "");
                    } else {
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.SelectState,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {

                                    }
                                });
                        ds.setCancelable(true);
                        ds.show(getFragmentManager(), "");
                    }
                }
                IsShouldOpenDialog = true;
            }
        });

        /**
         * Call WebAPI of Update or Add on submit button
         */
        tvSubmitAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etAddAddressCity.setError(null);
                etAddAddressState.setError(null);
                etAddAddressZipcode.setError(null);
                etAddAddress.setError(null);
                if (validation()) {
                    if (changeHeaderEdit.equals(strEdit)) {
                        UpdateAddress();
                    } else {
                        addClientAddress();
                    }
                }
            }
        });

        /**
         * Set address will default or not, if client editing address and address is already
         * default then toggleDefault should not change.
         */
        toggleDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked && IsDefaultAddress) {
                    toggleDefault.setChecked(true);
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.DeletDefaultAddressValidation,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                }
            }
        });
    }

    /**
     * Web API for add new address of client
     */
    public void addClientAddress() {
        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.addClientAddress(sharedpreferences.getString("Id", ""), etAddAddress.getText().toString(),
                strStateIdForState.toString(),strStateIdForCity.toString(),
                etAddAddressState.getText().toString(), etAddAddressCity.getText().toString(), etAddAddressZipcode.getText().toString(),
                new Callback<getAddressResponce>() {

                    @Override
                    public void success(getAddressResponce GetAddressResponce, Response response) {
                        hideProgressDialog();

                        if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!GetAddressResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", GetAddressResponce.Token);
                                editor.apply();
                            }

                            finish();

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

    /**
     * Web API for update address of client
     */
    public void UpdateAddress() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.updateClientAddress(Id.toString(), sharedpreferences.getString("Id", ""), etAddAddress.getText().toString(),
                Integer.parseInt(strStateIdForState), Integer.parseInt(strStateIdForCity), Integer.parseInt(etAddAddressZipcode.getText().toString()),
                toggleDefault.isChecked(), new Callback<getAddressResponce>() {

                    @Override
                    public void success(getAddressResponce GetAddressResponce, Response response) {
                        hideProgressDialog();

                        if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!GetAddressResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", GetAddressResponce.Token);
                                editor.apply();
                            }
                            finish();

                        } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                            DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {
                                            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                            notificationManager.cancelAll();
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

    /**
     * Web API for state list
     */
    public void getAddressStateList() {
        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class,
                sharedpreferences.getString("Token", ""));

        webserviceApi.getAllState(new Callback<GetAllStateResponce>() {
            @Override
            public void success(GetAllStateResponce getAllStateResponce, Response response) {

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
                            etAddAddressState.setText(states.Data.get(i).Name);
                            strStateIdForState = states.Data.get(i).Id;
                            break;
                        }
                    }

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

    public Boolean validation() {

        Boolean isValidate = true;
        String estring = "";
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);

        if (etAddAddress.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.Address);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etAddAddress.requestFocus();
            etAddAddress.setError(ssbuilder);
            isValidate = false;

        } else if (etAddAddressState.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.State);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etAddAddressState.requestFocus();
            etAddAddressState.setError(ssbuilder);
            isValidate = false;

        } else if (etAddAddressCity.getText().toString().trim().equals("")) {

            IsShouldOpenDialog = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.City);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etAddAddressCity.requestFocus();
            etAddAddressCity.setError(ssbuilder);
            isValidate = false;

        } else if (etAddAddressZipcode.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.Zipcode);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etAddAddressZipcode.requestFocus();
            etAddAddressZipcode.setError(ssbuilder);
            isValidate = false;

        } else if (!globalClass.checkInternetConnection()) {
            isValidate = false;
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        }
        return isValidate;
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

    private void HideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
