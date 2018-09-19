package com.aircall.app.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Model.GetAllPlanType.UnitTypeDetail;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 20/06/16.
 */
public class PlanCoverageAdapter extends RecyclerView.Adapter<PlanCoverageAdapter.PlanViewHolder> {

    private Context self;
    ArrayList<UnitTypeDetail> planCoverageDatas = new ArrayList<>();
    private PlanViewHolder myHolder;
    private ViewGroup parent;

    public PlanCoverageAdapter(Context context, ArrayList<UnitTypeDetail> planCoverageDatas) {
        Log.i("AdapterEmoji", "Init");
        this.self = context;
        this.planCoverageDatas = planCoverageDatas;
        //Log.e("Mobile tons", "" + mobileTones.size());

    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_plan_coverage, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, final int position) {
        myHolder = holder;
//        if (!planCoverageDatas.get(position).Image.trim().equalsIgnoreCase("")) {
//            Picasso.with(self).load(planCoverageDatas.get(position).Image).into(myHolder.ivPlanImage);
//        }
        myHolder.tvPlanName.setText(planCoverageDatas.get(position).PlanName);
        myHolder.tvPlanPrice.setText("Basic Fee $" + planCoverageDatas.get(position).BasicFee + "/Year/Visit");

        myHolder.tvPlanShortDiscription.setText("FeeIncrement $" +planCoverageDatas.get(position).FeeIncrement + " /Year/Visit");

        myHolder.llPlanCoverageContainer.setBackgroundColor(Color.parseColor(planCoverageDatas.get(position).BackgroundColor));
//        if (!planCoverageDatas.get(position).Image.equalsIgnoreCase("")) {
//            Picasso.with(self).load(planCoverageDatas.get(position).Image).into(myHolder.ivPlanImage);
//        }
        ViewGroup.LayoutParams params = myHolder.llPlanCoverageContainer.getLayoutParams();

        params.height = getLayoutHeight();
        myHolder.llPlanCoverageContainer.setLayoutParams(params);

    }

    private int getLayoutHeight() {
        WindowManager wm = (WindowManager) self.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y - dpToPx(60) - getStatusBarHeight();
        return height / 2;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = self.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = self.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = self.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public int getItemCount() {
        return planCoverageDatas.size();
    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private ImageView ivPlanImage;
        private LinearLayout llPlanCoverageContainer;
        private TextView tvPlanName, tvPlanPrice, tvPlanShortDiscription;

        public PlanViewHolder(View itemView) {
            super(itemView);
            //ivPlanImage = (ImageView) itemView.findViewById(R.id.ivPlanImage);
            tvPlanName = (TextView) itemView.findViewById(R.id.tvPlanName);
            tvPlanPrice = (TextView) itemView.findViewById(R.id.tvPlanPrice);
            tvPlanShortDiscription = (TextView) itemView.findViewById(R.id.tvPlanShortDiscription);
            llPlanCoverageContainer = (LinearLayout) itemView.findViewById(R.id.llPlanCoverageContainer);
        }
    }
}
