package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Adapter.ServiceDetailUnitsAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.RequestForServices.ServiceDetailResponce;
import com.aircall.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ServiceDetailFragment extends Fragment {

    @Bind(R.id.tvAddress)
    TextView tvAddress;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.tvTime)
    TextView tvTime;

    @Bind(R.id.tvDate)
    TextView tvDate;

    @Bind(R.id.tvReason)
    TextView tvReason;

    @Bind(R.id.tvNote)
    TextView tvNote;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.rvUnits)
    RecyclerView rvUnits;

    private GlobalClass globalClass;
    private Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private String ServiceId = "";
    private ServiceDetailUnitsAdapter unitAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_detail,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvents();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        ServiceId = getArguments().getString("ServiceId");
        LinearLayoutManager llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvUnits.setLayoutManager(llm);
        if (globalClass.checkInternetConnection()) {
            requestForServiceDetail();
        } else {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {
                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
        }
    }

    private void clickEvents() {
        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
    }

    /**
     * Call webAPI to get detail of service
     */
    public void requestForServiceDetail() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.requestForServiceDetail(sharedpreferences.getString("Id", ""), ServiceId, new Callback<ServiceDetailResponce>() {
            @Override
            public void success(ServiceDetailResponce serviceDetailResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();

                if (serviceDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!serviceDetailResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", serviceDetailResponce.Token);
                        editor.apply();
                    }
                    if (serviceDetailResponce.Data != null) {
                        tvAddress.setText(serviceDetailResponce.Data.Address.Address + "\n" + serviceDetailResponce.Data.Address.StateName + ", " +
                                serviceDetailResponce.Data.Address.CityName+" "+serviceDetailResponce.Data.Address.ZipCode);
                        tvDate.setText(serviceDetailResponce.Data.ServiceRequestedOn);
                        tvTime.setText(serviceDetailResponce.Data.ServiceRequestedTime);
                        tvReason.setText(serviceDetailResponce.Data.PurposeOfVisit);
                        tvNote.setText(serviceDetailResponce.Data.Notes);
                        unitAdapter = new ServiceDetailUnitsAdapter(activity,serviceDetailResponce.Data.Units);
                        rvUnits.setAdapter(unitAdapter);
                    }
                } else if (serviceDetailResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                } else {
                    DialogFragment ds = new SingleButtonAlert(serviceDetailResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");

                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(true);
                ds.show(getFragmentManager(), "");

                Log.e("failor", "failor " + error.getMessage());
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;

    }
}
