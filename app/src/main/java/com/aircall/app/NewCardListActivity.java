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
import com.aircall.app.Fragment.NewCardListFragment;
import com.aircall.app.Interfaces.AddressInterface;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NewCardListActivity extends Activity implements AddressInterface {

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
        changeFragment("NewCardList", false);
    }

    public void clickEvent() {

    }

    /**
     * Add NewCardListFragment, currently only one fragment in this activity, we can add more fragment
     * if need in future.
     * @param message
     * @param isAddInBackStack
     */
    @Override
    public void changeFragment(String message, Boolean isAddInBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_middel);

        HideKeyboard();
        if (message.equals("NewCardList")) {
            Fragment newFragment = new NewCardListFragment();
            strFragmentTag = newFragment.toString();
            transaction.add(R.id.frame_middel, newFragment, strFragmentTag);
            if (isAddInBackStack) {
                transaction.addToBackStack("");
            }
            transaction.commit();
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
        changeFragment("NewCardList", false);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}
