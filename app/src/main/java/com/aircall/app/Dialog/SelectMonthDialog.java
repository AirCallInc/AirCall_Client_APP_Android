package com.aircall.app.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aircall.app.Adapter.SelecteMonthAdapter;
import com.aircall.app.AddAddressActivity;
import com.aircall.app.AddNewCardActivity;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.R;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by kartik on 21/06/16.
 */
public class SelectMonthDialog extends DialogFragment {
    Dialog new_dialog;
    private GlobalClass globalClass;
    View mTransFilterQuantity;
    int i = 1;
    Activity activity;
    ImageView ivClose;
    TextView tvHeaderName;
    SharedPreferences sharedpreferences;
    UsernameDialogInteface usernameDialogInteface;
    SharedPreferences.Editor editor;
    private ProgressDialogFragment progressDialogFragment;
    private Boolean isMonth;
    ListView listViewForState;
    private ArrayList<String> month = new ArrayList<>();
    private SelecteMonthAdapter adapter;

    public SelectMonthDialog(GlobalClass globalClass, UsernameDialogInteface usernameDialogInteface, Boolean isMonth) {
        this.usernameDialogInteface = usernameDialogInteface;
        this.globalClass = globalClass;
        this.isMonth = isMonth;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_for_select_state, null);

        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.setContentView(promptsView);
        listViewForState = (ListView) new_dialog.findViewById(R.id.lvSelectState);
        sharedpreferences = getActivity().getSharedPreferences("AircallData", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        init();
        clickEvent();


        return new_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    public void clickEvent() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_dialog.dismiss();
            }
        });

        listViewForState.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Item position is in the variable position.

                usernameDialogInteface.submitClick("", month.get(position));
                new_dialog.dismiss();
            }
        });
    }

    public void init() {

        ivClose = (ImageView) new_dialog.findViewById(R.id.ivCancelDialog);
        tvHeaderName = (TextView) new_dialog.findViewById(R.id.tvHeaderName);
        if (isMonth) {
            tvHeaderName.setText("Select month");
            month.add("01");
            month.add("02");
            month.add("03");
            month.add("04");
            month.add("05");
            month.add("06");
            month.add("07");
            month.add("08");
            month.add("09");
            month.add("10");
            month.add("11");
            month.add("12");
        } else {
            tvHeaderName.setText("Select year");
            int year = Calendar.getInstance().get(Calendar.YEAR);
            for (int i = 0; i < 20; i++) {
                month.add("" + year);
                year += 1;
            }
        }
        adapter = new SelecteMonthAdapter(activity, month);
        listViewForState.setAdapter(adapter);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        String curruntActivity = getActivity().getClass().getSimpleName();
        if (curruntActivity.contains("AddNewCardActivity")) {
            globalClass = ((AddNewCardActivity) activity).globalClass;
        } else if (curruntActivity.contains("AddAddressActivity")) {
            globalClass = ((AddAddressActivity) activity).globalClass;
        }

    }


    public void hideProgressDialog() {
        try {
            if (progressDialogFragment != null) {
                progressDialogFragment.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public void showProgressDailog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        progressDialogFragment.show(getFragmentManager(), "");
        progressDialogFragment.setCancelable(false);
    }


}
