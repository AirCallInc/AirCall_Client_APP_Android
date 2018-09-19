package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.ContactUs.AddContactUsResponce;
import com.aircall.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ContactUsFragment extends Fragment {

    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private ProgressDialogFragment progressDialogFragment;
    ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
    SpannableStringBuilder ssbuilder;

    @Bind(R.id.etName)
    EditText etName;

    @Bind(R.id.etEmail)
    EditText etEmail;

    @Bind(R.id.etphone)
    EditText etphone;

    @Bind(R.id.etMessage)
    EditText etMessage;

    @Bind(R.id.tvSubmit)
    TextView tvSubmit;

    @Bind(R.id.ivNavDrawer)
    ImageView ivNavDrawer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_us, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvent();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;

    }

    public void init() {
        sharedpreferences = getActivity().getSharedPreferences("AircallData", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        etName.setText(sharedpreferences.getString("FirstName", "") + " " + sharedpreferences.getString("LastName", ""));
        etEmail.setText(sharedpreferences.getString("Email", ""));
        if (sharedpreferences.getString("MobileNumber", "").trim().equalsIgnoreCase("")) {
            etphone.setText(globalClass.MOBILE_NUMBER);
            Log.e("set from activity var","set");
        } else {
            etphone.setText(sharedpreferences.getString("MobileNumber", ""));
            Log.e("set from perf ","set");
        }
    }

    public void clickEvent() {
        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });

        ivNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etMessage.setError(null);
                etName.setError(null);
                etEmail.setError(null);
                ((DashboardActivity) getActivity()).openDrawer();
            }
        });
    }

    public void validation() {

        String estring = "";


        if (etName.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.Name);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etName.requestFocus();
            etName.setError(ssbuilder);

        } else if (etEmail.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.Email);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etEmail.requestFocus();
            etEmail.setError(ssbuilder);

        } else if (etphone.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.PhoneNumber);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etphone.requestFocus();
            etphone.setError(ssbuilder);

        } else if (etphone.getText().toString().trim().length() < 8) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.OfficeHomeNumberDigit);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etphone.requestFocus();
            etphone.setError(ssbuilder);

        } else if (!(GlobalClass.isValidEmail(etEmail.getText().toString().trim()))) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidEmail);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etEmail.requestFocus();
            etEmail.setError(ssbuilder);

        } else if (etMessage.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.Message);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etMessage.requestFocus();
            etMessage.setError(ssbuilder);

        } else if (!globalClass.checkInternetConnection()) {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");

        } else {

            sendContactUsInformtion();

        }
    }

    /**
     * Call webAPI to send data to admin
     */
    public void sendContactUsInformtion() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.addContactUsDetail(etName.getText().toString(), etEmail.getText().toString(),
                etphone.getText().toString(), etMessage.getText().toString(), new Callback<AddContactUsResponce>() {

                    @Override
                    public void success(AddContactUsResponce addContactUsResponce, Response response) {
                        hideProgressDialog();

                        if (addContactUsResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!addContactUsResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", addContactUsResponce.Token);
                                editor.apply();
                            }

                            etMessage.setText("");
                            Toast.makeText(activity, addContactUsResponce.Message, Toast.LENGTH_SHORT).show();
                            Log.e("Success Contact Us", "Success Contact Us");
                            activity.onBackPressed();

                        } else if (addContactUsResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(addContactUsResponce.Message,
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
                        hideProgressDialog();
                        DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                                new DialogInterfaceClick() {
                                    @Override
                                    public void dialogClick(String tag) {

                                    }
                                });
                        ds.setCancelable(true);
                        ds.show(getFragmentManager(), "");
                        Log.e("Fail", "fail" + error.getMessage());
                    }
                });
    }

    public void showProgressDailog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        progressDialogFragment.show(getFragmentManager(), "");
        progressDialogFragment.setCancelable(false);
    }

    public void hideProgressDialog() {
        try {
            if (progressDialogFragment != null) {
                progressDialogFragment.dismiss();
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        ((DashboardActivity) activity).last = "contact_us";
    }

    @Override
    public void onPause() {
        super.onPause();
        etMessage.setError(null);
        etName.setError(null);
        etEmail.setError(null);
    }
}
