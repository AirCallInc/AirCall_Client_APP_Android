package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.RoundedTransformation;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.MenuItemInteface;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;
import com.squareup.picasso.Picasso;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Jd on 27/01/16.
 */
public class DrawerFragment extends Fragment {

    @Bind(R.id.tvDashboard)
    TextView tvDashboard;

    @Bind(R.id.tvUpcomingSchedule)
    TextView tvUpcomingSchedule;

//    @Bind(R.id.tvUnSubmittedUnits)
//    TextView tvUnSubmittedUnits;

    @Bind(R.id.tvPastServices)
    TextView tvPastServices;

    @Bind(R.id.tvMyUnits)
    TextView tvMyUnits;

    @Bind(R.id.tvRequestForServices)
    TextView tvRequestForServices;

    @Bind(R.id.tvPlanCoverage)
    TextView tvPlanCoverage;

    @Bind(R.id.tvTerms)
    TextView tvTerms;

    @Bind(R.id.tvAboutUs)
    TextView tvAboutUs;

    @Bind(R.id.tvContactUs)
    TextView tvContactUs;

    @Bind(R.id.tvLogout)
    TextView tvLogout;

    @Bind(R.id.tvClientNameForProfile)
    TextView tvClientNameForProfile;

    @Bind(R.id.ivCloseSideMenu)
    ImageView ivCloseSideMenu;

    @Bind(R.id.ivClientProfileImage)
    ImageView ivClientProfileImage;

    @Bind(R.id.tvUserProfileUpdate)
    TextView tvUserProfileUpdate;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    private MenuItemInteface menuItemInteface;
    AlertDialog alertDialog;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GlobalClass globalClass;
    Activity activity;

    private ProgressDialogFragment progressDialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_drawer,
                container, false);
        globalClass = (GlobalClass) getActivity().getApplicationContext();
        ButterKnife.bind(this, view);
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        init();
        clickEvents();
        return view;
    }

    private void init() {
        setProfile();
    }


    /**
     *  Click event for all sidebar menus and edit user profile
     */

    public void clickEvents() {
        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "dashboard";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
        tvUpcomingSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "upcoming_schedule";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
//        tvUnSubmittedUnits.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View view) {
//                ((DashboardActivity) getActivity()).strSlider = "unsubmitted_units";
//                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
//            }
//
//        });
        tvPastServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "past_service";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
        tvMyUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "my_units";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
        tvRequestForServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "request_for_service";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
        tvPlanCoverage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "plan_coverage";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "terms";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
        tvAboutUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "about_us";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
        tvContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).strSlider = "contact_us";
                ((DashboardActivity) getActivity()).drawer_layout.closeDrawers();
            }
        });
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (globalClass.checkInternetConnection()) {
                    globalClass.Clientlogout();
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
        });

        ivCloseSideMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((DashboardActivity) getActivity()).closeDrawer();
            }
        });

        tvUserProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).closeDrawer();
                Intent i = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(i);
            }
        });

        tvClientNameForProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).closeDrawer();
                Intent i = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(i);
            }
        });

        ivClientProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).closeDrawer();
                Intent i = new Intent(getActivity(), UserProfileActivity.class);
                startActivity(i);
            }
        });
    }

    /**
     *  Set profile picture and client name every time when drawer open if in case of it changed from user profile
     */
    public void setProfile() {
        String imagePath = sharedpreferences.getString("ProfileImage", "");

        if (!imagePath.equalsIgnoreCase("")) {
            Picasso.with(getActivity())
                    .load(imagePath)
                    .resize(200, 200)
                    .transform(new RoundedTransformation(1700, 0))
                    .placeholder(R.drawable.placeholder_img)
                    .into(ivClientProfileImage);
        }

        String name = sharedpreferences.getString("FirstName", "") + " " + sharedpreferences.getString("LastName", "");
        tvClientNameForProfile.setText(name);
        if (name.length() <= 20) {
            tvClientNameForProfile.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(22));
            tvUserProfileUpdate.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(20));
        }

        if (name.length() > 20) {
            tvClientNameForProfile.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(20));
            tvUserProfileUpdate.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(18));
        }

        if (name.length() > 25) {
            tvClientNameForProfile.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(18));
            tvUserProfileUpdate.setTextSize(TypedValue.COMPLEX_UNIT_PX, dpToPx(16));
        }
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = activity.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        menuItemInteface = (MenuItemInteface) activity;
        globalClass = ((DashboardActivity) activity).globalClass;
        this.activity = activity;
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