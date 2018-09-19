package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Adapter.SummaryListAdapter;
import com.aircall.app.AddUnitActivity;
import com.aircall.app.BillingAddressActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.AddUnitInterface;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddUnit.AddUnitRequest;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.Model.AddUnit.CommonDataRequest;
import com.aircall.app.Model.DynamicViews.FilterViews;
import com.aircall.app.Model.DynamicViews.FuseViews;
import com.aircall.app.Model.Receipt.Receipt;
import com.aircall.app.R;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SummaryFragment extends Fragment {

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
    private AddUnitInterface addUnitInteface;
    AddUnitResponce addUnitResponce;
    SummaryListAdapter listAdapter;
    Receipt receipt = new Receipt();
    private String CommonId, NId, NType;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_summary,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvent();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvSummary.setLayoutManager(llm);
        if (getArguments().getString("summary") != null) {
            /**
             * if fragment start after add the unit
             */
            String json = getArguments().getString("summary");
            Gson gson = new Gson();
            addUnitResponce = gson.fromJson(json, AddUnitResponce.class);

            listAdapter = new SummaryListAdapter(activity, globalClass, sharedpreferences, editor, SummaryFragment.this, addUnitResponce.Data.Units);
            rvSummary.setAdapter(listAdapter);

            tvSummaryMessage.setText(addUnitResponce.Data.Message);
            tvSummaryTotalAmount.setText("$" + addUnitResponce.Data.Total);
        } else {
            /**
             * if fragment start from Notification click
             */
            CommonId = getArguments().getString("CommonId");
            NId = getArguments().getString("NId");
            NType = getArguments().getString("NType", "");

            if (globalClass.checkInternetConnection()) {
                if (NType.equalsIgnoreCase("11")) {
                    getPaymentFailedUnit();
                } else if (NType.equalsIgnoreCase("8")) {
                    GetExpiredPlanUnitById();
                }
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
    }

    private void clickEvent() {
        /**
         * Clear all the data of previous unit and again start to Mandatory Fragment.
         */
        tvSummaryAddAnotherUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (addUnitResponce.Data.Units.size() < 5) {
                    ((AddUnitActivity) activity).Quantity = addUnitResponce.Data.Units.size();
                    ((AddUnitActivity) activity).addUnitRequest = null;
                    ((AddUnitActivity) activity).addUnitRequest = new AddUnitRequest();
                    ((AddUnitActivity) activity).commonDataRequest = null;
                    ((AddUnitActivity) activity).commonDataRequest = new CommonDataRequest();
                    ((AddUnitActivity) activity).filterDetail = null;
                    ((AddUnitActivity) activity).filterDetail = new FilterViews();
                    ((AddUnitActivity) activity).fuseViews = null;
                    ((AddUnitActivity) activity).fuseViews = new FuseViews();
                    ((AddUnitActivity) activity).addUnitRequest.AddressId = "";
                    addUnitInteface.changeFragment("mendetory", true, null);
                } else {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.max5unit,
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

        tvSummaryCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!addUnitResponce.Data.ErrMessage.equals("")) {
                    DialogFragment ds = new SingleButtonAlert(addUnitResponce.Data.ErrMessage,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                } else {
                    ((AddUnitActivity) activity).Quantity = 0;
                    Intent intent = new Intent(getActivity(), BillingAddressActivity.class);
                    intent.putExtra("TotalAmount", addUnitResponce.Data.Total);
                    if (NType != null && NType.equalsIgnoreCase("8")) {
                        intent.putExtra("CommonId", CommonId);
                        intent.putExtra("NId", NId);
                        intent.putExtra("From", "ReNewPlan");
                    }
                    startActivity(intent);
                }

            }
        });

        flMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    /**
     * Remove unit from Summary
     *
     * @param position
     * @param addUnitResponce1
     */
    public void removeUnit(int position, AddUnitResponce addUnitResponce1) {
        Toast.makeText(activity, addUnitResponce1.Message, Toast.LENGTH_LONG).show();
        addUnitResponce.Data.Units.remove(position);
        listAdapter.notifyDataSetChanged();
        if (addUnitResponce.Data.Units.size() > 0) {
            tvSummaryMessage.setText(addUnitResponce1.Data.Message);
            tvSummaryTotalAmount.setText("$" + addUnitResponce1.Data.Total);
            addUnitResponce.Data.Total = addUnitResponce1.Data.Total;
            addUnitResponce.Data.Message = addUnitResponce1.Data.Message;
        } else {
            ((AddUnitActivity) activity).addUnitRequest = null;
            ((AddUnitActivity) activity).addUnitRequest = new AddUnitRequest();
            ((AddUnitActivity) activity).commonDataRequest = null;
            ((AddUnitActivity) activity).commonDataRequest = new CommonDataRequest();
            ((AddUnitActivity) activity).filterDetail = null;
            ((AddUnitActivity) activity).filterDetail = new FilterViews();
            ((AddUnitActivity) activity).fuseViews = null;
            ((AddUnitActivity) activity).fuseViews = new FuseViews();
            ((AddUnitActivity) activity).addUnitRequest.AddressId = "";
            addUnitInteface.changeFragment("mendetory", false, null);
        }
    }

    /**
     * Get Expired plan by id to renew particular plan
     */
    public void GetExpiredPlanUnitById() {

        Log.e("CommonId NId", CommonId + " and " + NId);

        ((AddUnitActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getExpiredPlanUnitById(sharedpreferences.getString("Id", ""), CommonId, NId, new Callback<AddUnitResponce>() {

            @Override
            public void success(AddUnitResponce addUnitResponce1, Response response) {

                ((AddUnitActivity) getActivity()).hideProgressDialog();

                if (addUnitResponce1.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addUnitResponce1.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addUnitResponce1.Token);
                        editor.apply();
                    }
                    addUnitResponce = addUnitResponce1;
                    listAdapter = new SummaryListAdapter(activity, globalClass, sharedpreferences, editor, SummaryFragment.this, addUnitResponce.Data.Units);
                    rvSummary.setAdapter(listAdapter);

                    tvSummaryMessage.setText(addUnitResponce1.Data.Message);
                    tvSummaryTotalAmount.setText("$" + addUnitResponce1.Data.Total);

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
                Log.e("error", "error " + error.getMessage());

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
     * Get Failed payment units
     */
    public void getPaymentFailedUnit() {

        ((AddUnitActivity) getActivity()).showProgressDailog("Please Wait...");
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
                    addUnitResponce = addUnitResponce1;
                    listAdapter = new SummaryListAdapter(activity, globalClass, sharedpreferences, editor, SummaryFragment.this, addUnitResponce.Data.Units);
                    rvSummary.setAdapter(listAdapter);

                    tvSummaryMessage.setText(addUnitResponce1.Data.Message);
                    tvSummaryTotalAmount.setText("$" + addUnitResponce1.Data.Total);

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
        addUnitInteface = (AddUnitActivity) activity;
        globalClass = ((AddUnitActivity) activity).globalClass;

    }

}
