package com.aircall.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.aircall.app.R;

@SuppressLint("ValidFragment")
public class ProgressDialogFragment extends DialogFragment {
	private String message;
	//private TextView tvMessage;

	public ProgressDialogFragment() {
		this.message = "";
	}
	private Dialog CameraDialog;
	public ProgressDialogFragment(String message) {
		this.message = message;
	}
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		CameraDialog = new Dialog(getActivity());
		CameraDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		CameraDialog.getWindow().setBackgroundDrawableResource(R.color.transperent);
		CameraDialog.setContentView(R.layout.progressdialog_fragment);
		CameraDialog.setCancelable(true);

		/*tvMessage = (TextView)CameraDialog.findViewById(tvMessage);
		if(!message.equalsIgnoreCase("")) {
			tvMessage.setText(message);
		}*/
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.copyFrom(CameraDialog.getWindow().getAttributes());
		DisplayMetrics displaymetrics = new DisplayMetrics();
		lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		lp.gravity = Gravity.CENTER;
		getActivity().getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);
		CameraDialog.getWindow().setAttributes(lp);

		return CameraDialog;
	}
}
