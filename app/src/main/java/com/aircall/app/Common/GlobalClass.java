package com.aircall.app.Common;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.NotificationCompat;

import com.aircall.app.AddNewCardActivity;
import com.aircall.app.AddUnitActivity;
import com.aircall.app.DashboardActivity;
import com.aircall.app.Interfaces.WebserviceApi;
import com.aircall.app.LoginActivity;
import com.aircall.app.Model.PastServices.RattingResponce;
import com.aircall.app.NoShowActivity;
import com.aircall.app.R;
import com.aircall.app.UserProfileActivity;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by jd on 30/05/16.
 */
public class GlobalClass extends Application {
    public String PreferenceName = "AircallData";
    public String strSucessCode = "200";
    public String strUnauthorized = "401";
    public String strBadRequest = "400";
    public String strNotFound = "404";
    public String strInternalServerError = "500";
    public String strPaymentFail = "502";
    public String token = "";
    public String ExternalStorageDirectoryPath = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/.Aircall/";

    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    public String NTYPE_SCHEDULE = "1";
    public String NTYPE_FRIENDLY_REMINDER = "2";
    public String NTYPE_NO_SHOW = "3";
    public String NTYPE_PART_PURCHASE = "4";
    public String NTYPE_RATE_SERVICE = "5";
    public String NTYPE_CARD_EXPIRE = "6";
    public String NTYPE_RENEW_UNIT = "8";
    public String NTYPE_RESCHEDULE = "10";
    public String NTYPE_PAYMENT_FAILED = "11";
    public String NTYPE_SERVICE_REMINDER = "16";
    public String NTYPE_INVOICE_PAYMENT_FAIL = "17";
    public String NTYPE_SUBSCRIPTION_INVOICE_PAYMENT_DUE_REMINDER = "18";
    public String NTYPE_PAST_DUE_REMINDER = "19";
    //Added
    public String NTYPE_LATE_CANCELLED = "21";
    //public Intent testIntent;
    public String MOBILE_NUMBER = "";

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sharedpreferences = getSharedPreferences(PreferenceName, Context.MODE_PRIVATE);
        editor = sharedpreferences.edit();
        //testIntent = new Intent(GlobalClass.this,TestService.class);


        File file = new File(ExternalStorageDirectoryPath);
        if (!file.exists()) {
            file.mkdirs();
        }

    }

    public boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected()) {
            return true;
        }
        NetworkInfo mobileNetwork = cm
                .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (mobileNetwork != null && mobileNetwork.isConnected()) {
            return true;
        }
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null && activeNetwork.isConnected()) {
            return true;
        }
        return false;
    }

    public static final boolean isValidEmail(CharSequence target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }


    /**
     * PASSWORD ENCRYPTION
     */
    public String getMD5EncryptedString(String encTarget) {
        MessageDigest mdEnc = null;
        try {
            mdEnc = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        // Encryption algorithm
        mdEnc.update(encTarget.getBytes(), 0, encTarget.length());
        String md5 = new BigInteger(1, mdEnc.digest()).toString(16);
        while (md5.length() < 32) {
            md5 = "0" + md5;
        }
        return md5;
    }


    /** NOTIFICATION SECTION */

    /**
     * Redirect to @{@link DashboardActivity} -> @{@link com.aircall.app.Fragment.UpcomingSchedualeDetailFragment}
     *
     * @param message
     * @param NId
     * @param CommonId
     * @param NType    NTYPE_SCHEDULE - 1
     * @param n
     */
    public void sendUpcomingScheduleNotification(String message, String NId, String CommonId, String NType, int n) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Send data to NotificationView Class
        intent.putExtra("NId", NId);
        intent.putExtra("CommonId", CommonId);
        intent.putExtra("NType", NType);
        intent.putExtra("dash", "upcoming_schedule_detail");

        sendNotificationWithActionIntent(message, intent, n);
    }

    /**
     * NTYPE_FRIENDLY_REMINDER - 2
     * Redirect to @{@link DashboardActivity} -> @{@link com.aircall.app.Fragment.NotificationListFragment}
     *
     * @param message
     * @param n
     */
    public void simpleNotification(String message, int n) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("dash", "NotificationListing");

        sendNotificationWithActionIntent(message, intent, n);
    }

    /**
     * NTYPE_NO_SHOW - 3
     * Redirect to @{@link NoShowActivity}
     *
     * @param message
     * @param NId
     * @param CommonId
     * @param n
     */
    public void sendNoShowNotification(String message, String NId, String CommonId, int n) {
        Intent intent = new Intent(this, NoShowActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Send data to NotificationView Class
        intent.putExtra("NId", NId);
        intent.putExtra("CommonId", CommonId);

        sendNotificationWithActionIntent(message, intent, n);
    }

    /**
     * NTYPE_PART_PURCHASE - 4
     * Redirect to @{@link UserProfileActivity} -> @{@link com.aircall.app.Fragment.BillingHistoryDetailsFragment}
     *
     * @param message
     * @param NId
     * @param CommonId
     * @param n
     */
    public void sendPartPurchaseNotification(String message, String NId, String CommonId, int n) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Send data to NotificationView Class
        intent.putExtra("NId", NId);
        intent.putExtra("CommonId", CommonId);
        intent.putExtra("IsPaymentFail", false);

        sendNotificationWithActionIntent(message, intent, n);
    }

    /**
     * NTYPE_RATE_SERVICE - 5
     * Redirect to @{@link DashboardActivity} -> @{@link com.aircall.app.Fragment.PastServiceDetailFragment}
     *
     * @param message
     * @param NId
     * @param CommonId
     * @param n
     */
    public void sendRateServiceNotification(String message, String NId, String CommonId, int n) {
        Intent intent = new Intent(this, DashboardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        // Send data to NotificationView Class
        intent.putExtra("NId", NId);
        intent.putExtra("CommonId", CommonId);
        intent.putExtra("dash", "PastServiceDetailFragment");

        sendNotificationWithActionIntent(message, intent, n);
    }

    /**
     * NTYPE_CARD_EXPIRE - 6
     * Redirect to @{@link AddNewCardActivity}
     *
     * @param message
     * @param NId
     * @param CommonId
     * @param n
     */
    public void sendCardExpireNotification(String message, String NId, String CommonId, int n) {
        Intent intent = new Intent(this, AddNewCardActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("NId", NId);
        intent.putExtra("CommonId", CommonId);

        sendNotificationWithActionIntent(message, intent, n);
    }

    /**
     * NTYPE_RENEW_UNIT - 8
     * Redirect to @{@link AddUnitActivity} -> @{@link com.aircall.app.Fragment.SummaryFragment}
     *
     * @param message
     * @param NId
     * @param CommonId
     * @param n
     * @param NTYPE_RENEW_UNIT
     */
    public void sendRenewUnitNotification(String message, String NId, String CommonId, int n, String NTYPE_RENEW_UNIT) {
        Intent intent = new Intent(this, AddUnitActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("NId", NId);
        intent.putExtra("CommonId", CommonId);
        intent.putExtra("addUnit", "summary");
        intent.putExtra("NType", NTYPE_RENEW_UNIT);

        sendNotificationWithActionIntent(message, intent, n);
    }

    /**
     * NTYPE_PAYMENT_FAILED - 11
     * Redirect to @{@link AddUnitActivity} -> @{@link com.aircall.app.Fragment.SummaryFragment}
     *
     * @param message
     * @param NId
     * @param CommonId
     * @param n
     * @param NTYPE_PAYMENT_FAILED
     */
    public void sendPaymentFailedNotification(String message, String NId, String CommonId, int n, String NTYPE_PAYMENT_FAILED) {
        Intent intent = new Intent(this, AddUnitActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("NId", NId);
        intent.putExtra("CommonId", CommonId);
        intent.putExtra("addUnit", "summary");
        intent.putExtra("NType", NTYPE_PAYMENT_FAILED);

        sendNotificationWithActionIntent(message, intent, n);
    }

    /**
     * NTYPE_INVOICE_PAYMENT_FAIL - 17
     * Redirect to @{@link UserProfileActivity} -> @{@link com.aircall.app.Fragment.BillingHistoryDetailsFragment}
     *
     * @param message
     * @param NId
     * @param CommonId
     * @param n
     */
    public void sendRecurringPaymentFailNotification(String message, String NId, String CommonId, int n) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("NId", NId);
        intent.putExtra("CommonId", CommonId);
        intent.putExtra("IsPaymentFail", true);
        sendNotificationWithActionIntent(message, intent, n);
    }

    public void sendNotificationWithActionIntent(String message, Intent intent, int n) {
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationBuilder.setColor(getResources().getColor(R.color.header, null));
            } else {
                notificationBuilder.setColor(getResources().getColor(R.color.header));
            }
        }

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(n, notificationBuilder.build());
    }

    public void sendSimpleNotification(String message, int n) {

        /*PendingIntent pIntent = PendingIntent.getBroadcast(this.getApplicationContext(), 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);*/

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle(getResources().getString(R.string.app_name))
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message));

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationBuilder.setColor(getResources().getColor(R.color.header, null));
            } else {
                notificationBuilder.setColor(getResources().getColor(R.color.header));
            }
        }

        NotificationManager notificationmanager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationmanager.notify(n, notificationBuilder.build());
    }



    /**
     * Client logout with clear sharedpreferences.
     */
    public void Clientlogout() {

        NotificationManager nMgr = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        nMgr.cancelAll();

        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        sharedpreferences.edit().remove("Id").commit();
        sharedpreferences.edit().remove("Token").commit();
        sharedpreferences.edit().remove("ProfileImage").commit();
        startActivity(intent);

        WebserviceApi webserviceApi = ServiceGenerator.createService(WebserviceApi.class);
        webserviceApi.Clientlogout(sharedpreferences.getString("Id", ""), new Callback<RattingResponce>() {

            @Override
            public void success(RattingResponce rattingResponce, Response response) {
            }

            @Override
            public void failure(RetrofitError error) {
            }
        });
    }

    public BigDecimal roundupTwoDecimalPoint(String value) {
        BigDecimal a = new BigDecimal(value);
        a = a.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        return a;
    }
}