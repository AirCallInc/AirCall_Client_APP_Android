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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aircall.app.Adapter.SelectAddressAdapter;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Model.GetAddress.AddressDetailForList;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by kartik on 21/06/16.
 */
public class SelectAddressListDialog extends DialogFragment {
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
    ArrayList<AddressDetailForList> addressList;
    ListView lvSelectAddress;
    TextView tvNoData;
    private SelectAddressAdapter adapter;

    public SelectAddressListDialog(GlobalClass globalClass, UsernameDialogInteface usernameDialogInteface,
                                   ArrayList<AddressDetailForList> addressList) {
        this.usernameDialogInteface = usernameDialogInteface;
        this.globalClass = globalClass;
        this.addressList = addressList;
        Log.e("call", "called");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_for_select_address, null);

        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
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
        lvSelectAddress.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                usernameDialogInteface.submitClick(addressList.get(position).Id, null);
                new_dialog.dismiss();
            }
        });
    }

    public void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        ivClose = (ImageView) new_dialog.findViewById(R.id.ivCancelDialog);
        lvSelectAddress = (ListView) new_dialog.findViewById(R.id.lvSelectAddress);
        tvNoData = (TextView) new_dialog.findViewById(R.id.tvNoData);

        if (addressList == null || addressList.size() == 0) {
            tvNoData.setVisibility(View.VISIBLE);
            lvSelectAddress.setVisibility(View.GONE);
        } else {
            tvNoData.setVisibility(View.GONE);
            lvSelectAddress.setVisibility(View.VISIBLE);
            adapter = new SelectAddressAdapter(activity, usernameDialogInteface, addressList);
            lvSelectAddress.setAdapter(adapter);
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        String curruntActivity = getActivity().getClass().getSimpleName();
        if (curruntActivity.contains("DashboardActivity")) {
            globalClass = ((DashboardActivity) activity).globalClass;
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
