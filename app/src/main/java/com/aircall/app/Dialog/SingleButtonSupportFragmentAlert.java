package com.aircall.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.R;


@SuppressLint("ValidFragment")
public class SingleButtonSupportFragmentAlert extends DialogFragment {
    DialogInterfaceClick dialogClick;
    private String strMSG;
    private Activity activity;
    private GlobalClass globalClass;

    public SingleButtonSupportFragmentAlert() {

    }

    public SingleButtonSupportFragmentAlert(String strMSG, DialogInterfaceClick dialogClick) {
        this.dialogClick = dialogClick;
        this.strMSG = strMSG;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = getActivity();
        globalClass = (GlobalClass) activity.getApplicationContext();
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.alert_single_button, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);

        LinearLayout layout_ok = (LinearLayout)promptsView.findViewById(R.id.layout_ok);
        TextView txtMessage = (TextView)promptsView.findViewById(R.id.txtMessage);
        txtMessage.setText(strMSG);
        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        layout_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClick.dialogClick("");
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }
}