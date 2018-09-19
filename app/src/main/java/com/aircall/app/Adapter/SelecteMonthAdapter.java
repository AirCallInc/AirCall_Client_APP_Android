package com.aircall.app.Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.aircall.app.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jd on 20/06/16.
 */
public class SelecteMonthAdapter extends BaseAdapter {

    Context context;
    List<String> months;

    public SelecteMonthAdapter(Context context, ArrayList<String> months) {
        this.context = context;
        this.months = months;

    }

    private class ViewHolder {
        TextView tvMonth;
    }

    @Override
    public int getCount() {
        return months.size();
    }

    @Override
    public Object getItem(int position) {
        return months.get(position);
    }

    @Override
    public long getItemId(int position) {
        return months.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if( convertView == null){
            convertView = inflater.inflate(R.layout.raw_month, null);
            holder = new ViewHolder();
            holder.tvMonth = (TextView) convertView.findViewById(R.id.tvMonth);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.tvMonth.setText(months.get(position));

        return convertView;
    }
}
