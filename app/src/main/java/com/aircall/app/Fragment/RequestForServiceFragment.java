package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Adapter.AddUnitAddressListAdapter;
import com.aircall.app.Adapter.PlanTypeAdapter;
import com.aircall.app.Adapter.ServiceReasionAdapter;
import com.aircall.app.Adapter.UnitsSelectionAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.CustomTimePickerDialog;
import com.aircall.app.Dialog.DoubleButtonAlert;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Dialog.TwoButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.GetAddress.AddressDetailForList;
import com.aircall.app.Model.GetAddress.getAddressResponce;
import com.aircall.app.Model.GetAllPlanType.GetPlanTypesResponce;
import com.aircall.app.Model.GetAllPlanType.UnitTypeDetailForList;
import com.aircall.app.Model.MyUnits.MyUnitsData;
import com.aircall.app.Model.MyUnits.MyUnitsResponce;
import com.aircall.app.Model.RequestForServices.GetPurposeOfVisitTimeResponce;
import com.aircall.app.Model.RequestForServices.ReasionList;
import com.aircall.app.Model.RequestForServices.RequestServicesResponce;
import com.aircall.app.Model.RequestForServices.ResedualRequest;
import com.aircall.app.R;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class RequestForServiceFragment extends Fragment implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    @Bind(R.id.ivNavDrawer)
    ImageView ivNavDrawer;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.ivRequestList)
    ImageView ivRequestList;

    @Bind(R.id.ivSelectDate)
    ImageView ivSelectDate;

    @Bind(R.id.etAddNote)
    EditText etAddNote;

    @Bind(R.id.tvDate)
    TextView tvDate;

    @Bind(R.id.tvTimeFirst)
    TextView tvTimeFirst;

    @Bind(R.id.tvTimeSecond)
    TextView tvTimeSecond;

    @Bind(R.id.txtUnitNoData)
    TextView txtUnitNoData;

    @Bind(R.id.txtAddressNoData)
    TextView txtAddressNoData;

    @Bind(R.id.tvSubmit)
    TextView tvSubmit;

    @Bind(R.id.txtPlanTypeNoData)
    TextView txtPlanTypeNoData;

    @Bind(R.id.rvSelectUnit)
    RecyclerView rvSelectUnit;

    @Bind(R.id.rvSelectReason)
    RecyclerView rvSelectReason;

    @Bind(R.id.rvAddressList)
    RecyclerView rvAddressList;

    @Bind(R.id.rvSelectPlanType)
    RecyclerView rvSelectPlanType;

    @Bind(R.id.ll_timeslots)
    LinearLayout ll_timeslots;

//    @Bind(R.id.ll_select_Emergencytime)
//    LinearLayout ll_select_Emergencytime;

//    @Bind(R.id.tvEmergencyTime)
//    TextView tvEmergencyTime;

    private GlobalClass globalClass;
    private Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String selectedTimeSlote = "", PlanTypeId;
    private int MaintenanceServicesWithinDays, EmergencyAndOtherServiceWithinDays;
    private ResedualRequest resedualRequest = new ResedualRequest();
    private int timeSlot1Units, timeSlot2Units, totalSelectedUnits = 0;

    /**
     * Address Listing
     */
    private ArrayList<AddressDetailForList> addressDetail = new ArrayList<>();
    private AddUnitAddressListAdapter addUnitAddressListAdapter;

    /**
     * Unit Listing
     */
    private ArrayList<MyUnitsData> unitsDatas = new ArrayList<>();
    private UnitsSelectionAdapter adapter;

    /**
     * Reason Listing
     */
    private ArrayList<ReasionList> reasionDatas = new ArrayList<>();
    private ServiceReasionAdapter reasionAdapter;

    /**
     * Types of unit/plan
     */
    private ArrayList<UnitTypeDetailForList> unitTypeDetail = new ArrayList<>();
    private PlanTypeAdapter planTypeAdapter;
    int hour, minute;
    CustomTimePickerDialog dialog;
    private String EmergencyServiceSlot1 = "", EmergencyServiceSlot2 = "", TimeSlot1 = "", TimeSlot2 = "";
    private Calendar setPreviousDate;
    private ArrayList<String> alertMsgList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_request_for_service,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvents();
        return view;
    }

    private void init() {

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);

        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        addUnitAddressListAdapter = new AddUnitAddressListAdapter(activity, addressDetail);
        LinearLayoutManager llm1 = new LinearLayoutManager(activity);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddressList.setLayoutManager(llm1);

        rvAddressList.setAdapter(addUnitAddressListAdapter);
        rvAddressList.setNestedScrollingEnabled(false);

        rvSelectUnit.setNestedScrollingEnabled(false);
        rvSelectUnit.setHasFixedSize(false);

        rvSelectReason.setNestedScrollingEnabled(false);
        rvSelectReason.setHasFixedSize(false);

        rvSelectPlanType.setNestedScrollingEnabled(false);
        rvSelectPlanType.setHasFixedSize(false);

        if (globalClass.checkInternetConnection()) {
            getAddressList();
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
        resedualRequest.ClientId = Integer.parseInt(sharedpreferences.getString("Id", ""));
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
         * To redirect to request listing screen.
         */
        ivRequestList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = getFragmentManager()
                        .beginTransaction();
                Fragment newFragment = new ListOfRequestFragment();
                String strFragmentTag = newFragment.toString();
                transaction.add(R.id.frame_middel, newFragment,
                        strFragmentTag);
                transaction.addToBackStack("");
                transaction.commit();
            }
        });


        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                submitService();
            }
        });

        /**
         * Select preferred Date for reschedule
         */
        ivSelectDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                if (resedualRequest.PurposeOfVisit.equalsIgnoreCase("3")) {
                    now.add(Calendar.DATE, MaintenanceServicesWithinDays);
                } else if (resedualRequest.PurposeOfVisit.equalsIgnoreCase("1")) {
                } else {
                    now.add(Calendar.DATE, EmergencyAndOtherServiceWithinDays);
                }
                if (!resedualRequest.PurposeOfVisit.trim().equalsIgnoreCase("1")) {
                    if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                        now.add(Calendar.DATE, 2);
                    } else if (now.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                        now.add(Calendar.DATE, 1);
                    }
                }

                String dateLen = tvDate.getText().toString().trim();
                String[] date = dateLen.split("/");
                DatePickerDialog dpd = DatePickerDialog.newInstance(
                        RequestForServiceFragment.this,
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
                selectTimeSlot(true);
            }
        });

        tvTimeSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalSelectedUnits > timeSlot2Units) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.MoreThen4Hrs,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                } else {
                    selectTimeSlot(false);
                }
            }
        });

        /**
         * Select units from list, if unit size will be more then capacity of second slot then we have to disable second slot.
         */
        rvSelectUnit.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        if (unitsDatas.get(position).isSelected) {
                            unitsDatas.get(position).isSelected = false;
                            totalSelectedUnits--;
                            if (totalSelectedUnits == timeSlot2Units) {
                                EnableDisableTimeSlot2(true);
                            }
                        } else {
                            if (totalSelectedUnits == timeSlot2Units) {
                                DialogFragment ds = new SingleButtonAlert(ErrorMessages.MoreThen4Hrs,
                                        new DialogInterfaceClick() {
                                            @Override
                                            public void dialogClick(String tag) {
                                            }
                                        });
                                ds.setCancelable(true);
                                ds.show(getFragmentManager(), "");
                                selectTimeSlot(true);
                                EnableDisableTimeSlot2(false);
                            }
                            totalSelectedUnits++;
                            unitsDatas.get(position).isSelected = true;
                        }
                        adapter.notifyDataSetChanged();
                    }
                })
        );

        rvSelectReason.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        for (int i = 0; i < reasionDatas.size(); i++) {
                            if (i == position) {
                                reasionDatas.get(i).isSelected = true;
                                resedualRequest.PurposeOfVisit = reasionDatas.get(i).Id;

                            } else {
                                reasionDatas.get(i).isSelected = false;
                            }
                            reasionAdapter.notifyDataSetChanged();
                        }
                        if (resedualRequest.PurposeOfVisit.trim().equalsIgnoreCase("1")) {

                            final Calendar c = Calendar.getInstance();
                            hour = c.get(Calendar.HOUR_OF_DAY);
                            minute = c.get(Calendar.MINUTE);
                            minute = (Math.round(minute / 15) * 15) % 60;
                            setInitialDate("emer");
                            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
                            Date newDate = null;
                            try {
                                newDate = formatter.parse(tvDate.getText().toString());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            if (!isDateValied(toCalendar(newDate))) {
                                tvTimeFirst.setText(EmergencyServiceSlot1);
                                tvTimeSecond.setText(EmergencyServiceSlot2);
                            }

                        }
                        else if (resedualRequest.PurposeOfVisit.equalsIgnoreCase("3")) {
                            setInitialDate("rep");
                            tvTimeFirst.setText(TimeSlot1);
                            tvTimeSecond.setText(TimeSlot2);
                        } else {
                            setInitialDate("eme");
                            tvTimeFirst.setText(TimeSlot1);
                            tvTimeSecond.setText(TimeSlot2);
                        }
                    }
                })
        );

        rvAddressList.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        for (int i = 0; i < addressDetail.size(); i++) {
                            if (i == position) {
                                addressDetail.get(i).IsSelected = true;
                                resedualRequest.AddressId = Integer.parseInt(addressDetail.get(i).Id);
                            } else {
                                addressDetail.get(i).IsSelected = false;
                            }
                        }
                        totalSelectedUnits = 0;
                        selectTimeSlot(true);
                        EnableDisableTimeSlot2(true);
                        addUnitAddressListAdapter.notifyDataSetChanged();
                        getAllPlanTypeFromAddressID("" + resedualRequest.AddressId);

                    }
                })
        );

        rvSelectPlanType.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        for (int i = 0; i < unitTypeDetail.size(); i++) {
                            if (i == position) {
                                PlanTypeId = unitTypeDetail.get(i).Id;
                                unitTypeDetail.get(i).isSelected = true;
                            } else {
                                unitTypeDetail.get(i).isSelected = false;
                            }
                        }
                        totalSelectedUnits = 0;
                        selectTimeSlot(true);
                        EnableDisableTimeSlot2(true);
                        getScheduleTimeByPlanTypeServiceId(PlanTypeId);
                        planTypeAdapter.notifyDataSetChanged();
                    }
                })
        );
    }


    private void selectTimeSlot(Boolean isFirst) {
        if (isFirst) {
            selectedTimeSlote = tvTimeFirst.getText().toString();
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                tvTimeFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                tvTimeFirst.setTextColor(getResources().getColor(R.color.white));
                tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.upcoming_raw_footer_back));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color));

            } else {
                tvTimeFirst.setBackground(getResources().getDrawable(R.drawable.button, null));
                tvTimeFirst.setTextColor(getResources().getColor(R.color.white, null));

                tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.upcoming_raw_footer_back));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color, null));
            }
        } else {
            selectedTimeSlote = tvTimeSecond.getText().toString();
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                tvTimeSecond.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.white));

                tvTimeFirst.setBackgroundColor(getResources().getColor(R.color.upcoming_raw_footer_back));
                tvTimeFirst.setTextColor(getResources().getColor(R.color.gray_text_color));

            } else {
                tvTimeSecond.setBackground(getResources().getDrawable(R.drawable.button, null));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.white, null));

                tvTimeFirst.setBackgroundColor(getResources().getColor(R.color.upcoming_raw_footer_back));
                tvTimeFirst.setTextColor(getResources().getColor(R.color.gray_text_color, null));
            }
        }
    }

    /**
     * Set enable or disable TimeSlot 2
     *
     * @param setEnable if true : Enable TimeSlot 2.
     */
    private void EnableDisableTimeSlot2(Boolean setEnable) {
        tvTimeSecond.setEnabled(setEnable);
        if (setEnable) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color));

            } else {
                //tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color, null));
            }
        } else {
            selectTimeSlot(true);
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                //tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.summary_add_another_unit));

            } else {
                //tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                tvTimeSecond.setTextColor(getResources().getColor(R.color.summary_add_another_unit, null));
            }
        }
    }

    private void setInitialDate(String type) {
        Calendar c = Calendar.getInstance();
        if (type.equalsIgnoreCase("rep")) {
            c.add(Calendar.DATE, MaintenanceServicesWithinDays);
        } else if (type.equalsIgnoreCase("emer")) {

        } else {
            c.add(Calendar.DATE, EmergencyAndOtherServiceWithinDays);
        }
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        if (!type.equalsIgnoreCase("emer")) {
            if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                c.add(Calendar.DATE, 2);
            } else if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                c.add(Calendar.DATE, 1);
            }
        }
        tvDate.setText(df.format(c.getTime()));
    }

    /**
     * WebAPI for get plan types based on AddressId
     *
     * @param AddressId
     */
    public void getAllPlanTypeFromAddressID(String AddressId) {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getAllPlanTypeFromAddressID(AddressId, new Callback<GetPlanTypesResponce>() {
            @Override
            public void success(GetPlanTypesResponce getPlanTypesResponce, Response response) {

                if (getPlanTypesResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!getPlanTypesResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getPlanTypesResponce.Token);
                        editor.apply();
                    }
                    unitTypeDetail.clear();
                    for (int i = 0; i < getPlanTypesResponce.Data.size(); i++) {
                        UnitTypeDetailForList unitTypeDetailForList = new UnitTypeDetailForList();
                        unitTypeDetailForList.Id = getPlanTypesResponce.Data.get(i).Id;
                        unitTypeDetailForList.Name = getPlanTypesResponce.Data.get(i).PlanName;
                        unitTypeDetailForList.BasicFee = getPlanTypesResponce.Data.get(i).BasicFee;
                        unitTypeDetailForList.FeeIncrement = getPlanTypesResponce.Data.get(i).FeeIncrement;
                        if (i == 0) {
                            PlanTypeId = unitTypeDetailForList.Id;
                            unitTypeDetailForList.isSelected = true;
                        } else {
                            unitTypeDetailForList.isSelected = false;
                        }
                        unitTypeDetail.add(unitTypeDetailForList);
                    }
                    getScheduleTimeByPlanTypeServiceId(PlanTypeId);
                    planTypeAdapter = new PlanTypeAdapter(activity, unitTypeDetail);
                    LinearLayoutManager llm = new LinearLayoutManager(activity);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    rvSelectPlanType.setLayoutManager(llm);
                    rvSelectPlanType.setAdapter(planTypeAdapter);
                    planTypeAdapter.notifyDataSetChanged();
                    rvSelectPlanType.setVisibility(View.VISIBLE);
                    txtPlanTypeNoData.setVisibility(View.GONE);

                } else if (getPlanTypesResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    ((DashboardActivity) getActivity()).hideProgressDialog();
                    rvSelectPlanType.setVisibility(View.GONE);
                    txtPlanTypeNoData.setVisibility(View.VISIBLE);
                    txtPlanTypeNoData.setText(ErrorMessages.NoPlan);

                    rvSelectUnit.setVisibility(View.GONE);
                    txtUnitNoData.setVisibility(View.VISIBLE);
                    txtUnitNoData.setText(ErrorMessages.NoUnit);

                } else if (getPlanTypesResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    ((DashboardActivity) getActivity()).hideProgressDialog();
                    rvSelectPlanType.setVisibility(View.GONE);
                    txtPlanTypeNoData.setVisibility(View.VISIBLE);
                    txtPlanTypeNoData.setText(ErrorMessages.ErrorGettingData);

                    rvSelectUnit.setVisibility(View.GONE);
                    txtUnitNoData.setVisibility(View.VISIBLE);
                    txtUnitNoData.setText(ErrorMessages.ErrorGettingData);

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
                    rvSelectPlanType.setVisibility(View.GONE);
                    txtPlanTypeNoData.setVisibility(View.VISIBLE);
                    txtPlanTypeNoData.setText(getPlanTypesResponce.Message);

                    unitTypeDetail.clear();
                    planTypeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                rvSelectPlanType.setVisibility(View.GONE);
                txtPlanTypeNoData.setVisibility(View.VISIBLE);
                txtPlanTypeNoData.setText(ErrorMessages.ErrorGettingData);

                rvSelectUnit.setVisibility(View.GONE);
                txtUnitNoData.setVisibility(View.VISIBLE);
                txtUnitNoData.setText(ErrorMessages.ErrorGettingData);

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

    /**
     * Get Plan's slot timing based on PlanTypeId
     *
     * @param PlanTypeId
     */
    public void getScheduleTimeByPlanTypeServiceId(final String PlanTypeId) {

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getScheduleTimeByPlanTypeServiceId(PlanTypeId, null, null, new Callback<GetPurposeOfVisitTimeResponce>() {
            @Override
            public void success(GetPurposeOfVisitTimeResponce getPurposeOfVisitTimeResponce, Response response) {
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
                    selectedTimeSlote = getPurposeOfVisitTimeResponce.Data.TimeSlot1;
                    timeSlot1Units = getPurposeOfVisitTimeResponce.Data.TotalUnitSlot1;
                    timeSlot2Units = getPurposeOfVisitTimeResponce.Data.TotalUnitSlot2;
                    MaintenanceServicesWithinDays = getPurposeOfVisitTimeResponce.Data.MaintenanceServicesWithinDays;
                    EmergencyAndOtherServiceWithinDays = getPurposeOfVisitTimeResponce.Data.EmergencyAndOtherServiceWithinDays;
                    reasionDatas.clear();
                    alertMsgList.clear();
                    for (int i = 0; i < getPurposeOfVisitTimeResponce.Data.Purpose.size(); i++) {
                        alertMsgList.add(getPurposeOfVisitTimeResponce.Data.Purpose.get(i).Message);
                        ReasionList reasionList = new ReasionList();
                        if (i == 0) {
                            reasionList.isSelected = true;
//                            resedualRequest.PurposeOfVisit = getPurposeOfVisitTimeResponce.Data.Purpose.get(i).Name;
                            resedualRequest.PurposeOfVisit = getPurposeOfVisitTimeResponce.Data.Purpose.get(i).Id;
                        } else {
                            reasionList.isSelected = false;
                        }
                        reasionList.reasion = getPurposeOfVisitTimeResponce.Data.Purpose.get(i).Name;
                        reasionList.Id = getPurposeOfVisitTimeResponce.Data.Purpose.get(i).Id;
                        reasionDatas.add(reasionList);
                    }
                    getClientUnitByAddressIdPlanType("" + resedualRequest.AddressId, PlanTypeId);
                    reasionAdapter = new ServiceReasionAdapter(activity, reasionDatas);
                    LinearLayoutManager llm = new LinearLayoutManager(activity);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    rvSelectReason.setLayoutManager(llm);
                    rvSelectReason.setLayoutManager(llm);
                    rvSelectReason.setAdapter(reasionAdapter);
//                    if (reasionDatas.get(0).reasion.equalsIgnoreCase("Maintenance Services")) {
                    if (reasionDatas.get(0).Id.equalsIgnoreCase("3")) {
                        setInitialDate("rep");
                    } else {
                        setInitialDate("eme");
                    }

                } else if (getPurposeOfVisitTimeResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    ((DashboardActivity) getActivity()).hideProgressDialog();
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
                    unitsDatas.clear();
                    adapter.notifyDataSetChanged();
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
     * Get Address Listing
     */
    private void getAddressList() {
        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getAllAddress(sharedpreferences.getString("Id", ""), new Callback<getAddressResponce>() {
            @Override
            public void success(getAddressResponce GetAddressResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!GetAddressResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", GetAddressResponce.Token);
                        editor.apply();
                    }
                    addressDetail.clear();
                    for (int i = 0; i < GetAddressResponce.Data.size(); i++) {
                        if (GetAddressResponce.Data.get(i).ShowAddressInApp) {
                            AddressDetailForList addressDetailForList = new AddressDetailForList();
                            addressDetailForList.Id = GetAddressResponce.Data.get(i).Id;
                            addressDetailForList.Address = GetAddressResponce.Data.get(i).Address;
                            addressDetailForList.StateName = GetAddressResponce.Data.get(i).StateName;
                            addressDetailForList.CityName = GetAddressResponce.Data.get(i).CityName;
                            addressDetailForList.ZipCode = GetAddressResponce.Data.get(i).ZipCode;
                            addressDetailForList.IsDefaultAddress = GetAddressResponce.Data.get(i).IsDefaultAddress;
                            if (GetAddressResponce.Data.get(i).IsDefaultAddress) {
                                addressDetailForList.IsSelected = true;
                                resedualRequest.AddressId = Integer.parseInt(GetAddressResponce.Data.get(i).Id);
                            } else {
                                addressDetailForList.IsSelected = false;
                            }
                            addressDetail.add(addressDetailForList);
                        }
                    }

                    addUnitAddressListAdapter.notifyDataSetChanged();
                    if (resedualRequest.AddressId != 0) {
                        getAllPlanTypeFromAddressID("" + resedualRequest.AddressId);
                        if (addressDetail.size() > 0) {
                            rvAddressList.setVisibility(View.VISIBLE);
                            txtAddressNoData.setVisibility(View.GONE);
                        } else {
                            rvAddressList.setVisibility(View.GONE);
                            txtAddressNoData.setVisibility(View.VISIBLE);
                            txtAddressNoData.setText(ErrorMessages.NoAddress);

                            rvSelectPlanType.setVisibility(View.GONE);
                            txtPlanTypeNoData.setVisibility(View.VISIBLE);
                            txtPlanTypeNoData.setText(ErrorMessages.NoPlan);

                            rvSelectUnit.setVisibility(View.GONE);
                            txtUnitNoData.setVisibility(View.VISIBLE);
                            txtUnitNoData.setText(ErrorMessages.NoUnit);
                        }
                    } else {
                        rvSelectPlanType.setVisibility(View.GONE);
                        txtPlanTypeNoData.setVisibility(View.VISIBLE);
                        txtPlanTypeNoData.setText(ErrorMessages.NoPlan);

                        rvSelectUnit.setVisibility(View.GONE);
                        txtUnitNoData.setVisibility(View.VISIBLE);
                        txtUnitNoData.setText(ErrorMessages.NoUnit);
                    }


                } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    rvAddressList.setVisibility(View.GONE);
                    txtAddressNoData.setVisibility(View.VISIBLE);
                    txtAddressNoData.setText(ErrorMessages.NoAddress);

                    rvSelectPlanType.setVisibility(View.GONE);
                    txtPlanTypeNoData.setVisibility(View.VISIBLE);
                    txtPlanTypeNoData.setText(ErrorMessages.NoPlan);

                    rvSelectUnit.setVisibility(View.GONE);
                    txtUnitNoData.setVisibility(View.VISIBLE);
                    txtUnitNoData.setText(ErrorMessages.NoUnit);

                } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    rvAddressList.setVisibility(View.GONE);
                    txtAddressNoData.setVisibility(View.VISIBLE);
                    txtAddressNoData.setText(ErrorMessages.ErrorGettingData);

                    rvSelectPlanType.setVisibility(View.GONE);
                    txtPlanTypeNoData.setVisibility(View.VISIBLE);
                    txtPlanTypeNoData.setText(ErrorMessages.ErrorGettingData);

                    rvSelectUnit.setVisibility(View.GONE);
                    txtUnitNoData.setVisibility(View.VISIBLE);
                    txtUnitNoData.setText(ErrorMessages.ErrorGettingData);

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
                    rvAddressList.setVisibility(View.GONE);
                    txtAddressNoData.setVisibility(View.VISIBLE);
                    txtAddressNoData.setText(ErrorMessages.ErrorGettingData);

                    rvSelectPlanType.setVisibility(View.GONE);
                    txtPlanTypeNoData.setVisibility(View.VISIBLE);
                    txtPlanTypeNoData.setText(ErrorMessages.ErrorGettingData);

                    rvSelectUnit.setVisibility(View.GONE);
                    txtUnitNoData.setVisibility(View.VISIBLE);
                    txtUnitNoData.setText(ErrorMessages.ErrorGettingData);

                    ((DashboardActivity) getActivity()).hideProgressDialog();
                    DialogFragment ds = new SingleButtonAlert(GetAddressResponce.Message,
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
                rvAddressList.setVisibility(View.GONE);
                txtAddressNoData.setVisibility(View.VISIBLE);
                txtAddressNoData.setText(ErrorMessages.ErrorGettingData);

                rvSelectPlanType.setVisibility(View.GONE);
                txtPlanTypeNoData.setVisibility(View.VISIBLE);
                txtPlanTypeNoData.setText(ErrorMessages.ErrorGettingData);

                rvSelectUnit.setVisibility(View.GONE);
                txtUnitNoData.setVisibility(View.VISIBLE);
                txtUnitNoData.setText(ErrorMessages.ErrorGettingData);

                ((DashboardActivity) getActivity()).hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                            }
                        });
                ds.setCancelable(true);
                ds.show(getFragmentManager(), "");
                Log.e("failor Address", "failor " + error.getMessage());

            }
        });
    }

    /**
     * Get Client units based on planTypeId and AddressId
     *
     * @param addressId
     * @param planTypeId
     */
    public void getClientUnitByAddressIdPlanType(final String addressId, final String planTypeId) {

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.getClientUnitByAddressIdPlanType(sharedpreferences.getString("Id", ""), addressId, planTypeId, new Callback<MyUnitsResponce>() {
            @Override
            public void success(MyUnitsResponce myUnitsResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                if (myUnitsResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    unitsDatas.clear();
                    if (!myUnitsResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", myUnitsResponce.Token);
                        editor.apply();
                    }
                    unitsDatas = myUnitsResponce.Data;
                    adapter = new UnitsSelectionAdapter(activity, unitsDatas);
                    LinearLayoutManager llm = new LinearLayoutManager(activity);
                    llm.setOrientation(LinearLayoutManager.VERTICAL);
                    rvSelectUnit.setLayoutManager(llm);
                    rvSelectUnit.setAdapter(adapter);
                    rvSelectUnit.setVisibility(View.VISIBLE);
                    txtUnitNoData.setVisibility(View.GONE);
                } else if (myUnitsResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    rvSelectUnit.setVisibility(View.GONE);
                    txtUnitNoData.setVisibility(View.VISIBLE);
                    txtUnitNoData.setText(ErrorMessages.NoUnit);
                } else if (myUnitsResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    rvSelectUnit.setVisibility(View.GONE);
                    txtUnitNoData.setVisibility(View.VISIBLE);
                    txtUnitNoData.setText(ErrorMessages.ErrorGettingData);

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
                    rvSelectUnit.setVisibility(View.GONE);
                    txtUnitNoData.setVisibility(View.VISIBLE);
                    txtUnitNoData.setText(ErrorMessages.ErrorGettingData);

                    unitsDatas.clear();
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                rvSelectUnit.setVisibility(View.GONE);
                txtUnitNoData.setVisibility(View.VISIBLE);
                txtUnitNoData.setText(ErrorMessages.ErrorGettingData);

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
     * Submit data in resedualRequest Model and check validation.
     */
    public void submitService() {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
        Date newDate = null;
        try {
            newDate = formatter.parse(tvDate.getText().toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        resedualRequest.ServiceRequestedTime = selectedTimeSlote;
        resedualRequest.ServiceRequestedOn = FormatedaDate(tvDate.getText().toString());
        resedualRequest.Notes = etAddNote.getText().toString().trim();
        resedualRequest.Units.clear();
        for (int i = 0; i < unitsDatas.size(); i++) {
            if (unitsDatas.get(i).isSelected) {
                resedualRequest.Units.add(Integer.parseInt(unitsDatas.get(i).Id));
            }
        }
        if (resedualRequest.AddressId == 0) {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.AddAddress,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {
                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        } else if (resedualRequest.Units.size() == 0) {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.SelectUnit,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {
                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        } else if (resedualRequest.PurposeOfVisit.trim().equalsIgnoreCase("0")) { //For Repair

            if (isDateValied(toCalendar(newDate))) {
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.OnlyEmergencySatSun_message,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            } else {
                DialogFragment ds = new DoubleButtonAlert(alertMsgList.get(0),
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                if (tag.trim().equalsIgnoreCase("yes")) {
                                    addRequestForService();
                                }
                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        }
//        else if (resedualRequest.PurposeOfVisit.trim().equalsIgnoreCase("Emergency")) {
        else if (resedualRequest.PurposeOfVisit.trim().equalsIgnoreCase("1")) { // Emergency
            DialogFragment ds = new TwoButtonAlert(alertMsgList.get(1),
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {
                            if (tag.trim().equalsIgnoreCase("agree")) {
                                addRequestForService();
                            }
                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        } else if (resedualRequest.PurposeOfVisit.trim().equalsIgnoreCase("2")) { //Continuing Previous Work

            if (isDateValied(toCalendar(newDate))) {
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.OnlyEmergencySatSun_message,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            } else {
                DialogFragment ds = new DoubleButtonAlert(alertMsgList.get(2),
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                if (tag.trim().equalsIgnoreCase("yes")) {
                                    addRequestForService();
                                }
                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        } else if (resedualRequest.PurposeOfVisit.trim().equalsIgnoreCase("3")) { //Maintenance Service

            if (isDateValied(toCalendar(newDate))) {
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.OnlyEmergencySatSun_message,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            } else {
                DialogFragment ds = new DoubleButtonAlert(alertMsgList.get(3),
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                if (tag.trim().equalsIgnoreCase("yes")) {
                                    addRequestForService();
                                }
                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        }
    }

    /**
     * Call webAPI for Request the service.
     */
    public void addRequestForService() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.addRequestForService(resedualRequest, new Callback<RequestServicesResponce>() {
            @Override
            public void success(RequestServicesResponce requestServicesResponce, Response response) {
                if (requestServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    ((DashboardActivity) getActivity()).hideProgressDialog();
                    if (!requestServicesResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", requestServicesResponce.Token);
                        editor.apply();
                    }
                    Toast.makeText(activity, "Request successfully submitted.", Toast.LENGTH_LONG).show();
                    resetData();
                    FragmentTransaction transaction = getFragmentManager()
                            .beginTransaction();
                    Fragment newFragment = new ListOfRequestFragment();
                    String strFragmentTag = newFragment.toString();
                    transaction.add(R.id.frame_middel, newFragment,
                            strFragmentTag);
                    transaction.addToBackStack("");
                    transaction.commit();

                } else if (requestServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(requestServicesResponce.Message,
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
                Log.e("Fail", "Fail " + error.getMessage());
            }
        });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        String date = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
        SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy");
        Calendar c = Calendar.getInstance();
        c.set(year, monthOfYear, dayOfMonth);
        if (resedualRequest.PurposeOfVisit.trim().equalsIgnoreCase("1")) {
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String newFormat = formatter.format(testDate);
            tvDate.setText(newFormat);
            Date newDate = null;
            try {
                newDate = formatter.parse(tvDate.getText().toString());
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
        } else {
            Date testDate = null;
            try {
                testDate = sdf.parse(date);
            } catch (Exception ex) {
                ex.printStackTrace();
            }

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            //SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            String newFormat = formatter.format(testDate);
            tvDate.setText(newFormat);
        }
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    /**
     * Check if selected date is not weekend
     *
     * @param c
     * @return
     */
    private boolean isDateValied(Calendar c) {

        if (c.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || c.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }
    }

    private String FormatedaDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        return formatter.format(testDate);
    }

    /**
     * Reset all the field of screen after submit request successfully.
     */
    private void resetData() {
        etAddNote.setText("");
        int address = resedualRequest.AddressId;
        resedualRequest = new ResedualRequest();
        resedualRequest.AddressId = address;
        resedualRequest.ClientId = Integer.parseInt(sharedpreferences.getString("Id", ""));
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        tvDate.setText(df.format(c.getTime()));
        for (int i = 0; i < reasionDatas.size(); i++) {
            if (i == 0) {
                reasionDatas.get(i).isSelected = true;
                resedualRequest.PurposeOfVisit = reasionDatas.get(i).Id;
//                resedualRequest.PurposeOfVisit = reasionDatas.get(i).reasion;
            } else {
                reasionDatas.get(i).isSelected = false;
            }
            reasionAdapter.notifyDataSetChanged();
        }
        for (int i = 0; i < unitsDatas.size(); i++) {
            unitsDatas.get(i).isSelected = false;
            resedualRequest.Units.clear();

            adapter.notifyDataSetChanged();
        }
        selectedTimeSlote = tvTimeFirst.getText().toString();
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            tvTimeFirst.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
            tvTimeFirst.setTextColor(getResources().getColor(R.color.white));

            tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.upcoming_raw_footer_back));
            tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color));

        } else {
            tvTimeFirst.setBackground(getResources().getDrawable(R.drawable.button, null));
            tvTimeFirst.setTextColor(getResources().getColor(R.color.white, null));

            tvTimeSecond.setBackgroundColor(getResources().getColor(R.color.upcoming_raw_footer_back));
            tvTimeSecond.setTextColor(getResources().getColor(R.color.gray_text_color, null));
        }
//        setEmergencyTime();
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;
    }

    @Override
    public void onStart() {
        super.onStart();
        ((DashboardActivity) activity).last = "request_for_service";
    }
}
