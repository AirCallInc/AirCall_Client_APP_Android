package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AboutUs.AboutUsResponse;
import com.aircall.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AboutUsFragment extends Fragment {

    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Bind(R.id.tvTitleAboutUs)
    TextView tvTitleAboutUs;

    @Bind(R.id.wvAboutUs)
    WebView wvAboutUs;

    @Bind(R.id.ivNavDrawer)
    ImageView ivNavDrawer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvent();
        return view;
    }

    public void init() {
        sharedpreferences = getActivity().getSharedPreferences("AircallData", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        validation();
    }

    public void clickEvent() {
        ivNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).openDrawer();
            }
        });
    }

    public void validation() {

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
            getAboutUs();
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;

    }

    /**
     * call webAPI to get data of about us and set in WebView(wvAboutUs).
     */
    public void getAboutUs() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.getAboutUsInformation("1", new Callback<AboutUsResponse>() {

            @Override
            public void success(AboutUsResponse aboutUsResponse, Response response) {

                ((DashboardActivity) getActivity()).hideProgressDialog();

                if (aboutUsResponse.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!aboutUsResponse.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", aboutUsResponse.Token);
                        editor.apply();
                    }

                    tvTitleAboutUs.setText(aboutUsResponse.Data.PageTitle);
                    wvAboutUs.getSettings().setJavaScriptEnabled(true);
                    wvAboutUs.loadDataWithBaseURL("", aboutUsResponse.Data.Description, "text/html", "UTF-8", "");

                } else if (aboutUsResponse.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    DialogFragment ds = new SingleButtonAlert(aboutUsResponse.Message,
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
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        ((DashboardActivity)activity).last = "about_us";
    }
}
