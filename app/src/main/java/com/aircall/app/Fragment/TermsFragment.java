package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
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

public class TermsFragment extends Fragment {

    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Bind(R.id.tvTitle)
    TextView tvTitle;

    @Bind(R.id.wvTermsConditions)
    WebView wvTermsConditions;

    @Bind(R.id.ivNavDrawer)
    ImageView ivNavDrawer;

    @Bind(R.id.ivClose)
    ImageView ivClose;

    private ProgressDialog progDailog;
    String GoogleDocs = "http://docs.google.com/gview?embedded=true&url=";
    String extStorageDirectory;
    String url;
    String PageId = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_terms, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvent();
        return view;
    }

    public void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        Bundle args = getArguments();
        if (args != null) {
            PageId = args.getString("pageid");
        }
        /**
         * If from drawer : Navigation drawer button will be Visible and close button will be Invisible
         * If from Signup or Payment Method : Navigation drawer button will be Invisible and close button will be Visible
         */
        if (((DashboardActivity) activity).fromOtherActivity) {
            ivClose.setVisibility(View.VISIBLE);
            ivNavDrawer.setVisibility(View.INVISIBLE);
        } else {
            ivClose.setVisibility(View.GONE);
            ivNavDrawer.setVisibility(View.VISIBLE);
        }
        validation();
    }

    public void clickEvent() {
        ivNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).openDrawer();
            }
        });

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
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
            if (PageId != null) {
                getTermsAndConditions();
            } else {
                Log.e("Load PDF", "Load PDF");
                tvTitle.setText(((DashboardActivity) activity).PageTitle);
                progDailog = ProgressDialog.show(activity, "Loading", "Please wait...", true);
                progDailog.setCancelable(false);

                wvTermsConditions.getSettings().setJavaScriptEnabled(true);
                wvTermsConditions.getSettings().setLoadWithOverviewMode(true);
//                wvTermsConditions.getSettings().setUseWideViewPort(true);
//                wvTermsConditions.zoomIn();
//                wvTermsConditions.zoomOut();
                wvTermsConditions.getSettings().setBuiltInZoomControls(true);
                wvTermsConditions.getSettings().setSupportZoom(true);
                wvTermsConditions.setWebViewClient(new WebViewClient() {

                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        progDailog.show();
                        view.loadUrl(url);

                        return true;
                    }

                    @Override
                    public void onPageFinished(WebView view, final String url) {
                        progDailog.dismiss();
                    }
                });
                url = GoogleDocs + ((DashboardActivity) activity).Pdfurl;
                Log.e("Pdf Url ", url);
                //GoogleDocs+"http://www.tinaja.com/glib/pdflink.pdf"
                wvTermsConditions.loadUrl(url);

            }
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;
    }

    /**
     * Call WebAPI to get data for terms and condition and load in WebView.
     */
    public void getTermsAndConditions() {
        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));
        webserviceApi.getAboutUsInformation(PageId, new Callback<AboutUsResponse>() {
            @Override
            public void success(AboutUsResponse aboutUsResponse, Response response) {

                ((DashboardActivity) getActivity()).hideProgressDialog();
                if (aboutUsResponse.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!aboutUsResponse.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", aboutUsResponse.Token);
                        editor.apply();
                    }

                    tvTitle.setText(aboutUsResponse.Data.PageTitle);
                    wvTermsConditions.getSettings().setJavaScriptEnabled(true);
                    wvTermsConditions.loadDataWithBaseURL("", aboutUsResponse.Data.Description, "text/html", "UTF-8", "");


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
        ((DashboardActivity) activity).last = "terms";
    }
}
