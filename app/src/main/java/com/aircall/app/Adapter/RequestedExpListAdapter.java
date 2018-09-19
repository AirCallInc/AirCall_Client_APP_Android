package com.aircall.app.Adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
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
import com.aircall.app.Model.RequestForServices.GroupingAddress;
import com.aircall.app.Model.RequestForServices.ServiceDeletResponce;
import com.aircall.app.R;

import java.util.HashMap;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zealous on 07/03/17.
 */

public class RequestedExpListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;
    private HashMap<String, List<GroupingAddress>> _listDataChild;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private GlobalClass globalClass;
    private ListOfRequestFragment fragment;
    private String ServiceId = "";

    public RequestedExpListAdapter(Context context, ListOfRequestFragment fragment, SharedPreferences sharedpreferences,
                                   SharedPreferences.Editor editor, GlobalClass globalClass, List<String> listDataHeader,
                                   HashMap<String, List<GroupingAddress>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
        this.editor = editor;
        this.sharedpreferences = sharedpreferences;
        this.globalClass = globalClass;
        this.fragment = fragment;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.raw_for_request_listing, null);
        }

        ImageView ivEdit, ivDelete;
        TextView tvServiceCaseNo, tvServiceDetail;
        LinearLayout llHead, llBottom;

        ivEdit = (ImageView) convertView.findViewById(R.id.ivEdit);
        ivDelete = (ImageView) convertView.findViewById(R.id.ivDelete);
        tvServiceCaseNo = (TextView) convertView.findViewById(R.id.tvServiceCaseNo);
        tvServiceDetail = (TextView) convertView.findViewById(R.id.tvServiceDetail);
        llHead = (LinearLayout) convertView.findViewById(R.id.llHead);
        llBottom = (LinearLayout) convertView.findViewById(R.id.llBottom);

        tvServiceCaseNo.setText(this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition).Address);
        tvServiceDetail.setText(this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition).Message);

        if (!this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition).AllowDelete) {
            llBottom.setVisibility(View.GONE);
        } else {
            llBottom.setVisibility(View.VISIBLE);
        }

        ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment ds = new DoubleButtonAlert(ErrorMessages.DeletedAddress,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                                if (tag.equalsIgnoreCase("yes")) {
                                    requestForServiceDetail(groupPosition, childPosition);
                                } else {

                                }
                            }
                        });
                ds.setCancelable(true);
                ds.show(((Activity) _context).getFragmentManager(), "");

            }
        });

        /**
         * Update request
         */
        ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.callReschedual(groupPosition, childPosition);
            }
        });


        /**
         * Redirect to display services detail
         */

        ServiceId = this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosition).Id;

        llHead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction transaction = ((Activity) _context).getFragmentManager()
                        .beginTransaction();
                Fragment newFragment = new ServiceDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("ServiceId", ServiceId);
                newFragment.setArguments(bundle);
                String strFragmentTag = newFragment.toString();
                transaction.add(R.id.frame_middel, newFragment,
                        strFragmentTag);
                transaction.addToBackStack("");
                transaction.commit();
            }
        });

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_group, null);
        }

        ImageView imgplus, imgminus;
        imgplus = (ImageView) convertView.findViewById(R.id.img_plus);
        imgminus = (ImageView) convertView.findViewById(R.id.img_minus);
        if (isExpanded) {
            imgminus.setVisibility(View.VISIBLE);
            imgplus.setVisibility(View.GONE);
        } else {
            imgminus.setVisibility(View.GONE);
            imgplus.setVisibility(View.VISIBLE);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.tvAddressTitle);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    public void requestForServiceDetail(final int groupposition, final int childposition) {

        ((DashboardActivity) _context).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.deleteRequestedService(sharedpreferences.getString("Id", ""), _listDataChild.get(_listDataHeader.get(groupposition)).get(childposition).Id, new Callback<ServiceDeletResponce>() {
            @Override
            public void success(ServiceDeletResponce serviceDeletResponce, Response response) {
                ((DashboardActivity) _context).hideProgressDialog();

                if (serviceDeletResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!serviceDeletResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", serviceDeletResponce.Token);
                        editor.apply();
                    }
                    //Added
                    _listDataChild.get(_listDataHeader.get(groupposition)).remove(childposition);
//                    requestList.remove(position);  //Removed
                    notifyDataSetChanged();
                    DialogFragment ds = new SingleButtonAlert(serviceDeletResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) _context).getFragmentManager(), "");

                    if (_listDataHeader.size() == 0) {
                        fragment.noRecordAfterDeleteLastData(ErrorMessages.NoRecordFound);
                    }

                } else if (serviceDeletResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    //Added
                    _listDataChild.get(_listDataHeader.get(groupposition)).remove(childposition);
//                    requestList.remove(position);  //Removed
                    notifyDataSetChanged();
                    fragment.noRecordAfterDeleteLastData(ErrorMessages.NoRecordFound);
                    DialogFragment ds = new SingleButtonAlert(serviceDeletResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) _context).getFragmentManager(), "");
                } else if (serviceDeletResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) _context).getFragmentManager(), "");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((DashboardActivity) _context).hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(true);
                ds.show(((Activity) _context).getFragmentManager(), "");

                Log.e("failor", "failor " + error.getMessage());
            }
        });

    }
}
