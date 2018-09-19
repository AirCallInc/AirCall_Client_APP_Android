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
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.PlanCoverage.PlanCoverageDetailResponce;
import com.aircall.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PlanDetailFragment extends Fragment {

    GlobalClass globalClass;
    Activity activity;
    String PlanTypeId;

    @Bind(R.id.tvPackageA)
    TextView tvPackageA;

    @Bind(R.id.tvPackageB)
    TextView tvPackageB;

    @Bind(R.id.ivBack)
    public ImageView ivBack;

    @Bind(R.id.tvHeaderName)
    TextView tvHeaderName;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.wvPlanCoverageDetail)
    WebView wvPlanCoverageDetail;

    String PlanName = null;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    PlanCoverageDetailResponce planCoverageDetailResponceData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan_detail, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvent();

        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        PlanTypeId = getArguments().getString("PlanTypeId");
        PlanName = getArguments().getString("PlanName");
        ((DashboardActivity) activity).last = "PlanDetailFragment";

        tvHeaderName.setText(PlanName);
        getPackageDetail();
    }

    public void clickEvent() {

        /**
         * Load data of package A in WebView (@wvPlanCoverageDetail)
         */
        tvPackageA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                tvPackageA.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                tvPackageA.setTextColor(getResources().getColor(R.color.white));

                tvPackageB.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvPackageB.setTextColor(getResources().getColor(R.color.black));

                wvPlanCoverageDetail.getSettings().setJavaScriptEnabled(true);
                wvPlanCoverageDetail.loadDataWithBaseURL("", planCoverageDetailResponceData.Data.get(0).Description, "text/html", "UTF-8", "");
            }
        });

        /**
         * Load data of package B in WebView (@wvPlanCoverageDetail)
         */
        tvPackageB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvPackageB.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                tvPackageB.setTextColor(getResources().getColor(R.color.white));

                tvPackageA.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvPackageA.setTextColor(getResources().getColor(R.color.black));

                wvPlanCoverageDetail.getSettings().setJavaScriptEnabled(true);
                wvPlanCoverageDetail.loadDataWithBaseURL("", planCoverageDetailResponceData.Data.get(1).Description, "text/html", "UTF-8", "");

            }
        });

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

    }

    /**
     * Call WebAPI to get Plan detail
     */
    public void getPackageDetail() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.getPlanDetail(PlanTypeId, new Callback<PlanCoverageDetailResponce>() {

            @Override
            public void success(PlanCoverageDetailResponce planCoverageDetailResponce, Response response) {

                ((DashboardActivity) getActivity()).hideProgressDialog();

                if (planCoverageDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!planCoverageDetailResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", planCoverageDetailResponce.Token);
                        editor.apply();
                    }

                    planCoverageDetailResponceData = planCoverageDetailResponce;
                    tvPackageA.setText(planCoverageDetailResponceData.Data.get(0).Name);
                    tvPackageB.setText(planCoverageDetailResponceData.Data.get(1).Name);
                    wvPlanCoverageDetail.getSettings().setJavaScriptEnabled(true);
                    wvPlanCoverageDetail.loadDataWithBaseURL("", planCoverageDetailResponceData.Data.get(0).Description, "text/html", "UTF-8", "");

                } else if (planCoverageDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(planCoverageDetailResponce.Message,
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
