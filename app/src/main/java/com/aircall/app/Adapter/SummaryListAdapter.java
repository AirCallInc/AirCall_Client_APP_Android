package com.aircall.app.Adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.AddUnitActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.DoubleButtonAlert;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Fragment.SummaryFragment;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddUnit.AddUnitResponce;
import com.aircall.app.Model.AddUnit.UnitDataDetail;
import com.aircall.app.R;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jd on 20/06/16.
 */
public class SummaryListAdapter extends RecyclerView.Adapter<SummaryListAdapter.PlanViewHolder> {

    private Context self;
    private PlanViewHolder myHolder;
    private ViewGroup parent;
    private GlobalClass globalClass;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    ArrayList<UnitDataDetail> unitDataDetails = new ArrayList<>();
    SummaryFragment fragment;

    public SummaryListAdapter(Context context, GlobalClass globalClass,
                              SharedPreferences sharedpreferences, SharedPreferences.Editor editor,
                              SummaryFragment fragment, ArrayList<UnitDataDetail> unitDataDetails) {
        this.self = context;
        this.unitDataDetails = unitDataDetails;
        this.sharedPreferences = sharedpreferences;
        this.editor = editor;
        this.globalClass = globalClass;
        this.fragment = fragment;
    }

    @Override
    public PlanViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_for_summary_list, parent, false);
        this.parent = parent;
        return new PlanViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PlanViewHolder holder, final int position) {
        myHolder = holder;
        myHolder.tvSummaryDetailDesc.setText(unitDataDetails.get(position).Description);
        myHolder.tvPlanType.setText(unitDataDetails.get(position).PlanType);
        if(unitDataDetails.get(position).PlanType.equalsIgnoreCase("recurring")) {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                myHolder.tvPlanType.setTextColor(self.getResources().getColor(R.color.black));
            } else {
                myHolder.tvPlanType.setTextColor(self.getResources().getColor(R.color.black,null));
            }
        } else {
            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                myHolder.tvPlanType.setTextColor(self.getResources().getColor(R.color.text_light_blue_color));
            } else {
                myHolder.tvPlanType.setTextColor(self.getResources().getColor(R.color.text_light_blue_color,null));
            }
        }
        myHolder.tvPerice.setText("$" + unitDataDetails.get(position).Price);
        myHolder.tvSummaryPlanName.setText("");//unitDataDetails.get(position).PlanName);
        myHolder.tvAcName.setText(unitDataDetails.get(position).UnitName);
        myHolder.ivRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AddUnitActivity) self).hideProgressDialog();
                DialogFragment ds = new DoubleButtonAlert(ErrorMessages.RemoveSummary,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                if (tag.equalsIgnoreCase("yes")) {
                                    deleteSummary(position);
                                }
                            }
                        });
                ds.setCancelable(true);
                ds.show(((Activity) self).getFragmentManager(), "");
            }
        });

    }

    @Override
    public int getItemCount() {
        return unitDataDetails.size();
    }

    /**
     * Web API call for Delete particular unit from listing
     * @param position
     */
    private void deleteSummary(final int position) {

        ((AddUnitActivity) self).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedPreferences.getString("Token", ""));

        webserviceApi.removeUnit(sharedPreferences.getString("Id", ""), unitDataDetails.get(position).Id, new Callback<AddUnitResponce>() {
            @Override
            public void success(AddUnitResponce getPlanTypesResponce, Response response) {
                ((AddUnitActivity) self).hideProgressDialog();

                if (getPlanTypesResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!getPlanTypesResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getPlanTypesResponce.Token);
                        editor.apply();
                    }
                    fragment.removeUnit(position, getPlanTypesResponce);

                } else if (getPlanTypesResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    if (!getPlanTypesResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getPlanTypesResponce.Token);
                        editor.apply();
                    }
                    fragment.removeUnit(position, getPlanTypesResponce);
                } else if (getPlanTypesResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) self).getFragmentManager(), "");
                } else {
                    ((AddUnitActivity) self).hideProgressDialog();
                    DialogFragment ds = new SingleButtonAlert(getPlanTypesResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) self).getFragmentManager(), "");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((AddUnitActivity) self).hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(true);
                ds.show(((Activity) self).getFragmentManager(), "");

                Log.e("failor", "failor " + error.getMessage());
            }
        });


    }

    public class PlanViewHolder extends RecyclerView.ViewHolder {
        private TextView tvSummaryPlanName, tvPlanType, tvPerice, tvSummaryDetailDesc, tvAcName;
        private ImageView ivRemove;

        public PlanViewHolder(View itemView) {
            super(itemView);
            tvSummaryPlanName = (TextView) itemView.findViewById(R.id.tvSummaryPlanName);
            tvPlanType = (TextView) itemView.findViewById(R.id.tvPlanType);
            tvPerice = (TextView) itemView.findViewById(R.id.tvPerice);
            tvSummaryDetailDesc = (TextView) itemView.findViewById(R.id.tvSummaryDetailDesc);
            tvAcName = (TextView) itemView.findViewById(R.id.tvAcName);
            ivRemove = (ImageView) itemView.findViewById(R.id.ivRemove);
        }
    }
}
