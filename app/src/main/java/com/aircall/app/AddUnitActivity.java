package com.aircall.app;

import android.app.Activity;
import android.app.ActivityManager;
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
import com.aircall.app.Fragment.AddUnitMandatoryFragment;
import com.aircall.app.Fragment.SummaryFragment;
import com.aircall.app.Interfaces.AddUnitInterface;
import com.aircall.app.Model.AddUnit.AddUnitRequest;
import com.aircall.app.Model.AddUnit.CommonDataRequest;
import com.aircall.app.Model.DynamicViews.FilterViews;
import com.aircall.app.Model.DynamicViews.FuseViews;

import java.util.List;

public class AddUnitActivity extends Activity implements AddUnitInterface {

    private String strFragmentTag;
    public GlobalClass globalClass;
    private ProgressDialogFragment progressDialogFragment;
    Activity activity;

    /**
     * Below variables are use throughout activity in all fragment to store unit information
     */
    //Remove
//    public Boolean isOptionalInfo = false;
    public int Quantity = 0;
    public AddUnitRequest addUnitRequest = new AddUnitRequest();
    public CommonDataRequest commonDataRequest = new CommonDataRequest();
    public FilterViews filterDetail = new FilterViews();
    public FuseViews fuseViews = new FuseViews();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);
        init();
        getFragmentManager().addOnBackStackChangedListener(getListener());
    }

    public void init() {
        globalClass = (GlobalClass) getApplicationContext();
        Intent intent = getIntent();
        if (intent.getStringExtra("addUnit") == null) {
            /**
             * When user want to add new unit
             */
            changeFragment("mendetory", false, null);
        } else if (intent.getStringExtra("addUnit").equalsIgnoreCase("summary")) {
            /**
             * When user want to continue for payment process with old unpaid units.
             */
            Bundle bundle = new Bundle();
            if (intent.getStringExtra("summaryData") != null) {
                bundle.putString("summary", intent.getStringExtra("summaryData"));
            } else {
                bundle.putString("CommonId", intent.getStringExtra("CommonId"));
                bundle.putString("NId", intent.getStringExtra("NId"));
                bundle.putString("NType", intent.getStringExtra("NType"));
            }
            changeFragment("summary", false, bundle);
        } else {
            changeFragment("mendetory", false, null);
        }
    }

    /**
     * Method implementation of AddUnitInterface.
     *
     * @param message
     * @param isAddInBackStack
     * @param bundle
     */
    @Override
    public void changeFragment(String message, Boolean isAddInBackStack, Bundle bundle) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentById(R.id.frame_middel);

        HideKeyboard();
        //Remove Optional from condition there is no need now.
        /*if (message.equals("optional")) {
            if (fragment instanceof AddUnitOptionalInformationFragment) {

            } else {
                Fragment newFragment = new AddUnitOptionalInformationFragment();
                strFragmentTag = newFragment.toString();
                transaction.add(R.id.frame_middel, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else*/
        if (message.equals("mendetory")) {
            if (fragment instanceof AddUnitMandatoryFragment) {

            } else {
                Fragment newFragment = new AddUnitMandatoryFragment();
                strFragmentTag = newFragment.toString();
                transaction.add(R.id.frame_middel, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("summary")) {
            if (fragment instanceof SummaryFragment) {

            } else {
                Fragment newFragment = new SummaryFragment();
                newFragment.setArguments(bundle);
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment, strFragmentTag);
                if (isAddInBackStack) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /**
         * When activity start from notification and already opened this activity.
         */
        if (intent.getStringExtra("addUnit").equalsIgnoreCase("summary")) {
            Bundle bundle = new Bundle();
            if (intent.getStringExtra("summaryData") == null) {
                bundle.putString("CommonId", intent.getStringExtra("CommonId"));
                bundle.putString("NId", intent.getStringExtra("NId"));
                changeFragment("summary", false, bundle);
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

    public void clearBackStack() {
        FragmentManager fm = this.getFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }


    /**
     * If current activity is top most activity then open Dashboard activity on press of back button.
     */
    @Override
    public void onBackPressed() {
        ActivityManager mngr = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskList = mngr.getRunningTasks(10);
        if (taskList.get(0).numActivities == 1 &&
                taskList.get(0).topActivity.getClassName().equals(this.getClass().getName())) {
            Intent intent = new Intent(AddUnitActivity.this, DashboardActivity.class);
            startActivity(intent);
            finish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Back stack change listener
     */
    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                FragmentManager manager = getFragmentManager();
                Fragment currentFragment = manager.findFragmentById(R.id.frame_middel);
                if (currentFragment instanceof AddUnitMandatoryFragment) {
                    try {
                        if (manager != null) {
                            //Remove Related Optional Info.
//                            AddUnitMandatoryFragment currFrag = (AddUnitMandatoryFragment) currentFragment;
//                            currFrag.ReturnFromOptional();
                        }
                    } catch (Exception ex) {
                        Log.e("Exception", "" + ex.getMessage());
                    }
                }
            }
        };
        return result;
    }
}
