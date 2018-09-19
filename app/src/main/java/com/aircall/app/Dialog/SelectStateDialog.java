package com.aircall.app.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aircall.app.Adapter.SelectStateAdapter;
import com.aircall.app.AddAddressActivity;
import com.aircall.app.BillingAddressActivity;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Model.GetAllState.GetAllStateResponce;
import com.aircall.app.R;

/**
 * Created by Jd on 21/06/16.
 */
public class SelectStateDialog extends DialogFragment {
    Dialog new_dialog;
    private GlobalClass globalClass;
    View mTransFilterQuantity;
    int i = 1;
    Activity activity;
    ImageView ivClose;
    SharedPreferences sharedpreferences;
    UsernameDialogInteface usernameDialogInteface;
    SharedPreferences.Editor editor;
    private ProgressDialogFragment progressDialogFragment;
    TextView tvNoData;
    ListView listViewForState;

    
    private SelectStateAdapter adapter;
    GetAllStateResponce getAllStateResponce;

    /**
     * No need to call API for get states, state list get from fragment
     * @param globalClass
     * @param usernameDialogInteface
     * @param getAllStateResponce
     */
    public SelectStateDialog(GlobalClass globalClass, UsernameDialogInteface usernameDialogInteface,
                             GetAllStateResponce getAllStateResponce) {
        this.usernameDialogInteface = usernameDialogInteface;
        this.globalClass = globalClass;
        this.getAllStateResponce = getAllStateResponce;
        Log.e("call", "called");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_for_select_state, null);

        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new_dialog.setContentView(promptsView);

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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Item position is in the variable position.

                usernameDialogInteface.submitClick(getAllStateResponce.Data.get(position).Name, getAllStateResponce.Data.get(position).Id);
                new_dialog.dismiss();
            }
        });
    }

    public void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        tvNoData = (TextView) new_dialog.findViewById(R.id.tvNoData);
        listViewForState = (ListView) new_dialog.findViewById(R.id.lvSelectState);
        ivClose = (ImageView) new_dialog.findViewById(R.id.ivCancelDialog);

        if (getAllStateResponce.Data == null || getAllStateResponce.Data.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
            listViewForState.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            listViewForState.setVisibility(View.VISIBLE);
            adapter = new SelectStateAdapter(activity, usernameDialogInteface, getAllStateResponce.Data);
            listViewForState.setAdapter(adapter);
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        String curruntActivity = getActivity().getClass().getSimpleName();
        if (curruntActivity.contains("BillingAddressActivity")) {
            globalClass = ((BillingAddressActivity) activity).globalClass;
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
