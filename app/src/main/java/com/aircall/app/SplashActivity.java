package com.aircall.app;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.aircall.app.Common.GlobalClass;
import com.aircall.app.Services.RegistrationIntentService;
import com.google.firebase.analytics.FirebaseAnalytics;

public class SplashActivity extends Activity {


    private SharedPreferences sharedpreferences;
    private GlobalClass globalClass;
    private FirebaseAnalytics mFirebaseAnalytics;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, "ID");
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Splash");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        //Sets whether analytics collection is enabled for this app on this device.
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
        //Sets the minimum engagement time required before starting a session. The default value is 10000 (10 seconds). Let's make it 20 seconds just for the fun
        mFirebaseAnalytics.setMinimumSessionDuration(20000);
        //Sets the duration of inactivity that terminates the current session. The default value is 1800000 (30 minutes).
        mFirebaseAnalytics.setSessionTimeoutDuration(500);
        /**
         * Get GCM Token and save in shared sharedpreferences.
         */
        globalClass = (GlobalClass) getApplicationContext();
        sharedpreferences = getSharedPreferences(globalClass.PreferenceName, Context.MODE_PRIVATE);
        if (sharedpreferences.getString("GcmToken", "").equalsIgnoreCase("")) {
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        Thread splash_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    /**
                     * If already login (Get Client id from sharedpreferences): Go to Dashboard screen
                     *
                     * If not login (Can't Get Client id from sharedpreferences): Go to Login screen
                     */
                    if (!sharedpreferences.getString("Id", "").equalsIgnoreCase("")) {
                        startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        finish();
                    }
                }
            }
        });
        splash_thread.start();
    }
}
