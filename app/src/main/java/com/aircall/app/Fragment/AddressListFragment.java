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
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Adapter.AddressListAdapter;
import com.aircall.app.AddAddressActivity;
import com.aircall.app.AddressListActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.GetAddress.AddressDetailForList;
import com.aircall.app.Model.GetAddress.getAddressResponce;
import com.aircall.app.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AddressListFragment extends Fragment {

    @Bind(R.id.ivAddAddress)
    public ImageView ivAddAddress;

    @Bind(R.id.ivBack)
    public ImageView ivBack;

    @Bind(R.id.rvForAddressListing)
    RecyclerView rvForAddressListing;

    @Bind(R.id.txtAddressNoData)
    TextView txtAddressNoData;

    private ProgressDialogFragment progressDialogFragment;
    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    getAddressResponce GetAddressResponceData;
    Boolean IssetDefaultAddress;

    /**
     * addressListAdapter : Address Listing Adapter
     */
    private ArrayList<AddressDetailForList> addressDetail = new ArrayList<>();
    AddressListAdapter addressListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_list, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvents();

        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        addressListAdapter = new AddressListAdapter(activity, AddressListFragment.this, addressDetail);
        LinearLayoutManager llm1 = new LinearLayoutManager(activity);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rvForAddressListing.setLayoutManager(llm1);
        rvForAddressListing.setAdapter(addressListAdapter);

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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
    }

    private void clickEvents() {

        /**
         * For add address we will use startActivityForResult, so that once it will come back we can get
         * notify from onActivityResult and reload new data in listing.
         */
        ivAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddAddressActivity.class);
                intent.putExtra("AddMoreAddress", "Add Address");
                startActivityForResult(intent, 3);
            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((AddressListActivity) activity).globalClass;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getAddressList();
    }

    /**
     * Web API call for get list of address and set in RecyclerView
     */
    public void getAddressList() {

        ((AddressListActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getAllAddress(sharedpreferences.getString("Id", ""), new Callback<getAddressResponce>() {

            @Override
            public void success(getAddressResponce GetAddressResponce, Response response) {
                ((AddressListActivity) getActivity()).hideProgressDialog();
                Log.e("Address List==>",GetAddressResponce.toString());

                if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!GetAddressResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", GetAddressResponce.Token);
                        editor.apply();
                    }
                    addressDetail.clear();
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

                        addressDetail.add(addressDetailForList);
                    }
                    addressListAdapter.notifyDataSetChanged();
                    rvForAddressListing.setVisibility(View.VISIBLE);
                    txtAddressNoData.setVisibility(View.GONE);

                } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    rvForAddressListing.setVisibility(View.GONE);
                    txtAddressNoData.setVisibility(View.VISIBLE);
                    txtAddressNoData.setText(GetAddressResponce.Message);

                } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    rvForAddressListing.setVisibility(View.GONE);
                    txtAddressNoData.setVisibility(View.VISIBLE);
                    txtAddressNoData.setText(ErrorMessages.ErrorGettingData);

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
                    rvForAddressListing.setVisibility(View.GONE);
                    txtAddressNoData.setVisibility(View.VISIBLE);
                    txtAddressNoData.setText(ErrorMessages.ErrorGettingData);

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
                ((AddressListActivity) getActivity()).hideProgressDialog();
                rvForAddressListing.setVisibility(View.GONE);
                txtAddressNoData.setVisibility(View.VISIBLE);
                txtAddressNoData.setText(ErrorMessages.ErrorGettingData);

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


    /**
     * Method calls from adapter after update default address and it will notifyDataSetChanged
     * @param position
     * @param GetAddressResponce Response of update address API call
     */
    public void updateLIst(int position, getAddressResponce GetAddressResponce) {

        addressDetail.clear();
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

            addressDetail.add(addressDetailForList);
        }
        addressListAdapter.notifyDataSetChanged();
    }


}
