package vn.kido.shop.Helper;

import android.content.Context;
import android.content.SharedPreferences;

import vn.kido.shop.Class.GlobalClass;

import static android.content.Context.MODE_PRIVATE;

public class StorageHelper {

    public static final String TOKEN = "TOKEN";
    public static final String USERNAME = "USERNAME";
    public static final String SWITCH = "SWITCH";
    public static final String LANGUAGE = "LANGUAGE";
    public static final String HOME_SCREEN = "HOME_SCREEN";
    public static final String POPUP_LOCATION = "POPUP_LOCATION";
    public static final String SERVER = "SERVER";
    public static final String DEVICE_ID = "DEVICE_ID";

    private static SharedPreferences preferences;

    public static SharedPreferences getPreferences() {
        if (preferences == null) {
            init(GlobalClass.getContext());
        }
        return preferences;
    }

    public static void init(Context context) {
        String PREF_FILE_NAME = "KIDO_NIP_STORAGE";
        preferences = context.getSharedPreferences(PREF_FILE_NAME, MODE_PRIVATE);
    }

//    public static void saveLanguage(String countryCode) {
//        try {
//            SharedPreferences.Editor editor = getPreferences().edit();
//            editor.putString("language", countryCode);
//            editor.commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static String getLanguage() {
//        return getPreferences().getString("language", "vi");
//    }

    public static void set(String key, Boolean value) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putBoolean(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean getBool(String key) {
        return getPreferences().getBoolean(key, false);
    }

    public static void set(String key, String value) {
        try {
            SharedPreferences.Editor editor = getPreferences().edit();
            editor.putString(key, value);
            editor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return getPreferences().getString(key, "");
    }
}
