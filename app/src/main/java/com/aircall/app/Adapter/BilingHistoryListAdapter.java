package com.aircall.app.Adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Fragment.BillingHistoryFragment;
import com.aircall.app.Model.BillingHistory.BillingHistorydData;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Jd on 21/06/16.
 */

/**
 * ADAPTER FOR BILLING HISTORY
 */

public class BilingHistoryListAdapter extends RecyclerView.Adapter<BilingHistoryListAdapter.BillingHistoryViewHolder> {

    private Context self;
    ArrayList<BillingHistorydData> billingHistorylist = new ArrayList<>();
    BillingHistoryViewHolder myHolder;
    private ViewGroup parent;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GlobalClass globalClass;
    FragmentManager fm;
    BillingHistoryFragment fragment;
    private ProgressDialogFragment progressDialogFragment;
    String strId, strPlanName, strPurchasedAmount, strTransactionDate, strTransactionId, strUnitName, strServiceCaseNumber;
    String finalBillingDate, finalBillingTime;

    public BilingHistoryListAdapter(Context context, BillingHistoryFragment fragment,
                                    ArrayList<BillingHistorydData> billingHistorylist) {
        Log.i("Adapterlist", "Init");
        this.self = context;
        this.billingHistorylist = billingHistorylist;
        this.fragment = fragment;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        globalClass = ((UserProfileActivity) self).globalClass;
    }

    @Override
    public BillingHistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_recyclerview_billing_history, parent, false);
        this.parent = parent;

        sharedpreferences = self.getSharedPreferences("AircallData", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        return new BillingHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BillingHistoryViewHolder holder, final int position) {

        final Context context = parent.getContext();
        fm = ((Activity) context).getFragmentManager();

        String DateForBilling = billingHistorylist.get(position).TransactionDate;
        String[] separated = DateForBilling.split("T");
        String strBillingDate = separated[0];
        String strBillingTime = separated[1];
        strBillingTime = strBillingTime.replace("Z", "");

        SimpleDateFormat inputDate = new SimpleDateFormat("yy-MM-dd");
        SimpleDateFormat outputDate = new SimpleDateFormat("MMM dd, yyyy");

        SimpleDateFormat inputTime = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat outputTime = new SimpleDateFormat("hh:mm a");

        try {
            Date billingDate = inputDate.parse(strBillingDate);
            finalBillingDate = outputDate.format(billingDate);

            Date billingTime = inputTime.parse(strBillingTime);
            finalBillingTime = outputTime.format(billingTime);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        myHolder = holder;
        myHolder.tvBillingHistoryPlanType.setText(billingHistorylist.get(position).PlanName);
        myHolder.tvBillingHistoryAmount.setText("$" + billingHistorylist.get(position).PurchasedAmount);
        myHolder.tvBillingHistoryDateTime.setText(finalBillingDate + ", " + finalBillingTime);

        if (billingHistorylist.get(position).IsPaid) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                holder.tvBillingHistoryAmount.setTextColor(self.getResources().getColor(R.color.black));
                holder.tvBillingHistoryPlanType.setTextColor(self.getResources().getColor(R.color.black));
                holder.tvBillingHistoryDateTime.setTextColor(self.getResources().getColor(R.color.gray_text_color));
            } else {
                holder.tvBillingHistoryAmount.setTextColor(self.getResources().getColor(R.color.black, null));
                holder.tvBillingHistoryPlanType.setTextColor(self.getResources().getColor(R.color.black, null));
                holder.tvBillingHistoryDateTime.setTextColor(self.getResources().getColor(R.color.gray_text_color, null));
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                holder.tvBillingHistoryAmount.setTextColor(self.getResources().getColor(R.color.notification_count_text));
                holder.tvBillingHistoryPlanType.setTextColor(self.getResources().getColor(R.color.notification_count_text));
                holder.tvBillingHistoryDateTime.setTextColor(self.getResources().getColor(R.color.notification_count_text));
            } else {
                holder.tvBillingHistoryAmount.setTextColor(self.getResources().getColor(R.color.notification_count_text, null));
                holder.tvBillingHistoryPlanType.setTextColor(self.getResources().getColor(R.color.notification_count_text, null));
                holder.tvBillingHistoryDateTime.setTextColor(self.getResources().getColor(R.color.notification_count_text, null));
            }
        }

        strId = billingHistorylist.get(position).Id;
        strPlanName = billingHistorylist.get(position).PlanName;
        strPurchasedAmount = billingHistorylist.get(position).PurchasedAmount;
        strTransactionDate = billingHistorylist.get(position).TransactionDate;
        strTransactionId = billingHistorylist.get(position).TransactionId;
        strUnitName = billingHistorylist.get(position).UnitName;
        strServiceCaseNumber = billingHistorylist.get(position).ServiceCaseNumber;
    }

    @Override
    public int getItemCount() {
        return billingHistorylist.size();
    }

    public class BillingHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvBillingHistoryAmount;
        TextView tvBillingHistoryPlanType;
        TextView tvBillingHistoryDateTime;
        LinearLayout rawBillingHistoryList;

        public BillingHistoryViewHolder(View itemView) {
            super(itemView);
            tvBillingHistoryAmount = (TextView) itemView.findViewById(R.id.tvBillingHistoryAmount);
            tvBillingHistoryPlanType = (TextView) itemView.findViewById(R.id.tvBillingHistoryPlanType);
            tvBillingHistoryDateTime = (TextView) itemView.findViewById(R.id.tvBillingHistoryDateTime);
            rawBillingHistoryList = (LinearLayout) itemView.findViewById(R.id.rawBillingHistoryList);
        }
    }

    public void showProgressDailog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        progressDialogFragment.show(fm, "");
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
