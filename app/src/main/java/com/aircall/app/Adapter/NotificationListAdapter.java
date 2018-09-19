package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.RoundedTransformation;
import com.aircall.app.Model.NotificationListing.NotificationListData;
import com.aircall.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */

/**
 * Calls in Notification list screen,
 *
 * for listing of Notifications
 *
 * */

public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    ArrayList<NotificationListData> notificationListDatas = new ArrayList<>();

    public NotificationListAdapter(Context context, ArrayList<NotificationListData> notificationListDatas) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.notificationListDatas = notificationListDatas;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_for_notifications, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        myHolder.tvNotificationsDesc.setText(notificationListDatas.get(position).Message);
        myHolder.tvNotificationsDate.setText(notificationListDatas.get(position).DateTime);
        if(notificationListDatas.get(position).ProfileImage!=null && !notificationListDatas.get(position).ProfileImage.equalsIgnoreCase("")) {
            Picasso.with(self).load(notificationListDatas.get(position).ProfileImage).noFade().resize(150, 150)
                    .transform(new RoundedTransformation(1700, 0)).placeholder(R.drawable.placeholder_img).into(myHolder.ivNotificationsImage);
        }
        if(notificationListDatas.get(position).Status.equalsIgnoreCase("UnRead")) {
            myHolder.llMain.setBackgroundResource(R.color.upcoming_raw_footer_back);
        } else {
            myHolder.llMain.setBackgroundResource(R.color.white);
        }

    }

    @Override
    public int getItemCount() {
        return notificationListDatas.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvNotificationsDesc,tvNotificationsDate;
        private ImageView ivNotificationsImage;
        private LinearLayout llMain;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvNotificationsDesc = (TextView) itemView.findViewById(R.id.tvNotificationsDesc);
            tvNotificationsDate = (TextView) itemView.findViewById(R.id.tvNotificationsDate);
            ivNotificationsImage = (ImageView) itemView.findViewById(R.id.ivNotificationsImage);
            llMain = (LinearLayout) itemView.findViewById(R.id.llMain);
        }
    }
}
