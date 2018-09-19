package com.aircall.app.Adapter;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Model.GetAllState.StateDetail;
import com.aircall.app.R;

import java.util.List;

/**
 * Created by kartik on 21/06/16.
 */
public class SelectStateAdapter extends BaseAdapter {

    Activity context;
    List<StateDetail> rowItems;
    UsernameDialogInteface usernameDialogInteface;

    public SelectStateAdapter(Activity context, UsernameDialogInteface usernameDialogInteface, List<StateDetail> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
        this.usernameDialogInteface = usernameDialogInteface;
    }

    private class ViewHolder {
        TextView txtListState;
        ImageView ivSelected;
        LinearLayout llMain;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.raw_for_get_state, null);
            holder = new ViewHolder();
            holder.txtListState = (TextView) convertView.findViewById(R.id.tvListOfAllState);
            holder.ivSelected = (ImageView) convertView.findViewById(R.id.ivSelected);
            holder.llMain = (LinearLayout) convertView.findViewById(R.id.llMain);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.txtListState.setText(rowItems.get(position).Name);
        if (rowItems.get(position).IsSelected) {
            holder.ivSelected.setVisibility(View.VISIBLE);
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                holder.llMain.setBackgroundColor(context.getResources().getColor(R.color.plan_detail_background));
            } else {
                holder.llMain.setBackgroundColor(context.getResources().getColor(R.color.plan_detail_background, null));
            }
        } else {
            holder.ivSelected.setVisibility(View.GONE);
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                holder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white));
            } else {
                holder.llMain.setBackgroundColor(context.getResources().getColor(R.color.white, null));
            }
        }

        return convertView;
    }
}
