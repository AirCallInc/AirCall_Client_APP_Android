package com.aircall.app.Services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.R;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * Created by Jd on 11/07/16.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    GlobalClass globleClass;

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            globleClass = (GlobalClass) getApplicationContext();
            sharedpreferences = getApplicationContext().getSharedPreferences(globleClass.PreferenceName, Context.MODE_PRIVATE);
            editor = sharedpreferences.edit();
            InstanceID instanceID = InstanceID.getInstance(this);
            globleClass.token = instanceID.getToken(getString(R.string.clientId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            editor.putString("GcmToken",globleClass.token);
            editor.apply();
            Log.e("inmtentservice", "GCM Registration Token: " + globleClass.token);
        } catch (Exception e) {

        }
    }
}