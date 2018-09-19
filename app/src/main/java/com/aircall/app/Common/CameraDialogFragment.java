package com.aircall.app.Common;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.R;

@SuppressLint({"ValidFragment", "NewApi"})
public class CameraDialogFragment extends DialogFragment {
    DialogInterfaceClick dialogClick;
    private Dialog CameraDialog;
    private Button btnCamera, btnAlbum, btnCameraCancel;

    public CameraDialogFragment(DialogInterfaceClick dialogClick) {
        this.dialogClick = dialogClick;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        CameraDialog = new Dialog(getActivity());
        CameraDialog.setContentView(R.layout.dialog_camera);
        CameraDialog.setCancelable(true);
        CameraDialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
//        int divierId = CameraDialog.getContext().getResources()
//                .getIdentifier("android:id/titleDivider", null, null);


        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(CameraDialog.getWindow().getAttributes());
        DisplayMetrics displaymetrics = new DisplayMetrics();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.gravity = Gravity.BOTTOM;
        getActivity().getWindowManager().getDefaultDisplay()
                .getMetrics(displaymetrics);
        CameraDialog.getWindow().setAttributes(lp);
        btnCamera = (Button) CameraDialog.findViewById(R.id.btnCamera);
        btnAlbum = (Button) CameraDialog.findViewById(R.id.btnAlbum);
        btnCameraCancel = (Button) CameraDialog
                .findViewById(R.id.btnCameraCancel);


//		btnAlbum.setText(Comman_data.ANYTextFromLibrary);
//		btnCamera.setText(Comman_data.ANYTextFromCamera);
//		btnCameraCancel.setText(Comman_data.ANYTextCancel);
        btnAlbum.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogClick.dialogClick("Album");
                CameraDialog.dismiss();
            }
        });
        btnCamera.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialogClick.dialogClick("Camera");
                CameraDialog.dismiss();
            }
        });
        btnCameraCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                CameraDialog.dismiss();
            }
        });
        return CameraDialog;
        // Use the current date as the default date in the picker
    }
}