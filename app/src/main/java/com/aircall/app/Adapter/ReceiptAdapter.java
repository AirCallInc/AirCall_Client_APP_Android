package com.aircall.app.Adapter;

import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Model.Receipt.ReceiptUnitDetail;
import com.aircall.app.R;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */
public class ReceiptAdapter extends RecyclerView.Adapter<ReceiptAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private String From;
    private ViewGroup parent;
    ArrayList<ReceiptUnitDetail> Units = new ArrayList<>();

    public ReceiptAdapter(Context context, ArrayList<ReceiptUnitDetail> Units, String From) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.Units = Units;
        this.From = From;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_receipt, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        myHolder.tvPrice.setText("$"+Units.get(position).Amount);
        myHolder.tvPlanType.setText(Units.get(position).PlanName);
        myHolder.tvUnitName.setText(Units.get(position).UnitName);
        myHolder.tvPaymentType.setText("("+Units.get(position).PlanType+")");

        if(From == null) {
            myHolder.llProcess.setVisibility(View.VISIBLE);
            if (!Units.get(position).payment_status.equalsIgnoreCase("In Process")) {
                myHolder.progress_wheel.setVisibility(View.GONE);
                if (Units.get(position).payment_status.equalsIgnoreCase("Received")) {
                    myHolder.tvError.setVisibility(View.GONE);
                    myHolder.ivFailed.setVisibility(View.GONE);
                    myHolder.ivSuccess.setVisibility(View.VISIBLE);
                    myHolder.tvStatus.setText("Received");
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        myHolder.tvStatus.setTextColor(self.getResources().getColor(R.color.text_light_blue_color));
                    } else {
                        myHolder.tvStatus.setTextColor(self.getResources().getColor(R.color.text_light_blue_color, null));
                    }
                } else {
                    myHolder.tvError.setVisibility(View.VISIBLE);
                    myHolder.ivFailed.setVisibility(View.VISIBLE);
                    myHolder.ivSuccess.setVisibility(View.GONE);
                    myHolder.tvError.setText(Units.get(position).StripeError);
                    myHolder.tvStatus.setText("Failed");
                    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                        myHolder.tvStatus.setTextColor(self.getResources().getColor(R.color.notification_count_text));
                    } else {
                        myHolder.tvStatus.setTextColor(self.getResources().getColor(R.color.notification_count_text, null));
                    }
                }
            } else {
                myHolder.tvError.setVisibility(View.GONE);
                myHolder.ivFailed.setVisibility(View.GONE);
                myHolder.ivSuccess.setVisibility(View.GONE);
                myHolder.progress_wheel.setVisibility(View.VISIBLE);
                myHolder.tvStatus.setText("In Process");
                if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                    myHolder.tvStatus.setTextColor(self.getResources().getColor(R.color.gray_text_color));
                } else {
                    myHolder.tvStatus.setTextColor(self.getResources().getColor(R.color.gray_text_color, null));
                }
            }
        } else {
            myHolder.llProcess.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return Units.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPrice,tvPlanType,tvUnitName,tvStatus,tvError,tvPaymentType;
        private ProgressWheel progress_wheel;
        private ImageView ivSuccess,ivFailed;
        private LinearLayout llProcess;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvPrice = (TextView) itemView.findViewById(R.id.tvPrice);
            tvPaymentType = (TextView) itemView.findViewById(R.id.tvPaymentType);
            tvPlanType = (TextView) itemView.findViewById(R.id.tvPlanType);
            tvUnitName = (TextView) itemView.findViewById(R.id.tvUnitName);
            tvStatus = (TextView) itemView.findViewById(R.id.tvStatus);
            tvError = (TextView) itemView.findViewById(R.id.tvError);
            progress_wheel = (ProgressWheel) itemView.findViewById(R.id.progress_wheel);
            ivFailed = (ImageView) itemView.findViewById(R.id.ivFailed);
            ivSuccess = (ImageView) itemView.findViewById(R.id.ivSuccess);
            llProcess = (LinearLayout) itemView.findViewById(R.id.llProcess);
        }
    }
}
