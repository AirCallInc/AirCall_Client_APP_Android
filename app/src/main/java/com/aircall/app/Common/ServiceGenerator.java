package com.aircall.app.Common;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.TimeUnit;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

public class ServiceGenerator {

    //public static final String BASE_URL = "https://system.aircallservices.com/api/v1"; // Client server
    public static final String BASE_URL = "https://beta.aircallservices.com/api/v1"; // Client server


//    public static final String BASE_URL = "http://166.62.36.157/api/v1"; // Client server

    public static RestAdapter.Builder builder = new RestAdapter.Builder()
            .setEndpoint(BASE_URL);

    /*public static RestAdapter.Builder builderLocal = new RestAdapter.Builder()
            .setEndpoint(LOCAL_BASE_URL)
            .setClient(new OkClient(new OkHttpClient()));*/

    public static <S> S createService(Class<S> serviceClass) {
        return createService(serviceClass, null);
    }

    public static boolean isApkDebugable(Context context) {
        try {
            ApplicationInfo info= context.getApplicationInfo();
            return (info.flags&ApplicationInfo.FLAG_DEBUGGABLE)!=0;
        } catch (Exception e) {

        }
        return false;
    }


    public static <S> S createService(Class<S> serviceClass, final String auth) {

        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(1, TimeUnit.MINUTES);
        okHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);
        builder.setClient(new OkClient(okHttpClient));

        if (auth != null) {
            builder.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", "bearer " + auth);
                }
            });

        }

        RestAdapter adapter = builder.build();
        return adapter.create(serviceClass);
    }

    /*public static <S> S createLocalService(Class<S> serviceClass, final String auth) {


        final OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(1, TimeUnit.MINUTES);
        okHttpClient.setConnectTimeout(1, TimeUnit.MINUTES);

        if (auth != null) {
            builderLocal.setRequestInterceptor(new RequestInterceptor() {
                @Override
                public void intercept(RequestFacade request) {
                    request.addHeader("Authorization", "bearer "+auth);
                }
            });
        }

        RestAdapter adapter = builderLocal.build();
        return adapter.create(serviceClass);
    }*/
}