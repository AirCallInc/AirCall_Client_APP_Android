package com.aircall.app.Adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Model.GetAddress.AddressDetailForList;
import com.aircall.app.R;

import java.util.List;

/**
 * Created by kartik on 21/06/16.
 */
public class SelectAddressAdapter extends BaseAdapter {

    Activity context;
    List<AddressDetailForList> addressList;
    UsernameDialogInteface usernameDialogInteface;

    public SelectAddressAdapter(Activity context, UsernameDialogInteface usernameDialogInteface, List<AddressDetailForList> addressList) {
        this.context = context;
        this.addressList = addressList;
        this.usernameDialogInteface = usernameDialogInteface;
    }

    private class ViewHolder {
        TextView txtAddress;
        TextView txtState;
        TextView txtCity;
        TextView txtZipcode;
        ImageView ivIsDefaultAddress;
        LinearLayout lladdressClick;
    }

    @Override
    public int getCount() {
        return addressList.size();
    }

    @Override
    public Object getItem(int position) {
        return addressList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return addressList.indexOf(getItem(position));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if( convertView == null){
            convertView = inflater.inflate(R.layout.raw_select_address, null);
            holder = new ViewHolder();
            holder.txtAddress = (TextView) convertView.findViewById(R.id.tvListOfAddress);
            holder.txtState = (TextView) convertView.findViewById(R.id.tvListOfAddressState);
            holder.txtCity = (TextView) convertView.findViewById(R.id.tvListOfAddressCity);
            holder.txtZipcode = (TextView) convertView.findViewById(R.id.tvListOfAddressZipcode);
            holder.ivIsDefaultAddress = (ImageView) convertView.findViewById(R.id.ivIsDefaultAddress);
            holder.lladdressClick = (LinearLayout) convertView.findViewById(R.id.lladdressClick);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        holder.txtAddress.setText(addressList.get(position).Address);
        holder.txtCity.setText(addressList.get(position).CityName + ",");
        holder.txtState.setText(addressList.get(position).StateName + ",");
        holder.txtZipcode.setText(addressList.get(position).ZipCode);

        if(addressList.get(position).IsSelected) {
            holder.ivIsDefaultAddress.setVisibility(View.VISIBLE);
            holder.lladdressClick.setBackgroundResource(R.color.default_address_color);
        } else {
            holder.ivIsDefaultAddress.setVisibility(View.INVISIBLE);
            holder.lladdressClick.setBackgroundResource(R.color.white);
        }

        return convertView;
    }
}
