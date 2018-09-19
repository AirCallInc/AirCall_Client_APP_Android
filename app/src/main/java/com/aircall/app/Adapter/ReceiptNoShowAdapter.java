package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aircall.app.Model.NoShowNotification.NoShowPaymentData;
import com.aircall.app.R;

/**
 * Created by jd on 20/06/16.
 */
public class ReceiptNoShowAdapter extends RecyclerView.Adapter<ReceiptNoShowAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    NoShowPaymentData data;

    public ReceiptNoShowAdapter(Context context, NoShowPaymentData data) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.data = data;
        //Log.e("Mobile tons", "" + mobileTones.size());
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_receipt_no_show, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        myHolder.tvPrice.setText("$"+data.NoShowAmount);
        myHolder.tvServiceCaseNo.setText(data.ServiceCaseNumber);
    }

    @Override
    public int getItemCount() {
        return 1;
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPrice,tvServiceCaseNo;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvServiceCaseNo = (TextView) itemView.findViewById(R.id.tvServiceCaseNo);
        }
    }
}
