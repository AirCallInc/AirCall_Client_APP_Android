package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Common.RoundedTransformation;
import com.aircall.app.Model.Dashboard.NotificationDetail;
import com.aircall.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */

/**
 * BILLING HISTORY DETAIL UNIT LIST ADAPTER
 */
public class DashboardNotificationListAdapter extends RecyclerView.Adapter<DashboardNotificationListAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    ArrayList<NotificationDetail> notificationDetails = new ArrayList<>();

    public DashboardNotificationListAdapter(Context context, ArrayList<NotificationDetail> notificationDetails) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.notificationDetails = notificationDetails;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_dashboard_notification_list, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, final int position) {
        myHolder = holder;
        myHolder.tvNotification.setText(notificationDetails.get(position).Message);
        if (notificationDetails.get(position).ProfileImage != null &&
                !notificationDetails.get(position).ProfileImage.equalsIgnoreCase("")) {
            Picasso.with(self).load(notificationDetails.get(position).ProfileImage)
                    .transform(new RoundedTransformation(1700, 0)).placeholder(R.drawable.placeholder_img).into(myHolder.ivNotification);
        }
    }

    @Override
    public int getItemCount() {
        return notificationDetails.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNotification;
        private ImageView ivNotification;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvNotification = (TextView) itemView.findViewById(R.id.tvNotification);
            ivNotification = (ImageView) itemView.findViewById(R.id.ivNotification);
        }
    }
}
