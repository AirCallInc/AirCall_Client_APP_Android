package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.aircall.app.Adapter.SummaryListAdapter;
import com.aircall.app.AddUnitActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by tommy on 2018/8/9.
 */

public class SummaryNFrament extends Fragment {
    @Bind(R.id.rvSummary)
    RecyclerView rvSummary;

    @Bind(R.id.tvSummaryTotalAmount)
    TextView tvSummaryTotalAmount;

    @Bind(R.id.tvSummaryAddAnotherUnit)
    TextView tvSummaryAddAnotherUnit;

    @Bind(R.id.tvSummaryMessage)
    TextView tvSummaryMessage;

    @Bind(R.id.tvSummaryCheckout)
    TextView tvSummaryCheckout;

    @Bind(R.id.flMain)
    public FrameLayout flMain;

    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    SummaryListAdapter listAdapter;
    AddUnitResponce addUnitResponce;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary,
                container, false);
        ButterKnife.bind(this, view);
        initdata();
        clickEvent();
        return view;
    }

    private void initdata() {
        sharedpreferences =  ((DashboardActivity) getActivity()).getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

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
           getPaymentFailedUnit();
        }

    }

    private void clickEvent() {

    }

    public void getPaymentFailedUnit() {
        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.getPaymentFailedUnit(sharedpreferences.getString("Id", ""), new Callback<AddUnitResponce>() {

            @Override
            public void success(AddUnitResponce addUnitResponce1, Response response) {

                ((AddUnitActivity) getActivity()).hideProgressDialog();

                if (addUnitResponce1.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addUnitResponce1.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addUnitResponce1.Token);
                        editor.apply();
                    }


                } else if (addUnitResponce1.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(addUnitResponce1.Message,
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

                ((AddUnitActivity) getActivity()).hideProgressDialog();
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DashboardActivity) activity).last = "unsubmitted_units";
    }
}
