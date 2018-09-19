package com.aircall.app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Model.UpcomingSchedual.UpcomingServicesData;
import com.aircall.app.R;

import java.util.ArrayList;
import java.util.List;

public class UpcomingSchedualAdapter extends BaseAdapter {

    Activity activity;
    List<UpcomingServicesData> upcomingServices = new ArrayList<>();
    LayoutInflater inflater;

    public UpcomingSchedualAdapter(Activity activity, List<UpcomingServicesData> upcomingServices) {
        this.activity = activity;
        this.upcomingServices = upcomingServices;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return upcomingServices.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }


    public class ViewHolder {
        TextView tvDiscription, tvTimeSlot, tvScheduleDate;
        LinearLayout llRawHader;

        LinearLayout layout_apponoff;


        public ViewHolder(View item) {

            tvDiscription = (TextView) item.findViewById(R.id.tvDiscription);
            tvTimeSlot = (TextView) item.findViewById(R.id.tvTimeSlot);
            tvScheduleDate = (TextView) item.findViewById(R.id.tvScheduleDate);
            llRawHader = (LinearLayout) item.findViewById(R.id.llRawHader);
        }
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        final ViewHolder holder;

        if (convertView == null) {

            convertView = inflater.inflate(R.layout.raw_month_services_list, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        /**
         * Make visually difference in Service is already scheduled and pending service.
         */
        if (upcomingServices.get(i).Status.equalsIgnoreCase("Scheduled")) {

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                holder.tvTimeSlot.setTextColor(activity.getResources().getColor(R.color.text_light_blue_color));
                holder.tvScheduleDate.setTextColor(activity.getResources().getColor(R.color.text_light_blue_color));
                holder.llRawHader.setBackgroundColor(activity.getResources().getColor(R.color.white));
            } else {
                holder.tvTimeSlot.setTextColor(activity.getResources().getColor(R.color.text_light_blue_color, null));
                holder.tvScheduleDate.setTextColor(activity.getResources().getColor(R.color.text_light_blue_color, null));
                holder.llRawHader.setBackgroundColor(activity.getResources().getColor(R.color.white, null));
            }
            holder.tvTimeSlot.setText(upcomingServices.get(i).ScheduleStartTime + " - " + upcomingServices.get(i).ScheduleEndTime);


        } else if (upcomingServices.get(i).Status.equalsIgnoreCase("Pending")) {

            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
                holder.tvTimeSlot.setTextColor(activity.getResources().getColor(R.color.gray_text_color));
                holder.tvScheduleDate.setTextColor(activity.getResources().getColor(R.color.gray_text_color));
                holder.llRawHader.setBackgroundColor(activity.getResources().getColor(R.color.upcoming_raw_header_back));
            } else {
                holder.tvTimeSlot.setTextColor(activity.getResources().getColor(R.color.gray_text_color, null));
                holder.tvScheduleDate.setTextColor(activity.getResources().getColor(R.color.gray_text_color, null));
                holder.llRawHader.setBackgroundColor(activity.getResources().getColor(R.color.upcoming_raw_header_back, null));
            }

            holder.tvTimeSlot.setText(upcomingServices.get(i).Appoinment);

        }
        holder.tvDiscription.setText(upcomingServices.get(i).EmpName);
        holder.tvScheduleDate.setText(upcomingServices.get(i).ScheduleDate);

        return convertView;
    }

}
