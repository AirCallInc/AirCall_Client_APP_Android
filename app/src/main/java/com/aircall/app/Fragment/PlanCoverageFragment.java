package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Adapter.PlanCoverageAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.GetAllPlanType.GetPlanTypesResponce;
import com.aircall.app.Model.GetAllPlanType.UnitTypeDetail;
import com.aircall.app.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PlanCoverageFragment extends Fragment {

    @Bind(R.id.rvPlanCoverage)
    RecyclerView rvPlanCoverage;

    @Bind(R.id.ivNavDrawer)
    ImageView ivNavDrawer;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.pvPlanComparison)
    TextView  pvPlanComparison;

    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    PlanCoverageAdapter planCoverageAdapter;
    //PlanCoverageResponce planCoverageResponceData;
    ArrayList<UnitTypeDetail> planCoverageDatas;
    String strPlanTypeId, strPlanName, strFragmentTag;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_plan_coverage,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvents();

        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        GridLayoutManager llm = new GridLayoutManager(activity, 2);
        llm.setOrientation(GridLayoutManager.VERTICAL);
        rvPlanCoverage.setLayoutManager(llm);
        if (globalClass.checkInternetConnection()) {
            getPlanCoverageList();
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

    private void clickEvents() {
        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).openDrawer();
            }
        });

        /**
         * On click event of any item, redirect to PlanDetailFragment.
         */
        rvPlanCoverage.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {

                        FragmentTransaction transaction = getFragmentManager().beginTransaction();
                        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_middel);

                        Fragment newFragment = new PlanDetailFragment();
                        strFragmentTag = newFragment.toString();
                        strPlanTypeId = planCoverageDatas.get(position).Id;
                        strPlanName = planCoverageDatas.get(position).PlanName;

                        Bundle args = new Bundle();
                        args.putString("PlanTypeId", strPlanTypeId);
                        args.putString("PlanName", strPlanName);
                        newFragment.setArguments(args);
                        transaction.add(R.id.frame_middel, newFragment, strFragmentTag);
                        transaction.addToBackStack("");
                        transaction.commit();

                        strPlanTypeId = planCoverageDatas.get(position).Id;
                    }
                })
        );

        pvPlanComparison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                intent.putExtra("dash", "terms");
                intent.putExtra("PdfUrl", ServiceGenerator.BASE_URL + "/uploads/plan/Aicall_Plan_v2.pdf");
                intent.putExtra("PageTitle", "Plan Comparison");
                startActivity(intent);
            }
        });
    }

    /**
     * Call webAPI for get Plan listing and fill it in @rvPlanCoverage
     */
    public void getPlanCoverageList() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getAllPlanType(new Callback<GetPlanTypesResponce>() {
            @Override
            public void success(GetPlanTypesResponce planTypeResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                if (planTypeResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!planTypeResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", planTypeResponce.Token);
                        editor.apply();
                    }
                    planCoverageDatas = planTypeResponce.Data;
                    planCoverageAdapter = new PlanCoverageAdapter(activity, planCoverageDatas);
                    rvPlanCoverage.setAdapter(planCoverageAdapter);
                } else if (planTypeResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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

                Log.e("failor", "failor " + error.getMessage());
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;

    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((DashboardActivity) activity).last = "plan_coverage";
    }
}
