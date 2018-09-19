package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Adapter.RequestedExpListAdapter;
import com.aircall.app.Adapter.RequestedServicesListWithAddressAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.RequestForServices.GroupingAddress;
import com.aircall.app.Model.RequestForServices.RequestForServiceListResponce;
import com.aircall.app.R;
import com.aircall.app.RequestForReschedulectivity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ListOfRequestFragment extends Fragment {

    @Bind(R.id.ivBack)
    ImageView ivBack;


    @Bind(R.id.lvExp)
    ExpandableListView lvExp;

    @Bind(R.id.flMain)
    FrameLayout flMain;

    @Bind(R.id.txtRequestNoData)
    TextView txtRequestNoData;

    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    String PurposeOfVisit = "";
    //Remove
    RequestedServicesListWithAddressAdapter requestListAdapter;
    RequestedExpListAdapter reqExpListAdapter;
    RequestForServiceListResponce RequestForServiceListResponce;
    ArrayList<GroupingAddress> properList;
    List<String> listDataHeader;
    HashMap<String, List<GroupingAddress>> listDataChild;
    private int lastExpandedPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list_of_request,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvents();
        return view;
    }

    private void init() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<GroupingAddress>>();
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        ((DashboardActivity) activity).last = "ListOfRequestFragment";
        if (globalClass.checkInternetConnection()) {
            getRequestList();
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

        lvExp.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    lvExp.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        flMain.setOnClickListener(new View.OnClickListener() {
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
    }

    /**
     * Call for Update the selected service
     *
     * @param grpposition,childposition
     */
    public void callReschedual(int grpposition, int childposition) {

        Intent intent = new Intent(activity, RequestForReschedulectivity.class);

        intent.putExtra("ServiceId", listDataChild.get(listDataHeader.get(grpposition)).get(childposition).Id);
        intent.putExtra("PurpusOfVisit", listDataChild.get(listDataHeader.get(grpposition)).get(childposition).PurposeOfVisit);
        intent.putExtra("TotalUnit", Integer.parseInt(listDataChild.get(listDataHeader.get(grpposition)).get(childposition).UnitCount));
        intent.putExtra("ServiceRequestedOn", getDate(listDataChild.get(listDataHeader.get(grpposition)).get(childposition).ServiceRequestedOn));
        intent.putExtra("ServiceRequestedTime", listDataChild.get(listDataHeader.get(grpposition)).get(childposition).ServiceRequestedTime);

        intent.putExtra("FromWhere", "RequestedService");
        startActivityForResult(intent, 1);
    }

    public void noRecordAfterDeleteLastData(String message) {
//        rvForListOfRequest.setVisibility(View.GONE);
        lvExp.setVisibility(View.GONE);
        txtRequestNoData.setVisibility(View.VISIBLE);
        txtRequestNoData.setText(message);
    }

    private String getDate(String dateString) {
        String str[] = dateString.split("T");
        return str[0];
    }

    /**
     * Web API call for get list of already requested service
     */
    public void getRequestList() {
        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        Log.e("client _id==", sharedpreferences.getString("Id", ""));
        webserviceApi.requestForServiceList(sharedpreferences.getString("Id", ""), new Callback<RequestForServiceListResponce>() {
            @Override
            public void success(RequestForServiceListResponce requestForServiceListResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                if (requestForServiceListResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!requestForServiceListResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", requestForServiceListResponce.Token);
                        editor.apply();
                    }
                    RequestForServiceListResponce = requestForServiceListResponce;

                    listDataHeader.clear();
                    listDataChild.clear();

                    //Set Proper Response for Adapter
                    String Address = "";
                    for (int i = 0; i < RequestForServiceListResponce.Data.size(); i++) {
                        properList = new ArrayList<GroupingAddress>();
                        Address = RequestForServiceListResponce.Data.get(i).Address;
                        listDataHeader.add(RequestForServiceListResponce.Data.get(i).Address);
                        for (int j = 0; j < RequestForServiceListResponce.Data.get(i).Services.size(); j++) {
                            GroupingAddress addressData = new GroupingAddress();
                            addressData.Address = Address;
                            Log.e("Address==>", addressData.Address);
                            Address = "";
                            addressData.Id = RequestForServiceListResponce.Data.get(i).Services.get(j).Id;
                            addressData.ServiceCaseNumber = RequestForServiceListResponce.Data.get(i).Services.get(j).ServiceCaseNumber;
                            addressData.ServiceRequestedTime = RequestForServiceListResponce.Data.get(i).Services.get(j).ServiceRequestedTime;
                            addressData.ServiceRequestedOn = RequestForServiceListResponce.Data.get(i).Services.get(j).ServiceRequestedOn;
                            addressData.Message = RequestForServiceListResponce.Data.get(i).Services.get(j).Message;
                            addressData.UnitCount = RequestForServiceListResponce.Data.get(i).Services.get(j).UnitCount;
                            addressData.PurposeOfVisit = RequestForServiceListResponce.Data.get(i).Services.get(j).PurposeOfVisit;
                            addressData.AllowDelete = RequestForServiceListResponce.Data.get(i).Services.get(j).AllowDelete;

                            properList.add(addressData);
                        }
                        listDataChild.put(listDataHeader.get(i), properList);
                        Log.e("proper list size==", properList.size() + "");
                    }

                    reqExpListAdapter = new RequestedExpListAdapter(getActivity(), ListOfRequestFragment.this, sharedpreferences, editor, globalClass, listDataHeader, listDataChild);
                    lvExp.setAdapter(reqExpListAdapter);
                    reqExpListAdapter.notifyDataSetChanged();
                    lvExp.expandGroup(0);
                } else if (requestForServiceListResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    lvExp.setVisibility(View.GONE);
                    txtRequestNoData.setVisibility(View.VISIBLE);
                    txtRequestNoData.setText(requestForServiceListResponce.Message);
                } else if (requestForServiceListResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    lvExp.setVisibility(View.GONE);
                    txtRequestNoData.setVisibility(View.VISIBLE);
                    txtRequestNoData.setText(ErrorMessages.ErrorGettingData);

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
                    lvExp.setVisibility(View.GONE);
                    txtRequestNoData.setVisibility(View.VISIBLE);
                    txtRequestNoData.setText(ErrorMessages.ErrorGettingData);

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
                ((DashboardActivity) getActivity()).hideProgressDialog();
                lvExp.setVisibility(View.GONE);
                txtRequestNoData.setVisibility(View.VISIBLE);
                txtRequestNoData.setText(ErrorMessages.ErrorGettingData);

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == activity.RESULT_OK) {
                String getListData = data.getStringExtra("data");
                if (getListData == null || getListData.equalsIgnoreCase("")) {
                } else {
                    Gson gson = new Gson();
                    RequestForServiceListResponce requestForServiceListResponce = gson.fromJson(getListData, RequestForServiceListResponce.class);
                    RequestForServiceListResponce = requestForServiceListResponce;

                    reqExpListAdapter = new RequestedExpListAdapter(getActivity(), ListOfRequestFragment.this, sharedpreferences, editor, globalClass, listDataHeader, listDataChild);
                    lvExp.setAdapter(reqExpListAdapter);
                    reqExpListAdapter.notifyDataSetChanged();
                    lvExp.expandGroup(0);
                }
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
