package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aircall.app.Model.RequestForServices.UnitsData;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */
public class ServiceDetailUnitsAdapter extends RecyclerView.Adapter<ServiceDetailUnitsAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    ArrayList<UnitsData> myUnits = new ArrayList<>();

    public ServiceDetailUnitsAdapter(Context context, ArrayList<UnitsData> myUnits) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.myUnits = myUnits;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_service_detail_units, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        myHolder.tvUnitName.setText(myUnits.get(position).UnitName);

    }

    @Override
    public int getItemCount() {
        return myUnits.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUnitName;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvUnitName = (TextView) itemView.findViewById(R.id.tvUnitName);
        }
    }
}
