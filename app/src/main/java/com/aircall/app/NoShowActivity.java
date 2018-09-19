package com.aircall.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.NoShowNotification.NoShowDetailsResponce;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NoShowActivity extends Activity {

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.tvNoShowDetailsPay)
    TextView tvNoShowDetailsPay;

    @Bind(R.id.tvNoShowDetailsAmount)
    TextView tvNoShowDetailsAmount;

    @Bind(R.id.tvNoShowDetails)
    TextView tvNoShowDetails;

    @Bind(R.id.tvNoShowDetailsDetailDesc)
    TextView tvNoShowDetailsDetailDesc;

    @Bind(R.id.tvMessage)
    TextView tvMessage;

    String CommonId, NId;
    public GlobalClass globalClass;
    private ProgressDialogFragment progressDialogFragment;
    Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_show);
        ButterKnife.bind(this);

        init();
        clickEvent();
    }

    private void init() {
        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        CommonId = getIntent().getExtras().getString("CommonId");
        NId = getIntent().getExtras().getString("NId");

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
            getNoShowData();
        }
    }

    public void clickEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tvNoShowDetailsPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NoShowActivity.this, BillingAddressActivity.class);
                intent.putExtra("TotalAmount", amount);
                intent.putExtra("From", "NoShow");
                intent.putExtra("CommonId", CommonId);
                intent.putExtra("NId", NId);
                startActivity(intent);
            }
        });
    }

    public void getNoShowData() {

        Log.e("Id", sharedpreferences.getString("Id", ""));
        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.noShowServiceDetails(sharedpreferences.getString("Id", ""), CommonId, NId, new Callback<NoShowDetailsResponce>() {
            @Override
            public void success(NoShowDetailsResponce noShowDetailsResponce, Response response) {
                hideProgressDialog();
                if (noShowDetailsResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!noShowDetailsResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", noShowDetailsResponce.Token);
                        editor.apply();
                    }
                    tvNoShowDetails.setText("For service " + noShowDetailsResponce.Data.ServiceCaseNumber + " performed on " + noShowDetailsResponce.Data.ScheduleDate +
                            " by " + noShowDetailsResponce.Data.EmpFirstName + " " + noShowDetailsResponce.Data.EmpLastName);
                    tvNoShowDetailsDetailDesc.setText(noShowDetailsResponce.Data.Reason);
                    tvMessage.setText(noShowDetailsResponce.Data.Message);
                    if (noShowDetailsResponce.Data.CollectPayment) {
                        tvNoShowDetailsAmount.setText("$" + noShowDetailsResponce.Data.NoShowAmount);
                        amount = noShowDetailsResponce.Data.NoShowAmount;
                        tvNoShowDetailsPay.setVisibility(View.VISIBLE);
                    } else {
                        tvNoShowDetailsPay.setVisibility(View.GONE);
                    }

                } else if (noShowDetailsResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    hideProgressDialog();
                    DialogFragment ds = new SingleButtonAlert(noShowDetailsResponce.Message,
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
                DialogFragment ds = new SingleButtonAlert(error.getMessage(),
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

    private void HideKeyboard() {
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

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            finish();
            Intent intent = new Intent(this, DashboardActivity.class);
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
}