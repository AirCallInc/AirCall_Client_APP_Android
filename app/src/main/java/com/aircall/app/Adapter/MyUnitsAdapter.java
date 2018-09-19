package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aircall.app.Model.MyUnits.MyUnitsData;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */

/**
 * Calls in My Units screen,
 *
 * for listing of units which is added by clients
 *
 * */

public class MyUnitsAdapter extends RecyclerView.Adapter<MyUnitsAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    ArrayList<MyUnitsData> myUnits = new ArrayList<>();

    public MyUnitsAdapter(Context context, ArrayList<MyUnitsData> myUnits) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.myUnits = myUnits;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_for_my_units, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        myHolder.tvStatus.setText(myUnits.get(position).StatusDesc);
        myHolder.tvPlan.setText(myUnits.get(position).PlanName);
        myHolder.tvUnitName.setText(myUnits.get(position).UnitName);

    }

    @Override
    public int getItemCount() {
        return myUnits.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvStatus,tvPlan,tvUnitName;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvPlan = (TextView) itemView.findViewById(R.id.tvPlan);
            tvUnitName = (TextView) itemView.findViewById(R.id.tvUnitName);
        }
    }
}
