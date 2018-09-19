package com.aircall.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aircall.app.Adapter.ReceiptAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.Model.Payment.CheckUnitPaymentResponce;
import com.aircall.app.Model.Receipt.Receipt;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ReceiptActivity extends Activity {

    @Bind(R.id.tvName)
    TextView tvName;

    @Bind(R.id.tvEmail)
    TextView tvEmail;

    @Bind(R.id.tvTotalPackage)
    TextView tvTotalPackage;

    @Bind(R.id.tvTotalAmount)
    TextView tvTotalAmount;

    @Bind(R.id.tvDashboard)
    TextView tvDashboard;

    @Bind(R.id.rvReceipt)
    RecyclerView rvReceipt;

    @Bind(R.id.rlRetry)
    RelativeLayout rlRetry;

    private Receipt receipt = new Receipt();
    private Context context;
    public GlobalClass globalClass;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private ReceiptAdapter receiptAdapter;
    private ProgressDialogFragment progressDialogFragment;
    private int count = 0;
    private float totalAmount = 0;
    private boolean reTryVisibility = false;
    private String From;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receipt);
        ButterKnife.bind(this);
        init();
        clickEvent();
    }

    private void init() {
        context = this;
        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        Intent intent = getIntent();
        String jsonReceipt = intent.getStringExtra("Receipt");
        From = intent.getStringExtra("From");
        Gson gson = new Gson();
        receipt = gson.fromJson(jsonReceipt, Receipt.class);
        tvName.setText(receipt.FirstName + " " + receipt.LastName);
        tvEmail.setText(receipt.Email);
        tvTotalPackage.setText("Total Units (" + receipt.units.size() + ")");
        tvTotalAmount.setText("$"+receipt.TotalAmount);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvReceipt.setLayoutManager(llm);
        receiptAdapter = new ReceiptAdapter(this, receipt.units, From);
        rvReceipt.setAdapter(receiptAdapter);
        receiptAdapter.notifyDataSetChanged();
        for (int i = 0; i < receipt.units.size(); i++) {
            receipt.units.get(i).payment_status = "Received";
        }

//        if (From == null) {
//            /**
//             * If payment is for purchase units, then check status for units.
//             */
//            if (!globalClass.checkInternetConnection()) {
//                DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
//                        new DialogInterfaceClick() {
//                            @Override
//                            public void dialogClick(String tag) {
//
//                            }
//                        });
//                ds.setCancelable(false);
//                ds.show(getFragmentManager(), "");
//            } else {
//                //RequestForNextUnitStatus();
//            }
//        } else {
//            /**
//             * If payment is for Renew plan then fill data directly in Receipt.
//             */
//            tvTotalAmount.setText("$"+receipt.units.get(0).Amount);
//        }

    }

    private void clickEvent() {
        tvDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add Firebase Analytics for total unit purchased
                FirebaseAnalytics mFirebaseAnalytics;
                mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
                Bundle bundle = new Bundle();
                bundle.putString("Unit_Purchased", "Unit Purchased");
                bundle.putString("Unit_Total_Price", "$"+receipt.units.get(0).Amount);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                //Sets whether analytics collection is enabled for this app on this device.
                mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
                //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
                mFirebaseAnalytics.setMinimumSessionDuration(20000);
                //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
                mFirebaseAnalytics.setSessionTimeoutDuration(500);

                Intent intent = new Intent(ReceiptActivity.this, DashboardActivity.class);
                startActivity(intent);
                finish();
            }
        });

        rlRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalClass.checkInternetConnection()) {
                    getPaymentFailedUnit();
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
        });
    }

    /**
     * Check statues for particular unit, if payment done for unit then move to next unit.
     * @param unitId
     */
    public void checkMyPaymentStatus(String unitId) {

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.checkMyPaymentStatus(sharedpreferences.getString("Id", ""), unitId, new Callback<CheckUnitPaymentResponce>() {

            @Override
            public void success(CheckUnitPaymentResponce checkUnitPaymentResponce, retrofit.client.Response response) {

                try {
                    if (checkUnitPaymentResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                        if (!checkUnitPaymentResponce.Token.equalsIgnoreCase("")) {
                            editor.putString("Token", checkUnitPaymentResponce.Token);
                            editor.apply();
                        }
                        if (checkUnitPaymentResponce.Data.Status.equalsIgnoreCase("Received")) {
                            receipt.units.get(count).payment_status = "Received";
                            totalAmount += Float.parseFloat(receipt.units.get(count).Amount);
                            tvTotalAmount.setText("$" + Float.toString(totalAmount));
                            count++;
                            RequestForNextUnitStatus();
                        } else if (checkUnitPaymentResponce.Data.Status.equalsIgnoreCase("Failed")) {
                            receipt.units.get(count).payment_status = "Failed";
                            receipt.units.get(count).StripeError = checkUnitPaymentResponce.Data.StripeError;
                            reTryVisibility = true;
                            count++;
                            RequestForNextUnitStatus();
                        } else {
                            Thread thread = new Thread(new Runnable() {
                                //
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    } finally {
                                        RequestForNextUnitStatus();
                                    }
                                }
                            });
                            thread.start();
                        }
                        receiptAdapter.notifyDataSetChanged();
                    } else if (checkUnitPaymentResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                        RequestForNextUnitStatus();
                    }
                } catch (Exception ex) {
                    Log.e("Exception arrive", "Ex " + ex.getMessage());
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Thread thread = new Thread(new Runnable() {
                    //
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        } finally {
                            RequestForNextUnitStatus();
                        }
                    }
                });
                thread.start();
            }
        });
    }

    /**
     * If payment gets fail and want to retry payment then we will again call below webAPI and get
     * details of failed payment units detail and redirect to Summary screen.
     */
    public void getPaymentFailedUnit() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.getPaymentFailedUnit(sharedpreferences.getString("Id", ""), new Callback<AddUnitResponce>() {

            @Override
            public void success(AddUnitResponce addUnitResponce, Response response) {

                hideProgressDialog();

                if (addUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addUnitResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addUnitResponce.Token);
                        editor.apply();
                    }
                    Gson gson = new Gson();
                    String json = gson.toJson(addUnitResponce);
                    Intent intent = new Intent(ReceiptActivity.this, AddUnitActivity.class);
                    intent.putExtra("addUnit", "summary");
                    intent.putExtra("summaryData", json);
                    startActivity(intent);
                    finish();
                } else if (addUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(addUnitResponce.Message,
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

    private void RequestForNextUnitStatus() {
        if (receipt.units.size() > count) {
            checkMyPaymentStatus(receipt.units.get(count).Id);
        } else {
            if (reTryVisibility) {
                rlRetry.setVisibility(View.VISIBLE);
            } else {
                rlRetry.setVisibility(View.GONE);
            }
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

    @Override
    public void onBackPressed() {

    }
}
