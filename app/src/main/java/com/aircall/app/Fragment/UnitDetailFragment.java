package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.EditUnitFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.MyUnits.MyUnitsData;
import com.aircall.app.Model.MyUnits.UnitsDetailResponce;
import com.aircall.app.R;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UnitDetailFragment extends Fragment {


    @Bind(R.id.tvUnitName)
    TextView tvUnitName;

    @Bind(R.id.tvUnitPlan)
    TextView tvUnitPlan;

    @Bind(R.id.tvUnitAge)
    TextView tvUnitAge;

    @Bind(R.id.tvServiceman)
    TextView tvServiceman;

    @Bind(R.id.tvLastServices)
    TextView tvLastServices;

    @Bind(R.id.tvUpcomingServices)
    TextView tvUpcomingServices;

    @Bind(R.id.tvTotalServices)
    TextView tvTotalServices;

    @Bind(R.id.tvRemainingServices)
    TextView tvRemainingServices;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.llEditUnit)
    LinearLayout llEditUnit;

    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private MyUnitsData myUnitsData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_unit_detail,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvent();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences("AircallData", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        ((DashboardActivity) activity).last = "UnitDetailFragment";
        if (getArguments().getString("UnitDetailData") != null) {
            String json = getArguments().getString("UnitDetailData");
            Gson gson = new Gson();
            myUnitsData = gson.fromJson(json, MyUnitsData.class);
            setData();
        } else {
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
                getClientUnitDetail(getArguments().getString("UnitId"));
            }
        }

    }

    private void clickEvent() {
        llMain.setOnClickListener(new View.OnClickListener() {
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

        llEditUnit.setOnClickListener(new View.OnClickListener() {
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

                }else {
                    DialogFragment ds = new EditUnitFragment(globalClass, tvUnitName.getText().toString(),
                            myUnitsData.Id, new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {
                            if (!tag.equalsIgnoreCase("")) {
                                tvUnitName.setText(tag);
                            }
                        }
                    });
                    ds.show(getFragmentManager(), "");
                }
            }
        });

    }

    /**
     * Set data
     */
    private void setData() {
        tvUnitName.setText(myUnitsData.UnitName);
        tvUnitPlan.setText(myUnitsData.PlanName);
        tvUnitAge.setText(myUnitsData.UnitAge);
        tvLastServices.setText(myUnitsData.LastService);
        tvUpcomingServices.setText(myUnitsData.UpcomingService);
        tvTotalServices.setText(myUnitsData.TotalService);
        tvRemainingServices.setText(myUnitsData.RemainingService);
        if ("NA".equalsIgnoreCase(myUnitsData.EmpFirstName)) {
            tvServiceman.setText(myUnitsData.EmpFirstName);
        } else {
            tvServiceman.setText(myUnitsData.EmpFirstName + " " + myUnitsData.EmpLastName);
        }
        ((DashboardActivity) activity).last = "UnitDetailFragment";
    }

    /**
     * Get client unit detail from web API
     * @param UnitId
     */
    public void getClientUnitDetail(String UnitId) {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.getClientUnitDetail(sharedpreferences.getString("Id", ""), UnitId, new Callback<UnitsDetailResponce>() {

            @Override
            public void success(UnitsDetailResponce unitsDetailResponce, Response response) {

                ((DashboardActivity) getActivity()).hideProgressDialog();

                if (unitsDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!unitsDetailResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", unitsDetailResponce.Token);
                        editor.apply();
                    }
                    myUnitsData = unitsDetailResponce.Data;
                    setData();

                } else if (unitsDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(unitsDetailResponce.Message,
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

                ((DashboardActivity) getActivity()).hideProgressDialog();
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
}
