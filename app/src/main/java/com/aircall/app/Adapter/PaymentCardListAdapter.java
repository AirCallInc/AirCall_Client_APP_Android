package com.aircall.app.Adapter;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.AddNewCardActivity;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Dialog.PaymentCardListDialog;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Interfaces.PaymentCardInterface;
import com.aircall.app.Model.AddNewCard.NewCardData;
import com.aircall.app.PaymentMethodActivity;
import com.aircall.app.R;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * Created by jd on 21/06/16.
 */

/**
 * Calls in Card list dialog,
 *
 * for listing the credit card with edit card button
 *
 * */

public class PaymentCardListAdapter extends RecyclerView.Adapter<PaymentCardListAdapter.CreditCardListViewHolder> implements PaymentCardInterface{

    private Activity self;
    ArrayList<NewCardData> creditCardDetail = new ArrayList<>();
    private CreditCardListViewHolder myHolder;
    private ViewGroup parent;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GlobalClass globalClass;
    FragmentManager fm;
    PaymentCardListDialog fragment;
    private ProgressDialogFragment progressDialogFragment;
    PaymentCardInterface paymentCardInterface;
    String cardHolderName, cardNumber, cardType, exMonth, exYear;
    boolean isSelected;

    public PaymentCardListAdapter(Activity context, GlobalClass globalClass, PaymentCardListDialog fragment,
                                  ArrayList<NewCardData> creditCardDetail, PaymentCardInterface paymentCardInterface)  {

        this.self = context;
        this.creditCardDetail = creditCardDetail;
        this.fragment = fragment;
        this.globalClass = globalClass;
        this.paymentCardInterface = paymentCardInterface;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        globalClass = ((PaymentMethodActivity) self).globalClass;
    }

    @Override
    public CreditCardListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_for_add_new_card_listing, parent, false);
        this.parent = parent;

        sharedpreferences = self.getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        return new CreditCardListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CreditCardListViewHolder holder, final int position) {

        final Context context = parent.getContext();
        fm = ((Activity) context).getFragmentManager();

        myHolder = holder;
        myHolder.txtCardHolderName.setText(creditCardDetail.get(position).NameOnCard);
        myHolder.txtCardNo.setText(creditCardDetail.get(position).CardNumber);
        myHolder.txtCardExpireMonth.setText(creditCardDetail.get(position).ExpiryMonth);
        myHolder.txtCardExpireYear.setText(creditCardDetail.get(position).ExpiryYear);
        //myHolder.llEdit.setVisibility(View.GONE);
        //myHolder.footerView.setVisibility(View.GONE);
        cardType = creditCardDetail.get(position).CardType;
        cardHolderName = creditCardDetail.get(position).NameOnCard;
        cardNumber = creditCardDetail.get(position).CardNumber;
        exMonth = creditCardDetail.get(position).ExpiryMonth;
        exYear = creditCardDetail.get(position).ExpiryYear;
        isSelected = creditCardDetail.get(position).IsSelected;

        if((creditCardDetail.get(position).IsSelected)) {
            myHolder.ivIsDefaultCard.setVisibility(View.VISIBLE);

        } else {
            myHolder.ivIsDefaultCard.setVisibility(View.INVISIBLE);
        }


        /**
         * Click event for select particular card
         * */
        myHolder.llRawClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.rawClickEvent(position);
            }
        });


        /**
         * Click event for edit card,
         *
         * it will redirect screen to AddNewCard Activity with card data for update
         *
         * */
        myHolder.llEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(self, AddNewCardActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(creditCardDetail.get(position));
                intent.putExtra("creditCardDetail", json);
                //self.startActivityForResult(intent, 3);
                self.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return creditCardDetail.size();
    }

    @Override
    public void cardDetail(int position, String cardHolderName, String cardNumber, String cardType, String exMonth, String exYear, boolean isSelect) {
//        strIsSelected = isSelect;
    }

    public class CreditCardListViewHolder extends RecyclerView.ViewHolder {

        TextView txtCardHolderName;
        TextView txtCardNo;
        TextView txtCardExpireMonth;
        TextView txtCardExpireYear;
        ImageView ivIsDefaultCard,ivEdit;
        LinearLayout llRawClick,llEdit;
        View footerView;

        public CreditCardListViewHolder(View itemView) {
            super(itemView);

            txtCardHolderName = (TextView) itemView.findViewById(R.id.tvListCardHolderName);
            txtCardNo = (TextView) itemView.findViewById(R.id.tvListCardNo);
            txtCardExpireMonth = (TextView) itemView.findViewById(R.id.tvListExpireMonth);
            txtCardExpireYear = (TextView) itemView.findViewById(R.id.tvListExpireYear);
            ivIsDefaultCard = (ImageView) itemView.findViewById(R.id.ivIsDefaultCard);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
            llRawClick = (LinearLayout) itemView.findViewById(R.id.llRawClick);
            llEdit = (LinearLayout) itemView.findViewById(R.id.llEdit);
            footerView = (View) itemView.findViewById(R.id.footerView);
        }
    }

    public void showProgressDailog(String message) {
        progressDialogFragment = new ProgressDialogFragment(message);
        progressDialogFragment.show(fm, "");
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
