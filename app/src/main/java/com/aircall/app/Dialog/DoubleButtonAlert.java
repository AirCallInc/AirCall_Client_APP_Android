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
public class DoubleButtonAlert extends DialogFragment {
    DialogInterfaceClick dialogClick;

    private Activity activity;
    private GlobalClass globalClass;

    String str_msg;

    public DoubleButtonAlert() {

    }

    public DoubleButtonAlert( String msg,DialogInterfaceClick dialogClick) {
        this.dialogClick = dialogClick;
        this.str_msg=msg;

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = getActivity();
        globalClass = (GlobalClass) activity.getApplicationContext();
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.alert_double_button, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);

        LinearLayout layout_yes = (LinearLayout)promptsView.findViewById(R.id.layout_yes);
        LinearLayout layout_no = (LinearLayout)promptsView.findViewById(R.id.layout_no);
        TextView txtMessage = (TextView)promptsView.findViewById(R.id.txtMessage);

        txtMessage.setText(str_msg);

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();

        layout_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogClick.dialogClick("yes");
                alertDialog.dismiss();
            }
        });

        layout_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialogClick.dialogClick("no");
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }
}