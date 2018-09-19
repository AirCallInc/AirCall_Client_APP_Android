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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SelectMonthDialog;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddNewCard.AddNewCardResponce;
import com.aircall.app.Model.AddNewCard.GetCardByIdResponce;
import com.aircall.app.Model.AddNewCard.NewCardData;
import com.google.gson.Gson;

import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddNewCardActivity extends Activity {

    public GlobalClass globalClass;
    Context context;
    private ProgressDialogFragment progressDialogFragment;
    private int MY_SCAN_REQUEST_CODE = 100; // arbitrary int
    String resultStrCardNo, resultStrMonth = null, resultStrYear = null, resultStrCvv = null, resultStrCardName = null, resultStrCardType = "Visa";

    public SharedPreferences sharedpreferences;
    public SharedPreferences.Editor editor;

    @Bind(R.id.etNameOnCard)
    EditText etNameOnCard;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.etCardNumber)
    EditText etCardNumber;

    @Bind(R.id.etCvv)
    EditText etCvv;

    @Bind(R.id.spinnerPaymentDayOfMonth)
    EditText spinnerPaymentDayOfMonth;

    @Bind(R.id.spinnerPaymentyear)
    EditText spinnerPaymentyear;

    @Bind(R.id.ivCardVisa)
    ImageView ivCardVisa;

    @Bind(R.id.ivMasterCard)
    ImageView ivMasterCard;

    @Bind(R.id.ivCardDiscover)
    ImageView ivCardDiscover;

    @Bind(R.id.ivCard4)
    ImageView ivCard4;

    @Bind(R.id.tvPaymentMethdAdd)
    TextView SubmitButton;

    @Bind(R.id.tvHeaderName)
    TextView tvHeaderName;

    @Bind(R.id.tvScan)
    TextView tvScan;

    Boolean IsDefaultCard = false;
    int month, year;
    NewCardData creditCardDetail;
    String CommonId, NId;
    boolean isFromNotification = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_card);
        ButterKnife.bind(this);

        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        init();
        clickEvents();
    }

    public void init() {

        //IsDefaultCard = getIntent().getExtras().getBoolean("IsDefaultCard");
        if (getIntent().getStringExtra("creditCardDetail") != null) {
            /**
             * Activity start for Edit card, so get data from intent and set in screen
             */
            tvScan.setVisibility(View.GONE);
            Gson gson = new Gson();
            String json = getIntent().getStringExtra("creditCardDetail");
            creditCardDetail = gson.fromJson(json, NewCardData.class);
            setScreenData();
            etCardNumber.setFocusable(false);
            ivCardVisa.setEnabled(false);
            ivMasterCard.setEnabled(false);
            ivCardDiscover.setEnabled(false);
            ivCard4.setEnabled(false);

        } else if (getIntent().getStringExtra("CommonId") != null) {
            /**
             * From notification of expired card, get detail of expired card from web API
             */
            isFromNotification = true;
            CommonId = getIntent().getExtras().getString("CommonId");
            NId = getIntent().getExtras().getString("NId");
            GetExpiredCreditCardById();
            etCardNumber.setFocusable(false);
            ivCardVisa.setEnabled(false);
            ivMasterCard.setEnabled(false);
            ivCardDiscover.setEnabled(false);
            ivCard4.setEnabled(false);
            tvScan.setVisibility(View.GONE);
        } else {
            /**
             * For Add new Card
             */
            onScanPress();
            tvScan.setVisibility(View.VISIBLE);
        }
    }

    public void clickEvents() {

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        SubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        tvScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanPress();
            }
        });

        ivCardVisa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultStrCardType = "Visa";
                ivCardVisa.setImageResource(R.drawable.radiobutton_selected);
                ivMasterCard.setImageResource(R.drawable.radiobutton);
                ivCardDiscover.setImageResource(R.drawable.radiobutton);
                ivCard4.setImageResource(R.drawable.radiobutton);
            }
        });

        ivMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultStrCardType = "MasterCard";
                ivMasterCard.setImageResource(R.drawable.radiobutton_selected);
                ivCardVisa.setImageResource(R.drawable.radiobutton);
                ivCardDiscover.setImageResource(R.drawable.radiobutton);
                ivCard4.setImageResource(R.drawable.radiobutton);
            }
        });

        ivCardDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultStrCardType = "Discover";
                ivCardDiscover.setImageResource(R.drawable.radiobutton_selected);
                ivCardVisa.setImageResource(R.drawable.radiobutton);
                ivMasterCard.setImageResource(R.drawable.radiobutton);
                ivCard4.setImageResource(R.drawable.radiobutton);
            }
        });

        ivCard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                resultStrCardType = "AMEX";
                ivCard4.setImageResource(R.drawable.radiobutton_selected);
                ivCardVisa.setImageResource(R.drawable.radiobutton);
                ivMasterCard.setImageResource(R.drawable.radiobutton);
                ivCardDiscover.setImageResource(R.drawable.radiobutton);
            }
        });

        spinnerPaymentDayOfMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment ds = new SelectMonthDialog(globalClass,
                        new UsernameDialogInteface() {
                            @Override
                            public void submitClick(String tag, String mnth) {
                                spinnerPaymentDayOfMonth.setText(mnth);
                                month = Integer.parseInt(mnth);
                            }
                        }, true);
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        spinnerPaymentyear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment ds = new SelectMonthDialog(globalClass,
                        new UsernameDialogInteface() {
                            @Override
                            public void submitClick(String tag, String yr) {
                                spinnerPaymentyear.setText(yr);
                                year = Integer.parseInt(yr);
                            }
                        }, false);
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });
    }

    /**
     *
     */
    public void onScanPress() {

        /**
         * Start CardIOActivity for scan card
         */
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        // customize these values to suit your needs.
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false); // default: false
        scanIntent.putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true);
        startActivityForResult(scanIntent, MY_SCAN_REQUEST_CODE);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * If user already in this activity and click on notification then it will call this method only.
         */
        if (getIntent().getExtras().getString("CommonId") != null) {
            isFromNotification = true;
            CommonId = getIntent().getExtras().getString("CommonId");
            NId = getIntent().getExtras().getString("NId");
            GetExpiredCreditCardById();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /**
         * Get scan data of card
         */
        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {

            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);
            resultStrCardNo = scanResult.getFormattedCardNumber().replaceAll(" ", "");
            resultStrMonth = String.valueOf(scanResult.expiryMonth);
            resultStrYear = String.valueOf(scanResult.expiryYear);
            resultStrCvv = String.valueOf(scanResult.cvv);
            resultStrCardName = String.valueOf(scanResult.cardholderName);
            resultStrCardType = String.valueOf(scanResult.getCardType());
            setValueCardDetails();
            HideKeyboard();

        }/* else {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.ScanCancel,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(true);
            ds.show(getFragmentManager(), "");
        }*/
    }

    /**
     * Set data in screen elements
     */
    private void setScreenData() {
        IsDefaultCard = creditCardDetail.IsDefaultPayment;
        tvHeaderName.setText("Update Card");
        etNameOnCard.setText(creditCardDetail.NameOnCard);
        etCardNumber.setText(creditCardDetail.CardNumber);
        spinnerPaymentDayOfMonth.setText(creditCardDetail.ExpiryMonth);
        month = Integer.parseInt(creditCardDetail.ExpiryMonth);
        spinnerPaymentyear.setText(creditCardDetail.ExpiryYear);
        year = Integer.parseInt(creditCardDetail.ExpiryYear);
        if (creditCardDetail.CardType == null) {
            resultStrCardType = "Visa";
            ivCardVisa.setImageResource(R.drawable.radiobutton_selected);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);

        } else if (creditCardDetail.CardType.toLowerCase().contains("visa")) {
            resultStrCardType = "Visa";
            ivCardVisa.setImageResource(R.drawable.radiobutton_selected);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);

        } else if (creditCardDetail.CardType.toLowerCase().contains("master")) {
            resultStrCardType = "MasterCard";
            ivMasterCard.setImageResource(R.drawable.radiobutton_selected);
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);

        } else if (creditCardDetail.CardType.toLowerCase().contains("discover")) {
            resultStrCardType = "Discover";
            ivCardDiscover.setImageResource(R.drawable.radiobutton_selected);
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);

        } else {
            resultStrCardType = "AMEX";
            ivCard4.setImageResource(R.drawable.radiobutton_selected);
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
        }
    }

    /**
     * Set images for selection of cards
     */
    public void setValueCardDetails() {
        etCardNumber.setText(resultStrCardNo);
        etCvv.setText(resultStrCvv);
        spinnerPaymentDayOfMonth.setText(resultStrMonth);
        spinnerPaymentyear.setText(resultStrYear);
        etCvv.setText(resultStrCvv);
        etNameOnCard.setText(resultStrCardName);
        year = Integer.parseInt(resultStrYear);
        month = Integer.parseInt(resultStrMonth);

        if (resultStrCardType.toLowerCase().contains("visa")) {
            resultStrCardType = "Visa";
            ivCardVisa.setImageResource(R.drawable.radiobutton_selected);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);

        } else if (resultStrCardType.toLowerCase().contains("master")) {
            resultStrCardType = "MasterCard";
            ivMasterCard.setImageResource(R.drawable.radiobutton_selected);
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);

        } else if (resultStrCardType.toLowerCase().contains("discover")) {
            resultStrCardType = "Discover";
            ivCardDiscover.setImageResource(R.drawable.radiobutton_selected);
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);

        } else if (resultStrCardType.toLowerCase().contains("amex")) {
            resultStrCardType = "AMEX";
            ivCard4.setImageResource(R.drawable.radiobutton_selected);
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
        } else {
            Toast toast = Toast.makeText(AddNewCardActivity.this, "Please select card type.", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 0);
            toast.show();
        }

    }

    /**
     * Get Expire card's detail
     */
    public void GetExpiredCreditCardById() {

        Log.e("Token", sharedpreferences.getString("Token", ""));

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getExpiredCreditCardById(sharedpreferences.getString("Id", ""), CommonId, NId, new Callback<GetCardByIdResponce>() {

            @Override
            public void success(GetCardByIdResponce getCardByIdResponce, Response response) {

                hideProgressDialog();

                if (getCardByIdResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!getCardByIdResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getCardByIdResponce.Token);
                        editor.apply();
                    }
                    creditCardDetail = getCardByIdResponce.Data;
                    setScreenData();

                } else if (getCardByIdResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(getCardByIdResponce.Message,
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
     * Web API for Update card
     */
    public void UpdateDefaultCreditCard() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.updateCreditCardDetail(creditCardDetail.Id, sharedpreferences.getString("Id", ""),
                resultStrCardType, etNameOnCard.getText().toString(), spinnerPaymentDayOfMonth.getText().toString(),
                etCardNumber.getText().toString().trim(), etCvv.getText().toString().trim(), spinnerPaymentyear.getText().toString(),
                creditCardDetail.IsDefaultPayment, new Callback<AddNewCardResponce>() {

                    @Override
                    public void success(AddNewCardResponce addNewCardResponce, Response response) {
                        hideProgressDialog();

                        if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!addNewCardResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", addNewCardResponce.Token);
                                editor.apply();
                            }
                            if (isFromNotification) {
                                Intent intent = new Intent(AddNewCardActivity.this, DashboardActivity.class);
                                startActivity(intent);
                            } else {
                                finish();
                            }

                        } else if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(addNewCardResponce.Message,
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
     * Web API for add new card
     */
    public void addNewCardDetails() {

        Log.e("Token", sharedpreferences.getString("Token", ""));

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.addNewCard(sharedpreferences.getString("Id", ""), resultStrCardType,
                etNameOnCard.getText().toString(), etCardNumber.getText().toString(), spinnerPaymentDayOfMonth.getText().toString(),
                spinnerPaymentyear.getText().toString(), etCvv.getText().toString().trim(), false, new Callback<AddNewCardResponce>() {

                    @Override
                    public void success(AddNewCardResponce addNewCardResponce, Response response) {

                        hideProgressDialog();

                        if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!addNewCardResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", addNewCardResponce.Token);
                                editor.apply();
                            }
                            finish();

                        } else if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(addNewCardResponce.Message,
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

    public void validation() {
        String estring = "";
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
        SpannableStringBuilder ssbuilder;

        if (etNameOnCard.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.NameOnCard);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.NameOnCard.length(), 0);
            etNameOnCard.requestFocus();
            etNameOnCard.setError(ssbuilder);

        } else if (etCardNumber.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.CardNumber);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.CardNumber.length(), 0);
            etCardNumber.requestFocus();
            etCardNumber.setError(ssbuilder);

        } else if (spinnerPaymentDayOfMonth.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.ExMonth);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.ExMonth.length(), 0);
            spinnerPaymentDayOfMonth.requestFocus();
            spinnerPaymentDayOfMonth.setError(ssbuilder);

        } else if (spinnerPaymentyear.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.ExYear);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.ExYear.length(), 0);
            spinnerPaymentyear.requestFocus();
            spinnerPaymentyear.setError(ssbuilder);

        } else if (etCardNumber.getText().toString().trim().length() < 13 || etCardNumber.getText().toString().trim().length() > 16) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.validCardNumber);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.validCardNumber.length(), 0);
            etCardNumber.requestFocus();
            etCardNumber.setError(ssbuilder);
        } else if (!checkExpireCard()) {
            ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidExpire);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.ValidExpire.length(), 0);
            spinnerPaymentDayOfMonth.setError(ssbuilder);
            spinnerPaymentyear.setError(ssbuilder);

        } else if (etCvv.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.CVV);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.CVV.length(), 0);
            etCvv.requestFocus();
            etCvv.setError(ssbuilder);

        } else if (etCvv.getText().toString().trim().length() < 3) {
            ssbuilder = new SpannableStringBuilder(ErrorMessages.validCVV);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.validCVV.length(), 0);
            etCvv.requestFocus();
            etCvv.setError(ssbuilder);
        } else if (!globalClass.checkInternetConnection()) {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        } else {
            if (creditCardDetail == null) {
                addNewCardDetails();
            } else {
                UpdateDefaultCreditCard();
            }
        }
    }

    private Boolean checkExpireCard() {
        int yr = Calendar.getInstance().get(Calendar.YEAR);
        int mnth = Calendar.getInstance().get(Calendar.MONTH);
        if (year > yr) {
            return true;
        } else if (month >= mnth + 1) {
            return true;
        } else {
            return false;
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

    public void HideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
