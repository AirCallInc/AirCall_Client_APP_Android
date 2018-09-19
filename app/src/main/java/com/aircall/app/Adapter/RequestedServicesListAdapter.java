package com.aircall.app.Adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.DoubleButtonAlert;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Fragment.ListOfRequestFragment;
import com.aircall.app.Fragment.ServiceDetailFragment;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.RequestForServices.RequestForServiceListData;
import com.aircall.app.Model.RequestForServices.ServiceDeletResponce;
import com.aircall.app.R;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jd on 20/06/16.
 */
public class RequestedServicesListAdapter extends RecyclerView.Adapter<RequestedServicesListAdapter.PlanViewHolder> {

    private Context self;
    ArrayList<RequestForServiceListData> requestList = new ArrayList<>();
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private GlobalClass globalClass;
    private ListOfRequestFragment fragment;

    public RequestedServicesListAdapter(Context context, ListOfRequestFragment fragment, SharedPreferences sharedpreferences, SharedPreferences.Editor editor, GlobalClass globalClass, ArrayList<RequestForServiceListData> requestList) {
        this.self = context;
        this.requestList = requestList;
        this.editor = editor;
        this.sharedpreferences = sharedpreferences;
        this.globalClass = globalClass;
        this.fragment = fragment;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_for_request_listing, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, final int position) {
        myHolder = holder;
        myHolder.tvServiceCaseNo.setText(requestList.get(position).Address);
        myHolder.tvServiceDetail.setText(requestList.get(position).Message);
        if (!requestList.get(position).AllowDelete) {
            myHolder.llBottom.setVisibility(View.GONE);
        } else {
            myHolder.llBottom.setVisibility(View.VISIBLE);
        }

        /**
         * Delete request
         */
        myHolder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment ds = new DoubleButtonAlert(ErrorMessages.DeletedAddress,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                                if (tag.equalsIgnoreCase("yes")) {
                                    requestForServiceDetail(position);
                                } else {

                                }
                            }
                        });
                ds.setCancelable(true);
                ds.show(((Activity) self).getFragmentManager(), "");

            }
        });

        /**
         * Update request
         */
        myHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                fragment.callReschedual(position);
                fragment.callReschedual(position,0);
                //0 Added temporary for expandble listview testing
            }
        });

        /**
         * Redirect to display services detail
         */
        myHolder.llHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = ((Activity) self).getFragmentManager()
                        .beginTransaction();
                Fragment newFragment = new ServiceDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ServiceId", requestList.get(position).Id);
                newFragment.setArguments(bundle);
                String strFragmentTag = newFragment.toString();
                transaction.add(R.id.frame_middel, newFragment,
                        strFragmentTag);
                transaction.addToBackStack("");
                transaction.commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivEdit, ivDelete;
        private TextView tvServiceCaseNo, tvServiceDetail,tvAddressTitle;
        private LinearLayout llHead, llBottom,ll_address;

        public PlanViewHolder(View itemView) {
            super(itemView);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            ivDelete = (ImageView) itemView.findViewById(R.id.ivDelete);
            tvServiceCaseNo = (TextView) itemView.findViewById(R.id.tvServiceCaseNo);
            tvServiceDetail = (TextView) itemView.findViewById(R.id.tvServiceDetail);
            llHead = (LinearLayout) itemView.findViewById(R.id.llHead);
            llBottom = (LinearLayout) itemView.findViewById(R.id.llBottom);
            tvAddressTitle = (TextView)itemView.findViewById(R.id.tvAddressTitle);
            ll_address = (LinearLayout)itemView.findViewById(R.id.ll_address);
        }
    }

    public void requestForServiceDetail(final int position) {

        ((DashboardActivity) self).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.deleteRequestedService(sharedpreferences.getString("Id", ""), requestList.get(position).Id, new Callback<ServiceDeletResponce>() {
            @Override
            public void success(ServiceDeletResponce serviceDeletResponce, Response response) {
                ((DashboardActivity) self).hideProgressDialog();

                if (serviceDeletResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!serviceDeletResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", serviceDeletResponce.Token);
                        editor.apply();
                    }
                    requestList.remove(position);
                    notifyDataSetChanged();
                    DialogFragment ds = new SingleButtonAlert(serviceDeletResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) self).getFragmentManager(), "");

                    if (requestList.size() == 0) {
                        fragment.noRecordAfterDeleteLastData(ErrorMessages.NoRecordFound);
                    }

                } else if (serviceDeletResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    requestList.remove(position);
                    notifyDataSetChanged();
                    fragment.noRecordAfterDeleteLastData(ErrorMessages.NoRecordFound);
                    DialogFragment ds = new SingleButtonAlert(serviceDeletResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) self).getFragmentManager(), "");
                } else if (serviceDeletResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) self).getFragmentManager(), "");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((DashboardActivity) self).hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(true);
                ds.show(((Activity) self).getFragmentManager(), "");

            }
        });

    }


}
