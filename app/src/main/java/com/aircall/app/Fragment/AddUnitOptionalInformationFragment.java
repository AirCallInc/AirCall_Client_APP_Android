package com.aircall.app.Fragment;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.AddUnitActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.FilterQuantityFragment;
import com.aircall.app.Dialog.SelectFilterSizeDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.AddUnitInterface;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.Model.DynamicViews.FilterViewDetail;
import com.aircall.app.Model.DynamicViews.FuseViewDetail;
import com.aircall.app.Model.Parts.GetFilterForUnitResponce;
import com.aircall.app.R;
import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddUnitOptionalInformationFragment extends Fragment {

    GlobalClass globalClass;
    Activity activity;
    String strFilterQuantity;

    @Bind(R.id.flMain)
    public FrameLayout flMain;

    @Bind(R.id.ivInfoForPackaged)
    TextView mInfoForPackaged;

    @Bind(R.id.ivInfoForSplit)
    TextView mInfoForSplit;

    @Bind(R.id.ivInfoForHeating)
    TextView mInfoForHeating;


    @Bind(R.id.ivCheckForOptionalInfo)
    ImageView ivCheckForOptionalInfo;

    @Bind(R.id.tvSubmitAddUnitOptional)
    TextView tvSubmitAddUnitOptional;

    /**
     * First Layout
     */

    @Bind(R.id.etAddUnitFilterQuantity)
    EditText mAddUnitFilterQuantityDialog;

    @Bind(R.id.etAddUnitFusesQuality)
    EditText mAddUnitFusesQuality;

    @Bind(R.id.llFilterDetails)
    LinearLayout llFilterDetails;

    @Bind(R.id.llFuseDetail)
    LinearLayout llFuseDetail;

    @Bind(R.id.etAddUnitModelNumber)
    EditText etAddUnitModelNumber;

    @Bind(R.id.etAddUnitSerialNumber)
    EditText etAddUnitSerialNumber;

    @Bind(R.id.etAddUnitBooster)
    EditText etAddUnitBooster;

    @Bind(R.id.tvCoolingTitle)
    TextView tvCoolingTitle;

    /**
     * Second Layout
     */

    @Bind(R.id.etAddUnitFilterQuantitySplit)
    EditText mAddUnitFilterQuantityDialogSplit;

    @Bind(R.id.etAddUnitFusesQualitySplit)
    EditText mAddUnitFusesQualitySplit;

    @Bind(R.id.llFilterDetailsSplit)
    LinearLayout llFilterDetailsSplit;

    @Bind(R.id.llFuseDetailSplit)
    LinearLayout llFuseDetailSplit;

    @Bind(R.id.etAddUnitModelNumberSplit)
    EditText etAddUnitModelNumberSplit;

    @Bind(R.id.etAddUnitSerialNumberSplit)
    EditText etAddUnitSerialNumberSplit;

    @Bind(R.id.etAddUnitBoosterSplit)
    EditText etAddUnitBoosterSplit;

    @Bind(R.id.llPackagedSplit)
    LinearLayout llPackagedSplit;

    @Bind(R.id.llOptionalInfo)
    LinearLayout llOptionalInfo;

    @Bind(R.id.tvHeatingTitle)
    TextView tvHeatingTitle;


    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    int optionId = R.layout.seperator_of_filter_size;
    View view;
    public boolean isFromAddButton = false;
    private AddUnitInterface addUnitInteface;
    private GetFilterForUnitResponce filterResponce, fuseResponce, boosters;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_addunit_optional_information, container, false);

        ButterKnife.bind(this, view);
        init();
        clickEvents();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        addUnitInteface = (AddUnitActivity) activity;
        globalClass = ((AddUnitActivity) activity).globalClass;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        getFilterSize();

    }

    public void clickEvents() {
//        mAddUnitFilterQuantityDialog.setClickable(true);

        flMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**
         * Submit optional information to addUnitRequest and move again to MandatoryFragment
         */
        tvSubmitAddUnitOptional.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //((AddUnitActivity) activity).addUnitRequest.ClientId = sharedpreferences.getString("Id", "");
                isFromAddButton = true;
                setCommonData();
                //Remove
//                ((AddUnitActivity) activity).transfrerData();

                Gson gson = new Gson();
                String json = gson.toJson(((AddUnitActivity) activity).addUnitRequest);
                Log.e("JSON", "obj " + json);
                activity.onBackPressed();
            }
        });


        mInfoForPackaged.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setSelectedmInfoForPackaged();
            }
        });

        mInfoForSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setSelectedmInfoForSplit();
            }
        });

        mInfoForHeating.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                setSelectedmInfoForHeating();
            }
        });


        /**
         * Get filter size from FilterQuantityFragment and dynamically set number of field to get
         * detail information of filter.
         */
        mAddUnitFilterQuantityDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment ds = new FilterQuantityFragment(globalClass, "Filter", mAddUnitFilterQuantityDialog.getText().toString(),
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                mAddUnitFilterQuantityDialog.setText(tag);
                                int number = Integer.parseInt(tag);
                                if (((AddUnitActivity) activity).filterDetail.viewList.size() > number) {
                                    while(((AddUnitActivity) activity).filterDetail.viewList.size()!=number) {
                                        ((AddUnitActivity) activity).filterDetail.viewList.remove(number);
                                    }
                                }
                                ChangeDataOfllFilterDetails(number);
                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        /**
         * Get Splits filter size from FilterQuantityFragment and dynamically set number of field to get
         * detail information of Splits filter.
         */
        mAddUnitFilterQuantityDialogSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment ds = new FilterQuantityFragment(globalClass, "Filter", mAddUnitFilterQuantityDialogSplit.getText().toString(),
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                mAddUnitFilterQuantityDialogSplit.setText(tag);
                                int number = Integer.parseInt(tag);
                                if (((AddUnitActivity) activity).filterDetail.viewListSplit.size() > number) {
                                    while(((AddUnitActivity) activity).filterDetail.viewListSplit.size()!=number) {
                                        ((AddUnitActivity) activity).filterDetail.viewListSplit.remove(number);
                                    }
                                }
                                ChangeDataOfllFilterDetailsSplit(number);
                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        /**
         * Get Fuse size from FilterQuantityFragment and dynamically set number of field to get
         * detail information of Fuse.
         */
        mAddUnitFusesQuality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment ds = new FilterQuantityFragment(globalClass, "Fuse", mAddUnitFusesQuality.getText().toString(),
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                mAddUnitFusesQuality.setText(tag);
                                int number = Integer.parseInt(tag);
                                if (((AddUnitActivity) activity).fuseViews.viewList.size() > number) {
                                    while(((AddUnitActivity) activity).fuseViews.viewList.size()!=number) {
                                        ((AddUnitActivity) activity).fuseViews.viewList.remove(number);
                                    }
                                }
                                ChangeDatatOfFuseDetail(number);
                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        /**
         * Get Splits Fuse size from FilterQuantityFragment and dynamically set number of field to get
         * detail information of Splits Fuse.
         */
        mAddUnitFusesQualitySplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment ds = new FilterQuantityFragment(globalClass, "Fuse", mAddUnitFusesQualitySplit.getText().toString(),
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                mAddUnitFusesQualitySplit.setText(tag);
                                int number = Integer.parseInt(tag);
                                if (((AddUnitActivity) activity).fuseViews.viewListSplit.size() > number) {
                                    while(((AddUnitActivity) activity).fuseViews.viewListSplit.size()!=number) {
                                        ((AddUnitActivity) activity).fuseViews.viewListSplit.remove(number);
                                    }
                                }
                                ChangeDatatOfFuseDetailSplit(number);
                            }
                        });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        /**
         * Select type of booster from SelectFilterSizeDialogFragment and set in commonDataRequest.
         */
        etAddUnitBooster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment ds = new SelectFilterSizeDialogFragment(globalClass, boosters, new UsernameDialogInteface() {
                    @Override
                    public void submitClick(String id, String text) {
                        etAddUnitBooster.setText(text);
                        ((AddUnitActivity) activity).commonDataRequest.commonData.boosterType = id;
                        ((AddUnitActivity) activity).commonDataRequest.commonData.boosterText = text;
                    }
                });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        /**
         * Select type of splits booster from SelectFilterSizeDialogFragment and set in commonDataRequest.
         */
        etAddUnitBoosterSplit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment ds = new SelectFilterSizeDialogFragment(globalClass, boosters, new UsernameDialogInteface() {
                    @Override
                    public void submitClick(String id, String text) {
                        ((AddUnitActivity) activity).commonDataRequest.commonDataSplit.boosterType = id;
                        ((AddUnitActivity) activity).commonDataRequest.commonDataSplit.boosterText = text;
                        etAddUnitBoosterSplit.setText(text);
                    }
                });
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        ivCheckForOptionalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ////Remove
//                if(((AddUnitActivity) activity).addUnitRequest != null &&
//                        ((AddUnitActivity) activity).addUnitRequest.OptionalInformation != null) {
//                    ((AddUnitActivity) activity).addUnitRequest.OptionalInformation.clear();
//                }
                activity.onBackPressed();
            }
        });

    }

    /**
     * Set data in commonDataRequest which are not different for simple and splits types unit
     */
    private void setCommonData() {
        ((AddUnitActivity) activity).commonDataRequest.commonData.ModelNumber = etAddUnitModelNumber.getText().toString().trim();
        ((AddUnitActivity) activity).commonDataRequest.commonData.SerialNumber = etAddUnitSerialNumber.getText().toString().trim();
        if (((AddUnitActivity) activity).addUnitRequest.UnitTypeId.equalsIgnoreCase("1")) {
            ((AddUnitActivity) activity).commonDataRequest.commonData.SplitType = "Packaged";
        } else if (((AddUnitActivity) activity).addUnitRequest.UnitTypeId.equalsIgnoreCase("2")) {
            ((AddUnitActivity) activity).commonDataRequest.commonData.SplitType = "Split-Heating";
            ((AddUnitActivity) activity).commonDataRequest.commonDataSplit.ModelNumber = etAddUnitModelNumberSplit.getText().toString().trim();
            ((AddUnitActivity) activity).commonDataRequest.commonDataSplit.SerialNumber = etAddUnitSerialNumberSplit.getText().toString().trim();
            ((AddUnitActivity) activity).commonDataRequest.commonDataSplit.SplitType = "Split-Cooling";
        } else {
            ((AddUnitActivity) activity).commonDataRequest.commonData.SplitType = "Heating";
        }
    }



    @TargetApi(Build.VERSION_CODES.M)
    private void setSelectedmInfoForPackaged() {
        ((AddUnitActivity) activity).addUnitRequest.UnitTypeId = "1";

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mInfoForPackaged.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
            mInfoForPackaged.setTextColor(getResources().getColor(R.color.white));

            mInfoForSplit.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForSplit.setTextColor(getResources().getColor(R.color.black));

            mInfoForHeating.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForHeating.setTextColor(getResources().getColor(R.color.black));
        } else {
            mInfoForPackaged.setBackground(getResources().getDrawable(R.drawable.button, null));
            mInfoForPackaged.setTextColor(getResources().getColor(R.color.white, null));

            mInfoForSplit.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForSplit.setTextColor(getResources().getColor(R.color.black, null));

            mInfoForHeating.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForHeating.setTextColor(getResources().getColor(R.color.black, null));
        }
        llPackagedSplit.setVisibility(View.GONE);
        tvCoolingTitle.setVisibility(View.GONE);
        tvHeatingTitle.setVisibility(View.GONE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setSelectedmInfoForSplit() {

        ((AddUnitActivity) activity).addUnitRequest.UnitTypeId = "2";

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mInfoForSplit.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
            mInfoForSplit.setTextColor(getResources().getColor(R.color.white));

            mInfoForPackaged.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForPackaged.setTextColor(getResources().getColor(R.color.black));

            mInfoForHeating.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForHeating.setTextColor(getResources().getColor(R.color.black));
        } else {
            mInfoForSplit.setBackground(getResources().getDrawable(R.drawable.button, null));
            mInfoForSplit.setTextColor(getResources().getColor(R.color.white, null));

            mInfoForPackaged.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForPackaged.setTextColor(getResources().getColor(R.color.black, null));

            mInfoForHeating.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForHeating.setTextColor(getResources().getColor(R.color.black, null));
        }
        llPackagedSplit.setVisibility(View.VISIBLE);
        tvCoolingTitle.setVisibility(View.VISIBLE);
        tvHeatingTitle.setVisibility(View.VISIBLE);
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void setSelectedmInfoForHeating() {
        ((AddUnitActivity) activity).addUnitRequest.UnitTypeId = "3";

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mInfoForHeating.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
            mInfoForHeating.setTextColor(getResources().getColor(R.color.white));

            mInfoForPackaged.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForPackaged.setTextColor(getResources().getColor(R.color.black));

            mInfoForSplit.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForSplit.setTextColor(getResources().getColor(R.color.black));
        } else {
            mInfoForHeating.setBackground(getResources().getDrawable(R.drawable.button, null));
            mInfoForHeating.setTextColor(getResources().getColor(R.color.white, null));

            mInfoForPackaged.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForPackaged.setTextColor(getResources().getColor(R.color.black, null));

            mInfoForSplit.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
            mInfoForSplit.setTextColor(getResources().getColor(R.color.black, null));
        }
        llPackagedSplit.setVisibility(View.GONE);
        tvCoolingTitle.setVisibility(View.GONE);
        tvHeatingTitle.setVisibility(View.GONE);
    }

    /**
     * When user change count of fuse then we have to change it's details field also
     *
     * @param count number of fuse user selected
     */
    private void ChangeDatatOfFuseDetail(int count) {
        if (((LinearLayout) llFuseDetail).getChildCount() >= 0) {
            ((LinearLayout) llFuseDetail).removeAllViews();
        }

        for (int i = 0; i < count; i++) {
            FuseViewDetail fuseViewDetail = new FuseViewDetail();
            View child = LayoutInflater.from(activity).inflate(
                    R.layout.seperator_of_quality_fuses, null);
            final EditText etAddUnitFuseQuality = (EditText) child.findViewById(R.id.etAddUnitFuseQuality);
            etAddUnitFuseQuality.setId(i);
            etAddUnitFuseQuality.setHint("Fuse type " + (i + 1));

            etAddUnitFuseQuality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment ds = new SelectFilterSizeDialogFragment(globalClass, fuseResponce, new UsernameDialogInteface() {
                        @Override
                        public void submitClick(String id, String text) {
                            ((AddUnitActivity) activity).fuseViews.viewList.get(etAddUnitFuseQuality.getId()).etAddUnitFuseQuality.setText(text);
                            ((AddUnitActivity) activity).fuseViews.viewList.get(etAddUnitFuseQuality.getId()).FuseTypeId = id;
                            ((AddUnitActivity) activity).fuseViews.viewList.get(etAddUnitFuseQuality.getId()).FuseTypeText = text;

                        }
                    });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                }
            });

            if (i < ((AddUnitActivity) activity).fuseViews.viewList.size()) {
                ((AddUnitActivity) activity).fuseViews.viewList.get(i).etAddUnitFuseQuality = etAddUnitFuseQuality;
                etAddUnitFuseQuality.setText(((AddUnitActivity) activity).fuseViews.viewList.get(i).FuseTypeText);
            } else {
                fuseViewDetail.FuseTypeText = "";
                fuseViewDetail.FuseTypeId = "";
                fuseViewDetail.etAddUnitFuseQuality = etAddUnitFuseQuality;
                ((AddUnitActivity) activity).fuseViews.viewList.add(fuseViewDetail);
            }
            llFuseDetail.addView(child);
        }
    }

    /**
     * When user change count of Split fuse then we have to change it's details field also
     *
     * @param count number of Split fuse user selected
     */
    private void ChangeDatatOfFuseDetailSplit(int count) {
        if (((LinearLayout) llFuseDetailSplit).getChildCount() >= 0) {
            ((LinearLayout) llFuseDetailSplit).removeAllViews();
        }

        for (int i = 0; i < count; i++) {
            FuseViewDetail fuseViewDetail = new FuseViewDetail();
            View child = LayoutInflater.from(activity).inflate(
                    R.layout.seperator_of_quality_fuses, null);
            final EditText etAddUnitFuseQuality = (EditText) child.findViewById(R.id.etAddUnitFuseQuality);
            etAddUnitFuseQuality.setId(i);
            etAddUnitFuseQuality.setHint("Fuse type " + (i + 1));

            etAddUnitFuseQuality.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment ds = new SelectFilterSizeDialogFragment(globalClass, fuseResponce, new UsernameDialogInteface() {
                        @Override
                        public void submitClick(String id, String text) {
                            ((AddUnitActivity) activity).fuseViews.viewListSplit.get(etAddUnitFuseQuality.getId()).etAddUnitFuseQuality.setText(text);
                            ((AddUnitActivity) activity).fuseViews.viewListSplit.get(etAddUnitFuseQuality.getId()).FuseTypeId = id;
                            ((AddUnitActivity) activity).fuseViews.viewListSplit.get(etAddUnitFuseQuality.getId()).FuseTypeText = text;

                        }
                    });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                }
            });

            if (i < ((AddUnitActivity) activity).fuseViews.viewListSplit.size()) {
                ((AddUnitActivity) activity).fuseViews.viewListSplit.get(i).etAddUnitFuseQuality = etAddUnitFuseQuality;
                etAddUnitFuseQuality.setText(((AddUnitActivity) activity).fuseViews.viewListSplit.get(i).FuseTypeText);
            } else {
                fuseViewDetail.FuseTypeText = "";
                fuseViewDetail.FuseTypeId = "";
                fuseViewDetail.etAddUnitFuseQuality = etAddUnitFuseQuality;
                ((AddUnitActivity) activity).fuseViews.viewListSplit.add(fuseViewDetail);
            }

            llFuseDetailSplit.addView(child);
        }
    }

    /**
     * When user change count of Filter then we have to change it's details field also
     *
     * @param count number of Filter user selected
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void ChangeDataOfllFilterDetails(int count) {
        if (((LinearLayout) llFilterDetails).getChildCount() >= 0) {
            ((LinearLayout) llFilterDetails).removeAllViews();
        }

        //((AddUnitActivity) activity).filterDetail.viewList = new ArrayList<>();
        //((AddUnitActivity) activity).filterDetail.viewList.clear();


        for (int i = 0; i < count; i++) {
            FilterViewDetail filterViewDetail = new FilterViewDetail();
            View child = LayoutInflater.from(activity).inflate(
                    R.layout.seperator_of_filter_size, null);

            final EditText etFilterSize = (EditText) child.findViewById(R.id.etFilterSize);
            etFilterSize.setId(i);
            TextView tvFilterLocationHead = (TextView) child.findViewById(R.id.tvFilterLocationHead);
            final TextView tvInsideEquipment = (TextView) child.findViewById(R.id.tvInsideEquipment);
            tvInsideEquipment.setId(i);
            final TextView tvInsideSpace = (TextView) child.findViewById(R.id.tvInsideSpace);
            tvInsideSpace.setId(i);

            etFilterSize.setHint("Filter Size " + (i + 1));
            tvFilterLocationHead.setText("Filter Location " + (i + 1));

            tvInsideEquipment.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        tvInsideEquipment.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.white));

                        tvInsideSpace.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        tvInsideEquipment.setBackground(getResources().getDrawable(R.drawable.button, null));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.white, null));

                        tvInsideSpace.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.black, null));
                    }
                    Log.e("Click Inside Equipment", "" + tvInsideEquipment.getId());
                    ((AddUnitActivity) activity).filterDetail.viewList.get(tvInsideEquipment.getId()).isInside = true;
                }
            });
            tvInsideSpace.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        tvInsideSpace.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.white));

                        tvInsideEquipment.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        tvInsideSpace.setBackground(getResources().getDrawable(R.drawable.button, null));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.white, null));

                        tvInsideEquipment.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.black, null));
                    }
                    Log.e("Click Inside Space", "" + tvInsideSpace.getId());
                    ((AddUnitActivity) activity).filterDetail.viewList.get(tvInsideEquipment.getId()).isInside = false;
                }
            });
            etFilterSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("Click Edit Text", "" + etFilterSize.getId());
                    DialogFragment ds = new SelectFilterSizeDialogFragment(globalClass, filterResponce, new UsernameDialogInteface() {
                        @Override
                        public void submitClick(String id, String text) {
                            ((AddUnitActivity) activity).filterDetail.viewList.get(etFilterSize.getId()).etFilterSize.setText(text);
                            ((AddUnitActivity) activity).filterDetail.viewList.get(etFilterSize.getId()).FilterSizeId = id;
                            ((AddUnitActivity) activity).filterDetail.viewList.get(etFilterSize.getId()).FilterSizeText = text;
                        }
                    });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                }
            });
            if (i < ((AddUnitActivity) activity).filterDetail.viewList.size()) {
                ((AddUnitActivity) activity).filterDetail.viewList.get(i).etFilterSize = etFilterSize;
                etFilterSize.setText(((AddUnitActivity) activity).filterDetail.viewList.get(i).FilterSizeText);
                if (((AddUnitActivity) activity).filterDetail.viewList.get(i).isInside) {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        tvInsideEquipment.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.white));

                        tvInsideSpace.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        tvInsideEquipment.setBackground(getResources().getDrawable(R.drawable.button, null));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.white, null));

                        tvInsideSpace.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.black, null));
                    }
                } else {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        tvInsideSpace.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.white));

                        tvInsideEquipment.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        tvInsideSpace.setBackground(getResources().getDrawable(R.drawable.button, null));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.white, null));

                        tvInsideEquipment.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.black, null));
                    }
                }
            } else {
                filterViewDetail.isInside = true;
                filterViewDetail.FilterSizeText = "";
                filterViewDetail.FilterSizeId = "";
                filterViewDetail.etFilterSize = etFilterSize;
                ((AddUnitActivity) activity).filterDetail.viewList.add(filterViewDetail);
            }

            llFilterDetails.addView(child);
        }

    }

    /**
     * When user change count of Split Filter then we have to change it's details field also
     *
     * @param count number of Split Filter user selected
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void ChangeDataOfllFilterDetailsSplit(int count) {
        if (((LinearLayout) llFilterDetailsSplit).getChildCount() >= 0) {
            ((LinearLayout) llFilterDetailsSplit).removeAllViews();
        }
        //((AddUnitActivity) activity).filterDetail.viewListSplit = new ArrayList<>();
        //((AddUnitActivity) activity).filterDetail.viewListSplit.clear();


        for (int i = 0; i < count; i++) {
            FilterViewDetail filterViewDetail = new FilterViewDetail();
            View child = LayoutInflater.from(activity).inflate(
                    R.layout.seperator_of_filter_size, null);

            final EditText etFilterSize = (EditText) child.findViewById(R.id.etFilterSize);
            etFilterSize.setId(i);
            TextView tvFilterLocationHead = (TextView) child.findViewById(R.id.tvFilterLocationHead);
            final TextView tvInsideEquipment = (TextView) child.findViewById(R.id.tvInsideEquipment);
            tvInsideEquipment.setId(i);
            final TextView tvInsideSpace = (TextView) child.findViewById(R.id.tvInsideSpace);
            tvInsideSpace.setId(i);

            etFilterSize.setHint("Filter Size " + (i + 1));
            tvFilterLocationHead.setText("Filter Location " + (i + 1));

            tvInsideEquipment.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        tvInsideEquipment.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.white));

                        tvInsideSpace.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        tvInsideEquipment.setBackground(getResources().getDrawable(R.drawable.button, null));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.white, null));

                        tvInsideSpace.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.black, null));
                    }
                    ((AddUnitActivity) activity).filterDetail.viewListSplit.get(tvInsideEquipment.getId()).isInside = true;
                    Log.e("Click Inside Equipment", "" + tvInsideEquipment.getId());
                }
            });
            tvInsideSpace.setOnClickListener(new View.OnClickListener() {
                @TargetApi(Build.VERSION_CODES.M)
                @Override
                public void onClick(View v) {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        tvInsideSpace.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.white));

                        tvInsideEquipment.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        tvInsideSpace.setBackground(getResources().getDrawable(R.drawable.button, null));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.white, null));

                        tvInsideEquipment.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.black, null));
                    }
                    ((AddUnitActivity) activity).filterDetail.viewListSplit.get(tvInsideEquipment.getId()).isInside = false;
                    Log.e("Click Inside Space", "" + tvInsideSpace.getId());
                }
            });
            etFilterSize.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFragment ds = new SelectFilterSizeDialogFragment(globalClass, filterResponce, new UsernameDialogInteface() {
                        @Override
                        public void submitClick(String id, String text) {
                            ((AddUnitActivity) activity).filterDetail.viewListSplit.get(etFilterSize.getId()).etFilterSize.setText(text);
                            ((AddUnitActivity) activity).filterDetail.viewListSplit.get(etFilterSize.getId()).FilterSizeId = id;
                            ((AddUnitActivity) activity).filterDetail.viewListSplit.get(etFilterSize.getId()).FilterSizeText = text;
                        }
                    });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                }
            });


            if (i < ((AddUnitActivity) activity).filterDetail.viewListSplit.size()) {
                ((AddUnitActivity) activity).filterDetail.viewListSplit.get(i).etFilterSize = etFilterSize;
                etFilterSize.setText(((AddUnitActivity) activity).filterDetail.viewListSplit.get(i).FilterSizeText);
                if (((AddUnitActivity) activity).filterDetail.viewListSplit.get(i).isInside) {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        tvInsideEquipment.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.white));

                        tvInsideSpace.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        tvInsideEquipment.setBackground(getResources().getDrawable(R.drawable.button, null));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.white, null));

                        tvInsideSpace.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.black, null));
                    }
                } else {
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        tvInsideSpace.setBackgroundDrawable(getResources().getDrawable(R.drawable.button));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.white));

                        tvInsideEquipment.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.black));

                    } else {
                        tvInsideSpace.setBackground(getResources().getDrawable(R.drawable.button, null));
                        tvInsideSpace.setTextColor(getResources().getColor(R.color.white, null));

                        tvInsideEquipment.setBackgroundColor(getResources().getColor(R.color.tab_normal_background));
                        tvInsideEquipment.setTextColor(getResources().getColor(R.color.black, null));
                    }
                }
            } else {
                filterViewDetail.isInside = true;
                filterViewDetail.FilterSizeText = "";
                filterViewDetail.FilterSizeId = "";
                filterViewDetail.etFilterSize = etFilterSize;
                ((AddUnitActivity) activity).filterDetail.viewListSplit.add(filterViewDetail);
            }


            llFilterDetailsSplit.addView(child);
        }
    }

    /**
     * Web API for get Filters type
     */
    public void getFilterSize() {

        ((AddUnitActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getFilters("Filter", new Callback<GetFilterForUnitResponce>() {
            @Override
            public void success(final GetFilterForUnitResponce getFilterForUnitResponce, Response response) {

                if (getFilterForUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!getFilterForUnitResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getFilterForUnitResponce.Token);
                        editor.apply();
                    }
                    filterResponce = getFilterForUnitResponce;
                    getFuseType();

                } else if (getFilterForUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    Log.e("Filter", "Not Get Data" + getFilterForUnitResponce.Message);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((AddUnitActivity) getActivity()).hideProgressDialog();

            }
        });

    }

    /**
     * Web API for get Fuse type
     */
    public void getFuseType() {

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getFilters("Fuse", new Callback<GetFilterForUnitResponce>() {
            @Override
            public void success(final GetFilterForUnitResponce getFilterForUnitResponce, Response response) {
                if (getFilterForUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!getFilterForUnitResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getFilterForUnitResponce.Token);
                        editor.apply();
                    }
                    fuseResponce = getFilterForUnitResponce;
                    getBoosterList();

                } else if (getFilterForUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    Log.e("Fuse", "Not Get Data" + getFilterForUnitResponce.Message);
                    /*((AddUnitActivity) getActivity()).hideProgressDialog();
                    DialogFragment ds = new SingleButtonAlert(getFilterForUnitResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");*/
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

    /**
     * Web API for get Thermostat type
     */
    public void getBoosterList() {

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getFilters("Thermostat", new Callback<GetFilterForUnitResponce>() {
            @Override
            public void success(final GetFilterForUnitResponce getFilterForUnitResponce, Response response) {
                ((AddUnitActivity) getActivity()).hideProgressDialog();
                if (getFilterForUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!getFilterForUnitResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getFilterForUnitResponce.Token);
                        editor.apply();
                    }
                    boosters = getFilterForUnitResponce;
                    //Remove
//                    setPreviousData();
                } else if (getFilterForUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    Log.e("Fuse", "Not Get Data" + getFilterForUnitResponce.Message);
                    /*DialogFragment ds = new SingleButtonAlert(getFilterForUnitResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");*/
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

    public void addNewUnit(String JsonArray) {

        ((AddUnitActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.addUnit(sharedpreferences.getString("Id", ""), ((AddUnitActivity) activity).addUnitRequest.UnitName,
                ((AddUnitActivity) activity).addUnitRequest.ManufactureDate, ((AddUnitActivity) activity).addUnitRequest.PlanTypeId,
                ((AddUnitActivity) activity).addUnitRequest.UnitTon, ((AddUnitActivity) activity).addUnitRequest.AddressId,
                ((AddUnitActivity) activity).addUnitRequest.AutoRenewal, ((AddUnitActivity) activity).addUnitRequest.Status,
                ((AddUnitActivity) activity).addUnitRequest.UnitTypeId, JsonArray, new Callback<AddUnitResponce>() {
                    @Override
                    public void success(AddUnitResponce addUnitResponce, Response response) {
                        ((AddUnitActivity) getActivity()).hideProgressDialog();

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

    public void addNewUnitRaw() {

        ((AddUnitActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.addUnitRaw(((AddUnitActivity) activity).addUnitRequest, new Callback<AddUnitResponce>() {
            @Override
            public void success(AddUnitResponce addUnitResponce, Response response) {
                ((AddUnitActivity) getActivity()).hideProgressDialog();

                if (addUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addUnitResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addUnitResponce.Token);
                        editor.apply();
                    }
                    Bundle bundle = new Bundle();
                    Gson gson = new Gson();
                    String json = gson.toJson(addUnitResponce);
                    Log.e("JSON", "is " + json);
                    bundle.putString("summary", json);
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
}