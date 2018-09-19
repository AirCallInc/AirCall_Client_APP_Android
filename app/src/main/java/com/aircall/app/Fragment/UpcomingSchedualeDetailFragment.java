package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Adapter.ServiceDetailUnitsAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.RoundedTransformation;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.DoubleButtonAlert;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.PastServices.RattingResponce;
import com.aircall.app.Model.RequestForServices.UnitsData;
import com.aircall.app.Model.UpcomingSchedual.CancelRequestedServiceResponse;
import com.aircall.app.Model.UpcomingSchedual.NotificationWaitingServicesResponce;
import com.aircall.app.Model.UpcomingSchedual.UpcomingServicesDetailData;
import com.aircall.app.Model.UpcomingSchedual.UpcomingServicesResponce;
import com.aircall.app.R;
import com.aircall.app.RequestForReschedulectivity;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UpcomingSchedualeDetailFragment extends Fragment {

    @Bind(R.id.ivTechnicianImage)
    ImageView ivTechnicianImage;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.tvTechnicianName)
    TextView tvTechnicianName;

    @Bind(R.id.tvServiceCaseNo)
    TextView tvServiceCaseNo;

    @Bind(R.id.tvSchedualDate)
    TextView tvSchedualDate;

    @Bind(R.id.tvSchedualTime)
    TextView tvSchedualTime;

    @Bind(R.id.tvSchedualComplaint)
    TextView tvSchedualComplaint;

    @Bind(R.id.tvAccept)
    TextView tvAccept;

    @Bind(R.id.tvReject)
    TextView tvReject;

    @Bind(R.id.tvReschedule)
    TextView tvReschedule;

    @Bind(R.id.tvAddress)
    TextView tvAddress;

    @Bind(R.id.llComplain)
    LinearLayout llComplain;

    @Bind(R.id.llNotificationButtons)
    LinearLayout llNotificationButtons;

    @Bind(R.id.rvScheduleDetailsUnitService)
    RecyclerView rvScheduleDetailsUnitService;

    @Bind(R.id.rl_toolbar)
    RelativeLayout rl_toolbar;

    GlobalClass globalClass;
    Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private ServiceDetailUnitsAdapter unitAdapter;
    private UpcomingServicesDetailData serviceDetail = new UpcomingServicesDetailData();
    private String CommonId, NId, NType, PurpusOfVisit = "";
    private String LateCancelDisplayMessage = "";
    private Boolean IsLateReschedule = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_schedule_detail,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvent();

        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        rvScheduleDetailsUnitService.setNestedScrollingEnabled(false);
        rvScheduleDetailsUnitService.setHasFixedSize(false);
        ((DashboardActivity) activity).last = "upcoming_schedule_detail";

        if (getArguments().getString("ServiceId") != null) {
            CommonId = getArguments().getString("ServiceId");
            Log.e("ServiceId==", CommonId);
        } else {
            NType = getArguments().getString("NType");
            NId = getArguments().getString("NId");
            CommonId = getArguments().getString("CommonId");

            Log.e("NID ==", NId);
            Log.e("CommonId ==", CommonId);
        }

        if (globalClass.checkInternetConnection()) {
            WaitingServiceDetail();
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

    private void clickEvent() {
        rl_toolbar.setOnClickListener(new View.OnClickListener() {
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
         * If fragment from Upcoming schedule listing then Only Reschedule button will be display and user can
         * reschedule service before 24 hr of it's time.
         */

        tvReschedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    Calendar c = Calendar.getInstance();
                    System.out.println("Current time => " + c.getTime());

                    SimpleDateFormat df = new SimpleDateFormat("MMMM dd yyyy");
                    String currentDate = df.format(c.getTime());

                    //if (currentDate.equalsIgnoreCase(serviceDetail.ScheduleDate)) {
                    if (serviceDetail.Is24HourLeft) {

                        DialogFragment ds = new DoubleButtonAlert(serviceDetail.LateRescheduleDisplayMessage,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String message) {
                                        if (globalClass.checkInternetConnection()) {
                                            if (message.equalsIgnoreCase("yes")) {

                                                serviceRecheduleRequest();

                                            } else {

                                            }
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
                        ds.setCancelable(false);
                        ds.show(getFragmentManager(), "");

                    } else {
                        Intent intent = new Intent(activity, RequestForReschedulectivity.class);
                        intent.putExtra("ServiceId", serviceDetail.Id);
                        intent.putExtra("FromWhere", "upcoming");
                        intent.putExtra("TotalUnit", serviceDetail.Units.size());
                        intent.putExtra("IsCancelled", false);
                        intent.putExtra("PurpusOfVisit", PurpusOfVisit);
                        intent.putExtra("ServiceRequestedOn", changeDateFormate(serviceDetail.ScheduleDate));
                        intent.putExtra("ServiceRequestedTime", serviceDetail.ServiceRequestedTime);
                        intent.putExtra("Is24HourLeft", serviceDetail.Is24HourLeft);
                        if (NId != null) {
                            intent.putExtra("NId", NId);
                        }
                        startActivity(intent);
                    }
                } catch (Exception e) {
                }
            }
        });

        /**
         * From notification of service schedule conformation user can accept or reject it.
         */

        tvAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalClass.checkInternetConnection()) {
                    ApproveWaitingService();
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

        tvReject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Added
                if (serviceDetail.Is24HourLeft) {
                    DialogFragment ds = new DoubleButtonAlert(LateCancelDisplayMessage, new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String message) {
                            if (message.trim().endsWith("yes")) {
                                //CALL Cancel API
                                IsLateReschedule = true;
                                serviceCancelRequest();
                            }
                        }
                    });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else {
                    IsLateReschedule = false;
                    serviceCancelRequest();
                }
            }
        });
    }

    private String changeDateFormate(String dateString) {
        String inputPattern = "MMM dd, yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(dateString);
            str = outputFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    private void setData() {
        if (!serviceDetail.EmpProfileImage.equalsIgnoreCase("")) {
            Picasso.with(getActivity()).load(serviceDetail.EmpProfileImage)
                    .resize(400, 400)
                    .transform(new RoundedTransformation(1700, 0))
                    .placeholder(R.drawable.placeholder_img)
                    .into(ivTechnicianImage);
        }
        tvTechnicianName.setText(serviceDetail.EMPFirstName + " " + serviceDetail.EMPLastName);
        tvServiceCaseNo.setText(" " + serviceDetail.ServiceCaseNumber);
        tvSchedualDate.setText(serviceDetail.ScheduleDate);
        tvSchedualTime.setText(serviceDetail.ScheduleStartTime + " - " + serviceDetail.ScheduleEndTime);
        tvAddress.setText(serviceDetail.Address.Address + "\n" + serviceDetail.Address.StateName + ", " +
                serviceDetail.Address.CityName + " " + serviceDetail.Address.ZipCode);
        if (!serviceDetail.CustomerComplaints.equalsIgnoreCase("")) {
            llComplain.setVisibility(View.VISIBLE);
            tvSchedualComplaint.setText(serviceDetail.CustomerComplaints);
        } else {
            llComplain.setVisibility(View.GONE);
        }
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvScheduleDetailsUnitService.setLayoutManager(llm);
        ArrayList<UnitsData> Units = new ArrayList<>();
        for (int i = 0; i < serviceDetail.Units.size(); i++) {
            UnitsData unit = new UnitsData();
            unit.Id = serviceDetail.Units.get(i).Id;
            unit.UnitName = serviceDetail.Units.get(i).UnitName;
            Units.add(unit);
        }
        unitAdapter = new ServiceDetailUnitsAdapter(activity, Units);
        rvScheduleDetailsUnitService.setAdapter(unitAdapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;
    }

    /**
     * Get data of service using webAPI and fill data in screen.
     */
    public void WaitingServiceDetail() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.WaitingServiceDetail(sharedpreferences.getString("Id", ""), CommonId, NId, new Callback<NotificationWaitingServicesResponce>() {

            @Override
            public void success(NotificationWaitingServicesResponce notificationWaitingServicesResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                if (notificationWaitingServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!notificationWaitingServicesResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", notificationWaitingServicesResponce.Token);
                        editor.apply();
                    }
                    serviceDetail = notificationWaitingServicesResponce.Data;
                    Log.e("Id ==", serviceDetail.Id);
                    Log.e("Shared Pref Id=", sharedpreferences.getString("Id", ""));

                    PurpusOfVisit = serviceDetail.PurposeOfVisit;
                    LateCancelDisplayMessage = serviceDetail.LateCancelDisplayMessage;
                    setData();

                    Log.e("IsRequested==", serviceDetail.IsRequested + "");
                    if (NType != null) {
                        Log.e("NType ==", NType);
                    }

                    //Added Here we are checking for notification type and according to that show the buttons.
                    if (getArguments().getString("ServiceId") == null) { //globalClass.NTYPE_SCHEDULE == 1 == ServiceApproval
                        if (NType != null && serviceDetail.IsRequested && NType.equalsIgnoreCase(globalClass.NTYPE_SCHEDULE)) {
//                            llNotificationButtons.setVisibility(View.VISIBLE);
                            tvAccept.setVisibility(View.VISIBLE);
                            tvReject.setVisibility(View.VISIBLE);
                            tvReschedule.setVisibility(View.VISIBLE);
                        } else if (NType != null && serviceDetail.IsRequested && !NType.equalsIgnoreCase(globalClass.NTYPE_SCHEDULE)) {
                            tvAccept.setVisibility(View.GONE);
                            tvReject.setVisibility(View.VISIBLE);
                            tvReschedule.setVisibility(View.VISIBLE);
                        } else if (NType != null && !serviceDetail.IsRequested && NType.equalsIgnoreCase(globalClass.NTYPE_SCHEDULE)) {
                            tvAccept.setVisibility(View.VISIBLE);
                            tvReject.setVisibility(View.GONE);
                            tvReschedule.setVisibility(View.VISIBLE);
                        } else {
//                            llNotificationButtons.setVisibility(View.GONE);
                            tvAccept.setVisibility(View.GONE);
                            tvReject.setVisibility(View.GONE);
                            tvReschedule.setVisibility(View.VISIBLE);
                        }
                    } else if (getArguments().getString("ServiceId") != null && serviceDetail.IsRequested) {
                        //When Service is Requested and it is listed in upcoming schedule listing
                        tvAccept.setVisibility(View.GONE);
                        tvReject.setVisibility(View.VISIBLE);
                        tvReschedule.setVisibility(View.VISIBLE);
                    } else {
                        //This Come When Service created for Maintenance and we run requested scheduler and accept from notification now it is in the upcoming schedule listing.
                        tvAccept.setVisibility(View.GONE);
                        tvReject.setVisibility(View.GONE);
                        tvReschedule.setVisibility(View.VISIBLE);
                    }

                } else if (notificationWaitingServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(notificationWaitingServicesResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    activity.onBackPressed();
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
                                activity.onBackPressed();
                            }
                        });
                ds.setCancelable(true);
                ds.show(getFragmentManager(), "");
                Log.e("Fail WtingServiceDetail", "Error " + error.getMessage());
            }
        });
    }


    /**
     * WebAPI calls when user will approve for schedule notification.
     */
    public void ApproveWaitingService() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.ApproveWaitingService(sharedpreferences.getString("Id", ""), NId, CommonId, new Callback<RattingResponce>() {

            @Override
            public void success(RattingResponce rattingResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                if (rattingResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!rattingResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", rattingResponce.Token);
                        editor.apply();
                    }
                    Toast.makeText(activity, ErrorMessages.ACCEPT, Toast.LENGTH_LONG).show();
                    activity.onBackPressed();

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
                    ((DashboardActivity) getActivity()).hideProgressDialog();
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
                Log.e("Fail WtngServiceDetail", "Error " + error.getMessage());
            }
        });

    }

    public void serviceCancelRequest() {
        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.serviceCancelRequest(sharedpreferences.getString("Id", ""), serviceDetail.Id, null, null,
                null, null, null, IsLateReschedule, new Callback<CancelRequestedServiceResponse>() {
                    @Override
                    public void success(CancelRequestedServiceResponse cancelRequestedServiceResponse, Response response) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                        if (cancelRequestedServiceResponse.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!cancelRequestedServiceResponse.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", cancelRequestedServiceResponse.Token);
                                editor.apply();
                            }

                            Toast.makeText(activity, cancelRequestedServiceResponse.Message, Toast.LENGTH_LONG).show();
                            activity.onBackPressed();
                        } else if (cancelRequestedServiceResponse.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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

                            DialogFragment ds = new SingleButtonAlert(cancelRequestedServiceResponse.Message,
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

                    }
                });

    }


    /**
     * When user try to reschedule service before time of 24hr of schedule it will set as NoShow service
     */
    public void serviceRecheduleRequest() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.serviceRecheduleRequest(sharedpreferences.getString("Id", ""), serviceDetail.Id, null, null,
                null, null, null, true, new Callback<UpcomingServicesResponce>() {
                    @Override
                    public void success(UpcomingServicesResponce upcomingServicesResponce, Response response) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                        if (upcomingServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!upcomingServicesResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", upcomingServicesResponce.Token);
                                editor.apply();
                            }

                            Toast.makeText(activity, upcomingServicesResponce.Message, Toast.LENGTH_LONG).show();
                            activity.onBackPressed();

                        } else if (upcomingServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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

                            DialogFragment ds = new SingleButtonAlert(upcomingServicesResponce.Message,
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DashboardActivity) activity).last = "upcoming_schedule_detail";
    }
}
