package com.aircall.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Fragment.SignInFragment;
import com.aircall.app.Fragment.SignupFragment;
import com.aircall.app.Interfaces.Signininterface;

public class LoginActivity extends Activity implements Signininterface {


    public GlobalClass globalClass;
    private ProgressDialogFragment progressDialogFragment;
    private String strFragmentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        globalClass = (GlobalClass) getApplicationContext();
        itemClick("login");
    }

    /**
     * Method Implementation of Signininterface.
     *
     * @param tag To Identify which fragment should transect.
     */
    public void itemClick(String tag) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();

        if (tag.equalsIgnoreCase("login")) {
            Fragment newFragment = new SignInFragment();
            strFragmentTag = newFragment.toString();
            transaction.replace(R.id.frmmain, newFragment,
                    strFragmentTag);
            transaction.commit();
        } else if (tag.equalsIgnoreCase("signup")) {
            Fragment newFragment = new SignupFragment();
            strFragmentTag = newFragment.toString();
            transaction.replace(R.id.frmmain, newFragment,
                    strFragmentTag);
            transaction.addToBackStack(strFragmentTag);
            transaction.commit();
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
}
