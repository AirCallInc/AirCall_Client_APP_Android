package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Model.GetAllPlanType.UnitTypeDetailForList;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */
public class PlanTypeAdapter extends RecyclerView.Adapter<PlanTypeAdapter.PlanViewHolder> {

    private Context self;
    ArrayList<UnitTypeDetailForList> unitTypeDetail = new ArrayList<>();
    private PlanViewHolder myHolder;
    private ViewGroup parent;

    public PlanTypeAdapter(Context context, ArrayList<UnitTypeDetailForList> unitTypeDetail) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.unitTypeDetail = unitTypeDetail;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_service_reason, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        myHolder.tvUnitName.setText(unitTypeDetail.get(position).Name);
        if(unitTypeDetail.get(position).isSelected) {
            myHolder.ivCheckBox.setImageResource(R.drawable.radiobutton_selected);
        } else {
            myHolder.ivCheckBox.setImageResource(R.drawable.radiobutton);

        }

    }

    @Override
    public int getItemCount() {
        return unitTypeDetail.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivCheckBox;
        private TextView tvUnitName;

        public PlanViewHolder(View itemView) {
            super(itemView);
            ivCheckBox = (ImageView) itemView.findViewById(R.id.ivCheckBox);
            tvUnitName = (TextView) itemView.findViewById(R.id.tvUnitName);
        }
    }
}
