package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aircall.app.Model.BillingHistory.PartHistoryData;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */

/**
 * BILLING HISTORY DETAIL UNIT LIST ADAPTER
 */

public class BillingHistoryPartsAdapter extends RecyclerView.Adapter<BillingHistoryPartsAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    ArrayList<PartHistoryData> partsHistory = new ArrayList<>();

    public BillingHistoryPartsAdapter(Context context, ArrayList<PartHistoryData> partsHistory) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.partsHistory = partsHistory;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_billing_parts, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, final int position) {
        myHolder = holder;
        myHolder.tvFilterName.setText(partsHistory.get(position).PartName + " " + partsHistory.get(position).Size);
        myHolder.tvAmount.setText("$" + partsHistory.get(position).Amount);
        myHolder.tvQuantity.setText("$" + (partsHistory.get(position).Amount / partsHistory.get(position).Quantity) +
                " x " + partsHistory.get(position).Quantity);
    }

    @Override
    public int getItemCount() {
        return partsHistory.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvFilterName, tvQuantity, tvAmount;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvFilterName = (TextView) itemView.findViewById(R.id.tvFilterName);
            tvQuantity = (TextView) itemView.findViewById(R.id.tvQuantity);
            tvAmount = (TextView) itemView.findViewById(R.id.tvAmount);
        }
    }
}
