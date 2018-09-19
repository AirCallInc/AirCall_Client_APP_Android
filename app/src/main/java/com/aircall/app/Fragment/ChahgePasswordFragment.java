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
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.LoginActivity;
import com.aircall.app.Model.ChangePassword.ChangePasswordResponce;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ChahgePasswordFragment extends Fragment {

    GlobalClass globalClass;
    Activity activity;

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Bind(R.id.etOldPassword)
    EditText etOldPassword;

    @Bind(R.id.etNewPassword)
    EditText etNewPassword;

    @Bind(R.id.etReTypePassword)
    EditText etReTypePassword;

    @Bind(R.id.tvChangePasswordReset)
    TextView tvChangePasswordReset;

    @Bind(R.id.tvChangePasswordCancel)
    TextView tvChangePasswordCancel;

    @Bind(R.id.flMain)
    public FrameLayout flMain;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        ButterKnife.bind(this, view);

        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        clickEvent();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((UserProfileActivity) activity).globalClass;

    }

    public void clickEvent() {
        flMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvChangePasswordCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });

        tvChangePasswordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validation();
            }
        });
    }

    public void updatePassword() {

        ((UserProfileActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.changePassword(sharedpreferences.getString("Id", ""),
                globalClass.getMD5EncryptedString(etOldPassword.getText().toString().trim()),
                globalClass.getMD5EncryptedString(etNewPassword.getText().toString().trim()), new Callback<ChangePasswordResponce>() {

                    @Override
                    public void success(ChangePasswordResponce changePasswordResponce, Response response) {
                        ((UserProfileActivity) activity).hideProgressDialog();

                        Log.e("success", "success");

                        if (changePasswordResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {

                            editor.putString("Id", changePasswordResponce.Data.Id);
                            editor.putString("OldPassword", changePasswordResponce.Data.OldPassword);
                            editor.putString("NewPassword", changePasswordResponce.Data.NewPassword);
                            editor.apply();
                            Toast.makeText(activity,"Password updated successfully",Toast.LENGTH_LONG).show();
                            activity.onBackPressed();

                        } else if (changePasswordResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(changePasswordResponce.Message,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {

                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");
                            etOldPassword.setText("");
                            etNewPassword.setText("");
                            etReTypePassword.setText("");
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ((LoginActivity) getActivity()).hideProgressDialog();
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

    public void validation() {

        String estring = "";
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
        SpannableStringBuilder ssbuilder;

        if (etOldPassword.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.Password);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etOldPassword.requestFocus();
            etOldPassword.setError(ssbuilder);

        } else if (etOldPassword.getText().toString().trim().length() < 6) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidPassowrd);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etOldPassword.requestFocus();
            etOldPassword.setError(ssbuilder);

        } else if (etNewPassword.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.NewPassword);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etNewPassword.requestFocus();
            etNewPassword.setError(ssbuilder);

        } else if (etNewPassword.getText().toString().trim().length() < 6) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidPassowrd);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etNewPassword.requestFocus();
            etNewPassword.setError(ssbuilder);

        } else if (etReTypePassword.getText().toString().trim().equals("")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.NotMatchPassword);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etReTypePassword.requestFocus();
            etReTypePassword.setError(ssbuilder);

        } else if (etReTypePassword.getText().toString().trim().length() < 6) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.NotMatchPassword);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etReTypePassword.requestFocus();
            etReTypePassword.setError(ssbuilder);

        } else if (!(((UserProfileActivity) activity).strUserPassword)
                .equals(globalClass.getMD5EncryptedString(etOldPassword.getText().toString().trim()))) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.NotMatchPassword);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etOldPassword.requestFocus();
            etOldPassword.setError(ssbuilder);
        } else if (!(etReTypePassword.getText().toString().trim())
                .equals(etNewPassword.getText().toString().trim())) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.NotMatchPassword);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etReTypePassword.requestFocus();
            etNewPassword.setError(ssbuilder);
            etReTypePassword.setError(ssbuilder);
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
            updatePassword();
        }
    }

}
