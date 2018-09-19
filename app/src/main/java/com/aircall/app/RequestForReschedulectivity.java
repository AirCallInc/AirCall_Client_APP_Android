package com.aircall.app;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.RequestForServices.GetPurposeOfVisitTimeResponce;
import com.aircall.app.Model.RequestForServices.RequestForServiceListResponce;
import com.aircall.app.Model.UpcomingSchedual.UpcomingServicesResponce;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestForReschedulectivity extends Activity implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    @Bind(R.id.etReschedulePreferredDay)
    EditText etReschedulePreferredDay;

    @Bind(R.id.etReason)
    EditText etReason;

    @Bind(R.id.tvTimeFirst)
    TextView tvTimeFirst;

    @Bind(R.id.tvTimeSecond)
    TextView tvTimeSecond;

    @Bind(R.id.tvSubmit)
    TextView tvSubmit;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    public GlobalClass globalClass;
    private SharedPreferences sharedpreferences;
    private ProgressDialogFragment progressDialogFragment;
    private SharedPreferences.Editor editor;
    private String ServiceId = "", NId = null;
    private String RequestedTime = "";
    private String FromWhere = "", type = "";
    private Boolean Is24HourLeft;
    private int MaintenanceServicesWithinDays, EmergencyAndOtherServiceWithinDays;
    private Boolean IsCancelled, isAllowSecondSlot;
    private int totalSelectedUnits, TotalUnitSlot2, TotalUnitSlot1;
    private String EmergencyServiceSlot1 = "", EmergencyServiceSlot2 = "", TimeSlot1 = "", TimeSlot2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_for_reschedulectivity);
        ButterKnife.bind(this);
        init();
        clickEvent();
    }

    private void init() {
        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        Intent intent = getIntent();
        ServiceId = intent.getStringExtra("ServiceId");
        totalSelectedUnits = intent.getIntExtra("TotalUnit", 0);
        FromWhere = intent.getStringExtra("FromWhere");
        RequestedTime = intent.getStringExtra("ServiceRequestedTime");
        IsCancelled = intent.getBooleanExtra("IsCancelled", false);
        Is24HourLeft = intent.getBooleanExtra("Is24HourLeft", false);
        NId = intent.getStringExtra("NId");
        type = intent.getStringExtra("PurpusOfVisit");
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date testDate = null;
        try {
            testDate = sdf.parse(intent.getStringExtra("ServiceRequestedOn"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        etReschedulePreferredDay.setText(df.format(testDate));

        //etReschedulePreferredDay.setText(intent.getStringExtra("ServiceRequestedOn"));

        /**
         * Get 2 time slot and detail about plan slot.
         */
        if (globalClass.checkInternetConnection()) {

            if (FromWhere.equalsIgnoreCase("upcoming")) {
                /**
                 * Send Service id if activity start from UpcomingScheduleDetail Fragment
                 */
                getScheduleTimeByPlanTypeServiceId(ServiceId, null);
            } else if (FromWhere.equalsIgnoreCase("RequestedService")) {
                /**
                 * Send Request Service id if activity start from Request for service listing
                 */
                getScheduleTimeByPlanTypeServiceId(null, ServiceId);
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

    private void clickEvent() {
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        etReschedulePreferredDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                if (type.equalsIgnoreCase("3")) {
                    now.add(Calendar.DATE, MaintenanceServicesWithinDays);
                } else if (type.equalsIgnoreCase("1")) {
                } else {
                    now.add(Calendar.DATE, EmergencyAndOtherServiceWithinDays);
                }
                String dateLen = etReschedulePreferredDay.getText().toString().trim();
                String[] date = dateLen.split("/");
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        RequestForReschedulectivity.this,
                        Integer.parseInt(date[2]),
                        (Integer.parseInt(date[0]) - 1),
                        Integer.parseInt(date[1])
                );
                dpd.setMinDate(now);
                dpd.setAccentColor(Color.parseColor("#164F86"));
                dpd.show(getFragmentManager(), "Datepickerdialog");
            }
        });

        tvTimeFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTimeSlot(true);
            }
        });

        tvTimeSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAllowSecondSlot) {
                    setTimeSlot(false);
                } else {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.MoreThen4Hrs,
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

        tvSubmit.setOnClickListener(new View.OnClickListener() {
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

                } else if (etReason.getText().toString().trim().equalsIgnoreCase("")) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.Reason,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                }else {
                    submitRequest();
                }
            }
        });
    }

    private void setTimeSlot(Boolean isFirst) {
        if (isFirst) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                tvTimeFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                tvTimeFirst.setTextColor(getResources().getColor(R.color.white));

                tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color));

            } else {
                tvTimeFirst.setBackground(getResources().getDrawable(R.drawable.button, null));
                tvTimeFirst.setTextColor(getResources().getColor(R.color.white, null));

                tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color, null));
            }
            RequestedTime = tvTimeFirst.getText().toString();
        } else {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                tvTimeSecond.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.white));

                tvTimeFirst.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeFirst.setTextColor(getResources().getColor(R.color.gray_text_color));

            } else {
                tvTimeSecond.setBackground(getResources().getDrawable(R.drawable.button, null));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.white, null));

                tvTimeFirst.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeFirst.setTextColor(getResources().getColor(R.color.gray_text_color, null));
            }
            RequestedTime = tvTimeSecond.getText().toString();
        }
    }

    private void setInitialDate() {
        Calendar c = Calendar.getInstance();
        if (type.equalsIgnoreCase("Maintenance Services")) {
            c.add(Calendar.DATE, MaintenanceServicesWithinDays);
        } else {
            c.add(Calendar.DATE, EmergencyAndOtherServiceWithinDays);
        }
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
            c.add(Calendar.DATE, 2);
        } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            c.add(Calendar.DATE, 1);
        }
        etReschedulePreferredDay.setText(df.format(c.getTime()));
    }

    private void submitRequest() {
        if (FromWhere.equalsIgnoreCase("upcoming")) {
            serviceRecheduleRequest();
        } else if (FromWhere.equalsIgnoreCase("RequestedService")) {
            editRequestForService();
        }
    }

    /**
     * Get plan detail with webAPI
     *
     * @param ServiceId
     * @param RequestedServiceId
     */
    public void getScheduleTimeByPlanTypeServiceId(String ServiceId, String RequestedServiceId) {
        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getScheduleTimeByPlanTypeServiceId(null, ServiceId, RequestedServiceId, new Callback<GetPurposeOfVisitTimeResponce>() {
            @Override
            public void success(GetPurposeOfVisitTimeResponce getPurposeOfVisitTimeResponce, Response response) {
                hideProgressDialog();
                if (getPurposeOfVisitTimeResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {

                    if (!getPurposeOfVisitTimeResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getPurposeOfVisitTimeResponce.Token);
                        editor.apply();
                    }
                    TimeSlot1 = getPurposeOfVisitTimeResponce.Data.TimeSlot1;
                    TimeSlot2 = getPurposeOfVisitTimeResponce.Data.TimeSlot2;
                    EmergencyServiceSlot1 = getPurposeOfVisitTimeResponce.Data.EmergencyServiceSlot1;
                    EmergencyServiceSlot2 = getPurposeOfVisitTimeResponce.Data.EmergencyServiceSlot2;

                    tvTimeFirst.setText(getPurposeOfVisitTimeResponce.Data.TimeSlot1);
                    tvTimeSecond.setText(getPurposeOfVisitTimeResponce.Data.TimeSlot2);
                    TotalUnitSlot2 = getPurposeOfVisitTimeResponce.Data.TotalUnitSlot2;
                    TotalUnitSlot1 = getPurposeOfVisitTimeResponce.Data.TotalUnitSlot1;
                    MaintenanceServicesWithinDays = getPurposeOfVisitTimeResponce.Data.MaintenanceServicesWithinDays;
                    EmergencyAndOtherServiceWithinDays = getPurposeOfVisitTimeResponce.Data.EmergencyAndOtherServiceWithinDays;
                    //setInitialDate();

                    if (type.equalsIgnoreCase("1")) {
                        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                        Date newDate = null;
                        try {
                            newDate = formatter.parse(etReschedulePreferredDay.getText().toString());
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        if (!isDateValied(toCalendar(newDate))) {
                            tvTimeFirst.setText(EmergencyServiceSlot1);
                            tvTimeSecond.setText(EmergencyServiceSlot2);
                        }
                    }

                    if (RequestedTime.equalsIgnoreCase("")) {
                        setTimeSlot(true);
                    } else if (RequestedTime.equalsIgnoreCase(getPurposeOfVisitTimeResponce.Data.TimeSlot1)) {
                        setTimeSlot(true);
                    } else if (RequestedTime.equalsIgnoreCase(getPurposeOfVisitTimeResponce.Data.TimeSlot2)) {
                        setTimeSlot(false);
                    } else {
                        setTimeSlot(true);
                    }

                    disableSelectedTimeSlotIfNeeded();

                } else if (getPurposeOfVisitTimeResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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

                    DialogFragment ds = new SingleButtonAlert(getPurposeOfVisitTimeResponce.Message,
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

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Disable second time slot to select if units service can not perform in second time
     */
    private void disableSelectedTimeSlotIfNeeded() {
        if (TotalUnitSlot2 < totalSelectedUnits) {
            setTimeSlot(true);
            isAllowSecondSlot = false;
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.summary_add_another_unit));

            } else {
                //tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.summary_add_another_unit, null));
            }
        } else {
            isAllowSecondSlot = true;
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color));

            } else {
                //tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color, null));
            }
        }
    }

    /**
     * WebAPI call for edit requested service
     */
    public void editRequestForService() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.editRequestForService(sharedpreferences.getString("Id", ""), ServiceId, RequestedTime,
                etReschedulePreferredDay.getText().toString(), etReason.getText().toString(), new Callback<RequestForServiceListResponce>() {
                    @Override
                    public void success(RequestForServiceListResponce requestForServiceListResponce, Response response) {
                        hideProgressDialog();
                        if (requestForServiceListResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!requestForServiceListResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", requestForServiceListResponce.Token);
                                editor.apply();
                            }
                            Toast.makeText(RequestForReschedulectivity.this, "Service rescheduled successfully.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent();
                            Gson gson = new Gson();
                            String json = gson.toJson(requestForServiceListResponce);
                            intent.putExtra("data", json);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else if (requestForServiceListResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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

                            DialogFragment ds = new SingleButtonAlert(requestForServiceListResponce.Message,
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
     * WebAPI call for reschedule requested service
     */
    public void serviceRecheduleRequest() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.serviceRecheduleRequest(sharedpreferences.getString("Id", ""), ServiceId, NId, RequestedTime,
                etReschedulePreferredDay.getText().toString(), etReason.getText().toString(), IsCancelled,
                Is24HourLeft, new Callback<UpcomingServicesResponce>() {
                    @Override
                    public void success(UpcomingServicesResponce upcomingServicesResponce, Response response) {
                        hideProgressDialog();
                        if (upcomingServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!upcomingServicesResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", upcomingServicesResponce.Token);
                                editor.apply();
                            }
                            Toast.makeText(RequestForReschedulectivity.this, "Service rescheduled successfully.", Toast.LENGTH_LONG).show();

                            Intent intent = new Intent(RequestForReschedulectivity.this, DashboardActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            intent.putExtra("dash", "upcoming");
                            startActivity(intent);

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
                        hideProgressDialog();
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
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);


        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String newFormat = formatter.format(testDate);

        etReschedulePreferredDay.setText(newFormat);

        if (type.equalsIgnoreCase("1")) {
            Date newDate = null;
            try {
                newDate = formatter.parse(etReschedulePreferredDay.getText().toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (isDateValied(toCalendar(newDate))) {
                tvTimeFirst.setText(TimeSlot1);
                tvTimeSecond.setText(TimeSlot2);
            } else {
                tvTimeFirst.setText(EmergencyServiceSlot1);
                tvTimeSecond.setText(EmergencyServiceSlot2);
            }
        }

    }

    /**
     * Check if selected date is not weekend
     *
     * @param c Calender object with selected date
     * @return
     */
    private boolean isDateValied(Calendar c) {
        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }
}
