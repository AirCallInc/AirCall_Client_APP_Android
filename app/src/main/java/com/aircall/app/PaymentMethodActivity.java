package com.aircall.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.PaymentCardListDialog;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SelectMonthDialog;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.PaymentCardInterface;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddNewCard.AddNewCardResponce;
import com.aircall.app.Model.AddNewCard.NewCardData;
import com.aircall.app.Model.Mail.SendSalesAgreementResponse;
import com.aircall.app.Model.NoShowNotification.NoShowPaymentResponce;
import com.aircall.app.Model.Payment.MyCartData;
import com.aircall.app.Model.Payment.ValidateCreditCardResponce;
import com.aircall.app.Model.Receipt.Receipt;
import com.aircall.app.Model.Receipt.ReceiptUnitDetail;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import io.card.payment.CardIOActivity;
import io.card.payment.CreditCard;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PaymentMethodActivity extends Activity {

    @Bind(R.id.tvTotalAmount)
    TextView tvTotalAmount;

    @Bind(R.id.tvPaymentMethdAdd)
    TextView tvPaymentMethdAdd;

    @Bind(R.id.tvTerms)
    TextView tvTerms;

    @Bind(R.id.tvScan)
    TextView tvScan;

    @Bind(R.id.etNameOnCard)
    EditText etNameOnCard;

    @Bind(R.id.etCardNumber)
    EditText etCardNumber;

    @Bind(R.id.etMonth)
    EditText etMonth;

    @Bind(R.id.etYear)
    EditText etYear;

    @Bind(R.id.etCvv)
    EditText etCvv;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.ivCardVisa)
    ImageView ivCardVisa;

    @Bind(R.id.ivMasterCard)
    ImageView ivMasterCard;

    @Bind(R.id.ivCardDiscover)
    ImageView ivCardDiscover;

    @Bind(R.id.ivCard4)
    ImageView ivCard4;

    @Bind(R.id.ivAdd)
    ImageView ivAdd;

    @Bind(R.id.cbAgreeTerm)
    CheckBox cbAgreeTerm;

    //Added
    @Bind(R.id.img_mail)
    ImageView img_mail;

    Context context;
    public GlobalClass globalClass;
    private String CardType = "Visa";
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private ProgressDialogFragment progressDialogFragment;
    AddNewCardResponce addNewCardResponceData;
    Receipt receipt = new Receipt();
    int month, year;
    private MyCartData billingAddress;
    private String TotalAmount, billingAdrs, From, CommonId, NId, FirstName, LastName, Company;//,resultStrCardNo,resultStrMonth,resultStrYear,resultStrCvv,resultStrCardName;

    /*Address Listing*/
    private ArrayList<NewCardData> creditCardDetail = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_method);
        ButterKnife.bind(this);
        init();
        clickEvent();
    }

    private void init() {
        Intent intent = getIntent();
        context = this;
        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        //Added FirstName, LastName, Company
        FirstName = intent.getStringExtra("FirstName");
        LastName = intent.getStringExtra("LastName");
        Company = intent.getStringExtra("Company");

        TotalAmount = intent.getStringExtra("TotalAmount");
        From = intent.getStringExtra("From");
        CommonId = intent.getStringExtra("CommonId");
        NId = intent.getStringExtra("NId");
        /*FirstName = intent.getStringExtra("FirstName");
        LastName = intent.getStringExtra("LastName");*/
        billingAdrs = intent.getStringExtra("billingAddress");
        Gson gson = new Gson();
        billingAddress = gson.fromJson(billingAdrs, MyCartData.class);

        if (billingAddress.PDFUrl.trim().equalsIgnoreCase("")) {
            img_mail.setVisibility(View.GONE);
            tvTerms.setClickable(false);
            tvTerms.setEnabled(false);
        }
        tvTotalAmount.setText("$" + TotalAmount);

        SpannableString content = new SpannableString(getResources().getString(R.string.payment_method_term));
        content.setSpan(new UnderlineSpan(), 0, getResources().getString(R.string.payment_method_term).length(), 0);
        tvTerms.setText(content);

        /**
         * Get card list and feel default card's information in screen
         */
        if (globalClass.checkInternetConnection()) {
            getCardList();
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
        HideKeyboard();
    }

    private void sendSalesAgreementMail() {
        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.sendSalesAgreement(sharedpreferences.getString("Id", ""), new Callback<SendSalesAgreementResponse>() {
            @Override
            public void success(SendSalesAgreementResponse sendSalesAgreementResponse, Response response) {
                hideProgressDialog();
                if (sendSalesAgreementResponse.StatusCode.trim().equalsIgnoreCase(globalClass.strSucessCode)) {
                    //do Stuff
                    DialogFragment ds = new SingleButtonAlert(sendSalesAgreementResponse.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                } else if (sendSalesAgreementResponse.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(sendSalesAgreementResponse.Message,
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
            }
        });
    }

    private void clickEvent() {
        //Added
        img_mail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!globalClass.checkInternetConnection()) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else {
                    sendSalesAgreementMail();
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
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
                CardType = "Visa";
                ivCardVisa.setImageResource(R.drawable.radiobutton_selected);
                ivMasterCard.setImageResource(R.drawable.radiobutton);
                ivCardDiscover.setImageResource(R.drawable.radiobutton);
                ivCard4.setImageResource(R.drawable.radiobutton);
            }
        });

        ivMasterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardType = "MasterCard";
                ivCardVisa.setImageResource(R.drawable.radiobutton);
                ivMasterCard.setImageResource(R.drawable.radiobutton_selected);
                ivCardDiscover.setImageResource(R.drawable.radiobutton);
                ivCard4.setImageResource(R.drawable.radiobutton);
            }
        });

        ivCardDiscover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardType = "Discover";
                ivCardVisa.setImageResource(R.drawable.radiobutton);
                ivMasterCard.setImageResource(R.drawable.radiobutton);
                ivCardDiscover.setImageResource(R.drawable.radiobutton_selected);
                ivCard4.setImageResource(R.drawable.radiobutton);
            }
        });

        ivCard4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CardType = "AMEX";
                ivCardVisa.setImageResource(R.drawable.radiobutton);
                ivMasterCard.setImageResource(R.drawable.radiobutton);
                ivCardDiscover.setImageResource(R.drawable.radiobutton);
                ivCard4.setImageResource(R.drawable.radiobutton_selected);
            }
        });

        etMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment ds = new SelectMonthDialog(globalClass,
                        new UsernameDialogInteface() {
                            @Override
                            public void submitClick(String tag, String mnth) {
                                etMonth.setText(mnth);
                                month = Integer.parseInt(mnth);
                            }
                        }, true);
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        etYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment ds = new SelectMonthDialog(globalClass,
                        new UsernameDialogInteface() {
                            @Override
                            public void submitClick(String tag, String yr) {
                                etYear.setText(yr);
                                year = Integer.parseInt(yr);
                            }
                        }, false);
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        tvPaymentMethdAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!globalClass.checkInternetConnection()) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else if (isValidet()) {
                    if (From.toString().equalsIgnoreCase("NoShow")) {
                        NoShowPayment();
                    } else if (From.toString().equalsIgnoreCase("ReNewPlan")) {
                        RenewPayment();
                    } else {
                        tvPaymentMethdAdd.setEnabled(false);
                        validateCreditCard();
                    }
                }
            }
        });

        /**
         * Open full screen dialog for selection of card from listing with PaymentCardListDialog.
         */
        ivAdd.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                DialogFragment ds = new PaymentCardListDialog(globalClass, addNewCardResponceData, new PaymentCardInterface() {

                    @Override
                    public void cardDetail(int position, String cardHolderName, String cardNumber, String cardType, String exMonth, String exYear, boolean isSelct) {
                        if (addNewCardResponceData != null && addNewCardResponceData.Data != null) {
                            for (int i = 0; i < addNewCardResponceData.Data.size(); i++) {
                                if (i == position) {
                                    addNewCardResponceData.Data.get(i).IsSelected = true;
                                } else {
                                    addNewCardResponceData.Data.get(i).IsSelected = false;
                                }
                            }
                        }
                        etNameOnCard.setText(cardHolderName);
                        etCardNumber.setText(cardNumber);
                        CardType = cardType;
                        etMonth.setText(exMonth);
                        etYear.setText(exYear);
                        year = Integer.parseInt(exYear);
                        month = Integer.parseInt(exMonth);
                        cardTypeSelection(cardType.toLowerCase());

                        setClickableButtons(false);
                    }
                });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PaymentMethodActivity.this, DashboardActivity.class);
                intent.putExtra("dash", "terms");
                intent.putExtra("PdfUrl", billingAddress.PDFUrl);
                intent.putExtra("PageTitle","Sales Agreement");
//                intent.putExtra("pageid","4");
                startActivity(intent);
            }
        });
    }

    private Boolean isValidet() {
        Boolean isValid = true;
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
        SpannableStringBuilder ssbuilder;
        if (etNameOnCard.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.NameOnCard);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.NameOnCard.length(), 0);
            etNameOnCard.requestFocus();
            etNameOnCard.setError(ssbuilder);
        } else if (etCardNumber.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.CardNumber);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.CardNumber.length(), 0);
            etCardNumber.requestFocus();
            etCardNumber.setError(ssbuilder);
        } else if (etMonth.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.ExMonth);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.ExMonth.length(), 0);
            etMonth.requestFocus();
            etMonth.setError(ssbuilder);
        } else if (etYear.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.ExYear);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.ExYear.length(), 0);
            etYear.requestFocus();
            etYear.setError(ssbuilder);
        } else if (etCardNumber.getText().toString().trim().length() < 13 ||
                etCardNumber.getText().toString().trim().length() > 16) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.validCardNumber);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.validCardNumber.length(), 0);
            etCardNumber.requestFocus();
            etCardNumber.setError(ssbuilder);
        } else if (!checkExpireCard()) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidExpire);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.ValidExpire.length(), 0);
            etMonth.requestFocus();
            etYear.setError(ssbuilder);
            etMonth.setError(ssbuilder);
        } else if (etCvv.getText().toString().trim().equalsIgnoreCase("")) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.CVV);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.CVV.length(), 0);
            etCvv.requestFocus();
            etCvv.setError(ssbuilder);
        } else if (etCvv.getText().toString().trim().length() < 3) {
            isValid = false;
            ssbuilder = new SpannableStringBuilder(ErrorMessages.validCVV);
            ssbuilder.setSpan(fgcspan, 0, ErrorMessages.validCVV.length(), 0);
            etCvv.requestFocus();
            etCvv.setError(ssbuilder);
        } else if (!cbAgreeTerm.isChecked()) {
            isValid = false;
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.Agree,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        }

        return isValid;
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

    /**
     * Start CardIOActivity for scan card
     */
    private void onScanPress() {
        Intent scanIntent = new Intent(this, CardIOActivity.class);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_EXPIRY, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CVV, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_RETURN_CARD_IMAGE, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_POSTAL_CODE, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_RESTRICT_POSTAL_CODE_TO_NUMERIC_ONLY, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_REQUIRE_CARDHOLDER_NAME, true);
        scanIntent.putExtra(CardIOActivity.EXTRA_HIDE_CARDIO_LOGO, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_SUPPRESS_MANUAL_ENTRY, false);
        scanIntent.putExtra(CardIOActivity.EXTRA_KEEP_APPLICATION_THEME, false);
        startActivityForResult(scanIntent, 1);
    }

    /**
     * Set credit card's information editable or not
     *
     * @param isClickableButtons if True : Set Editable
     */
    private void setClickableButtons(Boolean isClickableButtons) {
        ivCardVisa.setEnabled(isClickableButtons);
        ivCard4.setEnabled(isClickableButtons);
        ivCardDiscover.setEnabled(isClickableButtons);
        ivMasterCard.setEnabled(isClickableButtons);
        etMonth.setEnabled(isClickableButtons);
        etYear.setEnabled(isClickableButtons);
        etCardNumber.setEnabled(isClickableButtons);
    }

    /**
     * Return data of scanned card
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && data.hasExtra(CardIOActivity.EXTRA_SCAN_RESULT)) {

            CreditCard scanResult = data.getParcelableExtra(CardIOActivity.EXTRA_SCAN_RESULT);

            etCardNumber.setText(scanResult.getFormattedCardNumber().replaceAll(" ", ""));
            etNameOnCard.setText(scanResult.cardholderName);
            etCvv.setText(String.valueOf(scanResult.cvv));
            etMonth.setText(String.valueOf(scanResult.expiryMonth));
            etYear.setText(String.valueOf(scanResult.expiryYear));
            year = scanResult.expiryYear;
            month = scanResult.expiryMonth;
            cardTypeSelection(String.valueOf(scanResult.getCardType()).toLowerCase());

        }/* else {
            Toast.makeText(context, ErrorMessages.ScanCancel, Toast.LENGTH_LONG).show();
        }*/
    }

    private void cardTypeSelection(String cardType) {
        if (cardType.toLowerCase().contains("visa")) {
            CardType = "Visa";
            ivCardVisa.setImageResource(R.drawable.radiobutton_selected);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);
        } else if (cardType.toLowerCase().contains("master")) {
            CardType = "MasterCard";
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivMasterCard.setImageResource(R.drawable.radiobutton_selected);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton);
        } else if (cardType.toLowerCase().contains("discover")) {
            CardType = "Discover";
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton_selected);
            ivCard4.setImageResource(R.drawable.radiobutton);
        } else {
            CardType = "AMEX";
            ivCardVisa.setImageResource(R.drawable.radiobutton);
            ivMasterCard.setImageResource(R.drawable.radiobutton);
            ivCardDiscover.setImageResource(R.drawable.radiobutton);
            ivCard4.setImageResource(R.drawable.radiobutton_selected);
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

    /**
     * Get card list and fill data of default or first card in screen.
     */
    public void getCardList() {


        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getCreditCardList(sharedpreferences.getString("Id", ""), new Callback<AddNewCardResponce>() {

            @Override
            public void success(AddNewCardResponce addNewCardResponce, retrofit.client.Response response) {

                hideProgressDialog();

                if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addNewCardResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addNewCardResponce.Token);
                        editor.apply();
                    }

                    creditCardDetail.clear();
                    Boolean isFillData = false;
                    for (int i = 0; i < addNewCardResponce.Data.size(); i++) {
                        NewCardData newCardData = new NewCardData();
                        newCardData.Id = addNewCardResponce.Data.get(i).Id;
                        newCardData.NameOnCard = addNewCardResponce.Data.get(i).NameOnCard;
                        newCardData.CardNumber = addNewCardResponce.Data.get(i).CardNumber;
                        newCardData.CardType = addNewCardResponce.Data.get(i).CardType;
                        newCardData.IsDefaultPayment = addNewCardResponce.Data.get(i).IsDefaultPayment;
                        newCardData.ExpiryMonth = addNewCardResponce.Data.get(i).ExpiryMonth;
                        newCardData.ExpiryYear = addNewCardResponce.Data.get(i).ExpiryYear;

                        creditCardDetail.add(newCardData);

                        if (addNewCardResponce.Data.get(i).IsDefaultPayment) {
                            setClickableButtons(false);
                            newCardData.IsSelected = true;
                            isFillData = true;
                            etNameOnCard.setText(newCardData.NameOnCard);
                            etCardNumber.setText(newCardData.CardNumber);
                            etMonth.setText(newCardData.ExpiryMonth);
                            etYear.setText(newCardData.ExpiryYear);
                            year = Integer.parseInt(newCardData.ExpiryYear);
                            month = Integer.parseInt(newCardData.ExpiryMonth);
                            cardTypeSelection(newCardData.CardType);
                        } else {
                            newCardData.IsSelected = false;
                        }
                    }
                    addNewCardResponceData = addNewCardResponce;
                    /**
                     * If there is not any default card in system for client then fill information of first card from list,
                     *
                     * If there is not any card in system added yet then open CardIO's activity for scan card and
                     * also allow user to add card information manually.
                     *
                     */
                    if (!isFillData) {
                        if (creditCardDetail.size() > 0) {
                            setClickableButtons(false);
                            creditCardDetail.get(0).IsSelected = true;
                            etNameOnCard.setText(creditCardDetail.get(0).NameOnCard);
                            etCardNumber.setText(creditCardDetail.get(0).CardNumber);
                            etMonth.setText(creditCardDetail.get(0).ExpiryMonth);
                            etYear.setText(creditCardDetail.get(0).ExpiryYear);
                            year = Integer.parseInt(creditCardDetail.get(0).ExpiryYear);
                            month = Integer.parseInt(creditCardDetail.get(0).ExpiryMonth);
                        } else {
                            onScanPress();
                            setClickableButtons(true);
                        }
                    }

                } else if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    onScanPress();
                    setClickableButtons(true);
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
     * Payment for units purchases and for check if card is valid or not, in response it will give Receipt data to display in Receipt.
     * <p>
     * This payment will continue in background, will show progress in Receipt screen
     */
    public void validateCreditCard() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.ValidateCreditCard(sharedpreferences.getString("Id", ""), CardType, FirstName, LastName, Company, etNameOnCard.getText().toString().trim(),
                etCardNumber.getText().toString().trim(), etCvv.getText().toString().trim(), etMonth.getText().toString().trim(),
                etYear.getText().toString().trim(), "", billingAddress.Address, billingAddress.State,
                billingAddress.City, billingAddress.ZipCode, billingAddress.HomeNumber,
                billingAddress.MobileNumber, new Callback<ValidateCreditCardResponce>() {

                    @Override
                    public void success(ValidateCreditCardResponce validateCreditCardResponce, Response response) {
                        hideProgressDialog();
                        if (validateCreditCardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!validateCreditCardResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", validateCreditCardResponce.Token);
                                editor.apply();
                            }
                            receipt.TotalAmount = validateCreditCardResponce.Data.Total;
                            receipt.FirstName = validateCreditCardResponce.Data.FirstName;
                            receipt.LastName = validateCreditCardResponce.Data.LastName;
                            receipt.Email = validateCreditCardResponce.Data.Email;
                            for (int i = 0; i < validateCreditCardResponce.Data.Units.size(); i++) {
                                ReceiptUnitDetail unitDetail = new ReceiptUnitDetail();
                                unitDetail.PlanName = validateCreditCardResponce.Data.Units.get(i).PlanName;
                                unitDetail.Amount = validateCreditCardResponce.Data.Units.get(i).Price;
                                unitDetail.UnitName = validateCreditCardResponce.Data.Units.get(i).UnitName;
                                unitDetail.Id = validateCreditCardResponce.Data.Units.get(i).Id;
                                unitDetail.Status = validateCreditCardResponce.Data.Units.get(i).Status;
                                unitDetail.PlanType = validateCreditCardResponce.Data.Units.get(i).PlanType;
                                receipt.units.add(unitDetail);
                            }
                            Gson gson = new Gson();
                            Intent intent = new Intent(PaymentMethodActivity.this, ReceiptActivity.class);
                            intent.putExtra("Receipt", gson.toJson(receipt));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else if (validateCreditCardResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(validateCreditCardResponce.Message,
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
     * Payment for Renew Plan, in response it will give Receipt data to display in Receipt.
     * <p>
     * Once payment process will complete then we will get response with conformation
     */

    public void RenewPayment() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.CollectPaymentForRenew(sharedpreferences.getString("Id", ""), CommonId, NId, CardType, etNameOnCard.getText().toString().trim(),
                etCardNumber.getText().toString().trim(), etCvv.getText().toString().trim(), etMonth.getText().toString().trim(),
                etYear.getText().toString().trim(), "", billingAddress.Address, billingAddress.State,
                billingAddress.City, billingAddress.ZipCode, billingAddress.HomeNumber,
                billingAddress.MobileNumber, new Callback<ValidateCreditCardResponce>() {

                    @Override
                    public void success(ValidateCreditCardResponce validateCreditCardResponce, Response response) {
                        hideProgressDialog();
                        if (validateCreditCardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!validateCreditCardResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", validateCreditCardResponce.Token);
                                editor.apply();
                            }
                            receipt.TotalAmount = validateCreditCardResponce.Data.Total;
                            receipt.FirstName = validateCreditCardResponce.Data.FirstName;
                            receipt.LastName = validateCreditCardResponce.Data.LastName;
                            receipt.Email = validateCreditCardResponce.Data.Email;
                            for (int i = 0; i < validateCreditCardResponce.Data.Units.size(); i++) {
                                ReceiptUnitDetail unitDetail = new ReceiptUnitDetail();
                                unitDetail.PlanName = validateCreditCardResponce.Data.Units.get(i).PlanName;
                                unitDetail.Amount = validateCreditCardResponce.Data.Units.get(i).Price;
                                unitDetail.UnitName = validateCreditCardResponce.Data.Units.get(i).UnitName;
                                unitDetail.Id = validateCreditCardResponce.Data.Units.get(i).Id;
                                unitDetail.Status = validateCreditCardResponce.Data.Units.get(i).Status;
                                unitDetail.PlanType = validateCreditCardResponce.Data.Units.get(i).PlanType;
                                receipt.units.add(unitDetail);
                            }
                            Gson gson = new Gson();
                            Intent intent = new Intent(PaymentMethodActivity.this, ReceiptActivity.class);
                            intent.putExtra("Receipt", gson.toJson(receipt));
                            intent.putExtra("From", gson.toJson(From));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else if (validateCreditCardResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                            DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {
                                            globalClass.Clientlogout();
                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");
                        } else if (validateCreditCardResponce.StatusCode.equalsIgnoreCase(globalClass.strPaymentFail)) {
                            DialogFragment ds = new SingleButtonAlert(validateCreditCardResponce.Message,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {
                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");
                        } else {
                            DialogFragment ds = new SingleButtonAlert(validateCreditCardResponce.Message,
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
     * Payment for NoShow Services, in response it will give Receipt data to display in Receipt.
     * <p>
     * Once payment process will complete then we will get response with conformation
     */
    public void NoShowPayment() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.NoShowPayment(sharedpreferences.getString("Id", ""), CommonId, NId, CardType, etNameOnCard.getText().toString().trim(),
                etCardNumber.getText().toString().trim(), etCvv.getText().toString().trim(), etMonth.getText().toString().trim(),
                etYear.getText().toString().trim(), billingAddress.Address, billingAddress.State,
                billingAddress.City, billingAddress.ZipCode, billingAddress.HomeNumber,
                billingAddress.MobileNumber, new Callback<NoShowPaymentResponce>() {

                    @Override
                    public void success(NoShowPaymentResponce noShowPaymentResponce, Response response) {
                        hideProgressDialog();
                        if (noShowPaymentResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!noShowPaymentResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", noShowPaymentResponce.Token);
                                editor.apply();
                            }

                            Gson gson = new Gson();
                            Intent intent = new Intent(PaymentMethodActivity.this, ReceiptNoShowActivity.class);
                            intent.putExtra("Receipt", gson.toJson(noShowPaymentResponce.Data));
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);

                        } else if (noShowPaymentResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(noShowPaymentResponce.Message,
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
}
