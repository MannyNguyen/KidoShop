package vn.kido.shop.Helper;

import android.provider.Settings;

import vn.kido.shop.Class.GlobalClass;

public class AccountHelper {
    public static boolean isLogin() {
        String id = Settings.Secure.getString(GlobalClass.getContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        if (StorageHelper.get(StorageHelper.DEVICE_ID).equals(id)) {
            return true;
        }
        return false;
    }
}
