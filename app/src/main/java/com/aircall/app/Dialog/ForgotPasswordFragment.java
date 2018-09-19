package com.aircall.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.LoginActivity;
import com.aircall.app.Model.ForgotPassword.ForgotPasswordResponce;
import com.aircall.app.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


@SuppressLint("ValidFragment")
public class ForgotPasswordFragment extends DialogFragment {
    private DialogInterfaceClick dialogInterface;
    Dialog new_dialog;
    private GlobalClass globalClass;

    public ForgotPasswordFragment(GlobalClass globalClass, DialogInterfaceClick dialogInterface) {
        this.dialogInterface = dialogInterface;
        this.globalClass = globalClass;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.alert_forgot_password, null);
        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.setContentView(promptsView);

        final EditText edtEmailAddress = (EditText) new_dialog
                .findViewById(R.id.edtForgotPassword);

        final LinearLayout layout_submit = (LinearLayout) new_dialog.findViewById(R.id.layout_submit);

        ImageView ivClose = (ImageView) new_dialog.findViewById(R.id.ivClose);

        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_dialog.dismiss();
            }
        });

        layout_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String estring = "";
                ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                SpannableStringBuilder ssbuilder;
                if (edtEmailAddress.getText().toString().trim()
                        .equals("")) {
                    ssbuilder = new SpannableStringBuilder(ErrorMessages.Email);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    edtEmailAddress.requestFocus();
                    edtEmailAddress.setError(ssbuilder);
                } else if (!Patterns.EMAIL_ADDRESS.matcher(
                        edtEmailAddress.getText().toString().trim())
                        .matches()) {
                    ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidEmail);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    edtEmailAddress.requestFocus();
                    edtEmailAddress.setError(ssbuilder);
                } else {
                    forgotPassword(edtEmailAddress.getText().toString().trim());
                }
            }
        });

        return new_dialog;
    }

    /**
     * Call WebAPI for request forgot password
     *
     * @param Email Clients email address entered in EditText
     */

    public void forgotPassword(String Email) {

        ((LoginActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class);

        webserviceApi.forgotPassword(Email, new Callback<ForgotPasswordResponce>() {
            @Override
            public void success(ForgotPasswordResponce forgotPasswordResponce, Response response) {
                ((LoginActivity) getActivity()).hideProgressDialog();

                dialogInterface.dialogClick(forgotPasswordResponce.Message);
                new_dialog.dismiss();
            }

            @Override
            public void failure(RetrofitError error) {
                ((LoginActivity) getActivity()).hideProgressDialog();

                dialogInterface.dialogClick(error.getMessage());
                //dialogInterface.dialogClick(ErrorMessages.ServerError);
                new_dialog.dismiss();
            }
        });
    }
}