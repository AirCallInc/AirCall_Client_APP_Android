package com.aircall.app.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aircall.app.Model.Parts.PartsFilter;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */


/**
 * Calls in add unit screen,
 *
 * for listing of filter in selectFilterSizeDialogFragment
 *
 * */
public class FilterListAdapter extends RecyclerView.Adapter<FilterListAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    ArrayList<PartsFilter> partList = new ArrayList<>();

    public FilterListAdapter(Context context, ArrayList<PartsFilter> partList) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.partList = partList;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_select_filter, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder,final int position) {
        myHolder = holder;
        myHolder.tvPartName.setText(partList.get(position).PartName);
        myHolder.tvPartSize.setText(partList.get(position).Size);

    }

    @Override
    public int getItemCount() {
        return partList.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPartName,tvPartSize;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvPartName = (TextView) itemView.findViewById(R.id.tvPartName);
            tvPartSize = (TextView) itemView.findViewById(R.id.tvPartSize);
        }
    }
}
