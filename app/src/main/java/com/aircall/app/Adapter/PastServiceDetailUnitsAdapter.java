package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aircall.app.Model.PastServices.PastServiceUnitDetail;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */

/**
 * Calls in Past services detail screen,
 *
 * for listing of units which is served in particular services
 *
 * */

public class PastServiceDetailUnitsAdapter extends RecyclerView.Adapter<PastServiceDetailUnitsAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    ArrayList<PastServiceUnitDetail> myUnits = new ArrayList<>();

    public PastServiceDetailUnitsAdapter(Context context, ArrayList<PastServiceUnitDetail> myUnits) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.myUnits = myUnits;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_last_service_units, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        myHolder.tvUnitName.setText(myUnits.get(position).UnitName);
        myHolder.tvPlanName.setText(myUnits.get(position).PlanName);

    }

    @Override
    public int getItemCount() {
        return myUnits.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUnitName,tvPlanName;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);
            tvUnitName = (TextView) itemView.findViewById(R.id.tvUnitName);
        }
    }
}
