package com.aircall.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.R;


@SuppressLint("ValidFragment")
public class TwoButtonAlert extends DialogFragment {
    DialogInterfaceClick dialogClick;

    private Activity activity;
    private GlobalClass globalClass;

    String str_msg;

    public TwoButtonAlert() {

    }

    public TwoButtonAlert(String msg, DialogInterfaceClick dialogClick) {
        this.dialogClick = dialogClick;
        this.str_msg = msg;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = getActivity();
        globalClass = (GlobalClass) activity.getApplicationContext();
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.alert_two_button, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);

        TextView txt_yes = (TextView) promptsView.findViewById(R.id.txt_yes);
        TextView txt_no = (TextView) promptsView.findViewById(R.id.txt_no);
        LinearLayout layout_agree = (LinearLayout) promptsView.findViewById(R.id.layout_yes);
        LinearLayout layout_donot_agree = (LinearLayout) promptsView.findViewById(R.id.layout_no);
        TextView txtMessage = (TextView) promptsView.findViewById(R.id.txtMessage);

        txt_yes.setText(getString(R.string.agree));
        txt_no.setText(getString(R.string.donotagree));
        txtMessage.setText(str_msg);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        layout_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClick.dialogClick("agree");
                alertDialog.dismiss();
            }
        });

        layout_donot_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClick.dialogClick("donotagree");
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }
}