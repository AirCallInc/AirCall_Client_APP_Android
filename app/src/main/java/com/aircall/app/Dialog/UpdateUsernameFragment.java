package com.aircall.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Interfaces.UsernameDialogInterfaceWithCompany;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;

/**
 * Created by jd on 11/07/16.
 */
public class UpdateUsernameFragment extends DialogFragment {

    Dialog new_dialog;
    private GlobalClass globalClass;
    View mTransFilterQuantity;
    int i = 1;
    Activity activity;
    ImageView ivClose;
    EditText etUpdateFirstName, etUpdateLastName,etUpdateCompanyName;
    TextView btnSubmit, tvCancel;
    SharedPreferences sharedpreferences;
    UsernameDialogInteface usernameDialogInteface;
    UsernameDialogInterfaceWithCompany usernameDialogIntefaceWithCompany;
    SharedPreferences.Editor editor;
    String firstName = "", lastName = "",companyName="";
    SpannableStringBuilder ssbuilder;

   /* @SuppressLint("ValidFragment")
    public UpdateUsernameFragment(GlobalClass globalClass, String firstName, String lastName, UsernameDialogInteface usernameDialogInteface) {
        this.usernameDialogInteface = usernameDialogInteface;
        this.globalClass = globalClass;
        this.firstName = firstName;
        this.lastName = lastName;
        Log.e("call", "called");
    }*/
    @SuppressLint("ValidFragment")
    public UpdateUsernameFragment(GlobalClass globalClass, String firstName, String lastName,String companyname, UsernameDialogInterfaceWithCompany usernameDialogIntefaceWithCompany) {
//        this.usernameDialogInteface = usernameDialogInteface;
        this.usernameDialogIntefaceWithCompany = usernameDialogIntefaceWithCompany;
        this.globalClass = globalClass;
        this.firstName = firstName;
        this.lastName = lastName;
        this.companyName = companyname;
        Log.e("call", "called");
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_for_update_user_name, null);
        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.setContentView(promptsView);

        sharedpreferences = getActivity().getSharedPreferences("AircallData", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        init();
        clickEvent();

        return new_dialog;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((UserProfileActivity) activity).globalClass;
    }

    public void init() {
        // ivClose = (ImageView)new_dialog.findViewById(R.id.ivCancelDialog);
        etUpdateFirstName = (EditText) new_dialog.findViewById(R.id.etUpdateFirstName);
        etUpdateLastName = (EditText) new_dialog.findViewById(R.id.etUpdateLastName);
        etUpdateCompanyName = (EditText)new_dialog.findViewById(R.id.etUpdateCompanyName);
        btnSubmit = (TextView) new_dialog.findViewById(R.id.tvSubmitUpdateUsername);
        tvCancel = (TextView) new_dialog.findViewById(R.id.tvCancel);
        etUpdateFirstName.setText(firstName);
        etUpdateLastName.setText(lastName);
        etUpdateCompanyName.setText(companyName);
    }

    public void clickEvent() {


        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isValidate()) {
//                    usernameDialogInteface.submitClick(etUpdateFirstName.getText().toString().trim(), etUpdateLastName.getText().toString().trim());
                    usernameDialogIntefaceWithCompany.submitClick(etUpdateFirstName.getText().toString().trim(), etUpdateLastName.getText().toString().trim(),etUpdateCompanyName.getText().toString().trim());
                    new_dialog.dismiss();
                }
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_dialog.dismiss();
            }
        });

    }

    private boolean isValidate() {
        Boolean isValidate = true;
        String estring = "";
        ForegroundColorSpan fgcspan = new ForegroundColorSpan(Color.RED);
        if (etUpdateFirstName.getText().toString().trim().equals("")) {
            ssbuilder = new SpannableStringBuilder(ErrorMessages.FirstName);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etUpdateFirstName.requestFocus();
            etUpdateFirstName.setError(ssbuilder);
            isValidate = false;
        } else if (etUpdateLastName.getText().toString().trim().equals("")) {
            ssbuilder = new SpannableStringBuilder(ErrorMessages.LastName);
            ssbuilder.setSpan(fgcspan, 0, estring.length(), 0);
            etUpdateLastName.requestFocus();
            etUpdateLastName.setError(ssbuilder);
            isValidate = false;
        }
        return isValidate;
    }

}
