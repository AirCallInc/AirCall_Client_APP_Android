package com.aircall.app.Dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Adapter.PaymentCardListAdapter;
import com.aircall.app.AddNewCardActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.PaymentCardInterface;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddNewCard.AddNewCardResponce;
import com.aircall.app.Model.AddNewCard.NewCardData;
import com.aircall.app.PaymentMethodActivity;
import com.aircall.app.R;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by kartik on 21/06/16.
 */
public class PaymentCardListDialog extends DialogFragment {

    Dialog new_dialog;
    GlobalClass globalClass;
    PaymentCardInterface paymentCardInterface;
    TextView tvNoData;

    RecyclerView rvSelectPaymentCard;
    ImageView ivCloseDialog,ivAddNewCard;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Activity activity;
    private ProgressDialogFragment progressDialogFragment;

    /*Address Listing*/
    private ArrayList<NewCardData> creditCardDetail = new ArrayList<>();
    PaymentCardListAdapter paymentCardListAdapter;
    AddNewCardResponce addNewCardResponceData;

    public PaymentCardListDialog(GlobalClass globalClass, AddNewCardResponce addNewCardResponceData, PaymentCardInterface paymentCardInterface) {
        this.globalClass = globalClass;
        this.paymentCardInterface = paymentCardInterface;
        //this.addNewCardResponceData = addNewCardResponceData;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.dialog_for_paymentcard_list, null);
        //new_dialog = new Dialog(getActivity());
        new_dialog = new Dialog(getActivity(), android.R.style.Theme_Translucent_NoTitleBar);
        //new_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        new_dialog.setContentView(promptsView);

        init();
        clickEvent();
        return new_dialog;
    }

    public void init() {
        rvSelectPaymentCard = (RecyclerView) new_dialog.findViewById(R.id.rvSelectPaymentCard);
        ivCloseDialog = (ImageView) new_dialog.findViewById(R.id.ivCloseDialog);
        ivAddNewCard = (ImageView) new_dialog.findViewById(R.id.ivAddNewCard);
        tvNoData = (TextView) new_dialog.findViewById(R.id.tvNoData);
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        LinearLayoutManager llm1 = new LinearLayoutManager(activity);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rvSelectPaymentCard.setLayoutManager(llm1);

    }

    public void clickEvent() {

        ivCloseDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new_dialog.dismiss();
            }
        });

        ivAddNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddNewCardActivity.class);
                startActivity(intent);
            }
        });
    }

    public void rawClickEvent(int position) {
        creditCardDetail.get(position).IsSelected = true;
        paymentCardInterface.cardDetail(position, creditCardDetail.get(position).NameOnCard, creditCardDetail.get(position).CardNumber,
                creditCardDetail.get(position).CardType, creditCardDetail.get(position).ExpiryMonth,
                creditCardDetail.get(position).ExpiryYear, true);

        new_dialog.dismiss();
    }

    public void getCardList() {

        Log.e("addNewCardList", "addNewCardList");

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getCreditCardList(sharedpreferences.getString("Id", ""), new Callback<AddNewCardResponce>() {

            @Override
            public void success(AddNewCardResponce addNewCardResponce, retrofit.client.Response response) {

                hideProgressDialog();

                if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addNewCardResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addNewCardResponce.Token);
                        editor.apply();
                    }

                    creditCardDetail.clear();

                    for (int i = 0; i < addNewCardResponce.Data.size(); i++) {
                        NewCardData newCardData = new NewCardData();
                        newCardData.Id = addNewCardResponce.Data.get(i).Id;
                        newCardData.NameOnCard = addNewCardResponce.Data.get(i).NameOnCard;
                        newCardData.CardNumber = addNewCardResponce.Data.get(i).CardNumber;
                        newCardData.CardType = addNewCardResponce.Data.get(i).CardType;
                        newCardData.IsDefaultPayment = addNewCardResponce.Data.get(i).IsDefaultPayment;
                        newCardData.ExpiryMonth = addNewCardResponce.Data.get(i).ExpiryMonth;
                        newCardData.ExpiryYear = addNewCardResponce.Data.get(i).ExpiryYear;

                        creditCardDetail.add(newCardData);
                    }
                    addNewCardResponceData = addNewCardResponce;

                    creditCardDetail = addNewCardResponceData.Data;
                    if (creditCardDetail == null || creditCardDetail.size() == 0) {
                        tvNoData.setVisibility(View.VISIBLE);
                        rvSelectPaymentCard.setVisibility(View.GONE);
                    } else {
                        tvNoData.setVisibility(View.GONE);
                        rvSelectPaymentCard.setVisibility(View.VISIBLE);
                        paymentCardListAdapter = new PaymentCardListAdapter(activity, globalClass, PaymentCardListDialog.this,
                                creditCardDetail, paymentCardInterface);
                        rvSelectPaymentCard.setAdapter(paymentCardListAdapter);
                    }

                } else if(addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    tvNoData.setVisibility(View.VISIBLE);
                    rvSelectPaymentCard.setVisibility(View.GONE);
                } else if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(getFragmentManager(), "");
                }
            }

            @Override
            public void failure(RetrofitError error) {

                Log.e("addNewCardList failure", "addNewCardList failure" + error.getMessage());

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
        });
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((PaymentMethodActivity) activity).globalClass;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        getCardList();
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getCardList();
    }*/

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
