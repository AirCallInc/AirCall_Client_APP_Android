package com.aircall.app;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Fragment.BillingHistoryDetailsFragment;
import com.aircall.app.Fragment.BillingHistoryFragment;
import com.aircall.app.Fragment.ChahgePasswordFragment;
import com.aircall.app.Fragment.ContactNumberFragment;
import com.aircall.app.Fragment.UserProfileFragment;
import com.aircall.app.Interfaces.UserProfileInterface;
import com.aircall.app.Model.UpdateUserProfile.UpdateUserProfileData;

public class UserProfileActivity extends Activity implements UserProfileInterface {
    public GlobalClass globalClass;
    private ProgressDialogFragment progressDialogFragment;
    private String strFragmentTag;
    public String strUserPassword,CommonId,NId;
    public UpdateUserProfileData updateUserProfileData = new UpdateUserProfileData();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        globalClass = (GlobalClass) getApplicationContext();

        /**
         * Set Listener of change in back stack
         */
        getFragmentManager().addOnBackStackChangedListener(getListener());

        /**
         * If start activity from notification click, then open BillingHistory detail fragment
         * otherwise open UserProfile Fragment.
         */
        if(getIntent().getExtras() != null) {
            CommonId = getIntent().getExtras().getString("CommonId");
            NId = getIntent().getExtras().getString("NId");
        }

        if(CommonId == null) {
            changeFragment("UserProfile", false);
        } else {
            if(getIntent().getExtras().getBoolean("IsPaymentFail",false)) {
                changeFragment("BillingHistoryDetail", false);
            } else {
                changeFragment("BillingHistoryDetail_fail", false);
            }
        }
    }

    private void HideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    /**
     * Implementation method of UserProfileInterface for handle and change fragment
     * @param message Define which fragment need to be open
     * @param isAddInBackStack If fragment should in Back stack or not
     */
    @Override
    public void changeFragment(String message, Boolean isAddInBackStack) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frm_user_activity);

        HideKeyboard();
        if (message.equals("UserProfile")) {
            if (fragment instanceof UserProfileFragment) {

            } else {
                Fragment newFragment = new UserProfileFragment();
                strFragmentTag = newFragment.toString();
                transaction.add(R.id.frm_user_activity, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("ChangePassword")) {
            if (fragment instanceof ChahgePasswordFragment) {

            } else {
                Fragment newFragment = new ChahgePasswordFragment();
                strFragmentTag = newFragment.toString();
                transaction.add(R.id.frm_user_activity, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("ContactUpdate")) {
            if (fragment instanceof ContactNumberFragment) {

            } else {
                Fragment newFragment = new ContactNumberFragment();
                strFragmentTag = newFragment.toString();
                transaction.add(R.id.frm_user_activity, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("PaymentMethod")) {
            Intent intent = new Intent(UserProfileActivity.this, NewCardListActivity.class);
//            startActivityForResult(intent, 10);
            startActivity(intent);

        } else if (message.equals("BillingHistory")) {
            if (fragment instanceof BillingHistoryFragment) {

            } else {
                Fragment newFragment = new BillingHistoryFragment();
                strFragmentTag = newFragment.toString();
                transaction.add(R.id.frm_user_activity, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("BillingHistoryDetail")) {
            if (fragment instanceof BillingHistoryDetailsFragment) {

            } else {
                Fragment newFragment = new BillingHistoryDetailsFragment();
                strFragmentTag = newFragment.toString();
                Bundle args = new Bundle();
                args.putString("strPlanName", "Part Order");
                args.putString("strUnitName", "");
                newFragment.setArguments(args);

                transaction.add(R.id.frm_user_activity, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("BillingHistoryDetail_fail")) {
            if (fragment instanceof BillingHistoryDetailsFragment) {

            } else {
                Fragment newFragment = new BillingHistoryDetailsFragment();
                strFragmentTag = newFragment.toString();
                Bundle args = new Bundle();
                args.putString("strPlanName", "Pay Fail");
                args.putString("strUnitName", "");
                newFragment.setArguments(args);

                transaction.add(R.id.frm_user_activity, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
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

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    //setLastFromBackState();
                    FragmentManager manager = getFragmentManager();
                    Fragment currentFragment = manager.findFragmentById(R.id.frm_user_activity);
                } catch (Exception ex) {
                    Log.e("GetListener","Exception is "+ex.getMessage());
                }
            }
        };
        return result;
    }

    @Override
    public void onBackPressed() {
        if(isTaskRoot()) {
            /**
             * If activity launch from notification, then it will be the root activity in cycle.
             * So, at that point when user press back button we have to open dashboard screen instate of close the app.
             */
            Intent intent = new Intent(UserProfileActivity.this,DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            FragmentManager manager = getFragmentManager();
            Fragment currentFragment = manager.findFragmentById(R.id.frm_user_activity);
            if (currentFragment instanceof ChahgePasswordFragment || currentFragment instanceof ContactNumberFragment ) {
                HideKeyboard();
            }
            super.onBackPressed();
        }
    }
}