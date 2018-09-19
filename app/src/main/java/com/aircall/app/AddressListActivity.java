package com.aircall.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Fragment.AddressListFragment;
import com.aircall.app.Interfaces.AddressInterface;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Model.GetAddress.AddressDetailForList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *  Currently It's only one fragment in this activity, but as per complexity of project in future it is possible to
 *  we should have add more fragments in activity, that's why we use separate fragment for address listing instated of
 *  directly list address in activity.
 */

public class AddressListActivity extends Activity implements AddressInterface, DialogInterfaceClick {

    public AddressDetailForList addressDetailForList = new AddressDetailForList();

    @Bind(R.id.frame_middel)
    public FrameLayout frame_middel;

    public GlobalClass globalClass;
    private ProgressDialogFragment progressDialogFragment;
    private String strFragmentTag;
    Activity activity;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_list);
        ButterKnife.bind(this);

        init();
        clickEvent();
    }

    private void init() {
        globalClass = (GlobalClass) getApplicationContext();
        changeFragment("AddressList", false);

    }

    public void clickEvent() {

    }


    /**
     * Implementation of AddressInterface for switch fragment in activity
     * @param message Which fragment want to set in activity
     * @param isAddInBackStack Fragment should be in back stack or not
     */
    @Override
    public void changeFragment(String message, Boolean isAddInBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_middel);

        HideKeyboard();
        if (message.equals("AddressList")) {
            Fragment newFragment = new AddressListFragment();
            strFragmentTag = newFragment.toString();
            transaction.add(R.id.frame_middel, newFragment, strFragmentTag);
            if (isAddInBackStack) {
                transaction.addToBackStack("");
            }
            transaction.commit();
        } else if (message.equals("AddAddress")) {

        }
    }

    private void HideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public void showProgressDailog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        progressDialogFragment.show(getFragmentManager(), "");
        progressDialogFragment.setCancelable(false);
    }

    public void hideProgressDialog() {
        try {
            if (progressDialogFragment != null) {
                progressDialogFragment.dismiss();
            }
        } catch (Exception e) {
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        changeFragment("AddressList",false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_middel);
    }

    @Override
    public void dialogClick(String strAddEdit) {

        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_middel);

        if (strAddEdit.equals("Edit Address")) {
            Intent intent = new Intent(AddressListActivity.this, AddAddressActivity.class);
            startActivity(intent);
        }

    }

}
