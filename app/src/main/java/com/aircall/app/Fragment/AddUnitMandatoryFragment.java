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
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Adapter.AddUnitAddressListAdapter;
import com.aircall.app.Adapter.PlanTypeAdapter;
import com.aircall.app.AddAddressActivity;
import com.aircall.app.AddUnitActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.MnthYrPickerAlert;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.AddUnitInterface;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.Model.GetAddress.AddressDetailForList;
import com.aircall.app.Model.GetAddress.getAddressResponce;
import com.aircall.app.Model.GetAllPlanType.GetPlanTypesResponce;
import com.aircall.app.Model.GetAllPlanType.UnitTypeDetailForList;
import com.aircall.app.R;
import com.google.gson.Gson;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


/**
 * Created by Jd on 13/06/16.
 */
public class AddUnitMandatoryFragment extends Fragment implements com.wdullaer.materialdatetimepicker.date.DatePickerDialog.OnDateSetListener {

    @Bind(R.id.etManufactureYear)
    EditText etManufactureYear;

    @Bind(R.id.etUnitName)
    EditText etUnitName;

    @Bind(R.id.etUnitTon)
    EditText etUnitTon;

    @Bind(R.id.tvSubmit)
    TextView tvSubmit;

    @Bind(R.id.tvNoAddressMessage)
    TextView tvNoAddressMessage;

    @Bind(R.id.tvPlanComparison)
    TextView tvPlanComparison;

//    @Bind(R.id.tvDurationInMonth)
//    TextView tvDurationInMonth;

//    @Bind(R.id.tvPerMonth)
//    TextView tvPerMonth;

    @Bind(R.id.pricePerMonth)
    TextView pricePerMonth;

//    @Bind(R.id.tvPer36Month)
//    TextView tvPer36Month;

//    @Bind(R.id.tvSpecialOffer)
//    TextView tvSpecialOffer;
//
//    @Bind(R.id.tvOptionalInfo)
//    TextView tvOptionalInfo;
//
//    @Bind(R.id.llSpecialOffer)
//    LinearLayout llSpecialOffer;
//
//    @Bind(R.id.llAutoRenew)
//    LinearLayout llAutoRenew;

    @Bind(R.id.llMain)
    LinearLayout llMain;

//    @Bind(R.id.llSelectPlans)
//    LinearLayout llSelectPlans;
//
//    @Bind(R.id.ivCheckForOptionalInfo)
//    ImageView mCheckForOptionalInfo;
//
//    @Bind(R.id.ivCheckForAutoRenewalPlan)
//    ImageView mCheckForAutoRenewalPlan;
//
//    @Bind(R.id.ivCheckForSpecialOffer)
//    ImageView mCheckForSpecialOffer;

    @Bind(R.id.addAddress)
    ImageView addAddress;

    @Bind(R.id.rvUnitType)
    RecyclerView rvUnitType;

    @Bind(R.id.rvAddressList)
    RecyclerView rvAddressList;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.etQuantityunit)
    EditText etQuantityunit;

    @Bind(R.id.btnPlus)
    Button btnPlus;

    @Bind(R.id.btnMinus)
    Button btnMinus;

    @Bind(R.id.btnVisitMinus)
    Button btnVisitMinus;

    @Bind(R.id.btnVisitPlus)
    Button btnVisitPlus;

    @Bind(R.id.etVisitPerYear)
    EditText etVisitPerYear;

    GlobalClass globalClass;
    Activity activity;
    //    Boolean isOptionalData = false;
    //private String dateManufacture = "";
    private AddUnitInterface addUnitInteface;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private float mPricePerMonth, mSpecialPrice, mDiscountPrice;
    private boolean mIsAutoRenew = false,mIsSpecialOffer=false;

    /**
     * Types of unit/plan
     */
    private ArrayList<UnitTypeDetailForList> unitTypeDetail = new ArrayList<>();
    private BigDecimal basicPricePerMonth;
    private PlanTypeAdapter planTypeAdapter;

    /**
     * Address Listing
     */
    private ArrayList<AddressDetailForList> addressDetail = new ArrayList<>();
    private AddUnitAddressListAdapter addUnitAddressListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_unit_mandatory, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvent();
        return view;
    }

    private void init() {
        ivBack.setEnabled(false);
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        planTypeAdapter = new PlanTypeAdapter(activity, unitTypeDetail);
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvUnitType.setLayoutManager(llm);
        rvUnitType.setAdapter(planTypeAdapter);
        rvUnitType.setNestedScrollingEnabled(false);
        rvUnitType.setHasFixedSize(false);

        addUnitAddressListAdapter = new AddUnitAddressListAdapter(activity, addressDetail);
        LinearLayoutManager llm1 = new LinearLayoutManager(activity);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rvAddressList.setLayoutManager(llm1);
        rvAddressList.setAdapter(addUnitAddressListAdapter);
        rvAddressList.setNestedScrollingEnabled(false);
        rvAddressList.setHasFixedSize(false);
        SpannableString content = new SpannableString(getResources().getString(R.string.plan_comparison));
        content.setSpan(new UnderlineSpan(), 0, getResources().getString(R.string.plan_comparison).length(), 0);
        tvPlanComparison.setText(content);
        if (globalClass.checkInternetConnection()) {
            getPlanType();
        } else {
            ivBack.setEnabled(true);
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

    public void clickEvent() {

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Integer.parseInt(etQuantityunit.getText().toString()) + ((AddUnitActivity) activity).Quantity
                if (Integer.parseInt(etQuantityunit.getText().toString()) + ((AddUnitActivity) activity).Quantity < 5) {
                    etQuantityunit.setText((Integer.parseInt(etQuantityunit.getText().toString()) + 1) + "");
                    int quantity = Integer.parseInt(etQuantityunit.getText().toString());
                    BigDecimal num =new BigDecimal(quantity);
                    BigDecimal total = basicPricePerMonth.multiply(num);
                    pricePerMonth.setText("$"+ total);

//                    BigDecimal bdtvPerMonth = globalClass.roundupTwoDecimalPoint(Float.parseFloat(etQuantityunit.getText().toString()) * mPricePerMonth + "");
//                    tvPerMonth.setText("$" + bdtvPerMonth);
//                    BigDecimal a = globalClass.roundupTwoDecimalPoint((Float.parseFloat(etQuantityunit.getText().toString()) * mSpecialPrice) + "");
//                    BigDecimal b = globalClass.roundupTwoDecimalPoint((Float.parseFloat(etQuantityunit.getText().toString()) * mDiscountPrice) + "");
//                    tvSpecialOffer.setText("Save $" + a + " & Pay only $" + b);
                }
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Integer.parseInt(etQuantityunit.getText().toString()) > 1) {
                    etQuantityunit.setText((Integer.parseInt(etQuantityunit.getText().toString()) - 1) + "");
                    int quantity = Integer.parseInt(etQuantityunit.getText().toString());
                    BigDecimal num =new BigDecimal(quantity);
                    BigDecimal total = basicPricePerMonth.multiply(num);
                    pricePerMonth.setText("$"+ total);
//                    BigDecimal bdtvPerMonth = globalClass.roundupTwoDecimalPoint(Float.parseFloat(etQuantityunit.getText().toString()) * mPricePerMonth + "");
//                    tvPerMonth.setText("$" + bdtvPerMonth);
//                    BigDecimal a = globalClass.roundupTwoDecimalPoint((Float.parseFloat(etQuantityunit.getText().toString()) * mSpecialPrice) + "");
//                    BigDecimal b = globalClass.roundupTwoDecimalPoint((Float.parseFloat(etQuantityunit.getText().toString()) * mDiscountPrice) + "");
//                    tvSpecialOffer.setText("Save $" + a + " & Pay only $" + b);
                }

            }
        });

        tvPlanComparison.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), DashboardActivity.class);
                intent.putExtra("dash", "terms");
                intent.putExtra("PdfUrl", ServiceGenerator.BASE_URL + "/uploads/plan/Aicall_Plan_v2.pdf");
                intent.putExtra("PageTitle", "Plan Comparison");
                startActivity(intent);
            }
        });

        btnVisitPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(etVisitPerYear.getText().toString())  < 12) {
                    etVisitPerYear.setText((Integer.parseInt(etVisitPerYear.getText().toString()) + 1) + "");
                    for (int i=0; i<unitTypeDetail.size();i++)
                    {
                        if (unitTypeDetail.get(i).isSelected)
                        {
                            int feeIncrement = unitTypeDetail.get(i).FeeIncrement;
                            float basicFee =unitTypeDetail.get(i).BasicFee;
                            int visitYear = Integer.parseInt(etVisitPerYear.getText().toString());
                            int quantity = Integer.parseInt(etQuantityunit.getText().toString());
                            BigDecimal num =new BigDecimal(quantity);
                            float priceMonth = basicFee + (visitYear - 1) * feeIncrement;
                            basicPricePerMonth = globalClass.roundupTwoDecimalPoint(priceMonth + "");
                            pricePerMonth.setText("$"+ basicPricePerMonth.multiply(num));
                        }
                    }
                }
            }
        });

        btnVisitMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Integer.parseInt(etVisitPerYear.getText().toString())  > 1) {
                    etVisitPerYear.setText((Integer.parseInt(etVisitPerYear.getText().toString()) - 1) + "");
                    for (int i=0; i<unitTypeDetail.size();i++)
                    {
                        if (unitTypeDetail.get(i).isSelected)
                        {
                            int feeIncrement = unitTypeDetail.get(i).FeeIncrement;
                            float basicFee =unitTypeDetail.get(i).BasicFee;
                            int visitYear = Integer.parseInt(etVisitPerYear.getText().toString());
                            int quantity = Integer.parseInt(etQuantityunit.getText().toString());
                            BigDecimal num =new BigDecimal(quantity);
                            float priceMonth = basicFee + (visitYear - 1) * feeIncrement;
                            basicPricePerMonth = globalClass.roundupTwoDecimalPoint(priceMonth + "");
                            pricePerMonth.setText("$"+ basicPricePerMonth.multiply(num));
                        }
                    }
                }
            }
        });

        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
                } else if (((AddUnitActivity) activity).addUnitRequest.AddressId.equalsIgnoreCase("")) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.SelectAddress,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else if (((AddUnitActivity) activity).addUnitRequest.PlanTypeId.equalsIgnoreCase("")) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.SelectPlanType,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } /*else if(!isSelectedValidAddress) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.DeactiveAddress,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                }*/ else if (etQuantityunit.getText().toString().trim().equalsIgnoreCase("0") || etQuantityunit.getText().toString().trim().equalsIgnoreCase("")) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.EnterQuantity,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else {
                    //Remove Optional Information part.
                    ((AddUnitActivity) activity).addUnitRequest.ClientId = sharedpreferences.getString("Id", "");
                    saveData();

                    if (Integer.parseInt(etQuantityunit.getText().toString()) + ((AddUnitActivity) activity).Quantity > 5) {
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.max5unit,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {

                                    }
                                });
                        ds.setCancelable(true);
                        ds.show(getFragmentManager(), "");
                    } else {
                        addNewUnitRaw();
                    }

                }
            }
        });

        etManufactureYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int mnth = 0, yr = 0;

                if (etManufactureYear.getText().length() > 0) {
                    String[] MnthYr = etManufactureYear.getText().toString().split("/");
                    mnth = Integer.parseInt(MnthYr[0]);
                    yr = Integer.parseInt(MnthYr[1]);
                }

                DialogFragment ds = new MnthYrPickerAlert(mnth, yr,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                etManufactureYear.setText(tag);
                                ((AddUnitActivity) activity).addUnitRequest.ManufactureDate = tag;
                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");

            }
        });


//        llAutoRenew.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//
//
//                if (((AddUnitActivity) activity).addUnitRequest.AutoRenewal) {
//                    mCheckForAutoRenewalPlan.setImageResource(R.drawable.checkbox);
//                    mCheckForSpecialOffer.setImageResource(R.drawable.checkbox);
//                    ((AddUnitActivity) activity).addUnitRequest.AutoRenewal = false;
//                    ((AddUnitActivity) activity).addUnitRequest.SpecialOffer = false;
//                    llAutoRenew.setVisibility(View.VISIBLE);
//                    if(mIsSpecialOffer){
//                    llSpecialOffer.setVisibility(View.VISIBLE);}
//                } else {
//                    mCheckForAutoRenewalPlan.setImageResource(R.drawable.checkbox_selected);
//                    mCheckForSpecialOffer.setImageResource(R.drawable.checkbox);
//                    ((AddUnitActivity) activity).addUnitRequest.AutoRenewal = true;
//                    ((AddUnitActivity) activity).addUnitRequest.SpecialOffer = false;
//                    llAutoRenew.setVisibility(View.VISIBLE);
//                    llSpecialOffer.setVisibility(View.GONE);
//                }
//            }
//        });

//        llSpecialOffer.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//               /*mCheckForAutoRenewalPlan.setImageResource(R.drawable.checkbox);
//                mCheckForSpecialOffer.setImageResource(R.drawable.checkbox_selected);
//                ((AddUnitActivity) activity).addUnitRequest.AutoRenewal = false;
//                ((AddUnitActivity) activity).addUnitRequest.SpecialOffer = true;*/
//
//                if (((AddUnitActivity) activity).addUnitRequest.SpecialOffer) {
//                    mCheckForAutoRenewalPlan.setImageResource(R.drawable.checkbox);
//                    mCheckForSpecialOffer.setImageResource(R.drawable.checkbox);
//                    ((AddUnitActivity) activity).addUnitRequest.AutoRenewal = false;
//                    ((AddUnitActivity) activity).addUnitRequest.SpecialOffer = false;
//                    if(mIsAutoRenew){
//                    llAutoRenew.setVisibility(View.VISIBLE);}
//                    llSpecialOffer.setVisibility(View.VISIBLE);
//                } else {
//                    mCheckForAutoRenewalPlan.setImageResource(R.drawable.checkbox);
//                    mCheckForSpecialOffer.setImageResource(R.drawable.checkbox_selected);
//                    ((AddUnitActivity) activity).addUnitRequest.AutoRenewal = false;
//                    ((AddUnitActivity) activity).addUnitRequest.SpecialOffer = true;
//                    llAutoRenew.setVisibility(View.GONE);
//                    llSpecialOffer.setVisibility(View.VISIBLE);
//                }
//            }
//        });


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        addAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddAddressActivity.class);
                intent.putExtra("isDefaultAddress", false);
                startActivityForResult(intent, 1);
            }
        });

        rvUnitType.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        for (int i = 0; i < unitTypeDetail.size(); i++) {
                            if (i == position) {
                                unitTypeDetail.get(i).isSelected = true;
                                ((AddUnitActivity) activity).addUnitRequest.PlanTypeId = unitTypeDetail.get(i).Id;
                                int feeIncrement = unitTypeDetail.get(i).FeeIncrement;
                                float basicFee =unitTypeDetail.get(i).BasicFee;
                                int visitYear = Integer.parseInt(etVisitPerYear.getText().toString());
                                int quantity = Integer.parseInt(etQuantityunit.getText().toString());
                                BigDecimal num =new BigDecimal(quantity);
                                float priceMonth = basicFee + (visitYear - 1) * feeIncrement;
                                basicPricePerMonth =  globalClass.roundupTwoDecimalPoint(priceMonth + "");
                                pricePerMonth.setText("$"+ basicPricePerMonth.multiply(num));
                            } else {
                                unitTypeDetail.get(i).isSelected = false;
                            }
                        }
                        //llSelectPlans.setVisibility(View.VISIBLE);
                        planTypeAdapter.notifyDataSetChanged();
                        //getSpecialRateByPlanType(unitTypeDetail.get(position).Id);
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
                                ((AddUnitActivity) activity).addUnitRequest.AddressId = addressDetail.get(i).Id;
                            } else {
                                addressDetail.get(i).IsSelected = false;
                            }
                        }
                        addUnitAddressListAdapter.notifyDataSetChanged();
                    }
                })
        );
    }

    /**
     * Save some data in addUnitRequest from TextView
     */
    private void saveData() {
//        ((AddUnitActivity) activity).addUnitRequest.UnitTon = etUnitTon.getText().toString().trim();
        ((AddUnitActivity) activity).addUnitRequest.PricePerMonth = basicPricePerMonth;
        ((AddUnitActivity) activity).addUnitRequest.VisitPerYear = Integer.parseInt(etVisitPerYear.getText().toString());
        ((AddUnitActivity) activity).addUnitRequest.Qty = etQuantityunit.getText().toString();
        ((AddUnitActivity) activity).addUnitRequest.UnitName = etUnitName.getText().toString().trim();
//        ((AddUnitActivity) activity).addUnitRequest.ManufactureDate = etManufactureYear.getText().toString().trim();
    }

    /**
     * Web API for get PlanTypes for unit
     */
    public void getPlanType() {
        ((AddUnitActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getAllPlanType(new Callback<GetPlanTypesResponce>() {
            @Override
            public void success(GetPlanTypesResponce getPlanTypesResponce, Response response) {
                ((AddUnitActivity) getActivity()).hideProgressDialog();

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
                        //unitTypeDetailForList.isSelected = false;
                        if (i == 0) {
                            unitTypeDetailForList.isSelected = true;
                            ((AddUnitActivity) activity).addUnitRequest.PlanTypeId = unitTypeDetailForList.Id;
                            int feeIncrement = unitTypeDetailForList.FeeIncrement;
                            float basicFee = unitTypeDetailForList.BasicFee;
                            int visitYear = Integer.parseInt(etVisitPerYear.getText().toString());
                            float priceMonth = basicFee + (visitYear - 1) * feeIncrement;
                            basicPricePerMonth =  globalClass.roundupTwoDecimalPoint(priceMonth + "");
                            pricePerMonth.setText("$" + basicPricePerMonth);
//                            llSelectPlans.setVisibility(View.VISIBLE);
//                            planTypeAdapter.notifyDataSetChanged();
                            //getSpecialRateByPlanType(unitTypeDetail.get(0).Id);
                        } else {
                            unitTypeDetailForList.isSelected = false;
                        }
                        unitTypeDetail.add(unitTypeDetailForList);
                    }
                    planTypeAdapter.notifyDataSetChanged();
                    getAddressList();
                } else if (getPlanTypesResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    ivBack.setEnabled(true);
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
                    ivBack.setEnabled(true);
                    unitTypeDetail.clear();
                    planTypeAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ivBack.setEnabled(true);
                ((AddUnitActivity) getActivity()).hideProgressDialog();
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
     * Web API for get AddressLists for unit
     */
    public void getAddressList() {

        ((AddUnitActivity) activity).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getAllAddress(sharedpreferences.getString("Id", ""), new Callback<getAddressResponce>() {
            @Override
            public void success(getAddressResponce GetAddressResponce, Response response) {
                ivBack.setEnabled(true);
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
                            addressDetailForList.ShowAddressInApp = GetAddressResponce.Data.get(i).ShowAddressInApp;
                            addressDetailForList.IsSelected = false;
                            addressDetail.add(addressDetailForList);
                        }
                    }

                    if (addressDetail.size() == 1) {
                        addressDetail.get(0).IsSelected = true;
                        ((AddUnitActivity) activity).addUnitRequest.AddressId = addressDetail.get(0).Id;
                    }

                    if (GetAddressResponce.Data.size() > 0 && addressDetail.size() == 0) {
                        ((AddUnitActivity) activity).addUnitRequest.AddressId = "";
                        tvNoAddressMessage.setVisibility(View.VISIBLE);
                        rvAddressList.setVisibility(View.GONE);
                        tvNoAddressMessage.setText(ErrorMessages.NoActiveAddress);
                    } else {
                        addUnitAddressListAdapter.notifyDataSetChanged();
                        tvNoAddressMessage.setVisibility(View.GONE);
                        rvAddressList.setVisibility(View.VISIBLE);
                    }

                    //getSpecialRateByPlanType(unitTypeDetail.get(0).Id);
                    ivBack.setEnabled(true);
                    ((AddUnitActivity) activity).hideProgressDialog();
                } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    ((AddUnitActivity) activity).addUnitRequest.AddressId = "";
                    tvNoAddressMessage.setVisibility(View.VISIBLE);
                    rvAddressList.setVisibility(View.GONE);
//                    tvNoAddressMessage.setText(GetAddressResponce.Message);
                    tvNoAddressMessage.setText(ErrorMessages.NoAddressFound);
                    //getSpecialRateByPlanType(unitTypeDetail.get(0).Id);
                    ((AddUnitActivity) activity).hideProgressDialog();
                } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    ((AddUnitActivity) activity).hideProgressDialog();
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
                    ((AddUnitActivity) activity).hideProgressDialog();
                }

            }

            @Override
            public void failure(RetrofitError error) {
                ivBack.setEnabled(true);
                ((AddUnitActivity) activity).hideProgressDialog();
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
     * Web API for get Special offer rate for unit
     */
//    public void getSpecialRateByPlanType(String id) {
//
//        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
//        webserviceApi.getSpecialRateByPlanType(sharedpreferences.getString("Id", ""), id, new Callback<GetPlanTypesRateResponce>() {
//            @Override
//            public void success(GetPlanTypesRateResponce getPlanTypesRateResponce, Response response) {
//                ((AddUnitActivity) activity).hideProgressDialog();
//
//                if (getPlanTypesRateResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
//                    if (!getPlanTypesRateResponce.Token.equalsIgnoreCase("")) {
//                        editor.putString("Token", getPlanTypesRateResponce.Token);
//                        editor.apply();
//                    }
//                    mSpecialPrice = Float.parseFloat(getPlanTypesRateResponce.Data.SpecialPrice);
//                    mDiscountPrice = Float.parseFloat(getPlanTypesRateResponce.Data.DiscountPrice);
//                    mPricePerMonth = Float.parseFloat(getPlanTypesRateResponce.Data.PricePerMonth);
//
//                    if (Integer.parseInt(etQuantityunit.getText().toString()) == 1) {
//                        tvPerMonth.setText("$" + getPlanTypesRateResponce.Data.PricePerMonth);
//                    } else {
//                        BigDecimal a = globalClass.roundupTwoDecimalPoint(mPricePerMonth * Float.parseFloat(etQuantityunit.getText().toString()) + "");
//                        tvPerMonth.setText("$" + a);
//                    }
////                    tvPerMonth.setText("$" + getPlanTypesRateResponce.Data.PricePerMonth);
////                    tvPer36Month.setText("$" + getPlanTypesRateResponce.Data.Price);
//
//                    if (getPlanTypesRateResponce.Data.Display) {
//                        mIsSpecialOffer = true;
//                        llSpecialOffer.setVisibility(View.VISIBLE);
//                        if (Integer.parseInt(etQuantityunit.getText().toString()) == 1) {
//                            tvSpecialOffer.setText(getPlanTypesRateResponce.Data.SpecialText);
//                        } else {
//                            BigDecimal aa = globalClass.roundupTwoDecimalPoint((Float.parseFloat(etQuantityunit.getText().toString()) * mSpecialPrice)+"");
//                            BigDecimal bb = globalClass.roundupTwoDecimalPoint((Float.parseFloat(etQuantityunit.getText().toString()) * mDiscountPrice)+"");
//                            tvSpecialOffer.setText("Save $" + aa + " & Pay only $" + bb);
//                        }
//                        tvDurationInMonth.setText("For " + getPlanTypesRateResponce.Data.DurationInMonth + " Months");
//                    } else {
//                        mIsSpecialOffer = false;
//                        llSpecialOffer.setVisibility(View.GONE);
//                        tvDurationInMonth.setText("For " + getPlanTypesRateResponce.Data.DurationInMonth + " Months");
//                    }
//
//                    if (getPlanTypesRateResponce.Data.ShowAutoRenewal) {
//                        mIsAutoRenew = true;
//                        llAutoRenew.setVisibility(View.VISIBLE);
//
//
//                        mCheckForAutoRenewalPlan.setImageResource(R.drawable.checkbox);
//                        ((AddUnitActivity) activity).addUnitRequest.AutoRenewal = false;
//                        mCheckForSpecialOffer.setImageResource(R.drawable.checkbox);
//                        ((AddUnitActivity) activity).addUnitRequest.SpecialOffer = false;
//
//                    } else {
//                        mIsAutoRenew = false;
//                        llAutoRenew.setVisibility(View.GONE);
//
//                        if (getPlanTypesRateResponce.Data.Display) {
//
//                            mCheckForAutoRenewalPlan.setImageResource(R.drawable.checkbox);
//                            ((AddUnitActivity) activity).addUnitRequest.AutoRenewal = false;
//                            mCheckForSpecialOffer.setImageResource(R.drawable.checkbox);
//                            ((AddUnitActivity) activity).addUnitRequest.SpecialOffer = false;
//                        }
//                    }
//                } else if (getPlanTypesRateResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
//                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
//                            new DialogInterfaceClick() {
//                                @Override
//                                public void dialogClick(String tag) {
//                                    globalClass.Clientlogout();
//                                }
//                            });
//                    ds.setCancelable(true);
//                    ds.show(getFragmentManager(), "");
//                } else {
//                    DialogFragment ds = new SingleButtonAlert(getPlanTypesRateResponce.Message,
//                            new DialogInterfaceClick() {
//                                @Override
//                                public void dialogClick(String tag) {
//                                }
//                            });
//                    ds.setCancelable(true);
//                    ds.show(getFragmentManager(), "");
//                }
//            }
//
//            @Override
//            public void failure(RetrofitError error) {
//                ((AddUnitActivity) activity).hideProgressDialog();
//                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
//                        new DialogInterfaceClick() {
//                            @Override
//                            public void dialogClick(String tag) {
//                            }
//                        });
//                ds.setCancelable(true);
//                ds.show(getFragmentManager(), "");
//
//                Log.e("failor Rate", "failor " + error.getMessage());
//            }
//        });
//
//    }

    public void addNewUnit(String JsonArray) {

        ((AddUnitActivity) activity).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.addUnit(sharedpreferences.getString("Id", ""), ((AddUnitActivity) activity).addUnitRequest.UnitName,
                ((AddUnitActivity) activity).addUnitRequest.ManufactureDate, ((AddUnitActivity) activity).addUnitRequest.PlanTypeId,
                ((AddUnitActivity) activity).addUnitRequest.UnitTon, ((AddUnitActivity) activity).addUnitRequest.AddressId,
                ((AddUnitActivity) activity).addUnitRequest.AutoRenewal, ((AddUnitActivity) activity).addUnitRequest.Status,
                ((AddUnitActivity) activity).addUnitRequest.UnitTypeId, JsonArray, new Callback<AddUnitResponce>() {
                    @Override
                    public void success(AddUnitResponce addUnitResponce, Response response) {
                        ((AddUnitActivity) activity).hideProgressDialog();

                        if (addUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            Toast.makeText(activity, "New Unit Added Successfully", Toast.LENGTH_LONG).show();

                        } else if (addUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(addUnitResponce.Message,
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
                        ((AddUnitActivity) activity).hideProgressDialog();
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
     * Add unit in summary, in response we will get summary screen data to feel data in screen
     */
    //Change Model Class According to Remove Optional Information.
    public void addNewUnitRaw() {
        ((AddUnitActivity) activity).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.addUnitRaw(((AddUnitActivity) activity).addUnitRequest, new Callback<AddUnitResponce>() {
            @Override
            public void success(AddUnitResponce addUnitResponce, Response response) {
                ((AddUnitActivity) activity).hideProgressDialog();

                if (addUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addUnitResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addUnitResponce.Token);
                        editor.apply();
                    }
                    //Remove
//                    ((AddUnitActivity) activity).addUnitRequest.OptionalInformation = new ArrayList<>();
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    String json = gson.toJson(addUnitResponce);
                    bundle.putString("summary", json);
                    Log.e("Json====>>", json);
                    ((AddUnitActivity) activity).clearBackStack();
                    addUnitInteface.changeFragment("summary", false, bundle);
                } else if (addUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(addUnitResponce.Message,
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
                ((AddUnitActivity) activity).hideProgressDialog();
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getActivity();
        if (requestCode == 1) {
            getAddressList();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        addUnitInteface = (AddUnitActivity) activity;
        globalClass = ((AddUnitActivity) activity).globalClass;
    }

    /**
     * date picker call method after select date
     *
     * @param view
     * @param year
     * @param monthOfYear
     * @param dayOfMonth
     */
    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {

        String date = dayOfMonth + "/" + (++monthOfYear) + "/" + year;
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        SimpleDateFormat newFormat = new SimpleDateFormat("dd MMMM, yyyy");
        SimpleDateFormat sendFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date newDate = formatter.parse(date);
            etManufactureYear.setText(newFormat.format(newDate));
            //dateManufacture = sendFormat.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
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


}
