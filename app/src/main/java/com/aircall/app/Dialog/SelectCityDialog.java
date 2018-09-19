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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.aircall.app.Adapter.SelectCityAdapter;
import com.aircall.app.AddAddressActivity;
import com.aircall.app.BillingAddressActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.UsernameDialogInteface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.GetCityList.CityDetail;
import com.aircall.app.Model.GetCityList.GetCityListResponce;
import com.aircall.app.R;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jd on 11/07/16.
 */
public class SelectCityDialog extends DialogFragment {
    Dialog new_dialog;
    private GlobalClass globalClass;
    View mTransFilterQuantity;
    int i = 1;
    Activity activity;
    TextView tvNoData;
    EditText etSearchCity;
    ImageView ivClose;
    SharedPreferences sharedpreferences;
    UsernameDialogInteface usernameDialogInteface;
    SharedPreferences.Editor editor;
    private ProgressDialogFragment progressDialogFragment;
    ListView listViewForCity;
    String strStateIdForCity, Id = "";
    GetCityListResponce getCityListResponceData;


    private SelectCityAdapter adapter;
    ArrayList<CityDetail> dataTemp = new ArrayList<>();


    /**
     * @param globalClass
     * @param strStateIdForCity      State Id
     * @param usernameDialogInteface
     * @param Id                     already selected city if (If selected)
     */
    @SuppressLint("ValidFragment")
    public SelectCityDialog(GlobalClass globalClass, String strStateIdForCity,
                            UsernameDialogInteface usernameDialogInteface, String Id) {
        this.usernameDialogInteface = usernameDialogInteface;
        this.globalClass = globalClass;
        this.strStateIdForCity = strStateIdForCity;
        if (Id != null) {
            this.Id = Id;
        }

        Log.e("call", "called");
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_for_select_city_from_state_id, null);
        new_dialog = new Dialog(getActivity());
        new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        new_dialog.setContentView(promptsView);

        init();
        clickEvent();


        return new_dialog;
    }

    public void init() {
        ivClose = (ImageView) new_dialog.findViewById(R.id.ivCancelDialog);
        tvNoData = (TextView) new_dialog.findViewById(R.id.tvNoData);
        etSearchCity = (EditText) new_dialog.findViewById(R.id.etSearchCity);
        listViewForCity = (ListView) new_dialog.findViewById(R.id.lvSelectCity);
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        if (globalClass.checkInternetConnection()) {
            getCityList();
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


    public void clickEvent() {
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HideKeyboard();
                usernameDialogInteface.submitClick("",
                        "");
                new_dialog.dismiss();
            }
        });

        listViewForCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) { // Item position is in the variable position.
                //HideKeyboard();
                usernameDialogInteface.submitClick(dataTemp.get(position).Name,
                        dataTemp.get(position).Id);
                new_dialog.dismiss();
            }
        });

        etSearchCity.addTextChangedListener(new TextWatcher() {

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                dataTemp.clear();
                if (!etSearchCity.getText().toString().equalsIgnoreCase("")) {
                    for (int i = 0; i < getCityListResponceData.Data.size(); i++) {
                        if (getCityListResponceData.Data.get(i).Name.toLowerCase().contains(etSearchCity.getText().toString().toLowerCase())) {
                            dataTemp.add(getCityListResponceData.Data.get(i));
                            if (Id.equalsIgnoreCase(getCityListResponceData.Data.get(i).Id)) {
                                dataTemp.get(dataTemp.size() - 1).isSelected = true;
                            } else {
                                dataTemp.get(dataTemp.size() - 1).isSelected = false;
                            }
                        }
                    }
                } else {
                    dataTemp = new ArrayList<>(getCityListResponceData.Data);
                    for (int i = 0; i < dataTemp.size(); i++) {
                        if (Id.equalsIgnoreCase(dataTemp.get(i).Id)) {
                            dataTemp.get(i).isSelected = true;
                        }
                    }
                }
                adapter = null;
                adapter = new SelectCityAdapter(activity, usernameDialogInteface, dataTemp);
                listViewForCity.setAdapter(adapter);
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        String curruntActivity = getActivity().getClass().getSimpleName();
        if (curruntActivity.contains("BillingAddressActivity")) {
            globalClass = ((BillingAddressActivity) activity).globalClass;
        } else if (curruntActivity.contains("AddAddressActivity")) {
            globalClass = ((AddAddressActivity) activity).globalClass;
        }

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


    public void hideProgressDialog() {
        try {
            if (progressDialogFragment != null) {
                progressDialogFragment.dismiss();
            }
        } catch (Exception e) {
        }
    }

    public void showProgressDailog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        progressDialogFragment.show(getFragmentManager(), "");
        progressDialogFragment.setCancelable(false);
    }


    /**
     * Call Web API for Get city list for particular State
     */
    public void getCityList() {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getCityByState(strStateIdForCity, new Callback<GetCityListResponce>() {
            @Override
            public void success(GetCityListResponce getCityListResponce, Response response) {
                hideProgressDialog();
                if (getCityListResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!getCityListResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", getCityListResponce.Token);
                        editor.apply();
                    }

                    if (getCityListResponce.Data.size() == 0) {
                        etSearchCity.setFocusable(false);
                    }

                    if (!Id.equalsIgnoreCase("")) {
                        for (int i = 0; i < getCityListResponce.Data.size(); i++) {
                            if (Id.equalsIgnoreCase(getCityListResponce.Data.get(i).Id)) {
                                getCityListResponce.Data.get(i).isSelected = true;
                            }
                        }
                    }

                    getCityListResponceData = getCityListResponce;
                    dataTemp = new ArrayList<>(getCityListResponce.Data);
                    tvNoData.setVisibility(View.GONE);
                    listViewForCity.setVisibility(View.VISIBLE);

                    adapter = new SelectCityAdapter(activity, usernameDialogInteface, getCityListResponce.Data);
                    listViewForCity.setAdapter(adapter);

                } else if (getCityListResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    tvNoData.setVisibility(View.VISIBLE);
                    listViewForCity.setVisibility(View.GONE);
                    etSearchCity.setFocusable(false);
                } else if (getCityListResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    etSearchCity.setFocusable(false);
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                } else {
                    etSearchCity.setFocusable(false);
                    tvNoData.setVisibility(View.VISIBLE);
                    listViewForCity.setVisibility(View.GONE);
                    hideProgressDialog();

                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                }
            }

            @Override
            public void failure(RetrofitError error) {

                hideProgressDialog();

                DialogFragment ds = new SingleButtonAlert(ErrorMessages.ServerError,
                        new DialogInterfaceClick() {
                            @Override
                            public void dialogClick(String tag) {

                            }
                        });
                ds.setCancelable(true);
                ds.show(getFragmentManager(), "");

                Log.e("Get State failor", "failor " + error.getMessage());
            }
        });
    }

    private void HideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

}
