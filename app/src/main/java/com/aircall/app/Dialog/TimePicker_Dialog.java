package com.aircall.app.Dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.R;

import java.util.Calendar;

/**
 * Created by zealous on 13/02/17.
 */

public class TimePicker_Dialog extends DialogFragment {
    DialogInterfaceClick dialogClick;
    TimePicker timePicker;
    private int hour;
    private int minute;
    private Activity activity;
    TimePickerDialog mTimePickerDialog;

    public TimePicker_Dialog() {

    }

    public TimePicker_Dialog(DialogInterfaceClick dialogClick) {
        this.dialogClick = dialogClick;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        this.activity = getActivity();
        this.activity = getActivity();

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_time_picker, null);


        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getActivity());
        alertDialogBuilder.setView(promptsView);

        timePicker = (TimePicker) promptsView.findViewById(R.id.timePicker);

        final Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);


        final AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
        return alertDialog;
    }

    private TimePickerDialog.OnTimeSetListener timePickerListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int selectedHour,
                                      int selectedMinute) {
                    hour = selectedHour;
                    minute = selectedMinute;
                    // set current time into textview
                    updateTime(hour, minute);
                }
            };

    private static String utilTime(int value) {

        if (value < 10)
            return "0" + String.valueOf(value);
        else
            return String.valueOf(value);
    }

    // Used to convert 24hr format to 12hr format with AM/PM values
    private void updateTime(int hours, int mins) {

        String timeSet = "";
        if (hours > 12) {
            hours -= 12;
            timeSet = "PM";
        } else if (hours == 0) {
            hours += 12;
            timeSet = "AM";
        } else if (hours == 12)
            timeSet = "PM";
        else
            timeSet = "AM";


        String minutes = "";
        if (mins < 10)
            minutes = "0" + mins;
        else
            minutes = String.valueOf(mins);

        // Append in a StringBuilder
        String aTime = new StringBuilder().append(hours).append(':')
                .append(minutes).append(" ").append(timeSet).toString();

        this.dialogClick.dialogClick(aTime);
        dismiss();
//        output.setText(aTime);
    }
}
