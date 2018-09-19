package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Adapter.PastServicesAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.PastServices.PastServiceListingData;
import com.aircall.app.Model.PastServices.PastServiceListingResponce;
import com.aircall.app.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PastServiceFragment extends Fragment {

    @Bind(R.id.ivNavDrawer)
    ImageView ivNavDrawer;

    @Bind(R.id.rvForListOfRequest)
    RecyclerView rvForListOfRequest;

    @Bind(R.id.flMain)
    public FrameLayout flMain;

    @Bind(R.id.llLoadMore)
    LinearLayout llLoadMore;

    @Bind(R.id.tvNoData)
    TextView tvNoData;

    GlobalClass globalClass;
    Activity activity;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;
    private PastServicesAdapter servicesAdapter;
    private LinearLayoutManager llm1;
    private ArrayList<PastServiceListingData> aastServiceList = new ArrayList<>();

    /**
     * Variables for Load More
     */
    private boolean loading = true;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String PageNumber = "1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_past_service,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvents();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        llm1 = new LinearLayoutManager(activity);
        llm1.setOrientation(LinearLayoutManager.VERTICAL);
        rvForListOfRequest.setLayoutManager(llm1);
        servicesAdapter = new PastServicesAdapter(activity, aastServiceList);
        rvForListOfRequest.setAdapter(servicesAdapter);

        if (globalClass.checkInternetConnection()) {

            pastServiceListing(false);

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

    private void clickEvents() {
        flMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ivNavDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((DashboardActivity) getActivity()).openDrawer();
            }
        });

        rvForListOfRequest.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        FragmentTransaction transaction = getFragmentManager()
                                .beginTransaction();
                        Fragment newFragment = new PastServiceDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("Id", aastServiceList.get(position).Id);
                        newFragment.setArguments(bundle);
                        String strFragmentTag = newFragment.toString();
                        transaction.add(R.id.frame_middel, newFragment,
                                strFragmentTag);
                        transaction.addToBackStack("");
                        transaction.commit();
                    }
                })
        );

        /**
         * Check for load more data
         */
        rvForListOfRequest.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm1.getChildCount();
                    totalItemCount = llm1.getItemCount();
                    pastVisiblesItems = llm1.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            Log.e("...", "Last Item Wow !");
                            pastServiceListing(true);
                        }
                    }
                }
            }
        });
    }

    /**
     * Web API for Get data of past services
     * @param isLoadMore Call API for load more or first time.
     */
    public void pastServiceListing(final Boolean isLoadMore) {

        if (!isLoadMore) {
            ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        } else {
            llLoadMore.setVisibility(View.VISIBLE);
        }
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.pastServiceListing(sharedpreferences.getString("Id", ""), PageNumber, new Callback<PastServiceListingResponce>() {

            @Override
            public void success(PastServiceListingResponce pastServiceListingResponce, Response response) {
                if (!isLoadMore) {
                    ((DashboardActivity) getActivity()).hideProgressDialog();
                } else {
                    llLoadMore.setVisibility(View.GONE);
                }

                if (pastServiceListingResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!pastServiceListingResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", pastServiceListingResponce.Token);
                        editor.apply();
                    }
                    aastServiceList.addAll(pastServiceListingResponce.Data);
                    servicesAdapter.notifyDataSetChanged();
                    loading = true;
                    PageNumber = pastServiceListingResponce.PageNumber;
                    tvNoData.setVisibility(View.GONE);

                } else if (pastServiceListingResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    if (!isLoadMore) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                        tvNoData.setVisibility(View.VISIBLE);
                        tvNoData.setText(pastServiceListingResponce.Message);
                        rvForListOfRequest.setVisibility(View.GONE);
                    } else {
                        llLoadMore.setVisibility(View.GONE);
                    }
                    loading = false;
                    PageNumber = pastServiceListingResponce.PageNumber;
                } else if (pastServiceListingResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                    tvNoData.setVisibility(View.VISIBLE);
                    tvNoData.setText(pastServiceListingResponce.Message);
                    rvForListOfRequest.setVisibility(View.GONE);
                    if (!isLoadMore) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                    } else {
                        llLoadMore.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                tvNoData.setVisibility(View.VISIBLE);
                tvNoData.setText(ErrorMessages.ServerError);
                rvForListOfRequest.setVisibility(View.GONE);
                if (!isLoadMore) {
                    ((DashboardActivity) getActivity()).hideProgressDialog();
                } else {
                    llLoadMore.setVisibility(View.GONE);
                }
                if (servicesAdapter != null) {
                    aastServiceList.clear();
                    servicesAdapter.notifyDataSetChanged();
                }
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) activity).last = "past_service";
    }
}