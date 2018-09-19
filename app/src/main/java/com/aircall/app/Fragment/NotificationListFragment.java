package com.aircall.app.Fragment;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.Adapter.NotificationListAdapter;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.NotificationListing.NotificationListData;
import com.aircall.app.Model.NotificationListing.NotificationListResponce;
import com.aircall.app.R;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationListFragment extends Fragment {

    @Bind(R.id.rvNotifications)
    RecyclerView rvNotifications;

    @Bind(R.id.llLoadMore)
    LinearLayout llLoadMore;

    @Bind(R.id.tvNoData)
    TextView tvNoData;

    @Bind(R.id.ivBack)
    ImageView ivBack;

    @Bind(R.id.flMain)
    public FrameLayout flMain;

    GlobalClass globalClass;
    Activity activity;
    private LinearLayoutManager llm;
    private SharedPreferences sharedpreferences;
    private SharedPreferences.Editor editor;


    private NotificationListAdapter adapter;
    ArrayList<NotificationListData> NotificationData = new ArrayList<>();

    /**Load more*/
    private boolean loading = true;
    int pastVisiblesItems, visibleItemCount, totalItemCount;
    private String PageNumber = "1";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification_list,
                container, false);
        ButterKnife.bind(this, view);
        init();
        clickEvent();
        return view;
    }

    private void init() {
        sharedpreferences = getActivity().getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        llm = new LinearLayoutManager(activity);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        rvNotifications.setLayoutManager(llm);
        adapter = new NotificationListAdapter(activity, NotificationData);
        rvNotifications.setAdapter(adapter);

    }

    private void clickEvent() {
        flMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.onBackPressed();
            }
        });


        /**
         * Click event for notification listing, click event handle in DashboardActivity's notificationClick method.
         */
        rvNotifications.addOnItemTouchListener(
                new RecyclerItemClickListener(activity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        ((DashboardActivity)activity).notificationClick(NotificationData.get(position).NotificationType,
                                ""+NotificationData.get(position).NotificationId,""+NotificationData.get(position).CommonId);
                    }
                })
        );

        /**
         * For Load more data
         */
        rvNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = llm.getChildCount();
                    totalItemCount = llm.getItemCount();
                    pastVisiblesItems = llm.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + pastVisiblesItems) >= totalItemCount) {
                            loading = false;
                            notificationList(true);
                        }
                    }
                }
            }
        });
    }

    /**
     * web APi for notification listing
     * @param isLoadMore is call for load more data
     */
    public void notificationList(final Boolean isLoadMore) {

        if (!isLoadMore) {
            ((DashboardActivity) getActivity()).showProgressDailog("Please Wait...");
        } else {
            llLoadMore.setVisibility(View.VISIBLE);
        }

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.notificationList(sharedpreferences.getString("Id", ""), PageNumber, new Callback<NotificationListResponce>() {
            @Override
            public void success(NotificationListResponce notificationListResponce, Response response) {
                if (notificationListResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {

                    if (!notificationListResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", notificationListResponce.Token);
                        editor.apply();
                    }
                    NotificationData.addAll(notificationListResponce.Data);
                    adapter.notifyDataSetChanged();
                    loading = true;
                    PageNumber = ""+notificationListResponce.PageNumber;
                    tvNoData.setVisibility(View.GONE);
                    rvNotifications.setVisibility(View.VISIBLE);

                    if (!isLoadMore) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                    } else {
                        llLoadMore.setVisibility(View.GONE);
                    }

                } else if (notificationListResponce.StatusCode.equalsIgnoreCase(globalClass.strNotFound)) {
                    if (!isLoadMore) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                        tvNoData.setVisibility(View.VISIBLE);
                        tvNoData.setText(notificationListResponce.Message);
                        rvNotifications.setVisibility(View.GONE);
                    } else {
                        llLoadMore.setVisibility(View.GONE);
                        loading = false;
                        PageNumber = "1";
                    }

                    adapter.notifyDataSetChanged();
                } else if (notificationListResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
                    ((DashboardActivity) getActivity()).hideProgressDialog();
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
                    tvNoData.setText(notificationListResponce.Message);
                    rvNotifications.setVisibility(View.GONE);
                    if (!isLoadMore) {
                        ((DashboardActivity) getActivity()).hideProgressDialog();
                    } else {
                        llLoadMore.setVisibility(View.GONE);
                    }
                    if (adapter != null) {
                        NotificationData.clear();
                        adapter.notifyDataSetChanged();
                    }

                }
            }

            @Override
            public void failure(RetrofitError error) {
                ((DashboardActivity) getActivity()).hideProgressDialog();
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
    public void onStart() {
        super.onStart();
        if (globalClass.checkInternetConnection()) {
            notificationList(false);
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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        globalClass = ((DashboardActivity) activity).globalClass;

    }

    @Override
    public void onPause() {
        super.onPause();
        PageNumber = "1";
        NotificationData.clear();
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
}
