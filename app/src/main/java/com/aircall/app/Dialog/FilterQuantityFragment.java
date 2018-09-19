package com.aircall.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Fragment.AddUnitOptionalInformationFragment;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.R;

/**
 * Created by jd on 11/07/16.
 */

public class FilterQuantityFragment extends DialogFragment {
    private DialogInterfaceClick dialogInterface;
    Dialog new_dialog;
    private GlobalClass globalClass;
    View mTransFilterQuantity;
    int i = 1;
    String Quantity;


    AddUnitOptionalInformationFragment mAddUnitOptionalInformationFragment;
    RelativeLayout rlQuantityZero, mRlQuantityOne, mRlQuantityTwo, mRlQuantityThree, mRlQuantityFour,
            mRlQuantityFive, mRlQuantitySix;
    ImageView ivCheckForZero, mIvCheckForOne, mIvCheckForTwo, mIvCheckForThree, mIvCheckForFour, mIvCheckForFive, mIvCheckForSix;
    TextView HeaderName;

    @SuppressLint("ValidFragment")
    public FilterQuantityFragment(GlobalClass globalClass, String Quantity, String iValue, DialogInterfaceClick dialogInterface) {
        this.dialogInterface = dialogInterface;
        this.globalClass = globalClass;
        this.Quantity = Quantity;
        try{
            i = Integer.parseInt(iValue);
        } catch (Exception ex) {
            i = 0;
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_for_filter_quantity, null);
        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.setContentView(promptsView);
        ImageView ivClose = (ImageView) new_dialog.findViewById(R.id.ivCancelDialog);

        init();
        clickEvent();
        checkQuantityLayout();


        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new_dialog.dismiss();
            }
        });

        return new_dialog;
    }

    public void alertdialogforQuantity(String str_email) {

    }

    private void checkQuantityLayout() {
        if (i == 0) {
            ivCheckForZero.setVisibility(View.VISIBLE);
            mIvCheckForOne.setVisibility(View.INVISIBLE);
            mIvCheckForTwo.setVisibility(View.INVISIBLE);
            mIvCheckForThree.setVisibility(View.INVISIBLE);
            mIvCheckForFour.setVisibility(View.INVISIBLE);
            mIvCheckForFive.setVisibility(View.INVISIBLE);
            mIvCheckForSix.setVisibility(View.INVISIBLE);
        } else if (i == 1) {
            ivCheckForZero.setVisibility(View.INVISIBLE);
            mIvCheckForOne.setVisibility(View.VISIBLE);
            mIvCheckForTwo.setVisibility(View.INVISIBLE);
            mIvCheckForThree.setVisibility(View.INVISIBLE);
            mIvCheckForFour.setVisibility(View.INVISIBLE);
            mIvCheckForFive.setVisibility(View.INVISIBLE);
            mIvCheckForSix.setVisibility(View.INVISIBLE);
        } else if (i == 2) {
            ivCheckForZero.setVisibility(View.INVISIBLE);
            mIvCheckForOne.setVisibility(View.INVISIBLE);
            mIvCheckForTwo.setVisibility(View.VISIBLE);
            mIvCheckForThree.setVisibility(View.INVISIBLE);
            mIvCheckForFour.setVisibility(View.INVISIBLE);
            mIvCheckForFive.setVisibility(View.INVISIBLE);
            mIvCheckForSix.setVisibility(View.INVISIBLE);
        } else if (i == 3) {
            ivCheckForZero.setVisibility(View.INVISIBLE);
            mIvCheckForOne.setVisibility(View.INVISIBLE);
            mIvCheckForTwo.setVisibility(View.INVISIBLE);
            mIvCheckForThree.setVisibility(View.VISIBLE);
            mIvCheckForFour.setVisibility(View.INVISIBLE);
            mIvCheckForFive.setVisibility(View.INVISIBLE);
            mIvCheckForSix.setVisibility(View.INVISIBLE);
        } else if (i == 4) {
            ivCheckForZero.setVisibility(View.INVISIBLE);
            mIvCheckForOne.setVisibility(View.INVISIBLE);
            mIvCheckForTwo.setVisibility(View.INVISIBLE);
            mIvCheckForThree.setVisibility(View.INVISIBLE);
            mIvCheckForFour.setVisibility(View.VISIBLE);
            mIvCheckForFive.setVisibility(View.INVISIBLE);
            mIvCheckForSix.setVisibility(View.INVISIBLE);
        } else if (i == 5) {
            ivCheckForZero.setVisibility(View.INVISIBLE);
            mIvCheckForOne.setVisibility(View.INVISIBLE);
            mIvCheckForTwo.setVisibility(View.INVISIBLE);
            mIvCheckForThree.setVisibility(View.INVISIBLE);
            mIvCheckForFour.setVisibility(View.INVISIBLE);
            mIvCheckForFive.setVisibility(View.VISIBLE);
            mIvCheckForSix.setVisibility(View.INVISIBLE);
        } else if (i == 6) {
            ivCheckForZero.setVisibility(View.INVISIBLE);
            mIvCheckForOne.setVisibility(View.INVISIBLE);
            mIvCheckForTwo.setVisibility(View.INVISIBLE);
            mIvCheckForThree.setVisibility(View.INVISIBLE);
            mIvCheckForFour.setVisibility(View.INVISIBLE);
            mIvCheckForFive.setVisibility(View.INVISIBLE);
            mIvCheckForSix.setVisibility(View.VISIBLE);
        }
    }

    public void init() {
        rlQuantityZero = (RelativeLayout) new_dialog.findViewById(R.id.rlQuantityZero);
        mRlQuantityOne = (RelativeLayout) new_dialog.findViewById(R.id.rlQuantityOne);
        mRlQuantityTwo = (RelativeLayout) new_dialog.findViewById(R.id.rlQuantityTwo);
        mRlQuantityThree = (RelativeLayout) new_dialog.findViewById(R.id.rlQuantityThree);
        mRlQuantityFour = (RelativeLayout) new_dialog.findViewById(R.id.rlQuantityFour);
        mRlQuantityFive = (RelativeLayout) new_dialog.findViewById(R.id.rlQuantityFive);
        mRlQuantitySix = (RelativeLayout) new_dialog.findViewById(R.id.rlQuantitySix);

        ivCheckForZero = (ImageView) new_dialog.findViewById(R.id.ivCheckForZero);
        mIvCheckForOne = (ImageView) new_dialog.findViewById(R.id.ivCheckForOne);
        mIvCheckForTwo = (ImageView) new_dialog.findViewById(R.id.ivCheckForTwo);
        mIvCheckForThree = (ImageView) new_dialog.findViewById(R.id.ivCheckForThree);
        mIvCheckForFour = (ImageView) new_dialog.findViewById(R.id.ivCheckForFour);
        mIvCheckForFive = (ImageView) new_dialog.findViewById(R.id.ivCheckForFive);
        mIvCheckForSix = (ImageView) new_dialog.findViewById(R.id.ivCheckForSix);

        HeaderName = (TextView) new_dialog.findViewById(R.id.HeaderName);
        if (Quantity.equals("Filter")) {
            HeaderName.setText("Quantity Of Filter");
        } else if (Quantity.equals("Fuse")) {
            HeaderName.setText("Quantity Of Fuse");
        }
    }

    public void clickEvent() {

        rlQuantityZero.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                strQuantityCheck = "one";
                i = 0;
                Log.e("strQuantityCheck", String.valueOf(i));
                ivCheckForZero.setVisibility(View.VISIBLE);
                mIvCheckForOne.setVisibility(View.INVISIBLE);
                mIvCheckForTwo.setVisibility(View.INVISIBLE);
                mIvCheckForThree.setVisibility(View.INVISIBLE);
                mIvCheckForFour.setVisibility(View.INVISIBLE);
                mIvCheckForFive.setVisibility(View.INVISIBLE);
                mIvCheckForSix.setVisibility(View.INVISIBLE);
                dialogInterface.dialogClick("" + i);
                new_dialog.dismiss();
            }
        });

        mRlQuantityOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                strQuantityCheck = "one";
                i = 1;
                Log.e("strQuantityCheck", String.valueOf(i));
                ivCheckForZero.setVisibility(View.INVISIBLE);
                mIvCheckForOne.setVisibility(View.VISIBLE);
                mIvCheckForTwo.setVisibility(View.INVISIBLE);
                mIvCheckForThree.setVisibility(View.INVISIBLE);
                mIvCheckForFour.setVisibility(View.INVISIBLE);
                mIvCheckForFive.setVisibility(View.INVISIBLE);
                mIvCheckForSix.setVisibility(View.INVISIBLE);
                dialogInterface.dialogClick("" + i);
                new_dialog.dismiss();
            }
        });

        mRlQuantityTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                strQuantityCheck = "two";
                i = 2;
                Log.e("strQuantityCheck", String.valueOf(i));
                ivCheckForZero.setVisibility(View.INVISIBLE);
                mIvCheckForTwo.setVisibility(View.VISIBLE);

                mIvCheckForOne.setVisibility(View.INVISIBLE);
                mIvCheckForThree.setVisibility(View.INVISIBLE);
                mIvCheckForFour.setVisibility(View.INVISIBLE);
                mIvCheckForFive.setVisibility(View.INVISIBLE);
                mIvCheckForSix.setVisibility(View.INVISIBLE);
                dialogInterface.dialogClick("" + i);
                new_dialog.dismiss();
            }
        });

        mRlQuantityThree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                strQuantityCheck = "three";
                i = 3;
                Log.e("strQuantityCheck", String.valueOf(i));
                ivCheckForZero.setVisibility(View.INVISIBLE);
                mIvCheckForThree.setVisibility(View.VISIBLE);

                mIvCheckForOne.setVisibility(View.INVISIBLE);
                mIvCheckForTwo.setVisibility(View.INVISIBLE);
                mIvCheckForFour.setVisibility(View.INVISIBLE);
                mIvCheckForFive.setVisibility(View.INVISIBLE);
                mIvCheckForSix.setVisibility(View.INVISIBLE);
                dialogInterface.dialogClick("" + i);
                new_dialog.dismiss();
            }
        });

        mRlQuantityFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                strQuantityCheck = "four";
                i = 4;
                Log.e("strQuantityCheck", String.valueOf(i));
                ivCheckForZero.setVisibility(View.INVISIBLE);
                mIvCheckForFour.setVisibility(View.VISIBLE);

                mIvCheckForOne.setVisibility(View.INVISIBLE);
                mIvCheckForTwo.setVisibility(View.INVISIBLE);
                mIvCheckForThree.setVisibility(View.INVISIBLE);
                mIvCheckForFive.setVisibility(View.INVISIBLE);
                mIvCheckForSix.setVisibility(View.INVISIBLE);
                dialogInterface.dialogClick("" + i);
                new_dialog.dismiss();
            }
        });

        mRlQuantityFive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                strQuantityCheck = "five";
                i = 5;
                Log.e("strQuantityCheck", String.valueOf(i));
                ivCheckForZero.setVisibility(View.INVISIBLE);
                mIvCheckForFive.setVisibility(View.VISIBLE);

                mIvCheckForOne.setVisibility(View.INVISIBLE);
                mIvCheckForTwo.setVisibility(View.INVISIBLE);
                mIvCheckForThree.setVisibility(View.INVISIBLE);
                mIvCheckForFour.setVisibility(View.INVISIBLE);
                mIvCheckForSix.setVisibility(View.INVISIBLE);
                dialogInterface.dialogClick("" + i);
                new_dialog.dismiss();
            }
        });

        mRlQuantitySix.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                strQuantityCheck = "six";
                i = 6;
                Log.e("strQuantityCheck", String.valueOf(i));
                ivCheckForZero.setVisibility(View.INVISIBLE);
                mIvCheckForSix.setVisibility(View.VISIBLE);

                mIvCheckForOne.setVisibility(View.INVISIBLE);
                mIvCheckForTwo.setVisibility(View.INVISIBLE);
                mIvCheckForThree.setVisibility(View.INVISIBLE);
                mIvCheckForFour.setVisibility(View.INVISIBLE);
                mIvCheckForFive.setVisibility(View.INVISIBLE);
                dialogInterface.dialogClick("" + i);
                new_dialog.dismiss();
            }
        });

    }
}
