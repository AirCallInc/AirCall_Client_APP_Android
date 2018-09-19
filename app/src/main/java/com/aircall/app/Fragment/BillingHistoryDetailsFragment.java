package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Adapter.BillingHistoryPartsAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.BillingHistory.BillingHistoryResponce;
import com.aircall.app.Model.BillingHistory.PartHistoryData;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class BillingHistoryDetailsFragment extends Fragment {

    @Bind(R.id.ivBackBilling)
    public ImageView ivBack;


    @Bind(R.id.flMain)
    public FrameLayout flMain;

    @Bind(R.id.tvServiceCaseNo)
    TextView tvServiceCaseNo;

//    @Bind(R.id.tvTransactionId)
//    TextView tvTransactionId;

    @Bind(R.id.tvBillingDetailsDate)
    TextView tvBillingDetailsDate;

    @Bind(R.id.tvBillingDetailsTime)
    TextView tvBillingDetailsTime;

    @Bind(R.id.tvBillingDetailsUnitServiced)
    TextView tvBillingDetailsUnitServiced;

    @Bind(R.id.tvFailedPaymentReasion)
    TextView tvFailedPaymentReasion;

    @Bind(R.id.tvBillingDetailsAmount)
    TextView tvBillingDetailsAmount;

//    @Bind(R.id.tvBillingPaymentType)
//    TextView tvBillingPaymentType;

    @Bind(R.id.tvPaymentNumber)
    TextView tvPaymentNumber;

    @Bind(R.id.tvnInvoiceNo)
    TextView tvnInvoiceNo;

//    @Bind(R.id.llPartsDetails)
//    LinearLayout llPartsDetails;

    @Bind(R.id.llUnitServiced)
    LinearLayout llUnitServiced;

    @Bind(R.id.llFailedPaymentReasion)
    LinearLayout llFailedPaymentReasion;

//    @Bind(R.id.rvPartList)
//    RecyclerView rvPartList;

    GlobalClass globalClass;
    Activity activity;
    String date, Reason, BillingId = null;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Boolean IsPaid;

    /**
     * Adapter for part listing
     */
    BillingHistoryPartsAdapter partsAdapter;
    ArrayList<PartHistoryData> partsHistory = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_billing_details, container, false);
        ButterKnife.bind(this, view);

        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        LinearLayoutManager llm1 = new LinearLayoutManager(activity);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        //rvPartList.setLayoutManager(llm1);
        partsAdapter = new BillingHistoryPartsAdapter(activity, partsHistory);
        //rvPartList.setAdapter(partsAdapter);

        /**
         * If fragment is not from notification then get information from extra argument and set in TextViews.
         */
        if (((UserProfileActivity) activity).CommonId == null) {
            String strDateTime = getArguments().getString("strTransactionDate");
            Reason = getArguments().getString("Reason");
            IsPaid = getArguments().getBoolean("IsPaid");
            String[] separated = strDateTime.split("T");
            String strDate = separated[0];
            String strTime = separated[1];
            strTime = strTime.replace("Z", "");

            SimpleDateFormat input = new SimpleDateFormat("yy-MM-dd");
            SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy");

            SimpleDateFormat inputTime = new SimpleDateFormat("HH:mm:ss");
            SimpleDateFormat outputTime = new SimpleDateFormat("hh:mm a");

            try {
                Date oneWayTripDate = input.parse(strDate);                     // parse input
                tvBillingDetailsDate.setText(output.format(oneWayTripDate));    // format output

                Date dateT = inputTime.parse(strTime);
                tvBillingDetailsTime.setText(outputTime.format(dateT));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            BillingId = getArguments().getString("strId");
            //tvServiceCaseNo.setText(getArguments().getString("strServiceCaseNumber"));
            tvBillingDetailsAmount.setText("$" + getArguments().getString("strPurchasedAmount"));
            //tvTransactionId.setText(getArguments().getString("strTransactionId"));
            //tvBillingPaymentType.setText(getArguments().getString("ChargeBy"));
            tvPaymentNumber.setText(getArguments().getString("CardNumber"));
            tvServiceCaseNo.setText(getArguments().getString("OrderNumber"));
            tvnInvoiceNo.setText(getArguments().getString("InvoiceNumber"));
            tvBillingDetailsUnitServiced.setText(getArguments().getString("strUnitName"));

            if (IsPaid) {
                llFailedPaymentReasion.setVisibility(View.GONE);
            } else {
                llFailedPaymentReasion.setVisibility(View.VISIBLE);
                tvFailedPaymentReasion.setText(Reason);
            }

        } else {
            BillingId = (((UserProfileActivity) activity).CommonId);
        }

//        if (getArguments().getString("strUnitName").equalsIgnoreCase("")) {
//            llUnitServiced.setVisibility(View.GONE);
//            if (getArguments().getString("strPlanName").equalsIgnoreCase("Part Order")) {
//                llPartsDetails.setVisibility(View.VISIBLE);
//            } else {
//                llPartsDetails.setVisibility(View.GONE);
//            }
//        } else {
//            llPartsDetails.setVisibility(View.GONE);
//            llUnitServiced.setVisibility(View.VISIBLE);
//            tvBillingDetailsUnitServiced.setText(getArguments().getString("strUnitName"));
//        }

        if (globalClass.checkInternetConnection()) {
            GetBillingHistoryDetail();

        } else {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {
                            activity.onBackPressed();
                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        }


        clickEvent();
        return view;
    }

    /**
     * Web API call for Get billing detail.
     */

    public void GetBillingHistoryDetail() {

        ((UserProfileActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getBillingHistoryDetail(sharedpreferences.getString("Id", ""), BillingId,
                new Callback<BillingHistoryResponce>() {

                    @Override
                    public void success(BillingHistoryResponce billingHistoryDetailResponce, Response response) {

                        ((UserProfileActivity) getActivity()).hideProgressDialog();

                        if (billingHistoryDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!billingHistoryDetailResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", billingHistoryDetailResponce.Token);
                                editor.apply();
                            }
                            //partsHistory = billingHistoryDetailResponce.Data.PartsHistory;
                            //partsAdapter = new BillingHistoryPartsAdapter(activity, partsHistory);
                            //rvPartList.setAdapter(partsAdapter);

//                            if (partsHistory == null || partsHistory.size() == 0) {
//                                llPartsDetails.setVisibility(View.GONE);
//                            } else {
//                                llPartsDetails.setVisibility(View.VISIBLE);
//                            }

                            if (BillingId != null) {
                                tvServiceCaseNo.setText(billingHistoryDetailResponce.Data.get(0).OrderNumber);
                                tvnInvoiceNo.setText(billingHistoryDetailResponce.Data.get(0).InvoiceNumber);
                                //tvBillingDetailsAmount.setText("$" + billingHistoryDetailResponce.Data.PurchasedAmount);
                                //tvTransactionId.setText(billingHistoryDetailResponce.Data.TransactionId);
                                //tvBillingPaymentType.setText(billingHistoryDetailResponce.Data.ChargeBy);
                                tvPaymentNumber.setText(billingHistoryDetailResponce.Data.get(0).CardNumber);
                                //IsPaid = billingHistoryDetailResponce.Data.IsPaid;
                                //Reason = billingHistoryDetailResponce.Data.Reason;
                                if (!IsPaid) {
                                    tvFailedPaymentReasion.setVisibility(View.VISIBLE);
                                    tvFailedPaymentReasion.setText(Reason);
                                }
                                //String strDateTime = billingHistoryDetailResponce.Data.TransactionDate;
//                                String[] separated = strDateTime.split("T");
//                                String strDate = separated[0];
//                                String strTime = separated[1];
//                                strTime = strTime.replace("Z", "");
//
//                                SimpleDateFormat input = new SimpleDateFormat("yy-MM-dd");
//                                SimpleDateFormat output = new SimpleDateFormat("MMM dd, yyyy");
//
//                                SimpleDateFormat inputTime = new SimpleDateFormat("HH:mm:ss");
//                                SimpleDateFormat outputTime = new SimpleDateFormat("hh:mm a");

//                                try {
//                                    Date oneWayTripDate = input.parse(strDate);                 // parse input
//                                    tvBillingDetailsDate.setText(output.format(oneWayTripDate));    // format output
//
//                                    Date dateT = inputTime.parse(strTime);
//                                    tvBillingDetailsTime.setText(outputTime.format(dateT));
//                                } catch (ParseException e) {
//                                    e.printStackTrace();
//                                }
                            }

                        } else if (billingHistoryDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                            DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {
                                            globalClass.Clientlogout();
                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");
                            ((UserProfileActivity) getActivity()).hideProgressDialog();
                        } else {
                            //llPartsDetails.setVisibility(View.GONE);
                            ((UserProfileActivity) getActivity()).hideProgressDialog();
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        //llPartsDetails.setVisibility(View.GONE);
                        ((UserProfileActivity) getActivity()).hideProgressDialog();
                    }
                });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((UserProfileActivity) activity).globalClass;
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
    }

}
