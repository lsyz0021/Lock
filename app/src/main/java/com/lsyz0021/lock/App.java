package com.lsyz0021.lock;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by trila on 15/11/23.
 */
public class App extends Application {
    static String LOG_TAG = "HFAX";
    private static App singletonApplication;
    private static SharedPreferences userPreferences;
    private static Context context;

    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        App.context = context;
        singletonApplication = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        singletonApplication = this;
    }


    public static Context getContext() {
        return context;
    }

    public static App getApplication() {
        return singletonApplication;
    }

    public static SharedPreferences getUserPreferences() {
        return userPreferences;
    }

}
