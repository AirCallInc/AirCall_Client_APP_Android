package com.aircall.app.Adapter;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Fragment.UnitDetailFragment;
import com.aircall.app.Model.Dashboard.UnitDetails;
import com.aircall.app.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/** ADAPTER FOR DASHBOARD VIEW PAGER */

public class AcListPagerAdapter extends PagerAdapter {
    Activity activity;
    ArrayList<UnitDetails> units = new ArrayList();

    public AcListPagerAdapter(Activity act, ArrayList<UnitDetails> units) {
        this.units = units;
        activity = act;
//		this.ll_myfivepanelpager = ll_myfivepanelpager;
    }

    public int getCount() {
        return units.size();
    }

    public Object instantiateItem(View collection,final int position) {

        LayoutInflater inflater = (LayoutInflater) collection.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = null;
        view = inflater.inflate(R.layout.pager_raw, null);

        ImageView ivStatus = (ImageView) view.findViewById(R.id.ivStatus);
        TextView tvUnitName = (TextView) view.findViewById(R.id.tvUnitName);
        TextView tvUnitStatus = (TextView) view.findViewById(R.id.tvUnitStatus);

        tvUnitName.setText(units.get(position).UnitName);
        tvUnitStatus.setText(units.get(position).Status);
        if(units.get(position).Status.equalsIgnoreCase("Service Soon")) {
            Picasso.with(activity).load(R.drawable.error_icon).into(ivStatus);
        } else if(units.get(position).Status.equalsIgnoreCase("Need Repair")) {
            Picasso.with(activity).load(R.drawable.close_btn).into(ivStatus);
        } else {
            Picasso.with(activity).load(R.drawable.right_btn).into(ivStatus);
        }

        ((ViewPager) collection).addView(view, 0);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentTransaction transaction = activity.getFragmentManager()
                        .beginTransaction();
                Fragment newFragment = new UnitDetailFragment();

                Bundle bundle = new Bundle();
                bundle.putString("UnitId", units.get(position).UnitId);
                newFragment.setArguments(bundle);

                String strFragmentTag = newFragment.toString();
                transaction.add(R.id.frame_middel, newFragment,
                        strFragmentTag);
                transaction.addToBackStack("");
                transaction.commit();
            }
        });

        return view;

    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}