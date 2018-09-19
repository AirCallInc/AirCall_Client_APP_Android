package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Model.MyUnits.MyUnitsData;
import com.aircall.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */
public class UnitsSelectionAdapter extends RecyclerView.Adapter<UnitsSelectionAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    ArrayList<MyUnitsData> myUnits = new ArrayList<>();

    public UnitsSelectionAdapter(Context context, ArrayList<MyUnitsData> myUnits) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.myUnits = myUnits;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_select_unit, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, final int position) {
        myHolder = holder;
        myHolder.tvUnitName.setText(myUnits.get(position).UnitName);
        if (myUnits.get(position).isSelected) {
            Picasso.with(self).load(R.drawable.checkbox_selected).into(myHolder.ivCheckBox);
        } else {
            Picasso.with(self).load(R.drawable.checkbox).into(myHolder.ivCheckBox);
        }
    }

    @Override
    public int getItemCount() {
        return myUnits.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUnitName;
        private ImageView ivCheckBox;

        public PlanViewHolder(View itemView) {
            super(itemView);
            ivCheckBox = (ImageView) itemView.findViewById(R.id.ivCheckBox);
            tvUnitName = (TextView) itemView.findViewById(R.id.tvUnitName);
        }
    }
}