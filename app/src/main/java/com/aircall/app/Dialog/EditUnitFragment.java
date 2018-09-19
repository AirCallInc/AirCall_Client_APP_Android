package com.aircall.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.ForgotPassword.ForgotPasswordResponce;
import com.aircall.app.R;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


@SuppressLint("ValidFragment")
public class EditUnitFragment extends DialogFragment {
    private DialogInterfaceClick dialogInterface;
    Dialog new_dialog;
    private GlobalClass globalClass;
    EditText edtUnitName;
    SharedPreferences sharedpreferences;
    private String UnitName, UnitId;

    public EditUnitFragment(GlobalClass globalClass, String UnitName, String UnitId, DialogInterfaceClick dialogInterface) {
        this.dialogInterface = dialogInterface;
        this.globalClass = globalClass;
        this.UnitName = UnitName;
        this.UnitId = UnitId;
        Log.e("call", "called");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.alert_edit_unit, null);
        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.setContentView(promptsView);

        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        edtUnitName = (EditText) new_dialog
                .findViewById(R.id.edtUnitName);

        final LinearLayout layout_update = (LinearLayout) new_dialog.findViewById(R.id.layout_update);

        final LinearLayout layout_cancel = (LinearLayout) new_dialog.findViewById(R.id.layout_cancel);


        edtUnitName.setText(UnitName);
        edtUnitName.setSelection(edtUnitName.getText().length());
        layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_dialog.dismiss();
            }
        });

        layout_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String estring = "";
                ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                SpannableStringBuilder ssbuilder;
                if (edtUnitName.getText().toString().trim()
                        .equals("")) {
                    ssbuilder = new SpannableStringBuilder(ErrorMessages.UnitName);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    edtUnitName.requestFocus();
                    edtUnitName.setError(ssbuilder);
                } else {
                    updateName();
                }
            }
        });
        return new_dialog;
    }

    public void updateName() {

        ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class);

        webserviceApi.updateClientUnit(sharedpreferences.getString("Id", ""),
                UnitId, edtUnitName.getText().toString().trim(), new Callback<ForgotPasswordResponce>() {
                    @Override
                    public void success(ForgotPasswordResponce forgotPasswordResponce, Response response) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();

                        if (forgotPasswordResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            dialogInterface.dialogClick(edtUnitName.getText().toString().trim());
                            edtUnitName.setText("");
                            new_dialog.dismiss();
                            //Toast.makeText(getActivity(),forgotPasswordResponce.Message,Toast.LENGTH_LONG).show();
                        } else {
                            dialogInterface.dialogClick("");
                            edtUnitName.setText("");
                            new_dialog.dismiss();
                            //Toast.makeText(getActivity(),forgotPasswordResponce.Message,Toast.LENGTH_LONG).show();
                            DialogFragment ds = new SingleButtonAlert(forgotPasswordResponce.Message,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {

                                        }
                                    });
                            ds.setCancelable(false);
                            ds.show(getFragmentManager(), "");
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();

                        dialogInterface.dialogClick("");
                        //dialogInterface.dialogClick(ErrorMessages.ServerError);
                        edtUnitName.setText("");
                        new_dialog.dismiss();
                        Toast.makeText(getActivity(), ErrorMessages.ServerError, Toast.LENGTH_LONG).show();
                    }
                });
    }
}