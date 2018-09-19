package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
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
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.aircall.app.Adapter.CreditCardListAdapter;
import com.aircall.app.AddNewCardActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.AddNewCard.AddNewCardResponce;
import com.aircall.app.Model.AddNewCard.NewCardData;
import com.aircall.app.NewCardListActivity;
import com.aircall.app.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;

public class NewCardListFragment extends Fragment {

    @Bind(R.id.ivAddNewCard)
    ImageView ivAddNewCard;

    @Bind(R.id.rvForNewCardListing)
    RecyclerView rvForNewCardListing;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.flMain)
    FrameLayout flMain;

    @Bind(R.id.txtCardNoData)
    TextView txtCardNoData;

    private ProgressDialogFragment progressDialogFragment;
    GlobalClass globalClass;
    Activity activity;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    Boolean IssetDefaultCreditCard;

    /**
     * Card Listing
     */
    private ArrayList<NewCardData> creditCardDetail = new ArrayList<>();
    CreditCardListAdapter creditCardListAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_new_card_list, container, false);
        ButterKnife.bind(this, view);

        init();
        clickEvents();

        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();

        creditCardListAdapter = new CreditCardListAdapter(activity,NewCardListFragment.this, creditCardDetail);
        LinearLayoutManager llm1 = new LinearLayoutManager(activity);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rvForNewCardListing.setLayoutManager(llm1);
        rvForNewCardListing.setAdapter(creditCardListAdapter);

        if (globalClass.checkInternetConnection()) {
            addNewCardList();
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

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });
    }

    private void clickEvents() {
        /**
         * Start new Activity AddNewCardActivity for add new card.
         */
        ivAddNewCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, AddNewCardActivity.class);
                intent.putExtra("IsDefaultCard",IssetDefaultCreditCard);
                startActivityForResult(intent, 3);
            }
        });

        flMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((NewCardListActivity) activity).globalClass;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        addNewCardList();
    }

    /**
     * Web API for Get Card listing
     */
    public void addNewCardList() {

        Log.e("addNewCardList","addNewCardList");

        ((NewCardListActivity) getActivity()).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.getCreditCardList(sharedpreferences.getString("Id", ""), new Callback<AddNewCardResponce>() {

            @Override
            public void success(AddNewCardResponce addNewCardResponce, retrofit.client.Response response) {
                ((NewCardListActivity) getActivity()).hideProgressDialog();

                if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!addNewCardResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", addNewCardResponce.Token);
                        editor.apply();
                  }
                    Log.e("success addNewCardList","success addNewCardList");

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
//                      newCardData.AllowDelete = addNewCardResponce.Data.get(i).AllowDelete;
                        IssetDefaultCreditCard = addNewCardResponce.Data.get(i).IsDefaultPayment;

                        creditCardDetail.add(newCardData);
                    }
                    rvForNewCardListing.setVisibility(View.VISIBLE);
                    txtCardNoData.setVisibility(View.GONE);
                    creditCardListAdapter = new CreditCardListAdapter(activity,NewCardListFragment.this, creditCardDetail);
                    rvForNewCardListing.setAdapter(creditCardListAdapter);
                    creditCardListAdapter.notifyDataSetChanged();

                } else if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    rvForNewCardListing.setVisibility(View.GONE);
                    txtCardNoData.setVisibility(View.VISIBLE);
                    txtCardNoData.setText(addNewCardResponce.Message);

                } else if (addNewCardResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    rvForNewCardListing.setVisibility(View.GONE);
                    txtCardNoData.setVisibility(View.VISIBLE);
                    txtCardNoData.setText(ErrorMessages.ErrorGettingData);

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
                    rvForNewCardListing.setVisibility(View.GONE);
                    txtCardNoData.setVisibility(View.VISIBLE);
                    txtCardNoData.setText(ErrorMessages.ErrorGettingData);

                    DialogFragment ds = new SingleButtonAlert(addNewCardResponce.Message,
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
                rvForNewCardListing.setVisibility(View.GONE);
                txtCardNoData.setVisibility(View.VISIBLE);
                txtCardNoData.setText(ErrorMessages.ErrorGettingData);
                ((NewCardListActivity) getActivity()).hideProgressDialog();
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

    public void updateLIst(int position, AddNewCardResponce addNewCardResponce) {

        creditCardDetail.clear();
        for (int i = 0; i < addNewCardResponce.Data.size(); i++) {
            NewCardData newCardData = new NewCardData();
            newCardData.Id = addNewCardResponce.Data.get(i).Id;
            newCardData.NameOnCard = addNewCardResponce.Data.get(i).NameOnCard;
            newCardData.CardNumber = addNewCardResponce.Data.get(i).CardNumber;
            newCardData.ExpiryMonth = addNewCardResponce.Data.get(i).ExpiryMonth;
            newCardData.ExpiryYear = addNewCardResponce.Data.get(i).ExpiryYear;
            newCardData.IsDefaultPayment = addNewCardResponce.Data.get(i).IsDefaultPayment;

            creditCardDetail.add(newCardData);
        }
        creditCardListAdapter.notifyDataSetChanged();
    }
}
