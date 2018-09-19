package com.aircall.app;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Fragment.AboutUsFragment;
import com.aircall.app.Fragment.ContactUsFragment;
import com.aircall.app.Fragment.DashboardFragment;
import com.aircall.app.Fragment.DrawerFragment;
import com.aircall.app.Fragment.MyUnitsFragment;
import com.aircall.app.Fragment.NotificationListFragment;
import com.aircall.app.Fragment.PastServiceDetailFragment;
import com.aircall.app.Fragment.PastServiceFragment;
import com.aircall.app.Fragment.PlanCoverageFragment;
import com.aircall.app.Fragment.RequestForServiceFragment;
import com.aircall.app.Fragment.TermsFragment;
import com.aircall.app.Fragment.UpcomingSchedualeDetailFragment;
import com.aircall.app.Fragment.UpcomingSchesuleFragment;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.MenuItemInteface;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DashboardActivity extends FragmentActivity implements MenuItemInteface {

    @Bind(R.id.drawer_layout)
    public DrawerLayout drawer_layout;

    @Bind(R.id.frame_middel)
    public FrameLayout frame_middel;


    private Toolbar toolbar;
    public GlobalClass globalClass;
    private ProgressDialogFragment progressDialogFragment;
    private String strFragmentTag;
    public String strSlider = "";
    public String last = "";
    public Boolean fromOtherActivity = false;
    public Boolean isNotification = false;
    public Boolean isFromSignUp = false;
    public Bundle bundle;
    public String Pdfurl = "";
    public String PageTitle = "";
    public String TermsFromDrawer = "3", TermsFromSignup = "5";
    public boolean termsfromDrawer = false, termsfromSignup = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        ButterKnife.bind(this);

        init();
        /**
         * Set Listener of change in back stack
         */
        getFragmentManager().addOnBackStackChangedListener(getListener());
        getSupportFragmentManager().addOnBackStackChangedListener(getSupportListener());

        drawer_layout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                /**
                 * Call method from DrawerFragment to set client image and client name.
                 */
                HideKeyboard();
                Fragment fragment = getFragmentManager().findFragmentById(
                        R.id.frmLeftDrawer);
                ((DrawerFragment) fragment).setProfile();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                if (strSlider.equalsIgnoreCase("dashboard")) {
                    strSlider = "";
                    menuitemClick("dashboard", "");
                } else if (strSlider.equalsIgnoreCase("upcoming_schedule")) {
                    strSlider = "";
                    menuitemClick("upcoming_schedule", "");
                }  else if (strSlider.equalsIgnoreCase("unsubmitted_units")) {
                    strSlider = "";
                    menuitemClick("unsubmitted_units", "");
                }else if (strSlider.equalsIgnoreCase("past_service")) {
                    strSlider = "";
                    menuitemClick("past_service", "");
                } else if (strSlider.equalsIgnoreCase("my_units")) {
                    strSlider = "";
                    menuitemClick("my_units", "");
                } else if (strSlider.equalsIgnoreCase("request_for_service")) {
                    strSlider = "";
                    menuitemClick("request_for_service", "");
                } else if (strSlider.equalsIgnoreCase("plan_coverage")) {
                    strSlider = "";
                    menuitemClick("plan_coverage", "");
                } else if (strSlider.equalsIgnoreCase("terms")) {
                    strSlider = "";
                    termsfromDrawer = true;
                    menuitemClick("terms", "");
                } else if (strSlider.equalsIgnoreCase("about_us")) {
                    strSlider = "";
                    menuitemClick("about_us", "");
                } else if (strSlider.equalsIgnoreCase("contact_us")) {
                    strSlider = "";
                    menuitemClick("contact_us", "");
                }

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }

    private void init() {
        globalClass = (GlobalClass) getApplicationContext();
        Intent intent = getIntent();
        /**
         * Check if start activity from SignUp fragment, if yes : set isFromSignUp is true for use in DashboardFragment
         * to show welcome message.
         */
        if (intent.getStringExtra("PdfUrl") != null) {
            Pdfurl = intent.getStringExtra("PdfUrl");
        }

        if (intent.getStringExtra("PageTitle") != null) {
            PageTitle = intent.getStringExtra("PageTitle");
        }

        if (intent.getStringExtra("pageid") != null) {
            termsfromSignup = true;
            TermsFromSignup = intent.getStringExtra("pageid");
        }

        if (intent.getStringExtra("From") != null && intent.getStringExtra("From").equalsIgnoreCase("SignUp")) {
            isFromSignUp = true;
        }
        if (intent.getStringExtra("dash") == null) {
            menuitemClick("dashboard", "");
        } else if (intent.getStringExtra("dash").equalsIgnoreCase("upcoming")) {
            menuitemClick("upcoming_schedule", "");
        } else if (intent.getStringExtra("dash").equalsIgnoreCase("upcoming_schedule_detail")) {
            /**
             *  When tap on scheduled service notification and start dashboard activity for display service detail.
             */
            bundle = new Bundle();
            bundle.putString("NId", intent.getStringExtra("NId"));
            bundle.putString("CommonId", intent.getStringExtra("CommonId"));
            bundle.putString("NType", intent.getStringExtra("NType"));
            isNotification = true;
            menuitemClick("upcoming_schedule_detail", "");
        } else if (intent.getStringExtra("dash").equalsIgnoreCase("PastServiceDetailFragment")) {
            /**
             *  When tap on past service notification and start dashboard activity for display past service detail
             *  and give rate-review.
             */
            bundle = new Bundle();
            bundle.putString("NId", intent.getStringExtra("NId"));
            bundle.putString("CommonId", intent.getStringExtra("CommonId"));
            bundle.putString("NType", intent.getStringExtra("NType"));
            isNotification = true;
            menuitemClick("PastServiceDetailFragment", "");
        } else if (intent.getStringExtra("dash").equalsIgnoreCase("NotificationListing")) {
            /**
             * When tap on friendly reminder notification start activity to show notification listing.
             */
            isNotification = true;
            menuitemClick("NotificationListFragment", "");
        } else if (intent.getStringExtra("dash").equalsIgnoreCase("terms")) {
            /**
             * When start activity for display terms and condition (When open TermsFragment other then drawer)
             */
            fromOtherActivity = true;
            menuitemClick("terms", "");
        }

        //initNavigationDrawer(strFragmentTag);
    }

    /**
     * Implementation of method of MenuItemInteface.
     * <p>
     * If current fragment same as requested fragment then it will not load again.
     *
     * @param message key of which Fragment want to load.
     * @param str_id
     */

    @Override
    public void menuitemClick(String message, String str_id) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        Fragment fragment = getFragmentManager().findFragmentById(
                R.id.frame_middel);
        HideKeyboard();
        if (message.equalsIgnoreCase("closeDrawer")) {

        } else if (message.equals("dashboard")) {
            FragmentManager fm = getFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new DashboardFragment();
                strFragmentTag = newFragment.toString();
                transaction.add(R.id.frame_middel, newFragment,
                        strFragmentTag);
                transaction.commit();
            }
        } else if (message.equals("upcoming_schedule")) {

            if (last.equalsIgnoreCase(message)) {
                Log.e("upcoming_schedule", "in If");
            } else {

                Fragment newFragment = new UpcomingSchesuleFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }

        } //else if(message.equals("unsubmitted_units")) {
//            if (last.equalsIgnoreCase(message)) {
//            } else {
//                    Fragment newFragment = new SummaryNFrament();
//                    strFragmentTag = newFragment.toString();
//                    transaction.replace(R.id.frame_middel, newFragment, strFragmentTag);
//                    if (last.equalsIgnoreCase("dashboard")) {
//                        transaction.addToBackStack("");
//                    }
//
//                    transaction.commit();
//
//            }
//
//        }
        else if (message.equals("upcoming_schedule_detail")) {

            if (last.equalsIgnoreCase(message)) {
            } else {

                Fragment newFragment = new UpcomingSchedualeDetailFragment();
                newFragment.setArguments(bundle);
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard") || last.equals("NotificationListFragment")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }

        } else if (message.equals("past_service")) {
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new PastServiceFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("PastServiceDetailFragment")) {

            if (last.equalsIgnoreCase(message)) {
            } else {
                Fragment newFragment = new PastServiceDetailFragment();
                newFragment.setArguments(bundle);
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                /*if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }*/
                transaction.addToBackStack("");
                transaction.commit();
            }

        } else if (message.equals("my_units")) {
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new MyUnitsFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("request_for_service")) {
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new RequestForServiceFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("plan_coverage")) {
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new PlanCoverageFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("terms")) {
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new TermsFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);

                Bundle bundle = new Bundle();
                if (termsfromDrawer) {
                    termsfromDrawer = false;
                    bundle.putString("pageid", TermsFromDrawer);
                } else if (termsfromSignup) {
                    termsfromSignup = false;
                    bundle.putString("pageid", TermsFromSignup);
                }
                newFragment.setArguments(bundle);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("about_us")) {
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new AboutUsFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("contact_us")) {
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new ContactUsFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        } else if (message.equals("NotificationListFragment")) {
            if (last.equalsIgnoreCase(message)) {

            } else {
                Fragment newFragment = new NotificationListFragment();
                strFragmentTag = newFragment.toString();
                transaction.replace(R.id.frame_middel, newFragment,
                        strFragmentTag);
                if (last.equalsIgnoreCase("dashboard")) {
                    transaction.addToBackStack("");
                }
                transaction.commit();
            }
        }
        last = message;
    }

    public void openDrawer() {
        drawer_layout.openDrawer(Gravity.LEFT);
    }

    public void closeDrawer() {
        drawer_layout.closeDrawer(Gravity.LEFT);
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

    /**
     * When click on particular notification, Screen changes from this method.
     *
     * @param NType    Notification type
     * @param NId      Notification Id
     * @param CommonId Common Id
     */

    public void notificationClick(String NType, String NId, String CommonId) {
        if (NType != null) {
            if (NType.equalsIgnoreCase(globalClass.NTYPE_SCHEDULE) || NType.equalsIgnoreCase(globalClass.NTYPE_RESCHEDULE)
                    || NType.equalsIgnoreCase(globalClass.NTYPE_SERVICE_REMINDER)) {

                bundle = new Bundle();
                bundle.putString("NId", NId);
                bundle.putString("NType", NType);
                bundle.putString("CommonId", CommonId);
                //isNotification = true;
                menuitemClick("upcoming_schedule_detail", "");

            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_FRIENDLY_REMINDER)
                    || NType.equalsIgnoreCase(globalClass.NTYPE_SUBSCRIPTION_INVOICE_PAYMENT_DUE_REMINDER)
                    || NType.equalsIgnoreCase(globalClass.NTYPE_PAST_DUE_REMINDER)) {

            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_NO_SHOW) || NType.equalsIgnoreCase(globalClass.NTYPE_LATE_CANCELLED)) { //Added Late Cancelled.
                Intent intent = new Intent(this, NoShowActivity.class);
                intent.putExtra("NId", NId);
                intent.putExtra("CommonId", CommonId);
                startActivity(intent);
                overridePendingTransition(R.anim.slid_in_left, R.anim.slid_out_left);

            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_PART_PURCHASE)) {

                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("NId", NId);
                intent.putExtra("CommonId", CommonId);
                intent.putExtra("IsPaymentFail", false);
                startActivity(intent);
                overridePendingTransition(R.anim.slid_in_left, R.anim.slid_out_left);

            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_RATE_SERVICE)) {

                bundle = new Bundle();
                bundle.putString("NId", NId);
                bundle.putString("CommonId", CommonId);
                menuitemClick("PastServiceDetailFragment", "");

            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_CARD_EXPIRE)) {
                Intent intent = new Intent(this, AddNewCardActivity.class);
                intent.putExtra("NId", NId);
                intent.putExtra("CommonId", CommonId);
                startActivity(intent);
                overridePendingTransition(R.anim.slid_in_left, R.anim.slid_out_left);

            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_RENEW_UNIT)) {
                Intent intent = new Intent(this, AddUnitActivity.class);
                intent.putExtra("NId", NId);
                intent.putExtra("CommonId", CommonId);
                intent.putExtra("addUnit", "summary");
                intent.putExtra("NType", globalClass.NTYPE_RENEW_UNIT);
                startActivity(intent);
                overridePendingTransition(R.anim.slid_in_left, R.anim.slid_out_left);

            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_PAYMENT_FAILED)) {
                Intent intent = new Intent(this, AddUnitActivity.class);
                intent.putExtra("NId", NId);
                intent.putExtra("CommonId", CommonId);
                intent.putExtra("addUnit", "summary");
                intent.putExtra("NType", globalClass.NTYPE_PAYMENT_FAILED);
                startActivity(intent);
                overridePendingTransition(R.anim.slid_in_left, R.anim.slid_out_left);

            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_INVOICE_PAYMENT_FAIL)) {
                Intent intent = new Intent(this, UserProfileActivity.class);
                intent.putExtra("NId", NId);
                intent.putExtra("CommonId", CommonId);
                intent.putExtra("IsPaymentFail", true);
                startActivity(intent);
                overridePendingTransition(R.anim.slid_in_left, R.anim.slid_out_left);
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (fromOtherActivity) {
            /**
             * Ex. If Activity started for Terms and condition fragment from other activity, then will finish when
             * user press close button or back button.
             */
            finish();
        } else if (isNotification) {
            /**
             * If Activity start from notification on tap, open dashboard on back event
             */
            isNotification = false;
            menuitemClick("dashboard", "");
        } else if (last.equalsIgnoreCase("dashboard")) {
            /**
             * If user in dashboard fragment, activity will be finish and application need to close
             */
            finish();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * Method calls when user already have in Dashboard and again try to open Dashboard Activity.
     * Ex. User in Dashboard screen and tap on notification for reschedule.
     *
     * @param intent Intent data
     */

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent != null && intent.getStringExtra("dash").equalsIgnoreCase("upcoming_schedule_detail")) {
            bundle = new Bundle();
            bundle.putString("NId", intent.getStringExtra("NId"));
            bundle.putString("CommonId", intent.getStringExtra("CommonId"));
            isNotification = true;
            menuitemClick("upcoming_schedule_detail", "");
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (last.equalsIgnoreCase("")) {
            onBackPressed();
        } else if (last.equalsIgnoreCase("dashboard")) {
            /**
             *  Load DashboardFragment Fragment data again when come back from other activity to Dashboard activity.
             */
            /*FragmentManager manager = getFragmentManager();
            Fragment currentFragment = manager.findFragmentById(R.id.frame_middel);
            if (currentFragment instanceof DashboardFragment) {
                ((DashboardFragment) currentFragment).getDashboardData();
            }*/
        } else if (last.equalsIgnoreCase("my_units")) {
            /**
             *  Load MyUnitsFragment data again when come back from other activity to Dashboard activity.
             */
            FragmentManager manager = getFragmentManager();
            Fragment currentFragment = manager.findFragmentById(R.id.frame_middel);
            if (currentFragment instanceof MyUnitsFragment) {
                ((MyUnitsFragment) currentFragment).callForMyUnit();
            }
        }
    }

    private FragmentManager.OnBackStackChangedListener getListener() {
        FragmentManager.OnBackStackChangedListener result = new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    //setLastFromBackState();
                    FragmentManager manager = getFragmentManager();
                    Fragment currentFragment = manager.findFragmentById(R.id.frame_middel);
                    if (currentFragment instanceof NotificationListFragment) {
                        last = "NotificationListFragment";
                    } else if (currentFragment instanceof DashboardFragment) {
                        if (last.equalsIgnoreCase("UnitDetailFragment")) {
                            /**
                             *  Load DashboardFragment Fragment data again when come back from
                             *  UnitDetailFragment to Dashboard activity.
                             */
                            if (globalClass.checkInternetConnection()) {
                                ((DashboardFragment) currentFragment).getDashboardData();
                            } else {
                                DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                                        new DialogInterfaceClick() {
                                            @Override
                                            public void dialogClick(String tag) {
                                            }
                                        });
                                ds.setCancelable(false);
                                ds.show(getFragmentManager(), "");
                            }
                        }
                        last = "dashboard";
                        //((DashboardFragment) currentFragment).getDashboardData();
                    } else if (currentFragment instanceof MyUnitsFragment) {
                        if (last.equalsIgnoreCase("UnitDetailFragment")) {
                            /**
                             *  Load MyUnitsFragment data again when come back from
                             *  UnitDetailFragment to Dashboard activity (Incase of client change unit name).
                             */
                            ((MyUnitsFragment) currentFragment).callForMyUnit();
                        }
                        last = "my_units";
                    } else if (currentFragment instanceof UpcomingSchesuleFragment) {
                        if (last.equalsIgnoreCase("upcoming_schedule_detail")) {
                            /**
                             *  Load UpcomingSchesuleFragment Fragment data again when come back after reschedule any service.
                             */
                            ((UpcomingSchesuleFragment) currentFragment).upcomingServices();
                        }
                        last = "upcoming_schedule";
                    }
                } catch (Exception ex) {

                }
            }
        };
        return result;
    }

    private android.support.v4.app.FragmentManager.OnBackStackChangedListener getSupportListener() {
        android.support.v4.app.FragmentManager.OnBackStackChangedListener result = new android.support.v4.app.FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                try {
                    //setLastFromBackState();
                    FragmentManager manager = getFragmentManager();
                    Fragment currentFragment = manager.findFragmentById(R.id.frame_middel);
                    if (currentFragment instanceof NotificationListFragment) {
                        last = "NotificationListFragment";
                    } else if (currentFragment instanceof DashboardFragment) {
                        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                            if (last.equalsIgnoreCase("upcoming_schedule_detail")) {
                                ((DashboardFragment) currentFragment).getDashboardData();
                            }
                            last = "dashboard";
                        }
                    }
                } catch (Exception ex) {

                }
            }

        };
        return result;
    }

    private void setLastFromBackState() {
        FragmentManager manager = getFragmentManager();
        Fragment currentFragment = manager.findFragmentById(R.id.frame_middel);
        if (currentFragment instanceof NotificationListFragment) {
            last = "NotificationListFragment";
        } else if (currentFragment instanceof DashboardFragment) {
            last = "dashboard";
        }
    }
}
