package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aircall.app.Adapter.AcListPagerAdapter;
import com.aircall.app.Adapter.DashboardNotificationListAdapter;
import com.aircall.app.AddUnitActivity;
import com.aircall.app.AddressListActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.Model.Dashboard.DashboardResponce;
import com.aircall.app.Model.Dashboard.NotificationDetail;
import com.aircall.app.Model.Dashboard.UnitDetails;
import com.aircall.app.Model.PastServices.RattingResponce;
import com.aircall.app.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class DashboardFragment extends Fragment {

    @Bind(R.id.vpTopUnits)
    public ViewPager vpTopUnits;

    @Bind(R.id.ivNavDrawer)
    public ImageView ivNavDrawer;

    @Bind(R.id.rgPager)
    public RadioGroup rgPager;

    @Bind(R.id.tvNotificationCount)
    public TextView tvNotificationCount;

    @Bind(R.id.tvAddUnit)
    public TextView tvAddUnit;

    @Bind(R.id.tvUserName)
    public TextView tvUserName;

    @Bind(R.id.tvDay)
    public TextView tvDay;

    @Bind(R.id.tvMonth)
    public TextView tvMonth;

    @Bind(R.id.tvTimeSlot)
    public TextView tvTimeSlot;

    @Bind(R.id.tvDiscription)
    public TextView tvDiscription;

    @Bind(R.id.tvYear)
    public TextView tvYear;

    @Bind(R.id.tvAllUnit)
    public TextView tvAllUnit;

    @Bind(R.id.ivAddress)
    public ImageView ivAddress;

    @Bind(R.id.llNotificationFirst)
    public LinearLayout llNotificationFirst;


    @Bind(R.id.rvNotificationList)
    public RecyclerView rvNotificationList;

    @Bind(R.id.svData)
    public ScrollView svData;

    @Bind(R.id.rlNoData)
    public RelativeLayout rlNoData;

    @Bind(R.id.tvUserNameNoData)
    public TextView tvUserNameNoData;

    @Bind(R.id.ivAddUnit)
    public ImageView ivAddUnit;

    @Bind(R.id.ivNotificatios)
    public ImageView ivNotificatios;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.tvDefaultAddress)
    TextView tvDefaultAddress;

    private String TAG = "DASHBOARD FRAGMENT";
    private GlobalClass globalClass;
    private SharedPreferences sharedpreferences;
    private ProgressDialogFragment progressDialogFragment;
    private SharedPreferences.Editor editor;
    private Activity activity;

    /**
     * adapterPager : Adapter for AC List view pager
     */
    private AcListPagerAdapter adapterPager;
    private ArrayList<UnitDetails> units = new ArrayList();
    private ArrayList<RadioButton> radioButtons = new ArrayList<RadioButton>();

    /**
     * notificationListAdapter : Adapter for notification list
     */
    private DashboardNotificationListAdapter notificationListAdapter;
    private ArrayList<NotificationDetail> notificationDetails = new ArrayList<>();

    private boolean isPandingUnits = false, isProcessingUnit = false;//, isCalled = false;
    private String firstNId, firstCId, name;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard,
                container, false);
        ButterKnife.bind(this, view);

        Log.e(TAG, "onCreateView");

        init();
        clickEvents();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        /*isCalled = true;
        if (globalClass.checkInternetConnection()) {
            getClientToken();
        } else {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {
                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        }*/
    }

    private void clickEvents() {
        vpTopUnits.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (radioButtons.size() > 0) {
                    rgPager.check(radioButtons.get(vpTopUnits.getCurrentItem()).getId());
                    Log.e("Radio Id", "" + radioButtons.get(vpTopUnits.getCurrentItem()).getId());
                }
            }
        });
        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) activity).openDrawer();
            }
        });
        ivNotificatios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) activity).menuitemClick("NotificationListFragment", "");
            }
        });
        tvAllUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) activity).menuitemClick("my_units", "");
            }
        });
        tvAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (globalClass.checkInternetConnection()) {
                    if (isProcessingUnit) {
                        /**
                         * Check if is there process running of unit payment from server side.
                         */
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.ProccessingUnitPayment,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {
                                        if (globalClass.checkInternetConnection()) {
                                            getDashboardData();
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
//                        if (isPandingUnits) {
//                            /**
//                             * Check if is there any already added unit which payment has not done.
//                             */
//                            DialogFragment ds = new DoubleButtonAlert(ErrorMessages.PandingUnitProcess,
//                                    new DialogInterfaceClick() {
//                                        @Override
//                                        public void dialogClick(String message) {
//                                            if (globalClass.checkInternetConnection()) {
//                                                if (message.equalsIgnoreCase("yes")) {
//                                                    getPaymentFailedUnit();
//                                                } else {
//                                                    deleteOldData();
//                                                }
//                                            } else {
//                                                DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
//                                                        new DialogInterfaceClick() {
//                                                            @Override
//                                                            public void dialogClick(String tag) {
//                                                            }
//                                                        });
//                                                ds.setCancelable(false);
//                                                ds.show(getFragmentManager(), "");
//                                            }
//                                        }
//                                    });
//                            ds.setCancelable(false);
//                            ds.show(getFragmentManager(), "");
//                        } else {
                            Intent intent = new Intent(activity, AddUnitActivity.class);
                            startActivity(intent);
//                        }
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
        ivAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Check if is there process running of unit payment from server side.
                 */

                if (globalClass.checkInternetConnection()) {
                    if (isProcessingUnit) {
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.ProccessingUnitPayment,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {
                                        if (globalClass.checkInternetConnection()) {
                                            getDashboardData();
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
//                        if (isPandingUnits) {
//                            /**
//                             * Check if is there any already added unit which payment has not done.
//                             */
//                            DialogFragment ds = new DoubleButtonAlert(ErrorMessages.PandingUnitProcess,
//                                    new DialogInterfaceClick() {
//                                        @Override
//                                        public void dialogClick(String message) {
//                                            if (globalClass.checkInternetConnection()) {
//                                                if (message.equalsIgnoreCase("yes")) {
//                                                    getPaymentFailedUnit();
//                                                } else {
//                                                    deleteOldData();
//                                                }
//                                            } else {
//                                                DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
//                                                        new DialogInterfaceClick() {
//                                                            @Override
//                                                            public void dialogClick(String tag) {
//                                                            }
//                                                        });
//                                                ds.setCancelable(false);
//                                                ds.show(getFragmentManager(), "");
//                                            }
//                                        }
//                                    });
//                            ds.setCancelable(false);
//                            ds.show(getFragmentManager(), "");
//                        } else {
                            Intent intent = new Intent(activity, AddUnitActivity.class);
                            startActivity(intent);
                        //}
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
        ivAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddressListActivity.class);
                startActivity(intent);
            }
        });

        rvNotificationList.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ((DashboardActivity) activity).notificationClick(notificationDetails.get(position).NotificationType,
                                "" + notificationDetails.get(position).NotificationId, "" + notificationDetails.get(position).CommonId);
                    }
                })
        );

        llNotificationFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) activity).notificationClick(globalClass.NTYPE_SCHEDULE, firstNId, firstCId);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach");
        this.activity = activity;

        globalClass = ((DashboardActivity) activity).globalClass;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart");
        if (globalClass.checkInternetConnection()) {
            name = sharedpreferences.getString("FirstName", "");
            tvUserName.setText("Hello " + name);
            tvUserNameNoData.setText("Hello " + name);
            if (name.length() <= 10) {
                tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(20));
                tvUserNameNoData.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(20));
            }

            if (name.length() > 12) {
                tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(16));
                tvUserNameNoData.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(16));
            }

            if (name.length() > 15) {
                tvUserName.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(14));
                tvUserNameNoData.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(14));
            }
            getDashboardData();
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
        //}
        //isCalled = false;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
        ((DashboardActivity) activity).last = "dashboard";
        //if (!isCalled) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e(TAG, "onDetach");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e(TAG, "onDestroyView");
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    /*public void getClientToken() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class);

        webserviceApi.getClientToken(sharedpreferences.getString("Id", ""), new Callback<DeleteAddressResponce>() {
            @Override
            public void success(DeleteAddressResponce deleteAddressResponce, Response response) {
                hideProgressDialog();
                if (deleteAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    editor.putString("Token", deleteAddressResponce.Token);
                    editor.apply();
                    tvUserName.setText("Hello " + sharedpreferences.getString("FirstName", ""));
                    tvUserNameNoData.setText("Hello " + sharedpreferences.getString("FirstName", ""));
                    getDashboardData();
                } else {
                    DialogFragment ds = new SingleButtonAlert(deleteAddressResponce.Message,
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

    }*/

    /**
     * WebAPI for get dashboard data, if there is no units then display only add unit button layout.
     */
    public void getDashboardData() {

        Log.e("Id", sharedpreferences.getString("Id", ""));
        showProgressDailog("Please Wait...");
        Log.e("Token", sharedpreferences.getString("Token", ""));
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getDashboardData(sharedpreferences.getString("Id", ""), new Callback<DashboardResponce>() {
            @Override
            public void success(DashboardResponce dashboardResponce, Response response) {
                hideProgressDialog();
                if (dashboardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!dashboardResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", dashboardResponce.Token);
                        editor.apply();
                    }
                    if(dashboardResponce.Data.DefaultAddress != null) {
                        if (!dashboardResponce.Data.DefaultAddress.equalsIgnoreCase("")) {
                            tvDefaultAddress.setText(" @ " + dashboardResponce.Data.DefaultAddress);
                        }
                    }
                    units = dashboardResponce.Data.Units;
                    isPandingUnits = dashboardResponce.Data.HasPaymentFailedUnit;
                    isProcessingUnit = dashboardResponce.Data.HasPaymentProcessingUnits;
                    if (dashboardResponce.Data.NotificationCount == 0) {
                        tvNotificationCount.setVisibility(View.GONE);
                    } else {
                        tvNotificationCount.setVisibility(View.VISIBLE);
                        tvNotificationCount.setText("" + dashboardResponce.Data.NotificationCount);
                    }
                    if (units == null || units.size() == 0) {
                        svData.setVisibility(View.GONE);
                        rlNoData.setVisibility(View.VISIBLE);

                        //Remove Welcome Message when user is signup
//                        if (((DashboardActivity) activity).isFromSignUp) {
//                            ((DashboardActivity) activity).isFromSignUp = false;
//                            DialogFragment ds = new SingleButtonAlert(ErrorMessages.ThanxMessage,
//                                    new DialogInterfaceClick() {
//                                        @Override
//                                        public void dialogClick(String tag) {
//                                        }
//                                    });
//                            ds.setCancelable(false);
//                            ds.show(getFragmentManager(), "");
//                        }

                    } else {
                        svData.setVisibility(View.VISIBLE);
                        rlNoData.setVisibility(View.GONE);
                        adapterPager = new AcListPagerAdapter(getActivity(), units);
                        vpTopUnits.setClipToPadding(false);
                        vpTopUnits.setPageMargin(30);
                        vpTopUnits.setPadding(120, 0, 120, 0);
                        vpTopUnits.setAdapter(adapterPager);
//                        addRadioButtons(adapterPager.getCount());

                        if (dashboardResponce.Data.Notifications.size() > 0) {
                            int startWith = 0;
                            if (!dashboardResponce.Data.Notifications.get(0).NotificationType.equalsIgnoreCase(globalClass.NTYPE_SCHEDULE)) {
                                llNotificationFirst.setVisibility(View.GONE);
                            } else {
                                llNotificationFirst.setVisibility(View.VISIBLE);
                                firstCId = dashboardResponce.Data.Notifications.get(0).CommonId;
                                firstNId = dashboardResponce.Data.Notifications.get(0).NotificationId;
                                tvDay.setText(dashboardResponce.Data.Notifications.get(0).ScheduleDay);
                                tvMonth.setText(dashboardResponce.Data.Notifications.get(0).ScheduleMonth);
                                tvYear.setText(dashboardResponce.Data.Notifications.get(0).ScheduleYear);
                                tvTimeSlot.setText(dashboardResponce.Data.Notifications.get(0).ScheduleStartTime + " - " +
                                        dashboardResponce.Data.Notifications.get(0).ScheduleEndTime);
                                tvDiscription.setText(dashboardResponce.Data.Notifications.get(0).Message);
                                startWith = 1;
                            }
                            notificationDetails.clear();
                            for (int i = startWith; i < dashboardResponce.Data.Notifications.size(); i++) {
                                notificationDetails.add(dashboardResponce.Data.Notifications.get(i));
                            }
                            notificationListAdapter = new DashboardNotificationListAdapter(activity, notificationDetails);
                            LinearLayoutManager llm = new LinearLayoutManager(activity);
                            llm.setOrientation(LinearLayoutManager.VERTICAL);
                            rvNotificationList.setLayoutManager(llm);
                            rvNotificationList.setAdapter(notificationListAdapter);
                        } else {
                            llNotificationFirst.setVisibility(View.GONE);
                        }
                    }

                } else if (dashboardResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                hideProgressDialog();
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
     * Call WebAPI when client want to make payment of unpaid units.
     */
    public void getPaymentFailedUnit() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.getPaymentFailedUnit(sharedpreferences.getString("Id", ""), new Callback<AddUnitResponce>() {

            @Override
            public void success(AddUnitResponce addUnitResponce, Response response) {

                ((DashboardActivity) getActivity()).hideProgressDialog();

                if (addUnitResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addUnitResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addUnitResponce.Token);
                        editor.apply();
                    }
                    Gson gson = new Gson();
                    String json = gson.toJson(addUnitResponce);
                    Intent intent = new Intent(activity, AddUnitActivity.class);
                    intent.putExtra("addUnit", "summary");
                    intent.putExtra("summaryData", json);
                    startActivity(intent);


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
     * Call WebAPI when user don't want to make payment old units and want to add new unit, then old unpaid units will be deleted.
     */
    public void deleteOldData() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.deleteOldData(sharedpreferences.getString("Id", ""), new Callback<RattingResponce>() {

            @Override
            public void success(RattingResponce rattingResponce, Response response) {

                ((DashboardActivity) getActivity()).hideProgressDialog();

                if (rattingResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!rattingResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", rattingResponce.Token);
                        editor.apply();
                    }
                    Intent intent = new Intent(activity, AddUnitActivity.class);
                    startActivity(intent);

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
            }
        });
    }

    /**
     * Add dynamic radio buttons below viewpager of unit list.
     *
     * @param number Total numbers of radio button want to add.
     */
    public void addRadioButtons(int number) {
        radioButtons.clear();
        rgPager.removeAllViews();
        if (number > 1) {
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.WRAP_CONTENT,
                    RadioGroup.LayoutParams.WRAP_CONTENT
            );

            for (int i = 0; i < number; i++) {
                RadioGroup.LayoutParams params_button = new RadioGroup.LayoutParams(15, 15);
                params_button.setMargins(5, 0, 5, 0);
                RadioButton rdbtn = new RadioButton(activity);
                rdbtn.setEnabled(false);
                rdbtn.setId(i);
                rdbtn.setLayoutParams(params_button);
                rdbtn.setBackgroundResource(R.drawable.radio_button_background);
                //rdbtn.setBackgroundColor(getResources().getColor(R.color.button_material_dark));
                rdbtn.setButtonDrawable(R.color.transperent);
                if (i == 0) {
                    rdbtn.setChecked(true);
                }

                rgPager.addView(rdbtn);
                radioButtons.add(rdbtn);
            }
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
}