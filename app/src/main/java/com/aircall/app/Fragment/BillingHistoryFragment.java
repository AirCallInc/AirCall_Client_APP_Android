package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Adapter.BilingHistoryListAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.RecyclerTouchListener;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.ClickListener;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.BillingHistory.BillingHistoryResponce;
import com.aircall.app.Model.BillingHistory.BillingHistorydData;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BillingHistoryFragment extends Fragment {

    GlobalClass globalClass;
    Activity activity;
    Context context;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    /**Billing History Listing adapter and Model array*/
    private ArrayList<BillingHistorydData> billingHistorylist = new ArrayList<>();
    BilingHistoryListAdapter bilingHistoryListAdapter;

    @Bind(R.id.rvBillingHistory)
    RecyclerView rvBillingHistory;

    @Bind(R.id.flMain)
    FrameLayout flMain;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.txtBillingNoData)
    TextView txtBillingNoData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_billing_history, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvent();

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((UserProfileActivity) activity).globalClass;

    }

    public void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        bilingHistoryListAdapter = new BilingHistoryListAdapter(activity, BillingHistoryFragment.this, billingHistorylist);
        LinearLayoutManager llm1 = new LinearLayoutManager(activity);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rvBillingHistory.setLayoutManager(llm1);
        rvBillingHistory.setAdapter(bilingHistoryListAdapter);

        if(globalClass.checkInternetConnection()) {
            getBillingHistory();
        } else {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {
                            activity.finish();
                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        }
    }

    public void clickEvent() {
        flMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        /**
         * Redirect to BillingHistoryDetailsFragment with data of billing history detail,
         * Data will be use in billing history detail to show data if BillingHistoryDetail API sucks.
         */
        rvBillingHistory.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), rvBillingHistory, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                FragmentManager fm = getFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                Fragment fragment = fm.findFragmentById(R.id.frm_user_activity);

                Fragment newFragment = new BillingHistoryDetailsFragment();
                Bundle args = new Bundle();
                args.putString("strId", billingHistorylist.get(position).Id);
                args.putString("strPlanName", billingHistorylist.get(position).PlanName);
                args.putString("strPurchasedAmount", billingHistorylist.get(position).PurchasedAmount);
                args.putString("strTransactionDate", billingHistorylist.get(position).TransactionDate);
                args.putString("strTransactionId", billingHistorylist.get(position).TransactionId);
                args.putString("strUnitName", billingHistorylist.get(position).UnitName);
                args.putString("strServiceCaseNumber", billingHistorylist.get(position).ServiceCaseNumber);
                args.putString("Reason", billingHistorylist.get(position).Reason);
                args.putString("ChargeBy", billingHistorylist.get(position).ChargeBy);
                args.putString("CardNumber", billingHistorylist.get(position).CardNumber);
                args.putBoolean("IsPaid", billingHistorylist.get(position).IsPaid);
                args.putString("OrderNumber", billingHistorylist.get(position).OrderNumber);
                args.putString("InvoiceNumber", billingHistorylist.get(position).InvoiceNumber);
                newFragment.setArguments(args);
                transaction.addToBackStack("");
                transaction.add(R.id.frm_user_activity, newFragment).commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    /**
     * Web API for billing history listing
     */
    public void getBillingHistory() {

        ((UserProfileActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getBillingHistorylist(sharedpreferences.getString("Id", ""), new Callback<BillingHistoryResponce>() {

            @Override
            public void success(BillingHistoryResponce billingHistoryResponce, Response response) {

                ((UserProfileActivity) getActivity()).hideProgressDialog();

                if (billingHistoryResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!billingHistoryResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", billingHistoryResponce.Token);
                        editor.apply();
                    }
                    Log.e("Billing Success", String.valueOf(billingHistoryResponce.Data.size()));

//                    billingHistorylist.clear();

                    for (int i = 0; i < billingHistoryResponce.Data.size(); i++) {
                        BillingHistorydData billingHistorydData = new BillingHistorydData();
                        billingHistorydData.Id = billingHistoryResponce.Data.get(i).Id;
                        billingHistorydData.PlanName = billingHistoryResponce.Data.get(i).PlanName;
                        billingHistorydData.PurchasedAmount = billingHistoryResponce.Data.get(i).PurchasedAmount;
                        billingHistorydData.TransactionDate = billingHistoryResponce.Data.get(i).TransactionDate;
                        billingHistorydData.TransactionId= billingHistoryResponce.Data.get(i).TransactionId;
                        billingHistorydData.UnitName = billingHistoryResponce.Data.get(i).UnitName;
                        billingHistorydData.ServiceCaseNumber = billingHistoryResponce.Data.get(i).ServiceCaseNumber;
                        billingHistorydData.IsPaid = billingHistoryResponce.Data.get(i).IsPaid;
                        billingHistorydData.Reason = billingHistoryResponce.Data.get(i).Reason;
                        billingHistorydData.ChargeBy = billingHistoryResponce.Data.get(i).ChargeBy;
                        billingHistorydData.CardNumber = billingHistoryResponce.Data.get(i).CardNumber;
                        billingHistorydData.OrderNumber = billingHistoryResponce.Data.get(i).OrderNumber;
                        billingHistorydData.InvoiceNumber = billingHistoryResponce.Data.get(i).InvoiceNumber;


                        billingHistorylist.add(billingHistorydData);
                    }
                    bilingHistoryListAdapter.notifyDataSetChanged();

                } else if (billingHistoryResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    rvBillingHistory.setVisibility(View.GONE);
                    txtBillingNoData.setVisibility(View.VISIBLE);
                    txtBillingNoData.setText(billingHistoryResponce.Message);

                } else if (billingHistoryResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    rvBillingHistory.setVisibility(View.GONE);
                    txtBillingNoData.setVisibility(View.VISIBLE);
                    txtBillingNoData.setText(ErrorMessages.ErrorGettingData);

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
                    rvBillingHistory.setVisibility(View.GONE);
                    txtBillingNoData.setVisibility(View.VISIBLE);
                    txtBillingNoData.setText(ErrorMessages.ErrorGettingData);

                    DialogFragment ds = new SingleButtonAlert(billingHistoryResponce.Message,
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
                rvBillingHistory.setVisibility(View.GONE);
                txtBillingNoData.setVisibility(View.VISIBLE);
                txtBillingNoData.setText(ErrorMessages.ErrorGettingData);

                ((UserProfileActivity) getActivity()).hideProgressDialog();
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
