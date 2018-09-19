package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.Signininterface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.LoginActivity;
import com.aircall.app.Model.SignUp.SignUpResponce;
import com.aircall.app.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jd on 25/02/16.
 */
public class SignupFragment extends Fragment {


    @Bind(R.id.img_back)
    ImageView ivBack;


    @Bind(R.id.etFirstName)
    EditText etFirstName;

    @Bind(R.id.etLastName)
    EditText etLastName;

    @Bind(R.id.etCompanyName)
    EditText etCompanyName;

    @Bind(R.id.etEmail)
    EditText etEmail;


    @Bind(R.id.etPassword)
    EditText etPassword;

    @Bind(R.id.etReEnterPassword)
    EditText etReEnterPassword;

    @Bind(R.id.etPhone)
    EditText etPhone;

    @Bind(R.id.etPartnerCode)
    EditText etPartnerCode;

    @Bind(R.id.btnSignUp)
    Button btnSignUp;

    @Bind(R.id.llMoreThenTen)
    LinearLayout llMoreThenTen;

    @Bind(R.id.llLessThenTen)
    LinearLayout llLessThenTen;

    @Bind(R.id.llDoNotKnow)
    LinearLayout llDoNotKnow;

    @Bind(R.id.llMain)
    LinearLayout llMain;

    @Bind(R.id.ivMoreThenTen)
    ImageView ivMoreThenTen;

    @Bind(R.id.ivLessThenTen)
    ImageView ivLessThenTen;

    @Bind(R.id.ivDoNotKnow)
    ImageView ivDoNotKnow;

    @Bind(R.id.cbAgreeTerm)
    CheckBox cbAgreeTerm;

    @Bind(R.id.tvTerms)
    TextView tvTerms;


    GlobalClass globalClass;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Signininterface signininterface;
    Activity activity;
    String HVAC_Unit;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_signup,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvent();
        return view;
    }

    private void init() {
        editor = sharedpreferences.edit();
        etPassword.setTypeface(Typeface.DEFAULT);
        etReEnterPassword.setTypeface(Typeface.DEFAULT);
        HVAC_Unit = getResources().getString(R.string.HVAC_donot_know);

        SpannableString content = new SpannableString(getResources().getString(R.string.payment_method_term));
        content.setSpan(new UnderlineSpan(), 0, getResources().getString(R.string.payment_method_term).length(), 0);
        tvTerms.setText(content);
    }

    public void clickEvent() {
        llMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        /**
         * on Click of llMoreThenTen,llLessThenTen,llDoNotKnow show radio button as per design requirements and
         * save string in HVAC_Unit variable.
         *
         */

        llMoreThenTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivMoreThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_selected, activity.getTheme()));
                    ivLessThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton, activity.getTheme()));
                    ivDoNotKnow.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton, activity.getTheme()));
                } else {
                    ivMoreThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_selected));
                    ivLessThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton));
                    ivDoNotKnow.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton));
                }
                HVAC_Unit = getResources().getString(R.string.HVAC_more_then_ten);

            }
        });

        llLessThenTen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivLessThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_selected, activity.getTheme()));
                    ivMoreThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton, activity.getTheme()));
                    ivDoNotKnow.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton, activity.getTheme()));
                } else {
                    ivLessThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_selected));
                    ivMoreThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton));
                    ivDoNotKnow.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton));
                }
                HVAC_Unit = getResources().getString(R.string.HVAC_less_then_ten);
            }
        });

        llDoNotKnow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ivDoNotKnow.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_selected, activity.getTheme()));
                    ivLessThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton, activity.getTheme()));
                    ivMoreThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton, activity.getTheme()));
                } else {
                    ivDoNotKnow.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton_selected));
                    ivLessThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton));
                    ivMoreThenTen.setImageDrawable(getResources().getDrawable(R.drawable.radiobutton));
                }
                HVAC_Unit = getResources().getString(R.string.HVAC_donot_know);
            }
        });

        tvTerms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, DashboardActivity.class);
                intent.putExtra("dash", "terms");
                intent.putExtra("pageid", "4");
                startActivity(intent);
            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().onBackPressed();
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validation();
            }
        });
    }

    /**
     * Check all the require validation if all information is filled up then call webAPI for registration otherwise
     * give error message.
     */

    public void validation() {
        String estring = "";
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
        SpannableStringBuilder ssbuilder;
        if (etFirstName.getText().toString().trim().length() == 0) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.FirstName);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etFirstName.requestFocus();
            etFirstName.setError(ssbuilder);

        } else if (etLastName.getText().toString().trim().length() == 0) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.LastName);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etLastName.requestFocus();
            etLastName.setError(ssbuilder);

        } else if (etPhone.getText().toString().trim().length() == 0) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.PhonNumber);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etPhone.requestFocus();
            etPhone.setError(ssbuilder);

        } else if (etEmail.getText().toString().trim().length() == 0) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.Email);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etEmail.requestFocus();
            etEmail.setError(ssbuilder);

        }
        //!emailbusiness.matches(emailPattern)
        //else if (!(GlobalClass.isValidEmail(etEmail.getText().toString().trim()))) {
        else if (!etEmail.getText().toString().trim().matches(emailPattern)) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidEmail);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etEmail.requestFocus();
            etEmail.setError(ssbuilder);


        } else if (etPassword.getText().toString().trim().length() == 0) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.Password);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etPassword.requestFocus();
            etPassword.setError(ssbuilder);

        } else if (etPassword.getText().toString().trim().contains(" ")) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.SpaceInPassword);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etPassword.requestFocus();
            etPassword.setError(ssbuilder);

        } else if (etPassword.getText().toString().trim().length() < 6) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.ValidPassowrd);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etPassword.requestFocus();
            etPassword.setError(ssbuilder);

        } else if (etReEnterPassword.getText().toString().trim().length() == 0) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.ReTypePassword);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etReEnterPassword.requestFocus();
            etReEnterPassword.setError(ssbuilder);

        } else if (!etReEnterPassword.getText().toString().trim().equalsIgnoreCase(etPassword.getText().toString().trim())) {

            ssbuilder = new SpannableStringBuilder(ErrorMessages.NotMatchPassword);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etReEnterPassword.requestFocus();
            etReEnterPassword.setError(ssbuilder);

        } else if (!cbAgreeTerm.isChecked()) {
            DialogFragment ds = new SingleButtonAlert(ErrorMessages.Agree,
                    new DialogInterfaceClick() {
                        @Override
                        public void dialogClick(String tag) {

                        }
                    });
            ds.setCancelable(false);
            ds.show(getFragmentManager(), "");
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
            registerwebservice();
        }
    }

    /**
     * Calling webAPI for registration of client.
     * if client registered successfully : save client information in sharedpreferences and move to Dashboard.
     * if client can not registered successfully : Show error message.
     */
    private void registerwebservice() {
        ((LoginActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class);
        //Removed HVAC_Unit,
        webserviceApi.signUp(etFirstName.getText().toString().trim(), etLastName.getText().toString().trim(), etCompanyName.getText().toString().trim(), etEmail.getText().toString().trim(),
                globalClass.getMD5EncryptedString(etPassword.getText().toString().trim()), etPhone.getText().toString().trim(), etPartnerCode.getText().toString().trim(),
                "Android", sharedpreferences.getString("GcmToken", ""), new Callback<SignUpResponce>() {
                    @Override
                    public void success(SignUpResponce signUpResponce, Response response) {
                        ((LoginActivity) getActivity()).hideProgressDialog();


                        if (signUpResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {

                            editor.putString("Token", signUpResponce.Token);
                            editor.putString("Id", signUpResponce.Data.Id);
                            editor.putString("Email", signUpResponce.Data.Email);
                            editor.putString("FirstName", signUpResponce.Data.FirstName);
                            editor.putString("LastName", signUpResponce.Data.LastName);
                            editor.putString("Company", signUpResponce.Data.Company);
                            editor.putString("MobileNumber",etPhone.getText().toString().trim());
                            editor.apply();

                            Handler handler = new Handler();
                            final Runnable r = new Runnable() {
                                public void run() {
                                    Intent intent = new Intent(getActivity(), DashboardActivity.class);
                                    intent.putExtra("From", "SignUp");
                                    startActivity(intent);
                                    activity.finish();
                                }
                            };
                            handler.postDelayed(r, 350);

                        } else {
                            DialogFragment ds = new SingleButtonAlert(signUpResponce.Message,
                                    new DialogInterfaceClick() {
                                        @Override
                                        public void dialogClick(String tag) {

                                        }
                                    });
                            ds.setCancelable(true);
                            ds.show(getFragmentManager(), "");
                            /*etFirstName.setText("");
                            etLastName.setText("");
                            etEmail.setText("");
                            etPassword.setText("");
                            etPhone.setText("");*/
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
        sharedpreferences = activity.getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
    }


}
