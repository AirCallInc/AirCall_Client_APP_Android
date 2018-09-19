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

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.NumberPicker;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.R;

import java.util.Calendar;


@SuppressLint("ValidFragment")
public class MnthYrPickerAlert extends DialogFragment {
    DialogInterfaceClick dialogClick;
    private String strMSG;
    private Activity activity;
    private GlobalClass globalClass;
    int month, year;
    private NumberPicker npMonthPicker, npYearPicker;
    private String[] monthDisplay = new String[]{"January", "February", "March", "April", "May", "June", "July", "August",
            "September", "October", "November", "December"};

    public MnthYrPickerAlert() {

    }

    public MnthYrPickerAlert(int month, int year, DialogInterfaceClick dialogClick) {
        this.dialogClick = dialogClick;
        this.month = month;
        this.year = year;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = getActivity();
        globalClass = (GlobalClass) activity.getApplicationContext();
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.alert_mnth_yr, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);

        LinearLayout layout_ok = (LinearLayout) promptsView.findViewById(R.id.layout_ok);
        npMonthPicker = (NumberPicker) promptsView.findViewById(R.id.npMonthPicker);
        npYearPicker = (NumberPicker) promptsView.findViewById(R.id.npYearPicker);

        npMonthPicker.setMinValue(0);
        npMonthPicker.setMaxValue(11);
        npMonthPicker.setDisplayedValues(monthDisplay);

        Calendar calendar = Calendar.getInstance();
        int Year = calendar.get(Calendar.YEAR);
        npYearPicker.setMaxValue(Year);
        npYearPicker.setMinValue(1980);

        if (month == 0) {
            month = Calendar.getInstance().get(Calendar.MONTH) + 1;
            year = Year;
            npMonthPicker.setValue(month - 1);
            npYearPicker.setValue(Year);

        } else {
            npMonthPicker.setValue(month - 1);
            npYearPicker.setValue(year);
        }

        npMonthPicker.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                month = newVal + 1;
                checkDate();
            }
        });

        npYearPicker.setOnValueChangedListener(new android.widget.NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(android.widget.NumberPicker picker, int oldVal, int newVal) {
                year = newVal;
                checkDate();
            }
        });

        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

        layout_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogClick.dialogClick(String.format("%02d", month) + "/" + year);
                alertDialog.dismiss();
            }
        });
        return alertDialog;
    }

    private void checkDate() {
        int yr = Calendar.getInstance().get(Calendar.YEAR);
        int mnth = Calendar.getInstance().get(Calendar.MONTH);
        if (year == yr && month > mnth + 1) {
            month = mnth + 1;
            npMonthPicker.setValue(month-1);
        }
    }
}