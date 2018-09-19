package com.aircall.app.Adapter;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aircall.app.AddAddressActivity;
import com.aircall.app.AddressListActivity;
import com.aircall.app.Common.ErrorMessages;
import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Common.ServiceGenerator;
import com.aircall.app.Dialog.DoubleButtonAlert;
import com.aircall.app.Dialog.ProgressDialogFragment;
import com.aircall.app.Dialog.SingleButtonAlert;
import com.aircall.app.Fragment.AddressListFragment;
import com.aircall.app.Interfaces.DialogInterfaceClick;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.Model.DeleteAddress.DeleteAddressResponce;
import com.aircall.app.Model.GetAddress.AddressDetailForList;
import com.aircall.app.Model.GetAddress.getAddressResponce;
import com.aircall.app.R;

import java.util.ArrayList;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Jd on 21/06/16.
 */

/**
 * ADAPTER FOR ADDRESS LISTING PAGE
 */

public class AddressListAdapter extends RecyclerView.Adapter<AddressListAdapter.AddressListViewHolder> {

    private Context self;
    ArrayList<AddressDetailForList> addressDetail = new ArrayList<>();
    private AddressListViewHolder myHolder;
    private ViewGroup parent;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GlobalClass globalClass;
    String clientId;
    Activity activity;
    DeleteAddressResponce deleteAddressResponceData;
    FragmentManager fm;
    getAddressResponce GetAddressResponceData;
    private ProgressDialogFragment progressDialogFragment;
    LinearLayout llAddress;
    AddressListFragment fragment;


    public AddressListAdapter(Context context, AddressListFragment fragment, ArrayList<AddressDetailForList> addressDetail) {
        Log.i("Adapterlist", "Init");
        this.self = context;
        this.addressDetail = addressDetail;
        this.fragment = fragment;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);

        globalClass = ((AddressListActivity) self).globalClass;
    }


    @Override
    public AddressListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.raw_for_address_listing, parent, false);
        this.parent = parent;

        sharedpreferences = self.getSharedPreferences("AircallData", Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();


        return new AddressListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(AddressListViewHolder holder, final int position) {

        final Context context = parent.getContext();
        fm = ((Activity) context).getFragmentManager();

        myHolder = holder;
        myHolder.txtAddress.setText(addressDetail.get(position).Address);
        myHolder.txtCity.setText(addressDetail.get(position).CityName + ", ");
        myHolder.txtState.setText(addressDetail.get(position).StateName + " ");
        myHolder.txtZipcode.setText(addressDetail.get(position).ZipCode);

        /**
         * Check if address is allow to delete or not, if not then hide delete button
         */
        if (!addressDetail.get(position).AllowDelete) {
            myHolder.ivForDeletAddress.setVisibility(View.GONE);
        } else {
            myHolder.ivForDeletAddress.setVisibility(View.VISIBLE);
        }

        /**
         * Click event for change default address
         */
        myHolder.lladdressClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateDefaultAddress(position, addressDetail.get(position).Id, addressDetail.get(position).Address, addressDetail.get(position).City,
                        addressDetail.get(position).State, addressDetail.get(position).ZipCode, addressDetail.get(position).IsDefaultAddress);
            }
        });

        myHolder.ivForDeletAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (addressDetail.get(position).IsDefaultAddress && addressDetail.size() > 1) {
                    DialogFragment ds = new SingleButtonAlert(ErrorMessages.DeletDefaultAddressValidation,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                }
                            });
                    ds.setCancelable(true);
                    ds.show(fm, "");
                } else {
                    DialogFragment ds = new DoubleButtonAlert(ErrorMessages.DeletedAddress,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {

                                    if (tag.equalsIgnoreCase("yes")) {
                                        deleteAddress(position);
                                    } else {

                                    }
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(fm, "");
                }
            }
        });

        /**
         * Click event of edit address,
         * we will use startActivityForResult for start AddAddressActivity, so that once it will come back we can get
         * notify from onActivityResult and reload new data in listing.
         * 9898153703
         */

        myHolder.ivForEditAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(self, AddAddressActivity.class);
                i.putExtra("EditAddress", "Edit Address");
                i.putExtra("id", addressDetail.get(position).Id);
                i.putExtra("clientId", addressDetail.get(position).ClientId);
                i.putExtra("address", addressDetail.get(position).Address);
                i.putExtra("cityName", addressDetail.get(position).CityName);
                i.putExtra("city", addressDetail.get(position).City);
                i.putExtra("state", addressDetail.get(position).State);
                i.putExtra("stateName", addressDetail.get(position).StateName);
                i.putExtra("zipcode", addressDetail.get(position).ZipCode);
                i.putExtra("AddressCount", addressDetail.size());
                i.putExtra("AllowDelete", addressDetail.get(position).AllowDelete);
                i.putExtra("isDefaultAddress", addressDetail.get(position).IsDefaultAddress);
                ((AddressListActivity) self).startActivityForResult(i, 1);
//                ((AddressListActivity) self).onBackPressed();
            }
        });

        if (addressDetail.get(position).IsDefaultAddress) {
            myHolder.ivIsDefaultAddress.setVisibility(View.VISIBLE);
            myHolder.llAddress.setBackgroundResource(R.color.default_address_color);
        } else {
            myHolder.ivIsDefaultAddress.setVisibility(View.INVISIBLE);
            myHolder.llAddress.setBackgroundResource(R.color.white);
        }
    }

    @Override
    public int getItemCount() {
        return addressDetail.size();
    }

    public class AddressListViewHolder extends RecyclerView.ViewHolder {
        TextView txtAddress;
        TextView txtState;
        TextView txtCity;
        TextView txtZipcode;
        ImageView ivIsDefaultAddress;
        ImageView ivForEditAddress;
        ImageView ivForDeletAddress;
        LinearLayout llAddress;
        LinearLayout lladdressClick;

        public AddressListViewHolder(View itemView) {
            super(itemView);
            txtAddress = (TextView) itemView.findViewById(R.id.tvListOfAddress);
            txtState = (TextView) itemView.findViewById(R.id.tvListOfAddressState);
            txtCity = (TextView) itemView.findViewById(R.id.tvListOfAddressCity);
            txtZipcode = (TextView) itemView.findViewById(R.id.tvListOfAddressZipcode);
            ivIsDefaultAddress = (ImageView) itemView.findViewById(R.id.ivIsDefaultAddress);
            ivForEditAddress = (ImageView) itemView.findViewById(R.id.ivForEditAddress);
            ivForDeletAddress = (ImageView) itemView.findViewById(R.id.ivForDeletAddress);
            llAddress = (LinearLayout) itemView.findViewById(R.id.llAddress);
            lladdressClick = (LinearLayout) itemView.findViewById(R.id.lladdressClick);
        }
    }

    /**
     * Web API call for delete address
     * @param position Address item position in the list
     */
    public void deleteAddress(final int position) {

        ((AddressListActivity) self).showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.deleteClientAddress(addressDetail.get(position).Id, sharedpreferences.getString("Id", ""), new Callback<DeleteAddressResponce>() {

            @Override
            public void success(DeleteAddressResponce deleteAddressResponce, Response response) {

                ((AddressListActivity) self).hideProgressDialog();
                if (deleteAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                    if (!deleteAddressResponce.Token.equalsIgnoreCase("")) {
                        editor.putString("Token", deleteAddressResponce.Token);
                        editor.apply();
                    }

                    deleteAddressResponceData = deleteAddressResponce;

                    addressDetail.remove(position);
                    notifyDataSetChanged();
                    DialogFragment ds = new SingleButtonAlert(deleteAddressResponce.Message,
                            new DialogInterfaceClick() {
                                @Override
                                public void dialogClick(String tag) {
                                }
                            });
                    ds.setCancelable(true);
                    ds.show(((Activity) self).getFragmentManager(), "");

                } else if (deleteAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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

                    DialogFragment ds = new SingleButtonAlert(deleteAddressResponce.Message,
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
                ((AddressListActivity) self).hideProgressDialog();
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

    /**
     * Web API call for change default address (We call update address API)
     * @param position
     * @param AddressId
     * @param Address
     * @param City
     * @param State
     * @param zipcode
     * @param IsDefaultAddress
     */
    public void UpdateDefaultAddress(final int position, String AddressId, String Address, String City,
                                     String State, String zipcode, Boolean IsDefaultAddress) {

        showProgressDailog("Please Wait...");
        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class, sharedpreferences.getString("Token", ""));

        webserviceApi.updateClientAddress(AddressId.toString(), sharedpreferences.getString("Id", ""), Address, Integer.parseInt(State),
                Integer.parseInt(City), Integer.parseInt(zipcode), true, new Callback<getAddressResponce>() {

                    @Override
                    public void success(getAddressResponce GetAddressResponce, Response response) {
                        hideProgressDialog();

                        if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strSucessCode)) {
                            if (!GetAddressResponce.Token.equalsIgnoreCase("")) {
                                editor.putString("Token", GetAddressResponce.Token);
                                editor.apply();
                            }

                            fragment.updateLIst(position, GetAddressResponce);

                        } else if (GetAddressResponce.StatusCode.equalsIgnoreCase(globalClass.strUnauthorized)) {
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
                            DialogFragment ds = new SingleButtonAlert(GetAddressResponce.Message,
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
