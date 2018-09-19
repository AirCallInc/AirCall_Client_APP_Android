package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.ForgotPasswordFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.Signininterface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.LoginActivity;
import com.aircall.app.Model.Login.LoginResponce;
import com.aircall.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jd on 25/02/16.
 */
public class SignInFragment extends Fragment {

    @Bind(R.id.edt_email)
    EditText edt_email;

    @Bind(R.id.rootLayout)
    RelativeLayout rootLayout;

    @Bind(R.id.edt_password)
    EditText edt_password;

    @Bind(R.id.tvSignIn)
    TextView tvSignIn;

    @Bind(R.id.tvSignup)
    TextView tvSignup;

    @Bind(R.id.ivSavePassword)
    ImageView ivSavePassword;

    @Bind(R.id.txt_forgot)
    TextView txt_forgot;

    @Bind(R.id.llEmailContainer)
    LinearLayout llEmailContainer;

    @Bind(R.id.llSavePassword)
    LinearLayout llSavePassword;

    @Bind(R.id.llPasswordContainer)
    LinearLayout llPasswordContainer;


    Signininterface signininterface;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Animation fadeIn, fadeOut;
    Boolean savePass = false;
    GlobalClass globalClass;
    Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signin,
                container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvent();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        slideFromMiddleToRight(llEmailContainer, edt_email, 0);
        slideFromMiddleToRight(llPasswordContainer, edt_password, 0);

        fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator());
        fadeIn.setDuration(1500);

        fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(400);

        edt_password.setTypeface(Typeface.DEFAULT);

        /**
         * Set email and password in EditText from sharedpreferences.
         */
        edt_email.setText(sharedpreferences.getString("saved_username",""));
        edt_password.setText(sharedpreferences.getString("saved_password",""));

        if(sharedpreferences.getString("saved_username","").equalsIgnoreCase("")) {
            ivSavePassword.setImageResource(R.drawable.checkbox);
            savePass = false;
        } else {
            ivSavePassword.setImageResource(R.drawable.checkbox_selected);
            savePass = true;
        }

        /**
         * Show fields with animation
         */
        Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                slideFromRightToMiddle(llEmailContainer, edt_email, 250);
                slideFromRightToMiddle(llPasswordContainer, edt_password, 300);
                edt_email.setVisibility(View.VISIBLE);
                edt_password.setVisibility(View.VISIBLE);

                tvSignIn.setAnimation(fadeIn);
                txt_forgot.setAnimation(fadeIn);
                tvSignup.setAnimation(fadeIn);
                llSavePassword.setAnimation(fadeIn);

                txt_forgot.setVisibility(View.VISIBLE);
                tvSignup.setVisibility(View.VISIBLE);
                tvSignIn.setVisibility(View.VISIBLE);
                llSavePassword.setVisibility(View.VISIBLE);
            }
        };
        handler.postDelayed(r, 100);
    }

    public void clickEvent() {
        rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        tvSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signininterface.itemClick("signup");
            }
        });

        llSavePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (savePass) {
                    ivSavePassword.setImageResource(R.drawable.checkbox);
                } else {
                    ivSavePassword.setImageResource(R.drawable.checkbox_selected);
                }
                savePass = !savePass;
            }
        });

        tvSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String estring = "";
                ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
                SpannableStringBuilder ssbuilder;
                if (edt_email.getText().toString().trim().length() == 0) {

                    ssbuilder = new SpannableStringBuilder(ErrorMessages.Email);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    edt_email.requestFocus();
                    edt_email.setError(ssbuilder);

                } else if (!(GlobalClass.isValidEmail(edt_email.getText().toString().trim()))) {

                    ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidEmail);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    edt_email.requestFocus();
                    edt_email.setError(ssbuilder);

                } else if (edt_password.getText().toString().trim().length() == 0) {

                    ssbuilder = new SpannableStringBuilder(ErrorMessages.Password);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    edt_password.requestFocus();
                    edt_password.setError(ssbuilder);

                } else if (edt_password.getText().toString().trim().length() < 6) {
                    ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidPassowrd);
                    ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
                    edt_password.requestFocus();
                    edt_password.setError(ssbuilder);

                } else if (!globalClass.checkInternetConnection()) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(false);
                    ds.show(getFragmentManager(), "");
                } else {
                    loginservice(edt_email.getText().toString().trim(), edt_password.getText().toString().trim());
                }
            }
        });
        txt_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DialogFragment ds = new ForgotPasswordFragment(globalClass,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {
                                if (!globalClass.checkInternetConnection()) {
                                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.NoInternet,
                                            new DialogInterfaceClick() {
                                                @Override
                                                public void dialogClick(String tag) {

                                                }
                                            });
                                    ds.setCancelable(false);
                                    ds.show(getFragmentManager(), "");
                                } else {
                                    Toast.makeText(activity, tag, Toast.LENGTH_LONG).show();
                                }

                            }
                        });
                ds.show(getFragmentManager(), "");
            }
        });
    }

    /**
     * Method TranslateAnimation of views
     *
     * @param view1 View in which animation suppose to happened
     * @param view2 Component which we want to animate
     * @param duration Animation Time duration
     */

    public void slideFromRightToMiddle(View view1, View view2, int duration) {
        TranslateAnimation animate1 = new TranslateAnimation(view1.getWidth(),
                0, 0, 0);
        animate1.setDuration(duration);
        view2.startAnimation(animate1);
    }

    public void slideFromMiddleToLeft(View view1, View view2, int duration) {
        TranslateAnimation animate1 = new TranslateAnimation(0,
                -view1.getWidth(), 0, 0);
        animate1.setDuration(duration);
        view2.startAnimation(animate1);
    }

    public void slideFromMiddleToRight(View view1, View view2, int duration) {
        TranslateAnimation animate1 = new TranslateAnimation(0,
                view1.getWidth(), 0, 0);
        animate1.setDuration(duration);
        view2.startAnimation(animate1);
    }

    /**
     * Call WebAPI for user login.
     * if client registered successfully : save client information in sharedpreferences and move to Dashboard.
     * if client can not registered successfully : Show error message.
     *
     * @param Email user Email
     * @param Password user Password
     */
    public void loginservice(String Email, String Password) {

        ((LoginActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class);

        Log.e("Device Token=",sharedpreferences.getString("GcmToken", ""));
        webserviceApi.login(Email, globalClass.getMD5EncryptedString(Password), "Android", sharedpreferences.getString("GcmToken", ""), new Callback<LoginResponce>() {
            @Override
            public void success(LoginResponce loginResponses, Response response) {
                ((LoginActivity) getActivity()).hideProgressDialog();


                if (loginResponses.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    editor.putString("Token", loginResponses.Token);
                    editor.putString("Id", loginResponses.Data.Id);
                    editor.putString("Email", loginResponses.Data.Email);
                    editor.putString("FirstName", loginResponses.Data.FirstName);
                    editor.putString("LastName", loginResponses.Data.LastName);
                    //Added
                    editor.putString("Company", loginResponses.Data.Company);
                    editor.putString("ProfileImage", loginResponses.Data.ProfileImage);
                    editor.putString("MobileNumber",loginResponses.Data.MobileNumber);

                    if (savePass) {
                        editor.putString("saved_username", loginResponses.Data.Email);
                        editor.putString("saved_password", edt_password.getText().toString());
                    } else {
                        editor.putString("saved_username", "");
                        editor.putString("saved_password", "");
                    }
                    editor.apply();

                    Handler handler = new Handler();
                    final Runnable r = new Runnable() {
                        public void run() {
                            edt_email.setVisibility(View.GONE);
                            edt_password.setVisibility(View.GONE);

                            Intent intent = new Intent(getActivity(), DashboardActivity.class);
                            intent.putExtra("From", "LogIn");
                            startActivity(intent);
                            activity.finish();
                        }
                    };
                    handler.postDelayed(r, 350);

                    slideFromMiddleToLeft(llEmailContainer, edt_email, 350);
                    slideFromMiddleToLeft(llPasswordContainer, edt_password, 400);

                    txt_forgot.setVisibility(View.GONE);
                    tvSignup.setVisibility(View.GONE);
                    tvSignIn.setVisibility(View.GONE);
                    llSavePassword.setVisibility(View.GONE);

                    //tvSignIn.setAnimation(fadeOut);
                    //txt_forgot.setAnimation(fadeOut);
                    //tvSignup.setAnimation(fadeOut);

                } else {
                    DialogFragment ds = new SingleButtonAlert(loginResponses.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                    edt_password.setText("");
                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((LoginActivity) getActivity()).hideProgressDialog();
                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(true);
                ds.show(getFragmentManager(), "");

                Log.e("failor", "failor " + error.getMessage());
            }
        });

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        globalClass = ((LoginActivity) activity).globalClass;
        signininterface = ((Signininterface) activity);
        this.activity = activity;
    }
}
