package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Model.GetAddress.AddressDetailForList;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */

/**
 * ADAPTER FOR ADDRESS SELECTION IN ADD UNIT
 */

public class AddUnitAddressListAdapter extends RecyclerView.Adapter<AddUnitAddressListAdapter.PlanViewHolder> {

    private Context self;
    ArrayList<AddressDetailForList> addressDetail = new ArrayList<>();
    private PlanViewHolder myHolder;
    private ViewGroup parent;

    public AddUnitAddressListAdapter(Context context, ArrayList<AddressDetailForList> addressDetail) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.addressDetail = addressDetail;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_service_reason, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, final int position) {
        myHolder = holder;
        String address = addressDetail.get(position).Address;
        if (addressDetail.get(position).CityName != null) {
            address = address + "\n" + addressDetail.get(position).CityName;
        }
        if (addressDetail.get(position).StateName != null) {
            address = address + ", " + addressDetail.get(position).StateName;
        }
        if (addressDetail.get(position).ZipCode != null) {
            address = address + " " + addressDetail.get(position).ZipCode;
        }
        myHolder.tvUnitName.setText(address);
        if (addressDetail.get(position).IsSelected) {
            myHolder.ivCheckBox.setImageResource(R.drawable.radiobutton_selected);
        } else {
            myHolder.ivCheckBox.setImageResource(R.drawable.radiobutton);
        }

    }

    @Override
    public int getItemCount() {
        return addressDetail.size();
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
