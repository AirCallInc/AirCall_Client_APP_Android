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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.UpdateUserProfile.UpdateContactsInfoResponce;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ContactNumberFragment extends Fragment {

    @Bind(R.id.etCellNumber)
    EditText etCellNumber;

    @Bind(R.id.etOfficeNumber)
    EditText etOfficeNumber;

    @Bind(R.id.etHomeNumber)
    EditText etHomeNumber;

    @Bind(R.id.tvSubmit)
    TextView tvSubmit;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_number,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvent();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        etCellNumber.setText(((UserProfileActivity) activity).updateUserProfileData.MobileNumber);
        etOfficeNumber.setText(((UserProfileActivity) activity).updateUserProfileData.OfficeNumber);
        etHomeNumber.setText(((UserProfileActivity) activity).updateUserProfileData.HomeNumber);
    }

    private void clickEvent() {
        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!globalClass.checkInternetConnection()) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else if (etCellNumber.getText().toString().trim().equalsIgnoreCase("")) {
                    String estring = "";
                    ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                    SpannableStringBuilder ssbuilder = new SpannableStringBuilder(ErrorMessages.CellNumber);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    etCellNumber.requestFocus();
                    etCellNumber.setError(ssbuilder);
                } else if (etCellNumber.getText().toString().trim().length() < 10) {
                    String estring = "";
                    ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                    SpannableStringBuilder ssbuilder = new SpannableStringBuilder(ErrorMessages.CellNumberDigit);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    etCellNumber.requestFocus();
                    etCellNumber.setError(ssbuilder);
                } else if (!etOfficeNumber.getText().toString().trim().equalsIgnoreCase("") && etOfficeNumber.getText().toString().trim().length() < 8) {
                    String estring = "";
                    ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                    SpannableStringBuilder ssbuilder = new SpannableStringBuilder(ErrorMessages.OfficeHomeNumberDigit);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    etOfficeNumber.requestFocus();
                    etOfficeNumber.setError(ssbuilder);
                } else if (!etHomeNumber.getText().toString().trim().equalsIgnoreCase("") && etHomeNumber.getText().toString().trim().length() < 8) {
                    String estring = "";
                    ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                    SpannableStringBuilder ssbuilder = new SpannableStringBuilder(ErrorMessages.OfficeHomeNumberDigit);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    etHomeNumber.requestFocus();
                    etHomeNumber.setError(ssbuilder);
                } else {
                    updateContactInfo();
                }
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
    }

    private void updateContactInfo() {
        ((UserProfileActivity) getActivity()).showProgressDailog("Please Wait...");
        final WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.updateConntactInfo(sharedpreferences.getString("Id", ""), etCellNumber.getText().toString().trim(),
                etOfficeNumber.getText().toString().trim(), etHomeNumber.getText().toString().trim(), new Callback<UpdateContactsInfoResponce>() {

                    @Override
                    public void success(UpdateContactsInfoResponce updateContactsInfoResponce, Response response) {
                        ((UserProfileActivity) getActivity()).hideProgressDialog();

                        if (updateContactsInfoResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!updateContactsInfoResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", updateContactsInfoResponce.Token);
                                editor.apply();
                            }
                            Toast.makeText(activity, "Contact updated successfully", Toast.LENGTH_LONG).show();
                            ((UserProfileActivity) activity).updateUserProfileData.HomeNumber = etHomeNumber.getText().toString().trim();
                            ((UserProfileActivity) activity).updateUserProfileData.MobileNumber = etCellNumber.getText().toString().trim();
                            globalClass.MOBILE_NUMBER = etCellNumber.getText().toString().trim();
                            editor.putString("MobileNumber", globalClass.MOBILE_NUMBER);
                            editor.apply();
                            ((UserProfileActivity) activity).updateUserProfileData.OfficeNumber = etOfficeNumber.getText().toString().trim();
                            activity.onBackPressed();


                        } else if (updateContactsInfoResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(updateContactsInfoResponce.Message,
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
                        ((UserProfileActivity) getActivity()).hideProgressDialog();
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
        globalClass = ((UserProfileActivity) activity).globalClass;

    }
}
