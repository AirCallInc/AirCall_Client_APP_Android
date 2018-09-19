package com.aircall.app.Dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Adapter.FilterListAdapter;
import com.aircall.app.AddUnitActivity;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Model.Parts.GetFilterForUnitResponce;
import com.aircall.app.Model.Parts.PartsFilter;
import com.aircall.app.R;

import java.util.ArrayList;

/**
 * Created by jd on 11/07/16.
 */
public class SelectFilterSizeDialogFragment extends DialogFragment {
    Dialog new_dialog;
    private GlobalClass globalClass;
    View mTransFilterQuantity;
    int i = 1;
    Activity activity;
    ImageView ivCancelDialog;
    TextView tvNoData;
    SharedPreferences sharedpreferences;
    UsernameDialogInteface usernameDialogInteface;
    RecyclerView rvSelectPart;
    EditText etSearchPart;
    SharedPreferences.Editor editor;
    FilterListAdapter filterListAdapter;
    GetFilterForUnitResponce getFilterForUnitResponce;
    ArrayList<PartsFilter> dataTemp;

    @SuppressLint("ValidFragment")
    public SelectFilterSizeDialogFragment(GlobalClass globalClass, GetFilterForUnitResponce getFilterForUnitResponce, UsernameDialogInteface usernameDialogInteface) {
        this.usernameDialogInteface = usernameDialogInteface;
        this.globalClass = globalClass;
        this.getFilterForUnitResponce = getFilterForUnitResponce;
        Log.e("call", "called");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_select_part, null);
        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new_dialog.setContentView(promptsView);

        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        init();
        clickEvent();

        return new_dialog;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((AddUnitActivity) activity).globalClass;
    }

    public void init() {
        ivCancelDialog = (ImageView) new_dialog.findViewById(R.id.ivCancelDialog);
        rvSelectPart = (RecyclerView) new_dialog.findViewById(R.id.rvSelectPart);
        etSearchPart = (EditText) new_dialog.findViewById(R.id.etSearchPart);
        tvNoData = (TextView) new_dialog.findViewById(R.id.tvNoData);

        if(getFilterForUnitResponce != null ) {
            dataTemp = new ArrayList<>(getFilterForUnitResponce.Data);
            filterListAdapter = new FilterListAdapter(activity, dataTemp);
            LinearLayoutManager llm = new LinearLayoutManager(activity);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            rvSelectPart.setLayoutManager(llm);
            rvSelectPart.setAdapter(filterListAdapter);
            Log.e("Size", "Adapter Size " + filterListAdapter.getItemCount());

            tvNoData.setVisibility(View.GONE);
            etSearchPart.setVisibility(View.VISIBLE);
            rvSelectPart.setVisibility(View.VISIBLE);
        } else {
            tvNoData.setVisibility(View.VISIBLE);
            etSearchPart.setVisibility(View.GONE);
            rvSelectPart.setVisibility(View.GONE);
        }

    }

    public void clickEvent() {

        ivCancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_dialog.dismiss();

            }
        });

        rvSelectPart.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        usernameDialogInteface.submitClick(dataTemp.get(position).Id,
                                dataTemp.get(position).PartName + " " + dataTemp.get(position).Size);
                        new_dialog.dismiss();
                    }
                })
        );

        etSearchPart.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                dataTemp.clear();
                HideKeyboard();
                if (!etSearchPart.getText().toString().equalsIgnoreCase("")) {
                    for (int i = 0; i < getFilterForUnitResponce.Data.size(); i++) {
                        if (getFilterForUnitResponce.Data.get(i).PartName.toLowerCase().contains(etSearchPart.getText().toString().toLowerCase())) {
                            dataTemp.add(getFilterForUnitResponce.Data.get(i));
                        }
                    }
                } else {
                    dataTemp = new ArrayList<>(getFilterForUnitResponce.Data);
                }
                filterListAdapter = null;
                filterListAdapter = new FilterListAdapter(activity, dataTemp);
                rvSelectPart.setAdapter(filterListAdapter);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });

    }

    public static class RecyclerItemClickListener implements RecyclerView.OnItemTouchListener {
        private OnItemClickListener mListener;

        public interface OnItemClickListener {
            public void onItemClick(View view, int position);
        }

        GestureDetector mGestureDetector;

        public RecyclerItemClickListener(Context context, OnItemClickListener listener) {
            mListener = listener;
            mGestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView view, MotionEvent e) {
            View childView = view.findChildViewUnder(e.getX(), e.getY());
            if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
                mListener.onItemClick(childView, view.getChildPosition(childView));
                return true;
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView view, MotionEvent motionEvent) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
        }
    }

    private void HideKeyboard() {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
