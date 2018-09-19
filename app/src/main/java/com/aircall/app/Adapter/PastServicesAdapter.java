package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aircall.app.Model.PastServices.PastServiceListingData;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */


/**
 * Calls in Past services listing screen from drawer,
 *
 * for listing of Past services
 *
 * */

public class PastServicesAdapter extends RecyclerView.Adapter<PastServicesAdapter.PlanViewHolder> {

    private Context self;
    ArrayList<PastServiceListingData> aastServiceList = new ArrayList<>();
    private PlanViewHolder myHolder;
    private ViewGroup parent;

    public PastServicesAdapter(Context context, ArrayList<PastServiceListingData> aastServiceList) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.aastServiceList = aastServiceList;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_for_past_services, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        /*myHolder.tvPlanName.setText(aastServiceList.get(position).PlanName);
        myHolder.tvUnitName.setText(aastServiceList.get(position).UnitName);*/
        if(aastServiceList.get(position).IsNoShow) {
            myHolder.tvServicesSCN.setText(aastServiceList.get(position).ServiceCaseNumber+" (No Show)");
        } else {
            myHolder.tvServicesSCN.setText(aastServiceList.get(position).ServiceCaseNumber);
        }
        myHolder.tvDescription.setText(aastServiceList.get(position).Message);
    }

    @Override
    public int getItemCount() {
        return aastServiceList.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUnitName,tvPlanName,tvServicesSCN,tvDescription;

        public PlanViewHolder(View itemView) {
            super(itemView);
            /*tvUnitName = (TextView) itemView.findViewById(R.id.tvUnitName);
            tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);*/
            tvServicesSCN = (TextView) itemView.findViewById(R.id.tvServicesSCN);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
        }
    }
}
