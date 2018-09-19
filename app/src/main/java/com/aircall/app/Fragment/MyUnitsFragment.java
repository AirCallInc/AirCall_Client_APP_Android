package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Adapter.MyUnitsAdapter;
import com.aircall.app.AddUnitActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.DoubleButtonAlert;
import com.aircall.app.Dialog.SelectAddressListDialog;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.Model.GetAddress.AddressDetailForList;
import com.aircall.app.Model.GetAddress.getAddressResponce;
import com.aircall.app.Model.MyUnits.MyUnitsData;
import com.aircall.app.Model.MyUnits.MyUnitsResponce;
import com.aircall.app.Model.PastServices.RattingResponce;
import com.aircall.app.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MyUnitsFragment extends Fragment {

    @Bind(R.id.rvMyUnits)
    RecyclerView rvMyUnits;

    @Bind(R.id.llLoadMore)
    LinearLayout llLoadMore;

    @Bind(R.id.ivAddUnit)
    ImageView ivAddUnit;

    @Bind(R.id.ivSelectAddress)
    ImageView ivSelectAddress;

    @Bind(R.id.ivNavDrawer)
    ImageView ivNavDrawer;

    @Bind(R.id.flMain)
    public FrameLayout flMain;

    @Bind(R.id.tvNoData)
    TextView tvNoData;

    private String TAG = "MYUNIT FRAGMENT";

    private GlobalClass globalClass;
    private Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private MyUnitsAdapter adapter;
    private LinearLayoutManager llm;
    private ArrayList<AddressDetailForList> addressList = new ArrayList<>();
    private ArrayList<MyUnitsData> unitsDatas = new ArrayList<>();
    private String AddressId = null, PageNumber = "1";
    private boolean isPandingUnits = false, isProcessingUnit = false;


    /*Load more*/
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.fragment_my_units,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvent();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvMyUnits.setLayoutManager(llm);
        adapter = new MyUnitsAdapter(activity, unitsDatas);
        adapter.notifyDataSetChanged();
        rvMyUnits.setAdapter(adapter);
        callForMyUnit();
    }

    public void callForMyUnit() {
        if (globalClass.checkInternetConnection()) {
            unitsDatas.clear();
            loading = true;
            PageNumber = "1";
            isPandingUnits = false;
            isProcessingUnit = false;
            getMyUnits(AddressId, false);
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
        flMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (globalClass.checkInternetConnection()) {
                    if (isProcessingUnit) {
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.ProccessingUnitPayment,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {
                                        if (globalClass.checkInternetConnection()) {
                                            getMyUnits(null, false);
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

                        if (isPandingUnits) {
                            DialogFragment ds = new DoubleButtonAlert(ErrorMessages.PandingUnitProcess,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String message) {
                                            if (globalClass.checkInternetConnection()) {
                                                if (message.equalsIgnoreCase("yes")) {
                                                    getPaymentFailedUnit();
                                                } else {
                                                    deleteOldData();
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
                            Intent intent = new Intent(activity, AddUnitActivity.class);
                            startActivity(intent);
                        }
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

        ivSelectAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment ds = new SelectAddressListDialog(globalClass,
                        new UsernameDialogInteface() {
                            @Override
                            public void submitClick(String Id, String Id_null) {
                                for (int i = 0; i < addressList.size(); i++) {
                                    if (addressList.get(i).Id.equalsIgnoreCase(Id)) {
                                        addressList.get(i).IsSelected = true;
                                    } else {
                                        addressList.get(i).IsSelected = false;
                                    }
                                }
                                if (AddressId == null || !AddressId.equalsIgnoreCase(Id)) {
                                    unitsDatas.clear();
                                    AddressId = Id;
                                    PageNumber = "1";
                                    getMyUnits(Id, false);
                                }
                            }
                        }, addressList);
                ds.setCancelable(false);
                ds.show(getFragmentManager(), "");
            }
        });

        ivNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).openDrawer();
            }
        });

        /**
         * Check for load more data
         */
        rvMyUnits.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        FragmentTransaction transaction = getFragmentManager()
                                .beginTransaction();
                        Fragment fragment = getFragmentManager().findFragmentById(
                                R.id.frame_middel);
                        Fragment newFragment = new UnitDetailFragment();
                        Bundle bundle = new Bundle();
                        //Gson gson = new Gson();
                        //String json = gson.toJson(unitsDatas.get(position));
                        String unitId = unitsDatas.get(position).Id;
                        bundle.putString("UnitId", unitId);
                        //bundle.putString("UnitDetailData", json);
                        newFragment.setArguments(bundle);
                        String strFragmentTag = newFragment.toString();
                        transaction.add(R.id.frame_middel, newFragment,
                                strFragmentTag);
                        transaction.addToBackStack("");
                        transaction.commit();
                    }
                })
        );

        rvMyUnits.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            getMyUnits(AddressId, true);
                        }
                    }
                }
            }
        });
    }

    /**
     * Get list of units from web API
     *
     * @param addressId  Selected Address id
     * @param isLoadMore Is data for load more or first time
     */
    public void getMyUnits(final String addressId, final Boolean isLoadMore) {

        if (!isLoadMore) {
            ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        } else {
            llLoadMore.setVisibility(View.VISIBLE);
        }

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getClientUnit(sharedpreferences.getString("Id", ""), PageNumber, addressId, new Callback<MyUnitsResponce>() {
            @Override
            public void success(MyUnitsResponce myUnitsResponce, Response response) {
                if (myUnitsResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {

                    if (!myUnitsResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", myUnitsResponce.Token);
                        editor.apply();
                    }
                    unitsDatas.addAll(myUnitsResponce.Data);
                    isPandingUnits = myUnitsResponce.HasPaymentFailedUnit;
                    isProcessingUnit = myUnitsResponce.HasPaymentProcessingUnits;
                    adapter.notifyDataSetChanged();
                    if (addressId == null) {
                        getAddressList();
                    }
                    loading = true;
                    PageNumber = myUnitsResponce.PageNumber;
                    tvNoData.setVisibility(View.GONE);
                    rvMyUnits.setVisibility(View.VISIBLE);

                    if (!isLoadMore) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                    } else {
                        llLoadMore.setVisibility(View.GONE);
                    }

                } else if (myUnitsResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    if (!isLoadMore) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                        tvNoData.setVisibility(View.VISIBLE);
                        tvNoData.setText(myUnitsResponce.Message);
                        rvMyUnits.setVisibility(View.GONE);
                    } else {
                        llLoadMore.setVisibility(View.GONE);
                        loading = false;
                        PageNumber = "1";
                    }

                    adapter.notifyDataSetChanged();
                } else if (myUnitsResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(ErrorMessages.ServerError);
                    rvMyUnits.setVisibility(View.GONE);
                    if (!isLoadMore) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                    } else {
                        llLoadMore.setVisibility(View.GONE);
                    }
                    if (adapter != null) {
                        unitsDatas.clear();
                        adapter.notifyDataSetChanged();
                    }
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
     * Check if any unpaid unit is available in cart.
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
     * Get address list from webAPI for show listing in dialog
     */
    public void getAddressList() {

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
                    addressList.clear();
                    for (int i = 0; i < GetAddressResponce.Data.size(); i++) {
                        AddressDetailForList addressDetailForList = new AddressDetailForList();
                        addressDetailForList.Id = GetAddressResponce.Data.get(i).Id;
                        addressDetailForList.Address = GetAddressResponce.Data.get(i).Address;

                        addressDetailForList.StateName = GetAddressResponce.Data.get(i).StateName;
                        addressDetailForList.State = GetAddressResponce.Data.get(i).State;
                        addressDetailForList.CityName = GetAddressResponce.Data.get(i).CityName;
                        addressDetailForList.City = GetAddressResponce.Data.get(i).City;
                        addressDetailForList.ZipCode = GetAddressResponce.Data.get(i).ZipCode;
                        addressDetailForList.AllowDelete = GetAddressResponce.Data.get(i).AllowDelete;
                        addressDetailForList.IsDefaultAddress = GetAddressResponce.Data.get(i).IsDefaultAddress;
                        if (GetAddressResponce.Data.get(i).IsDefaultAddress) {
                            addressDetailForList.IsSelected = true;
                        } else {
                            addressDetailForList.IsSelected = false;
                        }

                        addressList.add(addressDetailForList);
                    }

                } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
     * web API for remove unpaid units from cart
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.e(TAG, "onAttach");
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
        Log.e(TAG, "onStart");
        ((DashboardActivity) activity).last = "my_units";
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.e(TAG, "onViewCreated");
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
}
