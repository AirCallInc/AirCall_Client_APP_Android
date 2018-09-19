package com.aircall.app.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Model.GetCityList.CityDetail;
import com.aircall.app.R;

import java.util.List;

/**
 * Created by kartik on 21/06/16.
 */
public class SelectCityAdapter extends BaseAdapter {

    Activity context;
    List<CityDetail> rowItems;
    UsernameDialogInteface usernameDialogInteface;

    String rawId;

    public SelectCityAdapter(Activity context, UsernameDialogInteface usernameDialogInteface, List<CityDetail> rowItems) {
        this.context = context;
        this.rowItems = rowItems;
        this.usernameDialogInteface = usernameDialogInteface;
    }

    private class ViewHolder {
        TextView txtListCity;
        ImageView ivSelected;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if( convertView == null){
            convertView = inflater.inflate(R.layout.raw_for_get_city, null);
            holder = new ViewHolder();
            holder.txtListCity =(TextView) convertView.findViewById(R.id.tvListOfAllCity);
            holder.ivSelected =(ImageView) convertView.findViewById(R.id.ivSelected);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        CityDetail rowItem = rowItems.get(position);

        String strCityName = rowItem.Name;
        String strStateId = rowItem.StateId;
        String strCityId = rowItem.Id;


        holder.txtListCity.setText(strCityName);
        if(rowItem.isSelected) {
            holder.ivSelected.setVisibility(View.VISIBLE);
        } else {
            holder.ivSelected.setVisibility(View.GONE);
        }

        return convertView;
    }


}
