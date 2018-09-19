package com.aircall.app.Adapter;

import android.app.Activity;
import android.app.DialogFragment;
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
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Fragment.NewCardListFragment;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddNewCard.AddNewCardResponce;
import com.aircall.app.Model.AddNewCard.NewCardData;
import com.aircall.app.NewCardListActivity;
import com.aircall.app.R;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jd on 21/06/16.
 */

/**
 * CREDIT CARD LIST ADAPTER
 */

public class CreditCardListAdapter extends RecyclerView.Adapter<CreditCardListAdapter.CreditCardListViewHolder> {

    private Context self;
    ArrayList<NewCardData> creditCardDetail = new ArrayList<>();
    private CreditCardListViewHolder myHolder;
    private ViewGroup parent;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GlobalClass globalClass;
    FragmentManager fm;
    NewCardListFragment fragment;
    private ProgressDialogFragment progressDialogFragment;

    Activity activity;
//    DeleteAddressResponce deleteAddressResponceData;

    public CreditCardListAdapter(Activity context, NewCardListFragment fragment, ArrayList<NewCardData> creditCardDetail) {
        this.self = context;
        this.activity = context;
        this.creditCardDetail = creditCardDetail;
        this.fragment = fragment;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        globalClass = ((NewCardListActivity) self).globalClass;
    }

    @Override
    public CreditCardListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_for_add_new_card_listing, parent, false);
        this.parent = parent;

        sharedpreferences = self.getSharedPreferences("AircallData", Context.MODE_PRIVATE);
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

        myHolder.llRawClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * Change default card
                 */
                UpdateDefaultCreditCard(position, creditCardDetail.get(position).Id,creditCardDetail.get(position).CardType, creditCardDetail.get(position).NameOnCard,
                        creditCardDetail.get(position).CardNumber, creditCardDetail.get(position).ExpiryMonth, creditCardDetail.get(position).ExpiryYear,
                        creditCardDetail.get(position).IsDefaultPayment);
            }
        });


        if (creditCardDetail.get(position).IsDefaultPayment) {
            myHolder.ivIsDefaultCard.setVisibility(View.VISIBLE);
//            myHolder.llRawClick.setBackgroundResource(R.color.default_address_color);

        } else {
            myHolder.ivIsDefaultCard.setVisibility(View.INVISIBLE);
//            myHolder.llRawClick.setBackgroundResource(R.color.white);
        }

        myHolder.ivEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /**
                 * Launch new activity for Edit Card
                 */
                Intent intent = new Intent(activity, AddNewCardActivity.class);
                Gson gson = new Gson();
                String json = gson.toJson(creditCardDetail.get(position));
                intent.putExtra("creditCardDetail", json);
                activity.startActivityForResult(intent, 3);
            }
        });
    }

    @Override
    public int getItemCount() {
        return creditCardDetail.size();
    }

    public class CreditCardListViewHolder extends RecyclerView.ViewHolder {
        TextView txtCardHolderName;
        TextView txtCardNo;
        TextView txtCardExpireMonth;
        TextView txtCardExpireYear;
        ImageView ivIsDefaultCard, ivEdit;
        LinearLayout llRawClick;

        public CreditCardListViewHolder(View itemView) {
            super(itemView);
            txtCardHolderName = (TextView) itemView.findViewById(R.id.tvListCardHolderName);
            txtCardNo = (TextView) itemView.findViewById(R.id.tvListCardNo);
            txtCardExpireMonth = (TextView) itemView.findViewById(R.id.tvListExpireMonth);
            txtCardExpireYear = (TextView) itemView.findViewById(R.id.tvListExpireYear);
            ivIsDefaultCard = (ImageView) itemView.findViewById(R.id.ivIsDefaultCard);
            llRawClick = (LinearLayout) itemView.findViewById(R.id.llRawClick);
            ivEdit = (ImageView) itemView.findViewById(R.id.ivEdit);
        }
    }

    public void UpdateDefaultCreditCard(final int position, String CreditCardId, String cardType, String CardHolderName, String CardNo,
                                        String ExpiryMonth, String ExpiryYear, Boolean IsDefaultPayment) {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.updateCreditCardDetail(CreditCardId, sharedpreferences.getString("Id", ""),
                cardType, CardHolderName, ExpiryMonth,CardNo, "", ExpiryYear, true, new Callback<AddNewCardResponce>() {

            @Override
            public void success(AddNewCardResponce addNewCardResponce, Response response) {
                hideProgressDialog();

                if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addNewCardResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addNewCardResponce.Token);
                        editor.apply();
                    }

                    fragment.updateLIst(position, addNewCardResponce);

                } else if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.TockenExpired,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                    globalClass.Clientlogout();
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(fm, "");
                } else {
                    DialogFragment ds = new SingleButtonAlert(addNewCardResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(fm, "");

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
                ds.show(fm, "");
            }
        });
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
