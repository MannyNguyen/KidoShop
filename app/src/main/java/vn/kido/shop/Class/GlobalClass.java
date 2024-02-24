package vn.kido.shop.Class;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.FragmentActivity;


public class GlobalClass extends MultiDexApplication {
    private static Context mContext;
    private static FragmentActivity activity;


    public static FragmentActivity getActivity() {
        return activity;
    }

    public static void setActivity(FragmentActivity activity) {
        GlobalClass.activity = activity;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
        //Realm.init(getApplicationContext());
        mContext = getApplicationContext();
//        FirebaseApp.initializeApp(this);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
//        AppEventsLogger logger = AppEventsLogger.newLogger(this);
//        logger.logEvent(AppEventsConstants.EVENT_NAME_ACTIVATED_APP);
    }

    public static Context getContext() {
        return mContext;
    }

}