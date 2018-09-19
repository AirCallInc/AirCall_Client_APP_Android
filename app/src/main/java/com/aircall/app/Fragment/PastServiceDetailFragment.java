package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Adapter.PastServiceDetailUnitsAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.RoundedTransformation;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.PastServices.PastServiceDetailResponce;
import com.aircall.app.Model.PastServices.RattingResponce;
import com.aircall.app.R;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PastServiceDetailFragment extends Fragment {

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.llWorkDoneData)
    LinearLayout llWorkDoneData;

    @Bind(R.id.llUnitList)
    LinearLayout llUnitList;

    @Bind(R.id.llWorkDoneData1)
    LinearLayout llWorkDoneData1;

    @Bind(R.id.llExtraTime)
    LinearLayout llExtraTime;

    @Bind(R.id.llDifferentTimes)
    LinearLayout llDifferentTimes;

    @Bind(R.id.tvSCN)
    TextView tvSCN;

    /*@Bind(R.id.tvPlan)
    TextView tvPlan;*/

    @Bind(R.id.tvDate)
    TextView tvDate;

    @Bind(R.id.tvAssignTotalTime)
    TextView tvAssignTotalTime;

    @Bind(R.id.tvAssignStartTime)
    TextView tvAssignStartTime;

    @Bind(R.id.tvAssignEndTime)
    TextView tvAssignEndTime;

    @Bind(R.id.tvServiceMan)
    TextView tvServiceMan;

    @Bind(R.id.tvPurposeOfVisit)
    TextView tvPurposeOfVisit;

    @Bind(R.id.tvWorkStartedTime)
    TextView tvWorkStartedTime;

    @Bind(R.id.tvExtraTime)
    TextView tvExtraTime;

    @Bind(R.id.tvComplateWorkingTime)
    TextView tvComplateWorkingTime;

    @Bind(R.id.tvWorkPerformed)
    TextView tvWorkPerformed;

    @Bind(R.id.tvRecommendation)
    TextView tvRecommendation;

    @Bind(R.id.tvRateNote)
    TextView tvRateNote;

    @Bind(R.id.tvAddress)
    TextView tvAddress;

    @Bind(R.id.rbRate)
    RatingBar rbRate;

    @Bind(R.id.etWriteReview)
    EditText etWriteReview;

    @Bind(R.id.btnSubmit)
    Button btnSubmit;

    @Bind(R.id.ivServiceMan)
    ImageView ivServiceMan;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.rvUnits)
    RecyclerView rvUnits;

    GlobalClass globalClass;
    Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String id = "", NotificationId = null;
    private PastServiceDetailUnitsAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_service_detail,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvents();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        if (!getArguments().getString("Id", "").equalsIgnoreCase("")) {
            /**
             * Open fragment from PastServicesFragment.
             */
            id = getArguments().getString("Id", "");
        } else {
            /**
             * Open fragment from notification.
             */
            id = getArguments().getString("CommonId", "");
            NotificationId = getArguments().getString("NId");
        }

        ((DashboardActivity) activity).last = "PastServiceDetailFragment";
        if (globalClass.checkInternetConnection()) {

            PastServiceListingDetail();

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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Rate value should be more then 0
                 */
                if (rbRate.getRating() == 0) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.Rate,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                }  else if (!globalClass.checkInternetConnection()) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else {
                    serviceRating();
                }
            }
        });
    }

    /**
     * web API for submit Rate and Review of service
     */
    public void serviceRating() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.serviceRating(sharedpreferences.getString("Id", ""), id, rbRate.getRating(),
                etWriteReview.getText().toString().trim(), new Callback<RattingResponce>() {

                    @Override
                    public void success(RattingResponce rattingResponce, Response response) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();

                        if (rattingResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!rattingResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", rattingResponce.Token);
                                editor.apply();
                            }
                            Toast.makeText(activity, "Rating submitted successfully", Toast.LENGTH_LONG).show();
                            rbRate.setEnabled(false);
                            etWriteReview.setFocusable(false);
                            btnSubmit.setVisibility(View.GONE);

                        } else if (rattingResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(rattingResponce.Message,
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
                        Log.e("fali", "fail " + error.getMessage());
                    }
                });
    }

    /**
     * call web API for get data of service detail.
     */
    public void PastServiceListingDetail() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        Log.e("UserId", "Id " + sharedpreferences.getString("Id", ""));

        webserviceApi.PastServiceListingDetail(sharedpreferences.getString("Id", ""), id, NotificationId, new Callback<PastServiceDetailResponce>() {

            @Override
            public void success(PastServiceDetailResponce pastServiceDetailResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();

                if (pastServiceDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!pastServiceDetailResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", pastServiceDetailResponce.Token);
                        editor.apply();
                    }
                    Log.e("Success", "Success");
                    if (!pastServiceDetailResponce.Data.IsNoShow) {
                        llWorkDoneData.setVisibility(View.VISIBLE);
                        llWorkDoneData1.setVisibility(View.VISIBLE);
                        btnSubmit.setVisibility(View.VISIBLE);
                    } else {
                        llWorkDoneData.setVisibility(View.GONE);
                        llWorkDoneData1.setVisibility(View.GONE);
                        btnSubmit.setVisibility(View.GONE);
                    }
                    setData(pastServiceDetailResponce);

                } else if (pastServiceDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(pastServiceDetailResponce.Message,
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

    /**
     * fill data in screen
     * @param pastServiceDetailResponce Model class of data
     */
    private void setData(PastServiceDetailResponce pastServiceDetailResponce) {
        tvDate.setText(pastServiceDetailResponce.Data.ScheduleDate);
        tvSCN.setText(pastServiceDetailResponce.Data.ServiceCaseNumber);
        tvServiceMan.setText(pastServiceDetailResponce.Data.FirstName +" "+pastServiceDetailResponce.Data.LastName);
        tvPurposeOfVisit.setText(pastServiceDetailResponce.Data.PurposeOfVisit);
        tvRecommendation.setText(pastServiceDetailResponce.Data.Recommendations);
        tvAddress.setText(pastServiceDetailResponce.Data.Address.Address + "\n" + pastServiceDetailResponce.Data.Address.StateName + ", " +
                pastServiceDetailResponce.Data.Address.CityName+" "+pastServiceDetailResponce.Data.Address.ZipCode);
        if (!pastServiceDetailResponce.Data.EmpProfileImage.equalsIgnoreCase("")) {
            Picasso.with(getActivity())
                    .load(pastServiceDetailResponce.Data.EmpProfileImage)
                    .resize(150, 150)
                    .transform(new RoundedTransformation(1700, 0))
                    .placeholder(R.drawable.placeholder_img)
                    .into(ivServiceMan);
        }

        if (pastServiceDetailResponce.Data.Units != null) {
            adapter = new PastServiceDetailUnitsAdapter(activity, pastServiceDetailResponce.Data.Units);
            LinearLayoutManager llm = new LinearLayoutManager(activity);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rvUnits.setLayoutManager(llm);
            rvUnits.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        if (!pastServiceDetailResponce.Data.IsNoShow) {
            tvAssignStartTime.setText(pastServiceDetailResponce.Data.ScheduleStartTime);
            tvAssignEndTime.setText(pastServiceDetailResponce.Data.ScheduleEndTime);
            tvAssignTotalTime.setText(pastServiceDetailResponce.Data.AssignedTotalTime);
            tvWorkPerformed.setText(pastServiceDetailResponce.Data.WorkPerformed);

            if (pastServiceDetailResponce.Data.IsDifferentTime) {
                llDifferentTimes.setVisibility(View.VISIBLE);
                tvWorkStartedTime.setText(pastServiceDetailResponce.Data.WorkStartedTime);
                tvComplateWorkingTime.setText(pastServiceDetailResponce.Data.WorkCompletedTime);
            } else {
                llDifferentTimes.setVisibility(View.GONE);
            }


            if (pastServiceDetailResponce.Data.Rate > 0) {
                rbRate.setRating(pastServiceDetailResponce.Data.Rate);
                etWriteReview.setText(pastServiceDetailResponce.Data.Review);
                rbRate.setEnabled(false);
                etWriteReview.setFocusable(false);
                btnSubmit.setVisibility(View.GONE);
                tvRateNote.setVisibility(View.GONE);
            } else {
                tvRateNote.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;

    }
}
