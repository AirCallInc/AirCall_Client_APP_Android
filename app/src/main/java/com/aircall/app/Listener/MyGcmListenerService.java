package com.aircall.app.Listener;

import android.os.Bundle;
import android.util.Log;

import com.aircall.app.Common.GlobalClass;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.Random;

/**
 * Created by Jd on 11/07/16.
 */
public class MyGcmListenerService extends GcmListenerService {


    int n;
    GlobalClass globalClass;

    @Override
    public void onMessageReceived(String from, Bundle data) {
        globalClass = (GlobalClass) getApplicationContext();
        String message = data.getString("message");
        String NType = data.getString("NType");
        String NId = data.getString("NId");
        String CommonId = data.getString("CommonId");
        Log.e("notification", "Data: " + data.toString());
        Log.e("notification", "Message: " + message);
        Log.e("notification", "NType: " + NType);
        Log.e("notification", "NId: " + NId);
        Log.e("notification", "CommonId: " + CommonId);

        Random gen = new Random();
        n = 100000;
        n = gen.nextInt(n);

        if (NType != null) {
            if (NType.equalsIgnoreCase(globalClass.NTYPE_SCHEDULE)) {
                globalClass.sendUpcomingScheduleNotification(message,
                        NId, CommonId, NType, n);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_FRIENDLY_REMINDER)
                    || NType.equalsIgnoreCase(globalClass.NTYPE_SUBSCRIPTION_INVOICE_PAYMENT_DUE_REMINDER)
                    || NType.equalsIgnoreCase(globalClass.NTYPE_PAST_DUE_REMINDER)) {
                /*globalClass.testIntent = new Intent(this,TestService.class);
                startService(globalClass.testIntent);*/
                globalClass.simpleNotification(message, n);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_NO_SHOW) || NType.equalsIgnoreCase(globalClass.NTYPE_LATE_CANCELLED)) { //Added Late Cancelled
                globalClass.sendNoShowNotification(message,
                        NId, CommonId, n);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_PART_PURCHASE)) {
                globalClass.sendPartPurchaseNotification(message,
                        NId, CommonId, n);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_RATE_SERVICE)) {
                globalClass.sendRateServiceNotification(message,
                        NId, CommonId, n);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_CARD_EXPIRE)) {
                globalClass.sendCardExpireNotification(message,
                        NId, CommonId, n);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_RENEW_UNIT)) {
                globalClass.sendRenewUnitNotification(message,
                        NId, CommonId, n, globalClass.NTYPE_RENEW_UNIT);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_RESCHEDULE)) {
                globalClass.sendUpcomingScheduleNotification(message,
                        NId, CommonId, NType, n);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_PAYMENT_FAILED)) {
                globalClass.sendPaymentFailedNotification(message,
                        NId, CommonId, n, globalClass.NTYPE_PAYMENT_FAILED);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_SERVICE_REMINDER)) {
                globalClass.sendUpcomingScheduleNotification(message,
                        NId, CommonId, NType, n);
            } else if (NType.equalsIgnoreCase(globalClass.NTYPE_INVOICE_PAYMENT_FAIL)) {
                globalClass.sendRecurringPaymentFailNotification(message,
                        NId, CommonId, n);
            } else {
                globalClass.simpleNotification(message, n);
            }
        } else {
            globalClass.simpleNotification(message, n);
        }
    }

    private void setIntentForListing() {

    }


}