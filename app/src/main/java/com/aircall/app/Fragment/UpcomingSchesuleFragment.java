package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.aircall.app.Adapter.UpcomingSchedualAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.UpcomingSchedual.UpcomingServicesData;
import com.aircall.app.Model.UpcomingSchedual.UpcomingServicesResponce;
import com.aircall.app.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class UpcomingSchesuleFragment extends Fragment {

    @Bind(R.id.lvMonthServicesList)
    ListView lvMonthServicesList;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.ivNavDrawer)
    public ImageView ivNavDrawer;

    @Bind(R.id.tvNoData)
    TextView tvNoData;

    GlobalClass globalClass;
    Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;

    /**
     * Adapter for Upcoming Services data
     */
    private UpcomingSchedualAdapter adapter;
    ArrayList<UpcomingServicesData> upcomingServices = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_schedule,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvents();


        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        if (!globalClass.checkInternetConnection()) {

            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");

        } else {
            upcomingServices();
        }
    }

    private void clickEvents() {

        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).openDrawer();
            }
        });

        /**
         * Scheduled service will be only clickable
         */
        lvMonthServicesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(upcomingServices.get(position).Status.equalsIgnoreCase("Scheduled")) {
                    FragmentTransaction transaction = getFragmentManager()
                            .beginTransaction();
                    Fragment newFragment = new UpcomingSchedualeDetailFragment();

                    Bundle bundle = new Bundle();
                    bundle.putString("ServiceId", upcomingServices.get(position).Id);
                    newFragment.setArguments(bundle);
                    String strFragmentTag = newFragment.toString();
                    transaction.add(R.id.frame_middel, newFragment,
                            strFragmentTag);
                    transaction.addToBackStack("");
                    transaction.commit();
                }
            }
        });
    }

    /**
     * Get data of UpcomingServices and set in list view.
     */
    public void upcomingServices() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.upcomingServices(sharedpreferences.getString("Id", ""), new Callback<UpcomingServicesResponce>() {

            @Override
            public void success(UpcomingServicesResponce upcomingServicesResponce, Response response) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
                if (upcomingServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!upcomingServicesResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", upcomingServicesResponce.Token);
                        editor.apply();
                    }
                    upcomingServices.clear();
                    if (upcomingServicesResponce.Data != null) {
                        upcomingServices = upcomingServicesResponce.Data;

                        tvNoData.setVisibility(View.GONE);
                        lvMonthServicesList.setVisibility(View.VISIBLE);
                        adapter = new UpcomingSchedualAdapter(activity, upcomingServices);
                        lvMonthServicesList.setAdapter(adapter);

                    } else {

                    }


                } else if (upcomingServicesResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    tvNoData.setText(upcomingServicesResponce.Message);
                    tvNoData.setVisibility(View.VISIBLE);
                    lvMonthServicesList.setVisibility(View.GONE);
                    adapter = new UpcomingSchedualAdapter(activity, upcomingServices);
                    lvMonthServicesList.setAdapter(adapter);
                    //setListViewHeightBasedOnChildren(lvMonthServicesList);
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

            }
        });
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        //ListAdapter listAdapter = listView.getAdapter();
        if (adapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < adapter.getCount(); i++) {
            view = adapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((DashboardActivity) activity).last = "upcoming_schedule";
    }
}
